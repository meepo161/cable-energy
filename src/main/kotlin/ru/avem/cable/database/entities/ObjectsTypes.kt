package ru.avem.cable.database.entities

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object ObjectsTypes : IntIdTable() {
    val objectName = varchar("objectName", 32)
    val cores = varchar("cores", 32)
    val scheme = varchar("scheme", 999999999)
}

class TestObjectsType(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TestObjectsType>(ObjectsTypes)

    var objectName by ObjectsTypes.objectName
    var cores by ObjectsTypes.cores
    var scheme by ObjectsTypes.scheme

    override fun toString(): String {
        return objectName
    }
}
