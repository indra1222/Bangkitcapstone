package com.example.sibantubaru.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sibantubaru.R
import com.example.sibantubaru.model.ServiceProvider

class ServiceProviderAdapter(
    private var serviceProviderList: List<ServiceProvider>,
    private val onItemClickListener: (ServiceProvider) -> Unit
) : RecyclerView.Adapter<ServiceProviderAdapter.ServiceProviderViewHolder>() {

    class ServiceProviderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageProvider: ImageView = itemView.findViewById(R.id.img_service_provider)
        val providerName: TextView = itemView.findViewById(R.id.tv_service_provider_name)
        val serviceType: TextView = itemView.findViewById(R.id.tv_service_type)
        val providerAddress: TextView = itemView.findViewById(R.id.tv_service_address)
        val ratingBar: RatingBar = itemView.findViewById(R.id.rating_service_provider)
        val ratingText: TextView = itemView.findViewById(R.id.tv_rating_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceProviderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service_provider, parent, false)
        return ServiceProviderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceProviderViewHolder, position: Int) {
        val serviceProvider = serviceProviderList[position]

        holder.providerName.text = serviceProvider.name
        holder.serviceType.text = serviceProvider.serviceType
        holder.providerAddress.text = serviceProvider.address
        holder.ratingBar.rating = serviceProvider.rating
        holder.ratingText.text = String.format("(%.1f)", serviceProvider.rating)

        // Load image into ImageView using Glide
        Glide.with(holder.itemView.context)
            .load(serviceProvider.profileImageUrl) // Assuming imageUrl is the link to the image
            .into(holder.imageProvider)

        // Set click listener
        holder.itemView.setOnClickListener {
            onItemClickListener(serviceProvider)
        }
    }

    override fun getItemCount() = serviceProviderList.size

    fun updateList(newList: List<ServiceProvider>) {
        serviceProviderList = newList
        notifyDataSetChanged()
    }
}
