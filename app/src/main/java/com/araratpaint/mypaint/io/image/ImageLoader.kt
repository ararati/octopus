package com.araratpaint.mypaint.io.image

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.io.File
import java.io.FileOutputStream

class ImageLoader(private val path: String) : Target {
    override fun onPrepareLoad(arg0: Drawable?) {
        //TODO
    }

    override fun onBitmapLoaded(bitmap: Bitmap, arg1: Picasso.LoadedFrom) {
        val file = File(path)

        try {
            file.createNewFile()
            val fostream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fostream)
            fostream.flush()
            fostream.close()
        } catch (e: Throwable) {
            throw  e
        }

    }

    override fun onBitmapFailed(arg0: Drawable?) {
        //TODO
    }
}