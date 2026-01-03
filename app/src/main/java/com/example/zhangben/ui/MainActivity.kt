package com.example.zhangben.ui
import com.example.zhangben.network.TokenManager
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zhangben.R
import com.example.zhangben.data.AppDatabase
import com.example.zhangben.ui.adapter.BillAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.concurrent.thread

class MainActivity : Activity() {
    private lateinit var adapter: BillAdapter
    private lateinit var tvTotal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 🚀 修复点 1：千万不能在主线程初始化 Token，必须开启线程
        thread {
            try {
                TokenManager.initToken()
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Token初始化失败: ${e.message}")
            }
        }

        tvTotal = findViewById(R.id.tvTotalExpense)
        val rv = findViewById<RecyclerView>(R.id.rvBills)

        // 🚀 修复点 2：增加空判断，防止布局加载失败导致的崩溃
        rv?.layoutManager = LinearLayoutManager(this)
        adapter = BillAdapter(emptyList())
        rv?.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fabAdd)?.setOnClickListener {
            startActivity(Intent(this, AddBillActivity::class.java))
        }

        findViewById<android.widget.LinearLayout>(R.id.headerCard)?.setOnClickListener {
            startActivity(Intent(this, StatisticsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun refreshData() {
        thread {
            try {
                val db = AppDatabase.getInstance(this)
                val list = db.billDao().getAllBills()

                // 关键修复：处理 null 值，如果没数据则默认为 0.0
                val total = db.billDao().getTotalExpense() ?: 0.0

                runOnUiThread {
                    adapter.updateData(list)
                    // 确保数据安全显示
                    tvTotal.text = "¥ ${String.format("%.2f", total)}"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // 如果还是崩，至少能看到原因
                android.util.Log.e("MainActivity", "刷新失败: ${e.message}")
            }
        }
    }
}