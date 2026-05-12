package com.reshmenamma.app.data

import androidx.room.*
import com.reshmenamma.app.data.entities.ForumComment
import kotlinx.coroutines.flow.Flow

@Dao
interface ForumCommentDao {
    @Query("SELECT * FROM forum_comments WHERE postId = :postId ORDER BY timestamp ASC")
    fun getCommentsForPost(postId: String): Flow<List<ForumComment>>

    @Query("SELECT * FROM forum_comments WHERE parentId = :parentId ORDER BY timestamp ASC")
    fun getRepliesForComment(parentId: String): Flow<List<ForumComment>>

    @Query("SELECT * FROM forum_comments WHERE id = :commentId")
    suspend fun getCommentById(commentId: String): ForumComment?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: ForumComment)

    @Update
    suspend fun updateComment(comment: ForumComment)

    @Delete
    suspend fun deleteComment(comment: ForumComment)

    @Query("DELETE FROM forum_comments WHERE postId = :postId")
    suspend fun deleteCommentsForPost(postId: String)

    @Query("UPDATE forum_comments SET likeCount = likeCount + 1 WHERE id = :commentId")
    suspend fun incrementLike(commentId: String)

    @Query("UPDATE forum_comments SET likeCount = likeCount - 1 WHERE id = :commentId AND likeCount > 0")
    suspend fun decrementLike(commentId: String)

    @Query("UPDATE forum_comments SET replyCount = replyCount + 1 WHERE id = :parentId")
    suspend fun incrementReplyCount(parentId: String)

    @Query("SELECT COUNT(*) FROM forum_comments WHERE postId = :postId")
    suspend fun getCommentCountForPost(postId: String): Int
}