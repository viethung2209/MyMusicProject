package com.hieubui00.musicplayer.util

import com.hunglee.mymusicproject.util.PRIVATE_KEY
import java.math.BigInteger
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.HashMap

object ApiHelper {
    private const val HMAC_SHA512 = "HmacSHA512"

    fun getCTime(): String = (Date().time / 1000).toString()

    fun getSig(api: String, param: MutableMap<String, String>): String {
        val n = buildMessage(param)
        val sha256 = getSHA256(n)
        return calculateHMAC(api + sha256)
    }

    private fun buildMessage(param: Map<String, String>): String {
        val treeMap = TreeMap(param)
        val newCer: MutableMap<String, String> = HashMap()
        for (key in treeMap.keys) {
            if ((key == "ctime" || key == "id") && treeMap[key] != null) {
                newCer[key] = treeMap[key]!!
            }
        }
        var query = ""
        for (key in newCer.keys) {
            query += "$key=" + URLEncoder.encode(
                newCer[key],
                StandardCharsets.UTF_8.toString()
            ) + ""
        }
        return query
    }

    private fun getSHA256(input: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val bytes = input.toByteArray(StandardCharsets.UTF_8)
        val byteArray = messageDigest.digest(bytes)
        val number = BigInteger(1, byteArray)
        val hexString = StringBuilder(number.toString(16))
        while (hexString.length < 32) {
            hexString.insert(0, '0')
        }
        return hexString.toString()
    }

    private fun calculateHMAC(data: String): String {
        val secretKeySpec = SecretKeySpec(PRIVATE_KEY.toByteArray(), HMAC_SHA512)
        val mac = Mac.getInstance(HMAC_SHA512)
        mac.init(secretKeySpec)
        val byteArray = mac.doFinal(data.toByteArray())
        val formatter = Formatter()
        byteArray.forEach {
            formatter.format("%02x", it)
        }
        return formatter.toString()
    }
}