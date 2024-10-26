import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.isar.imagine.utils.CommonMethods
import com.isar.imagine.utils.Results
import dashboard.models.BarChartModel
import dashboard.models.PieChartModel

class DashboardViewmodel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _brands = MutableLiveData<Results<BarChartModel>>()
    val brands: LiveData<Results<BarChartModel>> get() = _brands

    private val _conditionWise = MutableLiveData<Results<PieChartModel>>()
    val conditionWise: LiveData<Results<PieChartModel>> get() = _conditionWise

    init {
        getData()
    }

    private fun getData() {
        _brands.value = Results.Loading() // Set loading state
        firestore.collection("inventory").get()
            .addOnSuccessListener { result ->
                val brandMap = mutableMapOf<String,Float>()
                val quantityList: MutableList<Float> = mutableListOf()
                val conditionMap = mutableMapOf<String, Float>()
                val conditions = listOf("Gold", "Silver", "Platinum")
                conditions.forEach { conditionMap[it] = 0.0f }
                for (document in result) {
                    CommonMethods.showLogs("logging","data ${document.data}")
                    val inventory = document.data
                    val brand = inventory["brand"].toString()
                    val quantity = (inventory["quantity"] as? Number)?.toFloat() ?: 0.0f
                    val condition = inventory["condition"].toString()
                    quantityList.add(quantity)
                    brandMap[brand] = conditionMap.getOrPut( condition) {quantity}+ quantity
                    conditionMap[condition] = conditionMap.getOrDefault(condition, 0.0f) + quantity

                }
                val barChartModel = BarChartModel(brandMap.keys.toList(),brandMap.values.toList())
                val pieChart = PieChartModel(conditionMap.keys.toList(),conditionMap.values.toList())
                _brands.value = Results.Success(barChartModel) // Set success state
                _conditionWise.value = Results.Success(pieChart)
            }
            .addOnFailureListener { exception ->
                _brands.value = Results.Error(exception.message ?: "Unknown error")
            }
    }
}
