package main
import java.awt.Color
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*

class Window : JFrame(), ActionListener {

    private var logo = ImageIcon("src/res/LOGO.png")
    private var jLab1 = JLabel("  天眼查看")
    private var jLab2 = JLabel("物联网1902刘通达")
    private var jLab3 = JLabel("帧率:等待查询")
    private var jLab4 = JLabel("--:---")
    private var jLab5 = JLabel("--:---")

    private var jMB = JMenuBar()
    private var jM1 = JMenu("查询")
    private var jM2 = JMenu("管理")
    private var jMI1 = JMenuItem("查询所有数据")
    private var jMI2 = JMenuItem("查询--")
    private var jMI3 = JMenuItem("查询--")
    private var jMI4 = JMenuItem("检查数据")
    private var jMI5 = JMenuItem("--")
    private var jMI6 = JMenuItem("--")

    private var jLabel = JLabel()


    //  初始化
    init {
        layout = null
        //font = Font("仿宋", Font.BOLD, 15)
        background = Color(57,57,57)
        setSize(950, 559)
        iconImage = logo.image
        title = "天眼后台管理"

        jMB.setBounds(0, 0, 780, 30)
        jMB.background = Color(242,242,242)

        jLab1.setBounds(20, 491, 200, 20)
        jLab2.setBounds(650, 491, 150, 20)
        jLab3.setBounds(0, 471, 200, 20)
        jLab4.setBounds(200, 471, 200, 20)
        jLab5.setBounds(400, 471, 200, 20)

        jMB.add(jM1)
        jMB.add(jM2)

        jM1.add(jMI1)
        jM1.add(jMI2)
        jM1.add(jMI3)
        jM2.add(jMI4)
        jM2.add(jMI5)
        jM2.add(jMI6)

        jMI1.addActionListener(this)
        jMI2.addActionListener(this)
        jMI3.addActionListener(this)
        jMI4.addActionListener(this)
        jMI5.addActionListener(this)
        jMI6.addActionListener(this)

        jLabel.setBounds(0, 40, 320, 240)

        add(jMB)
        add(jLab1)
        add(jLab2)
        add(jLab3)
        add(jLab4)
        add(jLab5)
        add(jLabel)

        setLocationRelativeTo(null)
        defaultCloseOperation = EXIT_ON_CLOSE
        this.isVisible = true

        Thread {
            while (true) {
                jLabel.icon = ImageIcon("src/res/1.jpeg")
                Thread.sleep(20)
                jLabel.icon = ImageIcon("src/res/2.jpeg")
                Thread.sleep(20)
                jLabel.icon = ImageIcon("src/res/3.jpeg")
                Thread.sleep(20)
                jLabel.icon = ImageIcon("src/res/4.jpeg")
                Thread.sleep(20)
                jLabel.icon = ImageIcon("src/res/5.jpeg")
                Thread.sleep(20)
            }
        }.start()

    }

    override fun actionPerformed(e: ActionEvent?) {
        if (e!!.source == jMI1) {
            println("查询所有")
        } else if (e.source == jMI2) {
            println("查询未出库")
        } else if (e.source == jMI3) {
            println("查询已出库")
        }else if (e.source == jMI4) {
            println("检查数据")
        }else if (e.source == jMI5) {
            println("--")
        }else if (e.source == jMI6) {
            println("--")
        }
    }
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Window()
        }
    }

}