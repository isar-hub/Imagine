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
            name = "Apple",
            models = listOf(
                Model(
                    name = "iPhone 6",
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/csh-qp4ba4sq-aeny.png?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/csh-yicxxl1r-d1ta.png?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/csh-oh9xlwt8-yunr.png?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/csh-73kjnouo-hzvn.png?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/csh-b3sceh1m-qzme.png?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/csh-jwihkccb-gqpj.png?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/csh-ujdaoxqa-5pv1.png?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/csh-wwiz6qsn-dquh.png?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/csh-bw5nptgc-a6e3.png?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/csh-rbtsgpq4-fzgq.png?p=default&s=lg",
                    variants = listOf(
                        Variant(variant = "3GB-64GB-Silver"),
                        Variant(variant = "3GB-256GB-Silver"),

                        Variant(variant = "3GB-64GB-Space Grey"),
                        Variant(variant = "3GB-256GB-Space Grey")
                    )
                ),
                Model(
                    name = "iPhone XR",
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/csh-c3k2c29y-gvfh.png?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/csh-y1wozpeh-wvgg.png?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/csh-c6ih9q0d-gyrf.png?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/csh-d1aqg8xw-hm4n.png?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/csh-gw04hbhk-uxfr.png?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/csh-28w4wypo-vwqi.png?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/csh-29z2mb50-2bwu.png?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/csh-g0iq6da5-w5sp.png?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/csh-g0iq6da5-w5sp.png?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/csh-tzblmj5y-kbs1.png?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/csh-5r41ayfq-mh4r.png?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/e6d78dcb-e5c9.jpg?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/csh-0mttz8sn-2c8e.png?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/csh-3kglv4j8-f60m.png?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/csh-4i0t8fto-04ar.png?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/csh-3c4h9xyu-c8pe.png?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/csh-75qdy9md-gg3d.png?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/csh-xw1bqtaw-5cl3.png?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/5fc412f4-2fb5.jpg?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/e2c7dff8-23a0.jpg?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/f43b6269-c954.jpg?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/e14bb4d9-988f.jpg?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/a6db4974-1a86.jpg?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/db36cde6-e6e5.jpg?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/5c48706b-b04d.jpg?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/c6842e95-7635.jpg?p=default&s=lg",
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
                    imageUrl = "https://s3no.cashify.in/cashify/product/img/xhdpi/d197ee88-ccff.jpg?p=default&s=lg",
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