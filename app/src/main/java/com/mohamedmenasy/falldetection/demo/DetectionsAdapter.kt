package com.mohamedmenasy.falldetection.demo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mohamedmenasy.falldetection.FallDetectionEvent
import kotlinx.android.synthetic.main.adapter_detection_item.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter class to display the detection data
 */
class DetectionsAdapter(private var detectionsData: MutableList<FallDetectionEvent>) :
    RecyclerView.Adapter<DetectionsAdapter.ViewHolder>() {
    var formatter = SimpleDateFormat("dd-M-yyyy hh:mm:ss", Locale.US)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_detection_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = detectionsData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(detectionsData[position])
    }

    /**
     * Update adapter's data using [DiffUtil]
     */
    fun setData(newData: MutableList<FallDetectionEvent>) {
        val diffCallback =
            RatingDiffCallback(
                detectionsData,
                newData
            )
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        detectionsData.clear()
        detectionsData.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(fallDetectionData: FallDetectionEvent) = with(itemView) {
            val date = formatter.format(fallDetectionData.date)
            dateTV.text = date
            val timeInSeconds = fallDetectionData.duration / 1000.0

            detectionDurationTV.text =
                String.format(
                    context.getString(R.string.duration_item),
                    timeInSeconds
                )
        }

    }

    /**
     * Callback class for [DiffUtil]
     */
    class RatingDiffCallback(
        private val oldList: List<FallDetectionEvent>,
        private val newList: List<FallDetectionEvent>
    ) : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition] == newList[newItemPosition]

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            newList[newItemPosition] == oldList[oldItemPosition]

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            val oldData = oldList[oldItemPosition]
            val newData = newList[newItemPosition]

            val diff = Bundle()

            if (oldData.date != newData.date) {
                diff.putLong("date", newData.date.time)
            }
            if (diff.size() == 0) {
                return null
            }
            return diff

        }
    }
}