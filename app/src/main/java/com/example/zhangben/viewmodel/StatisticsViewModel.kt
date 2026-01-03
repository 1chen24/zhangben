package com.example.zhangben.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zhangben.network.OcrClient
import com.example.zhangben.network.OcrResponse
import com.example.zhangben.network.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddBillViewModel : ViewModel() {

    // 观察识别出来的金额
    val recognizedAmount = MutableLiveData<String>()
    // 观察识别状态（加载中、成功、失败）
    val ocrStatus = MutableLiveData<String>()

    /**
     * 调用网络接口识别图片
     */
    fun recognizeImage(base64: String) {
        val token = TokenManager.cachedToken
        if (token.isNullOrEmpty()) {
            ocrStatus.value = "授权失效，请重新启动应用"
            return
        }

        ocrStatus.value = "正在识别中..."

        OcrClient.service.recognizeReceipt(token, base64).enqueue(object : Callback<OcrResponse> {
            override fun onResponse(call: Call<OcrResponse>, response: Response<OcrResponse>) {
                if (response.isSuccessful) {
                    val fullText = response.body()?.words_result?.joinToString { it.words } ?: ""
                    // 正则提取金额
                    val regex = "(\\d+\\.\\d+)".toRegex()
                    val match = regex.find(fullText)

                    if (match != null) {
                        recognizedAmount.value = match.value
                        ocrStatus.value = "识别成功"
                    } else {
                        ocrStatus.value = "未发现金额数字"
                    }
                }
            }

            override fun onFailure(call: Call<OcrResponse>, t: Throwable) {
                ocrStatus.value = "网络请求失败"
            }
        })
    }
}