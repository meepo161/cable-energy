package ru.avem.cable.view

import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
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

    var cbVV1: ComboBox<String> by singleAssign()
    var cbVV2: ComboBox<String> by singleAssign()
    var cbVV3: ComboBox<String> by singleAssign()
    var cbVV4: ComboBox<String> by singleAssign()
    var cbVV5: ComboBox<String> by singleAssign()

    var cbZZ11: ComboBox<String> by singleAssign()
    var cbZZ12: ComboBox<String> by singleAssign()
    var cbZZ13: ComboBox<String> by singleAssign()
    var cbZZ14: ComboBox<String> by singleAssign()

    var cbZZ21: ComboBox<String> by singleAssign()
    var cbZZ22: ComboBox<String> by singleAssign()
    var cbZZ23: ComboBox<String> by singleAssign()
    var cbZZ24: ComboBox<String> by singleAssign()

    var cbZZ31: ComboBox<String> by singleAssign()
    var cbZZ32: ComboBox<String> by singleAssign()
    var cbZZ33: ComboBox<String> by singleAssign()
    var cbZZ34: ComboBox<String> by singleAssign()

    var cbZZ41: ComboBox<String> by singleAssign()
    var cbZZ42: ComboBox<String> by singleAssign()
    var cbZZ43: ComboBox<String> by singleAssign()
    var cbZZ44: ComboBox<String> by singleAssign()

    var cbZZ51: ComboBox<String> by singleAssign()
    var cbZZ52: ComboBox<String> by singleAssign()
    var cbZZ53: ComboBox<String> by singleAssign()
    var cbZZ54: ComboBox<String> by singleAssign()
    var allCb = listOf<ComboBox<String>>()

    override fun onDock() {
        setItemsOnCb()
        val listOfNumbers = mutableListOf<String>()
        for (i in 1..25) {
            listOfNumbers.add(i.toString())
        }
//        cbCount.items = listOfNumbers.asObservable()
        cbVV1.items = listOfNumbers.asObservable()
        cbVV2.items = listOfNumbers.asObservable()
        cbVV3.items = listOfNumbers.asObservable()
        cbVV4.items = listOfNumbers.asObservable()
        cbVV5.items = listOfNumbers.asObservable()
        cbZZ11.items = listOfNumbers.asObservable()
        cbZZ12.items = listOfNumbers.asObservable()
        cbZZ13.items = listOfNumbers.asObservable()
        cbZZ14.items = listOfNumbers.asObservable()
        cbZZ21.items = listOfNumbers.asObservable()
        cbZZ22.items = listOfNumbers.asObservable()
        cbZZ23.items = listOfNumbers.asObservable()
        cbZZ24.items = listOfNumbers.asObservable()
        cbZZ31.items = listOfNumbers.asObservable()
        cbZZ32.items = listOfNumbers.asObservable()
        cbZZ33.items = listOfNumbers.asObservable()
        cbZZ34.items = listOfNumbers.asObservable()
        cbZZ41.items = listOfNumbers.asObservable()
        cbZZ42.items = listOfNumbers.asObservable()
        cbZZ43.items = listOfNumbers.asObservable()
        cbZZ44.items = listOfNumbers.asObservable()
        cbZZ51.items = listOfNumbers.asObservable()
        cbZZ52.items = listOfNumbers.asObservable()
        cbZZ53.items = listOfNumbers.asObservable()
        cbZZ54.items = listOfNumbers.asObservable()

        allCb = listOf(
            cbVV1, cbZZ11, cbZZ12, cbZZ13, cbZZ14,
            cbVV2, cbZZ21, cbZZ22, cbZZ23, cbZZ24,
            cbVV3, cbZZ31, cbZZ32, cbZZ33, cbZZ34,
            cbVV4, cbZZ41, cbZZ42, cbZZ43, cbZZ44,
            cbVV5, cbZZ51, cbZZ52, cbZZ53, cbZZ54
        )

    }

    private fun setItemsOnCb() {
        comboBoxTestItem.items = transaction {
            TestObjectsType.all().toList().asObservable()
        }
        mainView.comboBoxList.forEach {
            it.items = transaction {
                TestObjectsType.all().toList().asObservable()
            }
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
                            var schemeCount = 0
                            var dot = ""
                            val list1 = mutableListOf<Int>()
                            val list2 = mutableListOf<Int>()
                            val list3 = mutableListOf<Int>()
                            val list4 = mutableListOf<Int>()
                            val list5 = mutableListOf<Int>()

                            schemeString.forEach {
                                if (it == '[') {
                                    schemeCount++
                                } else if (it == ',' || it == ']') {
                                    when (schemeCount) {
                                        1 -> {
                                            list1.add(dot.toInt())
                                        }
                                        2 -> {
                                            list2.add(dot.toInt())
                                        }
                                        3 -> {
                                            list3.add(dot.toInt())
                                        }
                                        4 -> {
                                            list4.add(dot.toInt())
                                        }
                                        5 -> {
                                            list5.add(dot.toInt())
                                        }
                                    }
                                    dot = ""
                                } else if (it != ',') {
                                    dot += it
                                }
                            }

                            allCb.forEach {
                                it.selectionModel.clearSelection()
                            }

                            if (list1.size > 0) {
                                cbVV1.selectionModel.select(list1[0].toString())
                            }
                            if (list1.size > 1) {
                                cbZZ11.selectionModel.select(list1[1].toString())
                            }
                            if (list1.size > 2) {
                                cbZZ12.selectionModel.select(list1[2].toString())
                            }
                            if (list1.size > 3) {
                                cbZZ13.selectionModel.select(list1[3].toString())
                            }
                            if (list1.size > 4) {
                                cbZZ14.selectionModel.select(list1[4].toString())
                            }

                            if (list2.size > 0) {
                                cbVV2.selectionModel.select(list2[0].toString())
                            }
                            if (list2.size > 1) {
                                cbZZ21.selectionModel.select(list2[1].toString())
                            }
                            if (list2.size > 2) {
                                cbZZ22.selectionModel.select(list2[2].toString())
                            }
                            if (list2.size > 3) {
                                cbZZ23.selectionModel.select(list2[3].toString())
                            }
                            if (list2.size > 4) {
                                cbZZ24.selectionModel.select(list2[4].toString())
                            }

                            if (list3.size > 0) {
                                cbVV3.selectionModel.select(list3[0].toString())
                            }
                            if (list3.size > 1) {
                                cbZZ31.selectionModel.select(list3[1].toString())
                            }
                            if (list3.size > 2) {
                                cbZZ32.selectionModel.select(list3[2].toString())
                            }
                            if (list3.size > 3) {
                                cbZZ33.selectionModel.select(list3[3].toString())
                            }
                            if (list3.size > 4) {
                                cbZZ34.selectionModel.select(list3[4].toString())
                            }

                            if (list4.size > 0) {
                                cbVV4.selectionModel.select(list4[0].toString())
                            }
                            if (list4.size > 1) {
                                cbZZ41.selectionModel.select(list4[1].toString())
                            }
                            if (list4.size > 2) {
                                cbZZ42.selectionModel.select(list4[2].toString())
                            }
                            if (list4.size > 3) {
                                cbZZ43.selectionModel.select(list4[3].toString())
                            }
                            if (list4.size > 4) {
                                cbZZ44.selectionModel.select(list4[4].toString())
                            }

                            if (list5.size > 0) {
                                cbVV5.selectionModel.select(list5[0].toString())
                            }
                            if (list5.size > 1) {
                                cbZZ51.selectionModel.select(list5[1].toString())
                            }
                            if (list5.size > 2) {
                                cbZZ52.selectionModel.select(list5[2].toString())
                            }
                            if (list5.size > 3) {
                                cbZZ53.selectionModel.select(list5[3].toString())
                            }
                            if (list5.size > 4) {
                                cbZZ54.selectionModel.select(list5[4].toString())
                            }

                        }
                    }
                }
                button("Удалить") {
                    action {
                        try {
                            transaction {
                                ObjectsTypes.deleteWhere {
                                    ObjectsTypes.id eq comboBoxTestItem.selectionModel.selectedItem.id
                                }
                            }
                            setItemsOnCb()
                        } catch (e: Exception) {
                            Toast.makeText("Нечего удалять").show(Toast.ToastType.ERROR)
                        }
                        Toast.makeText("Объект удален").show(Toast.ToastType.ERROR)
                    }
                }
            }
            vbox(spacing = 16.0) {
                style {
                    backgroundColor += c("#cecece")
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

//                label("Количество проводов")
//                cbCount = combobox {
//                    isEditable = true
//                    maxWidth = 100.0
//                    alignment = Pos.CENTER
//
//                }

                label("Схема")
                hbox(spacing = 8.0) {
                    alignmentProperty().set(Pos.CENTER)
                    label("ВВ")
                    cbVV1 = combobox {
                        isEditable = true
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    label("Заземление")
                    cbZZ11 = combobox {
                        isEditable = true
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ12 = combobox {
                        isEditable = true
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ13 = combobox {
                        isEditable = true
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ14 = combobox {
                        isEditable = true
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                }
                hbox(spacing = 8.0) {
                    alignmentProperty().set(Pos.CENTER)
                    label("ВВ")
                    cbVV2 = combobox {
                        isEditable = true
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    label("Заземление")
                    cbZZ21 = combobox {
                        isEditable = true
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ22 = combobox {
                        isEditable = true
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ23 = combobox {
                        isEditable = true
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ24 = combobox {
                        isEditable = true
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                }
                hbox(spacing = 8.0) {
                    alignmentProperty().set(Pos.CENTER)
                    label("ВВ")
                    cbVV3 = combobox {
                        isEditable = true
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    label("Заземление")
                    cbZZ31 = combobox {
                        isEditable = true
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ32 = combobox {
                        isEditable = true
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ33 = combobox {
                        isEditable = true
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ34 = combobox {
                        isEditable = true
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                }
                hbox(spacing = 8.0) {
                    alignmentProperty().set(Pos.CENTER)
                    label("ВВ")
                    cbVV4 = combobox {
                        isEditable = true
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    label("Заземление")
                    cbZZ41 = combobox {
                        isEditable = true
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ42 = combobox {
                        isEditable = true
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ43 = combobox {
                        isEditable = true
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ44 = combobox {
                        isEditable = true
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                }
                hbox(spacing = 8.0) {
                    alignmentProperty().set(Pos.CENTER)
                    label("ВВ")
                    cbVV5 = combobox {
                        isEditable = true
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    label("Заземление")
                    cbZZ51 = combobox {
                        isEditable = true
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ52 = combobox {
                        isEditable = true
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ53 = combobox {
                        isEditable = true
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                    cbZZ54 = combobox {
                        isEditable = true
                        maxWidth = 100.0
                        alignment = Pos.CENTER
                    }
                }
                button("Добавить") {
                    action {
                        if (check(
                                listOf(
                                    cbVV1.selectionModel.selectedItem,
                                    cbZZ11.selectionModel.selectedItem,
                                    cbZZ12.selectionModel.selectedItem,
                                    cbZZ13.selectionModel.selectedItem,
                                    cbZZ14.selectionModel.selectedItem
                                )
                            )
                        ) {
                            Toast.makeText("Повторяющиеся номера проводов").show(Toast.ToastType.ERROR)
                        } else if (!isInBetween()) {
                            Toast.makeText("Проверьте правильность введенных данных").show(Toast.ToastType.ERROR)

                        } else {
                            val list1 = mutableListOf<String>()
                            val list2 = mutableListOf<String>()
                            val list3 = mutableListOf<String>()
                            val list4 = mutableListOf<String>()
                            val list5 = mutableListOf<String>()

                            val list = mutableListOf<String>()

                            if (!cbVV1.selectionModel.selectedItem.isNullOrEmpty()) {
                                list1.add(cbVV1.selectionModel.selectedItem.toString())
                                if (!cbZZ11.selectionModel.selectedItem.isNullOrEmpty()) {
                                    list1.add(cbZZ11.selectionModel.selectedItem.toString())
                                }
                                if (!cbZZ12.selectionModel.selectedItem.isNullOrEmpty()) {
                                    list1.add(cbZZ12.selectionModel.selectedItem.toString())
                                }
                                if (!cbZZ13.selectionModel.selectedItem.isNullOrEmpty()) {
                                    list1.add(cbZZ13.selectionModel.selectedItem.toString())
                                }
                                if (!cbZZ14.selectionModel.selectedItem.isNullOrEmpty()) {
                                    list1.add(cbZZ14.selectionModel.selectedItem.toString())
                                }
                                list.addAll(list1)
                            }
                            if (!cbVV2.selectionModel.selectedItem.isNullOrEmpty()) {
                                list2.add(cbVV2.selectionModel.selectedItem.toString())
                                if (!cbZZ21.selectionModel.selectedItem.isNullOrEmpty()) {
                                    list2.add(cbZZ21.selectionModel.selectedItem.toString())
                                }
                                if (!cbZZ22.selectionModel.selectedItem.isNullOrEmpty()) {
                                    list2.add(cbZZ22.selectionModel.selectedItem.toString())
                                }
                                if (!cbZZ23.selectionModel.selectedItem.isNullOrEmpty()) {
                                    list2.add(cbZZ23.selectionModel.selectedItem.toString())
                                }
                                if (!cbZZ24.selectionModel.selectedItem.isNullOrEmpty()) {
                                    list2.add(cbZZ24.selectionModel.selectedItem.toString())
                                }
                                list.addAll(list2)
                            }
                            if (!cbVV3.selectionModel.selectedItem.isNullOrEmpty()) {
                                list3.add(cbVV3.selectionModel.selectedItem.toString())
                                if (!cbZZ31.selectionModel.selectedItem.isNullOrEmpty()) {
                                    list3.add(cbZZ31.selectionModel.selectedItem.toString())
                                }
                                if (!cbZZ32.selectionModel.selectedItem.isNullOrEmpty()) {
                                    list3.add(cbZZ32.selectionModel.selectedItem.toString())
                                }
                                if (!cbZZ33.selectionModel.selectedItem.isNullOrEmpty()) {
                                    list3.add(cbZZ33.selectionModel.selectedItem.toString())
                                }
                                if (!cbZZ34.selectionModel.selectedItem.isNullOrEmpty()) {
                                    list3.add(cbZZ34.selectionModel.selectedItem.toString())
                                }
                                list.addAll(list3)
                            }
                            if (!cbVV4.selectionModel.selectedItem.isNullOrEmpty()) {
                                list4.add(cbVV4.selectionModel.selectedItem.toString())
                                if (!cbZZ41.selectionModel.selectedItem.isNullOrEmpty()) {
                                    list4.add(cbZZ41.selectionModel.selectedItem.toString())
                                }
                                if (!cbZZ42.selectionModel.selectedItem.isNullOrEmpty()) {
                                    list4.add(cbZZ42.selectionModel.selectedItem.toString())
                                }
                                if (!cbZZ43.selectionModel.selectedItem.isNullOrEmpty()) {
                                    list4.add(cbZZ43.selectionModel.selectedItem.toString())
                                }
                                if (!cbZZ44.selectionModel.selectedItem.isNullOrEmpty()) {
                                    list4.add(cbZZ44.selectionModel.selectedItem.toString())
                                }
                                list.addAll(list4)
                            }
                            if (!cbVV5.selectionModel.selectedItem.isNullOrEmpty()) {
                                list5.add(cbVV5.selectionModel.selectedItem.toString())
                                if (!cbZZ51.selectionModel.selectedItem.isNullOrEmpty()) {
                                    list5.add(cbZZ51.selectionModel.selectedItem.toString())
                                }
                                if (!cbZZ52.selectionModel.selectedItem.isNullOrEmpty()) {
                                    list5.add(cbZZ52.selectionModel.selectedItem.toString())
                                }
                                if (!cbZZ53.selectionModel.selectedItem.isNullOrEmpty()) {
                                    list5.add(cbZZ53.selectionModel.selectedItem.toString())
                                }
                                if (!cbZZ54.selectionModel.selectedItem.isNullOrEmpty()) {
                                    list5.add(cbZZ54.selectionModel.selectedItem.toString())
                                }
                                list.addAll(list5)
                            }

                            val schemeList = (list1.toString() +
                                    list2.toString() +
                                    list3.toString() +
                                    list4.toString() +
                                    list5.toString())
                                .replace("[]", "")

                            transaction {
                                TestObjectsType.new {
                                    objectName = tfOI.text.toString()
                                    scheme = schemeList
                                }
                            }
                            Toast.makeText("Объект добавлен").show(Toast.ToastType.INFORMATION)
                            setItemsOnCb()
                            comboBoxTestItem.selectionModel.selectFirst()
                        }
                    }
                }
            }
        }
    }.addClass(Styles.hard)

    private fun isInBetween(): Boolean {
        var isValid = true
        allCb.forEach {
            if (!it.selectionModel.selectedItem.isNullOrEmpty()) {
                if (it.selectionModel.selectedItem.isInt()) {
                    if (it.selectionModel.selectedItem.toInt() > 25) {
                        isValid = false
                    }
                } else {
                    isValid = false
                }
            }
        }
        return isValid
    }

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
