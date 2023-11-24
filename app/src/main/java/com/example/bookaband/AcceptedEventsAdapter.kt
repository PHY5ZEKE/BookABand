package com.example.bookaband


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.cardview.widget.CardView
import android.widget.ImageView
import java.io.Serializable

class AcceptedEventsAdapter(
    private val eventList: List<AcceptedEvents>,
    private val onItemClick: (AcceptedEvents) -> Unit
) : RecyclerView.Adapter<AcceptedEventsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.eventCardView)
        val eventNameTextView: TextView = itemView.findViewById(R.id.eventNameTextView)
        val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(eventList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.event_recycler, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = eventList[position]

        holder.eventNameTextView.text = currentItem.eventName
        holder.locationTextView.text = currentItem.location
        holder.dateTextView.text = currentItem.date
        holder.timeTextView.text = currentItem.time
    }

    override fun getItemCount(): Int {
        return eventList.size
    }
}
