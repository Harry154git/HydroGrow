package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.chatbotpage

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    // State untuk menyimpan ID percakapan saat ini. Null jika percakapan baru.
    private val _chatId = mutableStateOf<String?>(null)

    // State untuk daftar pesan dalam percakapan. Menggunakan ChatMessage.
    private val _messages = mutableStateOf<List<ChatMessage>>(emptyList())
    val messages: State<List<ChatMessage>> = _messages

    // State untuk menunjukkan apakah AI sedang "mengetik" atau memproses.
    private val _isTyping = mutableStateOf(false)
    val isTyping: State<Boolean> = _isTyping

    // State untuk menampilkan pesan error jika terjadi.
    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    /**
     * Fungsi untuk menginisialisasi ViewModel dengan percakapan yang sudah ada.
     * Panggil fungsi ini dari UI jika pengguna membuka riwayat chat.
     * @param chatbotId ID dari percakapan yang akan dimuat.
     */
    fun loadChat(chatbotId: String) {
        // Jika ID sama dengan yang sudah dimuat, tidak perlu load ulang
        if (chatbotId == _chatId.value) return

        chatBotUseCase.getChatBotById(chatbotId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _isTyping.value = true
                }
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

    /**
     * Fungsi utama untuk mengirim pesan.
     * Fungsi ini akan otomatis menentukan apakah harus memulai percakapan baru
     * atau melanjutkan yang sudah ada berdasarkan _chatId.
     * @param userMessage Teks pesan dari pengguna.
     */
    fun sendMessage(userMessage: String) {
        if (userMessage.isBlank()) return

        // 1. Tampilkan pesan pengguna langsung di UI untuk responsivitas.
        val newUserMessage = ChatMessage(role = ChatMessage.ROLE_USER, content = userMessage)
        _messages.value += newUserMessage
        _isTyping.value = true
        _error.value = null // Hapus error sebelumnya

        viewModelScope.launch {
            // Tentukan use case yang akan dipanggil
            val currentChatId = _chatId.value
            val useCaseFlow = if (currentChatId == null) {
                // Mulai percakapan baru jika ID null
                chatBotUseCase.startNewConversation(userMessage)
            } else {
                // Lanjutkan percakapan jika ID sudah ada
                chatBotUseCase.continueConversation(currentChatId, userMessage)
            }

            // 2. Panggil use case dan tangani hasilnya
            useCaseFlow.onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // isTyping sudah true
                    }
                    is Resource.Success -> {
                        // Jika berhasil, muat ulang data chat untuk mendapatkan balasan dari AI
                        refreshConversation()
                    }
                    is Resource.Error -> {
                        _isTyping.value = false
                        _error.value = resource.message ?: "Terjadi kesalahan."
                        // Hapus pesan pengguna yang gagal dikirim agar bisa dicoba lagi
                        _messages.value = _messages.value.dropLast(1)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    /**
     * Memuat ulang data percakapan.
     * Dipanggil setelah pesan berhasil dikirim untuk mendapatkan balasan AI.
     */
    private fun refreshConversation() {
        val currentChatId = _chatId.value
        if (currentChatId != null) {
            // Jika ID sudah ada, cukup muat ulang chat tersebut
            loadChat(currentChatId)
        } else {
            // Jika ini adalah percakapan baru, kita perlu mencari ID chat yang baru dibuat.
            // Cara terbaik adalah mengambil chat terakhir dari riwayat.
            chatBotUseCase.getChatHistory().onEach { resource ->
                if (resource is Resource.Success) {
                    // Cari chat yang paling baru diperbarui (updatedAt)
                    val latestChat = resource.data?.maxByOrNull { it.updatedAt }
                    latestChat?.let {
                        // Muat chat yang baru ditemukan
                        loadChat(it.id)
                    }
                } else if (resource is Resource.Error){
                    _error.value = resource.message ?: "Gagal menyegarkan percakapan."
                }
                // Hentikan 'typing' setelah selesai refresh
                _isTyping.value = false
            }.launchIn(viewModelScope)
        }
    }

    /**
     * Fungsi untuk membersihkan pesan error setelah ditampilkan di UI (misal: di Snackbar).
     */
    fun clearError() {
        _error.value = null
    }
}