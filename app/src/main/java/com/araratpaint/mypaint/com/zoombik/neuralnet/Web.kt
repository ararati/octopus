package com.araratpaint.mypaint.com.zoombik.neuralnet

class Web(countNeurons: Int, sizeX: Int, sizeY: Int) {
    private lateinit var neurons: Array<Neuron>

    var countNeurons: Int = 0

    var sizeX: Int = 0
    var sizeY: Int = 0
    private var input = Array(0) { IntArray(0) }

    private var maxSum: Int = 0
    var maxSumIndex: Int = 0


    init {
        this.countNeurons = countNeurons
        this.sizeX = sizeX
        this.sizeY = sizeY


        initNeurons()
    }

    private fun initNeurons() {

        val emptyWeights = Array(sizeX, { IntArray(sizeY) })

        for (x in 0 until sizeX) {
            for (y in 0 until sizeY) {
                emptyWeights[x][y] = 0
            }
        }

        neurons = Array(countNeurons) {
            Neuron(
                sizeX,
                sizeY,
                emptyWeights,
                it.toString()
            )
        }
    }

    fun input(inputArr: Array<IntArray>) {
        this.input = inputArr
    }

    fun recognizeNumber(inputArr: Array<IntArray>): Web {
        input(inputArr)
        recognize()

        return this
    }

    fun recognizeNumber(): Web {
        recognize()

        return this
    }

    private fun recognize(): Web {
        for (i in 0 until countNeurons) {
            neurons[i].input = this.input
            neurons[i].mulWeight()
            neurons[i].setSumOfSignals()
        }

        val nIndex = maxSumOfNeuron()

        neurons[nIndex].symbol()

        return this
    }

    fun res(): String {
        return neurons[maxSumIndex].symbol()
    }

    private fun maxSumOfNeuron(): Int {
        var maxSumIndex = 0;
        var maxSum = neurons[maxSumIndex].sumOfSignals

        for (i in 1 until countNeurons) {
            if (neurons[i].sumOfSignals > maxSum) {
                maxSum = neurons[i].sumOfSignals
                maxSumIndex = i
            }
        }

        this.maxSumIndex = maxSumIndex
        this.maxSum = maxSum

        return this.maxSumIndex
    }

    private fun rejectRes(neuronIndex: Int) {
        neurons[neuronIndex].decW(input)
    }

    fun getIndexNeuronBySymbol(sym: String): Int? {
        for (i in 0 until countNeurons)
            if (neurons[i].symbol() == sym)
                return i

        return null
    }

    private fun acceptRes(neuronIndex: Int) {
        neurons[neuronIndex].incW(input)
    }

    operator fun get(i: Int): Neuron {
        return neurons[i]
    }

    fun neuronsAsArray() {
        var m = Array(10) { Array(sizeX) { IntArray(sizeY) } }

        for ((i, v) in neurons.withIndex())
            m[i] = v.weight
    }

}