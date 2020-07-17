package com.araratpaint.mypaint.io.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import java.io.File
import java.io.FileOutputStream

class ImageIO(width: Int = 0, height: Int = 0) {
    private var width: Int = 0
    private var height: Int = 0
    private var quickQuality: Boolean = true

    private var bitmap: Bitmap
    private var canvas: Canvas

    init {
        if (width <= 0 || height <= 0)
            throw java.lang.Exception("Image size must be greater than 0")

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        this.width = width
        this.height = height

        canvas = Canvas(bitmap)
    }

    fun draw(v: View): ImageIO {
        v.draw(canvas)

        return this
    }

    fun scale(w: Int, h: Int): ImageIO {
        if (w <= 0 || h <= 0)
            throw java.lang.Exception("Scalable size must be greater than 0")

        bitmap = Bitmap.createScaledBitmap(bitmap, w, h, quickQuality)
        this.width = w
        this.height = h

        return this
    }

    fun crop(x: Int, y: Int, width: Int, height: Int): ImageIO {
        bitmap = Bitmap.createBitmap(bitmap, x, y, width, height)

        this.width = width
        this.height = height

        return this
    }

    fun result(): Bitmap {
        return bitmap;
    }

    fun saveTo(path: String) {
        val file = File(path)

        val fostream: FileOutputStream
        try {
            file.createNewFile()
            fostream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fostream)
            fostream.flush()
            fostream.close()
        } catch (e: Exception) {
            throw e
        }
    }

    fun toArray(): Array<IntArray> {
        val m = Array(width) { IntArray(height) }

        var alpha: Int
        for (x in 0 until width)
            for (y in 0 until height) {
                alpha = Color.alpha(bitmap.getPixel(x, y))
                m[x][y] = if (alpha <= 10) 0 else 1
            }

        return m
    }
}