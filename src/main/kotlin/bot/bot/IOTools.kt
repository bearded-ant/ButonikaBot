package bot.bot

import org.json.JSONObject
import java.io.IOException
import java.net.URL
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets


object IOTools {
    private const val BUFFER_SIZE = 65535
    private val DEFAULT_CHARSET: Charset = StandardCharsets.UTF_8

    fun readJsonFromUrl(url: String): JSONObject {
        val jsonObject = JSONObject(readFileFromUrl(url))
        return jsonObject.getJSONObject("result")
    }

    private fun readFileFromUrl(url: String): String {
        try {
            Channels.newChannel(URL(url).openStream()).use { channel ->
                val buff: ByteBuffer = ByteBuffer.allocate(BUFFER_SIZE)
                channel.read(buff)
                return ("${buff.array()} $DEFAULT_CHARSET")
            }
        } catch (e: RuntimeException) {
            throw IOException(String.format("Error reading resource '%s': %s", url, e.message))
        }
    }
}