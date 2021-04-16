package ru.avem.cable.entities

import javafx.beans.property.StringProperty

data class TableValues(
        var voltage: StringProperty,
        var amperage: StringProperty,
        var time: StringProperty
)