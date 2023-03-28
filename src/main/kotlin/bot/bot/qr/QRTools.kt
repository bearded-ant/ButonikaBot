package bot.bot.qr

import com.google.zxing.*
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeWriter
import org.apache.logging.log4j.kotlin.Logging
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.nio.file.FileSystems
import java.util.*
import javax.imageio.ImageIO


private const val FILE_FORMAT = "png"
const val CHARSET = "UTF-8"

class QRTools : Logging {

    private val DEFAULT_VERSION: QRSize = QRSize.MEDIUM
    private val qrSizes: HashMap<QRSize, Int>? = null

    fun getTextFromQR(url: String): String {
        val result: Result = try {
            decodeBitmap(getBitmapFromUrl(url))
        } catch (e: RuntimeException) {
            logger.debug(String.format("decodeQR: %s", e.message))
            throw IOException(java.lang.String.format("Unable to decrypt QR-code: %s", e.message))
        } catch (e: MalformedURLException) {
            logger.debug(String.format("decodeQR: %s", e.message))
            throw IOException(String.format("Unable to decrypt QR-code: %s", e.message))
        }
        return result.text
    }

    fun encodeText(text: String?, width: Int, height: Int): String? {
        val qrCodeWriter = QRCodeWriter()
        val hashtable = mutableMapOf<EncodeHintType, String>()
        hashtable[EncodeHintType.CHARACTER_SET] = CHARSET
        val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hashtable)
        val path =
            FileSystems.getDefault().getPath(java.lang.String.format("./images/%s.%s", UUID.randomUUID(), FILE_FORMAT))
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path)
        return path.toAbsolutePath().toString()
    }

    fun encodeText(text: String?, qrSize: QRSize?): String? {
        val size = getQRSize(qrSize!!)
        return encodeText(text, size, size)
    }

    fun encodeText(text: String?): String? {
        return encodeText(text, DEFAULT_VERSION)
    }

    init {
        qrSizes?.put(QRSize.SMALL, 256)
        qrSizes?.put(QRSize.MEDIUM, 512)
        qrSizes?.put(QRSize.LARGE, 1024)
    }

    private fun getQRSize(qrSize: QRSize): Int = qrSizes!![qrSize]!!

    private fun getBitmapFromUrl(url: String): BinaryBitmap {
        val binaryBitmap: BinaryBitmap
        try {
            binaryBitmap = BinaryBitmap(
                HybridBinarizer(
                    BufferedImageLuminanceSource(ImageIO.read(URL(url)))
                )
            )
        } catch (e: IOException) {
            logger.error(String.format("{QRTools.getBitmapFromUrl}: %s", e.message))
            throw IOException(String.format("Unable to decrypt QR-code: %s", e.message))
        }
        return binaryBitmap
    }

    private fun decodeBitmap(binaryBitmap: BinaryBitmap): Result {
        val result: Result = try {
            MultiFormatReader().decode(binaryBitmap)
        } catch (e: NotFoundException) {
            throw IllegalArgumentException(
                java.lang.String.format(
                    "Image does not contain QR-code: %s",
                    e.message
                )
            )
        }
        return result
    }
}