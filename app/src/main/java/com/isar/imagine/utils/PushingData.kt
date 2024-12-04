package com.isar.imagine.utils

import com.google.firebase.firestore.FirebaseFirestore

class PushingData{


    private val db = FirebaseFirestore.getInstance()

    // Function to add a brand
    fun addBrand(brand : List<Brand>) {
        val brandId = db.collection("brands")
        for (i in brand){
            brandId.document(brand[0].name.lowercase()).set(i).addOnSuccessListener {
                CommonMethods.showLogs("FirebaseRepository", "Succefull")
            }.addOnFailureListener { e ->
                CommonMethods.showLogs("FirebaseRepository", "Error adding brand $e")
            }
        }



    }

    // Function to add a model
    private fun addModel(brandId: String, model: Model) {
        val modelData = hashMapOf(
            "name" to model.name,
            "imageUrl" to model.imageUrl,
            "variants" to model.variants.map { mapOf("variant" to it.variant) }
        )

        val modelId = db.collection("brands").document(brandId).collection("models").document().id
        db.collection("brands").document(brandId).collection("models").document(modelId).set(modelData)
            .addOnSuccessListener {
                CommonMethods.showLogs("FirebaseRepository", "Model added successfully")
            }
            .addOnFailureListener { e ->
                CommonMethods.showLogs("FirebaseRepository", "Error adding model $e")
            }
    }
}

data class Brand(
    val name: String = "",
    val models: List<Model> = emptyList()
)

data class Model(
    val name: String,
    val imageUrl: String,
    val variants: List<Variant> = emptyList()
)

data class Variant(
    val variant: String = "", // 8GB-128GB-Black
)
class Data{

    val brands = listOf(
        Brand(
            name = "Samsung",
            models = listOf(
                Model(
                    name = "Galaxy S10",
                    imageUrl = "https://www.canva.com/design/DAGYOpTrocU/V_Msx1pEHiKLNm5M0_6NkA/view",
                    variants = listOf(
                        Variant(variant = "3GB-64GB-Black"),
                        Variant(variant = "3GB-128GB-Black"),
                        Variant(variant = "3GB-256GB-Black"),
                    )
                ),
                Model(
                    name = "Galaxy S10+",
                    imageUrl = "https://www.canva.com/design/DAGYOpwclCY/3723R2q2fEXOdgM3K7F2Tw/view",
                    variants = listOf(
                        Variant(variant = "3GB-64GB-Black"),
                        Variant(variant = "3GB-128GB-Black"),
                        Variant(variant = "3GB-256GB-Black"),
                    )
                ),
                Model(
                    name = "Galaxy S21",
                    imageUrl = "https://www.canva.com/design/DAGYOrGs1yo/KjUBnx7LzN2Amt_H-nTZ_Q/view",
                    variants = listOf(
                        Variant(variant = "3GB-64GB-Black"),
                        Variant(variant = "3GB-128GB-Black"),
                        Variant(variant = "3GB-256GB-Black"),
                    )
                )

            )
        ),
        Brand(

            name = "Apple",
            models = listOf(
                Model(
                    name = "iPhone 6",
                    imageUrl = "https://www.canva.com/design/DAGYOoO2ac0/_oL6wk4a5rxF0uwnz48a0g/view",
                    variants = listOf(
                        Variant(variant = "1GB-16GB-Space Grey"),
                        Variant(variant = "1GB-32GB-Space Grey"),
                        Variant(variant = "1GB-64GB-Space Grey"),
                        Variant(variant = "1GB-128GB-Space Grey"),

                        Variant(variant = "1GB-16GB-Silver"),
                        Variant(variant = "1GB-32GB-Silver"),
                        Variant(variant = "1GB-64GB-Silver"),
                        Variant(variant = "1GB-128GB-Silver"),

                        Variant(variant = "1GB-16GB-Gold"),
                        Variant(variant = "1GB-32GB-Gold"),
                        Variant(variant = "1GB-64GB-Gold"),
                        Variant(variant = "1GB-128GB-Gold")
                    )
                ),
                Model(
                    name = "iPhone 6 Plus",
                    imageUrl = "https://www.canva.com/design/DAGYOj6DeKw/E_4D2TQZVKU57iDI8EFcug/view",
                    variants = listOf(
                        Variant(variant = "1GB-16GB-Space Grey"),
                        Variant(variant = "1GB-64GB-Space Grey"),
                        Variant(variant = "1GB-128GB-Space Grey"),

                        Variant(variant = "1GB-16GB-Silver"),
                        Variant(variant = "1GB-64GB-Silver"),
                        Variant(variant = "1GB-128GB-Silver"),

                        Variant(variant = "1GB-16GB-Gold"),
                        Variant(variant = "1GB-64GB-Gold"),
                        Variant(variant = "1GB-128GB-Gold")
                    )
                ),
                Model(
                    name = "iPhone 6S",
                    imageUrl = "https://www.canva.com/design/DAGYOvviT6k/SDazI81w2jee8WijTSIxSA/view",
                    variants = listOf(
                        Variant(variant = "2GB-16GB-Space Grey"),
                        Variant(variant = "2GB-32GB-Space Grey"),
                        Variant(variant = "2GB-64GB-Space Grey"),
                        Variant(variant = "2GB-128GB-Space Grey"),

                        Variant(variant = "2GB-16GB-Silver"),
                        Variant(variant = "2GB-32GB-Silver"),
                        Variant(variant = "2GB-64GB-Silver"),
                        Variant(variant = "2GB-128GB-Silver"),

                        Variant(variant = "2GB-16GB-Gold"),
                        Variant(variant = "2GB-32GB-Gold"),
                        Variant(variant = "2GB-64GB-Gold"),
                        Variant(variant = "2GB-128GB-Gold"),

                        Variant(variant = "2GB-16GB-Rose Gold"),
                        Variant(variant = "2GB-32GB-Rose Gold"),
                        Variant(variant = "2GB-64GB-Rose Gold"),
                        Variant(variant = "2GB-128GB-Rose Gold")
                    )
                ),
                Model(
                    name = "iPhone 6S Plus",
                    imageUrl = "https://www.canva.com/design/DAGYOqktm84/74hHOAd3v2WKCnXG8guiYQ/view",
                    variants = listOf(
                        Variant(variant = "2GB-16GB-Space Grey"),
                        Variant(variant = "2GB-32GB-Space Grey"),
                        Variant(variant = "2GB-64GB-Space Grey"),
                        Variant(variant = "2GB-128GB-Space Grey"),

                        Variant(variant = "2GB-16GB-Silver"),
                        Variant(variant = "2GB-32GB-Silver"),
                        Variant(variant = "2GB-64GB-Silver"),
                        Variant(variant = "2GB-128GB-Silver"),

                        Variant(variant = "2GB-16GB-Gold"),
                        Variant(variant = "2GB-32GB-Gold"),
                        Variant(variant = "2GB-64GB-Gold"),
                        Variant(variant = "2GB-128GB-Gold"),

                        Variant(variant = "2GB-16GB-Rose Gold"),
                        Variant(variant = "2GB-32GB-Rose Gold"),
                        Variant(variant = "2GB-64GB-Rose Gold"),
                        Variant(variant = "2GB-128GB-Rose Gold")
                    )
                ),
                Model(
                    name = "iPhone SE 1st Generation",
                    imageUrl = "https://www.canva.com/design/DAGYOs3MuJs/rX0cLV0nN1XEC9YGFw87xg/view",
                    variants = listOf(
                        Variant(variant = "2GB-16GB-Space Grey"),
                        Variant(variant = "2GB-32GB-Space Grey"),
                        Variant(variant = "2GB-64GB-Space Grey"),
                        Variant(variant = "2GB-128GB-Space Grey"),

                        Variant(variant = "2GB-16GB-Silver"),
                        Variant(variant = "2GB-32GB-Silver"),
                        Variant(variant = "2GB-64GB-Silver"),
                        Variant(variant = "2GB-128GB-Silver"),

                        Variant(variant = "2GB-16GB-Gold"),
                        Variant(variant = "2GB-32GB-Gold"),
                        Variant(variant = "2GB-64GB-Gold"),
                        Variant(variant = "2GB-128GB-Gold"),

                        Variant(variant = "2GB-16GB-Rose Gold"),
                        Variant(variant = "2GB-32GB-Rose Gold"),
                        Variant(variant = "2GB-64GB-Rose Gold"),
                        Variant(variant = "2GB-128GB-Rose Gold")
                    )
                ),
                Model(
                    name = "iPhone 7",
                    imageUrl = "https://www.canva.com/design/DAGYOrsU6w4/qakZeLhvZEiKJvEM2on1ow/view",
                    variants = listOf(
                        Variant(variant = "2GB-32GB-Jet Black"),
                        Variant(variant = "2GB-128GB-Jet Black"),
                        Variant(variant = "2GB-256GB-Jet Black"),

                        Variant(variant = "2GB-32GB-Black"),
                        Variant(variant = "2GB-128GB-Black"),
                        Variant(variant = "2GB-256GB-Black"),

                        Variant(variant = "2GB-32GB-Silver"),
                        Variant(variant = "2GB-128GB-Silver"),
                        Variant(variant = "2GB-256GB-Silver"),

                        Variant(variant = "2GB-32GB-Gold"),
                        Variant(variant = "2GB-128GB-Gold"),
                        Variant(variant = "2GB-256GB-Gold"),

                        Variant(variant = "2GB-32GB-Rose Gold"),
                        Variant(variant = "2GB-128GB-Rose Gold"),
                        Variant(variant = "2GB-256GB-Rose Gold"),

                        Variant(variant = "2GB-32GB-Red"),
                        Variant(variant = "2GB-128GB-Red"),
                        Variant(variant = "2GB-256GB-Red")
                    )
                ),
                Model(
                    name = "iPhone 7 Plus",
                    imageUrl = "https://www.canva.com/design/DAGYOpI2F2U/aC7kIB6s3G0xqbFgGPRUMA/view",
                    variants = listOf(
                        Variant(variant = "3GB-32GB-Jet Black"),
                        Variant(variant = "3GB-128GB-Jet Black"),
                        Variant(variant = "3GB-256GB-Jet Black"),

                        Variant(variant = "3GB-32GB-Black"),
                        Variant(variant = "3GB-128GB-Black"),
                        Variant(variant = "3GB-256GB-Black"),

                        Variant(variant = "3GB-32GB-Silver"),
                        Variant(variant = "3GB-128GB-Silver"),
                        Variant(variant = "3GB-256GB-Silver"),

                        Variant(variant = "3GB-32GB-Gold"),
                        Variant(variant = "3GB-128GB-Gold"),
                        Variant(variant = "3GB-256GB-Gold"),

                        Variant(variant = "3GB-32GB-Rose Gold"),
                        Variant(variant = "3GB-128GB-Rose Gold"),
                        Variant(variant = "3GB-256GB-Rose Gold"),

                        Variant(variant = "3GB-32GB-Red"),
                        Variant(variant = "3GB-128GB-Red"),
                        Variant(variant = "3GB-256GB-Red")
                    )
                ),
                Model(
                    name = "iPhone 8",
                    imageUrl = "https://www.canva.com/design/DAGYO6jjH7w/Sk-u68sKZUppj-1TKz38mg/view",
                    variants = listOf(
                        Variant(variant = "2GB-64GB-Silver"),
                        Variant(variant = "2GB-128GB-Silver"),
                        Variant(variant = "2GB-256GB-Silver"),

                        Variant(variant = "2GB-64GB-Space Gray"),
                        Variant(variant = "2GB-128GB-Space Gray"),
                        Variant(variant = "2GB-256GB-Space Gray"),

                        Variant(variant = "2GB-64GB-Gold"),
                        Variant(variant = "2GB-128GB-Gold"),
                        Variant(variant = "2GB-256GB-Gold"),

                        Variant(variant = "2GB-64GB-Red"),
                        Variant(variant = "2GB-128GB-Red"),
                        Variant(variant = "2GB-256GB-Red"),
                    )
                ),
                Model(
                    name = "iPhone 8 Plus",
                    imageUrl = "https://www.canva.com/design/DAGYO4x4kY8/ZLGeLeWEuwtGNFi-oBUscQ/view",
                    variants = listOf(
                        Variant(variant = "3GB-256GB-Silver"),
                        Variant(variant = "3GB-128GB-Silver"),
                        Variant(variant = "3GB-64GB-Silver"),

                        Variant(variant = "3GB-64GB-Space Gray"),
                        Variant(variant = "3GB-128GB-Space Gray"),
                        Variant(variant = "3GB-256GB-Space Gray"),

                        Variant(variant = "3GB-64GB-Gold"),
                        Variant(variant = "3GB-128GB-Gold"),
                        Variant(variant = "3GB-256GB-Gold"),

                        Variant(variant = "3GB-64GB-Red"),
                        Variant(variant = "3GB-128GB-Red"),
                        Variant(variant = "3GB-256GB-Red"),
                    )
                ),
                Model(
                    name = "iPhone X",
                    imageUrl = "https://www.canva.com/design/DAGYO0knLok/8vScaNwNZFobYKaKrmXEQA/view",
                    variants = listOf(
                        Variant(variant = "3GB-64GB-Silver"),
                        Variant(variant = "3GB-256GB-Silver"),

                        Variant(variant = "3GB-64GB-Space Grey"),
                        Variant(variant = "3GB-256GB-Space Grey")
                    )
                ),
                Model(
                    name = "iPhone XR",
                    imageUrl = "https://www.canva.com/design/DAGYO9MoqTQ/0je0kh-yJzi_eTau3kQlfQ/view",
                    variants = listOf(
                        Variant(variant = "3GB-64GB-Black"),
                        Variant(variant = "3GB-128GB-Black"),
                        Variant(variant = "3GB-256GB-Black"),

                        Variant(variant = "3GB-64GB-White"),
                        Variant(variant = "3GB-128GB-White"),
                        Variant(variant = "3GB-256GB-White"),

                        Variant(variant = "3GB-64GB-Blue"),
                        Variant(variant = "3GB-128GB-Blue"),
                        Variant(variant = "3GB-256GB-Blue"),

                        Variant(variant = "3GB-64GB-Yellow"),
                        Variant(variant = "3GB-128GB-Yellow"),
                        Variant(variant = "3GB-256GB-Yellow"),

                        Variant(variant = "3GB-64GB-Coral"),
                        Variant(variant = "3GB-128GB-Coral"),
                        Variant(variant = "3GB-256GB-Coral"),

                        Variant(variant = "3GB-64GB-Red"),
                        Variant(variant = "3GB-128GB-Red"),
                        Variant(variant = "3GB-256GB-Red")
                    )
                ),
                Model(
                    name = "iPhone XS",
                    imageUrl = "https://www.canva.com/design/DAGYO7UVRuA/Us6gfa39fn3W5Tw3HejjMA/view",
                    variants = listOf(
                        Variant(variant = "4GB-64GB-Silver"),
                        Variant(variant = "4GB-256GB-Silver"),
                        Variant(variant = "4GB-512GB-Silver"),

                        Variant(variant = "4GB-64GB- Gold"),
                        Variant(variant = "4GB-256GB-Gold"),
                        Variant(variant = "4GB-512GB-Gold"),

                        Variant(variant = "4GB-64GB-Space Grey"),
                        Variant(variant = "4GB-256GB-Space Grey"),
                        Variant(variant = "4GB-512GB-Space Grey"),
                    )
                ),
                Model(
                    name = "iPhone XS Max",
                    imageUrl = "https://www.canva.com/design/DAGYO5nYu5E/maKdoJN7C8sLUy2Z3rVRrg/view",
                    variants = listOf(
                        Variant(variant = "4GB-64GB-Silver"),
                        Variant(variant = "4GB-256GB-Silver"),
                        Variant(variant = "4GB-512GB-Silver"),

                        Variant(variant = "4GB-64GB- Gold"),
                        Variant(variant = "4GB-256GB-Gold"),
                        Variant(variant = "4GB-512GB-Gold"),

                        Variant(variant = "4GB-64GB-Space Grey"),
                        Variant(variant = "4GB-256GB-Space Grey"),
                        Variant(variant = "4GB-512GB-Space Grey"),
                    )
                ),
                Model(
                    name = "iPhone 11",
                    imageUrl = "https://www.canva.com/design/DAGYPNJW3sM/jVe4ZFg5T5GwXZqr8Q78EQ/view",
                    variants = listOf(
                        Variant(variant = "4GB-64GB-Black"),
                        Variant(variant = "4GB-128GB-Black"),
                        Variant(variant = "4GB-256GB-Black"),

                        Variant(variant = "4GB-64GB-White"),
                        Variant(variant = "4GB-128GB-White"),
                        Variant(variant = "4GB-256GB-White"),

                        Variant(variant = "4GB-64GB-Yellow"),
                        Variant(variant = "4GB-128GB-Yellow"),
                        Variant(variant = "4GB-256GB-Yellow"),

                        Variant(variant = "4GB-64GB-Green"),
                        Variant(variant = "4GB-128GB-Green"),
                        Variant(variant = "4GB-256GB-Green"),

                        Variant(variant = "4GB-64GB-Purple"),
                        Variant(variant = "4GB-128GB-Purple"),
                        Variant(variant = "4GB-256GB-Purple"),

                        Variant(variant = "4GB-64GB-Red"),
                        Variant(variant = "4GB-128GB-Red"),
                        Variant(variant = "4GB-256GB-Red")
                    )
                ),
                Model(
                    name = "iPhone 11 Pro",
                    imageUrl = "https://www.canva.com/design/DAGYPEDY8b4/MEne_ReG4yUnScJaaTfGog/view",
                    variants = listOf(
                        Variant(variant = "4GB-64GB-Silver"),
                        Variant(variant = "4GB-256GB-Silver"),
                        Variant(variant = "4GB-512GB-Silver"),

                        Variant(variant = "4GB-64GB-Space Grey"),
                        Variant(variant = "4GB-256GB-Space Grey"),
                        Variant(variant = "4GB-512GB-Space Grey"),

                        Variant(variant = "4GB-64GB-Gold"),
                        Variant(variant = "4GB-256GB-Gold"),
                        Variant(variant = "4GB-512GB-Gold"),

                        Variant(variant = "4GB-64GB-Midnight Green"),
                        Variant(variant = "4GB-256GB-Midnight Green"),
                        Variant(variant = "4GB-512GB-Midnight Green"),
                    )
                ),
                Model(
                    name = "iPhone 11 Pro Max",
                    imageUrl = "https://www.canva.com/design/DAGYPOCAd18/I-zikdLoG_77gIOeCfvFpg/view",
                    variants = listOf(
                        Variant(variant = "4GB-64GB-Silver"),
                        Variant(variant = "4GB-256GB-Silver"),
                        Variant(variant = "4GB-512GB-Silver"),

                        Variant(variant = "4GB-64GB-Space Grey"),
                        Variant(variant = "4GB-256GB-Space Grey"),
                        Variant(variant = "4GB-512GB-Space Grey"),

                        Variant(variant = "4GB-64GB-Gold"),
                        Variant(variant = "4GB-256GB-Gold"),
                        Variant(variant = "4GB-512GB-Gold"),

                        Variant(variant = "4GB-64GB-Midnight Green"),
                        Variant(variant = "4GB-256GB-Midnight Green"),
                        Variant(variant = "4GB-512GB-Midnight Green"),
                    )
                ),
                Model(
                    name = "iPhone SE 2nd Generation",
                    imageUrl = "https://www.canva.com/design/DAGYPLGVXNE/xnYR2HQub-4nbZSeOnAPQw/view",
                    variants = listOf(
                        Variant(variant = "3GB-64GB-Black"),
                        Variant(variant = "3GB-128GB-Black"),
                        Variant(variant = "3GB-256GB-Black"),

                        Variant(variant = "3GB-64GB-White"),
                        Variant(variant = "3GB-128GB-White"),
                        Variant(variant = "3GB-256GB-White"),

                        Variant(variant = "3GB-64GB-Red"),
                        Variant(variant = "3GB-128GB-Red"),
                        Variant(variant = "3GB-256GB-Red"),
                    )
                ),
                Model(
                    name = "iPhone 12",
                    imageUrl = "https://www.canva.com/design/DAGYPIuYxAY/mMKvKcrkZzaWe12bomYScg/view",
                    variants = listOf(
                        Variant(variant = "4GB-64GB-Black"),
                        Variant(variant = "4GB-128GB-Black"),
                        Variant(variant = "4GB-256GB-Black"),

                        Variant(variant = "4GB-64GB-White"),
                        Variant(variant = "4GB-128GB-White"),
                        Variant(variant = "4GB-256GB-White"),

                        Variant(variant = "4GB-64GB-Red"),
                        Variant(variant = "4GB-128GB-Red"),
                        Variant(variant = "4GB-256GB-Red"),

                        Variant(variant = "4GB-64GB-Green"),
                        Variant(variant = "4GB-128GB-Green"),
                        Variant(variant = "4GB-256GB-Green"),

                        Variant(variant = "4GB-64GB-Blue"),
                        Variant(variant = "4GB-128GB-Blue"),
                        Variant(variant = "4GB-256GB-Blue"),

                        Variant(variant = "4GB-64GB-Purple"),
                        Variant(variant = "4GB-128GB-Purple"),
                        Variant(variant = "4GB-256GB-Purple"),

                        )
                ),
                Model(
                    name = "iPhone 12 Mini",
                    imageUrl = "https://www.canva.com/design/DAGYPBBxK4E/3_JulD0r9u9eyuG_VfcwwQ/view",
                    variants = listOf(
                        Variant(variant = "4GB-64GB-Black"),
                        Variant(variant = "4GB-128GB-Black"),
                        Variant(variant = "4GB-256GB-Black"),

                        Variant(variant = "4GB-64GB-White"),
                        Variant(variant = "4GB-128GB-White"),
                        Variant(variant = "4GB-256GB-White"),

                        Variant(variant = "4GB-64GB-Red"),
                        Variant(variant = "4GB-128GB-Red"),
                        Variant(variant = "4GB-256GB-Red"),

                        Variant(variant = "4GB-64GB-Green"),
                        Variant(variant = "4GB-128GB-Green"),
                        Variant(variant = "4GB-256GB-Green"),

                        Variant(variant = "4GB-64GB-Blue"),
                        Variant(variant = "4GB-128GB-Blue"),
                        Variant(variant = "4GB-256GB-Blue"),

                        Variant(variant = "4GB-64GB-Purple"),
                        Variant(variant = "4GB-128GB-Purple"),
                        Variant(variant = "4GB-256GB-Purple"),
                    )
                ),
                Model(
                    name = "iPhone 12 Pro",
                    imageUrl = "https://www.canva.com/design/DAGYPA7WRnE/BkRD3hzbnVtuYx7nt4ffaA/view",
                    variants = listOf(
                        Variant(variant = "6GB-128GB-Silver"),
                        Variant(variant = "6GB-256GB-Silver"),
                        Variant(variant = "6GB-512GB-Silver"),

                        Variant(variant = "6GB-128GB-Graphite"),
                        Variant(variant = "6GB-256GB-Graphite"),
                        Variant(variant = "6GB-512GB-Graphite"),

                        Variant(variant = "6GB-128GB-Gold"),
                        Variant(variant = "6GB-256GB-Gold"),
                        Variant(variant = "6GB-512GB-Gold"),

                        Variant(variant = "6GB-128GB-Pacific Blue"),
                        Variant(variant = "6GB-256GB-Pacific Blue"),
                        Variant(variant = "6GB-512GB-Pacific Blue")
                    )
                ),
                Model(
                    name = "iPhone 12 Pro Max",
                    imageUrl = "https://www.canva.com/design/DAGYPGjyVAA/ZulydPal4IaINfg2R58Jbg/view",
                    variants = listOf(
                        Variant(variant = "6GB-128GB-Silver"),
                        Variant(variant = "6GB-256GB-Silver"),
                        Variant(variant = "6GB-512GB-Silver"),

                        Variant(variant = "6GB-128GB-Graphite"),
                        Variant(variant = "6GB-256GB-Graphite"),
                        Variant(variant = "6GB-512GB-Graphite"),

                        Variant(variant = "6GB-128GB-Gold"),
                        Variant(variant = "6GB-256GB-Gold"),
                        Variant(variant = "6GB-512GB-Gold"),

                        Variant(variant = "6GB-128GB-Pacific Blue"),
                        Variant(variant = "6GB-256GB-Pacific Blue"),
                        Variant(variant = "6GB-512GB-Pacific Blue")
                    )
                ),
                Model(
                    name = "iPhone 13 Mini",
                    imageUrl = "https://www.canva.com/design/DAGYPGlaJWc/FtZPgZs1ZPDyKFS1C30lgA/view",
                    variants = listOf(
                        Variant(variant = "4GB-128GB-Starlight"),
                        Variant(variant = "4GB-256GB-Starlight"),
                        Variant(variant = "4GB-512GB-Starlight"),

                        Variant(variant = "4GB-128GB-Midnight"),
                        Variant(variant = "4GB-256GB-Midnight"),
                        Variant(variant = "4GB-512GB-Midnight"),
                        Variant(variant = "4GB-128GB-Blue"),
                        Variant(variant = "4GB-256GB-Blue"),
                        Variant(variant = "4GB-512GB-Blue"),

                        Variant(variant = "4GB-128GB-Pink"),
                        Variant(variant = "4GB-256GB-Pink"),
                        Variant(variant = "4GB-512GB-Pink"),

                        Variant(variant = "4GB-128GB-Red"),
                        Variant(variant = "4GB-256GB-Red"),
                        Variant(variant = "4GB-512GB-Red"),

                        Variant(variant = "4GB-128GB-Green"),
                        Variant(variant = "4GB-256GB-Green"),
                        Variant(variant = "4GB-512GB-Green")
                    )
                ),
                Model(
                    name = "iPhone 13",
                    imageUrl = "https://www.canva.com/design/DAGYPGPm8w8/2kLDbzn8hO0o23kCt5-RTQ/view",
                    variants = listOf(
                        Variant(variant = "4GB-128GB-Starlight"),
                        Variant(variant = "4GB-256GB-Starlight"),
                        Variant(variant = "4GB-512GB-Starlight"),

                        Variant(variant = "4GB-128GB-Midnight"),
                        Variant(variant = "4GB-256GB-Midnight"),
                        Variant(variant = "4GB-512GB-Midnight"),

                        Variant(variant = "4GB-128GB-Blue"),
                        Variant(variant = "4GB-256GB-Blue"),
                        Variant(variant = "4GB-512GB-Blue"),

                        Variant(variant = "4GB-128GB-Pink"),
                        Variant(variant = "4GB-256GB-Pink"),
                        Variant(variant = "4GB-512GB-Pink"),

                        Variant(variant = "4GB-128GB-Red"),
                        Variant(variant = "4GB-256GB-Red"),
                        Variant(variant = "4GB-512GB-Red"),

                        Variant(variant = "4GB-128GB-Green"),
                        Variant(variant = "4GB-256GB-Green"),
                        Variant(variant = "4GB-512GB-Green")
                    )
                ),
                Model(
                    name = "iPhone 13 Pro",
                    imageUrl = "https://www.canva.com/design/DAGYPEpwPpk/5nEP2LvHFL1QV-Di4I6b-A/view",
                    variants = listOf(
                        Variant(variant = "6GB-128GB-Graphite"),
                        Variant(variant = "6GB-256GB-Graphite"),
                        Variant(variant = "6GB-512GB-Graphite"),
                        Variant(variant = "6GB-1TB-Graphite"),

                        Variant(variant = "6GB-128GB-Gold"),
                        Variant(variant = "6GB-256GB-Gold"),
                        Variant(variant = "6GB-512GB-Gold"),
                        Variant(variant = "6GB-1TB-Gold"),

                        Variant(variant = "6GB-128GB-Silver"),
                        Variant(variant = "6GB-256GB-Silver"),
                        Variant(variant = "6GB-512GB-Silver"),
                        Variant(variant = "6GB-1TB-Silver"),

                        Variant(variant = "6GB-128GB-Sierra Blue"),
                        Variant(variant = "6GB-256GB-Sierra Blue"),
                        Variant(variant = "6GB-512GB-Sierra Blue"),
                        Variant(variant = "6GB-1TB-Sierra Blue"),


                        Variant(variant = "6GB-128GB-Alpine Green"),
                        Variant(variant = "6GB-256GB-Alpine Green"),
                        Variant(variant = "6GB-512GB-Alpine Green"),
                        Variant(variant = "6GB-1TB-Alpine Green"),
                    )
                ),
                Model(
                    name = "iPhone 13 Pro Max",
                    imageUrl = "https://www.canva.com/design/DAGYPJwRIHk/mrTidK45oS3k1DT9DjDKMA/view",
                    variants = listOf(
                        Variant(variant = "6GB-128GB-Graphite"),
                        Variant(variant = "6GB-256GB-Graphite"),
                        Variant(variant = "6GB-512GB-Graphite"),
                        Variant(variant = "6GB-1TB-Graphite"),

                        Variant(variant = "6GB-128GB-Gold"),
                        Variant(variant = "6GB-256GB-Gold"),
                        Variant(variant = "6GB-512GB-Gold"),
                        Variant(variant = "6GB-1TB-Gold"),

                        Variant(variant = "6GB-128GB-Silver"),
                        Variant(variant = "6GB-256GB-Silver"),
                        Variant(variant = "6GB-512GB-Silver"),
                        Variant(variant = "6GB-1TB-Silver"),

                        Variant(variant = "6GB-128GB-Sierra Blue"),
                        Variant(variant = "6GB-256GB-Sierra Blue"),
                        Variant(variant = "6GB-512GB-Sierra Blue"),
                        Variant(variant = "6GB-1TB-Sierra Blue"),


                        Variant(variant = "6GB-128GB-Alpine Green"),
                        Variant(variant = "6GB-256GB-Alpine Green"),
                        Variant(variant = "6GB-512GB-Alpine Green"),
                        Variant(variant = "6GB-1TB-Alpine Green"),
                    )
                ),
                Model(
                    name = "iPhone 14",
                    imageUrl = "https://www.canva.com/design/DAGYPMRoCQI/hAfkVfb3Pk2KrhAVZzMPdw/view",
                    variants = listOf(
                        Variant(variant = "6GB-128GB-Midnight"),
                        Variant(variant = "6GB-256GB-Midnight"),
                        Variant(variant = "6GB-512GB-Midnight"),
                        Variant(variant = "6GB-1TB-Midnight"),

                        Variant(variant = "6GB-128GB-Purple"),
                        Variant(variant = "6GB-256GB-Purple"),
                        Variant(variant = "6GB-512GB-Purple"),
                        Variant(variant = "6GB-1TB-Purple"),


                        Variant(variant = "6GB-128GB-Starlight"),
                        Variant(variant = "6GB-256GB-Starlight"),
                        Variant(variant = "6GB-512GB-Starlight"),
                        Variant(variant = "6GB-1TB-Starlight"),

                        Variant(variant = "6GB-128GB-Blue"),
                        Variant(variant = "6GB-256GB-Blue"),
                        Variant(variant = "6GB-512GB-Blue"),
                        Variant(variant = "6GB-1TB-Blue"),

                        Variant(variant = "6GB-128GB-Red"),
                        Variant(variant = "6GB-256GB-Red"),
                        Variant(variant = "6GB-512GB-Red"),
                        Variant(variant = "6GB-1TB-Red"),

                        Variant(variant = "6GB-128GB-Yellow"),
                        Variant(variant = "6GB-256GB-Yellow"),
                        Variant(variant = "6GB-512GB-Yellow"),
                        Variant(variant = "6GB-1TB-Yellow")
                    )
                ),
                Model(
                    name = "iPhone 14 Pro",
                    imageUrl = "https://www.canva.com/design/DAGYPJxRyhQ/d0M_yA9Q6PqJaD5OJA6MFg/view",
                    variants = listOf(
                        Variant(variant = "6GB-128GB-Silver"),
                        Variant(variant = "6GB-256GB-Silver"),
                        Variant(variant = "6GB-512GB-Silver"),
                        Variant(variant = "6GB-1TB-Silver"),

                        Variant(variant = "6GB-128GB-Space Black"),
                        Variant(variant = "6GB-256GB-Space Black"),
                        Variant(variant = "6GB-512GB-Space Black"),
                        Variant(variant = "6GB-1TB-Space Black"),


                        Variant(variant = "6GB-128GB-Gold"),
                        Variant(variant = "6GB-256GB-Gold"),
                        Variant(variant = "6GB-512GB-Gold"),
                        Variant(variant = "6GB-1TB-Gold"),

                        Variant(variant = "6GB-128GB-Deep Purple"),
                        Variant(variant = "6GB-256GB-Deep Purple"),
                        Variant(variant = "6GB-512GB-Deep Purple"),
                        Variant(variant = "6GB-1TB-Deep Purple")
                    )
                ),
                Model(
                    name = "iPhone 14 Pro Max",
                    imageUrl = "https://www.canva.com/design/DAGYPZy0WYo/_pR589u5q85ul2PMEmhKCA/view",
                    variants = listOf(
                        Variant(variant = "6GB-128GB-Silver"),
                        Variant(variant = "6GB-256GB-Silver"),
                        Variant(variant = "6GB-512GB-Silver"),
                        Variant(variant = "6GB-1TB-Silver"),

                        Variant(variant = "6GB-128GB-Space Black"),
                        Variant(variant = "6GB-256GB-Space Black"),
                        Variant(variant = "6GB-512GB-Space Black"),
                        Variant(variant = "6GB-1TB-Space Black"),


                        Variant(variant = "6GB-128GB-Gold"),
                        Variant(variant = "6GB-256GB-Gold"),
                        Variant(variant = "6GB-512GB-Gold"),
                        Variant(variant = "6GB-1TB-Gold"),

                        Variant(variant = "6GB-128GB-Deep Purple"),
                        Variant(variant = "6GB-256GB-Deep Purple"),
                        Variant(variant = "6GB-512GB-Deep Purple"),
                        Variant(variant = "6GB-1TB-Deep Purple")
                    )
                ),
                Model(
                    name = "Apple iPhone 14 Plus",
                    imageUrl = "https://www.canva.com/design/DAGYPZy0WYo/_pR589u5q85ul2PMEmhKCA/view",
                    variants = listOf(
                        Variant(variant = "6GB-128GB-Midnight"),
                        Variant(variant = "6GB-256GB-Midnight"),
                        Variant(variant = "6GB-512GB-Midnight"),

                        Variant(variant = "6GB-128GB-Purple"),
                        Variant(variant = "6GB-256GB-Purple"),
                        Variant(variant = "6GB-512GB-Purple"),

                        Variant(variant = "6GB-128GB-Starlight"),
                        Variant(variant = "6GB-256GB-Starlight"),
                        Variant(variant = "6GB-512GB-Starlight"),

                        Variant(variant = "6GB-128GB-Blue"),
                        Variant(variant = "6GB-256GB-Blue"),
                        Variant(variant = "6GB-512GB-Blue"),

                        Variant(variant = "6GB-128GB-Red"),
                        Variant(variant = "6GB-256GB-Red"),
                        Variant(variant = "6GB-512GB-Red"),

                        Variant(variant = "6GB-128GB-Yellow"),
                        Variant(variant = "6GB-256GB-Yellow"),
                        Variant(variant = "6GB-512GB-Yellow")
                    )
                ),
                Model(
                    name = "Apple iPhone 15",
                    imageUrl = "https://www.canva.com/design/DAGYPZy0WYo/_pR589u5q85ul2PMEmhKCA/view",
                    variants = listOf(
                        Variant(variant = "6GB-128GB-Black"),
                        Variant(variant = "6GB-256GB-Black"),
                        Variant(variant = "6GB-512GB-Black"),

                        Variant(variant = "6GB-128GB-Blue"),
                        Variant(variant = "6GB-256GB-Blue"),
                        Variant(variant = "6GB-512GB-Blue"),

                        Variant(variant = "6GB-128GB-Green"),
                        Variant(variant = "6GB-256GB-Green"),
                        Variant(variant = "6GB-512GB-Green"),

                        Variant(variant = "6GB-128GB-Yellow"),
                        Variant(variant = "6GB-256GB-Yellow"),
                        Variant(variant = "6GB-512GB-Yellow"),

                        Variant(variant = "6GB-128GB-Pink"),
                        Variant(variant = "6GB-256GB-Pink"),
                        Variant(variant = "6GB-512GB-Pink")
                    )
                ),
                Model(
                    name = "Apple iPhone 15 Pro",
                    imageUrl = "https://www.canva.com/design/DAGYPZy0WYo/_pR589u5q85ul2PMEmhKCA/view",
                    variants = listOf(
                        Variant(variant = "8GB-128GB-Black Titanium"),
                        Variant(variant = "8GB-256GB-Black Titanium"),
                        Variant(variant = "8GB-512GB-Black Titanium"),
                        Variant(variant = "8GB-1TB-Black Titanium"),

                        Variant(variant = "8GB-128GB-White Titanium"),
                        Variant(variant = "8GB-256GB-White Titanium"),
                        Variant(variant = "8GB-512GB-White Titanium"),
                        Variant(variant = "8GB-1TB-White Titanium"),

                        Variant(variant = "8GB-128GB-Blue Titanium"),
                        Variant(variant = "8GB-256GB-Blue Titanium"),
                        Variant(variant = "8GB-512GB-Blue Titanium"),
                        Variant(variant = "8GB-1TB-Blue Titanium"),

                        Variant(variant = "8GB-128GB-Natural Titanium"),
                        Variant(variant = "8GB-256GB-Natural Titanium"),
                        Variant(variant = "8GB-512GB-Natural Titanium"),
                        Variant(variant = "8GB-1TB-Natural Titanium")
                    )
                ),
                Model(
                    name = "Apple iPhone 15 Pro Max",
                    imageUrl = "https://www.canva.com/design/DAGYPZy0WYo/_pR589u5q85ul2PMEmhKCA/view",
                    variants = listOf(
                        Variant(variant = "8GB-256GB-Black Titanium"),
                        Variant(variant = "8GB-512GB-Black Titanium"),
                        Variant(variant = "8GB-1TB-Black Titanium"),

                        Variant(variant = "8GB-256GB-White Titanium"),
                        Variant(variant = "8GB-512GB-White Titanium"),
                        Variant(variant = "8GB-1TB-White Titanium"),

                        Variant(variant = "8GB-256GB-Blue Titanium"),
                        Variant(variant = "8GB-512GB-Blue Titanium"),
                        Variant(variant = "8GB-1TB-Blue Titanium"),

                        Variant(variant = "8GB-256GB-Natural Titanium"),
                        Variant(variant = "8GB-512GB-Natural Titanium"),
                        Variant(variant = "8GB-1TB-Natural Titanium")
                    )
                ),
                Model(
                    name = "Apple iPhone 15 Plus",
                    imageUrl = "https://www.canva.com/design/DAGYPZy0WYo/_pR589u5q85ul2PMEmhKCA/view",
                    variants = listOf(
                        Variant(variant = "6GB-128GB-Black"),
                        Variant(variant = "6GB-256GB-Black"),
                        Variant(variant = "6GB-512GB-Black"),

                        Variant(variant = "6GB-128GB-Blue"),
                        Variant(variant = "6GB-256GB-Blue"),
                        Variant(variant = "6GB-512GB-Blue"),

                        Variant(variant = "6GB-128GB-Green"),
                        Variant(variant = "6GB-256GB-Green"),
                        Variant(variant = "6GB-512GB-Green"),

                        Variant(variant = "6GB-128GB-Yellow"),
                        Variant(variant = "6GB-256GB-Yellow"),
                        Variant(variant = "6GB-512GB-Yellow"),

                        Variant(variant = "6GB-128GB-Pink"),
                        Variant(variant = "6GB-256GB-Pink"),
                        Variant(variant = "6GB-512GB-Pink")
                    )
                ),
                Model(
                    name = "Apple iPhone 16",
                    imageUrl = "https://www.canva.com/design/DAGYPZy0WYo/_pR589u5q85ul2PMEmhKCA/view",
                    variants = listOf(
                        Variant(variant = "8GB-128GB-Black"),
                        Variant(variant = "8GB-256GB-Black"),
                        Variant(variant = "8GB-512GB-Black"),

                        Variant(variant = "8GB-128GB-White"),
                        Variant(variant = "8GB-256GB-White"),
                        Variant(variant = "8GB-512GB-White"),

                        Variant(variant = "8GB-128GB-Pink"),
                        Variant(variant = "8GB-256GB-Pink"),
                        Variant(variant = "8GB-512GB-Pink"),

                        Variant(variant = "8GB-128GB-Teal"),
                        Variant(variant = "8GB-256GB-Teal"),
                        Variant(variant = "8GB-512GB-Teal"),

                        Variant(variant = "8GB-128GB-Ultramarine"),
                        Variant(variant = "8GB-256GB-Ultramarine"),
                        Variant(variant = "8GB-512GB-Ultramarine")
                    )
                ),
                Model(
                    name = "Apple iPhone 16 Plus",
                    imageUrl = "https://www.canva.com/design/DAGYPZy0WYo/_pR589u5q85ul2PMEmhKCA/view",
                    variants = listOf(
                        Variant(variant = "8GB-128GB-Black"),
                        Variant(variant = "8GB-256GB-Black"),
                        Variant(variant = "8GB-512GB-Black"),

                        Variant(variant = "8GB-128GB-White"),
                        Variant(variant = "8GB-256GB-White"),
                        Variant(variant = "8GB-512GB-White"),

                        Variant(variant = "8GB-128GB-Pink"),
                        Variant(variant = "8GB-256GB-Pink"),
                        Variant(variant = "8GB-512GB-Pink"),

                        Variant(variant = "8GB-128GB-Teal"),
                        Variant(variant = "8GB-256GB-Teal"),
                        Variant(variant = "8GB-512GB-Teal"),

                        Variant(variant = "8GB-128GB-Ultramarine"),
                        Variant(variant = "8GB-256GB-Ultramarine"),
                        Variant(variant = "8GB-512GB-Ultramarine")
                    )
                ),
                Model(
                    name = "Apple iPhone 16 Pro",
                    imageUrl = "https://www.canva.com/design/DAGYPZy0WYo/_pR589u5q85ul2PMEmhKCA/view",
                    variants = listOf(
                        Variant(variant = "8GB-128GB-Black Titanium"),
                        Variant(variant = "8GB-256GB-Black Titanium"),
                        Variant(variant = "8GB-512GB-Black Titanium"),
                        Variant(variant = "8GB-1TB-Black Titanium"),

                        Variant(variant = "8GB-128GB-White Titanium"),
                        Variant(variant = "8GB-256GB-White Titanium"),
                        Variant(variant = "8GB-512GB-White Titanium"),
                        Variant(variant = "8GB-1TB-White Titanium"),

                        Variant(variant = "8GB-128GB-Natural Titanium"),
                        Variant(variant = "8GB-256GB-Natural Titanium"),
                        Variant(variant = "8GB-512GB-Natural Titanium"),
                        Variant(variant = "8GB-1TB-Natural Titanium"),

                        Variant(variant = "8GB-128GB-Desert Titanium"),
                        Variant(variant = "8GB-256GB-Desert Titanium"),
                        Variant(variant = "8GB-512GB-Desert Titanium"),
                        Variant(variant = "8GB-1TB-Desert Titanium")
                    )
                ),
                Model(
                    name = "Apple iPhone 16 Pro Max",
                    imageUrl = "https://www.canva.com/design/DAGYPZy0WYo/_pR589u5q85ul2PMEmhKCA/view",
                    variants = listOf(
                        Variant(variant = "8GB-256GB-Black Titanium"),
                        Variant(variant = "8GB-512GB-Black Titanium"),
                        Variant(variant = "8GB-1TB-Black Titanium"),

                        Variant(variant = "8GB-256GB-White Titanium"),
                        Variant(variant = "8GB-512GB-White Titanium"),
                        Variant(variant = "8GB-1TB-White Titanium"),

                        Variant(variant = "8GB-256GB-Blue Titanium"),
                        Variant(variant = "8GB-512GB-Blue Titanium"),
                        Variant(variant = "8GB-1TB-Blue Titanium"),

                        Variant(variant = "8GB-256GB-Natural Titanium"),
                        Variant(variant = "8GB-512GB-Natural Titanium"),
                        Variant(variant = "8GB-1TB-Natural Titanium"),

                        Variant(variant = "8GB-256GB-Desert Titanium"),
                        Variant(variant = "8GB-512GB-Desert Titanium"),
                        Variant(variant = "8GB-1TB-Desert Titanium")
                    )
                )
            )

        )
    )

}