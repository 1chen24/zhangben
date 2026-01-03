package com.example.zhangben.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BillDao {
    // 获取所有账单，按时间倒序排列（最新的在前面）

        @Insert
        fun insert(bill: Bill) // 确保名字叫 insert，参数是 Bill 类型

        @Query("SELECT * FROM bills ORDER BY id DESC")
        fun getAllBills(): List<Bill>


    // 删除账单
    @Delete
    fun deleteBill(bill: Bill)

    // 统计总支出（用于主页显示）
    @Query("SELECT SUM(amount) FROM bills WHERE type = '支出'")
    fun getTotalExpense(): Double?

    // 根据分类查询（用于可视化图表分析）
    @Query("SELECT * FROM bills WHERE category = :cat")
    fun getBillsByCategory(cat: String): List<Bill>
}