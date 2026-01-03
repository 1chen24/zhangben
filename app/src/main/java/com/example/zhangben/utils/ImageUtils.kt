package com.example.zhangben.utils

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImageUtils {
    /**
     * 将 Bitmap 转换为 Base64 字符串
     * @param bitmap 原始图片
     * @return Base64 编码后的字符串
     */
    fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        // 百度 API 限制图片大小，这里压缩质量设为 80
        // 使用 JPEG 格式可以显著减小体积
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val bytes = outputStream.toByteArray()

        // NO_WRAP 参数非常重要：它会移除 Base64 中的换行符，
        // 否则传给服务器时可能会报“非法参数”错误。
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}