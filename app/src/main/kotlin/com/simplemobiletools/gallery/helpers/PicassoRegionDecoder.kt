package com.simplemobiletools.gallery.helpers

import android.content.Context
import android.graphics.*
import android.net.Uri
import com.davemorrissey.labs.subscaleview.decoder.ImageRegionDecoder

class PicassoRegionDecoder : ImageRegionDecoder {
    private var decoder: BitmapRegionDecoder? = null
    private val decoderLock = Any()

    override fun init(context: Context, uri: Uri): Point {
        val inputStream = context.contentResolver.openInputStream(uri)
        this.decoder = BitmapRegionDecoder.newInstance(inputStream, false)
        return Point(this.decoder!!.width, this.decoder!!.height)
    }

    override fun decodeRegion(rect: Rect, sampleSize: Int): Bitmap {
        synchronized(this.decoderLock) {
            val options = BitmapFactory.Options()
            options.inSampleSize = sampleSize
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            val bitmap = this.decoder!!.decodeRegion(rect, options)
            return bitmap ?: throw RuntimeException("Region decoder returned null bitmap - image format may not be supported")
        }
    }

    override fun isReady() = this.decoder != null && !this.decoder!!.isRecycled

    override fun recycle() {
        this.decoder!!.recycle()
    }
}
