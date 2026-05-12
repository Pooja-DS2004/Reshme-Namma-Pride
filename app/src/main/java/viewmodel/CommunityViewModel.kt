package com.reshmenamma.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.reshmenamma.app.data.AppDatabase
import com.reshmenamma.app.data.entities.CommunityPost
import com.reshmenamma.app.data.entities.ForumComment
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date

class CommunityViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val postDao = database.communityPostDao()
    private val commentDao = database.forumCommentDao()

    val posts: Flow<List<CommunityPost>> = postDao.getAllPosts()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        addDemoPosts()
    }

    private fun addDemoPosts() {
        viewModelScope.launch {
            val count = postDao.getAllPosts().first().size
            if (count == 0) {
                val demoPosts = listOf(
                    CommunityPost("demo_1", "farmer_ramesh", "Ramesh Kumar", "Ramanagara, Karnataka", null, true,
                        "Successful harvest using temperature control method",
                        "By maintaining exactly 25°C throughout the third instar stage, I achieved a 95% healthy cocoon yield this season.",
                        "success", null, Date(System.currentTimeMillis() - 3600000), 24, 2, false, false),
                    CommunityPost("demo_2", "farmer_lakshmi", "Lakshmi Devi", "Mysore, Karnataka", null, false,
                        "Urgent: Silkworms showing signs of Flacherie disease",
                        "My silkworms in the second instar are showing symptoms of lethargy and loss of appetite.",
                        "disease", null, Date(System.currentTimeMillis() - 7200000), 15, 1, false, false),
                    CommunityPost("demo_3", "expert_venkat", "Dr. Venkatesh Rao", "Central Silk Board, Bangalore", null, true,
                        "Expert Tip: Ideal humidity management for Instar 4",
                        "During the fourth instar, silkworms consume maximum food and grow rapidly. Maintain humidity between 70-80%.",
                        "tip", null, Date(System.currentTimeMillis() - 14400000), 42, 0, false, false),
                    CommunityPost("demo_4", "farmer_guru", "Gururaj Patil", "Channapatna, Karnataka", null, false,
                        "Question: Best mulberry variety for summer batch?",
                        "I am planning to start a summer batch with CSR2 hybrid silkworms. Which mulberry variety performs best?",
                        "question", null, Date(System.currentTimeMillis() - 21600000), 8, 0, false, false),
                    CommunityPost("demo_5", "market_trader", "Silk Market Updates", "Ramanagara Silk Exchange", null, false,
                        "Market Update: Cocoon prices expected to rise",
                        "Due to reduced production, Indian cocoon prices are expected to increase by 15-20%.",
                        "market", null, Date(System.currentTimeMillis() - 43200000), 35, 0, false, false)
                )
                demoPosts.forEach { postDao.insertPost(it) }
            }
        }
    }

    fun getPost(postId: String): Flow<CommunityPost?> = flow { emit(postDao.getPostById(postId)) }

    fun getComments(postId: String): Flow<List<ForumComment>> =
        commentDao.getCommentsForPost(postId).stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            postDao.getPostById(postId)?.let { post ->
                if (post.isLikedByUser) {
                    postDao.decrementLike(postId)
                    postDao.updatePost(post.copy(isLikedByUser = false))
                } else {
                    postDao.incrementLike(postId)
                    postDao.updatePost(post.copy(isLikedByUser = true))
                }
            }
        }
    }

    fun toggleBookmark(postId: String) {
        viewModelScope.launch {
            postDao.getPostById(postId)?.let { post ->
                postDao.updatePost(post.copy(isBookmarked = !post.isBookmarked))
            }
        }
    }

    fun addComment(postId: String, content: String, parentId: String? = null) {
        viewModelScope.launch {
            val depth = if (parentId != null) commentDao.getCommentById(parentId)?.depth?.plus(1) ?: 0 else 0
            val comment = ForumComment(
                id = java.util.UUID.randomUUID().toString(),
                postId = postId, parentId = parentId, authorId = "current_user",
                authorName = "You", content = content, depth = depth, timestamp = Date()
            )
            commentDao.insertComment(comment)
            postDao.incrementCommentCount(postId)
            parentId?.let { commentDao.incrementReplyCount(it) }
        }
    }

    fun toggleCommentLike(commentId: String) {
        viewModelScope.launch {
            commentDao.getCommentById(commentId)?.let { comment ->
                if (comment.isLiked) {
                    commentDao.decrementLike(commentId)
                    commentDao.updateComment(comment.copy(isLiked = false))
                } else {
                    commentDao.incrementLike(commentId)
                    commentDao.updateComment(comment.copy(isLiked = true))
                }
            }
        }
    }

    fun createPost(title: String, content: String, category: String, imageUrl: String? = null) {
        viewModelScope.launch {
            val post = CommunityPost(
                id = java.util.UUID.randomUUID().toString(),
                authorId = "current_user",
                authorName = "You",
                authorLocation = "Karnataka",
                title = title,
                content = content,
                category = category,
                imageUrl = imageUrl,
                timestamp = Date()
            )
            postDao.insertPost(post)
        }
    }

    fun loadComments(postId: String) {}
    fun searchPosts(query: String): Flow<List<CommunityPost>> = postDao.searchPosts(query)
    fun getPostsByCategory(category: String): Flow<List<CommunityPost>> = postDao.getPostsByCategory(category)
}