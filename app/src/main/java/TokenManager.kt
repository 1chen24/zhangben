package com.example.zhangben.network

import retrofit2.Call      // 必须是 retrofit2 的 Call
import retrofit2.Callback  // 必须是 retrofit2 的 Callback
import retrofit2.Response

object TokenManager {
    // 这是你刚才申请的 Key，没问题
   private const val API_KEY = "0af4FfHY4jvKkf5iUgiWnAAG"
   private const val SECRET_KEY = "J2oeSWhGHN0aM0GtAbPAWDyaIQYTbmse"

   var cachedToken: String? = null

   fun initToken() {
        // 调用接口获取 Token
        OcrClient.service.getToken(apiKey = API_KEY, secretKey = SECRET_KEY)
            .enqueue(object : Callback<TokenResponse> { // 必须指明泛型 <TokenResponse>

                // 确保参数签名完全一致：(Call<TokenResponse>, Response<TokenResponse>)
                override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                    if (response.isSuccessful) {
                        cachedToken = response.body()?.access_token
                    }
                }

                // 必须实现 onFailure，否则匿名内部类会报“not abstract”错误
                override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                    t.printStackTrace() // 打印错误日志，方便调试
                }
            })
    }
}