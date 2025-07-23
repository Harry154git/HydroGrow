package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.chatbotpage

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemrogamanmobile.hydrogrow.domain.model.ChatBot
import com.pemrogamanmobile.hydrogrow.domain.model.ChatMessage
import com.pemrogamanmobile.hydrogrow.domain.usecase.ai.chatbot.ChatBotUseCase
import com.pemrogamanmobile.hydrogrow.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatBotScreenViewModel @Inject constructor(
    private val chatBotUseCase: ChatBotUseCase
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

    init {
        // ✅ FIX: Memisahkan proses observe dan refresh data
        observeChatHistory() // 1. Mulai amati data dari database lokal
        refreshChatHistory() // 2. Minta data baru dari jaringan
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

    fun sendMessage(userMessage: String) {
        if (userMessage.isBlank()) return

        val newUserMessage = ChatMessage(role = ChatMessage.ROLE_USER, content = userMessage)
        _messages.value += newUserMessage
        _isTyping.value = true
        _error.value = null

        viewModelScope.launch {
            val currentChatId = _chatId.value
            val useCaseFlow = if (currentChatId == null) {
                chatBotUseCase.startNewConversation(userMessage)
            } else {
                chatBotUseCase.continueConversation(currentChatId, userMessage)
            }

            useCaseFlow.onEach { resource ->
                when (resource) {
                    is Resource.Loading -> { /* isTyping sudah true */ }
                    is Resource.Success -> {
                        // ✅ FIX: Panggil fungsi refresh yang benar
                        refreshChatHistory()    // Refresh daftar history di drawer
                        refreshConversation()   // Refresh pesan di chat yang aktif
                    }
                    is Resource.Error -> {
                        _isTyping.value = false
                        _error.value = resource.message ?: "Terjadi kesalahan."
                        // Hapus pesan user yang gagal dikirim dari UI
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