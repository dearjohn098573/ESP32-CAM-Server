package main

import main.logic.Repository.storeImage
import main.logic.Repository.clear
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.SocketTimeoutException
import java.net.URL

class Server {

    init {
        downloadFile("192.168.137.15")
    }

    //  视频连接
    private fun downloadFile(ip: String) {

        Thread{ clear() }.start()

        val downloadUrl = "http://$ip:80/stream"

        var bufferedInputStream: BufferedInputStream? = null
        val outputStream: FileOutputStream? = null
        try {
            val url = URL(downloadUrl)
            var reConnect = 1
            while (true) {
                try {
                    println("Info: 正在尝试连接($reConnect/10)...")
                    val httpURLConnection = url.openConnection() as HttpURLConnection
                    httpURLConnection.requestMethod = "GET"
                    httpURLConnection.connectTimeout = 1000 * 5
                    httpURLConnection.readTimeout = 1000
                    httpURLConnection.doInput = true
                    httpURLConnection.connect()
                    if (httpURLConnection.responseCode == 200) {

                        println("Info: 连接成功, 数据开始传输")

                        val header = "CONTENT-TYPE: IMAGE/JPEG\r\nCONTENT-LENGTH: "
                        //val ender = "\r\n||||||||||||||||||||||||||||||||||||||||\r\n:"
                        val inputStream = httpURLConnection.inputStream
                        var buffer: ByteArray
                        bufferedInputStream = BufferedInputStream(inputStream)
                        val headerBytes = ByteArray(50)
                        var headerString = ""

                        var numberOfImages = 0
                        while (true) {
                            try {
                                //  寻找文件头
                                if (numberOfImages != 0) {
                                    val readOne = ByteArray(1)
                                    while (true) {
                                        bufferedInputStream.read(readOne)
                                        if (readOne[0].toInt().toChar() == ':') break
                                    }
                                }

                                //  读取并校验文件头
                                bufferedInputStream.read(headerBytes)
                                for (i in headerBytes) {
                                    headerString += i.toInt().toChar()
                                }
                                //println("文件头是: ${headerString.substring(0, 46)}")
                                if (headerString.substring(0, 42) == header) {
                                    //println("文件头校验正确")
                                }
                                else {
                                    //println("文件头校验失败")
                                    continue
                                }

                                //  获取图片大小
                                var imageSize = headerString.substring(42, 46).toInt()
                                if (imageSize < 2000) {
                                    val readOne = ByteArray(1)
                                    bufferedInputStream.read(readOne)
                                    val num = headerString.substring(46, 47).toInt()
                                    //println("最后一位是: $num")
                                    imageSize = imageSize * 10 + num
                                }
                                headerString = ""
                                //println("图像大小是: $imageSize")

                                //  读取图片
                                var t = 0
                                buffer = ByteArray(imageSize)
                                while (t < imageSize) t += bufferedInputStream.read(buffer, t, imageSize-t)

                                /**
                                var str = ""
                                for (i in buffer) str += "$i,"
                                println(str + "\r\n" + str.length.toString())
                                 */

                                //bytesToImageFile(buffer)
                                /**
                                 * 此处要对图片进行旋转
                                 */
                                //addWatermark()

                                //SaveImage().saveImage(buffer)
                                storeImage(buffer)

                                numberOfImages++

                                //println("已成功接收 $numberOfImages 张图像")
                            } catch (e: SocketTimeoutException) {
                                println("Warning: 流读取超时: " + e.message + " 连接已断开！")
                                break
                            }
                        }
                        reConnect = 0
                        Thread.sleep(1000)
                    }
                } catch (e: SocketTimeoutException) { println("Warning: 连接超时: " + e.message) }
                if (++reConnect == 11) break
                Thread.sleep(5000)
            }
            println("Info: 请检查 ESP32-CAM !")

        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } finally {
            try {
                bufferedInputStream?.close()
                outputStream?.close()
            } catch (e: IOException) { println("Warning: 流关闭失败: " + e.message) }
            println("Info: 流程结束")
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Server()
        }
    }
}