package com.isar.imagine.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.icu.text.MessageFormat
import android.os.Environment
import androidx.core.content.res.ResourcesCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.isar.imagine.R
import com.itextpdf.io.image.ImageData
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.Style
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.Locale


object Invoice {

    fun Context.createPdf(
        userInformation: UserInformation,
        productList: List<ProductInformation>,
        quantity: Int
    ): String {


        val directory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "MyApp"
        )
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory, "bill.pdf")
        val outPutStream = FileOutputStream(file)


        val writer = PdfWriter(file)
        val pdfDocument = PdfDocument(writer)
        val document = Document(pdfDocument)


        val table0 = Table(floatArrayOf(560f))
        table0.addCell(
            Cell().add(
                Paragraph("Tax Invoice").setTextAlignment(TextAlignment.CENTER).setFontSize(10f)
            ).setBorder(Border.NO_BORDER)
        )
        val columnWidth = floatArrayOf(140f, 140f, 140f, 140f)
        val table1 = Table(columnWidth)

        val fontSize = Style().apply {
            setFontSize(8f)

        }

        val innerTable = Table(floatArrayOf(1f, 6f))
        innerTable.setWidth(UnitValue.createPercentValue(100f))
        innerTable.addCell(Cell().add(drawableToImage(R.drawable.redcat_logo).setBorder(Border.NO_BORDER)))
        innerTable.addCell(
            Cell().add(
                Paragraph(resources.getString(R.string.company_info)).addStyle(
                    fontSize
                ).setBorder(Border.NO_BORDER)
            )
        )


        //table-----  01 (header)
        table1.addCell(Cell(5, 2).add(innerTable))
        table1.addCell(
            Cell().add(
                Paragraph("Invoice No. \n ${userInformation.transactionNumber}").setFontSize(
                    9f
                )
            )
        )
        table1.addCell(Cell().add(Paragraph("Dated \n ${userInformation.date}").setFontSize(9f)))

        //table-----  02 (header)
        table1.addCell(Cell().add(Paragraph("Delivery Note \n ").setFontSize(9f)))
        table1.addCell(Cell().add(Paragraph("Mode/Terms of Payment \n ").setFontSize(9f)))

        //table-----  03 (header)
        table1.addCell(Cell().add(Paragraph("Reference No. & Date.\n ").setFontSize(9f)))
        table1.addCell(Cell().add(Paragraph("Other References \n ").setFontSize(9f)))

        //table-----  04 (header)
        table1.addCell(Cell().add(Paragraph("Buyer’s Order No.\n ").setFontSize(9f)))
        table1.addCell(Cell().add(Paragraph("Dated\n ").setFontSize(9f)))

        //table-----  05 (header)
        table1.addCell(Cell().add(Paragraph("Dispatch Doc No\n ").setFontSize(9f)))
        table1.addCell(Cell().add(Paragraph("Delivery Note Date\n ").setFontSize(9f)))

        //table-----  06 (header)
        table1.addCell(
            Cell(2, 2).add(
                Paragraph(
                    "Buyer (Bill to)\n" + "${userInformation.name}\n"+"${userInformation.address}\n" + "State Name : ${userInformation.stateName}, Code : ${userInformation.code}\n" + "Place of Supply :${userInformation.placeOfSupply}\n"
                ).setFontSize(10f)
            )
        )
        table1.addCell(Cell().add(Paragraph("Dispatched through \n isar").setFontSize(9f)))
        table1.addCell(Cell().add(Paragraph("Destination\n isar").setFontSize(9f)))


        //table-----  07 (header)
        table1.addCell(Cell(3, 2).add(Paragraph("Terms of Delivery\n  ").setFontSize(9f)))


        // product table
        val table2 = Table(floatArrayOf(10f, 120f, 140f, 50f, 40f, 50f, 50f, 40f, 60f))
        table2.setFontSize(9f)
        val blackColor = DeviceRgb(0, 0, 0)
        val topBorder = SolidBorder(blackColor, 1f)
        table2.addCell(Cell().add(Paragraph("UPC")))
        table2.addCell(Cell().add(Paragraph("SI No.")))
        table2.addCell(Cell().add(Paragraph("Description of Goods")))
        table2.addCell(Cell().add(Paragraph("HSN/SAC")))
        table2.addCell(Cell().add(Paragraph("GST Rate")))
        table2.addCell(Cell().add(Paragraph("Quantity")))
        table2.addCell(Cell().add(Paragraph("Rate")))
        table2.addCell(Cell().add(Paragraph("Disc. %")))
        table2.addCell(Cell().add(Paragraph("Amount")))

        val totalQuantity = productList.sumOf { it.quantity }

        val totalPrice = productList.sumOf {( it.total * it.quantity * (1 - it.discount / 100.0) ) + (calculatePercentage(it.total*it.quantity,it.gst_rate)) }

        val totalWithoutGst = productList.sumOf { it.total * it.quantity }
        for (i in 1..quantity) {
            val productInformation = productList[i - 1]
            table2.addCell(Cell().add(Paragraph("$i")))
            table2.addCell(Cell().add(barCode(productInformation.serialNumber)))
            table2.addCell(Cell().add(Paragraph(productInformation.description)))
            table2.addCell(Cell().add(Paragraph("85238020")))
            table2.addCell(Cell().add(Paragraph("${productInformation.gst_rate} %")))
            table2.addCell(Cell().add(Paragraph("${productInformation.quantity} PCS")))
            table2.addCell(Cell().add(Paragraph("${productInformation.rate}")))
            table2.addCell(Cell().add(Paragraph("${productInformation.discount} %")))
            table2.addCell(Cell().add(Paragraph("${productInformation.total*productInformation.quantity}").setBold()))

            table2.addCell(Cell().add(Paragraph()))
            table2.addCell(Cell().add(Paragraph()))
            table2.addCell(Cell().add(Paragraph("IGST ${productInformation.gst_rate} %").setBold()))
            table2.addCell(Cell().add(Paragraph()))
            table2.addCell(Cell().add(Paragraph()))
            table2.addCell(Cell().add(Paragraph()))
            table2.addCell(Cell().add(Paragraph()))
            table2.addCell(Cell().add(Paragraph()))
            table2.addCell(Cell().add(Paragraph("${
                calculatePercentage(
                    productInformation.total*productInformation.quantity,
                    productInformation.gst_rate
                )
            }").setBold()))
        }


        //total item
        table2.addCell(Cell().add(Paragraph()))
        table2.addCell(Cell().add(Paragraph()))
        table2.addCell(Cell().add(Paragraph("Total").setBold()))
        table2.addCell(Cell().add(Paragraph()))
        table2.addCell(Cell().add(Paragraph()))
        table2.addCell(Cell().add(Paragraph("$totalQuantity PCS").setBold()))
        table2.addCell(Cell().add(Paragraph()))
        table2.addCell(Cell().add(Paragraph()))
        table2.addCell(Cell().add(Paragraph("₹ $totalPrice").setBold()))


        val priceInWords = totalPrice.toWords("en","US")
        table2.addCell(
            Cell(
                1,
                9
            ).add(Paragraph("Amount Chargeable (in words) : $priceInWords").setFontSize(10f))
        )


        // gst table
        val table3 = Table(floatArrayOf(300f, 70f, 50f, 70f, 70f))
        table3.setFontSize(10f)

        table3.addCell(Cell(2, 1).add(Paragraph("HSN/SAC ")))
        table3.addCell(Cell(2, 1).add(Paragraph("Taxable Value")))
        table3.addCell(Cell(1, 2).add(Paragraph("IGST")))
        table3.addCell(Cell(2, 1).add(Paragraph("Total Tax Amount")))


        table3.addCell(Cell().add(Paragraph("Rate")))
        table3.addCell(Cell().add(Paragraph("Amount")))

        table3.addCell(Cell().add(Paragraph("85238020")))
        table3.addCell(Cell().add(Paragraph("$totalWithoutGst")))
        table3.addCell(Cell().add(Paragraph("${productList[0].gst_rate} %")))
        table3.addCell(
            Cell().add(
                Paragraph(
                    "${
                        calculatePercentage(
                            totalWithoutGst,
                            productList[0].gst_rate
                        )
                    }"
                )
            )
        )
        table3.addCell(
            Cell().add(
                Paragraph(
                    "${
                        calculatePercentage(
                            totalWithoutGst,
                            productList[0].gst_rate
                        )
                    }"
                )
            )
        )

        table3.addCell(Cell().add(Paragraph("Total")))
        table3.addCell(Cell().add(Paragraph("$totalWithoutGst")))
        table3.addCell(Cell().add(Paragraph()))
        table3.addCell(
            Cell().add(
                Paragraph(
                    "${
                        calculatePercentage(
                            totalWithoutGst,
                            productList[0].gst_rate
                        )
                    }"
                )
            )
        )
        table3.addCell(
            Cell().add(
                Paragraph(
                    "${
                        calculatePercentage(
                            totalWithoutGst,
                            productList[0].gst_rate
                        )
                    }"
                )
            )
        )


        val table4 = Table(floatArrayOf(280f, 280f))
        table4.setBorder(topBorder)
        table4.addCell(
            Cell(1, 2).add(
                Paragraph(
                    "Tax Amount (in words) : ${
                        calculatePercentage(
                            totalWithoutGst,
                            productList[0].gst_rate
                        ).toWords("en","US")
                    }"
                )
            )
                .setBorder(Border.NO_BORDER)
        )


        table4.addCell(
            Cell().add(
                Paragraph(
                    "Company’s PAN : AAKCR9587Q\n" + "Declaration\n" + "We declare that this invoice shows the actual price of\n" + "the goods described and that all particulars are true\n" + "and correct."
                ).setFontSize(8f)
            ).setBorder(Border.NO_BORDER)
        )
        table4.addCell(
            Cell().add(
                Paragraph(
                    "Company’s Bank Details\n" + "A/c Holder’s Name : RED CAT HOSPITALITY PRIVATE LIMITED\n" + "Bank Name : IDFC FIRST BANK LTD\n" + "A/c No. : 10184585891\n" + "Branch & IFS Code : Arjun Marg DLF1, Gurgaon & IDFB0020133\n" + "SWIFT Code :"
                ).setFontSize(8f)
            ).setBorder(Border.NO_BORDER)
        )

        val table5 = Table(floatArrayOf(280f, 280f))
        table5.setBorder(topBorder)
        table5.setFontSize(10f)

        table5.addCell(Cell().add(Paragraph("\n \n \n Customer’s Seal and Signature").setTextAlignment(TextAlignment.CENTER)))
        table5.addCell(Cell().add(Paragraph("\n \n \n  Authorised Signatory").setTextAlignment(TextAlignment.CENTER)))


        val table6 = Table(floatArrayOf(560f))
        table6.addCell(
            Cell().add(
                Paragraph("SUBJECT TO GURGAON JURISDICTION").setTextAlignment(
                    TextAlignment.CENTER
                ).setFontSize(10f)
            ).setBorder(Border.NO_BORDER)
        )
        table6.addCell(
            Cell().add(
                Paragraph("This is a Computer Generated Invoice").setTextAlignment(
                    TextAlignment.CENTER
                ).setFontSize(8f)
            ).setBorder(Border.NO_BORDER)
        )


        document.add(table0)
        document.add(table1)
        document.add(table2)
        document.add(table3)
        document.add(table4)
        document.add(table5)
        document.add(table6)

        document.close()



        return file.absolutePath


    }

    fun Double.toWords(language: String, country: String): String {
        val formatter = MessageFormat(
            "{0,spellout,currency}",
            Locale(language, country)
        )
        return formatter.format(arrayOf(this))
    }
    fun calculatePercentage(totalAmount: Double, percentage: Double): Double {
        return (totalAmount * percentage) / 100
    }

    fun Context.drawableToImage(drawable: Int): Image {
        val drawable = ResourcesCompat.getDrawable(resources, drawable, theme)
        val bitmap = (drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val imageData: ImageData = ImageDataFactory.create(stream.toByteArray())
        return Image(imageData).scaleToFit(100f, 120f).setBorder(Border.NO_BORDER)
    }


    fun barCode(serialNumber: String): Image? {
        val multiFormatWriter = MultiFormatWriter()
        return try {
            val bitMatrix: BitMatrix = multiFormatWriter.encode(serialNumber, BarcodeFormat.CODE_128, 100, 50)
            val bitmap = Bitmap.createBitmap(100, 50, Bitmap.Config.RGB_565)

            // Loop through the bitMatrix to set pixel values
            for (i in 0 until 100) {
                for (j in 0 until 50) {
                    val color = if (bitMatrix[i, j]) Color.BLACK else Color.WHITE
                    bitmap.setPixel(i, j, color)
                }
            }

            // Convert the Bitmap to PNG format
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            // Convert byte array to iText Image (using ImageDataFactory for iText 7)
            val imageData = ImageDataFactory.create(byteArray)
            Image(imageData)

        } catch (e: Exception) {
            // Handle any exceptions
            e.printStackTrace()
            null
        }
    }



    data class UserInformation(
        val name: String,
        val address: String,
        val transactionNumber: String,
        val stateName: String,
        val placeOfSupply: String,
        val code: Number,
        val date: String,

        )

    data class ProductInformation(
        val serialNumber: String,
        val description: String,
        val gst_rate: Double,
        val quantity: Long,
        val rate: Double,
        val discount: Double,
        val total: Double
    )

}