package com.reshmenamma.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.reshmenamma.app.data.entities.CommunityPost
import com.reshmenamma.app.data.entities.PostCategory
import com.reshmenamma.app.ui.theme.*
import com.reshmenamma.app.viewmodel.CommunityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    onNavigateBack: () -> Unit,
    onPostClick: (String) -> Unit,
    viewModel: CommunityViewModel = viewModel()
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }

    val posts by viewModel.posts.collectAsState(initial = emptyList())

    val filteredPosts = if (selectedCategory != null) {
        posts.filter { it.category == selectedCategory }
    } else {
        posts
    }

    // Create Post Dialog
    if (showCreateDialog) {
        CreatePostDialog(
            onDismiss = { showCreateDialog = false },
            onCreatePost = { title, content, category ->
                viewModel.createPost(title, content, category)
                showCreateDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("🌾 Farmer Community", color = SilkWhite)
                        Text("Share & Learn Together", style = MaterialTheme.typography.bodySmall, color = SilkGoldLight)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = SilkWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Mulberry)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = Mulberry,
                contentColor = SilkWhite
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Create Post")
            }
        },
        containerColor = SilkWhite
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Search posts...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Mulberry, focusedLabelColor = Mulberry),
                singleLine = true
            )

            ScrollableTabRow(
                selectedTabIndex = 0,
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp,
                divider = {}
            ) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("All") },
                    modifier = Modifier.padding(end = 8.dp)
                )
                PostCategory.entries.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category.value,
                        onClick = { selectedCategory = if (selectedCategory == category.value) null else category.value },
                        label = { Text("${category.icon} ${category.displayName}") },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredPosts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📝", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No posts yet", style = MaterialTheme.typography.titleMedium, color = TextSecondary)
                        Text("Be the first to share!", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filteredPosts, key = { it.id }) { post ->
                        CommunityPostCard(
                            post = post,
                            onClick = { onPostClick(post.id) },
                            onLike = { viewModel.toggleLike(post.id) },
                            onBookmark = { viewModel.toggleBookmark(post.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CreatePostDialog(
    onDismiss: () -> Unit,
    onCreatePost: (String, String, String) -> Unit
) {
    var postTitle by remember { mutableStateOf("") }
    var postContent by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("general") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("✏️ Create New Post", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                OutlinedTextField(
                    value = postTitle,
                    onValueChange = { postTitle = it; showError = false },
                    label = { Text("Title") },
                    placeholder = { Text("Enter post title...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = showError && postTitle.isBlank()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = postContent,
                    onValueChange = { postContent = it; showError = false },
                    label = { Text("Content") },
                    placeholder = { Text("Share your experience or ask a question...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5,
                    isError = showError && postContent.isBlank()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("Category", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "general" to "General",
                        "question" to "Question",
                        "success" to "Success",
                        "tip" to "Tip"
                    ).forEach { (value, label) ->
                        FilterChip(
                            selected = selectedCategory == value,
                            onClick = { selectedCategory = value },
                            label = { Text(label) }
                        )
                    }
                }
                if (showError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Please fill title and content", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (postTitle.isNotBlank() && postContent.isNotBlank()) {
                        onCreatePost(postTitle, postContent, selectedCategory)
                    } else {
                        showError = true
                    }
                }
            ) {
                Text("Post")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CommunityPostCard(
    post: CommunityPost,
    onClick: () -> Unit,
    onLike: () -> Unit,
    onBookmark: () -> Unit
) {
    val category = PostCategory.fromValue(post.category)

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Mulberry.copy(alpha = 0.1f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(post.authorName.first().toString(), fontWeight = FontWeight.Bold, color = Mulberry)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(post.authorName, fontWeight = FontWeight.Bold)
                        if (post.isVerifiedExpert) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Verified, null, tint = Mulberry, modifier = Modifier.size(16.dp))
                        }
                    }
                    Text(post.authorLocation, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                Surface(
                    color = androidx.compose.ui.graphics.Color(category.color).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "${category.icon} ${category.displayName}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = androidx.compose.ui.graphics.Color(category.color)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(post.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(post.content, maxLines = 3, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onLike, modifier = Modifier.size(32.dp)) {
                        Icon(
                            if (post.isLikedByUser) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (post.isLikedByUser) DangerRed else TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text("${post.likeCount}", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(Icons.Default.Comment, null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${post.commentCount}", style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = onBookmark, modifier = Modifier.size(32.dp)) {
                    Icon(
                        if (post.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (post.isBookmarked) SilkGold else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}