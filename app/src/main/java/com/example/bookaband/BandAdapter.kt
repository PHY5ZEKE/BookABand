package com.example.bookaband

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookaband.BandData

class BandAdapter(private val bandList: List<BandData>, private val onItemClick: (BandData) -> Unit) :
    RecyclerView.Adapter<BandAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.band_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val band = bandList[position]

        // Load image using Glide (ensure to handle null imageURL)
        Glide.with(holder.itemView.context)
            .load(band.imageURL)
            .into(holder.itemImage)

        // Set other views' data
        holder.itemName.text = band.name
        holder.itemGenre.text = band.genre
        holder.itemPrice.text = "$${band.price}" // Formatting the price
    }

    override fun getItemCount(): Int {
        return bandList.size
    }

    // ViewHolder inner class
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImage: ImageView = itemView.findViewById(R.id.itemImage)
        val itemName: TextView = itemView.findViewById(R.id.itemName)
        val itemGenre: TextView = itemView.findViewById(R.id.itemGenre)
        val itemPrice: TextView = itemView.findViewById(R.id.itemPrice)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(bandList[position])
                }
            }
        }
    }
}
