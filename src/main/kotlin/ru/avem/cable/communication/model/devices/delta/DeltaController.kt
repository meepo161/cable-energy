package ru.avem.cable.communication.model.devices.delta

import ru.avem.cable.communication.model.DeviceRegister
import ru.avem.cable.communication.model.IDeviceController
import ru.avem.cable.communication.model.devices.delta.DeltaModel.Companion.CONTROL_REGISTER
import ru.avem.cable.communication.model.devices.delta.DeltaModel.Companion.CURRENT_FREQUENCY_OUTPUT_REGISTER
import ru.avem.cable.communication.model.devices.delta.DeltaModel.Companion.MAX_FREQUENCY_REGISTER
import ru.avem.cable.communication.model.devices.delta.DeltaModel.Companion.MAX_VOLTAGE_REGISTER
import ru.avem.cable.communication.model.devices.delta.DeltaModel.Companion.NOM_FREQUENCY_REGISTER
import ru.avem.cable.communication.model.devices.delta.DeltaModel.Companion.POINT_1_FREQUENCY_REGISTER
import ru.avem.cable.communication.model.devices.delta.DeltaModel.Companion.POINT_1_VOLTAGE_REGISTER
import ru.avem.cable.communication.model.devices.delta.DeltaModel.Companion.POINT_2_FREQUENCY_REGISTER
import ru.avem.cable.communication.model.devices.delta.DeltaModel.Companion.POINT_2_VOLTAGE_REGISTER
import ru.avem.cable.communication.utils.TypeByteOrder
import ru.avem.cable.communication.utils.allocateOrderedByteBuffer
import ru.avem.cable.utils.Measuring.VOLT
import ru.avem.kserialpooler.communication.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.communication.adapters.utils.ModbusRegister
import ru.avem.kserialpooler.communication.utils.TransportException
import java.nio.ByteBuffer
import java.nio.ByteOrder


class DeltaController(
    override val name: String,
    override val protocolAdapter: ModbusRTUAdapter,
    override val id: Byte
) : IDeviceController {
    override var isResponding = false
    private val model = DeltaModel()
    override var requestTotalCount = 0
    override var requestSuccessCount = 0
    override val pollingRegisters = mutableListOf<DeviceRegister>()
    override val writingMutex = Any()
    override val writingRegisters = mutableListOf<Pair<DeviceRegister, Number>>()
    override val pollingMutex = Any()

    init {
        protocolAdapter.connection.connect()
    }

    override fun readRegister(register: DeviceRegister) {
        isResponding = try {
            transactionWithAttempts {
                when (register.valueType) {
                    DeviceRegister.RegisterValueType.SHORT -> {
                        val modbusRegister =
                            protocolAdapter.readHoldingRegisters(id, register.address, 1).map(ModbusRegister::toShort)
                        register.value = modbusRegister.first().toDouble()
                    }
                    DeviceRegister.RegisterValueType.FLOAT -> {
                        val modbusRegister =
                            protocolAdapter.readHoldingRegisters(id, register.address, 2).map(ModbusRegister::toShort)
                        register.value =
                            ru.avem.kserialpooler.communication.utils.allocateOrderedByteBuffer(
                                modbusRegister,
                                ru.avem.kserialpooler.communication.utils.TypeByteOrder.BIG_ENDIAN,
                                4
                            ).float.toDouble()
                    }
                    DeviceRegister.RegisterValueType.INT32 -> {
                        val modbusRegister =
                            protocolAdapter.readHoldingRegisters(id, register.address, 2).map(ModbusRegister::toShort)
                        register.value =
                            ru.avem.kserialpooler.communication.utils.allocateOrderedByteBuffer(
                                modbusRegister,
                                ru.avem.kserialpooler.communication.utils.TypeByteOrder.BIG_ENDIAN,
                                4
                            ).int.toDouble()
                    }
                }
            }
            true
        } catch (e: TransportException) {
            false
        }
    }

    override fun readAllRegisters() {
        model.registers.values.forEach {
            readRegister(it)
        }
    }

    @Synchronized
    override fun <T : Number> writeRegister(register: DeviceRegister, value: T) {
        isResponding = try {
            when (value) {
                is Float -> {
                    val bb = ByteBuffer.allocate(4).putFloat(value).order(ByteOrder.BIG_ENDIAN)
                    val registers = listOf(ModbusRegister(bb.getShort(2)), ModbusRegister(bb.getShort(0)))
                    transactionWithAttempts {
                        protocolAdapter.presetMultipleRegisters(id, register.address, registers)
                    }
                }
                is Int -> {
                    val bb = ByteBuffer.allocate(4).putInt(value).order(ByteOrder.BIG_ENDIAN)
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
        } catch (e: TransportException) {
            false
        }
    }

    override fun writeRegisters(register: DeviceRegister, values: List<Short>) {
        val registers = values.map { ModbusRegister(it) }
        transactionWithAttempts {
            protocolAdapter.presetMultipleRegisters(id, register.address, registers)
        }
    }

    override fun checkResponsibility() {
        try {
            model.registers.values.firstOrNull()?.let {
                readRegister(it)
            }
        } catch (ignored: TransportException) {
        }
    }

    override fun getRegisterById(idRegister: String) = model.getRegisterById(idRegister)

    enum class Direction {
        FORWARD,
        REVERSE
    }

    fun startObject(direction: Direction = Direction.FORWARD) {
        if (direction == Direction.FORWARD) {
            writeRegister(getRegisterById(CONTROL_REGISTER), (0b010010).toShort())
        }
        if (direction == Direction.REVERSE) {
            writeRegister(getRegisterById(CONTROL_REGISTER), (0b100010).toShort())
        }
    }

    fun stopObject() {
        writeRegister(getRegisterById(CONTROL_REGISTER), (0b1).toShort())
    }

    fun setObjectParams(fOut: Number, voltageP1: Number, fP1: Number) {
        try {
            writeRegister(getRegisterById(MAX_VOLTAGE_REGISTER), (400.v() + 1).toShort())
            writeRegister(getRegisterById(MAX_FREQUENCY_REGISTER), (50.hz() + 1).toShort())
            writeRegister(getRegisterById(NOM_FREQUENCY_REGISTER), (50.hz() + 1).toShort())

            writeRegister(getRegisterById(POINT_1_VOLTAGE_REGISTER), voltageP1.v())
            writeRegister(getRegisterById(POINT_1_FREQUENCY_REGISTER), fP1.hz())

            writeRegister(getRegisterById(POINT_2_VOLTAGE_REGISTER), 1.v())
            writeRegister(getRegisterById(POINT_2_FREQUENCY_REGISTER), 1.hz())

            writeRegister(getRegisterById(CURRENT_FREQUENCY_OUTPUT_REGISTER), fOut.hz())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun Number.hz(): Short = (this.toDouble() * 100).toInt().toShort()
    private fun Number.v(): Short = (this.toDouble() * 10).toInt().toShort()

    fun setObjectUMax(voltageMax: Number) {
        writeRegister(getRegisterById(POINT_1_VOLTAGE_REGISTER), voltageMax.v())
    }
}

