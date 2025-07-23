package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.chatbotpage

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemrogamanmobile.hydrogrow.domain.model.ChatBot
import com.pemrogamanmobile.hydrogrow.domain.model.ChatMessage
import com.pemrogamanmobile.hydrogrow.domain.model.Garden
import com.pemrogamanmobile.hydrogrow.domain.model.Plant
import com.pemrogamanmobile.hydrogrow.domain.usecase.ai.chatbot.ChatBotUseCase
import com.pemrogamanmobile.hydrogrow.domain.usecase.garden.GardenUseCase
import com.pemrogamanmobile.hydrogrow.domain.usecase.plant.PlantUseCase
import com.pemrogamanmobile.hydrogrow.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ChatBotScreenViewModel @Inject constructor(
    private val chatBotUseCase: ChatBotUseCase,
    private val gardenUseCase: GardenUseCase, // BARU
    private val plantUseCase: PlantUseCase    // BARU
) : ViewModel() {

    private val _chatId = mutableStateOf<String?>(null)
    val chatId: State<String?> = _chatId

    private val _messages = mutableStateOf<List<ChatMessage>>(emptyList())
    val messages: State<List<ChatMessage>> = _messages

    private val _isTyping = mutableStateOf(false)
    val isTyping: State<Boolean> = _isTyping

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _chatHistory = mutableStateOf<List<ChatBot>>(emptyList())
    val chatHistory: State<List<ChatBot>> = _chatHistory

    private val _historyLoading = mutableStateOf(false)
    val historyLoading: State<Boolean> = _historyLoading

    private val _gardens = mutableStateOf<List<Garden>>(emptyList())
    val gardens: State<List<Garden>> = _gardens

    private val _plants = mutableStateOf<List<Plant>>(emptyList())
    val plants: State<List<Plant>> = _plants

    // Pair<ID, Nama, Tipe("garden"/"plant")>
    private val _selectedContext = mutableStateOf<Triple<String, String, String>?>(null)
    val selectedContext: State<Triple<String, String, String>?> = _selectedContext


    init {
        // ✅ FIX: Memisahkan proses observe dan refresh data
        observeChatHistory() // 1. Mulai amati data dari database lokal
        refreshChatHistory() // 2. Minta data baru dari jaringan
        // BARU: Muat data untuk konteks saat ViewModel dibuat
        loadGardens()
        loadPlants()
    }

    // BARU: Fungsi untuk memuat daftar kebun dan tanaman
    private fun loadGardens() {
        gardenUseCase.getAllGardens().onEach {
            _gardens.value = it
        }.launchIn(viewModelScope)
    }

    private fun loadPlants() {
        viewModelScope.launch {
            _plants.value = plantUseCase.getAllPlants()
        }
    }

    // BARU: Fungsi untuk mengelola state konteks
    fun selectContext(id: String, name: String, type: String) {
        _selectedContext.value = Triple(id, name, type)
    }

    fun clearContext() {
        _selectedContext.value = null
    }

    // BARU: Fungsi untuk menambahkan pesan gambar ke UI secara instan
    fun addVisualMessage(uri: Uri) {
        val imageMessage = ChatMessage(role = ChatMessage.ROLE_IMAGE, content = uri.toString())
        _messages.value += imageMessage
    }

    // BARU: Fungsi untuk mengirim pesan dengan gambar
    fun sendMessageWithImage(userMessage: String, imageFile: File) {
        if (userMessage.isBlank()) return

        // Hapus pesan visual (hanya URI) dan ganti dengan pesan teks asli
        _messages.value = _messages.value.dropLast(1)
        val newUserMessage = ChatMessage(role = ChatMessage.ROLE_USER, content = userMessage)
        _messages.value += newUserMessage

        _isTyping.value = true
        _error.value = null

        viewModelScope.launch {
            chatBotUseCase.startNewConversationWithImageDetection(userMessage, imageFile).onEach { resource ->
                when (resource) {
                    is Resource.Loading -> { /* isTyping sudah true */ }
                    is Resource.Success -> {
                        refreshChatHistory()
                        refreshConversation()
                    }
                    is Resource.Error -> {
                        _isTyping.value = false
                        _error.value = resource.message ?: "Terjadi kesalahan pada deteksi gambar."
                        _messages.value = _messages.value.dropLast(1)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    /**
     * ✅ FIX: Fungsi ini sekarang HANYA mengamati Flow dari database lokal.
     * UI akan otomatis update setiap kali ada data baru di database.
     */
    private fun observeChatHistory() {
        chatBotUseCase.getChatHistory().onEach { history ->
            _chatHistory.value = history // Tidak ada lagi 'Resource' wrapper
        }.launchIn(viewModelScope)
    }

    /**
     * ✅ BARU: Fungsi untuk memicu sinkronisasi data dari jaringan ke database.
     */
    private fun refreshChatHistory() {
        viewModelScope.launch {
            _historyLoading.value = true
            val result = chatBotUseCase.refreshChatHistory()
            if (result is Resource.Error) {
                _error.value = result.message ?: "Gagal memuat riwayat."
            }
            _historyLoading.value = false
        }
    }

    fun startNewChat() {
        _chatId.value = null
        _messages.value = emptyList()
        _isTyping.value = false
        _error.value = null
    }

    fun loadChat(chatbotId: String) {
        // Tidak perlu cek, karena getChatById sekarang real-time,
        // kita bisa berlangganan ulang untuk memastikan data terbaru.
        chatBotUseCase.getChatBotById(chatbotId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _isTyping.value = true
                is Resource.Success -> {
                    _isTyping.value = false
                    resource.data?.let { chatBot ->
                        _chatId.value = chatBot.id
                        _messages.value = chatBot.conversation
                    }
                }
                is Resource.Error -> {
                    _isTyping.value = false
                    _error.value = resource.message ?: "Gagal memuat percakapan."
                }
            }
        }.launchIn(viewModelScope)
    }

    // MODIFIKASI: sendMessage menjadi context-aware
    fun sendMessage(userMessage: String) {
        if (userMessage.isBlank()) return

        val newUserMessage = ChatMessage(role = ChatMessage.ROLE_USER, content = userMessage)
        _messages.value += newUserMessage
        _isTyping.value = true
        _error.value = null

        viewModelScope.launch {
            val context = _selectedContext.value
            val currentChatId = _chatId.value

            val useCaseFlow = when {
                // Kasus 1: Ada konteks terpilih (Garden/Plant)
                context != null -> {
                    val (id, _, type) = context
                    chatBotUseCase.startNewConversationWithChosenData(userMessage, id, type)
                }
                // Kasus 2: Melanjutkan percakapan yang ada
                currentChatId != null -> {
                    chatBotUseCase.continueConversation(currentChatId, userMessage)
                }
                // Kasus 3: Memulai percakapan baru tanpa konteks
                else -> {
                    chatBotUseCase.startNewConversation(userMessage)
                }
            }

            // Hapus konteks setelah digunakan untuk satu pertanyaan
            clearContext()

            useCaseFlow.onEach { resource ->
                when (resource) {
                    is Resource.Loading -> { /* isTyping sudah true */ }
                    is Resource.Success -> {
                        refreshChatHistory()
                        refreshConversation()
                    }
                    is Resource.Error -> {
                        _isTyping.value = false
                        _error.value = resource.message ?: "Terjadi kesalahan."
                        _messages.value = _messages.value.dropLast(1)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun refreshConversation() {
        val currentChatId = _chatId.value
        if (currentChatId != null) {
            // Jika chat sudah ada, cukup panggil loadChat lagi.
            // Karena getChatById real-time, ia akan dapat data terbaru.
            loadChat(currentChatId)
        } else {
            // Jika ini percakapan baru, ambil ID dari history yang sudah di-refresh.
            // _chatHistory sudah dijamin terbaru karena refreshChatHistory() dipanggil sebelumnya.
            val latestChat = _chatHistory.value.firstOrNull()
            if (latestChat != null) {
                loadChat(latestChat.id)
            } else {
                _isTyping.value = false
                _error.value = "Gagal menemukan percakapan baru."
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}