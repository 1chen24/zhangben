package com.example.zhangben.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zhangben.R
import com.example.zhangben.data.Bill

// 注意：这里的 <BillAdapter.ViewHolder> 必须写全，代表引用内部定义的类
class BillAdapter(private var list: List<Bill>) : RecyclerView.Adapter<BillAdapter.ViewHolder>() {

    // --- 关键点：ViewHolder 类必须定义在 BillAdapter 的内部 ---
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // 这里的 ID 必须对应你刚才发给我的 XML 里的 android:id
        val tvCategory: TextView = view.findViewById(R.id.tvBillCategory) // 修正
        val tvAmount: TextView = view.findViewById(R.id.tvBillAmount)     // 修正
        val tvDate: TextView = view.findViewById(R.id.tvBillDate)         // 修正
        val tvIcon: TextView = view.findViewById(R.id.tvCategoryIcon)     // 新增
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bill, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bill = list[position]

        // 必须通过 holder. 访问控件
        holder.tvCategory.text = bill.category
        holder.tvDate.text = bill.date

        // 格式化金额：保留两位小数
        try {
            holder.tvAmount.text = "¥${String.format("%.2f", bill.amount)}"
        } catch (e: Exception) {
            holder.tvAmount.text = "¥${bill.amount}"
        }
    }

    override fun getItemCount(): Int = list.size

    // 刷新数据的方法
    fun updateData(newList: List<Bill>) {
        this.list = newList
        notifyDataSetChanged()
    }
}