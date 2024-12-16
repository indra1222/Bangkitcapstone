package com.example.sibantubaru.adapter

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sibantubaru.R
import com.example.sibantubaru.model.Order
import com.example.sibantubaru.model.OrderStatus
import java.text.SimpleDateFormat
import java.util.Locale

class OrderAdapter(
    private var orders: List<Order>
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvOrderTitle: TextView = itemView.findViewById(R.id.tv_order_title)
        private val tvServiceProvider: TextView = itemView.findViewById(R.id.tv_service_provider)
        private val tvOrderDate: TextView = itemView.findViewById(R.id.tv_order_date)
        private val tvOrderStatus: TextView = itemView.findViewById(R.id.tv_order_status)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.rating_service_provider)
        private val btnOrderAgain: Button = itemView.findViewById(R.id.btn_order_again)

        fun bind(order: Order) {
            tvOrderTitle.text = order.serviceType
            tvServiceProvider.text = order.serviceProviderName

            // Format date
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
            tvOrderDate.text = dateFormat.format(order.createdAt)

            tvOrderStatus.text = when (order.status) {
                OrderStatus.COMPLETED -> "Done"
                OrderStatus.ONGOING -> "On Going"
                OrderStatus.CANCELLED -> "Canceled"
            }

            ratingBar.rating = order.rating

            // Directly open WhatsApp when "Order Again" is clicked
            btnOrderAgain.setOnClickListener {
                val context = itemView.context
                val whatsappNumber = order.whatsappNumber

                // Check if WhatsApp is installed
                if (isWhatsappInstalled(context)) {
                    // Create an Intent to open WhatsApp chat
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://wa.me/$whatsappNumber")
                    }
                    context.startActivity(intent)
                } else {
                    // If WhatsApp is not installed, open WhatsApp Web
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://wa.me/$whatsappNumber")
                    }
                    context.startActivity(intent)
                }
            }
        }

        // Check if WhatsApp is installed
        private fun isWhatsappInstalled(context: Context): Boolean {
            val pm = context.packageManager
            return try {
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_card, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    // Update orders when new data is available
    fun updateOrders(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}
