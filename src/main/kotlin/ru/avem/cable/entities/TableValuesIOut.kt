package ru.avem.cable.entities

import javafx.beans.property.StringProperty

data class TableValuesIOut(
        var amperage: StringProperty
)

data class TableValuesIOutSet(
        var amperage: StringProperty
)

data class TableValueUOut(
        var voltage: StringProperty
)

data class TableValueTime(
        var time: StringProperty
)

data class TableValuesIn(
        var voltage: StringProperty,
        var amperage: StringProperty
)