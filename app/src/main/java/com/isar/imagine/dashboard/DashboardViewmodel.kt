import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.isar.imagine.utils.CommonMethods
import com.isar.imagine.utils.Results
import com.isar.imagine.dashboard.models.BarChartModel
import com.isar.imagine.dashboard.models.PieChartModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DashboardViewmodel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _brands = MutableLiveData<Results<BarChartModel>>()
    val brands: LiveData<Results<BarChartModel>> get() = _brands

    private val _conditionWise = MutableLiveData<Results<PieChartModel>>()
    val conditionWise: LiveData<Results<PieChartModel>> get() = _conditionWise

    init {
        viewModelScope.launch {
            getData()
        }

    }

    private suspend fun getData() {
        _brands.value = Results.Loading() // Set loading state
        CommonMethods.showLogs("getData", "Fetching data from Firestore...")
        try {
            val brands = firestore.collection("inventory").get().await()
            CommonMethods.showLogs(
                "getData",
                "Firestore query completed. Total documents: ${brands.size()}"
            )

            if (!brands.isEmpty) {
                val brandMap = mutableMapOf<String, Long>()
                val conditionMap = mutableMapOf<String, Long>()
                val conditions = listOf("Gold", "Silver", "Platinum")
                conditions.forEach {
                    conditionMap[it] = 0
                    CommonMethods.showLogs("getData", "Initialized condition: $it with 0.0f")
                }
                for (document in brands) {
                    CommonMethods.showLogs("getData", "Processing document: ${document.id}")
                    val inventory = document.data
                    CommonMethods.showLogs("getData", "Document data: $inventory")

                    val brand = inventory["brand"].toString()
                    val quantity = (inventory["quantity"] as? Number)?.toLong() ?: 0
                    val condition = inventory["condition"].toString()
                    CommonMethods.showLogs(
                        "getData",
                        "Brand: $brand, Quantity: $quantity, Condition: $condition"
                    )
                    brandMap[brand] = brandMap.getOrDefault(brand, 0) + quantity
                    conditionMap[condition] = conditionMap.getOrDefault(condition, 0) + quantity

                }

                CommonMethods.showLogs("getData", "Final brandMap: $brandMap")
                CommonMethods.showLogs("getData", "Final conditionMap: $conditionMap")
                val barChartModel = BarChartModel(brandMap)
                val pieChart = PieChartModel(conditionMap)

                _brands.value = Results.Success(barChartModel) // Set success state
                _conditionWise.value = Results.Success(pieChart)
                CommonMethods.showLogs("getData", "Success state updated with charts")

            } else {
                _brands.value = Results.Error("No Brands Available")
                _conditionWise.value = Results.Error("No Brands Available")
            }

        } catch (e: Exception) {
            _brands.value = Results.Error("Error : ${e.message}")
            _conditionWise.value = Results.Error("Error : ${e.message}")
        }

    }
}
