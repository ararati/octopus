package com.araratpaint.mypaint.com.zoombik.neuralnet

class Neuron {
    var sizeX: Int = 0
    var sizeY: Int = 0

    var mul = Array(0, { IntArray(0) })
    var weight = Array(0, { IntArray(0) })
    var input = Array(0, { IntArray(0) })

    var limit: Int = 500

    var sumOfSignals = 0

    private lateinit var symbol: String

    constructor()

    constructor(sizeX: Int, sizeY: Int, inP: Array<IntArray>, recognizeSymbol: String) {
        this.sizeX = sizeX
        this.sizeY = sizeY

        weight = Array(sizeX, { IntArray(sizeY) })

        mul = Array(sizeX, { IntArray(sizeY) })

        input = Array(sizeX, { IntArray(sizeY) })
        input = inP

        this.symbol = recognizeSymbol
    }

    fun symbol(): String {
        return this.symbol
    }

    fun mulWeight() {
        for (x in 0 until sizeX)
            for (y in 0 until sizeY)
                mul[x][y] = input[x][y] * weight[x][y]
    }

    fun setSumOfSignals() {
        sumOfSignals = 0

        for (x in 0 until sizeX)
            for (y in 0 until sizeY)
                sumOfSignals += mul[x][y]
    }

    fun result(): Boolean {
        return sumOfSignals >= limit
    }

    fun incW(inP: Array<IntArray>) {
        for (x in 0 until sizeX)
            for (y in 0 until sizeY)
                weight[x][y] += inP[x][y]
    }

    fun decW(inP: Array<IntArray>) {
        for (x in 0 until sizeX)
            for (y in 0 until sizeY)
                weight[x][y] -= inP[x][y]
    }
}