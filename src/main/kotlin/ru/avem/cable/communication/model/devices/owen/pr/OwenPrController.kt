package ru.avem.cable.communication.model.devices.owen.pr

import ru.avem.cable.communication.model.DeviceRegister
import ru.avem.cable.communication.model.IDeviceController
import ru.avem.cable.utils.sleep
import ru.avem.kserialpooler.communication.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.communication.adapters.utils.ModbusRegister
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.math.pow

class OwenPrController(
    override val name: String,
    override val protocolAdapter: ModbusRTUAdapter,
    override val id: Byte
) : IDeviceController {
    val model = OwenPrModel()
    override var isResponding = false
    override var requestTotalCount = 0
    override var requestSuccessCount = 0
    override val pollingRegisters = mutableListOf<DeviceRegister>()
    override val writingMutex = Any()
    override val writingRegisters = mutableListOf<Pair<DeviceRegister, Number>>()
    override val pollingMutex = Any()

    var outMask1: Short = 0
    var outMask2: Short = 0
    var outMask3: Short = 0
    var outMask4: Short = 0
    var outMask5: Short = 0

    companion object {
        const val TRIG_RESETER: Short = 0xFFFF.toShort()
        const val WD_RESETER: Short = 0b10
    }

    override fun readRegister(register: DeviceRegister) {
        isResponding = try {
            transactionWithAttempts {
                val modbusRegister =
                    protocolAdapter.readHoldingRegisters(id, register.address, 1).map(ModbusRegister::toShort)
                register.value = modbusRegister.first()
            }
            true
        } catch (e: ru.avem.kserialpooler.communication.utils.TransportException) {
            false
        }
    }

    override fun readAllRegisters() {
        model.registers.values.forEach {
            readRegister(it)
        }
    }

    override fun <T : Number> writeRegister(register: DeviceRegister, value: T) {
        isResponding = try {
            when (value) {
                is Float -> {
                    val bb = ByteBuffer.allocate(4).putFloat(value).order(ByteOrder.LITTLE_ENDIAN)
                    val registers = listOf(ModbusRegister(bb.getShort(2)), ModbusRegister(bb.getShort(0)))
                    transactionWithAttempts {
                        protocolAdapter.presetMultipleRegisters(id, register.address, registers)
                    }
                }
                is Int -> {
                    val bb = ByteBuffer.allocate(4).putInt(value).order(ByteOrder.LITTLE_ENDIAN)
                    val registers = listOf(ModbusRegister(bb.getShort(2)), ModbusRegister(bb.getShort(0)))
                    transactionWithAttempts {
                        protocolAdapter.presetMultipleRegisters(id, register.address, registers)
                    }
                }
                is Short -> {
                    transactionWithAttempts {
                        protocolAdapter.presetSingleRegister(id, register.address, ModbusRegister(value))
                    }
                }
                else -> {
                    throw UnsupportedOperationException("Method can handle only with Float, Int and Short")
                }
            }
            true
        } catch (e: ru.avem.kserialpooler.communication.utils.TransportException) {
            false
        }
    }

    override fun writeRegisters(register: DeviceRegister, values: List<Short>) {
        val registers = values.map { ModbusRegister(it) }
        isResponding = try {
            transactionWithAttempts {
                protocolAdapter.presetMultipleRegisters(id, register.address, registers)
            }
            true
        } catch (e: ru.avem.kserialpooler.communication.utils.TransportException) {
            false
        }
    }

    override fun checkResponsibility() {
        model.registers.values.firstOrNull()?.let {
            readRegister(it)
        }
    }

    override fun getRegisterById(idRegister: String) = model.getRegisterById(idRegister)

    fun onBitInRegister1(register: DeviceRegister, bitPosition: Short) {
        val nor = bitPosition - 1
        val outMask1Old = outMask1
        outMask1 = outMask1 or 2.0.pow(nor).toInt().toShort()
        if (outMask1Old != outMask1) {
            writeRegister(register, outMask1)
            sleep(300)
        }
    }

    fun onBitInRegister2(register: DeviceRegister, bitPosition: Short) {
        val nor = bitPosition - 1
        val outMask2Old = outMask2
        outMask2 = outMask2 or 2.0.pow(nor).toInt().toShort()
        if (outMask2Old != outMask2) {
            writeRegister(register, outMask2)
            sleep(300)
        }
    }

    fun onBitInRegister3(register: DeviceRegister, bitPosition: Short) {
        val nor = bitPosition - 1
        val outMask3Old = outMask3
        outMask3 = outMask3 or 2.0.pow(nor).toInt().toShort()
        if (outMask3Old != outMask3) {
            writeRegister(register, outMask3)
            sleep(300)
        }
    }

    fun onBitInRegister4(register: DeviceRegister, bitPosition: Short) {
        val nor = bitPosition - 1
        val outMask4Old = outMask4
        outMask4 = outMask4 or 2.0.pow(nor).toInt().toShort()
        if (outMask4Old != outMask4) {
            writeRegister(register, outMask4)
            sleep(300)
        }
    }

    fun onBitInRegister5(register: DeviceRegister, bitPosition: Short) {
        val nor = bitPosition - 1
        val outMask5Old = outMask5
        outMask5 = outMask5 or 2.0.pow(nor).toInt().toShort()
        if (outMask5Old != outMask5) {
            writeRegister(register, outMask5)
            sleep(300)
        }
    }

    fun offBitInRegister1(register: DeviceRegister, bitPosition: Short) {
        val nor = bitPosition - 1
        val outMask1Old = outMask1
        outMask1 = outMask1 and 2.0.pow(nor).toInt().inv().toShort()
        if (outMask1Old != outMask1) {
            writeRegister(register, outMask1)
            sleep(300)
        }
    }

    fun offBitInRegister2(register: DeviceRegister, bitPosition: Short) {
        val nor = bitPosition - 1
        val outMask2Old = outMask2
        outMask2 = outMask2 and 2.0.pow(nor).toInt().inv().toShort()
        if (outMask2Old != outMask2) {
            writeRegister(register, outMask2)
            sleep(300)
        }
    }

    fun offBitInRegister3(register: DeviceRegister, bitPosition: Short) {
        val nor = bitPosition - 1
        val outMask3Old = outMask3
        outMask3 = outMask3 and 2.0.pow(nor).toInt().inv().toShort()
        if (outMask3Old != outMask3) {
            writeRegister(register, outMask3)
            sleep(300)
        }
    }

    fun offBitInRegister4(register: DeviceRegister, bitPosition: Short) {
        val nor = bitPosition - 1
        val outMask4Old = outMask4
        outMask4 = outMask4 and 2.0.pow(nor).toInt().inv().toShort()
        if (outMask4Old != outMask4) {
            writeRegister(register, outMask4)
            sleep(300)
        }
    }

    fun offBitInRegister5(register: DeviceRegister, bitPosition: Short) {
        val nor = bitPosition - 1
        val outMask5Old = outMask5
        outMask5 = outMask5 and 2.0.pow(nor).toInt().inv().toShort()
        if (outMask5Old != outMask5) {
            writeRegister(register, outMask5)
            sleep(300)
        }
    }


    fun initOwenPR() {
        writeRegister(getRegisterById(OwenPrModel.RES_REGISTER), 0)
        writeRegister(getRegisterById(OwenPrModel.RES_REGISTER), 1)
    }

    fun resetKMS() {
        writeRegister(getRegisterById(OwenPrModel.KMS1_REGISTER), 0)
        writeRegister(getRegisterById(OwenPrModel.KMS2_REGISTER), 0)
        writeRegister(getRegisterById(OwenPrModel.KMS3_REGISTER), 0)
        writeRegister(getRegisterById(OwenPrModel.KMS4_REGISTER), 0)
        writeRegister(getRegisterById(OwenPrModel.KMS5_REGISTER), 0)
    }

    fun on1_1() {
        onBitInRegister1(getRegisterById(OwenPrModel.KMS1_REGISTER), 1)
    }

    fun on1_2() {
        onBitInRegister1(getRegisterById(OwenPrModel.KMS1_REGISTER), 2)
    }

    fun on1_3() {
        onBitInRegister1(getRegisterById(OwenPrModel.KMS1_REGISTER), 3)
    }

    fun on1_4() {
        onBitInRegister1(getRegisterById(OwenPrModel.KMS1_REGISTER), 4)
    }

    fun on1_5() {
        onBitInRegister1(getRegisterById(OwenPrModel.KMS1_REGISTER), 5)
    }

    fun on1_6() {
        onBitInRegister1(getRegisterById(OwenPrModel.KMS1_REGISTER), 6)
    }

    fun on1_7() {
        onBitInRegister1(getRegisterById(OwenPrModel.KMS1_REGISTER), 7)
    }

    fun on1_8() {
        onBitInRegister1(getRegisterById(OwenPrModel.KMS1_REGISTER), 8)
    }

    fun on2_1() {
        onBitInRegister2(getRegisterById(OwenPrModel.KMS2_REGISTER), 1)
    }

    fun on2_2() {
        onBitInRegister2(getRegisterById(OwenPrModel.KMS2_REGISTER), 2)
    }

    fun on2_3() {
        onBitInRegister2(getRegisterById(OwenPrModel.KMS2_REGISTER), 3)
    }

    fun on2_4() {
        onBitInRegister2(getRegisterById(OwenPrModel.KMS2_REGISTER), 4)
    }

    fun on2_5() {
        onBitInRegister2(getRegisterById(OwenPrModel.KMS2_REGISTER), 5)
    }

    fun on2_6() {
        onBitInRegister2(getRegisterById(OwenPrModel.KMS2_REGISTER), 6)
    }

    fun on2_7() {
        onBitInRegister2(getRegisterById(OwenPrModel.KMS2_REGISTER), 7)
    }

    fun on2_8() {
        onBitInRegister2(getRegisterById(OwenPrModel.KMS2_REGISTER), 8)
    }

    fun on3_1() {
        onBitInRegister3(getRegisterById(OwenPrModel.KMS3_REGISTER), 1)
    }

    fun on3_2() {
        onBitInRegister3(getRegisterById(OwenPrModel.KMS3_REGISTER), 2)
    }

    fun on3_3() {
        onBitInRegister3(getRegisterById(OwenPrModel.KMS3_REGISTER), 3)
    }

    fun on3_4() {
        onBitInRegister3(getRegisterById(OwenPrModel.KMS3_REGISTER), 4)
    }

    fun on3_5() {
        onBitInRegister3(getRegisterById(OwenPrModel.KMS3_REGISTER), 5)
    }

    fun on3_6() {
        onBitInRegister3(getRegisterById(OwenPrModel.KMS3_REGISTER), 6)
    }

    fun on3_7() {
        onBitInRegister3(getRegisterById(OwenPrModel.KMS3_REGISTER), 7)
    }

    fun on3_8() {
        onBitInRegister3(getRegisterById(OwenPrModel.KMS3_REGISTER), 8)
    }

    fun on4_1() {
        onBitInRegister4(getRegisterById(OwenPrModel.KMS4_REGISTER), 1)
    }

    fun on4_2() {
        onBitInRegister4(getRegisterById(OwenPrModel.KMS4_REGISTER), 2)
    }

    fun on4_3() {
        onBitInRegister4(getRegisterById(OwenPrModel.KMS4_REGISTER), 3)
    }

    fun on4_4() {
        onBitInRegister4(getRegisterById(OwenPrModel.KMS4_REGISTER), 4)
    }

    fun on4_5() {
        onBitInRegister4(getRegisterById(OwenPrModel.KMS4_REGISTER), 5)
    }

    fun on4_6() {
        onBitInRegister4(getRegisterById(OwenPrModel.KMS4_REGISTER), 6)
    }

    fun on4_7() {
        onBitInRegister4(getRegisterById(OwenPrModel.KMS4_REGISTER), 7)
    }

    fun on4_8() {
        onBitInRegister4(getRegisterById(OwenPrModel.KMS4_REGISTER), 8)
    }

    fun on5_1() {
        onBitInRegister5(getRegisterById(OwenPrModel.KMS5_REGISTER), 1)
    }

    fun on5_2() {
        onBitInRegister5(getRegisterById(OwenPrModel.KMS5_REGISTER), 2)
    }

    fun on5_3() {
        onBitInRegister5(getRegisterById(OwenPrModel.KMS5_REGISTER), 3)
    }

    fun on5_4() {
        onBitInRegister5(getRegisterById(OwenPrModel.KMS5_REGISTER), 4)
    }

    fun on5_5() {
        onBitInRegister5(getRegisterById(OwenPrModel.KMS5_REGISTER), 5)
    }

    fun on5_6() {
        onBitInRegister5(getRegisterById(OwenPrModel.KMS5_REGISTER), 6)
    }

    fun on5_7() {
        onBitInRegister5(getRegisterById(OwenPrModel.KMS5_REGISTER), 7)
    }

    fun on5_8() {
        onBitInRegister5(getRegisterById(OwenPrModel.KMS5_REGISTER), 8)
    }

    fun off1_1() {
        offBitInRegister1(getRegisterById(OwenPrModel.KMS1_REGISTER), 1)
    }

    fun off1_2() {
        offBitInRegister1(getRegisterById(OwenPrModel.KMS1_REGISTER), 2)
    }

    fun off1_3() {
        offBitInRegister1(getRegisterById(OwenPrModel.KMS1_REGISTER), 3)
    }

    fun off1_4() {
        offBitInRegister1(getRegisterById(OwenPrModel.KMS1_REGISTER), 4)
    }

    fun off1_5() {
        offBitInRegister1(getRegisterById(OwenPrModel.KMS1_REGISTER), 5)
    }

    fun off1_6() {
        offBitInRegister1(getRegisterById(OwenPrModel.KMS1_REGISTER), 6)
    }

    fun off1_7() {
        offBitInRegister1(getRegisterById(OwenPrModel.KMS1_REGISTER), 7)
    }

    fun off1_8() {
        offBitInRegister1(getRegisterById(OwenPrModel.KMS1_REGISTER), 8)
    }

    fun off2_1() {
        offBitInRegister2(getRegisterById(OwenPrModel.KMS2_REGISTER), 1)
    }

    fun off2_2() {
        offBitInRegister2(getRegisterById(OwenPrModel.KMS2_REGISTER), 2)
    }

    fun off2_3() {
        offBitInRegister2(getRegisterById(OwenPrModel.KMS2_REGISTER), 3)
    }

    fun off2_4() {
        offBitInRegister2(getRegisterById(OwenPrModel.KMS2_REGISTER), 4)
    }

    fun off2_5() {
        offBitInRegister2(getRegisterById(OwenPrModel.KMS2_REGISTER), 5)
    }

    fun off2_6() {
        offBitInRegister2(getRegisterById(OwenPrModel.KMS2_REGISTER), 6)
    }

    fun off2_7() {
        offBitInRegister2(getRegisterById(OwenPrModel.KMS2_REGISTER), 7)
    }

    fun off2_8() {
        offBitInRegister2(getRegisterById(OwenPrModel.KMS2_REGISTER), 8)
    }

    fun off3_1() {
        offBitInRegister3(getRegisterById(OwenPrModel.KMS3_REGISTER), 1)
    }

    fun off3_2() {
        offBitInRegister3(getRegisterById(OwenPrModel.KMS3_REGISTER), 2)
    }

    fun off3_3() {
        offBitInRegister3(getRegisterById(OwenPrModel.KMS3_REGISTER), 3)
    }

    fun off3_4() {
        offBitInRegister3(getRegisterById(OwenPrModel.KMS3_REGISTER), 4)
    }

    fun off3_5() {
        offBitInRegister3(getRegisterById(OwenPrModel.KMS3_REGISTER), 5)
    }

    fun off3_6() {
        offBitInRegister3(getRegisterById(OwenPrModel.KMS3_REGISTER), 6)
    }

    fun off3_7() {
        offBitInRegister3(getRegisterById(OwenPrModel.KMS3_REGISTER), 7)
    }

    fun off3_8() {
        offBitInRegister3(getRegisterById(OwenPrModel.KMS3_REGISTER), 8)
    }

    fun off4_1() {
        offBitInRegister4(getRegisterById(OwenPrModel.KMS4_REGISTER), 1)
    }

    fun off4_2() {
        offBitInRegister4(getRegisterById(OwenPrModel.KMS4_REGISTER), 2)
    }

    fun off4_3() {
        offBitInRegister4(getRegisterById(OwenPrModel.KMS4_REGISTER), 3)
    }

    fun off4_4() {
        offBitInRegister4(getRegisterById(OwenPrModel.KMS4_REGISTER), 4)
    }

    fun off4_5() {
        offBitInRegister4(getRegisterById(OwenPrModel.KMS4_REGISTER), 5)
    }

    fun off4_6() {
        offBitInRegister4(getRegisterById(OwenPrModel.KMS4_REGISTER), 6)
    }

    fun off4_7() {
        offBitInRegister4(getRegisterById(OwenPrModel.KMS4_REGISTER), 7)
    }

    fun off4_8() {
        offBitInRegister4(getRegisterById(OwenPrModel.KMS4_REGISTER), 8)
    }

    fun off5_1() {
        offBitInRegister5(getRegisterById(OwenPrModel.KMS5_REGISTER), 1)
    }

    fun off5_2() {
        offBitInRegister5(getRegisterById(OwenPrModel.KMS5_REGISTER), 2)
    }

    fun off5_3() {
        offBitInRegister5(getRegisterById(OwenPrModel.KMS5_REGISTER), 3)
    }

    fun off5_4() {
        offBitInRegister5(getRegisterById(OwenPrModel.KMS5_REGISTER), 4)
    }

    fun off5_5() {
        offBitInRegister5(getRegisterById(OwenPrModel.KMS5_REGISTER), 5)
    }

    fun off5_6() {
        offBitInRegister5(getRegisterById(OwenPrModel.KMS5_REGISTER), 6)
    }

    fun off5_7() {
        offBitInRegister5(getRegisterById(OwenPrModel.KMS5_REGISTER), 7)
    }

    fun off5_8() {
        offBitInRegister5(getRegisterById(OwenPrModel.KMS5_REGISTER), 8)
    }

    fun offAllKMs1() {
        outMask1 = 0
        writeRegister(getRegisterById(OwenPrModel.KMS1_REGISTER), outMask1)
        outMask2 = 0
        writeRegister(getRegisterById(OwenPrModel.KMS2_REGISTER), outMask2)
        outMask3 = 0
        writeRegister(getRegisterById(OwenPrModel.KMS3_REGISTER), outMask3)
        outMask4 = 0
        writeRegister(getRegisterById(OwenPrModel.KMS4_REGISTER), outMask4)
        outMask5 = 0
        writeRegister(getRegisterById(OwenPrModel.KMS5_REGISTER), outMask5)
    }

    fun offAllKMs2() {
        outMask1 = 0
        writeRegister(getRegisterById(OwenPrModel.KMS1_REGISTER), outMask1)
        outMask2 = 0
        writeRegister(getRegisterById(OwenPrModel.KMS2_REGISTER), outMask2)
    }
}
