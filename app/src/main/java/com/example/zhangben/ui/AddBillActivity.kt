package com.example.zhangben.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import com.example.zhangben.R
import com.example.zhangben.data.AppDatabase
import com.example.zhangben.data.Bill
import com.example.zhangben.network.OcrClient
import com.example.zhangben.network.OcrResponse
import com.example.zhangben.utils.ImageUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat // 必须导入
import java.util.* // 必须导入
import kotlin.concurrent.thread

class AddBillActivity : Activity() {
    private lateinit var etAmount: EditText
    private lateinit var spinner: Spinner

    private fun showToast(message: String) {
        runOnUiThread { Toast.makeText(this, message, Toast.LENGTH_SHORT).show() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_bill)

        etAmount = findViewById(R.id.etAmount)
        spinner = findViewById(R.id.spinnerCategory)

        findViewById<Button>(R.id.btnScan).setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 101)
        }

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            saveToDb()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 101 && resultCode == RESULT_OK) {
            // 1. 安全地获取数据，避免直接强转导致的闪退
            val extras = data?.extras
            val bitmap = extras?.get("data") as? Bitmap // 使用 as? 安全强转

            if (bitmap != null) {
                try {
                    val base64 = ImageUtils.bitmapToBase64(bitmap)
                    requestOcr(base64)
                } catch (e: Exception) {
                    showToast("图片处理失败")
                }
            } else {
                // 2. 如果 bitmap 为空，通常是因为 Intent 没有返回缩略图
                showToast("拍照结果为空，请重试")
            }
        }
    }

    private fun requestOcr(base64: String) {
        val token = com.example.zhangben.network.TokenManager.cachedToken
        if (token.isNullOrEmpty()) {
            showToast("正在获取授权，请稍后再试")
            return
        }

        OcrClient.service.recognizeReceipt(token, base64).enqueue(object : Callback<OcrResponse> {
            override fun onResponse(call: Call<OcrResponse>, response: Response<OcrResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()?.words_result
                    if (result.isNullOrEmpty()) {
                        showToast("图片太模糊，请重新拍摄")
                        return
                    }

                    // 1. 预处理：将容易识别错的字母“强制转换”回数字
                    // 比如：5O.OO -> 50.00, S1.2 -> 51.2
                    val fullText = result.joinToString(" ") { it.words }
                        .uppercase()
                        .replace("O", "0")
                        .replace("S", "5")
                        .replace("I", "1")
                        .replace("L", "1")
                        .replace("B", "8")
                        .replace("G", "6")

                    android.util.Log.d("OCR_CLEANED", "纠错后文本: $fullText")

                    // 2. 改进正则：
                    // (?:^|\s) 表示数字前要么是开头要么是空格（防止抓到日期 2026-01-01 里的 01）
                    // \d+\.\d{2} 优先匹配两位小数（收据最常见格式）
                    // \d+\.\d+| \d+ 匹配普通小数或整数
                    val regex = "(?:^|\\s)([0-9]+\\.[0-9]{1,2}|[0-9]+)".toRegex()

                    // 3. 提取并过滤
                    val matches = regex.findAll(fullText)
                        .map { it.groupValues[1].toDoubleOrNull() } // 提取第一个捕获组
                        .filterNotNull()
                        .filter { it > 0.1 && it < 10000 } // 过滤掉过小或过大的无关数字
                        .toList()

                    runOnUiThread {
                        if (matches.isNotEmpty()) {
                            // 4. 核心策略：寻找最像总额的数字
                            // 如果有带 2 位小数的数字，优先考虑；否则取最大值
                            val finalAmount = matches.find { it * 100 % 1 == 0.0 } ?: matches.maxOrNull() ?: 0.0

                            etAmount.setText(String.format("%.2f", finalAmount))
                            showToast("识别成功，请核对")
                        } else {
                            showToast("未发现金额，请手动输入")
                        }
                    }
                }
            }
            override fun onFailure(call: Call<OcrResponse>, t: Throwable) {
                showToast("网络连接失败")
            }
        })
    }

    private fun saveToDb() {
        val amountStr = etAmount.text.toString()
        if (amountStr.isEmpty()) {
            showToast("请输入金额")
            return
        }
        val amount = amountStr.toDoubleOrNull() ?: 0.0

        thread {
            // ✅ 修复：将 Long 时间戳转为 String，解决 Argument type mismatch
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val dateString = sdf.format(Date())

            val bill = Bill(
                amount = amount,
                category = spinner.selectedItem.toString(),
                type = "支出",
                date = dateString
            )

            // ✅ 确保 billDao() 里的方法名与你 BillDao.kt 定义的一致（建议用 insert）
            AppDatabase.getInstance(this).billDao().insert(bill)

            runOnUiThread {
                showToast("保存成功")
                finish()
            }
        }
    }
}