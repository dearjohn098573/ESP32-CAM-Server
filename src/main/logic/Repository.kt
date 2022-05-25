package main.logic

import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.imageio.ImageIO

object Repository {

    /**
     * 计算两个时间的时间差 (相差几秒几毫秒)
     * @param one 开始时间
     * @param two 结束时间
     * @return 时间差
     */
    fun getDistanceTime(one: Date, two: Date): String {
        val day: Long //天数差
        val hour: Long //小时数差
        val min: Long //分钟数差
        val second: Long //秒数差
        val diff: Long //毫秒差
        var result = ""
        try {
            val time1: Long = one.time
            val time2: Long = two.time
            diff = time2 - time1
            day = diff / (24 * 60 * 60 * 1000)
            hour = diff / (60 * 60 * 1000) - day * 24
            min = diff / (60 * 1000) - day * 24 * 60 - hour * 60
            second = diff / 1000
            println("day=" + day + " hour=" + hour + " min=" + min + " ss=" + second % 60 + " SSS=" + diff % 1000)
            result = (second % 60).toString() + "秒" + diff % 1000 + "毫秒"
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    /**
     * 给图片添加时间水印
     * @param srcImgPath 图片的存储路径
     * @param degree 水印旋转角度
     */
    fun addWatermark(srcImgPath: String, targetImgPath: String, degree: Int = 0) {
        val dNow = Date()
        val ft = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val time = ft.format(dNow)
        val inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            val srcImg: Image = ImageIO.read(File(srcImgPath))
            val buffImg = BufferedImage(srcImg.getWidth(null), srcImg.getHeight(null), BufferedImage.TYPE_INT_RGB)
            val g = buffImg.createGraphics()
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
            g.drawImage(
                srcImg.getScaledInstance(
                    srcImg.getWidth(null),
                    srcImg.getHeight(null), Image.SCALE_SMOOTH
                ), 0, 0, null
            )
            g.rotate(
                Math.toRadians(degree.toDouble()),
                buffImg.width.toDouble() / 2,
                buffImg.height.toDouble() / 2
            )
            g.color = Color.WHITE
            g.font = Font("宋体", Font.BOLD, 10)
            g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f)
            g.drawString(time, 222, 230)
            g.dispose()
            outputStream = FileOutputStream(targetImgPath)
            ImageIO.write(buffImg, "jpeg", outputStream)
            //println("图片完成添加水印文字")
        } catch (e: Exception) { e.printStackTrace() }
        finally {
            try { inputStream?.close() } catch (e: Exception) { e.printStackTrace() }
            try { outputStream?.close() } catch (e: Exception) { e.printStackTrace() }
        }
    }

    /**
     * 将字节数组转化为图片并存储
     * @param bytes 表示图片的字节数组
     * @param path 保存路径 默认为 "src/res/0A.jpeg"
     * @author Tongda
     */
    fun bytesToImageFile(bytes: ByteArray, path: String = "src/res/0A.jpeg") {
        try {
            val fos = FileOutputStream(File(path))
            fos.write(bytes, 0, bytes.size)
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}