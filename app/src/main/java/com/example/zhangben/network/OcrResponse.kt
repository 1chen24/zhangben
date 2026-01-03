package com.example.zhangben.network

// 定义返回结果的结构
data class OcrResponse(
    val words_result: List<WordItem>?,
    val words_result_num: Int,
    val log_id: Long
)
data class TokenResponse(
    val access_token: String, // 百度返回的是下划线格式
    val expires_in: Long
)
data class WordItem(
    val words: String // 识别出来的每一行文字
)