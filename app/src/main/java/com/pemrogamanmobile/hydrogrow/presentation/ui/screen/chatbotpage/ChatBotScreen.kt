package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.chatbotpage

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pemrogamanmobile.hydrogrow.domain.model.ChatBot
import com.pemrogamanmobile.hydrogrow.domain.model.ChatMessage
import com.pemrogamanmobile.hydrogrow.domain.model.Garden
import com.pemrogamanmobile.hydrogrow.domain.model.Plant
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.chatbotpage.ChatBotScreenViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

//================================//
//     MAIN COMPOSABLE SCREEN     //
//================================//

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotScreen(
    navController: NavController,
    viewModel: ChatBotScreenViewModel = hiltViewModel(),
    chatbotId: String? = null
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Ambil state dari ViewModel
    val messages by viewModel.messages
    val isTyping by viewModel.isTyping
    val error by viewModel.error
    val chatHistory by viewModel.chatHistory
    val activeChatId by viewModel.chatId
    val selectedContext by viewModel.selectedContext
    val gardens by viewModel.gardens
    val plants by viewModel.plants
    var userInput by rememberSaveable { mutableStateOf("") }

    // State untuk UI Interaktif
    var showAttachmentSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showContextDialog by remember { mutableStateOf<String?>(null) } // "garden" atau "plant"
    var showContextTypeSelectionDialog by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Launcher untuk mengambil gambar dari galeri
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { imageUri = it }
    }

    // Launcher untuk mengambil foto dari kamera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (!success) {
            imageUri = null
        }
    }

    // Launcher untuk meminta izin kamera
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val file = context.createImageFile()
            val uri = FileProvider.getUriForFile(
                Objects.requireNonNull(context),
                context.packageName + ".provider", file
            )
            imageUri = uri
            cameraLauncher.launch(uri)
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Izin kamera diperlukan untuk mengambil foto.")
            }
        }
    }

    // Muat percakapan atau mulai yang baru
    LaunchedEffect(chatbotId) {
        if (chatbotId != null) {
            viewModel.loadChat(chatbotId)
        } else {
            viewModel.startNewChat()
        }
    }

    // Sinkronisasi navigasi setelah chat baru dibuat
    LaunchedEffect(activeChatId) {
        if (chatbotId == null && activeChatId != null) {
            navController.navigate("chatbot_screen?chatbotId=$activeChatId") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    // Tampilkan error jika ada
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(message = it, duration = SnackbarDuration.Long)
            viewModel.clearError()
        }
    }

    val backgroundColor = Color(0xFFE9FDD9)
    val sendButtonColor = Color(0xFF59A869)

    // Tampilkan dialog pemilihan tipe konteks (Garden/Plant)
    if (showContextTypeSelectionDialog) {
        ContextTypeSelectionDialog(
            onDismiss = { showContextTypeSelectionDialog = false },
            onSelection = { type ->
                showContextTypeSelectionDialog = false
                showContextDialog = type
            }
        )
    }

    // Tampilkan dialog pemilihan item konteks
    if (showContextDialog != null) {
        val type = showContextDialog!!
        val (items, title) = if (type == "garden") gardens to "Pilih Kebun" else plants to "Pilih Tanaman"
        ItemListDialog(
            title = title,
            items = items,
            onDismiss = { showContextDialog = null },
            onItemSelected = { id, name ->
                viewModel.selectContext(id, name, type)
                showContextDialog = null
            }
        )
    }

    // Tampilkan bottom sheet untuk lampiran
    if (showAttachmentSheet) {
        AttachmentBottomSheet(
            onDismiss = { showAttachmentSheet = false },
            sheetState = sheetState,
            onCameraClick = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showAttachmentSheet = false
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            val file = context.createImageFile()
                            val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
                            imageUri = uri
                            cameraLauncher.launch(uri)
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                }
            },
            onGalleryClick = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showAttachmentSheet = false
                        galleryLauncher.launch("image/*")
                    }
                }
            },
            onAskWithContextClick = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showAttachmentSheet = false
                        showContextTypeSelectionDialog = true
                    }
                }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ChatHistoryDrawer(
                histories = chatHistory,
                currentChatId = activeChatId,
                onHistoryClick = { historyId ->
                    scope.launch { drawerState.close() }
                    if (historyId != activeChatId) {
                        navController.navigate("chatbot_screen?chatbotId=$historyId") {
                            launchSingleTop = true
                        }
                    }
                },
                onNewChatClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate("chatbot_screen") {
                        popUpTo(navController.graph.startDestinationId)
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
                    title = { Text("HydroGrow", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Buka Menu")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
            },
            bottomBar = {
                Column(modifier = Modifier.background(backgroundColor)) {
                    selectedContext?.let { (_, name, _) ->
                        ContextChip(
                            contextName = name,
                            onClear = { viewModel.clearContext() }
                        )
                    }
                    imageUri?.let { uri ->
                        ImagePreview(
                            imageUri = uri,
                            onClearImage = { imageUri = null }
                        )
                    }
                    UserInputArea(
                        value = userInput,
                        onValueChange = { userInput = it },
                        onSend = {
                            // ✅ FIX: Lakukan konversi Uri ke File di sini
                            val currentInput = userInput
                            val currentImageUri = imageUri

                            // Konversi Uri ke File HANYA jika tidak null
                            val imageFile: File? = currentImageUri?.toFile(context)

                            // Reset UI state untuk responsivitas
                            userInput = ""
                            imageUri = null

                            // Panggil fungsi ViewModel dengan parameter yang sudah benar (File, bukan Uri)
                            viewModel.sendMessage(currentInput, imageFile)
                        },
                        onAttachmentClick = { showAttachmentSheet = true },
                        buttonColor = sendButtonColor,
                        isSendEnabled = userInput.isNotBlank() || imageUri != null
                    )
                }
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
                        }
                    )
                } else {
                    val listState = rememberLazyListState()
                    LaunchedEffect(messages.size) {
                        if (messages.isNotEmpty()) {
                            listState.animateScrollToItem(messages.size - 1)
                        }
                    }
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        state = listState,
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(messages, key = { "${it.timestamp}-${it.content}-${it.imageUrl}" }) { message ->
                            ChatBubble(message)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        if (isTyping) {
                            item {
                                ChatBubble(ChatMessage(role = "model", content = "..."))
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}


//================================//
//      UI HELPER COMPOSABLES     //
//================================//

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatHistoryDrawer(
    histories: List<ChatBot>,
    currentChatId: String?,
    onHistoryClick: (String) -> Unit,
    onNewChatClick: () -> Unit
) {
    ModalDrawerSheet(modifier = Modifier.fillMaxWidth(0.8f)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNewChatClick)
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Edit, "Percakapan Baru", Modifier.size(24.dp))
                Spacer(Modifier.width(16.dp))
                Text("Mulai percakapan baru", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(16.dp))
            Divider()
            Spacer(Modifier.height(16.dp))
            Text("History", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))
            LazyColumn {
                items(histories, key = { it.id }) { history ->
                    HistoryItem(history.title, history.id == currentChatId) { onHistoryClick(history.id) }
                }
            }
        }
    }
}

@Composable
private fun HistoryItem(title: String, isSelected: Boolean, onClick: () -> Unit) {
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
        Text(title, color = textColor, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal, fontSize = 14.sp, maxLines = 1)
    }
    Spacer(Modifier.height(4.dp))
}

@Composable
private fun InitialChatView(onSuggestionClick: (String) -> Unit) {
    val suggestions = listOf(
        "Bagaimana cara kerja sistem hidroponik NFT?",
        "Buatkan jadwal tanam dan perawatan otomatis untuk selada!",
        "Rekomendasi sayuran daun untuk pemula?"
    )
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("👋 Halo!", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text("Tanyakan apa saja tentang hidroponik", fontSize = 18.sp, color = Color.Gray)
        Spacer(Modifier.height(48.dp))
        suggestions.forEach { suggestion ->
            SuggestionCard(suggestion) { onSuggestionClick(suggestion) }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun SuggestionCard(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(text, modifier = Modifier.padding(16.dp), textAlign = TextAlign.Center, color = Color.DarkGray)
    }
}

@Composable
private fun UserInputArea(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onAttachmentClick: () -> Unit,
    buttonColor: Color,
    isSendEnabled: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onAttachmentClick) { Icon(Icons.Default.Add, "Tambah Lampiran", tint = Color.Gray) }
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Bertanya apa saja...") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )
            IconButton(onClick = onSend, enabled = isSendEnabled) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(if (isSendEnabled) buttonColor else Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowUpward, "Kirim", tint = Color.White)
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isUserMessage = message.role == "user"
    val bubbleColor = if (isUserMessage) Color(0xFFDCF8C6) else Color.White
    val alignment = if (isUserMessage) Alignment.End else Alignment.Start
    Box(
        modifier = Modifier.fillMaxWidth().wrapContentWidth(alignment)
    ) {
        Box(
            modifier = Modifier.background(bubbleColor, RoundedCornerShape(12.dp)).widthIn(max = 300.dp),
        ) {
            Column(modifier = Modifier.padding(if (message.imageUrl != null) 8.dp else 12.dp)) {
                message.imageUrl?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = "Gambar dari Pengguna",
                        modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp).clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                if (message.content.isNotEmpty() && message.content != "...") {
                    Spacer(Modifier.height(if (message.imageUrl != null) 8.dp else 0.dp))
                    Text(message.content)
                }
                if (message.content == "...") {
                    Text(message.content)
                }
            }
        }
    }
}

@Composable
private fun ImagePreview(imageUri: Uri, onClearImage: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 8.dp)) {
        AsyncImage(
            model = imageUri,
            contentDescription = "Image Preview",
            modifier = Modifier.size(100.dp).clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        IconButton(
            onClick = onClearImage,
            modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(Icons.Default.Close, "Hapus Gambar", tint = Color.White, modifier = Modifier.size(18.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttachmentBottomSheet(
    onDismiss: () -> Unit,
    sheetState: SheetState,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onAskWithContextClick: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Tambahkan ke Pesan", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(start = 8.dp, bottom = 16.dp))
            AttachmentOption(Icons.Default.CameraAlt, "Ambil Foto", onCameraClick)
            AttachmentOption(Icons.Default.Collections, "Pilih dari Galeri", onGalleryClick)
            AttachmentOption(Icons.Default.QuestionAnswer, "Tanya dengan Konteks", onAskWithContextClick)
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AttachmentOption(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, text, Modifier.size(28.dp))
        Spacer(Modifier.width(16.dp))
        Text(text, fontSize = 16.sp)
    }
}

@Composable
fun ContextTypeSelectionDialog(onDismiss: () -> Unit, onSelection: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pilih Konteks Pertanyaan") },
        text = {
            Column {
                Text("Berdasarkan Kebun", Modifier.fillMaxWidth().clickable { onSelection("garden") }.padding(vertical = 12.dp))
                Text("Berdasarkan Tanaman", Modifier.fillMaxWidth().clickable { onSelection("plant") }.padding(vertical = 12.dp))
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@Composable
fun <T> ItemListDialog(title: String, items: List<T>, onDismiss: () -> Unit, onItemSelected: (id: String, name: String) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            if (items.isEmpty()) {
                Text("Tidak ada data tersedia.")
            } else {
                LazyColumn {
                    items(items, key = { item ->
                        when (item) {
                            is Garden -> item.id
                            is Plant -> item.id
                            else -> item.hashCode()
                        }
                    }) { item ->
                        val (id, name) = when (item) {
                            is Garden -> item.id to item.gardenName
                            is Plant -> item.id to item.plantName
                            else -> "" to "Item tidak dikenal"
                        }
                        Text(name, Modifier.fillMaxWidth().clickable { onItemSelected(id, name) }.padding(vertical = 12.dp))
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@Composable
fun ContextChip(contextName: String, onClear: () -> Unit) {
    Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        AssistChip(
            onClick = {},
            label = { Text("Konteks: $contextName") },
            trailingIcon = {
                Icon(Icons.Default.Close, "Hapus Konteks", Modifier.size(18.dp).clickable(onClick = onClear))
            }
        )
    }
}

//================================//
//       UTILITY FUNCTIONS        //
//================================//

fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(imageFileName, ".jpg", externalCacheDir)
}

// Letakkan kode ini di bagian paling bawah file ChatBotScreen.kt

fun Uri.toFile(context: Context): File {
    val contentResolver = context.contentResolver
    val fileName = "temp_image_${System.currentTimeMillis()}.jpg"
    val file = File(context.cacheDir, fileName)
    contentResolver.openInputStream(this)?.use { inputStream ->
        file.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }
    return file
}