package com.reshmenamma.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "forum_comments")
data class ForumComment(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),
    val postId: String,
    val parentId: String? = null, // null = top-level comment, non-null = reply
    val authorId: String,
    val authorName: String,
    val authorAvatarUrl: String? = null,
    val isExpert: Boolean = false,
    val content: String,
    val timestamp: Date = Date(),
    val likeCount: Int = 0,
    val replyCount: Int = 0,
    val isLiked: Boolean = false,
    val depth: Int = 0, // 0 = top level, 1 = first reply, 2 = reply to reply
    val isEdited: Boolean = false,
    val editedTimestamp: Date? = null
)