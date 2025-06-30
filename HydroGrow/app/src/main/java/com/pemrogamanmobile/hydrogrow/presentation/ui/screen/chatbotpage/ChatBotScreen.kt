package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.chatbotpage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.chatbotpage.ChatBotScreenViewModel
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.chatbotpage.Message

@Composable
fun ChatBotScreen(navController: NavController, viewModel: ChatBotScreenViewModel = hiltViewModel()) {
    var userInput by rememberSaveable { mutableStateOf("") }
    val messages = viewModel.messages

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Chatbot", style = MaterialTheme.typography.headlineMedium)
            Button(onClick = { navController.popBackStack() }) {
                Text("Kembali")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                ChatBubble(message)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            TextField(
                value = userInput,
                onValueChange = { userInput = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Tanya sesuatu...") }
            )
            IconButton(onClick = {
                if (userInput.isNotBlank()) {
                    viewModel.sendMessage(userInput)
                    userInput = ""
                }
            }) {
                Icon(Icons.Default.Send, contentDescription = "Kirim")
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    val bubbleColor = if (message.isUser) Color(0xFFDCF8C6) else Color(0xFFFFFFFF)
    val alignment = if (message.isUser) Alignment.End else Alignment.Start

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(alignment)
    ) {
        Box(
            modifier = Modifier
                .background(bubbleColor, shape = RoundedCornerShape(12.dp))
                .padding(12.dp)
                .widthIn(max = 250.dp)
        ) {
            Text(message.text)
        }
    }
}