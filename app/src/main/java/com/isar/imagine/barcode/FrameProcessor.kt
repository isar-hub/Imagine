package com.isar.imagine.barcode


import android.graphics.Bitmap
import java.nio.ByteBuffer
import android.os.SystemClock
import android.util.Log
import androidx.annotation.GuardedBy
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskExecutors
import com.google.mlkit.vision.common.InputImage
import com.isar.imagine.utils.Utils

import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicBoolean


/** An interface to process the input camera frame and perform detection on it.  */
    interface FrameProcessor {

        /** Processes the input frame with the underlying detector.  */
        fun process(data: ByteBuffer, frameMetadata: FrameMetadata, graphicOverlay: GraphicOverlay)

        /** Stops the underlying detector and release resources.  */
        fun stop()
    }

/** Metadata info of a camera frame.  */
class FrameMetadata(val width: Int, val height: Int, val rotation: Int)


/** Abstract base class of [FrameProcessor].  */
abstract class FrameProcessorBase<T> : FrameProcessor {

    // To keep the latest frame and its metadata.
    @GuardedBy("this")
    private var latestFrame: ByteBuffer? = null

    @GuardedBy("this")
    private var latestFrameMetaData: FrameMetadata? = null

    // To keep the frame and metadata in process.
    @GuardedBy("this")
    private var processingFrame: ByteBuffer? = null

    @GuardedBy("this")
    private var processingFrameMetaData: FrameMetadata? = null
    private val executor = ScopedExecutor(TaskExecutors.MAIN_THREAD)

    @Synchronized
    override fun process(
        data: ByteBuffer,
        frameMetadata: FrameMetadata,
        graphicOverlay: GraphicOverlay
    ) {
        latestFrame = data
        latestFrameMetaData = frameMetadata
        if (processingFrame == null && processingFrameMetaData == null) {
            processLatestFrame(graphicOverlay)
        }
    }

    @Synchronized
    private fun processLatestFrame(graphicOverlay: GraphicOverlay) {
        processingFrame = latestFrame
        processingFrameMetaData = latestFrameMetaData
        latestFrame = null
        latestFrameMetaData = null
        val frame = processingFrame ?: return
        val frameMetaData = processingFrameMetaData ?: return
        val image = InputImage.fromByteBuffer(
            frame,
            frameMetaData.width,
            frameMetaData.height,
            frameMetaData.rotation,
            InputImage.IMAGE_FORMAT_NV21
        )
        val startMs = SystemClock.elapsedRealtime()
        detectInImage(image)
            .addOnSuccessListener(executor) { results: T ->
                Log.d(TAG, "Latency is: ${SystemClock.elapsedRealtime() - startMs}")
                this@FrameProcessorBase.onSuccess(CameraInputInfo(frame, frameMetaData), results, graphicOverlay)
                processLatestFrame(graphicOverlay)
            }
            .addOnFailureListener(executor) { e -> OnFailureListener { this@FrameProcessorBase.onFailure(it) } }
    }

    override fun stop() {
        executor.shutdown()
    }

    protected abstract fun detectInImage(image: InputImage): Task<T>

    /** Be called when the detection succeeds.  */
    protected abstract fun onSuccess(
        inputInfo: InputInfo,
        results: T,
        graphicOverlay: GraphicOverlay
    )

    protected abstract fun onFailure(e: Exception)

    companion object {
        private const val TAG = "FrameProcessorBase"
    }
}


/**
 * Wraps an existing executor to provide a [.shutdown] method that allows subsequent
 * cancellation of submitted runnables.
 */
class ScopedExecutor(private val executor: Executor) : Executor {
    private val shutdown = AtomicBoolean()
    override fun execute(command: Runnable) {
        // Return early if this object has been shut down.
        if (shutdown.get()) {
            return
        }
        executor.execute {

            // Check again in case it has been shut down in the mean time.
            if (shutdown.get()) {
                return@execute
            }
            command.run()
        }
    }

    /**
     * After this method is called, no runnables that have been submitted or are subsequently
     * submitted will start to execute, turning this executor into a no-op.
     *
     *
     * Runnables that have already started to execute will continue.
     */
    fun shutdown() {
        shutdown.set(true)
    }
}

interface InputInfo {
    fun getBitmap(): Bitmap
}

class CameraInputInfo(
    private val frameByteBuffer: ByteBuffer,
    private val frameMetadata: FrameMetadata
) : InputInfo {

    private var bitmap: Bitmap? = null

    @Synchronized
    override fun getBitmap(): Bitmap {
        return bitmap ?: let {
            bitmap = Utils.convertToBitmap(
                frameByteBuffer, frameMetadata.width, frameMetadata.height, frameMetadata.rotation
            )
            bitmap!!
        }
    }
}

class BitmapInputInfo(private val bitmap: Bitmap) : InputInfo {
    override fun getBitmap(): Bitmap {
        return bitmap
    }
}