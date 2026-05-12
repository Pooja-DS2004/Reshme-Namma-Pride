package com.reshmenamma.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "community_posts")
data class CommunityPost(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),
    val authorId: String,
    val authorName: String,
    val authorLocation: String,
    val authorAvatarUrl: String? = null,
    val isVerifiedExpert: Boolean = false,
    val title: String,
    val content: String,
    val category: String, // "disease", "success", "question", "tip", "market"
    val imageUrl: String? = null,
    val timestamp: Date = Date(),
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val isLikedByUser: Boolean = false,
    val isBookmarked: Boolean = false,
    val tags: List<String> = emptyList(),
    val viewCount: Int = 0
)

enum class PostCategory(val value: String, val displayName: String, val icon: String, val color: Long) {
    DISEASE("disease", "Disease Alert", "🦠", 0xFFDC143C),
    SUCCESS("success", "Success Story", "🎉", 0xFF00C853),
    QUESTION("question", "Question", "❓", 0xFF2196F3),
    TIP("tip", "Expert Tip", "💡", 0xFFFF8C00),
    MARKET("market", "Market Update", "💰", 0xFFDAA520),
    GENERAL("general", "General", "📝", 0xFF8B2252);

    companion object {
        fun fromValue(value: String): PostCategory {
            return entries.find { it.value == value } ?: GENERAL
        }
    }
}