package ru.avem.cable.app

import javafx.scene.image.Image
import javafx.scene.input.KeyCombination
import javafx.stage.Stage
import javafx.stage.StageStyle
import ru.avem.cable.database.validateDB
import ru.avem.cable.view.MainView
import ru.avem.cable.view.Styles
import ru.avem.kserialpooler.communication.PortDiscover
import tornadofx.App
import tornadofx.FX
import kotlin.system.exitProcess

class Cable : App(MainView::class, Styles::class) {

    companion object {
        var isAppRunning = true
    }

    override fun init() {
        validateDB()
    }

    override fun start(stage: Stage) {
        stage.isFullScreen = true
        stage.isResizable = false
        stage.initStyle(StageStyle.TRANSPARENT)
        stage.fullScreenExitKeyCombination = KeyCombination.NO_MATCH
        super.start(stage)
        FX.primaryStage.icons += Image("icon.png")
    }


    override fun stop() {
        isAppRunning = false
        PortDiscover.isPortDiscover = false
        super.stop()
    }
}
