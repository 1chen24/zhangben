package com.example.zhangben.utils

import android.content.Context
import com.alibaba.excel.EasyExcel
import com.example.zhangben.data.Bill
import java.io.File

object ExcelUtils {
    /**
     * 导出账单数据到 Excel
     * @param context 上下文
     * @param billList 数据库查出来的账单列表
     * @return 生成文件的绝对路径
     */
    fun exportBillsToExcel(context: Context, billList: List<Bill>): String {
        // 定义存储路径：/storage/emulated/0/Android/data/包名/files/Documents/
        val folder = context.getExternalFilesDir("Documents")
        if (folder?.exists() == false) folder.mkdirs()

        val fileName = "账单导出_${System.currentTimeMillis()}.xlsx"
        val file = File(folder, fileName)

        // 执行写入操作
        EasyExcel.write(file.absolutePath, Bill::class.java)
            .sheet("账单明细")
            .doWrite(billList)

        return file.absolutePath
    }
}