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
import com.reshmenamma.app.data.entities.ForumComment
import java.util.*

class ForumCommentAdapter(
    private val onReplyClick: (ForumComment) -> Unit,
    private val onLikeClick: (ForumComment) -> Unit,
    private val onDeleteClick: (ForumComment) -> Unit,
    private val onAuthorClick: (String) -> Unit,
    private val currentUserId: String? = null
) : ListAdapter<ForumComment, ForumCommentAdapter.CommentViewHolder>(CommentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forum_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivAuthorAvatar: ImageView = itemView.findViewById(R.id.ivCommentAuthorAvatar)
        private val tvAuthorName: TextView = itemView.findViewById(R.id.tvCommentAuthorName)
        private val tvCommentTime: TextView = itemView.findViewById(R.id.tvCommentTime)
        private val tvCommentText: TextView = itemView.findViewById(R.id.tvCommentText)
        private val tvLikeCount: TextView = itemView.findViewById(R.id.tvCommentLikeCount)
        private val tvReplyCount: TextView = itemView.findViewById(R.id.tvReplyCount)
        private val btnLike: ImageButton = itemView.findViewById(R.id.btnCommentLike)
        private val btnReply: ImageButton = itemView.findViewById(R.id.btnCommentReply)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnCommentDelete)
        private val viewReplyIndicator: View = itemView.findViewById(R.id.viewReplyIndicator)
        private val viewExpertBadge: ImageView = itemView.findViewById(R.id.viewExpertBadge)

        fun bind(comment: ForumComment) {
            val context = itemView.context

            // Author info
            tvAuthorName.text = comment.authorName
            tvCommentText.text = comment.content

            // Relative time display
            tvCommentTime.text = getRelativeTime(comment.timestamp)

            // Expert badge
            viewExpertBadge.visibility = if (comment.isExpert) View.VISIBLE else View.GONE

            // Like count
            tvLikeCount.text = if (comment.likeCount > 0) "${comment.likeCount}" else ""

            // Reply count
            tvReplyCount.text = if (comment.replyCount > 0) "${comment.replyCount} replies" else "Reply"

            // Reply indentation for nested comments
            val replyMargin = comment.depth * 48 // 48dp per depth level
            val layoutParams = itemView.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.marginStart = replyMargin
            itemView.layoutParams = layoutParams

            // Reply indicator line
            viewReplyIndicator.visibility = if (comment.parentId != null) View.VISIBLE else View.GONE

            // Delete button (only shown for user's own comments)
            btnDelete.visibility = if (comment.authorId == currentUserId) View.VISIBLE else View.GONE

            // Like button state
            btnLike.setImageResource(
                if (comment.isLiked) R.drawable.ic_thumb_up_filled
                else R.drawable.ic_thumb_up_outline
            )

            // Highlight user's own comments
            if (comment.authorId == currentUserId) {
                itemView.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.mulberry_light_transparent)
                )
            } else {
                itemView.setBackgroundColor(
                    ContextCompat.getColor(context, android.R.color.transparent)
                )
            }

            // Click listeners
            tvAuthorName.setOnClickListener { onAuthorClick(comment.authorId) }
            btnLike.setOnClickListener { onLikeClick(comment) }
            btnReply.setOnClickListener { onReplyClick(comment) }
            btnDelete.setOnClickListener { onDeleteClick(comment) }
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

    private class CommentDiffCallback : DiffUtil.ItemCallback<ForumComment>() {
        override fun areItemsTheSame(oldItem: ForumComment, newItem: ForumComment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ForumComment, newItem: ForumComment): Boolean {
            return oldItem == newItem
        }
    }
}