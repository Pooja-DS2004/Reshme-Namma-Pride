package com.reshmenamma.app.adapters

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reshmenamma.app.R
import com.reshmenamma.app.data.entities.CommunityPost
import com.reshmenamma.app.data.entities.PostCategory
import java.text.SimpleDateFormat
import java.util.*

class CommunityPostAdapter(
    private val onPostClick: (CommunityPost) -> Unit,
    private val onLikeClick: (CommunityPost) -> Unit,
    private val onCommentClick: (CommunityPost) -> Unit,
    private val onShareClick: (CommunityPost) -> Unit,
    private val onBookmarkClick: (CommunityPost) -> Unit,
    private val onAuthorClick: (String) -> Unit
) : ListAdapter<CommunityPost, CommunityPostAdapter.PostViewHolder>(PostDiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_community_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivAuthorAvatar: ImageView = itemView.findViewById(R.id.ivAuthorAvatar)
        private val tvAuthorName: TextView = itemView.findViewById(R.id.tvAuthorName)
        private val tvAuthorLocation: TextView = itemView.findViewById(R.id.tvAuthorLocation)
        private val tvPostTime: TextView = itemView.findViewById(R.id.tvPostTime)
        private val tvPostCategory: TextView = itemView.findViewById(R.id.tvPostCategory)
        private val tvPostTitle: TextView = itemView.findViewById(R.id.tvPostTitle)
        private val tvPostContent: TextView = itemView.findViewById(R.id.tvPostContent)
        private val tvLikeCount: TextView = itemView.findViewById(R.id.tvLikeCount)
        private val tvCommentCount: TextView = itemView.findViewById(R.id.tvCommentCount)
        private val ivPostImage: ImageView = itemView.findViewById(R.id.ivPostImage)
        private val btnLike: ImageButton = itemView.findViewById(R.id.btnLike)
        private val btnComment: ImageButton = itemView.findViewById(R.id.btnComment)
        private val btnShare: ImageButton = itemView.findViewById(R.id.btnShare)
        private val btnBookmark: ImageButton = itemView.findViewById(R.id.btnBookmark)
        private val viewVerifiedBadge: ImageView = itemView.findViewById(R.id.viewVerifiedBadge)

        fun bind(post: CommunityPost) {
            val context = itemView.context

            // Author info
            tvAuthorName.text = post.authorName
            tvAuthorLocation.text = "📍 ${post.authorLocation}"
            tvPostTime.text = getRelativeTime(post.timestamp)

            // Verified badge
            viewVerifiedBadge.visibility = if (post.isVerifiedExpert) View.VISIBLE else View.GONE

            // Category badge with color
            val category = PostCategory.fromValue(post.category)
            tvPostCategory.text = category.displayName.uppercase()
            tvPostCategory.setBackgroundColor(
                ContextCompat.getColor(context, when (category) {
                    PostCategory.DISEASE -> R.color.danger_red
                    PostCategory.SUCCESS -> R.color.success_green
                    PostCategory.QUESTION -> R.color.info_blue
                    PostCategory.TIP -> R.color.warning_orange
                    PostCategory.MARKET -> R.color.silk_gold
                    else -> R.color.mulberry
                })
            )

            // Post content
            tvPostTitle.text = post.title
            tvPostContent.text = post.content

            // Interaction counts
            tvLikeCount.text = "${post.likeCount} Likes"
            tvCommentCount.text = "${post.commentCount} Comments"

            // Like button state
            btnLike.setImageResource(
                if (post.isLikedByUser) R.drawable.ic_heart_filled
                else R.drawable.ic_heart_outline
            )

            // Bookmark state
            btnBookmark.setImageResource(
                if (post.isBookmarked) R.drawable.ic_bookmark_filled
                else R.drawable.ic_bookmark_outline
            )

            // Post image handling
            if (!post.imageUrl.isNullOrEmpty()) {
                ivPostImage.visibility = View.VISIBLE
                // Load image using Glide or Coil
                // Glide.with(context).load(post.imageUrl).into(ivPostImage)
            } else {
                ivPostImage.visibility = View.GONE
            }

            // Click listeners
            itemView.setOnClickListener { onPostClick(post) }
            tvAuthorName.setOnClickListener { onAuthorClick(post.authorId) }
            btnLike.setOnClickListener { onLikeClick(post) }
            btnComment.setOnClickListener { onCommentClick(post) }
            btnShare.setOnClickListener { onShareClick(post) }
            btnBookmark.setOnClickListener { onBookmarkClick(post) }
        }

        private fun getRelativeTime(timestamp: Date): String {
            val now = System.currentTimeMillis()
            val time = timestamp.time
            return DateUtils.getRelativeTimeSpanString(
                time, now, DateUtils.MINUTE_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE
            ).toString()
        }
    }

    private class PostDiffCallback : DiffUtil.ItemCallback<CommunityPost>() {
        override fun areItemsTheSame(oldItem: CommunityPost, newItem: CommunityPost): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CommunityPost, newItem: CommunityPost): Boolean {
            return oldItem == newItem
        }
    }
}