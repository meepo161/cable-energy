package ru.avem.cable.protocol

import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.usermodel.charts.AxisPosition
import org.apache.poi.ss.usermodel.charts.ChartDataSource
import org.apache.poi.ss.usermodel.charts.DataSources
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFChart
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean
import ru.avem.cable.app.Cable
import ru.avem.cable.database.entities.Protocol
import ru.avem.cable.database.entities.ProtocolSingle
import ru.avem.cable.utils.Toast
import ru.avem.cable.utils.copyFileFromStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException

var TO_DESIRED_ROW = 0

fun saveProtocolAsWorkbook(protocol: Protocol, path: String = "protocol.xlsx") {
    val template = File(path)
    copyFileFromStream(Cable::class.java.getResource("protocol.xlsx").openStream(), template)

    try {
        XSSFWorkbook(template).use { wb ->
            val sheet = wb.getSheetAt(0)
            for (iRow in 0 until 100) {
                val row = sheet.getRow(iRow)
                if (row != null) {
                    for (iCell in 0 until 100) {
                        val cell = row.getCell(iCell)
                        if (cell != null && (cell.cellType == CellType.STRING)) {
                            when (cell.stringCellValue) {
                                "#PROTOCOL_NUMBER#" -> cell.setCellValue(protocol.id.toString())
                                "#DATE#" -> cell.setCellValue(protocol.date)
                                "#TIME#" -> cell.setCellValue(protocol.time)

                                else -> {
                                    if (cell.stringCellValue.contains("#")) {
                                        cell.setCellValue("")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            fillParameters18(
                wb,
                protocol.temp11,
                protocol.temp12,
                protocol.temp13,
                protocol.temp14,
                protocol.temp15,
                protocol.temp16,
                protocol.temp17,
                protocol.temp21,
                protocol.temp22,
                protocol.temp23,
                protocol.temp24,
                protocol.temp25,
                protocol.temp26,
                protocol.temp31,
                protocol.temp32,
                protocol.temp33,
                protocol.temp34,
                protocol.temp35,
                protocol.temp36,
                0, 15
            )
            drawLineChart18(wb)
            val outStream = ByteArrayOutputStream()
            wb.write(outStream)
            outStream.close()
        }
    } catch (e: FileNotFoundException) {
        Toast.makeText("Не удалось сохранить протокол на диск")
    }
}

fun saveProtocolAsWorkbook(protocolSingle: ProtocolSingle, path: String = "protocol.xlsx", start: Int, end: Int) {
    val template = File(path)
    copyFileFromStream(Cable::class.java.getResource("protocol.xlsx").openStream(), template)

    try {
        XSSFWorkbook(template).use { wb ->
            val sheet = wb.getSheetAt(0)
            for (iRow in 0 until 100) {
                val row = sheet.getRow(iRow)
                if (row != null) {
                    for (iCell in 0 until 100) {
                        val cell = row.getCell(iCell)
                        if (cell != null && (cell.cellType == CellType.STRING)) {
                            when (cell.stringCellValue) {
                                "#PROTOCOL_NUMBER#" -> cell.setCellValue(protocolSingle.id.toString())
                                "#DATE#" -> cell.setCellValue(protocolSingle.date)
                                "#TIME#" -> cell.setCellValue(protocolSingle.time)

                                else -> {
                                    if (cell.stringCellValue.contains("#")) {
                                        cell.setCellValue("")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            fillParameters(wb, protocolSingle.temp, start, end)
            drawLineChart(wb)
            val outStream = ByteArrayOutputStream()
            wb.write(outStream)
            outStream.close()
        }
    } catch (e: FileNotFoundException) {
        Toast.makeText("Не удалось сохранить протокол на диск")
    }
}

fun fillParameters(wb: XSSFWorkbook, dots: String, start: Int, end: Int) {
    var values = dots.removePrefix("[").removePrefix("'").removeSuffix("]")
        .split(", ").map { it.replace(',', '.') }.map(String::toDouble)
    val valuesForExcel = arrayListOf<Double>()
    for (i in values.indices) {
        valuesForExcel.add(values[i])
    }
    val sheet = wb.getSheetAt(0)
    var row: Row
    var cellStyle: XSSFCellStyle = generateStyles(wb) as XSSFCellStyle
    var rowNum = sheet.lastRowNum + 1
    row = sheet.createRow(rowNum)
    var columnNum = 0
    for (i in values.indices) {
        columnNum = fillOneCell(row, columnNum, cellStyle, i  + start)
        columnNum = fillOneCell(row, columnNum, cellStyle, values[i])
        row = sheet.createRow(++rowNum)
        columnNum = 0
    }
}

private fun drawLineChart(workbook: XSSFWorkbook) {
    val sheet = workbook.getSheet("Sheet1")
    val lastRowIndex = sheet.lastRowNum - 1
    val timeData = DataSources.fromNumericCellRange(sheet, CellRangeAddress(16, lastRowIndex, 0, 0))
    val valueData = DataSources.fromNumericCellRange(sheet, CellRangeAddress(16, lastRowIndex, 1, 1))

    var lineChart = createLineChart(sheet)
    drawLineChart(lineChart, timeData, valueData)
}

private fun drawLineChart(
    lineChart: XSSFChart,
    xAxisData: ChartDataSource<Number>,
    yAxisData: ChartDataSource<Number>
) {
    val data = lineChart.chartDataFactory.createLineChartData()

    val xAxis = lineChart.chartAxisFactory.createCategoryAxis(AxisPosition.BOTTOM)
    val yAxis = lineChart.createValueAxis(AxisPosition.LEFT)
    yAxis.crosses = org.apache.poi.ss.usermodel.charts.AxisCrosses.AUTO_ZERO

    val series = data.addSeries(xAxisData, yAxisData)
    series.setTitle("График")
    lineChart.plot(data, xAxis, yAxis)

    val plotArea = lineChart.ctChart.plotArea
    plotArea.lineChartArray[0].smooth
    val ctBool = CTBoolean.Factory.newInstance()
    ctBool.`val` = false
    plotArea.lineChartArray[0].smooth = ctBool
    for (series in plotArea.lineChartArray[0].serArray) {
        series.smooth = ctBool
    }
}

fun fillParameters18(
    wb: XSSFWorkbook,
    dots11: String,
    dots12: String,
    dots13: String,
    dots14: String,
    dots15: String,
    dots16: String,
    dots17: String,
    dots21: String,
    dots22: String,
    dots23: String,
    dots24: String,
    dots25: String,
    dots26: String,
    dots31: String,
    dots32: String,
    dots33: String,
    dots34: String,
    dots35: String,
    dots36: String,
    columnNumber: Int, rawNumber: Int
) {
    val values11 = dots11.removePrefix("[").removePrefix("'").removeSuffix("]")
        .split(", ").map { it.replace(',', '.') }.map(String::toDouble)
    val values12 = dots12.removePrefix("[").removePrefix("'").removeSuffix("]")
        .split(", ").map { it.replace(',', '.') }.map(String::toDouble)
    val values13 = dots13.removePrefix("[").removePrefix("'").removeSuffix("]")
        .split(", ").map { it.replace(',', '.') }.map(String::toDouble)
    val values14 = dots14.removePrefix("[").removePrefix("'").removeSuffix("]")
        .split(", ").map { it.replace(',', '.') }.map(String::toDouble)
    val values15 = dots15.removePrefix("[").removePrefix("'").removeSuffix("]")
        .split(", ").map { it.replace(',', '.') }.map(String::toDouble)
    val values16 = dots16.removePrefix("[").removePrefix("'").removeSuffix("]")
        .split(", ").map { it.replace(',', '.') }.map(String::toDouble)
    val values17 = dots17.removePrefix("[").removePrefix("'").removeSuffix("]")
        .split(", ").map { it.replace(',', '.') }.map(String::toDouble)
    val values21 = dots21.removePrefix("[").removePrefix("'").removeSuffix("]")
        .split(", ").map { it.replace(',', '.') }.map(String::toDouble)
    val values22 = dots22.removePrefix("[").removePrefix("'").removeSuffix("]")
        .split(", ").map { it.replace(',', '.') }.map(String::toDouble)
    val values23 = dots23.removePrefix("[").removePrefix("'").removeSuffix("]")
        .split(", ").map { it.replace(',', '.') }.map(String::toDouble)
    val values24 = dots24.removePrefix("[").removePrefix("'").removeSuffix("]")
        .split(", ").map { it.replace(',', '.') }.map(String::toDouble)
    val values25 = dots25.removePrefix("[").removePrefix("'").removeSuffix("]")
        .split(", ").map { it.replace(',', '.') }.map(String::toDouble)
    val values26 = dots26.removePrefix("[").removePrefix("'").removeSuffix("]")
        .split(", ").map { it.replace(',', '.') }.map(String::toDouble)
    val values31 = dots31.removePrefix("[").removePrefix("'").removeSuffix("]")
        .split(", ").map { it.replace(',', '.') }.map(String::toDouble)
    val values32 = dots32.removePrefix("[").removePrefix("'").removeSuffix("]")
        .split(", ").map { it.replace(',', '.') }.map(String::toDouble)
    val values33 = dots33.removePrefix("[").removePrefix("'").removeSuffix("]")
        .split(", ").map { it.replace(',', '.') }.map(String::toDouble)
    val values34 = dots34.removePrefix("[").removePrefix("'").removeSuffix("]")
        .split(", ").map { it.replace(',', '.') }.map(String::toDouble)
    val values35 = dots35.removePrefix("[").removePrefix("'").removeSuffix("]")
        .split(", ").map { it.replace(',', '.') }.map(String::toDouble)
    val values36 = dots36.removePrefix("[").removePrefix("'").removeSuffix("]")
        .split(", ").map { it.replace(',', '.') }.map(String::toDouble)

    val valuesForExcel11 = arrayListOf<Double>()
    val valuesForExcel12 = arrayListOf<Double>()
    val valuesForExcel13 = arrayListOf<Double>()
    val valuesForExcel14 = arrayListOf<Double>()
    val valuesForExcel15 = arrayListOf<Double>()
    val valuesForExcel16 = arrayListOf<Double>()
    val valuesForExcel17 = arrayListOf<Double>()
    val valuesForExcel21 = arrayListOf<Double>()
    val valuesForExcel22 = arrayListOf<Double>()
    val valuesForExcel23 = arrayListOf<Double>()
    val valuesForExcel24 = arrayListOf<Double>()
    val valuesForExcel25 = arrayListOf<Double>()
    val valuesForExcel26 = arrayListOf<Double>()
    val valuesForExcel31 = arrayListOf<Double>()
    val valuesForExcel32 = arrayListOf<Double>()
    val valuesForExcel33 = arrayListOf<Double>()
    val valuesForExcel34 = arrayListOf<Double>()
    val valuesForExcel35 = arrayListOf<Double>()
    val valuesForExcel36 = arrayListOf<Double>()

    var step = 1
    if (values11.size > 1000) {
        step = (values11.size - values11.size % 1000) / 1000
    }

    for (i in values11.indices step step) {
        valuesForExcel11.add(values11[i])
        valuesForExcel12.add(values12[i])
        valuesForExcel13.add(values13[i])
        valuesForExcel14.add(values14[i])
        valuesForExcel15.add(values15[i])
        valuesForExcel16.add(values16[i])
        valuesForExcel17.add(values17[i])
        valuesForExcel21.add(values21[i])
        valuesForExcel22.add(values22[i])
        valuesForExcel23.add(values23[i])
        valuesForExcel24.add(values24[i])
        valuesForExcel25.add(values25[i])
        valuesForExcel26.add(values26[i])
        valuesForExcel31.add(values31[i])
        valuesForExcel32.add(values32[i])
        valuesForExcel33.add(values33[i])
        valuesForExcel34.add(values34[i])
        valuesForExcel35.add(values35[i])
        valuesForExcel36.add(values36[i])
    }
    val sheet = wb.getSheetAt(0)
    var row: Row
    val cellStyle: XSSFCellStyle = generateStyles(wb) as XSSFCellStyle
    var rowNum = rawNumber
    row = sheet.createRow(rowNum)
    for (i in valuesForExcel11.indices) {
        fillOneCell(row, columnNumber, cellStyle, i)
        fillOneCell(row, columnNumber + 1, cellStyle, valuesForExcel11[i])
        fillOneCell(row, columnNumber + 2, cellStyle, i)
        fillOneCell(row, columnNumber + 3, cellStyle, valuesForExcel12[i])
        fillOneCell(row, columnNumber + 4, cellStyle, i)
        fillOneCell(row, columnNumber + 5, cellStyle, valuesForExcel13[i])
        fillOneCell(row, columnNumber + 6, cellStyle, i)
        fillOneCell(row, columnNumber + 7, cellStyle, valuesForExcel14[i])
        fillOneCell(row, columnNumber + 8, cellStyle, i)
        fillOneCell(row, columnNumber + 9, cellStyle, valuesForExcel15[i])
        fillOneCell(row, columnNumber + 10, cellStyle, i)
        fillOneCell(row, columnNumber + 11, cellStyle, valuesForExcel16[i])
        fillOneCell(row, columnNumber + 12, cellStyle, i)
        fillOneCell(row, columnNumber + 13, cellStyle, valuesForExcel21[i])
        fillOneCell(row, columnNumber + 14, cellStyle, i)
        fillOneCell(row, columnNumber + 15, cellStyle, valuesForExcel22[i])
        fillOneCell(row, columnNumber + 16, cellStyle, i)
        fillOneCell(row, columnNumber + 17, cellStyle, valuesForExcel23[i])
        fillOneCell(row, columnNumber + 18, cellStyle, i)
        fillOneCell(row, columnNumber + 19, cellStyle, valuesForExcel24[i])
        fillOneCell(row, columnNumber + 20, cellStyle, i)
        fillOneCell(row, columnNumber + 21, cellStyle, valuesForExcel25[i])
        fillOneCell(row, columnNumber + 22, cellStyle, i)
        fillOneCell(row, columnNumber + 23, cellStyle, valuesForExcel26[i])
        fillOneCell(row, columnNumber + 24, cellStyle, i)
        fillOneCell(row, columnNumber + 25, cellStyle, valuesForExcel31[i])
        fillOneCell(row, columnNumber + 26, cellStyle, i)
        fillOneCell(row, columnNumber + 27, cellStyle, valuesForExcel32[i])
        fillOneCell(row, columnNumber + 28, cellStyle, i)
        fillOneCell(row, columnNumber + 29, cellStyle, valuesForExcel33[i])
        fillOneCell(row, columnNumber + 30, cellStyle, i)
        fillOneCell(row, columnNumber + 31, cellStyle, valuesForExcel34[i])
        fillOneCell(row, columnNumber + 32, cellStyle, i)
        fillOneCell(row, columnNumber + 33, cellStyle, valuesForExcel35[i])
        fillOneCell(row, columnNumber + 34, cellStyle, i)
        fillOneCell(row, columnNumber + 35, cellStyle, valuesForExcel36[i])
        fillOneCell(row, columnNumber + 36, cellStyle, i)
        fillOneCell(row, columnNumber + 37, cellStyle, valuesForExcel17[i])
        row = sheet.createRow(++rowNum)
    }
}

private fun fillOneCell(row: Row, columnNum: Int, cellStyle: XSSFCellStyle, points: Double): Int {
    val cell: Cell = row.createCell(columnNum)
    cell.cellStyle = cellStyle
    cell.setCellValue(points)
    return columnNum + 1
}

private fun fillOneCell(row: Row, columnNum: Int, cellStyle: XSSFCellStyle, points: Int): Int {
    val cell: Cell = row.createCell(columnNum)
    cell.cellStyle = cellStyle
    cell.setCellValue(points.toString())
    return columnNum + 1
}

private fun generateStyles(wb: XSSFWorkbook): CellStyle {
    val headStyle: CellStyle = wb.createCellStyle()
    headStyle.wrapText = true
    headStyle.borderBottom = BorderStyle.THIN
    headStyle.borderTop = BorderStyle.THIN
    headStyle.borderLeft = BorderStyle.THIN
    headStyle.borderRight = BorderStyle.THIN
    headStyle.alignment = HorizontalAlignment.CENTER
    headStyle.verticalAlignment = VerticalAlignment.CENTER
    return headStyle
}

private fun drawLineChart18(workbook: XSSFWorkbook) {
    val sheet = workbook.getSheet("Sheet1")
    val lastRowIndex = sheet.lastRowNum - 1

    val timeData11 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 0, 0))
    val valueData11 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 1, 1))

    val timeData12 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 2, 2))
    val valueData12 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 3, 3))

    val timeData13 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 4, 4))
    val valueData13 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 5, 5))

    val timeData14 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 6, 6))
    val valueData14 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 7, 7))

    val timeData15 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 8, 8))
    val valueData15 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 9, 9))

    val timeData16 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 10, 10))
    val valueData16 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 11, 11))

    val timeData21 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 12, 12))
    val valueData21 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 13, 13))

    val timeData22 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 14, 14))
    val valueData22 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 15, 15))

    val timeData23 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 16, 16))
    val valueData23 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 17, 17))

    val timeData24 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 18, 18))
    val valueData24 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 19, 19))

    val timeData25 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 20, 20))
    val valueData25 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 21, 21))

    val timeData26 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 22, 22))
    val valueData26 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 23, 23))

    val timeData31 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 24, 24))
    val valueData31 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 25, 25))

    val timeData32 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 26, 26))
    val valueData32 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 27, 27))

    val timeData33 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 28, 28))
    val valueData33 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 29, 29))

    val timeData34 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 30, 30))
    val valueData34 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 31, 31))

    val timeData35 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 32, 32))
    val valueData35 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 33, 33))

    val timeData36 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 34, 34))
    val valueData36 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 35, 35))

    val timeData17 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 36, 36))
    val valueData17 = DataSources.fromNumericCellRange(sheet, CellRangeAddress(15, lastRowIndex, 37, 37))

    val lineChart11 = createLineChart(sheet, 16, 26)
    drawLineChart18(lineChart11, timeData11, valueData11)
    val lineChart12 = createLineChart(sheet, 27, 37)
    drawLineChart18(lineChart12, timeData12, valueData12)
    val lineChart13 = createLineChart(sheet, 38, 48)
    drawLineChart18(lineChart13, timeData13, valueData13)
    val lineChart14 = createLineChart(sheet, 49, 59)
    drawLineChart18(lineChart14, timeData14, valueData14)
    val lineChart15 = createLineChart(sheet, 60, 70)
    drawLineChart18(lineChart15, timeData15, valueData15)
    val lineChart16 = createLineChart(sheet, 71, 81)
    drawLineChart18(lineChart16, timeData16, valueData16)
    val lineChart21 = createLineChart(sheet, 82, 92)
    drawLineChart18(lineChart21, timeData21, valueData21)
    val lineChart22 = createLineChart(sheet, 93, 103)
    drawLineChart18(lineChart22, timeData22, valueData22)
    val lineChart23 = createLineChart(sheet, 104, 114)
    drawLineChart18(lineChart23, timeData23, valueData23)
    val lineChart24 = createLineChart(sheet, 115, 125)
    drawLineChart18(lineChart24, timeData24, valueData24)
    val lineChart25 = createLineChart(sheet, 126, 136)
    drawLineChart18(lineChart25, timeData25, valueData25)
    val lineChart26 = createLineChart(sheet, 137, 147)
    drawLineChart18(lineChart26, timeData26, valueData26)
    val lineChart31 = createLineChart(sheet, 148, 158)
    drawLineChart18(lineChart31, timeData31, valueData31)
    val lineChart32 = createLineChart(sheet, 159, 169)
    drawLineChart18(lineChart32, timeData32, valueData32)
    val lineChart33 = createLineChart(sheet, 170, 180)
    drawLineChart18(lineChart33, timeData33, valueData33)
    val lineChart34 = createLineChart(sheet, 181, 191)
    drawLineChart18(lineChart34, timeData34, valueData34)
    val lineChart35 = createLineChart(sheet, 192, 202)
    drawLineChart18(lineChart35, timeData35, valueData35)
    val lineChart36 = createLineChart(sheet, 203, 213)
    drawLineChart18(lineChart36, timeData36, valueData36)
    val lineChart17 = createLineChart(sheet, 214, 224)
    drawLineChart18(lineChart17, timeData17, valueData17)
}

private fun createLineChart(sheet: XSSFSheet, rowStart: Int, rowEnd: Int): XSSFChart {
    val drawing = sheet.createDrawingPatriarch()
    val anchor = drawing.createAnchor(0, 0, 0, 0, 38, rowStart, 48, rowEnd)

    return drawing.createChart(anchor)
}

private fun createLineChart(sheet: XSSFSheet): XSSFChart {
    val drawing = sheet.createDrawingPatriarch()
    val anchor = drawing.createAnchor(0, 0, 0, 0, 3, 16, 36, 26)

    return drawing.createChart(anchor)
}

private fun drawLineChart18(
    lineChart: XSSFChart,
    xAxisData: ChartDataSource<Number>,
    yAxisData: ChartDataSource<Number>
) {
    val data = lineChart.chartDataFactory.createLineChartData()

    val xAxis = lineChart.chartAxisFactory.createCategoryAxis(AxisPosition.BOTTOM)
    val yAxis = lineChart.createValueAxis(AxisPosition.LEFT)
    yAxis.crosses = org.apache.poi.ss.usermodel.charts.AxisCrosses.AUTO_ZERO

    val series = data.addSeries(xAxisData, yAxisData)
    series.setTitle("График")
    lineChart.plot(data, xAxis, yAxis)

    val plotArea = lineChart.ctChart.plotArea
    plotArea.lineChartArray[0].smooth
    val ctBool = CTBoolean.Factory.newInstance()
    ctBool.`val` = false
    plotArea.lineChartArray[0].smooth = ctBool
    for (series in plotArea.lineChartArray[0].serArray) {
        series.smooth = ctBool
    }
}
