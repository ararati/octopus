package com.araratpaint.mypaint

import android.content.Context
import android.widget.Toast
import com.araratpaint.mypaint.com.zoombik.neuralnet.NeuralFacade
import java.io.File
import java.lang.Exception

class Recognition() {

    private lateinit var drawingSurface: Surface
    private lateinit var neuralFacade: NeuralFacade
    private lateinit var mainActivity: MainActivity
    private lateinit var baseContext: Context
    private lateinit var appPath: String

    private var trainingNum = 0
    private var training = false
    private var trainingIteration = 0
    private var trainingEachNum = 2

    companion object {
        const val SIZE_X = 28
        const val SIZE_Y = 28

        private const val countNeurons = 10
    }

    constructor(context: Context, surface: Surface, activity: MainActivity) : this() {
        this.appPath = context.filesDir.absolutePath
        this.baseContext = context
        this.drawingSurface = surface
        this.mainActivity = activity

        this.drawingSurface.onTouchStateChanged = { _, newValue ->
            userTouchHandler(newValue)
        }

        neuralFacade = NeuralFacade(countNeurons, SIZE_X, SIZE_Y)

        createTrainingFiles()
        this.loadTraining()
    }

    fun userTouchHandler(surfaceState: Int) {
        if (Surface.TOUCH_END_EVENT === surfaceState) {
            if (training) {
                runTrainingMode()
            } else {
                neuralFacade.recognizeSymbol(this.drawingSurface.getImageAsMatrix())
                mainActivity.setFooterText(neuralFacade.getRecognizedSymbol())
            }
        }

    }

    fun clearTraining() {
        for (i in 0 until neuralFacade.getCountNeurons()) {
            val file = File(getTrainingFilePath(i))
            val data = neuralFacade.getWeightOfNeuron(i)
            try {
                file.createNewFile()
                file.writeText("")

                for (x in 0 until data.size) {
                    for (y in 0 until data[0].size) {
                        file.appendText(0.toString())
                        if (y + 1 != data[0].size) file.appendText(" ")
                    }

                    if (x + 1 < data.size)
                        file.appendText("\n")
                }

            } catch (e: Exception) {
                throw e
            }
        }
    }

    fun saveTraining() {
        for (i in 0 until neuralFacade.getCountNeurons()) {
            saveWeight(getTrainingFilePath(i), neuralFacade.getWeightOfNeuron(i))
        }
    }

    private fun saveWeight(path: String, data: Array<IntArray>) {
        val file = File(path)

        try {
            file.createNewFile()
            file.writeText("")

            for (x in 0 until data.size) {
                for (y in 0 until data[0].size) {
                    file.appendText(data[x][y].toString())
                    if (y + 1 != data[0].size)
                        file.appendText(" ");
                }

                if (x + 1 < data.size)
                    file.appendText("\n")
            }

        } catch (e: Exception) {
            throw e
        }
    }

    fun loadTraining() {
        var x = 0

        try {
            for (i in 0 until countNeurons) {
                x = 0
                File(getTrainingFilePath(i)).forEachLine {
                    it.split(" ").forEachIndexed { y, v ->
                        neuralFacade.setNeuronWeightValue(i, x, y, v.toInt())
                    }
                    if (x < SIZE_X)
                        x++
                }
            }
        } catch (e: Exception) {
            Toast.makeText(baseContext, e.message + "x $x", Toast.LENGTH_SHORT).show()
        }
    }

    fun createTrainingFiles() {
        for (i in 0 until countNeurons) {
            var file = File(getTrainingFilePath(i));
            if (!file.exists()) {
                file.createNewFile()
            }
        }
    }

    fun getTrainingFilePath(trainingNum: Int): String {
        return appPath + "/training/$trainingNum.txt"
    }

    fun runTrainingMode() {
        neuralFacade.addWeightToNeuron(trainingNum, drawingSurface.getImageAsMatrix())

        if (training) {
            printTrainingSymbol()
        }

        trainingIteration++

        if (trainingIteration == trainingEachNum && trainingNum == 9) {
            training = false
            saveTraining()
//            invalidate()
        }

        if (trainingIteration == trainingEachNum) {
            trainingIteration = 0
            trainingNum++
        }

        this.drawingSurface.clear()
    }

    fun printTrainingSymbol() {
        mainActivity.setFooterText("$trainingNum ($trainingIteration/$trainingEachNum)")
    }

    fun trainingMode() {
        trainingNum = 0
        training = true
        trainingIteration = 0
        trainingEachNum = 5

        this.drawingSurface.clear()
    }
}