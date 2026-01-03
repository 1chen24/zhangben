package com.example.zhangben.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OcrClient {
    private const val BASE_URL = "https://aip.baidubce.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()) // 自动将 JSON 转为实体类
        .build()

    val service: OcrService = retrofit.create(OcrService::class.java)
}