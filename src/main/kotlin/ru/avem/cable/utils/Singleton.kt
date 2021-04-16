package ru.avem.cable.utils

import ru.avem.cable.database.entities.Protocol
import ru.avem.cable.database.entities.ProtocolSingle
import ru.avem.cable.database.entities.TestObjectsType


object Singleton {
    lateinit var currentProtocol: Protocol
    lateinit var currentTestItem: TestObjectsType
}
