package ru.avem.cable.view

import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.cable.communication.utils.toInt
import ru.avem.cable.database.entities.ObjectsTypes
import ru.avem.cable.database.entities.TestObjectsType
import ru.avem.cable.utils.Toast
import tornadofx.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

class ObjectEditorWindow : View("Редактор объектов испытания") {

    var comboBoxTestItem: ComboBox<TestObjectsType> by singleAssign()

    val mainView: MainView by inject()

    var tfOI: TextField by singleAssign()
    var buttonConfirm: Button by singleAssign()
    var btnAdd: Button by singleAssign()
    var btnDelete: Button by singleAssign()

    var cbZZ01: CheckBox by singleAssign()
    var cbZZ02: CheckBox by singleAssign()
    var cbZZ03: CheckBox by singleAssign()
    var cbZZ04: CheckBox by singleAssign()
    var cbZZ05: CheckBox by singleAssign()

    var cbZZ11: CheckBox by singleAssign()
    var cbZZ12: CheckBox by singleAssign()
    var cbZZ13: CheckBox by singleAssign()
    var cbZZ14: CheckBox by singleAssign()

    var cbZZ21: CheckBox by singleAssign()
    var cbZZ22: CheckBox by singleAssign()
    var cbZZ23: CheckBox by singleAssign()
    var cbZZ24: CheckBox by singleAssign()

    var cbZZ31: CheckBox by singleAssign()
    var cbZZ32: CheckBox by singleAssign()
    var cbZZ33: CheckBox by singleAssign()
    var cbZZ34: CheckBox by singleAssign()

    var cbZZ41: CheckBox by singleAssign()
    var cbZZ42: CheckBox by singleAssign()
    var cbZZ43: CheckBox by singleAssign()
    var cbZZ44: CheckBox by singleAssign()

    var cbZZ51: CheckBox by singleAssign()
    var cbZZ52: CheckBox by singleAssign()
    var cbZZ53: CheckBox by singleAssign()
    var cbZZ54: CheckBox by singleAssign()
    var allCb = listOf<CheckBox>()

    var cbCount: ComboBox<String> by singleAssign()

    override fun onDock() {
        setItemsOnCb()
        allCb = listOf(
            cbZZ01, cbZZ11, cbZZ12, cbZZ13, cbZZ14,
            cbZZ02, cbZZ21, cbZZ22, cbZZ23, cbZZ24,
            cbZZ03, cbZZ31, cbZZ32, cbZZ33, cbZZ34,
            cbZZ04, cbZZ41, cbZZ42, cbZZ43, cbZZ44,
            cbZZ05, cbZZ51, cbZZ52, cbZZ53, cbZZ54
        )

    }

    private fun setItemsOnCb() {
        comboBoxTestItem.items = transaction {
            TestObjectsType.all().toList().asObservable()
        }
        mainView.comboBoxList.forEach {
            it.items.clear()
        }
        mainView.comboBoxListCount.forEach {
            it.selectionModel.clearSelection()
        }
    }

    override val root = anchorpane {
        prefWidth = 1280.0
        prefHeight = 720.0

        hbox(spacing = 16.0) {
            anchorpaneConstraints {
                leftAnchor = 16.0
                rightAnchor = 16.0
                topAnchor = 16.0
                bottomAnchor = 16.0
            }
            alignmentProperty().set(Pos.CENTER)
            vbox(spacing = 16.0) {
                alignmentProperty().set(Pos.CENTER)
                comboBoxTestItem = combobox {
                    prefWidth = 400.0
                    onAction = EventHandler {
                        if (selectionModel.selectedItem != null) {
                            tfOI.text = selectionModel.selectedItem.objectName
                            val schemeString = selectionModel.selectedItem.scheme.replace(" ", "")
                            var dot = ""
                            val list = mutableListOf<Int>()

                            schemeString.forEach {
                                if (it == '[') {
                                } else if (it == ',' || it == ']') {
                                    list.add(dot.toInt())
                                    dot = ""
                                } else if (it != ',') {
                                    dot += it
                                }
                            }

                            allCb.forEach {
                                it.isSelected = false
                            }

                            for (i in 0..24) {
                                allCb[i].isSelected = list[i] == 1
                            }
                            cbCount.selectionModel.select(selectionModel.selectedItem.cores)
                        }
                    }
                }
                btnDelete = button("Удалить") {
                    action {
                        try {
                            transaction {
                                ObjectsTypes.deleteWhere {
                                    ObjectsTypes.id eq comboBoxTestItem.selectionModel.selectedItem.id
                                }
                            }
                            Toast.makeText("Объект удален").show(Toast.ToastType.ERROR)
                            setItemsOnCb()
                        } catch (e: Exception) {
                            Toast.makeText("Нечего удалять").show(Toast.ToastType.ERROR)
                        }
                    }
                }
            }
            vbox(spacing = 16.0) {
                style {
                    backgroundColor += c("#3F3F3F")
                    borderColor += box(c("#a1a1a1"))
                }
                hboxConstraints {
                    paddingAll = 16.0
                }
                alignmentProperty().set(Pos.CENTER)
                label("Название ОИ")
                tfOI = textfield {
                    maxWidth = 400.0
                    alignment = Pos.CENTER
                }

                label("Количество жил")
                cbCount = combobox {
                    isEditable = true
                    maxWidth = 160.0
                    alignment = Pos.CENTER
                    val list = observableListOf<String>()
                    for (i in 1..25) {
                        if (i != 1) {
                            list.add("$i")
                        }
                        list.add("${i}b")
                    }
                    items = list
                }

                label("Схема")
                hbox(spacing = 8.0) {
                    alignmentProperty().set(Pos.CENTER)
                    label("1-й этап")
                    cbZZ01 = checkbox {
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ11 = checkbox {
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ12 = checkbox {
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ13 = checkbox {
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ14 = checkbox {
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                }
                hbox(spacing = 8.0) {
                    alignmentProperty().set(Pos.CENTER)
                    label("2-й этап")
                    cbZZ02 = checkbox {
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ21 = checkbox {
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ22 = checkbox {
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ23 = checkbox {
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ24 = checkbox {
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                }
                hbox(spacing = 8.0) {
                    alignmentProperty().set(Pos.CENTER)
                    label("3-й этап")
                    cbZZ03 = checkbox {
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ31 = checkbox {
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ32 = checkbox {
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ33 = checkbox {
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ34 = checkbox {
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                }
                hbox(spacing = 8.0) {
                    alignmentProperty().set(Pos.CENTER)
                    label("4-й этап")
                    cbZZ04 = checkbox {
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ41 = checkbox {
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ42 = checkbox {
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ43 = checkbox {
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ44 = checkbox {
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                }
                hbox(spacing = 8.0) {
                    alignmentProperty().set(Pos.CENTER)
                    label("5-й этап")
                    cbZZ05 = checkbox {
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ51 = checkbox {
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ52 = checkbox {
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ53 = checkbox {
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ54 = checkbox {
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                }
                buttonConfirm = button("Подтвердить") {
                    action {
                        confirm()
                    }
                }
                btnAdd = button("Добавить") {
                    isDisable = true
                    action {
                        val list = mutableListOf<Int>()
                        allCb.forEach {
                            list.add(it.isSelected.toInt())
                        }

                        transaction {
                            TestObjectsType.new {
                                objectName = tfOI.text.toString()
                                cores = cbCount.selectedItem.toString()
                                scheme = list.toString()
                            }
                        }
                        Toast.makeText("Объект добавлен").show(Toast.ToastType.INFORMATION)
                        setItemsOnCb()
                        tfOI.text = ""
                        allCb.forEach {
                            it.isSelected = false
                        }
                        cbCount.selectionModel.clearSelection()
                        confirm()
                    }

                }
            }
        }
    }.addClass(Styles.maxTemp, Styles.blackThemeOnlyColor)

    private fun confirm() {
        if (buttonConfirm.text == "Подтвердить") {
            if (tfOI.text.isNullOrEmpty()) {
                runLater {
                    Toast.makeText("Убедитесь в правильности заданных значений")
                        .show(Toast.ToastType.ERROR)
                }
            } else {
                allCb.forEach {
                    it.isDisable = true
                }
                cbCount.isDisable = true
                btnDelete.isDisable = true
                comboBoxTestItem.isDisable = true
                tfOI.isDisable = true
                btnAdd.isDisable = false
                buttonConfirm.text = "Отменить"
            }
        } else {
            allCb.forEach {
                it.isDisable = false
            }
            cbCount.isDisable = false
            btnDelete.isDisable = false
            comboBoxTestItem.isDisable = false
            tfOI.isDisable = false
            btnAdd.isDisable = true
            buttonConfirm.text = "Подтвердить"
        }
    }

//    private fun isInBetween(): Boolean {
//        var isValid = true
//        allCb.forEach {
//            if (!it.selectionModel.selectedItem.isNullOrEmpty()) {
//                if (it.selectionModel.selectedItem.isInt()) {
//                    if (it.selectionModel.selectedItem.toInt() > 25) {
//                        isValid = false
//                    }
//                } else {
//                    isValid = false
//                }
//            }
//        }
//        return isValid
//    }

    @OptIn(ExperimentalTime::class)
    private fun check(entities: List<String?>): Boolean {
        val (b, _) = measureTimedValue {
            mutableSetOf(*entities.filter {
                !it.isNullOrEmpty()
            }.toTypedArray()).also { it ->
                it.removeAll { it.isNullOrEmpty() }
            }.size != entities.filter { !it.isNullOrEmpty() }.size
        }
        return b
    }
}
