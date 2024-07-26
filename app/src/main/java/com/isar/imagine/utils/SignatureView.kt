package com.isar.imagine.utils

import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlin.math.max
import kotlin.math.min

class SignatureView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint()
    private val path = Path()
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private val dirtyRect = RectF()
    private val STROKE_WIDTH = 5f
    private val HALF_STROKE_WIDTH = STROKE_WIDTH / 2



    lateinit var bitmap: Bitmap

    init {
        paint.isAntiAlias = true
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeWidth = STROKE_WIDTH
    }


    fun clear() {
        path.reset()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val eventX = event.x
        val eventY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(eventX, eventY)
                lastTouchX = eventX
                lastTouchY = eventY
                return true
            }
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                resetDirtyRect(eventX, eventY)
                for (i in 0 until event.historySize) {
                    val historicalX = event.getHistoricalX(i)
                    val historicalY = event.getHistoricalY(i)
                    expandDirtyRect(historicalX, historicalY)
                    path.lineTo(historicalX, historicalY)
                }
                path.lineTo(eventX, eventY)
            }
            else -> return false
        }
        invalidate(
            (dirtyRect.left - HALF_STROKE_WIDTH).toInt(),
            (dirtyRect.top - HALF_STROKE_WIDTH).toInt(),
            (dirtyRect.right + HALF_STROKE_WIDTH).toInt(),
            (dirtyRect.bottom + HALF_STROKE_WIDTH).toInt()
        )
        lastTouchX = eventX
        lastTouchY = eventY
        return true
    }

    private fun expandDirtyRect(historicalX: Float, historicalY: Float) {
        if (historicalX < dirtyRect.left) {
            dirtyRect.left = historicalX
        } else if (historicalX > dirtyRect.right) {
            dirtyRect.right = historicalX
        }
        if (historicalY < dirtyRect.top) {
            dirtyRect.top = historicalY
        } else if (historicalY > dirtyRect.bottom) {
            dirtyRect.bottom = historicalY
        }
    }

    private fun resetDirtyRect(eventX: Float, eventY: Float) {
        dirtyRect.left = min(lastTouchX, eventX)
        dirtyRect.right = max(lastTouchX, eventX)
        dirtyRect.top = min(lastTouchY, eventY)
        dirtyRect.bottom = max(lastTouchY, eventY)
    }

    private fun getSignatureStorageDirectory(): File {
        val cw = ContextWrapper(context)
        return cw.getDir("imageDir", Context.MODE_PRIVATE)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDateTime(): String {
        // Get the current date and time
        val currentDateTime = LocalDateTime.now()

        // Define the format
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        // Format the date and time
        return currentDateTime.format(formatter)
    }
}
