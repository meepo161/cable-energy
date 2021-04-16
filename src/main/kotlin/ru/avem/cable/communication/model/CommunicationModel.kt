package ru.avem.cable.communication.model

import ru.avem.kserialpooler.communication.Connection
import ru.avem.kserialpooler.communication.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.communication.utils.SerialParameters
import ru.avem.cable.app.Cable.Companion.isAppRunning
import ru.avem.cable.communication.model.devices.avem.avem4.Avem4Controller
import ru.avem.cable.communication.model.devices.avem.avem7.Avem7Controller
import ru.avem.cable.communication.model.devices.delta.DeltaController
import ru.avem.cable.communication.model.devices.owen.pr.OwenPrController
import ru.avem.cable.communication.model.devices.pm130.PM130Controller
import java.lang.Thread.sleep
import kotlin.concurrent.thread

object CommunicationModel {
    @Suppress("UNUSED_PARAMETER")
    enum class DeviceID(description: String) {
        DD1("ПР1"),
        DD2("ПР2"),
        PM130("PM130"),
        CP2000("CP2000"),
        A71("Ток утечки 1"),
        A72("Ток утечки 2"),
        A73("Ток утечки 3"),
        A74("Ток утечки 4"),
        A75("Ток утечки 5"),
        A76("Ток утечки 6"),
        KVM1("Напряжение на ОИ")
    }

    var isConnected = false
    var isConnectedCP2000 = false

    private val connection = Connection(
        adapterName = "CP2103 USB to RS-485",
        serialParameters = SerialParameters(8, 0, 1, 38400),
        timeoutRead = 100,
        timeoutWrite = 100
    ).apply {
        connect()
        isConnected = true
    }

    private val connectionCP2000 = Connection(
        adapterName = "CP2103 USB to CP2000",
        serialParameters = SerialParameters(8, 0, 1, 38400),
        timeoutRead = 100,
        timeoutWrite = 100
    ).apply {
        connect()
        isConnectedCP2000 = true
    }

    private val modbusAdapter = ModbusRTUAdapter(connection)
    private val modbusAdapterCP2000 = ModbusRTUAdapter(connectionCP2000)

    private val deviceControllers: Map<DeviceID, IDeviceController> = mapOf(
        DeviceID.DD1 to OwenPrController(DeviceID.DD1.toString(), modbusAdapter, 1),
        DeviceID.DD2 to OwenPrController(DeviceID.DD2.toString(), modbusAdapter, 2),
        DeviceID.PM130 to PM130Controller(DeviceID.PM130.toString(), modbusAdapter, 3),
        DeviceID.A71 to Avem7Controller(DeviceID.A71.toString(), modbusAdapter, 4),
        DeviceID.A72 to Avem7Controller(DeviceID.A72.toString(), modbusAdapter, 5),
        DeviceID.A73 to Avem7Controller(DeviceID.A73.toString(), modbusAdapter, 6),
        DeviceID.A74 to Avem7Controller(DeviceID.A74.toString(), modbusAdapter, 7),
        DeviceID.A75 to Avem7Controller(DeviceID.A75.toString(), modbusAdapter, 8),
        DeviceID.A76 to Avem7Controller(DeviceID.A76.toString(), modbusAdapter, 9),
        DeviceID.KVM1 to Avem4Controller(DeviceID.KVM1.toString(), modbusAdapter, 10),
        DeviceID.CP2000 to DeltaController(DeviceID.CP2000.toString(), modbusAdapterCP2000, 11)
    )

    init {
        thread(isDaemon = true) {
            while (isAppRunning) {
                if (isConnected) {
                    deviceControllers.values.forEach {
                        it.readPollingRegisters()
                    }
                }
                sleep(100)
            }
        }
        thread(isDaemon = true) {
            while (isAppRunning) {
                if (isConnected) {
                    deviceControllers.values.forEach {
                        it.writeWritingRegisters()
                    }
                }
                sleep(100)
            }
        }
    }

    fun getDeviceById(deviceID: DeviceID) = deviceControllers[deviceID] ?: error("Не определено $deviceID")

    fun startPoll(deviceID: DeviceID, registerID: String, block: (Number) -> Unit) {
        val device = getDeviceById(deviceID)
        val register = device.getRegisterById(registerID)
        register.addObserver { _, arg ->
            block(arg as Number)
        }
        device.addPollingRegister(register)
    }

    fun clearPollingRegisters() {
        deviceControllers.values.forEach(IDeviceController::removeAllPollingRegisters)
    }

    fun removePollingRegister(deviceID: DeviceID, registerID: String) {
        val device = getDeviceById(deviceID)
        val register = device.getRegisterById(registerID)
        register.deleteObservers()
        device.removePollingRegister(register)
    }

    fun checkDevices(): List<DeviceID> {
        deviceControllers.values.forEach(IDeviceController::checkResponsibility)
        return deviceControllers.filter { !it.value.isResponding }.keys.toList()
    }

    fun addWritingRegister(deviceID: DeviceID, registerID: String, value: Number) {
        val device = getDeviceById(deviceID)
        val register = device.getRegisterById(registerID)
        device.addWritingRegister(register to value)
    }
}
