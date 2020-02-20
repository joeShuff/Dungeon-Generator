package com.joeshuff.dddungeongenerator;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon;
import com.joeshuff.dddungeongenerator.memory.MemoryController;
import com.joeshuff.dddungeongenerator.screens.ResultsFragment;

public class ResultsActivity extends AppCompatActivity {

    Dungeon generatedDungeon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Gson gson = new GsonBuilder().registerTypeAdapterFactory(GeneratingActivity.roomFeatureAdapter).create();

//        generatedDungeon = gson.fromJson(getIntent().getStringExtra("dungeon"), Dungeon.class);
        generatedDungeon = gson.fromJson(MemoryController.getFromSharedPreferences(getApplicationContext(), "RECENT_DUNGEON"), Dungeon.class);

        getSupportActionBar().setTitle(generatedDungeon.name);

        loadUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.results_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.homeIcon || item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return true;
        }

        return false;
    }

    private void loadUI() {
        ViewPager pager = findViewById(R.id.resultsPager);
        TabLayout tabs = findViewById(R.id.resultsTab);

        pager.setAdapter(new ResultsFragment.ResultFragmentPagerAdapter(this, generatedDungeon));
        tabs.setupWithViewPager(pager);
    }

    /**
     * This method takes a dp value and turns it to an exact value
     */
    public static int dpToExact(Context mainActivity, int dp) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mainActivity.getResources().getDisplayMetrics()));
    }
    public static int dpToExact(Context mainActivity, float dp) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mainActivity.getResources().getDisplayMetrics()));
    }
    public static int spToExact(Context mainActivity, int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, mainActivity.getResources().getDisplayMetrics());
    }

    /**
     * This method gets the amount of pixel wide the screen is.
     *
     * @return Integer - Screen width in pixels
     */
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    /**
     * This method gets the amount of pixels high the screen it.
     *
     * @return Integer - Screen height in pixel
     */
    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}
