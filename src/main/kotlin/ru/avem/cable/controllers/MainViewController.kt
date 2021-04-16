package ru.avem.cable.controllers

import com.fazecast.jSerialComm.SerialPort
import javafx.application.Platform
import javafx.scene.text.Text
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.cable.app.Cable.Companion.isAppRunning
import ru.avem.cable.communication.model.CommunicationModel
import ru.avem.cable.communication.model.devices.avem.avem4.Avem4Controller
import ru.avem.cable.communication.model.devices.avem.avem4.Avem4Model
import ru.avem.cable.communication.model.devices.avem.avem7.Avem7Controller
import ru.avem.cable.communication.model.devices.avem.avem7.Avem7Model
import ru.avem.cable.communication.model.devices.delta.DeltaController
import ru.avem.cable.communication.model.devices.delta.DeltaModel
import ru.avem.cable.communication.model.devices.owen.pr.OwenPrController
import ru.avem.cable.communication.model.devices.owen.pr.OwenPrModel
import ru.avem.cable.communication.model.devices.pm130.PM130Controller
import ru.avem.cable.communication.model.devices.pm130.PM130Model
import ru.avem.cable.database.entities.Protocol
import ru.avem.cable.database.entities.TestObjectsType
import ru.avem.cable.utils.LogTag
import ru.avem.cable.utils.State
import ru.avem.cable.utils.Toast
import ru.avem.cable.utils.sleep
import ru.avem.cable.view.MainView
import tornadofx.*
import java.lang.Math.random
import java.text.SimpleDateFormat
import kotlin.concurrent.thread
import kotlin.experimental.and


class MainViewController : Controller() {

    protected val owenPR1 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.DD1) as OwenPrController
    protected val owenPR2 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.DD2) as OwenPrController
    protected val pm130 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.PM130) as PM130Controller
    protected val a71 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A71) as Avem7Controller
    protected val a72 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A72) as Avem7Controller
    protected val a73 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A73) as Avem7Controller
    protected val a74 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A74) as Avem7Controller
    protected val a75 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A75) as Avem7Controller
    protected val a76 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A76) as Avem7Controller
    protected val kvm1 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.KVM1) as Avem4Controller
    protected val cp2000 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.CP2000) as DeltaController

    val view: MainView by inject()
    var position1 = ""
    private var logBuffer: String? = null

    @Volatile
    var isExperimentRunning: Boolean = false

    var cause: String = ""
        set(value) {
            if (value != "") {
                isExperimentRunning = false
            }
            field = value
        }

    fun refreshObjectsTypes() {
        view.comboBoxList.forEach {
            it.items = transaction {
                TestObjectsType.all().toList().asObservable()
            }
            it.selectionModel.select(0)
        }
    }

    fun showAboutUs() {
        Toast.makeText("Версия ПО: 1.0.0\nВерсия БСУ: 1.0.0\nДата: 30.03.2021").show(Toast.ToastType.INFORMATION)
    }

    fun isDevicesResponding(): Boolean {
        return true/*CommunicationModel.getDeviceById(CommunicationModel.DeviceID.DD2).isResponding &&
                CommunicationModel.getDeviceById(CommunicationModel.DeviceID.TRM1).isResponding &&
                CommunicationModel.getDeviceById(CommunicationModel.DeviceID.TRM2).isResponding &&
                CommunicationModel.getDeviceById(CommunicationModel.DeviceID.TRM3).isResponding*/
    }

    fun handleStopTest() {
        cause = "Отменено оператором"
    }

    private fun appendOneMessageToLog(tag: LogTag, message: String) {
        if (logBuffer == null || logBuffer != message) {
            logBuffer = message
            appendMessageToLog(tag, message)
        }
    }

    private fun appendMessageToLog(tag: LogTag, _msg: String) {
        val msg = Text("${SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis())} | $_msg")
        msg.style {
            fill = when (tag) {
                LogTag.MESSAGE -> tag.c
                LogTag.ERROR -> tag.c
                LogTag.DEBUG -> tag.c
            }
        }

        Platform.runLater {
            view.vBoxLog.add(msg)
        }
    }

    private fun isAtLeastOneItemIsSelected(): Boolean {
        return true /*view.comboBox1.selectionModel.selectedItem != null ||
                view.comboBox2.selectionModel.selectedItem != null ||
                view.comboBox3.selectionModel.selectedItem != null ||
                view.comboBox4.selectionModel.selectedItem != null ||
                view.comboBox5.selectionModel.selectedItem != null ||
                view.comboBox6.selectionModel.selectedItem != null*/
    }

    private fun saveProtocolToDB() {
        val dateFormatter = SimpleDateFormat("dd.MM.y")
        val timeFormatter = SimpleDateFormat("HH:mm:ss")
        val unixTime = System.currentTimeMillis()

        transaction {
            Protocol.new {
                date = dateFormatter.format(unixTime).toString()
                time = timeFormatter.format(unixTime).toString()
            }
        }
    }

    private fun startPollDevices() {
        CommunicationModel.startPoll(CommunicationModel.DeviceID.DD1, OwenPrModel.FIXED_STATES_REGISTER_1) { value ->
            value.toShort() and 32 > 0
        }
        CommunicationModel.startPoll(CommunicationModel.DeviceID.DD2, OwenPrModel.FIXED_STATES_REGISTER_1) { value ->
            value.toShort() and 32 > 0
        }
    }

    fun start() {
//        Test1Controller().startTest()
        if (!isAtLeastOneItemIsSelected()) {
            runLater {
                Toast.makeText("Выставьте объекты испытания из выбранных").show(Toast.ToastType.ERROR)
            }
        } else {
            if (isExperimentRunning) {
                CommunicationModel.addWritingRegister(
                    CommunicationModel.DeviceID.DD1,
                    OwenPrModel.RESET_DOG,
                    1.toShort()
                )
                owenPR1.initOwenPR()

                CommunicationModel.addWritingRegister(
                    CommunicationModel.DeviceID.DD2,
                    OwenPrModel.RESET_DOG,
                    1.toShort()
                )
                owenPR2.initOwenPR()

                startPollDevices()
            }

            owenPR1.offAllKMs1()
            owenPR2.offAllKMs2()

            owenPR2.on2_1()
            owenPR2.on2_2()
            owenPR2.on2_3()
            owenPR2.on2_4()
            owenPR2.on2_5()
            owenPR2.on2_6()
            owenPR2.on2_7()
            owenPR2.on2_8()



            for (i in 0..1000) {
                owenPR1.on1_1()
                sleep(300)
                owenPR1.on1_2()
                sleep(300)
                owenPR1.on1_3()
                sleep(300)
                owenPR1.on1_4()
                sleep(300)
                owenPR1.on1_5()
                sleep(300)
                owenPR1.on1_6()
                sleep(300)
                owenPR1.on1_7()
                sleep(300)
                owenPR1.on1_8()
                sleep(300)

                owenPR1.on2_1()
                sleep(300)
                owenPR1.on2_2()
                sleep(300)
                owenPR1.on2_3()
                sleep(300)
                owenPR1.on2_4()
                sleep(300)
                owenPR1.on2_5()
                sleep(300)
                owenPR1.on2_6()
                sleep(300)
                owenPR1.on2_7()
                sleep(300)
                owenPR1.on2_8()
                sleep(300)

                owenPR1.on4_1()
                sleep(300)
                owenPR1.on4_2()
                sleep(300)
                owenPR1.on4_3()
                sleep(300)
                owenPR1.on4_4()
                sleep(300)
                owenPR1.on4_5()
                sleep(300)
                owenPR1.on4_6()
                sleep(300)
                owenPR1.on4_7()
                sleep(300)
                owenPR1.on4_8()
                sleep(300)

                owenPR1.on5_1()
                sleep(300)
                owenPR1.on5_2()
                sleep(300)
                owenPR1.on5_3()
                sleep(300)
                owenPR1.on5_4()
                sleep(300)
                owenPR1.on5_5()
                sleep(300)
                owenPR1.on5_6()
                sleep(300)
                owenPR1.on5_7()
                sleep(300)
                owenPR1.on5_8()
                sleep(300)

                owenPR1.off1_1()
                sleep(300)
                owenPR1.off1_2()
                sleep(300)
                owenPR1.off1_3()
                sleep(300)
                owenPR1.off1_4()
                sleep(300)
                owenPR1.off1_5()
                sleep(300)
                owenPR1.off1_6()
                sleep(300)
                owenPR1.off1_7()
                sleep(300)
                owenPR1.off1_8()
                sleep(300)

                owenPR1.off2_1()
                sleep(300)
                owenPR1.off2_2()
                sleep(300)
                owenPR1.off2_3()
                sleep(300)
                owenPR1.off2_4()
                sleep(300)
                owenPR1.off2_5()
                sleep(300)
                owenPR1.off2_6()
                sleep(300)
                owenPR1.off2_7()
                sleep(300)
                owenPR1.off2_8()
                sleep(300)

                owenPR1.off4_1()
                sleep(300)
                owenPR1.off4_2()
                sleep(300)
                owenPR1.off4_3()
                sleep(300)
                owenPR1.off4_4()
                sleep(300)
                owenPR1.off4_5()
                sleep(300)
                owenPR1.off4_6()
                sleep(300)
                owenPR1.off4_7()
                sleep(300)
                owenPR1.off4_8()
                sleep(300)

                owenPR1.off5_1()
                sleep(300)
                owenPR1.off5_2()
                sleep(300)
                owenPR1.off5_3()
                sleep(300)
                owenPR1.off5_4()
                sleep(300)
                owenPR1.off5_5()
                sleep(300)
                owenPR1.off5_6()
                sleep(300)
                owenPR1.off5_7()
                sleep(300)
                owenPR1.off5_8()
                sleep(300)
            }

            appendOneMessageToLog(LogTag.DEBUG, "Инициализация устройств...")
            sleep(300)
            view.circlePR102.fill = State.OK.c
            sleep(300)
            view.circlePR200.fill = State.OK.c
            sleep(300)
            view.circlePM130.fill = State.OK.c
            sleep(300)
            view.circleCP2000.fill = State.OK.c
            sleep(300)
            view.circleAvem71.fill = State.OK.c
            sleep(300)
            view.circleAvem72.fill = State.OK.c
            sleep(300)
            view.circleAvem73.fill = State.OK.c
            sleep(300)
            view.circleAvem74.fill = State.OK.c
            sleep(300)
            view.circleAvem75.fill = State.OK.c
            sleep(300)
            view.circleAvem76.fill = State.OK.c
            sleep(300)
            view.circleKVM.fill = State.OK.c
            sleep(300)

            view.circleDI1.fill = State.OK.c
            sleep(300)
            view.circleDI2.fill = State.OK.c
            sleep(300)
            view.circleDI3.fill = State.OK.c
            sleep(300)
            view.circleDI4.fill = State.OK.c
            sleep(300)
            view.circleDI5.fill = State.OK.c
            sleep(300)
            view.circleDI6.fill = State.OK.c
            sleep(300)
            view.circleDI7.fill = State.OK.c
            sleep(300)
            view.circleDI8.fill = State.OK.c
            sleep(300)
            view.circleDI9.fill = State.OK.c
            sleep(300)

            view.tableValues[0].time.value = "15"
            view.tableValues[1].time.value = "15"
            view.tableValues[2].time.value = "15"
            view.tableValues[3].time.value = "15"
            view.tableValues[4].time.value = "15"
            view.tableValues[5].time.value = "15"

            appendOneMessageToLog(LogTag.DEBUG, "Поднятие напряжения")
            var i = 0
            while (i <= 2000.0) {
                i += 100 + (random() * 100).toInt()
                if (view.checkBox1.isSelected) {
                    view.tableValues[0].voltage.value = i.toString()
                }
                if (view.checkBox2.isSelected) {
                    view.tableValues[1].voltage.value = i.toString()
                }
                if (view.checkBox3.isSelected) {
                    view.tableValues[2].voltage.value = i.toString()
                }
                if (view.checkBox4.isSelected) {
                    view.tableValues[3].voltage.value = i.toString()
                }
                if (view.checkBox5.isSelected) {
                    view.tableValues[4].voltage.value = i.toString()
                }
                if (view.checkBox6.isSelected) {
                    view.tableValues[5].voltage.value = i.toString()
                }
                sleep(100)
            }

            appendOneMessageToLog(LogTag.DEBUG, "Напряжение выставлено")

            if (view.checkBox1.isSelected) {
                view.tableValues[0].amperage.value = "1.2"
            }
            if (view.checkBox2.isSelected) {
                view.tableValues[1].amperage.value = "1.3"
            }
            if (view.checkBox3.isSelected) {
                view.tableValues[2].amperage.value = "1.4"
            }
            if (view.checkBox4.isSelected) {
                view.tableValues[2].amperage.value = "1.4"
            }
            if (view.checkBox5.isSelected) {
                view.tableValues[3].amperage.value = "1.5"
            }
            if (view.checkBox6.isSelected) {
                view.tableValues[5].amperage.value = "1.7"
            }

            for (time in 0 until 15) {
                val timeOff = 14 - time
                if (view.checkBox1.isSelected) {
                    view.tableValues[0].time.value = timeOff.toString()
                }
                if (view.checkBox2.isSelected) {
                    view.tableValues[1].time.value = timeOff.toString()
                }
                if (view.checkBox3.isSelected) {
                    view.tableValues[2].time.value = timeOff.toString()
                }
                if (view.checkBox4.isSelected) {
                    view.tableValues[3].time.value = timeOff.toString()
                }
                if (view.checkBox5.isSelected) {
                    view.tableValues[4].time.value = timeOff.toString()
                }
                if (view.checkBox6.isSelected) {
                    view.tableValues[5].time.value = timeOff.toString()
                }
                sleep(1000)
            }

            appendOneMessageToLog(LogTag.MESSAGE, "Испытание завершено")

        }
    }


    private fun getScheme(scheme: String): List<MutableList<Int>> {
        val schemeString = scheme.replace(" ", "")
        var schemeCount = 0
        var dot = ""
        val list1 = mutableListOf<Int>()
        val list2 = mutableListOf<Int>()
        val list3 = mutableListOf<Int>()
        val list4 = mutableListOf<Int>()
        val list5 = mutableListOf<Int>()

        schemeString.forEach {
            if (it == '[') {
                schemeCount++
            } else if (it == ',' || it == ']') {
                when (schemeCount) {
                    1 -> {
                        list1.add(dot.toInt())
                    }
                    2 -> {
                        list2.add(dot.toInt())
                    }
                    3 -> {
                        list3.add(dot.toInt())
                    }
                    4 -> {
                        list4.add(dot.toInt())
                    }
                    5 -> {
                        list5.add(dot.toInt())
                    }
                }
                dot = ""
            } else if (it != ',') {
                dot += it
            }
        }
        return listOf(list1, list2, list3, list4, list5)
    }

    private fun setResult() {
        if (cause.isNotEmpty()) {
            appendMessageToLog(LogTag.ERROR, "Испытание прервано по причине: $cause")
//        } else if (!controller.isDevicesRespondingTest()) {
//            appendMessageToLog(LogTag.ERROR, "Испытание прервано по причине: \nпотеряна связь с устройствами")
//            soundError()
        } else {
            appendMessageToLog(LogTag.MESSAGE, "Испытание завершено успешно")
        }
    }

    private fun finalizeExperiment() {
        owenPR1.offAllKMs1()
        owenPR2.offAllKMs1()
        CommunicationModel.clearPollingRegisters()
    }

    init {
        refreshObjectsTypes()
        println()
        getScheme(view.comboBox1.selectionModel.selectedItem.scheme).forEach {

            if (it.size > 0) {
                println("VV ${it[0]}")
            }
            if (it.size > 1) {
                println("Z1 ${it[1]}")
            }
            if (it.size > 2) {
                println("Z2 ${it[2]}")
            }
            if (it.size > 3) {
                println("Z3 ${it[3]}")
            }
            if (it.size > 4) {
                println("Z4 ${it[4]}")
            }
            if (it.size > 5) {
                println("Z5 ${it[5]}")
            }
        }
        thread(isDaemon = true) {
            while (isAppRunning) {
                val serialPort = SerialPort.getCommPorts().filter {
                    it.toString() == "CP2103 USB to RS-485"
                }
                if (serialPort.isEmpty()) {
                    view.comIndicate.fill = State.BAD.c
                    view.circleAvem71.fill = State.BAD.c
                    view.circleAvem72.fill = State.BAD.c
                    view.circleAvem73.fill = State.BAD.c
                    view.circleAvem74.fill = State.BAD.c
                    view.circleAvem75.fill = State.BAD.c
                    view.circleAvem76.fill = State.BAD.c
                    view.circleCP2000.fill = State.BAD.c
                    view.circlePR102.fill = State.BAD.c
                    view.circlePR200.fill = State.BAD.c
                    view.circlePM130.fill = State.BAD.c
                    view.circleKVM.fill = State.BAD.c
                } else {
                    view.comIndicate.fill = State.OK.c
                }
                val serialPortCP2000 = SerialPort.getCommPorts().filter {
                    it.toString() == "CP2103 USB to CP2000"
                }
                if (serialPortCP2000.isEmpty()) {
                    view.comIndicateCP2000.fill = State.BAD.c
                } else {
                    view.comIndicateCP2000.fill = State.OK.c
                }

                CommunicationModel.getDeviceById(CommunicationModel.DeviceID.DD1).readRegister(
                    CommunicationModel.getDeviceById(CommunicationModel.DeviceID.DD1)
                        .getRegisterById(OwenPrModel.INSTANT_STATES_REGISTER_1)
                )
                if (!CommunicationModel.getDeviceById(CommunicationModel.DeviceID.DD1).isResponding) {
                    view.circlePR102.fill = State.BAD.c
                } else {
                    view.circlePR102.fill = State.OK.c
                }

                CommunicationModel.getDeviceById(CommunicationModel.DeviceID.DD2).readRegister(
                    CommunicationModel.getDeviceById(CommunicationModel.DeviceID.DD2)
                        .getRegisterById(OwenPrModel.INSTANT_STATES_REGISTER_1)
                )
                if (!CommunicationModel.getDeviceById(CommunicationModel.DeviceID.DD2).isResponding) {
                    view.circlePR200.fill = State.BAD.c
                } else {
                    view.circlePR200.fill = State.OK.c
                }

                CommunicationModel.getDeviceById(CommunicationModel.DeviceID.PM130).readRegister(
                    CommunicationModel.getDeviceById(CommunicationModel.DeviceID.PM130)
                        .getRegisterById(PM130Model.F_REGISTER)
                )
                if (!CommunicationModel.getDeviceById(CommunicationModel.DeviceID.PM130).isResponding) {
                    view.circlePM130.fill = State.BAD.c
                } else {
                    view.circlePM130.fill = State.OK.c
                }

                CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A71).readRegister(
                    CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A71)
                        .getRegisterById(Avem7Model.AMPERAGE)
                )
                if (!CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A71).isResponding) {
                    view.circleAvem71.fill = State.BAD.c
                } else {
                    view.circleAvem71.fill = State.OK.c
                }

                CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A72).readRegister(
                    CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A72)
                        .getRegisterById(Avem7Model.AMPERAGE)
                )
                if (!CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A72).isResponding) {
                    view.circleAvem72.fill = State.BAD.c
                } else {
                    view.circleAvem72.fill = State.OK.c
                }

                CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A73).readRegister(
                    CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A73)
                        .getRegisterById(Avem7Model.AMPERAGE)
                )
                if (!CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A73).isResponding) {
                    view.circleAvem73.fill = State.BAD.c
                } else {
                    view.circleAvem73.fill = State.OK.c
                }

                CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A74).readRegister(
                    CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A74)
                        .getRegisterById(Avem7Model.AMPERAGE)
                )
                if (!CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A74).isResponding) {
                    view.circleAvem74.fill = State.BAD.c
                } else {
                    view.circleAvem74.fill = State.OK.c
                }

                CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A75).readRegister(
                    CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A75)
                        .getRegisterById(Avem7Model.AMPERAGE)
                )
                if (!CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A75).isResponding) {
                    view.circleAvem75.fill = State.BAD.c
                } else {
                    view.circleAvem75.fill = State.OK.c
                }

                CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A76).readRegister(
                    CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A76)
                        .getRegisterById(Avem7Model.AMPERAGE)
                )
                if (!CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A76).isResponding) {
                    view.circleAvem76.fill = State.BAD.c
                } else {
                    view.circleAvem76.fill = State.OK.c
                }

                CommunicationModel.getDeviceById(CommunicationModel.DeviceID.CP2000).readRegister(
                    CommunicationModel.getDeviceById(CommunicationModel.DeviceID.CP2000)
                        .getRegisterById(DeltaModel.ERRORS_REGISTER)
                )
                if (!CommunicationModel.getDeviceById(CommunicationModel.DeviceID.CP2000).isResponding) {
                    view.circleCP2000.fill = State.BAD.c
                } else {
                    view.circleCP2000.fill = State.OK.c
                }

                CommunicationModel.getDeviceById(CommunicationModel.DeviceID.KVM1).readRegister(
                    CommunicationModel.getDeviceById(CommunicationModel.DeviceID.KVM1)
                        .getRegisterById(Avem4Model.AMP_VOLTAGE)
                )
                if (!CommunicationModel.getDeviceById(CommunicationModel.DeviceID.KVM1).isResponding) {
                    view.circleKVM.fill = State.BAD.c
                } else {
                    view.circleKVM.fill = State.OK.c
                }

                sleep(1000)
            }
        }
    }
}
