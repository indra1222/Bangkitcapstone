package com.example.sibantubaru.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.sibantubaru.R
import com.example.sibantubaru.adapter.OrderAdapter
import com.example.sibantubaru.model.Order
import com.example.sibantubaru.model.OrderStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class OrderFragment : Fragment() {
    private lateinit var ongoingOrderSection: LinearLayout
    private lateinit var tvStatus: TextView
    private lateinit var tvStatusDescription: TextView
    private lateinit var tvServiceProviderName: TextView
    private lateinit var tvServiceType: TextView
    private lateinit var tvStatusBadge: TextView
    private lateinit var btnChangeStatus: Button
    private lateinit var rvCompletedOrders: RecyclerView
    private lateinit var orderAdapter: OrderAdapter

    private val orders = mutableListOf<Order>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order, container, false)

        ongoingOrderSection = view.findViewById(R.id.ongoing_order_section)
        tvStatus = view.findViewById(R.id.tv_status)
        tvStatusDescription = view.findViewById(R.id.tv_status_description)
        tvServiceProviderName = view.findViewById(R.id.tv_service_provider_name)
        tvServiceType = view.findViewById(R.id.tv_service_type)
        tvStatusBadge = view.findViewById(R.id.tv_status_badge)
        btnChangeStatus = view.findViewById(R.id.btn_change_status)
        rvCompletedOrders = view.findViewById(R.id.rv_completed_orders)

        orderAdapter = OrderAdapter(orders)
        rvCompletedOrders.adapter = orderAdapter
        btnChangeStatus.setOnClickListener {
            changeOrderStatus()
        }
        loadOrders()
        return view
    }

    private fun loadOrders() {
        val sharedPreferences = requireActivity().getSharedPreferences("OrderData", Context.MODE_PRIVATE)
        val ongoingOrderJson = sharedPreferences.getString("ongoing_order", null)
        if (ongoingOrderJson != null) {
            val ongoingOrder = Gson().fromJson(ongoingOrderJson, Order::class.java)
            displayOngoingOrder(ongoingOrder)
        } else {
            ongoingOrderSection.visibility = View.GONE
        }

        val completedOrdersJson = sharedPreferences.getString("completed_orders", null)
        if (completedOrdersJson != null) {
            val type = object : TypeToken<List<Order>>() {}.type
            val completedOrders = Gson().fromJson<List<Order>>(completedOrdersJson, type) ?: listOf()
            orders.clear()
            orders.addAll(completedOrders)
            orderAdapter.updateOrders(orders)
        }
    }

    private fun displayOngoingOrder(order: Order) {
        ongoingOrderSection.visibility = View.VISIBLE
        tvServiceProviderName.text = order.serviceProviderName
        tvServiceType.text = order.serviceType
        tvStatus.text = "On Going"
        tvStatusDescription.text = "Your Service is On Progress"
        tvStatusBadge.text = "Active"
    }

    private fun changeOrderStatus() {
        val sharedPreferences = requireActivity().getSharedPreferences("OrderData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val ongoingOrderJson = sharedPreferences.getString("ongoing_order", null)
        if (ongoingOrderJson != null) {
            val ongoingOrder = Gson().fromJson(ongoingOrderJson, Order::class.java)

            val completedOrder = ongoingOrder.copy(
                status = OrderStatus.COMPLETED,
                completedAt = Date()
            )

            editor.remove("ongoing_order")
            val completedOrdersJson = sharedPreferences.getString("completed_orders", null)
            val completedOrders = if (completedOrdersJson != null) {
                val type = object : TypeToken<MutableList<Order>>() {}.type
                Gson().fromJson<MutableList<Order>>(completedOrdersJson, type) ?: mutableListOf()
            } else {
                mutableListOf()
            }

            completedOrders.add(0, completedOrder)
            editor.putString("completed_orders", Gson().toJson(completedOrders))
            editor.apply()
            loadOrders()
        }
    }
}