package com.isar.imagine.dashboard.models

data class BarChartModel(val brandQuantity : Map<String,Long>)
data class PieChartModel(val map: Map<String,Long>)
