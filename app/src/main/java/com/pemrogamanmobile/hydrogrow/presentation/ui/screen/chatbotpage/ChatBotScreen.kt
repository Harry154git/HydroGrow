package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.chatbotpage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pemrogamanmobile.hydrogrow.domain.model.ChatMessage
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.chatbotpage.ChatBotScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotScreen(
    navController: NavController,
    viewModel: ChatBotScreenViewModel = hiltViewModel(),
    chatbotId: String? = null // Terima ID dari argumen navigasi
) {
    // Muat percakapan jika chatbotId diberikan saat pertama kali screen dibuat
    LaunchedEffect(chatbotId) {
        if (chatbotId != null) {
            viewModel.loadChat(chatbotId)
        }
    }

    // Ambil semua state yang diperlukan dari ViewModel
    val messages by viewModel.messages
    val isTyping by viewModel.isTyping
    val error by viewModel.error
    var userInput by rememberSaveable { mutableStateOf("") }

    // Warna dari desain
    val backgroundColor = Color(0xFFE9FDD9)
    val sendButtonColor = Color(0xFF59A869)

    // State untuk Snackbar
    val snackbarHostState = remember { SnackbarHostState() }

    // Tampilkan Snackbar jika ada error
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Long
            )
            viewModel.clearError() // Hapus error setelah ditampilkan
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = backgroundColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("ChatBot", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Aksi untuk menu */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            UserInputArea(
                value = userInput,
                onValueChange = { userInput = it },
                onSend = {
                    viewModel.sendMessage(userInput)
                    userInput = ""
                },
                buttonColor = sendButtonColor
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Tampilkan UI awal hanya jika tidak ada pesan dan AI tidak sedang mengetik
            if (messages.isEmpty() && !isTyping) {
                InitialChatView(
                    onSuggestionClick = { suggestion ->
                        userInput = suggestion
                        // Opsional: langsung kirim pesan saat sugesti diklik
                        // viewModel.sendMessage(suggestion)
                    }
                )
            } else {
                // Tampilkan daftar chat jika sudah ada percakapan
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    state = rememberLazyListState(),
                    reverseLayout = true // Pesan baru muncul dari bawah
                ) {
                    // Tampilkan indikator "mengetik" di paling bawah
                    if (isTyping) {
                        item {
                            ChatBubble(ChatMessage(role = "model", content = "..."))
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    items(messages.reversed()) { message ->
                        ChatBubble(message)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun InitialChatView(onSuggestionClick: (String) -> Unit) {
    val suggestions = listOf(
        "Bagaimana cara kerja sistem hidroponik sumbu / NFT / DFT / rakit apung?",
        "Buatkan jadwal tanam dan perawatan otomatis berdasarkan tanaman pilihan saya!",
        "Saya ingin menanam sayuran daun, apa saja yang direkomendasikan?"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "👋 Halo Anna !",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(48.dp))
        suggestions.forEach { suggestion ->
            SuggestionCard(
                text = suggestion,
                onClick = { onSuggestionClick(suggestion) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun SuggestionCard(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center,
            color = Color.DarkGray
        )
    }
}

@Composable
private fun UserInputArea(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    buttonColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* TODO: Aksi tombol tambah */ }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah", tint = Color.Gray)
            }
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("bertanya apa saja tentang tanaman") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )
            IconButton(onClick = onSend) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(buttonColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ArrowUpward,
                        contentDescription = "Kirim",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isUser = message.role == ChatMessage.ROLE_USER
    val bubbleColor = if (isUser) Color(0xFFDCF8C6) else Color.White
    val alignment = if (isUser) Alignment.End else Alignment.Start

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(alignment)
    ) {
        Box(
            modifier = Modifier
                .background(bubbleColor, shape = RoundedCornerShape(12.dp))
                .padding(12.dp)
                .widthIn(max = 280.dp),
        ) {
            Text(message.content)
        }
    }
}