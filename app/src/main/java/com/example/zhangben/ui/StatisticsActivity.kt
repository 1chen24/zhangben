package com.example.zhangben.ui

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.alibaba.excel.EasyExcel
import com.example.zhangben.R
import com.example.zhangben.data.AppDatabase
import com.example.zhangben.data.Bill
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import android.graphics.Color
import kotlin.concurrent.thread
import java.io.File

class StatisticsActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        val pieChart = findViewById<PieChart>(R.id.pieChart)
        val btnExport = findViewById<Button>(R.id.btnExport)

        thread {
            val db = AppDatabase.getInstance(this)
            val allBills = db.billDao().getAllBills()

            // 绘制饼图逻辑
            showPieChart(pieChart, allBills)

            runOnUiThread {
                btnExport.setOnClickListener {
                    exportToExcel(allBills)
                }
            }
        }
    }

    private fun showPieChart(pieChart: PieChart, list: List<Bill>) {
        val entries = list.groupBy { it.category }
            .map { PieEntry(it.value.sumOf { b -> b.amount }.toFloat(), it.key) }

        if (entries.isNotEmpty()) {
            val dataSet = PieDataSet(entries, "支出分布")
            // 手动指定颜色，避开 ColorTemplate 引用报错
            dataSet.colors = listOf(
                Color.parseColor("#FF8A80"),
                Color.parseColor("#82B1FF"),
                Color.parseColor("#B9F6CA")
            )

            runOnUiThread {
                pieChart.data = PieData(dataSet)
                pieChart.invalidate()
            }
        }
    }

    private fun exportToExcel(list: List<Bill>) {
        if (list.isEmpty()) {
            runOnUiThread { Toast.makeText(this, "暂无数据", Toast.LENGTH_SHORT).show() }
            return
        }

        thread {
            try {
                // 存储在 App 私有目录：/storage/emulated/0/Android/data/包名/files/
                val path = getExternalFilesDir(null)?.absolutePath + "/账单_${System.currentTimeMillis()}.xlsx"
                val file = File(path)

                EasyExcel.write(file, Bill::class.java)
                    .sheet("账单明细")
                    .doWrite(list)

                runOnUiThread {
                    Toast.makeText(this, "导出成功!\n路径: $path", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "导出失败: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}