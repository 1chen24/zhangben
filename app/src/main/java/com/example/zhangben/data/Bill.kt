package com.example.zhangben.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alibaba.excel.annotation.ExcelProperty
import com.alibaba.excel.annotation.write.style.ColumnWidth

@Entity(tableName = "bills")
data class Bill(
    @PrimaryKey(autoGenerate = true)
    @ExcelProperty("编号")
    val id: Int = 0,

    @ExcelProperty("金额")
    val amount: Double = 0.0,

    @ExcelProperty("分类")
    val category: String = "",

    @ExcelProperty("类型") // 新增：支出或收入
    val type: String = "支出",

    @ExcelProperty("备注")
    val note: String = "",

    @ExcelProperty("日期")
    val date: String = ""
)