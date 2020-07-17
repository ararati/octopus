package com.araratpaint.mypaint.com.zoombik.neuralnet

class NeuralFacade(countNeurons: Int, sizeX: Int, sizeY: Int) {
    private var web: Web

    init {
        web = Web(countNeurons, sizeX, sizeY)
    }

    fun getCountNeurons(): Int {
        return web.countNeurons
    }

    fun recognizeSymbol(imageAsMatrix: Array<IntArray>) {
        web.recognizeNumber(imageAsMatrix)
    }

    fun getRecognizedSymbol(): String {
        return getRecognizedNeuron().symbol()
    }

    fun getRecognizedNeuron(): Neuron {
        return web[web.maxSumIndex]
    }

    fun getWeightOfNeuron(index: Int): Array<IntArray> {
        return web[index].weight;
    }

    fun setNeuronWeightValue(neuronIndex: Int, x: Int, y: Int, value: Int) {
        web[neuronIndex].weight[x][y] = value
    }

    fun addWeightToNeuron(index: Int, value: Array<IntArray>) {
        web[index].incW(value)
    }
}