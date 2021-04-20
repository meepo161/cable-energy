package ru.avem.cable.utils

import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window
import ru.avem.cable.app.Cable
import ru.avem.cable.view.Styles
import tornadofx.*
import java.awt.Desktop
import java.io.*
import java.nio.ByteBuffer
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

fun sleep(mills: Long) {
    Thread.sleep(mills)
}

fun formatRealNumber(num: Double): Double {
    val absNum = abs(num)

    var format = "%.0f"
    when {
        absNum == 0.0 -> format = "%.0f"
        absNum < 0.1f -> format = "%.5f"
        absNum < 1f -> format = "%.4f"
        absNum < 10f -> format = "%.3f"
        absNum < 100f -> format = "%.2f"
        absNum < 1000f -> format = "%.1f"
        absNum < 10000f -> format = "%.0f"
    }
    return String.format(Locale.US, format, num).toDouble()
}

fun openFile(file: File) {
    try {
        Desktop.getDesktop().open(file)
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun copyFileFromStream(_inputStream: InputStream, dest: File) {
    _inputStream.use { inputStream ->
        try {
            val fileOutputStream = FileOutputStream(dest)
            val buffer = ByteArray(1024)
            var length = inputStream.read(buffer)
            while (length > 0) {
                fileOutputStream.write(buffer, 0, length)
                length = inputStream.read(buffer)
            }
        } catch (e: FileNotFoundException) {
        }
    }
}

fun ByteArray.toHexString(numBytesRead: Int = this.size): String {
    return buildString {
        for ((i, b) in this@toHexString.withIndex()) {
            if (i == numBytesRead) break
            append(Integer.toHexString(b.toInt() and 0xFF).padStart(2, '0') + ' ')
        }
    }.toUpperCase().trim()
}

fun TextField.callKeyBoard() {
    onTouchPressed = EventHandler {
        Desktop.getDesktop()
            .open(Paths.get("C:/Program Files/Common Files/Microsoft Shared/ink/TabTip.exe").toFile())
        requestFocus()
    }
}

fun Int.getRange(offset: Int, length: Int = 1) = (shr(offset) and getMask(length))
private fun getMask(length: Int) = (0xFFFFFFFF).shr(32 - length).toInt()

fun intToByteArray(i: Int): ByteArray? {
    val convertBuffer = ByteBuffer.allocate(4)
    convertBuffer.clear()
    return convertBuffer.putInt(i).array()
}

fun floatToByteArray(f: Float): ByteArray? {
    val convertBuffer = ByteBuffer.allocate(4)
    convertBuffer.clear()
    return convertBuffer.putFloat(f).array()
}

var transitionLeft = ViewTransition.Fade(1.seconds).apply {
    setup {
        style = "-fx-background-color: #444"
    }
}
var transitionRight = ViewTransition.Fade(1.seconds).apply {
    setup {
        style = "-fx-background-color: #444"
    }
}
private val auchCRCHi = shortArrayOf(
    0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
    0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
    0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0,
    0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
    0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1,
    0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41,
    0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1,
    0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
    0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
    0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40,
    0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1,
    0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
    0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
    0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40,
    0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0,
    0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
    0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
    0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
    0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0,
    0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
    0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0,
    0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40,
    0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1,
    0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
    0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
    0x80, 0x41, 0x00, 0xC1, 0x81, 0x40
)

/* Table of CRC values for low-order byte */
private val auchCRCLo = shortArrayOf(
    0x00, 0xC0, 0xC1, 0x01, 0xC3, 0x03, 0x02, 0xC2, 0xC6, 0x06,
    0x07, 0xC7, 0x05, 0xC5, 0xC4, 0x04, 0xCC, 0x0C, 0x0D, 0xCD,
    0x0F, 0xCF, 0xCE, 0x0E, 0x0A, 0xCA, 0xCB, 0x0B, 0xC9, 0x09,
    0x08, 0xC8, 0xD8, 0x18, 0x19, 0xD9, 0x1B, 0xDB, 0xDA, 0x1A,
    0x1E, 0xDE, 0xDF, 0x1F, 0xDD, 0x1D, 0x1C, 0xDC, 0x14, 0xD4,
    0xD5, 0x15, 0xD7, 0x17, 0x16, 0xD6, 0xD2, 0x12, 0x13, 0xD3,
    0x11, 0xD1, 0xD0, 0x10, 0xF0, 0x30, 0x31, 0xF1, 0x33, 0xF3,
    0xF2, 0x32, 0x36, 0xF6, 0xF7, 0x37, 0xF5, 0x35, 0x34, 0xF4,
    0x3C, 0xFC, 0xFD, 0x3D, 0xFF, 0x3F, 0x3E, 0xFE, 0xFA, 0x3A,
    0x3B, 0xFB, 0x39, 0xF9, 0xF8, 0x38, 0x28, 0xE8, 0xE9, 0x29,
    0xEB, 0x2B, 0x2A, 0xEA, 0xEE, 0x2E, 0x2F, 0xEF, 0x2D, 0xED,
    0xEC, 0x2C, 0xE4, 0x24, 0x25, 0xE5, 0x27, 0xE7, 0xE6, 0x26,
    0x22, 0xE2, 0xE3, 0x23, 0xE1, 0x21, 0x20, 0xE0, 0xA0, 0x60,
    0x61, 0xA1, 0x63, 0xA3, 0xA2, 0x62, 0x66, 0xA6, 0xA7, 0x67,
    0xA5, 0x65, 0x64, 0xA4, 0x6C, 0xAC, 0xAD, 0x6D, 0xAF, 0x6F,
    0x6E, 0xAE, 0xAA, 0x6A, 0x6B, 0xAB, 0x69, 0xA9, 0xA8, 0x68,
    0x78, 0xB8, 0xB9, 0x79, 0xBB, 0x7B, 0x7A, 0xBA, 0xBE, 0x7E,
    0x7F, 0xBF, 0x7D, 0xBD, 0xBC, 0x7C, 0xB4, 0x74, 0x75, 0xB5,
    0x77, 0xB7, 0xB6, 0x76, 0x72, 0xB2, 0xB3, 0x73, 0xB1, 0x71,
    0x70, 0xB0, 0x50, 0x90, 0x91, 0x51, 0x93, 0x53, 0x52, 0x92,
    0x96, 0x56, 0x57, 0x97, 0x55, 0x95, 0x94, 0x54, 0x9C, 0x5C,
    0x5D, 0x9D, 0x5F, 0x9F, 0x9E, 0x5E, 0x5A, 0x9A, 0x9B, 0x5B,
    0x99, 0x59, 0x58, 0x98, 0x88, 0x48, 0x49, 0x89, 0x4B, 0x8B,
    0x8A, 0x4A, 0x4E, 0x8E, 0x8F, 0x4F, 0x8D, 0x4D, 0x4C, 0x8C,
    0x44, 0x84, 0x85, 0x45, 0x87, 0x47, 0x46, 0x86, 0x82, 0x42,
    0x43, 0x83, 0x41, 0x81, 0x80, 0x40
)

fun calculateCRC(data: ByteArray, offset: Int, len: Int): IntArray {
    val crc = intArrayOf(0xFF, 0xFF)
    var nextByte = 0
    var uIndex: Int /* will index into CRC lookup*/ /* table */
    /* pass through message buffer */
    var i = offset
    while (i < len && i < data.size) {
        nextByte = 0xFF and data[i].toInt()
        uIndex = crc[0] xor nextByte //*puchMsg++; /* calculate the CRC */
        crc[0] = crc[1] xor auchCRCHi[uIndex].toInt()
        crc[1] = auchCRCLo[uIndex].toInt()
        i++
    }
    return crc
}

fun String?.toIntOrDefaultByFormatter(default: Int) = toIntOrNullByFormatter() ?: default
fun String?.toIntOrNullByFormatter() = when {
    this == null -> null
    startsWith("0x") -> trim().replaceFirst("0x", "").toIntOrNull(16)
    startsWith("0b") -> trim().replaceFirst("0b", "").toIntOrNull(2)
    else -> trim().toIntOrNull()
}

@ExperimentalTime
fun toHHmmss(time: Long): String {
    return time.milliseconds.toComponents { hours, minutes, seconds, _ ->
        "${padZero(hours)}:${padZero(minutes)}:${padZero(seconds)}"
    }
}

private fun padZero(d: Int) = d.toString().padStart(2, '0')

fun callKeyBoard() {
    Desktop.getDesktop()
        .open(Paths.get("C:/Program Files/Common Files/Microsoft Shared/ink/TabTip.exe").toFile())
}

fun showOKDialog(
    timeout: Long,
    title: String,
    text: String,
    isDialogOpened: () -> Boolean,
    breakCondition: () -> Boolean
) {
    val initTime = System.currentTimeMillis()

    runLater {
        Alert(Alert.AlertType.NONE, title, ButtonType.OK).apply {
            this.title = title
            contentText = text
        }.also {
            (it.dialogPane.scene.window as Stage).icons.add(FX.primaryStage.icons[0])
            it.dialogPane.style {

            }
            it.showAndWait().ifPresent { }
        }
    }

    while (isDialogOpened() && !breakCondition()) {
        Thread.sleep(10)
        val elapsedTime = System.currentTimeMillis() - initTime

        if (elapsedTime > timeout) {
            throw Exception("Время ожидания диалога превышено")
        }
    }
}

fun showSaveDialogConfirmation(currentWindow: Window?) {
    confirmation(
        "Конец",
        "Все выбранные испытания завершены/отменены. Протоколы сохранены",
        ButtonType("Ок"),
        title = "Конец",
        owner = currentWindow
    ) { buttonType ->
        when (buttonType.text) {
            "Да" -> {
            }
        }
    }
}

fun showTwoWayDialog(
    title: String,
    text: String,
    way1Title: String,
    way2Title: String,
    way1: () -> Unit,
    way2: () -> Unit,
    currentWindow: Window
) {
    val initTime = System.currentTimeMillis()

    var isDialogOpened = true

    runLater {
        warning(
            title,
            text,
            ButtonType(way1Title),
            ButtonType(way2Title),
            owner = currentWindow
        ) { buttonType ->
            when (buttonType.text) {
                way1Title -> way1()
                way2Title -> way2()
            }
            isDialogOpened = false
        }
    }

//    while (isDialogOpened && !breakCondition()) {
//        println("running ${System.currentTimeMillis()}")
//        Thread.sleep(10)
//        val elapsedTime = System.currentTimeMillis() - initTime
//
//        if (elapsedTime > timeout) {
////            if (isDialogOpened) { TODO
////                runLater { alert.close() }
////            }
//            throw Exception("Время ожидания диалога превышено")
//        }
//    }
}