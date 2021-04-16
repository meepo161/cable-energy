package ru.avem.cable.view

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.shape.Circle
import javafx.stage.Modality
import ru.avem.cable.controllers.MainViewController
import ru.avem.cable.database.entities.TestObjectsType
import ru.avem.cable.entities.TableValues
import ru.avem.cable.utils.State
import ru.avem.cable.utils.Toast
import ru.avem.cable.view.Styles.Companion.megaHard
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.concurrent.thread
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime


class MainView : View("Экран Энергия") {
    override val configPath: Path = Paths.get("./app.conf")

    private val controller: MainViewController by inject()

    var mainMenubar: MenuBar by singleAssign()
    var comIndicate: Circle by singleAssign()
    var comIndicateCP2000: Circle by singleAssign()
    var vBoxLog: VBox by singleAssign()

    var checkBox1: CheckBox by singleAssign()
    var checkBox2: CheckBox by singleAssign()
    var checkBox3: CheckBox by singleAssign()
    var checkBox4: CheckBox by singleAssign()
    var checkBox5: CheckBox by singleAssign()
    var checkBox6: CheckBox by singleAssign()

    var comboBox1: ComboBox<TestObjectsType> by singleAssign()
    var comboBox2: ComboBox<TestObjectsType> by singleAssign()
    var comboBox3: ComboBox<TestObjectsType> by singleAssign()
    var comboBox4: ComboBox<TestObjectsType> by singleAssign()
    var comboBox5: ComboBox<TestObjectsType> by singleAssign()
    var comboBox6: ComboBox<TestObjectsType> by singleAssign()

    var textField1: TextField by singleAssign()
    var textField2: TextField by singleAssign()
    var textField3: TextField by singleAssign()
    var textField4: TextField by singleAssign()
    var textField5: TextField by singleAssign()
    var textField6: TextField by singleAssign()

    var circleAvem71: Circle by singleAssign()
    var circleAvem72: Circle by singleAssign()
    var circleAvem73: Circle by singleAssign()
    var circleAvem74: Circle by singleAssign()
    var circleAvem75: Circle by singleAssign()
    var circleAvem76: Circle by singleAssign()
    var circleCP2000: Circle by singleAssign()
    var circlePR102: Circle by singleAssign()
    var circlePR200: Circle by singleAssign()
    var circlePM130: Circle by singleAssign()
    var circleKVM: Circle by singleAssign()

    var circleDI1: Circle by singleAssign()
    var circleDI2: Circle by singleAssign()
    var circleDI3: Circle by singleAssign()
    var circleDI4: Circle by singleAssign()
    var circleDI5: Circle by singleAssign()
    var circleDI6: Circle by singleAssign()
    var circleDI7: Circle by singleAssign()
    var circleDI8: Circle by singleAssign()
    var circleDI9: Circle by singleAssign()

    var buttonStart: Button by singleAssign()

    lateinit var comboBoxList: List<ComboBox<TestObjectsType>>
    lateinit var checkBoxList: List<CheckBox>
    lateinit var textFieldList: List<TextField>

    var tableValues = observableListOf(
        TableValues(
            SimpleStringProperty(""),
            SimpleStringProperty(""),
            SimpleStringProperty("")
        ),
        TableValues(
            SimpleStringProperty(""),
            SimpleStringProperty(""),
            SimpleStringProperty("")
        ),
        TableValues(
            SimpleStringProperty(""),
            SimpleStringProperty(""),
            SimpleStringProperty("")
        ),
        TableValues(
            SimpleStringProperty(""),
            SimpleStringProperty(""),
            SimpleStringProperty("")
        ),
        TableValues(
            SimpleStringProperty(""),
            SimpleStringProperty(""),
            SimpleStringProperty("")
        ),
        TableValues(
            SimpleStringProperty(""),
            SimpleStringProperty(""),
            SimpleStringProperty("")
        )
    )

    override fun onBeforeShow() {
    }

    override fun onDock() {
        comboBoxList = listOf(comboBox1, comboBox2, comboBox3, comboBox4, comboBox5, comboBox6)
        checkBoxList = listOf(checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6)
        textFieldList = listOf(textField1, textField2, textField3, textField4, textField5, textField6)

        comboBoxList.forEach { it.isDisable = true }
        textFieldList.forEach { it.isDisable = true }
        comboBoxList.forEach { it.selectionModel.selectFirst() }

        controller.refreshObjectsTypes()
    }

    @ExperimentalTime
    override val root = borderpane {
        maxWidth = 1920.0
        maxHeight = 1000.0
        top {
            mainMenubar = menubar {
                menu("Меню") {
                    item("Выход") {
                        action {
                            exitProcess(0)
                        }
                    }
                }
                menu("База данных") {
                    item("Объект испытания") {
                        action {
                            find<ObjectEditorWindow>().openModal(
                                modality = Modality.WINDOW_MODAL,
                                escapeClosesWindow = true,
                                resizable = false,
                                owner = this@MainView.currentWindow
                            )
                        }
                    }
                    item("Протоколы") {
                        action {
                            find<ProtocolListWindow>().openModal(
                                modality = Modality.WINDOW_MODAL,
                                escapeClosesWindow = true,
                                resizable = false,
                                owner = this@MainView.currentWindow
                            )
                        }
                    }
                }
                menu("Информация") {
                    item("Версия ПО") {
                        action {
                            controller.showAboutUs()
                        }
                    }
                }
            }
        }
        center {
            anchorpane {
                vbox(spacing = 16.0) {
                    anchorpaneConstraints {
                        leftAnchor = 16.0
                        rightAnchor = 16.0
                        topAnchor = 16.0
                        bottomAnchor = 16.0
                    }
                    alignmentProperty().set(Pos.CENTER)
                    hbox(spacing = 16.0) {
                        alignmentProperty().set(Pos.CENTER)
                        vbox(spacing = 10.0) {
                            alignmentProperty().set(Pos.CENTER)
                            hbox {
                                paddingTop = 16.0
                                label("")
                            }
                            hbox(spacing = 16.0) {
                                paddingLeft = 64.0
                                alignmentProperty().set(Pos.CENTER_LEFT)
                                checkBox1 = checkbox {
                                    action {
                                        comboBox1.isDisable = !isSelected
                                        textField1.isDisable = !isSelected
                                    }
                                }
                                comboBox1 = combobox {
                                    minWidth = 300.0

                                }
                                textField1 = textfield {
                                    minWidth = 300.0
                                    promptText = "Заводской номер"
                                    alignment = Pos.CENTER
                                }
                            }
                            hbox(spacing = 16.0) {
                                paddingLeft = 64.0
                                alignmentProperty().set(Pos.CENTER_LEFT)
                                checkBox2 = checkbox {
                                    action {
                                        comboBox2.isDisable = !isSelected
                                        textField2.isDisable = !isSelected
                                    }

                                }
                                comboBox2 = combobox {
                                    minWidth = 300.0

                                }
                                textField2 = textfield {
                                    minWidth = 300.0
                                    promptText = "Заводской номер"
                                    alignment = Pos.CENTER
                                }
                            }
                            hbox(spacing = 16.0) {
                                paddingLeft = 64.0
                                alignmentProperty().set(Pos.CENTER_LEFT)
                                checkBox3 = checkbox {
                                    action {
                                        comboBox3.isDisable = !isSelected
                                        textField3.isDisable = !isSelected
                                    }

                                }
                                comboBox3 = combobox {
                                    minWidth = 300.0

                                }
                                textField3 = textfield {
                                    minWidth = 300.0
                                    promptText = "Заводской номер"
                                    alignment = Pos.CENTER
                                }
                            }
                            hbox(spacing = 16.0) {
                                paddingLeft = 64.0
                                alignmentProperty().set(Pos.CENTER_LEFT)
                                checkBox4 = checkbox {
                                    action {
                                        comboBox4.isDisable = !isSelected
                                        textField4.isDisable = !isSelected
                                    }

                                }
                                comboBox4 = combobox {
                                    minWidth = 300.0

                                }
                                textField4 = textfield {
                                    minWidth = 300.0
                                    promptText = "Заводской номер"
                                    alignment = Pos.CENTER
                                }
                            }
                            hbox(spacing = 16.0) {
                                paddingLeft = 64.0
                                alignmentProperty().set(Pos.CENTER_LEFT)
                                checkBox5 = checkbox {
                                    action {
                                        comboBox5.isDisable = !isSelected
                                        textField5.isDisable = !isSelected
                                    }

                                }
                                comboBox5 = combobox {
                                    minWidth = 300.0

                                }
                                textField5 = textfield {
                                    minWidth = 300.0
                                    promptText = "Заводской номер"
                                    alignment = Pos.CENTER
                                }
                            }
                            hbox(spacing = 16.0) {
                                paddingLeft = 64.0
                                alignmentProperty().set(Pos.CENTER_LEFT)

                                checkBox6 = checkbox {
                                    action {
                                        comboBox6.isDisable = !isSelected
                                        textField6.isDisable = !isSelected
                                    }

                                }
                                comboBox6 = combobox {
                                    minWidth = 300.0

                                }
                                textField6 = textfield {
                                    minWidth = 300.0
                                    promptText = "Заводской номер"
                                    alignment = Pos.CENTER
                                }
                            }
                        }
                        tableview(tableValues) {
                            minHeight = 404.0
                            maxHeight = 404.0
                            minWidth = 600.0
                            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                            mouseTransparentProperty().set(true)
                            column("U, В", TableValues::voltage.getter)
                            column("I, мA", TableValues::amperage.getter)
                            column("Время, сек", TableValues::time.getter)
                        }

                        vbox(spacing = 4.0) {
                            anchorpaneConstraints {
                                leftAnchor = 16.0
                                rightAnchor = 400.0
                                topAnchor = 16.0
                                bottomAnchor = 16.0
                            }
                            alignmentProperty().set(Pos.TOP_RIGHT)
                            hbox(spacing = 16.0) {
                                maxWidth = 300.0
                                alignment = Pos.CENTER
                                label("Состояние устройств")
                            }
                            hbox(spacing = 16.0) {
                                maxWidth = 200.0
                                alignment = Pos.CENTER_LEFT
                                circlePR102 = circle(radius = 12) {
                                    stroke = c("black")
                                    fill = State.INTERMEDIATE.c
                                }
                                label("ПР102")
                            }
                            hbox(spacing = 16.0) {
                                maxWidth = 200.0
                                alignment = Pos.CENTER_LEFT
                                circlePR200 = circle(radius = 12) {
                                    stroke = c("black")
                                    fill = State.INTERMEDIATE.c
                                }
                                label("ПР200")
                            }
                            hbox(spacing = 16.0) {
                                maxWidth = 200.0
                                alignment = Pos.CENTER_LEFT
                                circlePM130 = circle(radius = 12) {
                                    stroke = c("black")
                                    fill = State.INTERMEDIATE.c
                                }
                                label("PМ130P")
                            }
                            hbox(spacing = 16.0) {
                                maxWidth = 200.0
                                alignment = Pos.CENTER_LEFT
                                circleCP2000 = circle(radius = 12) {
                                    stroke = c("black")
                                    fill = State.INTERMEDIATE.c
                                }
                                label("ЧП")
                            }
                            hbox(spacing = 16.0) {
                                maxWidth = 200.0
                                alignment = Pos.CENTER_LEFT
                                circleAvem71 = circle(radius = 12) {
                                    stroke = c("black")
                                    fill = State.INTERMEDIATE.c
                                }
                                label("АВЭМ7-1")
                            }
                            hbox(spacing = 16.0) {
                                maxWidth = 200.0
                                alignment = Pos.CENTER_LEFT
                                circleAvem72 = circle(radius = 12) {
                                    stroke = c("black")
                                    fill = State.INTERMEDIATE.c
                                }
                                label("АВЭМ7-2")
                            }
                            hbox(spacing = 16.0) {
                                maxWidth = 200.0
                                alignment = Pos.CENTER_LEFT
                                circleAvem73 = circle(radius = 12) {
                                    stroke = c("black")
                                    fill = State.INTERMEDIATE.c
                                }
                                label("АВЭМ7-3")
                            }
                            hbox(spacing = 16.0) {
                                maxWidth = 200.0
                                alignment = Pos.CENTER_LEFT
                                circleAvem74 = circle(radius = 12) {
                                    stroke = c("black")
                                    fill = State.INTERMEDIATE.c
                                }
                                label("АВЭМ7-4")
                            }
                            hbox(spacing = 16.0) {
                                maxWidth = 200.0
                                alignment = Pos.CENTER_LEFT
                                circleAvem75 = circle(radius = 12) {
                                    stroke = c("black")
                                    fill = State.INTERMEDIATE.c
                                }
                                label("АВЭМ7-5")
                            }
                            hbox(spacing = 16.0) {
                                maxWidth = 200.0
                                alignment = Pos.CENTER_LEFT
                                circleAvem76 = circle(radius = 12) {
                                    stroke = c("black")
                                    fill = State.INTERMEDIATE.c
                                }
                                label("АВЭМ7-6")
                            }
                            hbox(spacing = 16.0) {
                                maxWidth = 200.0
                                alignment = Pos.CENTER_LEFT
                                circleKVM = circle(radius = 12) {
                                    stroke = c("black")
                                    fill = State.INTERMEDIATE.c
                                }
                                label("КВМ")
                            }
                        }
                        vbox(spacing = 8.0) {
                            anchorpaneConstraints {
                                leftAnchor = 16.0
                                rightAnchor = 16.0
                                topAnchor = 16.0
                                bottomAnchor = 16.0
                            }
                            alignmentProperty().set(Pos.TOP_RIGHT)
                            hbox(spacing = 16.0) {
                                maxWidth = 300.0
                                alignment = Pos.CENTER_LEFT
                                label("Контроль")
                            }
                            hbox(spacing = 16.0) {
                                maxWidth = 300.0
                                alignment = Pos.CENTER_LEFT
                                circleDI1 = circle(radius = 12) {
                                    stroke = c("black")
                                    fill = State.INTERMEDIATE.c
                                }
                                label("КМ1")
                            }
                            hbox(spacing = 16.0) {
                                maxWidth = 300.0
                                alignment = Pos.CENTER_LEFT
                                circleDI2 = circle(radius = 12) {
                                    stroke = c("black")
                                    fill = State.INTERMEDIATE.c
                                }
                                label("КМ2")
                            }
                            hbox(spacing = 16.0) {
                                maxWidth = 300.0
                                alignment = Pos.CENTER_LEFT
                                circleDI3 = circle(radius = 12) {
                                    stroke = c("black")
                                    fill = State.INTERMEDIATE.c
                                }
                                label("КМ3")
                            }
                            hbox(spacing = 16.0) {
                                maxWidth = 300.0
                                alignment = Pos.CENTER_LEFT
                                circleDI4 = circle(radius = 12) {
                                    stroke = c("black")
                                    fill = State.INTERMEDIATE.c
                                }
                                label("Тока НН А")
                            }
                            hbox(spacing = 16.0) {
                                maxWidth = 300.0
                                alignment = Pos.CENTER_LEFT
                                circleDI5 = circle(radius = 12) {
                                    stroke = c("black")
                                    fill = State.INTERMEDIATE.c
                                }
                                label("Тока НН B")
                            }
                            hbox(spacing = 16.0) {
                                maxWidth = 300.0
                                alignment = Pos.CENTER_LEFT
                                circleDI6 = circle(radius = 12) {
                                    stroke = c("black")
                                    fill = State.INTERMEDIATE.c
                                }
                                label("Тока НН C")
                            }
                            hbox(spacing = 16.0) {
                                maxWidth = 300.0
                                alignment = Pos.CENTER_LEFT
                                circleDI7 = circle(radius = 12) {
                                    stroke = c("black")
                                    fill = State.INTERMEDIATE.c
                                }
                                label("Дверей шкафа")
                            }
                            hbox(spacing = 16.0) {
                                maxWidth = 300.0
                                alignment = Pos.CENTER_LEFT
                                circleDI8 = circle(radius = 12) {
                                    stroke = c("black")
                                    fill = State.INTERMEDIATE.c
                                }
                                label("Двери оператора")
                            }
                            hbox(spacing = 16.0) {
                                maxWidth = 300.0
                                alignment = Pos.CENTER_LEFT
                                circleDI9 = circle(radius = 12) {
                                    stroke = c("black")
                                    fill = State.INTERMEDIATE.c
                                }
                                label("Ворот")
                            }
                        }
                    }
                    hbox {
                        alignmentProperty().set(Pos.CENTER)
                        anchorpane {
                            scrollpane {
//                            anchorpaneConstraints {
//                                leftAnchor = 0.0
//                                rightAnchor = 0.0
//                                topAnchor = 0.0
//                                bottomAnchor = 0.0
//                            }
                                minHeight = 400.0
                                maxHeight = 400.0
                                prefHeight = 400.0
                                minWidth = 1800.0
                                minWidth = 1800.0
                                prefWidth = 1800.0
                                vBoxLog = vbox {
                                }

                                vvalueProperty().bind(vBoxLog.heightProperty())
                            }
                        }
                    }
                    buttonStart = button("Старт") {
                        minHeight = 100.0
                        minWidth = 600.0
                        action {
                            handleStart()
                        }
                    }.addClass(Styles.startStop)

                }

            }
        }
        bottom = hbox(spacing = 64) {
            alignment = Pos.CENTER_LEFT
            comIndicate = circle(radius = 18) {
                hboxConstraints {
                    hGrow = Priority.ALWAYS
                    marginLeft = 8.0
                    marginBottom = 8.0
                }
                fill = State.INTERMEDIATE.c
                stroke = c("black")
                isSmooth = true
            }
            label(" Связь с преобразователем") {
                hboxConstraints {
                    hGrow = Priority.ALWAYS
                    marginBottom = 8.0
                }
            }
            comIndicateCP2000 = circle(radius = 18) {
                hboxConstraints {
                    hGrow = Priority.ALWAYS
                    marginLeft = 8.0
                    marginBottom = 8.0
                }
                fill = State.INTERMEDIATE.c
                stroke = c("black")
                isSmooth = true
            }
            label(" Связь с ЧП") {
                hboxConstraints {
                    hGrow = Priority.ALWAYS
                    marginBottom = 8.0
                }
            }
        }
    }.addClass(Styles.blackTheme, megaHard)

    private fun handleStart() {
        thread(isDaemon = true) {
            val isOneCheckBoxSelected = checkBoxList.map(CheckBox::isSelected).reduce { acc, b ->
                acc || b
            }
            if (controller.isExperimentRunning) {
                controller.cause = "Отменено оператором"
            } else if (isOneCheckBoxSelected) {
                runLater {
                    buttonStart.text = "СТОП"
                    comboBoxList.forEach { it.isDisable = true }
                    checkBoxList.forEach { it.isDisable = true }
                    textFieldList.forEach { it.isDisable = true }

                    clearTable()
                }

                controller.start()

            } else {
                runLater {
                    Toast.makeText("Выберите хотя бы 1 ОИ").show(Toast.ToastType.ERROR)
                }
            }
            runLater {
                buttonStart.text = "Старт"
                checkBoxList.forEach { it.isDisable = false }
                checkBoxList.forEach { it.isSelected = false }
            }
        }
    }

    private fun clearTable() {
        tableValues.forEach {
            it.voltage.value = ""
            it.amperage.value = ""
            it.time.value = ""
        }
    }
}
