package com.example.a3dmodelviewer.model


enum class BundledModel(
    val assetName: String,
    val displayName: String
) {
    CHAIR("chair", "Chair"),
    TABLE("table", "Table"),
    LAMP("lamp", "Lamp"),
    CAR("car", "Car"),
    HELMET("helmet", "Helmet");
}