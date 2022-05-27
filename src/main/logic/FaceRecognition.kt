package main.logic

import com.arcsoft.face.*
import com.arcsoft.face.enums.DetectMode
import com.arcsoft.face.enums.DetectOrient
import com.arcsoft.face.enums.ErrorInfo
import com.arcsoft.face.toolkit.ImageFactory
import java.io.File

class FaceRecognition {

    private val face = "C:\\Users\\HongMoying\\Desktop\\000.jpeg"
    private val appId = "JAZFE81wXFg3z9KgbzPdr1k1Y2WvbbNxscSfdapQa9WP"
    private val sdkKey = "5LV6DeGkdkMapbX7viKF9BkEtggm3KjNZiuJhk8DjyHm"

    init {

        val currentTime = System.currentTimeMillis()

        val faceEngine = engineInitialization()

        createWhiteLibrary(faceEngine)

        val targetFaceFeatureData = getMyFeatureData(faceEngine, face)
        val result = compare(faceEngine, targetFaceFeatureData, 0.78)
        for (i in result) println("识别结果: $i")

        val endTime = System.currentTimeMillis()
        val difference = endTime - currentTime
        println("运行时间: $difference")
    }

    /**
     * 初始化脸部引擎
     * @return 脸部引擎
     */
    private fun engineInitialization(): FaceEngine {

        val faceEngine = FaceEngine("C:\\Users\\HongMoying\\IdeaProjects\\Face Recognition\\libs\\WIN64")

        //激活引擎
        var errorCode = faceEngine.activeOnline(appId, sdkKey)

        if (errorCode != ErrorInfo.MOK.value
            && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.value
        ) {
            println("引擎激活失败")
        }

        val activeFileInfo = ActiveFileInfo()
        errorCode = faceEngine.getActiveFileInfo(activeFileInfo)
        if (errorCode != ErrorInfo.MOK.value && errorCode
            != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.value
        ) {
            println("获取激活文件信息失败")
        }

        //引擎配置
        val engineConfiguration = EngineConfiguration()
        engineConfiguration.detectMode = DetectMode.ASF_DETECT_MODE_IMAGE
        engineConfiguration.detectFaceOrientPriority = DetectOrient.ASF_OP_ALL_OUT
        engineConfiguration.detectFaceMaxNum = 10
        engineConfiguration.detectFaceScaleVal = 16

        //功能配置
        val functionConfiguration = FunctionConfiguration()
        functionConfiguration.isSupportAge = true
        functionConfiguration.isSupportFace3dAngle = true
        functionConfiguration.isSupportFaceDetect = true
        functionConfiguration.isSupportFaceRecognition = true
        functionConfiguration.isSupportGender = true
        functionConfiguration.isSupportLiveness = true
        functionConfiguration.isSupportIRLiveness = true
        engineConfiguration.functionConfiguration = functionConfiguration

        //初始化引擎
        errorCode = faceEngine.init(engineConfiguration)

        if (errorCode != ErrorInfo.MOK.value) {
            println("初始化引擎失败")
        }

        return faceEngine
    }

    /**
     * @param faceEngine 脸部引擎
     */
    fun createWhiteLibrary(faceEngine: FaceEngine) {

        File("src/res/library/white_library.txt").writeText("")

        val fileNames: MutableList<String> = mutableListOf()

        val fileTree: FileTreeWalk = File("src/res/faceRepository/").walk()
        fileTree.maxDepth(1) //需遍历的目录层级为1，即无需检查子目录
            .filter { it.isFile } //只挑选文件，不处理文件夹
            .filter { it.extension in listOf("jpeg") } //选择扩展名为png和jpg的图片文件
            .forEach { fileNames.add(it.name) } //循环处理符合条件的文件

        for (i in fileNames) {
            var featureData = ""
            for (n in getMyFeatureData(faceEngine, "src/res/faceRepository/$i")["0"]!!) {
                featureData += "$n;"
            }
            File("src/res/library/white_library.txt").appendText("${i.substring(0, i.length - 5)};" + featureData + "\n")
        }
    }

    /**
     * @param faceEngine 脸部引擎
     * @param path_0 图像存放路径
     * @return faceFeatureData: byte[1032] 即字节数组形式的脸部特征值
     */
    private fun getMyFeatureData(faceEngine: FaceEngine, path_0: String): Map<String, ByteArray> {

        //人脸检测
        val imageInfo = ImageFactory.getRGBData(File(path_0))
        val faceInfoList: List<FaceInfo> = ArrayList()
        faceEngine.detectFaces(
            imageInfo.imageData,
            imageInfo.width,
            imageInfo.height,
            imageInfo.imageFormat,
            faceInfoList
        )
        //println(faceInfoList)

        val resultMap = HashMap<String, ByteArray>()

        for (i in faceInfoList.indices) {
            //特征提取
            val faceFeature = FaceFeature()
            faceEngine.extractFaceFeature(
                imageInfo.imageData,
                imageInfo.width,
                imageInfo.height,
                imageInfo.imageFormat,
                faceInfoList[i],
                faceFeature
            )
            //println("特征值大小：" + faceFeature.featureData.size)
            resultMap["$i"] = faceFeature.featureData
        }

        return resultMap
    }

    /**
     * @param faceEngine 脸部引擎
     * @param targetFaceFeatureDataMap 目标脸部特征信息表
     * @param threshold 相似度阈值
     * @return 判定结果: "未知" 或 "$人名"
     */
    private fun compare(faceEngine: FaceEngine, targetFaceFeatureDataMap: Map<String, ByteArray>, threshold: Double): Array<String> {
        val resultArray = Array(targetFaceFeatureDataMap.size) {""}

        for (j in 0 until  targetFaceFeatureDataMap.size) {
            for (i in File("src/res/library/white_library.txt").readLines() ) {
                val list = i.split(";").toMutableList()
                val name = list[0]
                list -= list[0]
                val sourceFeatureDate = stringListToByteArray(list)

                //特征比对
                val targetFaceFeature = FaceFeature()
                targetFaceFeature.featureData = targetFaceFeatureDataMap["$j"]
                val sourceFaceFeature = FaceFeature()
                sourceFaceFeature.featureData = sourceFeatureDate

                val faceSimilar = FaceSimilar()
                faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, faceSimilar)

                println("与${name}的相似度: " + faceSimilar.score)

                if (faceSimilar.score > threshold) resultArray[j] = name
            }
            if (resultArray[j] == "") resultArray[j] = "未知"
        }

        return resultArray
    }

    /**
     * @param list 字符串数组
     * @return faceFeatureData: byte[1032] 即字节数组形式的脸部特征值
     */
    private fun stringListToByteArray(list: List<String>): ByteArray {
        val resultArray = ByteArray(1032)
        for (i in 0..1031) {
            resultArray[i] = list[i].toByte()
        }
        return resultArray
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            FaceRecognition()
        }
    }
}