package com.reshmenamma.app.data

import androidx.room.*
import com.reshmenamma.app.data.entities.CommunityPost
import kotlinx.coroutines.flow.Flow

@Dao
interface CommunityPostDao {
    @Query("SELECT * FROM community_posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<CommunityPost>>

    @Query("SELECT * FROM community_posts WHERE category = :category ORDER BY timestamp DESC")
    fun getPostsByCategory(category: String): Flow<List<CommunityPost>>

    @Query("SELECT * FROM community_posts WHERE id = :postId")
    suspend fun getPostById(postId: String): CommunityPost?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: CommunityPost)

    @Update
    suspend fun updatePost(post: CommunityPost)

    @Delete
    suspend fun deletePost(post: CommunityPost)

    @Query("UPDATE community_posts SET likeCount = likeCount + 1 WHERE id = :postId")
    suspend fun incrementLike(postId: String)

    @Query("UPDATE community_posts SET likeCount = likeCount - 1 WHERE id = :postId")
    suspend fun decrementLike(postId: String)

    @Query("UPDATE community_posts SET commentCount = commentCount + 1 WHERE id = :postId")
    suspend fun incrementCommentCount(postId: String)

    @Query("SELECT * FROM community_posts WHERE authorId = :authorId ORDER BY timestamp DESC")
    fun getPostsByAuthor(authorId: String): Flow<List<CommunityPost>>

    @Query("SELECT * FROM community_posts WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'")
    fun searchPosts(query: String): Flow<List<CommunityPost>>
}