package ru.avem.cable.utils

import javafx.scene.paint.Color
import tornadofx.c

enum class ExperimentType(val type: String) {
    AC("Переменный ток") {
        override fun toString() = type
    },
    DC("Постоянный ток") {
        override fun toString() = type
    }
}

enum class LogTag(val c: Color) {
    MESSAGE(c("#5dbb25")),
    ERROR(c("#ff1b1b")),
    DEBUG(c("#33ccff"))
}

enum class State(val c: Color) {
    OK(c("#00dd00")),
    INTERMEDIATE(c("#6f6fff")),
    BAD(c("#fa1414")),
}

object Measuring {
    const val VOLT = 10
    const val HZ = 100
}

const val KTR = 31

const val YES = "ДА"
const val NO = "НЕТ"

const val SHEET_PASSWORD = "444488888888"

const val KM2_STATE = 0b1
const val A1_PROTECTION_STATE = 0b10
const val A2_PROTECTION_STATE = 0b100
const val A3_PROTECTION_STATE = 0b1000
const val DOOR_STATE = 0b10000

const val BREAK_IKAS = 1.0E9
