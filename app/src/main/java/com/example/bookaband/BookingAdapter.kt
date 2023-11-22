package com.example.bookaband

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.cardview.widget.CardView // Import CardView

class BookingAdapter(private val bookingList: List<BookingData>) : RecyclerView.Adapter<BookingAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.bookingCardView)
        val eventNameTextView: TextView = itemView.findViewById(R.id.eventNameTextView)
        val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.booking_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = bookingList[position]

        holder.eventNameTextView.text = currentItem.eventName
        holder.locationTextView.text = currentItem.location
        holder.dateTextView.text = currentItem.date
        holder.timeTextView.text = currentItem.time
    }

    override fun getItemCount(): Int {
        return bookingList.size
    }
}