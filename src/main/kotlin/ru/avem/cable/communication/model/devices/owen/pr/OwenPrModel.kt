package ru.avem.cable.communication.model.devices.owen.pr

import ru.avem.cable.communication.model.DeviceRegister
import ru.avem.cable.communication.model.IDeviceModel

class OwenPrModel : IDeviceModel {
    companion object {
        const val INSTANT_STATES_REGISTER_1 = "INSTANT_STATES_REGISTER_1"
        const val FIXED_STATES_REGISTER_1 = "FIXED_STATES_REGISTER_1"
        const val RES_REGISTER = "RES_REGISTER"
        const val KMS1_REGISTER = "KMS1_REGISTER"
        const val KMS2_REGISTER = "KMS2_REGISTER"
        const val KMS3_REGISTER = "KMS3_REGISTER"
        const val KMS4_REGISTER = "KMS4_REGISTER"
        const val KMS5_REGISTER = "KMS5_REGISTER"
        const val RESET_DOG = "RESET_DOG"
        const val INSTANT_STATES_REGISTER_2 = "INSTANT_STATES_REGISTER_2"
        const val FIXED_STATES_REGISTER_2 = "FIXED_STATES_REGISTER_2"
        const val INSTANT_STATES_REGISTER_3 = "INSTANT_STATES_REGISTER_3"
        const val FIXED_STATES_REGISTER_3 = "FIXED_STATES_REGISTER_3"
        const val INSTANT_STATES_REGISTER_4 = "INSTANT_STATES_REGISTER_4"
        const val FIXED_STATES_REGISTER_4 = "FIXED_STATES_REGISTER_4"
        const val INSTANT_STATES_REGISTER_5 = "INSTANT_STATES_REGISTER_5"
        const val FIXED_STATES_REGISTER_5 = "FIXED_STATES_REGISTER_5"
        const val RESET_TIMER = "RESET_TIMER"
    }

    override val registers: Map<String, DeviceRegister> = mapOf(
        RES_REGISTER to DeviceRegister(514, DeviceRegister.RegisterValueType.SHORT),
        RESET_TIMER to DeviceRegister(522, DeviceRegister.RegisterValueType.SHORT),
        RESET_DOG to DeviceRegister(517, DeviceRegister.RegisterValueType.SHORT),

        INSTANT_STATES_REGISTER_1 to DeviceRegister(512, DeviceRegister.RegisterValueType.SHORT),
        FIXED_STATES_REGISTER_1 to DeviceRegister(513, DeviceRegister.RegisterValueType.SHORT),
        KMS1_REGISTER to DeviceRegister(515, DeviceRegister.RegisterValueType.SHORT),

        INSTANT_STATES_REGISTER_2 to DeviceRegister(520, DeviceRegister.RegisterValueType.SHORT),
        FIXED_STATES_REGISTER_2 to DeviceRegister(521, DeviceRegister.RegisterValueType.SHORT),
        KMS2_REGISTER to DeviceRegister(516, DeviceRegister.RegisterValueType.SHORT),

        INSTANT_STATES_REGISTER_3 to DeviceRegister(523, DeviceRegister.RegisterValueType.SHORT),
        FIXED_STATES_REGISTER_3 to DeviceRegister(524, DeviceRegister.RegisterValueType.SHORT),
        KMS3_REGISTER to DeviceRegister(518, DeviceRegister.RegisterValueType.SHORT),

        INSTANT_STATES_REGISTER_4 to DeviceRegister(525, DeviceRegister.RegisterValueType.SHORT),
        FIXED_STATES_REGISTER_4 to DeviceRegister(526, DeviceRegister.RegisterValueType.SHORT),
        KMS4_REGISTER to DeviceRegister(529, DeviceRegister.RegisterValueType.SHORT),

        INSTANT_STATES_REGISTER_5 to DeviceRegister(527, DeviceRegister.RegisterValueType.SHORT),
        FIXED_STATES_REGISTER_5 to DeviceRegister(528, DeviceRegister.RegisterValueType.SHORT),
        KMS5_REGISTER to DeviceRegister(530, DeviceRegister.RegisterValueType.SHORT)
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")

    var outMask: Short = 0
}