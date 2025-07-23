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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ChatBotScreenViewModel @Inject constructor(
    private val chatBotUseCase: ChatBotUseCase,
    private val gardenUseCase: GardenUseCase,
    private val plantUseCase: PlantUseCase
) : ViewModel() {

    // MARK: - UI States
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

    private val _selectedContext = mutableStateOf<Triple<String, String, String>?>(null)
    val selectedContext: State<Triple<String, String, String>?> = _selectedContext

    init {
        observeChatHistory()
        refreshChatHistory()
        loadGardens()
        loadPlants()
    }

    // MARK: - Core Logic
    /**
     * ✅ FUNGSI UTAMA: Mengirim pesan, baik teks, gambar, atau keduanya.
     * Fungsi ini menangani semua skenario: memulai percakapan baru atau melanjutkannya.
     */
    fun sendMessage(userMessage: String, imageFile: File?) {
        if (userMessage.isBlank() && imageFile == null) return

        val newUserMessage = ChatMessage(role = ChatMessage.ROLE_USER, content = userMessage)
        val optimisticMessage = newUserMessage.copy(imageUrl = imageFile?.toUri()?.toString())

        _messages.value += optimisticMessage
        _isTyping.value = true
        _error.value = null

        viewModelScope.launch {
            val context = _selectedContext.value
            val currentChatId = _chatId.value

            // ✅ FIX: Urutan prioritas diubah. Konteks sekarang dicek PERTAMA.
            val useCaseFlow: Flow<Resource<out Any>> = when {
                // PRIORITAS 1: Jika pengguna memilih konteks, selalu mulai percakapan baru dengan konteks itu.
                context != null -> {
                    chatBotUseCase.startNewConversationWithChosenData(userMessage, context.first, context.third)
                }
                // PRIORITAS 2: Jika tidak ada konteks, baru cek apakah ini percakapan lanjutan.
                currentChatId != null -> {
                    chatBotUseCase.continueConversation(currentChatId, newUserMessage, imageFile)
                }
                // PRIORITAS 3: Memulai percakapan baru dengan gambar.
                imageFile != null -> {
                    chatBotUseCase.startNewConversationWithImageDetection(userMessage, imageFile)
                }
                // PRIORITAS 4: Memulai percakapan baru hanya dengan teks.
                else -> {
                    chatBotUseCase.startNewConversation(userMessage)
                }
            }

            clearContext()

            useCaseFlow.onEach { resource ->
                when (resource) {
                    is Resource.Loading -> { /* isTyping sudah true */ }
                    is Resource.Success -> {
                        if (resource.data is String?) {
                            val newChatId = resource.data as? String
                            if (newChatId != null) {
                                refreshChatHistory()
                                loadChat(newChatId)
                            } else {
                                _isTyping.value = false
                                _error.value = "Gagal mendapatkan ID percakapan baru."
                            }
                        } else {
                            refreshChatHistory()
                            refreshConversation()
                        }
                    }
                    is Resource.Error -> {
                        _isTyping.value = false
                        _error.value = resource.message ?: "Terjadi kesalahan."
                        _messages.value = _messages.value.filterNot { it.timestamp == optimisticMessage.timestamp }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    // MARK: - State Management Functions
    fun loadChat(chatbotId: String) {
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

    fun startNewChat() {
        _chatId.value = null
        _messages.value = emptyList()
        _isTyping.value = false
        _error.value = null
        _selectedContext.value = null
    }

    private fun observeChatHistory() {
        chatBotUseCase.getChatHistory().onEach { history ->
            _chatHistory.value = history
        }.launchIn(viewModelScope)
    }

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

    private fun refreshConversation() {
        val currentChatId = _chatId.value
        if (currentChatId != null) {
            // Jika chat sudah ada, muat ulang untuk mendapatkan data terbaru dari Flow
            loadChat(currentChatId)
        } else {
            // Jika ini percakapan baru, ID baru akan didapatkan dari riwayat yang telah di-refresh.
            // Ambil ID dari chat terbaru di history, lalu muat percakapan tersebut.
            val latestChatId = _chatHistory.value.maxByOrNull { it.createdAt }?.id
            if (latestChatId != null) {
                loadChat(latestChatId)
            } else {
                _isTyping.value = false
                _error.value = "Gagal menemukan percakapan yang baru dibuat."
            }
        }
    }

    // MARK: - Context and Helper Functions
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

    fun selectContext(id: String, name: String, type: String) {
        _selectedContext.value = Triple(id, name, type)
    }

    fun clearContext() {
        _selectedContext.value = null
    }

    fun clearError() {
        _error.value = null
    }
}

// Helper extension function untuk mengubah File menjadi Uri.
fun File.toUri(): Uri = Uri.fromFile(this)