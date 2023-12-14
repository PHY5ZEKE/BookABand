package com.example.bookaband

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.cardview.widget.CardView
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.io.Serializable

class BookingAdapter2(
    private val bookingList: List<BookingData>,
    private val onItemClick: (BookingData) -> Unit
) : RecyclerView.Adapter<BookingAdapter2.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.bookingCardView)
        val eventNameTextView: TextView = itemView.findViewById(R.id.eventNameTextView)
        val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val bandTextView: TextView = itemView.findViewById(R.id.nameComission)
        val bandImageView: ImageView = itemView.findViewById(R.id.comissioner)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(bookingList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.booking_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = bookingList[position]

        holder.eventNameTextView.text = currentItem.eventName
        holder.locationTextView.text = currentItem.location
        holder.dateTextView.text = currentItem.date
        holder.timeTextView.text = currentItem.time
        holder.bandTextView.text = currentItem.bandName
        Glide.with(holder.itemView.context)
            .load(currentItem.bandImage)
            .into(holder.bandImageView)
    }

    override fun getItemCount(): Int {
        return bookingList.size
    }
}
