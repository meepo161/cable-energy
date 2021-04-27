package ru.avem.cable.view

import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.shape.Circle
import javafx.stage.Modality
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.cable.controllers.MainViewController
import ru.avem.cable.database.entities.TestObjectsType
import ru.avem.cable.entities.*
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

    var cbSetU: ComboBox<String> by singleAssign()
    var cbSetUList = observableListOf<String>("")
    var cbSetType: ComboBox<String> by singleAssign()

    var isBurn = false
    var burn: Label by singleAssign()

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

    var cbCount1: ComboBox<String> by singleAssign()
    var cbCount2: ComboBox<String> by singleAssign()
    var cbCount3: ComboBox<String> by singleAssign()
    var cbCount4: ComboBox<String> by singleAssign()
    var cbCount5: ComboBox<String> by singleAssign()
    var cbCount6: ComboBox<String> by singleAssign()

    var textField1: TextField by singleAssign()
    var textField2: TextField by singleAssign()
    var textField3: TextField by singleAssign()
    var textField4: TextField by singleAssign()
    var textField5: TextField by singleAssign()
    var textField6: TextField by singleAssign()
    var tfSetU: TextField by singleAssign()
    var tfSetI: TextField by singleAssign()
    var tfSetTime: TextField by singleAssign()

    var tableviewIOutSet: TableView<TableValuesIOutSet> by singleAssign()
    var tableviewIOutSum: TableView<TableValueIOutSum> by singleAssign()

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
    var buttonStop: Button by singleAssign()

    var group = ToggleGroup()

    lateinit var comboBoxList: List<ComboBox<TestObjectsType>>
    lateinit var comboBoxListCount: List<ComboBox<String>>
    lateinit var checkBoxList: List<CheckBox>
    lateinit var textFieldList: List<TextField>
    lateinit var textFieldSet: List<TextField>

    var tableValuesIOut = observableListOf(
        TableValuesIOut(
            SimpleStringProperty("")
        ),
        TableValuesIOut(
            SimpleStringProperty("")
        ),
        TableValuesIOut(
            SimpleStringProperty("")
        ),
        TableValuesIOut(
            SimpleStringProperty("")
        ),
        TableValuesIOut(
            SimpleStringProperty("")
        ),
        TableValuesIOut(
            SimpleStringProperty("")
        )
    )
    var tableValuesIOutSet = observableListOf(
        TableValuesIOutSet(
            SimpleStringProperty("")
        ),
        TableValuesIOutSet(
            SimpleStringProperty("")
        ),
        TableValuesIOutSet(
            SimpleStringProperty("")
        ),
        TableValuesIOutSet(
            SimpleStringProperty("")
        ),
        TableValuesIOutSet(
            SimpleStringProperty("")
        ),
        TableValuesIOutSet(
            SimpleStringProperty("")
        )
    )

    var tableValueUOut = observableListOf(
        TableValueUOut(
            SimpleStringProperty("")
        )
    )
    var tableValueIOutSum = observableListOf(
        TableValueIOutSum(
            SimpleStringProperty("")
        )
    )
    var tableValueTime = observableListOf(
        TableValueTime(
            SimpleStringProperty("")
        )
    )
    var tableValuesIn = observableListOf(
        TableValuesIn(
            SimpleStringProperty(""),
            SimpleStringProperty("")
        ),
        TableValuesIn(
            SimpleStringProperty(""),
            SimpleStringProperty("")
        ),
        TableValuesIn(
            SimpleStringProperty(""),
            SimpleStringProperty("")
        )
    )

    override fun onBeforeShow() {
    }

    override fun onDock() {
        comboBoxList = listOf(comboBox1, comboBox2, comboBox3, comboBox4, comboBox5, comboBox6)
        comboBoxListCount = listOf(cbCount1, cbCount2, cbCount3, cbCount4, cbCount5, cbCount6)
        checkBoxList = listOf(checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6)
        textFieldList = listOf(textField1, textField2, textField3, textField4, textField5, textField6)
        textFieldSet = listOf(tfSetU, tfSetI, tfSetTime)

        comboBoxList.forEach { it.isDisable = true }
        comboBoxListCount.forEach { it.isDisable = true }
        textFieldList.forEach { it.isDisable = true }
        comboBoxList.forEach { it.selectionModel.selectFirst() }
        for (i in 0..5) {
            tableValuesIOutSet[i].amperage.value = "10000"
        }
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
                menu("Режим") {
                    radiomenuitem("Виу", group) {
                        action {
                            isBurn = false
                            burn.hide()
                        }
                    }.isSelected = true
                    radiomenuitem("Прожиг", group) {
                        action {
                            isBurn = true
                            burn.show()
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
                            hbox(spacing = 16.0) {
                                paddingLeft = 64.0
                                alignmentProperty().set(Pos.CENTER_LEFT)
                                label("Тип схемы: ")
                                cbSetType = combobox {
                                    prefWidth = 130.0
                                    alignment = Pos.CENTER
                                    items = observableListOf("6 кВ", "10 кВ")
                                    onAction = EventHandler {
                                        Toast.makeText("Убедитесь в правильности сборки схемы трансформатора")
                                            .show(Toast.ToastType.WARNING)
                                        cbSetUList = observableListOf("Польз.")
                                        if (cbSetType.value == "6 кВ") {
                                            for (i in 1..12) {
                                                cbSetUList.add("${i * 500}")
                                            }
                                        } else {
                                            for (i in 1..20) {
                                                cbSetUList.add("${i * 500}")
                                            }
                                        }
                                        cbSetU.items.clear()
                                        cbSetU.items = cbSetUList
                                    }
                                }
                                label("U, В ")
                                cbSetU = combobox {
                                    prefWidth = 140.0
                                    alignment = Pos.CENTER
                                    onAction = EventHandler {
                                        if (cbSetU.value == null) cbSetU.value = "Польз."
                                        if (cbSetU.value != "Польз.") {
                                            tfSetU.text = cbSetU.value.toString()
                                            tfSetU.isEditable = false
                                        } else {
                                            tfSetU.text = "0"
                                            tfSetU.isEditable = true
                                        }
                                    }
                                }
                                tfSetU = textfield {
                                    promptText = "Напряжение, В"
                                    maxWidth = 140.0
                                    alignment = Pos.CENTER
                                }
                                label("Ток, мА ")
                                tfSetI = textfield {
                                    promptText = "Ток, мА"
                                    maxWidth = 120.0
                                    alignment = Pos.CENTER
                                    onAction = EventHandler {
                                        if (text.isInt()) {
                                            for (i in 0..5) {
                                                tableValuesIOutSet[i].amperage.value = text.toString()
                                            }
                                        } else {
                                            runLater {
                                                Toast.makeText("Неверное значение тока утечки")
                                                    .show(Toast.ToastType.ERROR)
                                            }
                                        }
                                    }
                                }
                                label("Время, с ")
                                tfSetTime = textfield {
                                    promptText = "Время, с"
                                    maxWidth = 120.0
                                    alignment = Pos.CENTER
                                }
                            }
                            hbox(spacing = 16.0) {
                                paddingLeft = 32.0
                                alignmentProperty().set(Pos.CENTER_LEFT)
                                label("Пост 1")
                                checkBox1 = checkbox {
                                    action {
                                        comboBox1.isDisable = !isSelected
                                        textField1.isDisable = !isSelected
                                        cbCount1.isDisable = !isSelected
                                    }
                                }
                                cbCount1 = combobox {
                                    isEditable = true
                                    maxWidth = 120.0
                                    alignment = Pos.CENTER
                                    val list = observableListOf<String>()
                                    list.add("Все")
                                    for (i in 1..25) {
                                        if (i != 1) {
                                            list.add("$i")
                                        }
                                        list.add("${i}b")
                                    }
                                    items = list
                                    onAction = javafx.event.EventHandler {
                                        comboBox1.items.clear()
                                        if (!selectionModel.isEmpty) {
                                            if (selectionModel.selectedItem.toString() == "Все") {
                                                comboBox1.items = transaction {
                                                    TestObjectsType.all().toList().asObservable()
                                                }.toObservable()
                                            } else {
                                                comboBox1.items = transaction {
                                                    TestObjectsType.all().toList().asObservable()
                                                }.filter { it.cores == selectionModel.selectedItem.toString() }
                                                    .toObservable()
                                            }
                                        }
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
                                paddingLeft = 32.0
                                alignmentProperty().set(Pos.CENTER_LEFT)
                                label("Пост 2")
                                checkBox2 = checkbox {
                                    action {
                                        comboBox2.isDisable = !isSelected
                                        textField2.isDisable = !isSelected
                                        cbCount2.isDisable = !isSelected
                                    }
                                }
                                cbCount2 = combobox {
                                    isEditable = true
                                    maxWidth = 120.0
                                    alignment = Pos.CENTER
                                    val list = observableListOf<String>()
                                    list.add("Все")
                                    for (i in 1..25) {
                                        if (i != 1) {
                                            list.add("$i")
                                        }
                                        list.add("${i}b")
                                    }
                                    items = list
                                    onAction = javafx.event.EventHandler {
                                        comboBox2.items.clear()
                                        if (!selectionModel.isEmpty) {
                                            if (selectionModel.selectedItem.toString() == "Все") {
                                                comboBox2.items = transaction {
                                                    TestObjectsType.all().toList().asObservable()
                                                }.toObservable()
                                            } else {
                                                comboBox2.items = transaction {
                                                    TestObjectsType.all().toList().asObservable()
                                                }.filter { it.cores == selectionModel.selectedItem.toString() }
                                                    .toObservable()
                                            }
                                        }
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
                                paddingLeft = 32.0
                                alignmentProperty().set(Pos.CENTER_LEFT)
                                label("Пост 3")
                                checkBox3 = checkbox {
                                    action {
                                        comboBox3.isDisable = !isSelected
                                        textField3.isDisable = !isSelected
                                        cbCount3.isDisable = !isSelected
                                    }

                                }
                                cbCount3 = combobox {
                                    isEditable = true
                                    maxWidth = 120.0
                                    alignment = Pos.CENTER
                                    val list = observableListOf<String>()
                                    list.add("Все")
                                    for (i in 1..25) {
                                        if (i != 1) {
                                            list.add("$i")
                                        }
                                        list.add("${i}b")
                                    }
                                    items = list
                                    onAction = javafx.event.EventHandler {
                                        comboBox3.items.clear()
                                        if (!selectionModel.isEmpty) {
                                            if (selectionModel.selectedItem.toString() == "Все") {
                                                comboBox3.items = transaction {
                                                    TestObjectsType.all().toList().asObservable()
                                                }.toObservable()
                                            } else {
                                                comboBox3.items = transaction {
                                                    TestObjectsType.all().toList().asObservable()
                                                }.filter { it.cores == selectionModel.selectedItem.toString() }
                                                    .toObservable()
                                            }
                                        }
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
                                paddingLeft = 32.0
                                alignmentProperty().set(Pos.CENTER_LEFT)
                                label("Пост 4")
                                checkBox4 = checkbox {
                                    action {
                                        comboBox4.isDisable = !isSelected
                                        textField4.isDisable = !isSelected
                                        cbCount4.isDisable = !isSelected
                                    }

                                }
                                cbCount4 = combobox {
                                    isEditable = true
                                    maxWidth = 120.0
                                    alignment = Pos.CENTER
                                    val list = observableListOf<String>()
                                    list.add("Все")
                                    for (i in 1..25) {
                                        if (i != 1) {
                                            list.add("$i")
                                        }
                                        list.add("${i}b")
                                    }
                                    items = list
                                    onAction = javafx.event.EventHandler {
                                        comboBox4.items.clear()
                                        if (!selectionModel.isEmpty) {
                                            if (selectionModel.selectedItem.toString() == "Все") {
                                                comboBox4.items = transaction {
                                                    TestObjectsType.all().toList().asObservable()
                                                }.toObservable()
                                            } else {
                                                comboBox4.items = transaction {
                                                    TestObjectsType.all().toList().asObservable()
                                                }.filter { it.cores == selectionModel.selectedItem.toString() }
                                                    .toObservable()
                                            }
                                        }
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
                                paddingLeft = 32.0
                                alignmentProperty().set(Pos.CENTER_LEFT)
                                label("Пост 5")
                                checkBox5 = checkbox {
                                    action {
                                        comboBox5.isDisable = !isSelected
                                        textField5.isDisable = !isSelected
                                        cbCount5.isDisable = !isSelected
                                    }

                                }
                                cbCount5 = combobox {
                                    isEditable = true
                                    maxWidth = 120.0
                                    alignment = Pos.CENTER
                                    val list = observableListOf<String>()
                                    list.add("Все")
                                    for (i in 1..25) {
                                        if (i != 1) {
                                            list.add("$i")
                                        }
                                        list.add("${i}b")
                                    }
                                    items = list
                                    onAction = javafx.event.EventHandler {
                                        comboBox5.items.clear()
                                        if (!selectionModel.isEmpty) {
                                            if (selectionModel.selectedItem.toString() == "Все") {
                                                comboBox5.items = transaction {
                                                    TestObjectsType.all().toList().asObservable()
                                                }.toObservable()
                                            } else {
                                                comboBox5.items = transaction {
                                                    TestObjectsType.all().toList().asObservable()
                                                }.filter { it.cores == selectionModel.selectedItem.toString() }
                                                    .toObservable()
                                            }
                                        }
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
                                paddingLeft = 32.0
                                alignmentProperty().set(Pos.CENTER_LEFT)
                                label("Пост 6")
                                checkBox6 = checkbox {
                                    action {
                                        comboBox6.isDisable = !isSelected
                                        textField6.isDisable = !isSelected
                                        cbCount6.isDisable = !isSelected
                                    }

                                }
                                cbCount6 = combobox {
                                    isEditable = true
                                    maxWidth = 120.0
                                    alignment = Pos.CENTER
                                    val list = observableListOf<String>()
                                    list.add("Все")
                                    for (i in 1..25) {
                                        if (i != 1) {
                                            list.add("$i")
                                        }
                                        list.add("${i}b")
                                    }
                                    items = list
                                    onAction = javafx.event.EventHandler {
                                        comboBox6.items.clear()
                                        if (!selectionModel.isEmpty) {
                                            if (selectionModel.selectedItem.toString() == "Все") {
                                                comboBox6.items = transaction {
                                                    TestObjectsType.all().toList().asObservable()
                                                }.toObservable()
                                            } else {
                                                comboBox6.items = transaction {
                                                    TestObjectsType.all().toList().asObservable()
                                                }.filter { it.cores == selectionModel.selectedItem.toString() }
                                                    .toObservable()
                                            }
                                        }
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
                        tableviewIOutSet = tableview(tableValuesIOutSet) {
                            minHeight = 404.0
                            maxHeight = 404.0
                            minWidth = 100.0
                            maxWidth = 160.0
                            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                            column("Зад.: I, мА", TableValuesIOutSet::amperage.getter).makeEditable()
                        }
                        tableview(tableValuesIOut) {
                            minHeight = 404.0
                            maxHeight = 404.0
                            minWidth = 100.0
                            maxWidth = 160.0
                            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                            mouseTransparentProperty().set(true)
                            column("I, мА", TableValuesIOut::amperage.getter)
                        }
                        vbox {
                            tableview(tableValueUOut) {
                                minHeight = 119.0
                                maxHeight = 119.0
                                minWidth = 200.0
                                maxWidth = 160.0
                                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                                mouseTransparentProperty().set(true)
                                column("U, В", TableValueUOut::voltage.getter)
                            }
                            tableview(tableValueTime) {
                                minHeight = 119.0
                                maxHeight = 119.0
                                minWidth = 200.0
                                maxWidth = 200.0
                                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                                mouseTransparentProperty().set(true)
                                column("Время, с", TableValueTime::time.getter)
                            }
                            tableview(tableValueIOutSum) {
                                minHeight = 119.0
                                maxHeight = 119.0
                                minWidth = 200.0
                                maxWidth = 200.0
                                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                                mouseTransparentProperty().set(true)
                                column("Ток, А", TableValueIOutSum::amperage.getter)
                            }
                        }
//                        vbox {
//                            tableview(tableValuesIn) {
//                                minHeight = 233.0
//                                maxHeight = 233.0
//                                minWidth = 400.0
//                                maxWidth = 400.0
//                                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
//                                mouseTransparentProperty().set(true)
//                                column("U, В", TableValuesIn::voltage.getter)
//                                column("I, А", TableValuesIn::amperage.getter)
//                            }
//                        }
                    }

                    hbox(spacing = 16.0) {
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
                                minWidth = 1200.0
                                maxWidth = 1200.0
                                prefWidth = 1200.0
                                vBoxLog = vbox {
                                }.addClass(Styles.maxTemp)

                                vvalueProperty().bind(vBoxLog.heightProperty())
                            }
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
                            hbox(spacing = 16.0) {
                                maxWidth = 200.0
                                alignment = Pos.CENTER_LEFT
                                circleCP2000 = circle(radius = 12) {
                                    stroke = c("black")
                                    fill = State.INTERMEDIATE.c
                                }
                                label("ЧП")
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
                    hbox(spacing = 32.0) {
                        alignment = Pos.CENTER
                        burn = label("Активирован режим прожига").addClass(Styles.startStop)
                        burn.hide()
                        buttonStart = button("Старт") {
                            minHeight = 100.0
                            minWidth = 600.0
                            action {
                                handleStart()
                            }
                        }.addClass(Styles.startStop)
//                        buttonStop = button("СТОП ЧП") {
//                            minHeight = 100.0
//                            minWidth = 600.0
//                            action {
//                                controller.stopCP2000()
//                            }
//                        }.addClass(Styles.startStop)
                    }

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
            var isFine = true
            if (checkBox1.isSelected) {
                if (comboBox1.selectionModel.selectedItem == null) {
                    isFine = false
                }
                if (tableValuesIOutSet[0].amperage.value.toString().toInt() < 0) {
                    isFine = false
                }
            } else {
//                comboBox1.selectionModel.clearSelection()
//                cbCount1.selectionModel.clearSelection()
            }
            if (checkBox2.isSelected) {
                if (comboBox2.selectionModel.selectedItem == null) {
                    isFine = false
                }
                if (tableValuesIOutSet[1].amperage.value.toString().toInt() < 0) {
                    isFine = false
                }
            } else {
//                comboBox2.selectionModel.clearSelection()
//                cbCount2.selectionModel.clearSelection()
            }
            if (checkBox3.isSelected) {
                if (comboBox3.selectionModel.selectedItem == null) {
                    isFine = false
                }
                if (tableValuesIOutSet[2].amperage.value.toString().toInt() < 0) {
                    isFine = false
                }
            } else {
//                comboBox3.selectionModel.clearSelection()
//                cbCount3.selectionModel.clearSelection()
            }
            if (checkBox4.isSelected) {
                if (comboBox4.selectionModel.selectedItem == null) {
                    isFine = false
                }
                if (tableValuesIOutSet[3].amperage.value.toString().toInt() < 0) {
                    isFine = false
                }
            } else {
//                comboBox4.selectionModel.clearSelection()
//                cbCount4.selectionModel.clearSelection()
            }
            if (checkBox5.isSelected) {
                if (comboBox5.selectionModel.selectedItem == null) {
                    isFine = false
                }
                if (tableValuesIOutSet[4].amperage.value.toString().toInt() < 0) {
                    isFine = false
                }
            } else {
//                comboBox5.selectionModel.clearSelection()
//                cbCount5.selectionModel.clearSelection()
            }
            if (checkBox6.isSelected) {
                if (comboBox6.selectionModel.selectedItem == null) {
                    isFine = false
                }
                if (tableValuesIOutSet[5].amperage.value.toString().toInt() < 0) {
                    isFine = false
                }
            } else {
//                comboBox6.selectionModel.clearSelection()
//                cbCount6.selectionModel.clearSelection()
            }
            if (!tfSetU.text.isInt()) {
                isFine = false
            } else {
                if (tfSetU.text.toInt() < 1 || tfSetU.text.toInt() > 10000) {
                    isFine = false
                }
            }
            if (!tfSetTime.text.isInt()) {
                isFine = false
            }
            if (controller.isExperimentRunning) {
                controller.cause = "Отменено оператором"
            } else if (!isFine) {
                runLater {
                    Toast.makeText("Проверьте правильность введенных данных").show(Toast.ToastType.ERROR)
                    setToDefault()
                }
            } else if (!controller.isResponding) {
                runLater {
                    Toast.makeText("Нет связи с устройствами").show(Toast.ToastType.ERROR)
                    setToDefault()
                }
            } else if (isOneCheckBoxSelected) {
                runLater {
                    buttonStart.text = "СТОП"
                    mainMenubar.isDisable = true
                    tableviewIOutSet.isDisable = true
                    comboBoxList.forEach { it.isDisable = true }
                    checkBoxList.forEach { it.isDisable = true }
                    textFieldList.forEach { it.isDisable = true }
                    textFieldSet.forEach { it.isDisable = true }
                    comboBoxListCount.forEach { it.isDisable = true }
                    clearTable()
                }

                controller.start()

                runLater {
                    buttonStart.text = "Старт"
                    mainMenubar.isDisable = false
                    tableviewIOutSet.isDisable = false
                    checkBoxList.forEach { it.isDisable = false }
                    checkBoxList.forEach { it.isSelected = false }
                    textFieldSet.forEach { it.isDisable = false }
                    comboBoxListCount.forEach {
                        it.selectionModel.clearSelection()
                    }
                    comboBoxList.forEach {
                        it.selectionModel.clearSelection()
                    }
                }
            } else {
                runLater {
                    Toast.makeText("Выберите хотя бы 1 ОИ").show(Toast.ToastType.ERROR)
                }
            }
            checkBoxList.forEach { it.isSelected = false }
        }
    }

    private fun setToDefault() {
        comboBoxList.forEach { it.isDisable = true }
        textFieldList.forEach { it.isDisable = true }
        comboBoxListCount.forEach { it.isDisable = true }
        checkBoxList.forEach { it.isDisable = false }
        checkBoxList.forEach { it.isSelected = false }
        comboBoxListCount.forEach {
            it.selectionModel.clearSelection()
        }
        comboBoxList.forEach {
            it.selectionModel.clearSelection()
        }
    }

    private fun clearTable() {
        tableValuesIOut.forEach {
            it.amperage.value = ""
        }
    }
}
