package test

import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class Test {

}

fun main() {
    val ft = SimpleDateFormat("MM-dd-HH-mm-ss")
    val time = ft.format(Date())
    println(time)
    if (File("src/res/$time.jpeg").exists()) println("存在")

}