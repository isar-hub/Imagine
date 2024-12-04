package com.isar.imagine.billing

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.isar.imagine.R
import com.isar.imagine.barcode_scenning.BarCodeScanningActivity
import com.isar.imagine.utils.CustomDialog
import com.isar.imagine.utils.getTextView
import com.rajat.pdfviewer.PdfRendererView
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class ShowBillFragment : Fragment() {

    private lateinit var pdfView: PdfRendererView
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var file: File
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val path = requireArguments().getString("PDF_PATH")
        Log.d("ShowBillFragment", "Received PDF path: $path")

        if (path != null) {
            file = File(path)
            Log.d("ShowBillFragment", "File created: ${file.absolutePath}")

            if (file.exists()) {
                try {
                    Log.d(
                        "ShowBillFragment",
                        "Initializing PDF view with file: ${file.absolutePath}"
                    )
                    pdfView.initWithFile(file)

                } catch (e: Exception) {
                    Log.e("ShowBillFragment", "Error loading PDF: ${e.message}")
                    CustomDialog.showAlertDialog(
                        requireContext(),
                        requireContext().getTextView("Failed to load PDF. Please try again."),
                        "ERROR"
                    )
                }
            } else {
                Log.e("ShowBillFragment", "File not found at: $path")
                CustomDialog.showAlertDialog(
                    requireContext(), requireContext().getTextView("PDF file not found"), "ERROR"
                )
            }
        } else {
            Log.e("ShowBillFragment", "PDF path is null")
            CustomDialog.showAlertDialog(
                requireContext(), requireContext().getTextView("PDF path is null"), "ERROR"
            )
        }

        bottomAppBar(file)
    }

    private fun bottomAppBar(file: File) {
        Log.d("ShowBillFragment", "Setting up BottomAppBar with file options")
        bottomNavigationView.setOnItemSelectedListener {
            Log.d("BottomAppBar", "Item clicked: ${it.itemId}")

            when (it.itemId) {
                R.id.share -> {
                    Log.d("ShowBillFragment", "Share option clicked")
                    sharePDF(file)
                    true
                }

                R.id.print -> {
                    Log.d("ShowBillFragment", "Print option clicked")
                    printPDF(file)
                    true
                }

                R.id.download -> {
                    Log.d("ShowBillFragment", "Download option clicked")
                    CustomDialog.showAlertDialog(
                        requireContext(),
                        requireContext().getTextView(file.absolutePath),
                        "Downloaded"
                    )
                    true
                }

                else -> false
            }
        }


        bottomAppBar.setOnMenuItemClickListener { item ->
            Log.d("BottomAppBar", "Item clicked: ${item.itemId}")
            when (item.itemId) {
                R.id.share -> {
                    Log.d("ShowBillFragment", "Share option clicked")
                    sharePDF(file)
                    true
                }

                R.id.print -> {
                    Log.d("ShowBillFragment", "Print option clicked")
                    printPDF(file)
                    true
                }

                R.id.download -> {
                    Log.d("ShowBillFragment", "Download option clicked")
                    CustomDialog.showAlertDialog(
                        requireContext(),
                        requireContext().getTextView(file.absolutePath),
                        "Downloaded"
                    )
                    true
                }

                else -> false
            }
        }
    }

    private fun printPDF(file: File) {
        Log.d("ShowBillFragment", "Starting print process for file: ${file.absolutePath}")
        val printManager = requireActivity().getSystemService(Context.PRINT_SERVICE) as PrintManager

        try {
            val printAdapter = object : PrintDocumentAdapter() {
                override fun onStart() {
                    super.onStart()
                    Log.d("PrintDocumentAdapter", "Printing started")
                }

                override fun onLayout(
                    oldAttributes: PrintAttributes?,
                    newAttributes: PrintAttributes?,
                    cancellationSignal: CancellationSignal?,
                    callback: LayoutResultCallback?,
                    args: Bundle?
                ) {
                    Log.d("PrintDocumentAdapter", "Layout phase started")
                    callback?.onLayoutFinished(
                        PrintDocumentInfo.Builder(file.name)
                            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                            .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN).build(),
                        oldAttributes == newAttributes
                    )
                }

                override fun onWrite(
                    pages: Array<out PageRange>?,
                    destination: ParcelFileDescriptor?,
                    cancellationSignal: CancellationSignal?,
                    callback: WriteResultCallback?
                ) {
                    try {
                        Log.d("PrintDocumentAdapter", "Writing document to output stream")
                        val inputStream = FileInputStream(file)
                        val outputStream = FileOutputStream(destination?.fileDescriptor)
                        inputStream.copyTo(outputStream)
                        callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                        Log.d("PrintDocumentAdapter", "PDF writing finished")
                    } catch (e: Exception) {
                        Log.e("PrintDocumentAdapter", "Error printing PDF: ${e.message}")
                    }
                }
            }

            printManager.print("Documents", printAdapter, PrintAttributes.Builder().build())
            Log.d("ShowBillFragment", "Print job submitted")
        } catch (e: Exception) {
            Log.e("ShowBillFragment", "Error initiating print: ${e.message}")
        }
    }

    private fun sharePDF(file: File) {
        Log.d("ShowBillFragment", "Sharing PDF: ${file.absolutePath}")


        val uri = FileProvider.getUriForFile(
            requireContext(), "com.isar.imagine.provider", file
        )
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "application/pdf"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(Intent.createChooser(intent, "Share PDF"))
        Log.d("ShowBillFragment", "Share intent started")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        Log.d("ShowBillFragment", "Creating view for the fragment")
        val v = inflater.inflate(R.layout.fragment_show_bill, container, false)
        bottomAppBar = v.findViewById(R.id.bottom_app_bar)
        bottomNavigationView = v.findViewById(R.id.navigation)
        pdfView = v.findViewById(R.id.pdfView)

        val done = v.findViewById<MaterialButton>(R.id.done)
        done.setOnClickListener {
            startActivity(Intent(requireContext(), BarCodeScanningActivity::class.java))
        }
        return v
    }
}
