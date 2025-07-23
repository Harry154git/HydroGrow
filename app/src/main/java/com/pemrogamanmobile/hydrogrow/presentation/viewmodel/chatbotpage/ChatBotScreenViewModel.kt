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

    // State baru untuk menyimpan riwayat percakapan
    private val _chatHistory = mutableStateOf<List<ChatBot>>(emptyList())
    val chatHistory: State<List<ChatBot>> = _chatHistory

    // State baru untuk loading riwayat
    private val _historyLoading = mutableStateOf(false)
    val historyLoading: State<Boolean> = _historyLoading

    init {
        // Langsung muat riwayat saat ViewModel dibuat
        loadChatHistory()
    }

    /**
     * [BARU] Memuat seluruh riwayat percakapan untuk ditampilkan di drawer.
     */
    private fun loadChatHistory() {
        chatBotUseCase.getChatHistory().onEach { resource ->
            when (resource) {
                is Resource.Loading -> _historyLoading.value = true
                is Resource.Success -> {
                    _historyLoading.value = false
                    // Urutkan dari yang terbaru
                    _chatHistory.value = resource.data?.sortedByDescending { it.updatedAt } ?: emptyList()
                }
                is Resource.Error -> {
                    _historyLoading.value = false
                    _error.value = resource.message ?: "Gagal memuat riwayat."
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * [BARU] Fungsi untuk memulai percakapan baru.
     * Akan mereset state percakapan saat ini di UI.
     */
    fun startNewChat() {
        _chatId.value = null
        _messages.value = emptyList()
        _isTyping.value = false
        _error.value = null
    }

    fun loadChat(chatbotId: String) {
        if (chatbotId == _chatId.value && _messages.value.isNotEmpty()) return

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
                        // Setelah sukses, refresh history dan conversation
                        loadChatHistory() // Refresh daftar history
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
            loadChat(currentChatId)
        } else {
            // Jika percakapan baru, ID baru dibuat di backend.
            // Ambil ID dari chat paling baru di history yang sudah di-refresh.
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