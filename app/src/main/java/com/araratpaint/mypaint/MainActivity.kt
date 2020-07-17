package com.araratpaint.mypaint

import android.Manifest
import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.SeekBar
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.app.Activity
import android.widget.TextView
import android.widget.Toast
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var surface: Surface
    private lateinit var recognition: Recognition

    private final var REQUEST_EXTERNAL_STORAGE: Int = 1
    private val PERMISSIONS_STORAGE =
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        verifyStoragePermissions(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        this.createAppDirectories()

        this.surface = Surface(this)
        this.recognition = Recognition(baseContext, this.surface, this)

        val drawLayout = findViewById<LinearLayout>(R.id.drawLayout)
        drawLayout.addView(this.surface)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflator = menuInflater
        inflator.inflate(R.menu.top_bar, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_clean_training -> {
            this.recognition.clearTraining()
            true
        }
        R.id.action_training_mode -> {
            startTrainingMode()
            true
        }
        R.id.action_about_author -> {
            displayAboutAuthorWindow()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun verifyStoragePermissions(activity: Activity) {
        val permission =
            ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    fun setupChangingBrushSize() {
        findViewById<SeekBar>(R.id.brushSizeBar).setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    changeBrushSize(seekBar.progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}

        })
    }

    fun startTrainingMode() {
        Toast.makeText(baseContext, "Training mode ON", Toast.LENGTH_SHORT).show()
        this.recognition.trainingMode()
        this.recognition.runTrainingMode()
    }

    fun setFooterText(text: String) {
        findViewById<TextView>(R.id.footerText).text = text
    }

    fun changeBrushSize(size: Int) {
        Toast.makeText(baseContext, "Brush size: $size", Toast.LENGTH_SHORT).show()
        surface.setBrushSize(size)
    }

    fun createAppDirectories() {
        val trainingDirPath = "${baseContext.filesDir.absolutePath}/training/"
        val trainingDir = File(trainingDirPath)
        if (!trainingDir.exists()) {
            trainingDir.mkdir()
        }
    }

    fun displayAboutAuthorWindow() {
        Toast.makeText(
            baseContext, "" +
                    "Author: Ararat Poghosyan \nE-mail: mistermoondi@gmail.com \n with love =)"
            , Toast.LENGTH_LONG
        ).show()
    }
}
