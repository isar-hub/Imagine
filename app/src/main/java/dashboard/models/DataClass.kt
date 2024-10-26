package dashboard.models

data class BarChartModel(
    val brandName : List<String>,
    val quantity : List<Float>

)
data class PieChartModel(
    val condition : List<String>,
    val quantity : List<Float>

)

