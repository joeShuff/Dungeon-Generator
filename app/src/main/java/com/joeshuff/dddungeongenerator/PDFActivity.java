package com.joeshuff.dddungeongenerator;

import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatDelegate;
import com.crashlytics.android.Crashlytics;
import com.github.barteksc.pdfviewer.PDFView;
import com.joeshuff.dddungeongenerator.util.AppPreferences;

import java.io.IOException;

public class PDFActivity extends AppCompatActivity {

    static String filenameKey = "PDF_filename";
    static String titleNameKey = "title_filename";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!getIntent().hasExtra(filenameKey) || !getIntent().hasExtra(titleNameKey)) {
            Toast.makeText(this, "Not enough info to show PDF", Toast.LENGTH_LONG).show();
            finish();
        } else {
            getSupportActionBar().setTitle(getIntent().getStringExtra(titleNameKey));

            try {
                PDFView pdfView = findViewById(R.id.pdfView);
                pdfView
                        .fromStream(getApplicationContext().getAssets().open(getIntent().getStringExtra(filenameKey)))
                        .spacing(4)
                        .nightMode(AppPreferences.INSTANCE.getDarkThemeMode() == AppCompatDelegate.MODE_NIGHT_YES)
                        .load();
            } catch (Exception e) {
                Logs.e("PDFActivity", "Error setting PDF file", e);
                Toast.makeText(this, "Something went wrong loading that PDF", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }

        return false;
    }
}
