package com.joeshuff.dddungeongenerator.pdfviewing

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.joeshuff.dddungeongenerator.util.Logs
import com.joeshuff.dddungeongenerator.R
import com.joeshuff.dddungeongenerator.util.AppPreferences.pdfIsDark
import kotlinx.android.synthetic.main.activity_pdf_view.*

open class PDFActivity : AppCompatActivity() {

    companion object {
        var filenameKey = "PDF_filename"
        var titleNameKey = "title_filename"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!intent.hasExtra(filenameKey) || !intent.hasExtra(titleNameKey)) {
            Toast.makeText(this, "Not enough info to show PDF", Toast.LENGTH_LONG).show()
            finish()
        } else {
            supportActionBar?.title = intent.getStringExtra(titleNameKey)

            try {
                pdfView
                        .fromStream(applicationContext.assets.open(intent.getStringExtra(filenameKey)))
                        .spacing(4)
                        .nightMode(pdfIsDark)
                        .load()
            } catch (e: Exception) {
                Logs.e("PDFActivity", "Error setting PDF file", e)
                Toast.makeText(this, "Something went wrong loading that PDF", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.pdf_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()
                return true
            }
            R.id.pdf_day_night_toggle -> {
                pdfIsDark = !pdfIsDark
                pdfView.setNightMode(pdfIsDark)
                pdfView.requestLayout()
                return true
            }
        }

        return false
    }
}