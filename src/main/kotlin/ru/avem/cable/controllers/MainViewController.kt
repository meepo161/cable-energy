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
import ru.avem.cable.utils.*
import ru.avem.cable.utils.Measuring.VOLT
import ru.avem.cable.view.MainView
import tornadofx.*
import java.text.SimpleDateFormat
import kotlin.concurrent.thread
import kotlin.experimental.and


class MainViewController : Controller() {

    private val owenPR1 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.DD1) as OwenPrController
    private val owenPR2 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.DD2) as OwenPrController
    private val pm130 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.PM130) as PM130Controller
    private val a71 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A71) as Avem7Controller
    private val a72 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A72) as Avem7Controller
    private val a73 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A73) as Avem7Controller
    private val a74 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A74) as Avem7Controller
    private val a75 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A75) as Avem7Controller
    private val a76 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.A76) as Avem7Controller
    private val kvm1 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.KVM1) as Avem4Controller
    private val cp2000 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.CP2000) as DeltaController

    val view: MainView by inject()
    var position1 = ""
    private var logBuffer: String? = null
    var isCP2000Ready = false
    var measuringUKVM = 0.0
    var stage = 0
    var isResponding = false



    @Volatile
    var isExperimentRunning: Boolean = false

    var cause: String = ""
        set(value) {
            if (value != "") {
                isExperimentRunning = false
                view.buttonStart.isDisable = true
            }
            field = value
        }

    fun refreshObjectsTypes() {
//        view.comboBoxList.forEach {
//            it.items = transaction {
//                TestObjectsType.all().toList().asObservable()
//            }
//            it.selectionModel.select(0)
//        }

    }

    fun showAboutUs() {
        Toast.makeText("Версия ПО: 1.0.1\nВерсия БСУ: 1.0.0\nДата: 19.04.2021").show(Toast.ToastType.INFORMATION)
    }

    fun isDevicesResponding(): Boolean {
        return true/*owenPR2.isResponding &&
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
        CommunicationModel.startPoll(CommunicationModel.DeviceID.KVM1, Avem4Model.RMS_VOLTAGE) { value ->
            measuringUKVM = value.toDouble() * 1000
            view.tableValueUOut[0].voltage.value = formatRealNumber(measuringUKVM).toString()
        }

        CommunicationModel.startPoll(CommunicationModel.DeviceID.PM130, PM130Model.I_A_REGISTER) { value ->
            view.tableValuesIn[0].amperage.value = formatRealNumber(value.toDouble() * 50).toString()
        }
        CommunicationModel.startPoll(CommunicationModel.DeviceID.PM130, PM130Model.I_B_REGISTER) { value ->
            view.tableValuesIn[1].amperage.value = formatRealNumber(value.toDouble() * 50).toString()
        }
        CommunicationModel.startPoll(CommunicationModel.DeviceID.PM130, PM130Model.I_C_REGISTER) { value ->
            view.tableValuesIn[2].amperage.value = formatRealNumber(value.toDouble() * 50).toString()
        }
        CommunicationModel.startPoll(CommunicationModel.DeviceID.PM130, PM130Model.U_AB_REGISTER) { value ->
            view.tableValuesIn[0].voltage.value = formatRealNumber(value.toDouble()).toString()
        }
        CommunicationModel.startPoll(CommunicationModel.DeviceID.PM130, PM130Model.U_BC_REGISTER) { value ->
            view.tableValuesIn[1].voltage.value = formatRealNumber(value.toDouble()).toString()
        }
        CommunicationModel.startPoll(CommunicationModel.DeviceID.PM130, PM130Model.U_CA_REGISTER) { value ->
            view.tableValuesIn[2].voltage.value = formatRealNumber(value.toDouble()).toString()
        }

        CommunicationModel.startPoll(CommunicationModel.DeviceID.A71, Avem7Model.AMPERAGE) { value ->
            view.tableValuesIOut[0].amperage.value = (value.toDouble() * 2 * 1000).toInt().toString()
            if ((value.toDouble() * 2 * 1000).toInt() > view.tableValuesIOutSet[0].amperage.value.toInt()) {
                offPost1()
                if (view.checkBox1.isSelected) {
                    appendOneMessageToLog(LogTag.ERROR, "Ток на Пост1 превысил заданный")
                }
                view.checkBox1.isSelected = false
            }
        }
        CommunicationModel.startPoll(CommunicationModel.DeviceID.A72, Avem7Model.AMPERAGE) { value ->
            view.tableValuesIOut[1].amperage.value = (value.toDouble() * 2 * 1000).toInt().toString()
            if ((value.toDouble() * 2 * 1000).toInt() > view.tableValuesIOutSet[1].amperage.value.toInt()) {
                offPost2()
                if (view.checkBox2.isSelected) {
                    appendOneMessageToLog(LogTag.ERROR, "Ток на Пост2 превысил заданный")
                }
                view.checkBox2.isSelected = false
            }
        }
        CommunicationModel.startPoll(CommunicationModel.DeviceID.A73, Avem7Model.AMPERAGE) { value ->
            view.tableValuesIOut[2].amperage.value = (value.toDouble() * 2 * 1000).toInt().toString()
            if ((value.toDouble() * 2 * 1000).toInt() > view.tableValuesIOutSet[2].amperage.value.toInt()) {
                offPost3()
                if (view.checkBox3.isSelected) {
                    appendOneMessageToLog(LogTag.ERROR, "Ток на Пост3 превысил заданный")
                }
                view.checkBox3.isSelected = false
            }
        }
        CommunicationModel.startPoll(CommunicationModel.DeviceID.A74, Avem7Model.AMPERAGE) { value ->
            view.tableValuesIOut[3].amperage.value = (value.toDouble() * 2 * 1000).toInt().toString()
            if ((value.toDouble() * 2 * 1000).toInt() > view.tableValuesIOutSet[3].amperage.value.toInt()) {
                offPost4()
                if (view.checkBox4.isSelected) {
                    appendOneMessageToLog(LogTag.ERROR, "Ток на Пост4 превысил заданный")
                }
                view.checkBox4.isSelected = false
            }
        }
        CommunicationModel.startPoll(CommunicationModel.DeviceID.A75, Avem7Model.AMPERAGE) { value ->
            view.tableValuesIOut[4].amperage.value = (value.toDouble() * 2 * 1000).toInt().toString()
            if ((value.toDouble() * 2 * 1000).toInt() > view.tableValuesIOutSet[4].amperage.value.toInt()) {
                offPost5()
                if (view.checkBox5.isSelected) {
                    appendOneMessageToLog(LogTag.ERROR, "Ток на Пост5 превысил заданный")
                }
                view.checkBox5.isSelected = false
            }
        }
        CommunicationModel.startPoll(CommunicationModel.DeviceID.A76, Avem7Model.AMPERAGE) { value ->
            view.tableValuesIOut[5].amperage.value = (value.toDouble() * 2 * 1000).toInt().toString()
            if ((value.toDouble() * 2 * 1000).toInt() > view.tableValuesIOutSet[5].amperage.value.toInt()) {
                offPost6()
                if (view.checkBox6.isSelected) {
                    appendOneMessageToLog(LogTag.ERROR, "Ток на Пост6 превысил заданный")
                }
                view.checkBox6.isSelected = false
            }
            var sum = 0
            view.tableValuesIOut.forEach{
                sum += it.amperage.value.toInt()
            }
            view.tableValueIOutSum[0].amperage.value = sum.toString()
        }
    }

    fun start() {
        if (!isAtLeastOneItemIsSelected()) {
            runLater {
                Toast.makeText("Выставьте объекты испытания из выбранных").show(Toast.ToastType.ERROR)
            }
        } else {
            var timeOut = 0
            appendOneMessageToLog(LogTag.DEBUG, "Начало испытания")
            isExperimentRunning = true

            if (isExperimentRunning && isResponding) {
                appendOneMessageToLog(LogTag.DEBUG, "Инициализация БСУ")
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
                appendOneMessageToLog(LogTag.DEBUG, "БСУ готов")
                appendOneMessageToLog(LogTag.DEBUG, "Инициализация измерительных устройств")
                startPollDevices()
                appendOneMessageToLog(LogTag.MESSAGE, "Измерительные устройства готовы к работе")
            }

            if (isExperimentRunning) {
                appendOneMessageToLog(LogTag.DEBUG, "Подготовка схемы")
                owenPR1.on1_1()

                if (!view.isBurn) {
                    owenPR1.on1_2()
                } else {
                    owenPR2.on21_1()
                }

                owenPR2.on2_1()
                owenPR2.on2_2()
                owenPR2.on2_3()
                owenPR2.on2_4()
                owenPR2.on2_5()
                owenPR2.on2_6()
                owenPR2.on2_7()
                owenPR2.on2_8()

                offAllSolenoids()
                appendOneMessageToLog(LogTag.MESSAGE, "Схема собрана")
            }

            if (isExperimentRunning) {
                appendOneMessageToLog(LogTag.DEBUG, "Инициализация частотного преобразователя")
            }
            timeOut = 30
            while (!cp2000.isResponding && isExperimentRunning && timeOut-- > 0) {
                cp2000.readRegister(cp2000.getRegisterById(DeltaModel.STATUS_REGISTER))
                sleep(1000)
            }

            isCP2000Ready = true

            if (isExperimentRunning && isResponding && cp2000.isResponding) {
                appendOneMessageToLog(LogTag.MESSAGE, "Частотный преобразователь готов к работе")
                sleep(3000)
                cp2000.setObjectParams(
                    fOut = 50,
                    voltageP1 = 10,
                    fP1 = 50
                )
            }

            if (isExperimentRunning) {
                val listOfZero = observableListOf<Int>()
                repeat(25) { listOfZero.add(0) }
                val list1 = if (view.checkBox1.isSelected) {
                    getScheme(view.comboBox1.selectionModel.selectedItem.scheme)
                } else {
                    getScheme(listOfZero.toString())
                }
                val list2 = if (view.checkBox2.isSelected) {
                    getScheme(view.comboBox2.selectionModel.selectedItem.scheme)
                } else {
                    getScheme(listOfZero.toString())
                }
                val list3 = if (view.checkBox3.isSelected) {
                    getScheme(view.comboBox3.selectionModel.selectedItem.scheme)
                } else {
                    getScheme(listOfZero.toString())
                }
                val list4 = if (view.checkBox4.isSelected) {
                    getScheme(view.comboBox4.selectionModel.selectedItem.scheme)
                } else {
                    getScheme(listOfZero.toString())
                }
                val list5 = if (view.checkBox5.isSelected) {
                    getScheme(view.comboBox5.selectionModel.selectedItem.scheme)
                } else {
                    getScheme(listOfZero.toString())
                }
                val list6 = if (view.checkBox6.isSelected) {
                    getScheme(view.comboBox6.selectionModel.selectedItem.scheme)
                } else {
                    getScheme(listOfZero.toString())
                }

                val maxSize = getMaxSize(list1, list2, list3, list4, list5, list6)

                stage = 0

                for (i in 1..maxSize) {
                    if (isExperimentRunning) {
                        if (view.checkBox1.isSelected) {
                            offPost1()
                            if (list1[stage + 0] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост1 Пневмоцилиндр1")
                                owenPR1.on1_3()
                            }
                            if (list1[stage + 1] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост1 Пневмоцилиндр2")
                                owenPR1.on1_4()
                            }
                            if (list1[stage + 2] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост1 Пневмоцилиндр3")
                                owenPR1.on1_5()
                            }
                            if (list1[stage + 3] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост1 Пневмоцилиндр4")
                                owenPR1.on1_6()
                            }
                            if (list1[stage + 4] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост1 Пневмоцилиндр5")
                                owenPR1.on1_7()
                            }
                        }

                        if (view.checkBox2.isSelected) {
                            offPost2()
                            if (list2[stage + 0] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост2 Пневмоцилиндр1")
                                owenPR1.on1_8()
                            }
                            if (list2[stage + 1] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост2 Пневмоцилиндр2")
                                owenPR1.on2_1()
                            }
                            if (list2[stage + 2] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост2 Пневмоцилиндр3")
                                owenPR1.on2_2()
                            }
                            if (list2[stage + 3] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост2 Пневмоцилиндр4")
                                owenPR1.on2_3()
                            }
                            if (list2[stage + 4] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост2 Пневмоцилиндр5")
                                owenPR1.on2_4()
                            }
                        }

                        if (view.checkBox3.isSelected) {
                            offPost3()
                            if (list3[stage + 0] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост3 Пневмоцилиндр1")
                                owenPR1.on2_5()
                            }
                            if (list3[stage + 1] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост3 Пневмоцилиндр2")
                                owenPR1.on2_6()
                            }
                            if (list3[stage + 2] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост3 Пневмоцилиндр3")
                                owenPR1.on2_7()
                            }
                            if (list3[stage + 3] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост3 Пневмоцилиндр4")
                                owenPR1.on2_8()
                            }
                            if (list3[stage + 4] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост3 Пневмоцилиндр5")
                                owenPR1.on4_1()
                            }
                        }

                        if (view.checkBox4.isSelected) {
                            offPost4()
                            if (list4[stage + 0] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост4 Пневмоцилиндр1")
                                owenPR1.on4_2()
                            }
                            if (list4[stage + 1] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост4 Пневмоцилиндр2")
                                owenPR1.on4_3()
                            }
                            if (list4[stage + 2] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост4 Пневмоцилиндр3")
                                owenPR1.on4_4()
                            }
                            if (list4[stage + 3] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост4 Пневмоцилиндр4")
                                owenPR1.on4_5()
                            }
                            if (list4[stage + 4] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост4 Пневмоцилиндр5")
                                owenPR1.on4_6()
                            }
                        }

                        if (view.checkBox5.isSelected) {
                            offPost5()
                            if (list5[stage + 0] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост5 Пневмоцилиндр1")
                                owenPR1.on4_7()
                            }
                            if (list5[stage + 1] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост5 Пневмоцилиндр2")
                                owenPR1.on4_8()
                            }
                            if (list5[stage + 2] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост5 Пневмоцилиндр3")
                                owenPR1.on5_1()
                            }
                            if (list5[stage + 3] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост5 Пневмоцилиндр4")
                                owenPR1.on5_2()
                            }
                            if (list5[stage + 4] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост5 Пневмоцилиндр5")
                                owenPR1.on5_3()
                            }
                        }

                        if (view.checkBox6.isSelected) {
                            offPost6()
                            if (list6[stage + 0] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост6 Пневмоцилиндр1")
                                owenPR1.on5_4()
                            }
                            if (list6[stage + 1] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост6 Пневмоцилиндр2")
                                owenPR1.on5_5()
                            }
                            if (list6[stage + 2] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост6 Пневмоцилиндр3")
                                owenPR1.on5_6()
                            }
                            if (list6[stage + 3] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост6 Пневмоцилиндр4")
                                owenPR1.on5_7()
                            }
                            if (list6[stage + 4] == 1) {
                                appendOneMessageToLog(LogTag.DEBUG, "Замкнут Пост6 Пневмоцилиндр5")
                                owenPR1.on5_8()
                            }
                        }

                        if (isExperimentRunning) {
                            cp2000.startObject()
                        }

                        timeOut = 50
                        while (isExperimentRunning && timeOut-- > 0) {
                            sleep(100)
                        }

                        if (isExperimentRunning && !view.isBurn) {
                            appendOneMessageToLog(LogTag.DEBUG, "Поднятие напряжения до ${view.tfSetU.text.toInt()}В")
                            regulation(1 * VOLT, 10, 3, view.tfSetU.text.toDouble(), 0.15, 100.0, 300, 300)
                        }
                        if (isExperimentRunning && view.isBurn) {
                            appendOneMessageToLog(LogTag.DEBUG, "Операция прожига")
                            regulationBurn(1 * VOLT, 10, 3, 10000.0, 0.15, 100.0, 300, 300)
                        }

                        if (isExperimentRunning) {
                            appendOneMessageToLog(LogTag.MESSAGE, "Напряжение выставлено")
                            appendOneMessageToLog(LogTag.DEBUG, "Запуск отсчета времени")
                        }

                        timeOut = view.tfSetTime.text.toInt()
                        while (isExperimentRunning && timeOut-- > 0) {
                            sleep(1000)
                            view.tableValueTime[0].time.value = timeOut.toString()
                        }
                        if (isExperimentRunning) {
                            appendOneMessageToLog(LogTag.MESSAGE, "Выдержка завершена")
                            appendOneMessageToLog(LogTag.DEBUG, "Остановка частотного преобразователя")
                        }
                        cp2000.stopObject()

                        timeOut = 50
                        while (isExperimentRunning && timeOut-- > 0) {
                            sleep(100)
                        }
                        stage += 5

                        var isContinue = false

                        if (isExperimentRunning && maxSize != i) {
                            view.currentWindow?.let {
                                showTwoWayDialog(
                                    "Переход в следующий этап",
                                    "Для продолжения испытания, подключите следующие жилы к постам или нажмите <Отменить>",
                                    "Продолжить",
                                    "Отменить",
                                    { isContinue = true },
                                    { cause = "Отмена" },
                                    currentWindow = it
                                )
                            }
                            while (!isContinue && isExperimentRunning) {
                                sleep(100)
                            }
                        }

                    }
                }
            }
            finalizeExperiment()
        }
    }

    private fun getMaxSize(
        list1: List<Int>,
        list2: List<Int>,
        list3: List<Int>,
        list4: List<Int>,
        list5: List<Int>,
        list6: List<Int>
    ): Int {
        val listOfMaxSize = mutableListOf<Int>()

        listOfMaxSize.add(mutableListOf(
            getSize(list1)
        ).let {
            it.sortDescending()
            it[0]
        }
        )
        listOfMaxSize.add(mutableListOf(
            getSize(list2)
        ).let {
            it.sortDescending()
            it[0]
        }
        )
        listOfMaxSize.add(mutableListOf(
            getSize(list3)
        ).let {
            it.sortDescending()
            it[0]
        }
        )
        listOfMaxSize.add(mutableListOf(
            getSize(list4)
        ).let {
            it.sortDescending()
            it[0]
        }
        )
        listOfMaxSize.add(mutableListOf(
            getSize(list5)
        ).let {
            it.sortDescending()
            it[0]
        }
        )
        listOfMaxSize.add(mutableListOf(
            getSize(list6)
        ).let {
            it.sortDescending()
            it[0]
        }
        )

        val maxSize = listOfMaxSize.let {
            it.sortDescending()
            it[0]
        }
        return maxSize
    }

    private fun offAllSolenoids() {
        offPost1()
        offPost2()
        offPost3()
        offPost4()
        offPost5()
        offPost6()
    }

    private fun offPost6() {
        owenPR1.off5_4()
        owenPR1.off5_5()
        owenPR1.off5_6()
        owenPR1.off5_7()
        owenPR1.off5_8()
    }

    private fun offPost5() {
        owenPR1.off4_7()
        owenPR1.off4_8()
        owenPR1.off5_1()
        owenPR1.off5_2()
        owenPR1.off5_3()
    }

    private fun offPost4() {
        owenPR1.off4_2()
        owenPR1.off4_3()
        owenPR1.off4_4()
        owenPR1.off4_5()
        owenPR1.off4_6()
    }

    private fun offPost3() {
        owenPR1.off2_5()
        owenPR1.off2_6()
        owenPR1.off2_7()
        owenPR1.off2_8()
        owenPR1.off4_1()
    }

    private fun offPost2() {
        owenPR1.off1_8()
        owenPR1.off2_1()
        owenPR1.off2_2()
        owenPR1.off2_3()
        owenPR1.off2_4()
    }

    private fun offPost1() {
        owenPR1.off1_3()
        owenPR1.off1_4()
        owenPR1.off1_5()
        owenPR1.off1_6()
        owenPR1.off1_7()
    }

    private fun getSize(list: List<Int>): Int {
        var listSize = 0
        if (list[0] == 1 || list[1] == 1 || list[2] == 1 || list[3] == 1 || list[4] == 1) {
            listSize = 1
        }
        if (list[5] == 1 || list[6] == 1 || list[7] == 1 || list[8] == 1 || list[9] == 1) {
            listSize = 2
        }
        if (list[10] == 1 || list[11] == 1 || list[12] == 1 || list[13] == 1 || list[14] == 1) {
            listSize = 3
        }
        if (list[15] == 1 || list[16] == 1 || list[17] == 1 || list[18] == 1 || list[19] == 1) {
            listSize = 4
        }
        if (list[20] == 1 || list[21] == 1 || list[22] == 1 || list[23] == 1 || list[24] == 1) {
            listSize = 5
        }
        return listSize
    }

    private fun getScheme(scheme: String): List<Int> {
        val schemeString = scheme.replace(", ", "").replace("[", "").replace("]", "")
        val list1 = mutableListOf<Int>()
        schemeString.forEach {
            list1.add(it.toString().toInt())
        }
        return list1
    }

    private fun regulation(
        start: Int,
        coarseStep: Int,
        fineStep: Int,
        end: Double,
        coarseLimit: Double,
        fineLimit: Double,
        coarseSleep: Int,
        fineSleep: Int
    ): Int {
        var start = start
        val coarseMinLimit = 1 - coarseLimit
        val coarseMaxLimit = 1 + coarseLimit
        var timeOut = 30
        while (isExperimentRunning && (measuringUKVM < end * coarseMinLimit || measuringUKVM > end * coarseMaxLimit) && isDevicesResponding() && timeOut-- > 0) {
            if (measuringUKVM < end * coarseMinLimit) {
                cp2000.setObjectUMax(coarseStep.let { start += it; start })
            } else if (measuringUKVM > end * coarseMaxLimit) {
                cp2000.setObjectUMax(coarseStep.let { start -= it; start })
            }
            sleep(coarseSleep.toLong())
            appendOneMessageToLog(LogTag.MESSAGE, "Выводим напряжение для получения заданного значения грубо")
        }
        timeOut = 30
        while (isExperimentRunning && (measuringUKVM < end /*- fineLimit TODO чтоб больше было*/ || measuringUKVM > end + fineLimit) && isDevicesResponding() && timeOut-- > 0) {
            if (measuringUKVM < end /*- fineLimit*/) {
                cp2000.setObjectUMax(fineStep.let { start += it; start })
            } else if (measuringUKVM > end + fineLimit) {
                cp2000.setObjectUMax(fineStep.let { start -= it; start })
            }
            sleep(fineSleep.toLong())
            appendOneMessageToLog(LogTag.MESSAGE, "Выводим напряжение для получения заданного значения точно")
        }
        return start
    }

    private fun regulationBurn(
        start: Int,
        coarseStep: Int,
        fineStep: Int,
        end: Double,
        coarseLimit: Double,
        fineLimit: Double,
        coarseSleep: Int,
        fineSleep: Int
    ): Int {
        var start = start
        val coarseMinLimit = 1 - coarseLimit
        val coarseMaxLimit = 1 + coarseLimit
        var timeOut = 30
        while (isExperimentRunning && (view.tableValueIOutSum[0].amperage.value.toInt() < end * coarseMinLimit || view.tableValueIOutSum[0].amperage.value.toInt() > end * coarseMaxLimit) && isDevicesResponding() && timeOut-- > 0) {
            if (view.tableValueIOutSum[0].amperage.value.toInt() < end * coarseMinLimit) {
                cp2000.setObjectUMax(coarseStep.let { start += it; start })
            } else if (view.tableValueIOutSum[0].amperage.value.toInt() > end * coarseMaxLimit) {
                cp2000.setObjectUMax(coarseStep.let { start -= it; start })
            }
            sleep(coarseSleep.toLong())
            appendOneMessageToLog(LogTag.MESSAGE, "Выводим напряжение для получения заданного значения тока грубо")
        }


        timeOut = 30
        while (isExperimentRunning && (view.tableValueIOutSum[0].amperage.value.toInt() < end /*- fineLimit TODO чтоб больше было*/ || view.tableValueIOutSum[0].amperage.value.toInt() > end + fineLimit) && isDevicesResponding() && timeOut-- > 0) {
            if (view.tableValueIOutSum[0].amperage.value.toInt() < end /*- fineLimit*/) {
                cp2000.setObjectUMax(fineStep.let { start += it; start })
            } else if (view.tableValueIOutSum[0].amperage.value.toInt() > end + fineLimit) {
                cp2000.setObjectUMax(fineStep.let { start -= it; start })
            }
            sleep(fineSleep.toLong())
            appendOneMessageToLog(LogTag.MESSAGE, "Выводим напряжение для получения заданного значения тока точно")
        }
        return start
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
        cp2000.stopObject()
        while (measuringUKVM > 100) {
            sleep(100)
        }
        offAllSolenoids()
        owenPR1.offAllKMs1()
        owenPR2.offAllKMs2()
        CommunicationModel.clearPollingRegisters()
        isExperimentRunning = false
        view.buttonStart.isDisable = false
        setResult()
    }

    init {
        thread(isDaemon = true) {
            while (isAppRunning) {
                val serialPort = SerialPort.getCommPorts().filter {
                    it.toString() == "CP2103 USB to RS-485"
                }
                if (serialPort.isEmpty()) {
                    view.comIndicate.fill = State.BAD.c
                    view.circleAvem71.fill = State.INTERMEDIATE.c
                    view.circleAvem72.fill = State.INTERMEDIATE.c
                    view.circleAvem73.fill = State.INTERMEDIATE.c
                    view.circleAvem74.fill = State.INTERMEDIATE.c
                    view.circleAvem75.fill = State.INTERMEDIATE.c
                    view.circleAvem76.fill = State.INTERMEDIATE.c
                    view.circlePR102.fill = State.INTERMEDIATE.c
                    view.circlePR200.fill = State.INTERMEDIATE.c
                    view.circlePM130.fill = State.INTERMEDIATE.c
                    view.circleKVM.fill = State.INTERMEDIATE.c
                } else {
                    view.comIndicate.fill = State.OK.c

                    sleep(100)
                    var dd1In = owenPR1.getRegisterById(OwenPrModel.INSTANT_STATES_REGISTER_1)
                    owenPR1.readRegister(dd1In)
                    if (!owenPR1.isResponding) {
                        view.circlePR102.fill = State.BAD.c
                        view.circleDI1.fill = State.INTERMEDIATE.c
                        view.circleDI2.fill = State.INTERMEDIATE.c
                        view.circleDI3.fill = State.INTERMEDIATE.c
                        view.circleDI4.fill = State.INTERMEDIATE.c
                        view.circleDI5.fill = State.INTERMEDIATE.c
                        view.circleDI6.fill = State.INTERMEDIATE.c
                        view.circleDI7.fill = State.INTERMEDIATE.c
                        view.circleDI8.fill = State.INTERMEDIATE.c
                        view.circleDI9.fill = State.INTERMEDIATE.c
                    } else {
                        view.circlePR102.fill = State.OK.c
                        if (dd1In.value.toShort() and 1 > 0) {
                            view.circleDI1.fill = State.OK.c
                        } else {
                            view.circleDI1.fill = State.BAD.c
                        }
                        if (dd1In.value.toShort() and 2 > 0) {
                            view.circleDI2.fill = State.OK.c
                        } else {
                            view.circleDI2.fill = State.BAD.c
                        }
                        if (dd1In.value.toShort() and 4 > 0) {
                            view.circleDI3.fill = State.OK.c
                        } else {
                            view.circleDI3.fill = State.BAD.c
                        }
                        if (dd1In.value.toShort() and 32 > 0) {
                            view.circleDI4.fill = State.BAD.c
                        } else {
                            view.circleDI4.fill = State.OK.c
                        }
                        if (dd1In.value.toShort() and 64 > 0) {
                            view.circleDI5.fill = State.BAD.c
                        } else {
                            view.circleDI5.fill = State.OK.c
                        }
                        if (dd1In.value.toShort() and 128 > 0) {
                            view.circleDI6.fill = State.BAD.c
                        } else {
                            view.circleDI6.fill = State.OK.c
                        }

                        sleep(100)
                        var dd1In2 = owenPR1.getRegisterById(OwenPrModel.INSTANT_STATES_REGISTER_2)
                        owenPR1.readRegister(dd1In2)
                        if (dd1In2.value.toShort() and 2 > 0) {
                            view.circleDI7.fill = State.OK.c
                        } else {
                            view.circleDI7.fill = State.BAD.c
                        }
                        if (dd1In2.value.toShort() and 4 > 0) {
                            view.circleDI8.fill = State.OK.c
                        } else {
                            view.circleDI8.fill = State.BAD.c
                        }
                        if (dd1In2.value.toShort() and 8 > 0) {
                            view.circleDI9.fill = State.OK.c
                        } else {
                            view.circleDI9.fill = State.BAD.c
                        }

                    }
                    sleep(100)
                    owenPR2.readRegister(owenPR2.getRegisterById(OwenPrModel.INSTANT_STATES_REGISTER_1))
                    if (!owenPR2.isResponding) {
                        view.circlePR200.fill = State.BAD.c
                    } else {
                        view.circlePR200.fill = State.OK.c
                    }

                    sleep(100)
                    pm130.readRegister(pm130.getRegisterById(PM130Model.F_REGISTER))
                    if (!pm130.isResponding) {
                        view.circlePM130.fill = State.BAD.c
                    } else {
                        view.circlePM130.fill = State.OK.c
                    }

                    sleep(100)
                    a71.readRegister(a71.getRegisterById(Avem7Model.AMPERAGE))
                    if (!a71.isResponding) {
                        view.circleAvem71.fill = State.BAD.c
                    } else {
                        view.circleAvem71.fill = State.OK.c
                    }

                    sleep(100)
                    a72.readRegister(a72.getRegisterById(Avem7Model.AMPERAGE))
                    if (!a72.isResponding) {
                        view.circleAvem72.fill = State.BAD.c
                    } else {
                        view.circleAvem72.fill = State.OK.c
                    }

                    sleep(100)
                    a73.readRegister(a73.getRegisterById(Avem7Model.AMPERAGE))
                    if (!a73.isResponding) {
                        view.circleAvem73.fill = State.BAD.c
                    } else {
                        view.circleAvem73.fill = State.OK.c
                    }

                    sleep(100)
                    a74.readRegister(a74.getRegisterById(Avem7Model.AMPERAGE))
                    if (!a74.isResponding) {
                        view.circleAvem74.fill = State.BAD.c
                    } else {
                        view.circleAvem74.fill = State.OK.c
                    }

                    sleep(100)
                    a75.readRegister(a75.getRegisterById(Avem7Model.AMPERAGE))
                    if (!a75.isResponding) {
                        view.circleAvem75.fill = State.BAD.c
                    } else {
                        view.circleAvem75.fill = State.OK.c
                    }

                    sleep(100)
                    a76.readRegister(a76.getRegisterById(Avem7Model.AMPERAGE))
                    if (!a76.isResponding) {
                        view.circleAvem76.fill = State.BAD.c
                    } else {
                        view.circleAvem76.fill = State.OK.c
                    }

                    sleep(100)
                    kvm1.readRegister(kvm1.getRegisterById(Avem4Model.AMP_VOLTAGE))
                    if (!kvm1.isResponding) {
                        view.circleKVM.fill = State.BAD.c
                    } else {
                        view.circleKVM.fill = State.OK.c
                    }
                }
                val serialPortCP2000 = SerialPort.getCommPorts().filter {
                    it.toString() == "CP2103 USB to CP2000"
                }
                if (serialPortCP2000.isEmpty()) {
                    view.comIndicateCP2000.fill = State.BAD.c
                } else {
                    view.comIndicateCP2000.fill = State.OK.c

                    sleep(100)
                    if (isCP2000Ready) {
                        cp2000.readRegister(cp2000.getRegisterById(DeltaModel.STATUS_REGISTER))
                        if (!cp2000.isResponding) {
                            view.circleCP2000.fill = State.BAD.c
                        } else {
                            view.circleCP2000.fill = State.OK.c
                        }
                    }
                }
                isResponding = kvm1.isResponding
                        && a71.isResponding
                        && a72.isResponding
                        && a73.isResponding
                        && a74.isResponding
                        && a75.isResponding
                        && a76.isResponding
                        && owenPR1.isResponding
                        && owenPR2.isResponding
                        && pm130.isResponding

                sleep(1000)
            }
        }
    }
}
