package ru.avem.cable.controllers

import javafx.application.Platform
import javafx.scene.text.Text
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.cable.communication.model.CommunicationModel
import ru.avem.cable.communication.model.devices.avem.avem4.Avem4Controller
import ru.avem.cable.communication.model.devices.avem.avem7.Avem7Controller
import ru.avem.cable.communication.model.devices.delta.DeltaController
import ru.avem.cable.communication.model.devices.owen.pr.OwenPrController
import ru.avem.cable.communication.model.devices.pm130.PM130Controller
import ru.avem.cable.database.entities.Protocol
import ru.avem.cable.utils.LogTag
import ru.avem.cable.view.MainView
import tornadofx.add
import tornadofx.px
import tornadofx.style
import java.text.SimpleDateFormat

class Test1Controller : TestController() {
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

    val controller: MainViewController by inject()
    val mainView: MainView by inject()

    private var logBuffer: String? = null

    @Volatile
    var isExperimentEnded: Boolean = true

    private fun appendOneMessageToLog(tag: LogTag, message: String) {
        if (logBuffer == null || logBuffer != message) {
            logBuffer = message
            appendMessageToLog(tag, message)
        }
    }

    fun appendMessageToLog(tag: LogTag, _msg: String) {
        val msg = Text("${SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis())} | $_msg")
        msg.style {
            fill = when (tag) {
                LogTag.MESSAGE -> tag.c
                LogTag.ERROR -> tag.c
                LogTag.DEBUG -> tag.c
            }
            fontSize = 26.px
        }

        Platform.runLater {
            mainView.vBoxLog.add(msg)
        }
    }

    private fun startPollDevices() {

    }

    fun startTest() {

    }


}
