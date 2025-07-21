package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.chatbotpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemrogamanmobile.hydrogrow.domain.usecase.ai.ChatBotUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.delay

@HiltViewModel
class ChatBotScreenViewModel @Inject constructor(
    private val chatBotUseCase: ChatBotUseCase
) : ViewModel() {

    private val _messages = mutableStateListOf<Message>()
    val messages: List<Message> = _messages

    private val _isTyping = mutableStateOf(false)

    fun sendMessage(userMessage: String) {
        _messages.add(Message(userMessage, isUser = true))
        _isTyping.value = true

        viewModelScope.launch {
            val response = chatBotUseCase.execute(listOf(userMessage))

            val aiMessage = Message("", isUser = false)
            _messages.add(aiMessage)

            for (i in 1..response.length) {
                val partial = response.substring(0, i)
                _messages[_messages.lastIndex] = aiMessage.copy(text = partial)
                delay(30)
            }

            _isTyping.value = false
        }
    }
}

data class Message(
    val text: String,
    val isUser: Boolean
)

