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
import androidx.compose.material.icons.outlined.Edit
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
import com.pemrogamanmobile.hydrogrow.domain.model.ChatBot
import com.pemrogamanmobile.hydrogrow.domain.model.ChatMessage
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.chatbotpage.ChatBotScreenViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotScreen(
    navController: NavController,
    viewModel: ChatBotScreenViewModel = hiltViewModel(),
    chatbotId: String? = null
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Ambil state dari ViewModel
    val messages by viewModel.messages
    val isTyping by viewModel.isTyping
    val error by viewModel.error
    val chatHistory by viewModel.chatHistory
    val activeChatId by viewModel.chatId
    var userInput by rememberSaveable { mutableStateOf("") }

    // Muat percakapan jika chatbotId dari argumen navigasi diberikan
    LaunchedEffect(chatbotId) {
        if (chatbotId != null) {
            viewModel.loadChat(chatbotId)
        } else {
            if (messages.isEmpty()) {
                viewModel.startNewChat()
            }
        }
    }

    val backgroundColor = Color(0xFFE9FDD9)
    val sendButtonColor = Color(0xFF59A869)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(message = it, duration = SnackbarDuration.Long)
            viewModel.clearError()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ChatHistoryDrawer(
                histories = chatHistory,
                currentChatId = activeChatId,
                onHistoryClick = { historyId ->
                    scope.launch { drawerState.close() }
                    // PERBAIKAN: Gunakan rute yang benar "chatbot_screen"
                    navController.navigate("chatbot_screen?chatbotId=$historyId") {
                        launchSingleTop = true
                    }
                },
                onNewChatClick = {
                    scope.launch { drawerState.close() }
                    viewModel.startNewChat()
                    // PERBAIKAN: Gunakan rute yang benar "chatbot_screen" tanpa argumen
                    navController.navigate("chatbot_screen") {
                        launchSingleTop = true
                    }
                }
            )
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("ChatBot", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Buka Menu")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    )
                )
            },
            bottomBar = {
                UserInputArea(
                    value = userInput,
                    onValueChange = { userInput = it },
                    onSend = {
                        if (userInput.isNotBlank()) {
                            viewModel.sendMessage(userInput)
                            userInput = ""
                        }
                    },
                    buttonColor = sendButtonColor
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                if (messages.isEmpty() && !isTyping) {
                    InitialChatView(
                        onSuggestionClick = { suggestion ->
                            userInput = suggestion
                            viewModel.sendMessage(suggestion)
                            userInput = ""
                        }
                    )
                } else {
                    val listState = rememberLazyListState()
                    LaunchedEffect(messages) {
                        if (messages.isNotEmpty()) {
                            listState.animateScrollToItem(0)
                        }
                    }
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        state = listState,
                        reverseLayout = true
                    ) {
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
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatHistoryDrawer(
    histories: List<ChatBot>,
    currentChatId: String?,
    onHistoryClick: (String) -> Unit,
    onNewChatClick: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.fillMaxWidth(0.8f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNewChatClick)
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Percakapan Baru",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "mulai percakapan baru",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "History",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn {
                items(histories, key = { it.id }) { history ->
                    HistoryItem(
                        title = history.title,
                        isSelected = history.id == currentChatId,
                        onClick = { onHistoryClick(history.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFFD7F5D7) else Color.Transparent
    val textColor = if (isSelected) Color.Black else Color.DarkGray

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = title,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
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
            .fillMaxSize()
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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
            IconButton(onClick = onSend, enabled = value.isNotBlank()) {
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
                .widthIn(max = 300.dp),
        ) {
            Text(message.content)
        }
    }
}