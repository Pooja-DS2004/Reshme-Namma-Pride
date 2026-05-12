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
import com.reshmenamma.app.data.entities.ForumComment
import com.reshmenamma.app.data.entities.PostCategory
import com.reshmenamma.app.ui.theme.*
import com.reshmenamma.app.viewmodel.CommunityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: String,
    onNavigateBack: () -> Unit,
    viewModel: CommunityViewModel = viewModel()
) {
    var replyText by remember { mutableStateOf("") }
    var replyingTo by remember { mutableStateOf<ForumComment?>(null) }

    val post by viewModel.getPost(postId).collectAsState(initial = null)
    val comments by viewModel.getComments(postId).collectAsState(initial = emptyList<ForumComment>())

    LaunchedEffect(postId) {
        viewModel.loadComments(postId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post", color = SilkWhite) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = SilkWhite)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share */ }) {
                        Icon(Icons.Default.Share, "Share", tint = SilkWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Mulberry)
            )
        },
        containerColor = SilkWhite
    ) { padding ->
        if (post == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Mulberry)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Full Post Content
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        // Author info
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Mulberry.copy(alpha = 0.1f),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        post!!.authorName.first().toString(),
                                        fontWeight = FontWeight.Bold,
                                        color = Mulberry
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(post!!.authorName, fontWeight = FontWeight.Bold)
                                Text("📍 ${post!!.authorLocation}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Title
                        Text(post!!.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(8.dp))

                        // Content
                        Text(post!!.content, style = MaterialTheme.typography.bodyLarge, lineHeight = 24.sp)
                    }
                }
            }

            // Comments Header
            item {
                Text(
                    "💬 ${comments.size} Comments",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Comments List
            items(comments, key = { it.id }) { comment ->
                CommentCard(
                    comment = comment,
                    onReply = { replyingTo = comment },
                    onLike = { viewModel.toggleCommentLike(comment.id) }
                )
            }
        }

        // Reply Bar at Bottom
        if (replyingTo != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Mulberry.copy(alpha = 0.05f)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Replying to ${replyingTo?.authorName}", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { replyingTo = null }) {
                        Icon(Icons.Default.Close, "Cancel", modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        // Reply Input
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Write a comment...") },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Mulberry),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (replyText.isNotBlank()) {
                            viewModel.addComment(postId, replyText, replyingTo?.id)
                            replyText = ""
                            replyingTo = null
                        }
                    },
                    enabled = replyText.isNotBlank()
                ) {
                    Icon(
                        Icons.Default.Send,
                        "Send",
                        tint = if (replyText.isNotBlank()) Mulberry else TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun CommentCard(
    comment: ForumComment,
    onReply: () -> Unit,
    onLike: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (comment.depth * 24).dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(comment.authorName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                if (comment.isExpert) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.Verified, null, tint = Mulberry, modifier = Modifier.size(14.dp))
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    java.text.SimpleDateFormat("dd MMM", java.util.Locale.getDefault()).format(comment.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(comment.content, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onLike, modifier = Modifier.size(28.dp)) {
                    Icon(
                        if (comment.isLiked) Icons.Default.ThumbUp else Icons.Default.ThumbUpOffAlt,
                        null,
                        tint = if (comment.isLiked) Mulberry else TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text("${comment.likeCount}", style = MaterialTheme.typography.labelSmall)

                Spacer(modifier = Modifier.width(16.dp))

                TextButton(onClick = onReply) {
                    Text("Reply", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}