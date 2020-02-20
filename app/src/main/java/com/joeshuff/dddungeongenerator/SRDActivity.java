package com.joeshuff.dddungeongenerator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import com.crashlytics.android.Crashlytics;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.IOException;

public class SRDActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_srd);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Licences");

        try {
            ((PDFView) findViewById(R.id.pdfView)).fromStream(getApplicationContext().getAssets().open("SRD.pdf")).load();
        } catch (IOException e) {
            try {
                Crashlytics.logException(e);
            } catch (Exception e2) {e2.printStackTrace();}
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
