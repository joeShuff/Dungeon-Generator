package com.joeshuff.dddungeongenerator.pdfviewing

import android.graphics.pdf.PdfDocument
import android.os.Bundle

/**
 * These are separated into 2 classes so that firebase analytics can easily seperate them into 2 screens.
 * So I can see how many people read the paper/licences.
 */

class PaperActivity : PDFActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        intent.putExtra(filenameKey, "paper.pdf")
        intent.putExtra(titleNameKey, "Research Paper")
        super.onCreate(savedInstanceState)
    }
}

class SRDActivity: PDFActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        intent.putExtra(filenameKey, "SRD.pdf")
        intent.putExtra(titleNameKey, "Licences")
        super.onCreate(savedInstanceState)
    }
}