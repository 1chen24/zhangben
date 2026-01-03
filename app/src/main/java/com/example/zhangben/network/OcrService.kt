package com.example.zhangben.network

import retrofit2.Call
import retrofit2.http.*

interface OcrService {
    // 1. 获取 Access Token 的方法 (对应你报错的地方)
    @POST("oauth/2.0/token")
    fun getToken(
        @Query("grant_type") grantType: String = "client_credentials",
        @Query("client_id") apiKey: String,
        @Query("client_secret") secretKey: String
    ): Call<TokenResponse>

    // 2. 识别票据的方法
    @FormUrlEncoded
    @POST("rest/2.0/ocr/v1/receipt")
    fun recognizeReceipt(
        @Query("access_token") token: String,
        @Field("image") imageBase64: String
    ): Call<OcrResponse>
}