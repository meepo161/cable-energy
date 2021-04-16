package ru.avem.cable.controllers

import ru.avem.cable.communication.model.CommunicationModel
import ru.avem.cable.communication.model.CommunicationModel.getDeviceById
import ru.avem.cable.communication.model.devices.owen.pr.OwenPrController
import tornadofx.Controller

abstract class TestController : Controller() {
    protected val owenPR = getDeviceById(CommunicationModel.DeviceID.DD2) as OwenPrController
}
