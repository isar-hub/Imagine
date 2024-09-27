import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Rect
import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.objects.DetectedObject
import com.isar.imagine.barcode.InputInfo
import com.isar.imagine.utils.Utils
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.HashSet

/** View model for handling application workflow based on camera preview.  */
class WorkflowModel(application: Application) : AndroidViewModel(application) {

    val workflowState = MutableLiveData<WorkflowState>()
    val objectToSearch = MutableLiveData<DetectedObjectInfo>()
//    val searchedObject = MutableLiveData<SearchedObject>()
    val detectedBarcode = MutableLiveData<Barcode>()

    private val objectIdsToSearch = HashSet<Int>()

    var isCameraLive = false
        private set

    private var confirmedObject: DetectedObjectInfo? = null

    private val context: Context
        get() = getApplication<Application>().applicationContext

    /**
     * State set of the application workflow.
     */
    enum class WorkflowState {
        NOT_STARTED,
        DETECTING,
        DETECTED,
        CONFIRMING,
        CONFIRMED,
        SEARCHING,
        SEARCHED
    }

    @MainThread
    fun setWorkflowState(workflowState: WorkflowState) {
        if (workflowState != WorkflowState.CONFIRMED &&
            workflowState != WorkflowState.SEARCHING &&
            workflowState != WorkflowState.SEARCHED
        ) {
            confirmedObject = null
        }
        this.workflowState.value = workflowState
    }

    @MainThread
    fun confirmingObject(confirmingObject: DetectedObjectInfo, progress: Float) {
        val isConfirmed = progress.compareTo(1f) == 0
        if (isConfirmed) {
            confirmedObject = confirmingObject
            if (Utils.isAutoSearchEnabled(context)) {
                setWorkflowState(WorkflowState.SEARCHING)
                triggerSearch(confirmingObject)
            } else {
                setWorkflowState(WorkflowState.CONFIRMED)
            }
        } else {
            setWorkflowState(WorkflowState.CONFIRMING)
        }
    }

    @MainThread
    fun onSearchButtonClicked() {
        confirmedObject?.let {
            setWorkflowState(WorkflowState.SEARCHING)
            triggerSearch(it)
        }
    }

    private fun triggerSearch(detectedObject: DetectedObjectInfo) {
        val objectId = detectedObject.objectId ?: throw NullPointerException()
        if (objectIdsToSearch.contains(objectId)) {
            // Already in searching.
            return
        }

        objectIdsToSearch.add(objectId)
        objectToSearch.value = detectedObject
    }

    fun markCameraLive() {
        isCameraLive = true
        objectIdsToSearch.clear()
    }

    fun markCameraFrozen() {
        isCameraLive = false
    }

    fun onSearchCompleted(detectedObject: DetectedObjectInfo) {
        val lConfirmedObject = confirmedObject
        if (detectedObject != lConfirmedObject) {
            // Drops the search result from the object that has lost focus.
            return
        }

        objectIdsToSearch.remove(detectedObject.objectId)
        setWorkflowState(WorkflowState.SEARCHED)

    }
}
class DetectedObjectInfo(
    private val detectedObject: DetectedObject,
    val objectIndex: Int,
    private val inputInfo: InputInfo
) {

    private var bitmap: Bitmap? = null
    private var jpegBytes: ByteArray? = null

    val objectId: Int? = detectedObject.trackingId
    val boundingBox: Rect = detectedObject.boundingBox
    val labels: List<DetectedObject.Label> = detectedObject.labels

    val imageData: ByteArray?
        @Synchronized get() {
            if (jpegBytes == null) {
                try {
                    ByteArrayOutputStream().use { stream ->
                        getBitmap().compress(CompressFormat.JPEG, /* quality= */ 100, stream)
                        jpegBytes = stream.toByteArray()
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "Error getting object image data!")
                }
            }
            return jpegBytes
        }

    @Synchronized
    fun getBitmap(): Bitmap {
        return bitmap ?: let {
            val boundingBox = detectedObject.boundingBox
            val createdBitmap = Bitmap.createBitmap(
                inputInfo.getBitmap(),
                boundingBox.left,
                boundingBox.top,
                boundingBox.width(),
                boundingBox.height()
            )
            if (createdBitmap.width > MAX_IMAGE_WIDTH) {
                val dstHeight = (MAX_IMAGE_WIDTH.toFloat() / createdBitmap.width * createdBitmap.height).toInt()
                bitmap = Bitmap.createScaledBitmap(createdBitmap, MAX_IMAGE_WIDTH, dstHeight, /* filter= */ false)
            }
            createdBitmap
        }
    }

    companion object {
        private const val TAG = "DetectedObject"
        private const val MAX_IMAGE_WIDTH = 640
        private const val INVALID_LABEL = "N/A"

        fun hasValidLabels(detectedObject: DetectedObject): Boolean {
            return detectedObject.labels.isNotEmpty() &&
                    detectedObject.labels.none { label -> label.text == INVALID_LABEL }
        }
    }
}


