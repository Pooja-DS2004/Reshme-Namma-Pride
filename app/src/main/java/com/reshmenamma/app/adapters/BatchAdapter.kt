package com.reshmenamma.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reshmenamma.app.R
import com.reshmenamma.app.data.entities.Batch
import com.reshmenamma.app.data.entities.InstarStage
import java.text.SimpleDateFormat
import java.util.*

class BatchAdapter(
    private val onBatchClick: (Batch) -> Unit,
    private val onDeleteClick: (Batch) -> Unit,
    private val onCompleteClick: (Batch) -> Unit
) : ListAdapter<Batch, BatchAdapter.BatchViewHolder>(BatchDiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BatchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_batch, parent, false)
        return BatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: BatchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvBatchName: TextView = itemView.findViewById(R.id.tvBatchName)
        private val tvBreed: TextView = itemView.findViewById(R.id.tvBreed)
        private val tvStartDate: TextView = itemView.findViewById(R.id.tvStartDate)
        private val tvInstarStage: TextView = itemView.findViewById(R.id.tvInstarStage)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val tvDaysCount: TextView = itemView.findViewById(R.id.tvDaysCount)
        private val progressInstar: ProgressBar = itemView.findViewById(R.id.progressInstar)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
        private val btnComplete: ImageButton = itemView.findViewById(R.id.btnComplete)
        private val viewInstarIndicator: View = itemView.findViewById(R.id.viewInstarIndicator)

        fun bind(batch: Batch) {
            val context = itemView.context

            // Batch Name
            tvBatchName.text = batch.batchName

            // Breed with emoji
            tvBreed.text = "🐛 ${batch.breed}"

            // Start Date
            tvStartDate.text = "📅 Started: ${dateFormat.format(batch.startDate)}"

            // Instar stage display
            val instarInfo = InstarStage.fromInstar(batch.currentInstar)
            tvInstarStage.text = "Instar ${batch.currentInstar}: ${instarInfo.name}"

            // Calculate days since start
            val daysSinceStart = ((Date().time - batch.startDate.time) / (1000 * 60 * 60 * 24)).toInt()
            tvDaysCount.text = "Day $daysSinceStart"

            // Progress bar (5 stages = max 5)
            progressInstar.max = 5
            progressInstar.progress = batch.currentInstar

            // Color coding based on instar stage
            val instarColor = when (batch.currentInstar) {
                1 -> ContextCompat.getColor(context, R.color.instar_1)
                2 -> ContextCompat.getColor(context, R.color.instar_2)
                3 -> ContextCompat.getColor(context, R.color.instar_3)
                4 -> ContextCompat.getColor(context, R.color.instar_4)
                5 -> ContextCompat.getColor(context, R.color.instar_5)
                else -> ContextCompat.getColor(context, R.color.mulberry)
            }

            progressInstar.progressTintList = android.content.res.ColorStateList.valueOf(instarColor)
            viewInstarIndicator.setBackgroundColor(instarColor)

            // Active/Completed status
            if (batch.isActive) {
                tvStatus.text = "● Active"
                tvStatus.setTextColor(ContextCompat.getColor(context, R.color.success_green))
                btnComplete.visibility = View.VISIBLE
            } else {
                tvStatus.text = "● Completed"
                tvStatus.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
                btnComplete.visibility = View.GONE
            }

            // Click listeners
            itemView.setOnClickListener { onBatchClick(batch) }

            btnDelete.setOnClickListener {
                onDeleteClick(batch)
            }

            btnComplete.setOnClickListener {
                onCompleteClick(batch)
            }
        }
    }

    private class BatchDiffCallback : DiffUtil.ItemCallback<Batch>() {
        override fun areItemsTheSame(oldItem: Batch, newItem: Batch): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Batch, newItem: Batch): Boolean {
            return oldItem == newItem
        }
    }
}