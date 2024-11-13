package com.isar.imagine.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.YuvImage
import android.hardware.Camera
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import com.google.android.gms.common.images.Size
import com.google.android.material.button.MaterialButton
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.isar.imagine.R
import com.isar.imagine.barcode.CameraSizePair
import com.isar.imagine.barcode.GraphicOverlay
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
 fun Context.getTextView(message: String): TextView{
    return TextView(this).apply {
        text = message
    }
}

object Utils {
    const val ASPECT_RATIO_TOLERANCE = 0.01f

    fun now(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    }

    fun convertToBitmap(data: ByteBuffer, width: Int, height: Int, rotationDegrees: Int): Bitmap? {
        data.rewind()
        val imageInBuffer = ByteArray(data.limit())
        data.get(imageInBuffer, 0, imageInBuffer.size)
        try {
            val image = YuvImage(
                imageInBuffer, InputImage.IMAGE_FORMAT_NV21, width, height, null
            )
            val stream = ByteArrayOutputStream()
            image.compressToJpeg(Rect(0, 0, width, height), 80, stream)
            val bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size())
            stream.close()

            // Rotate the image back to straight.
            val matrix = Matrix()
            matrix.postRotate(rotationDegrees.toFloat())
            return Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, true)
        } catch (e: java.lang.Exception) {
            Log.e("Utils", "Error: " + e.message)
        }
        return null
    }

    fun getUserSpecifiedPreviewSize(context: Context): CameraSizePair? {
        return try {
            val previewSizePrefKey = context.getString(R.string.pref_key_rear_camera_preview_size)
            val pictureSizePrefKey = context.getString(R.string.pref_key_rear_camera_picture_size)
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val previewSize =
                Size.parseSize(sharedPreferences.getString(previewSizePrefKey, null)!!)
            val pictureSize =
                Size.parseSize(sharedPreferences.getString(pictureSizePrefKey, null)!!)
            CameraSizePair(previewSize, pictureSize)

        } catch (e: Exception) {
            null
        }
    }

    private fun getBooleanPref(
        context: Context, @StringRes prefKeyId: Int, defaultValue: Boolean
    ): Boolean = PreferenceManager.getDefaultSharedPreferences(context)
        .getBoolean(context.getString(prefKeyId), defaultValue)

    private fun getIntPref(context: Context, @StringRes prefKeyId: Int, defaultValue: Int): Int {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val prefKey = context.getString(prefKeyId)
        return sharedPreferences.getInt(prefKey, defaultValue)
    }

    fun getBarcodeReticleBox(overlay: GraphicOverlay): RectF {
        val context = overlay.context
        val overlayWidth = overlay.width.toFloat()
        val overlayHeight = overlay.height.toFloat()
        val boxWidth =
            overlayWidth * getIntPref(context, R.string.pref_key_barcode_reticle_width, 80) / 100
        val boxHeight =
            overlayHeight * getIntPref(context, R.string.pref_key_barcode_reticle_height, 35) / 100
        val cx = overlayWidth / 2
        val cy = overlayHeight / 2
        return RectF(cx - boxWidth / 2, cy - boxHeight / 2, cx + boxWidth / 2, cy + boxHeight / 2)
    }

    fun getProgressToMeetBarcodeSizeRequirement(overlay: GraphicOverlay, barcode: Barcode): Float {
        val context = overlay.context
        return if (getBooleanPref(context, R.string.pref_key_enable_barcode_size_check, false)) {
            val reticleBoxWidth = getBarcodeReticleBox(overlay).width()
            val barcodeWidth = overlay.translateX(barcode.boundingBox?.width()?.toFloat() ?: 0f)
            val requiredWidth = reticleBoxWidth * getIntPref(
                context, R.string.pref_key_minimum_barcode_width, 50
            ) / 100
            (barcodeWidth / requiredWidth).coerceAtMost(1f)
        } else {
            1f
        }
    }

    fun isPortraitMode(context: Context): Boolean =
        context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    fun isAutoSearchEnabled(context: Context): Boolean =
        getBooleanPref(context, R.string.pref_key_enable_auto_search, true)

    fun shouldDelayLoadingBarcodeResult(context: Context): Boolean =
        getBooleanPref(context, R.string.pref_key_delay_loading_barcode_result, true)

    /**
     * Generates a list of acceptable preview sizes. Preview sizes are not acceptable if there is not
     * a corresponding picture size of the same aspect ratio. If there is a corresponding picture size
     * of the same aspect ratio, the picture size is paired up with the preview size.
     *
     *
     * This is necessary because even if we don't use still pictures, the still picture size must
     * be set to a size that is the same aspect ratio as the preview size we choose. Otherwise, the
     * preview images may be distorted on some devices.
     */
    fun generateValidPreviewSizeList(camera: Camera): List<CameraSizePair> {
        val parameters = camera.parameters
        val supportedPreviewSizes = parameters.supportedPreviewSizes
        val supportedPictureSizes = parameters.supportedPictureSizes
        val validPreviewSizes = ArrayList<CameraSizePair>()
        for (previewSize in supportedPreviewSizes) {
            val previewAspectRatio = previewSize.width.toFloat() / previewSize.height.toFloat()

            // By looping through the picture sizes in order, we favor the higher resolutions.
            // We choose the highest resolution in order to support taking the full resolution
            // picture later.
            for (pictureSize in supportedPictureSizes) {
                val pictureAspectRatio = pictureSize.width.toFloat() / pictureSize.height.toFloat()
                if (abs(previewAspectRatio - pictureAspectRatio) < ASPECT_RATIO_TOLERANCE) {
                    validPreviewSizes.add(CameraSizePair(previewSize, pictureSize))
                    break
                }
            }
        }

        // If there are no picture sizes with the same aspect ratio as any preview sizes, allow all of
        // the preview sizes and hope that the camera can handle it.  Probably unlikely, but we still
        // account for it.
        if (validPreviewSizes.isEmpty()) {
            Log.e("Utils", "No preview sizes have a corresponding same-aspect-ratio picture size.")
            for (previewSize in supportedPreviewSizes) {
                // The null picture size will let us know that we shouldn't set a picture size.
                validPreviewSizes.add(CameraSizePair(previewSize, null))
            }
        }

        return validPreviewSizes
    }

    fun saveStringPreference(context: Context, @StringRes prefKeyId: Int, value: String?) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
            .putString(context.getString(prefKeyId), value).apply()
    }

    fun showProgressDialog(textmessage: String) {

    }

}


object CustomProgressBar {

    private var dialog: Dialog? = null

    fun show(context: Context, message: String) {
        if (dialog == null) {
            dialog = Dialog(context).apply {
                setCancelable(false)
                setContentView(LayoutInflater.from(context).inflate(R.layout.progress_dialog, null))
            }
        }

        dialog?.findViewById<TextView>(R.id.progress_message)?.text = message
        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
        dialog = null
    }
}


object CustomDialog {

    private var dialog: AlertDialog? = null

    fun showAlertDialog(
        context: Context,
        message: View,
        header: String,
        onPositiveAction: (() -> Unit)? = null,
        onNegativeAction: (() -> Unit)? = null
    ) {
        if (dialog == null) {
            val dialogView = View.inflate(context, R.layout.custom_dialog_layout, null)

            val container: ViewGroup = dialogView.findViewById(R.id.body)
            dialogView.findViewById<MaterialButton>(R.id.cancel).setOnClickListener {
                onNegativeAction?.invoke()
                dismiss()
            }
            dialogView.findViewById<MaterialButton>(R.id.ok).setOnClickListener {
                onPositiveAction?.invoke()
                dismiss()
            }
            dialogView.findViewById<TextView>(R.id.header).text = header
            container.removeAllViews()
            container.addView(message)

            dialog = AlertDialog.Builder(context).setView(dialogView).create()
        }
        dialog?.show()
    }


    fun dismiss() {
        dialog?.dismiss()
        dialog = null
    }
}

