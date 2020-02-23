package com.joeshuff.dddungeongenerator;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.joeshuff.dddungeongenerator.memory.MemoryController;
import com.joeshuff.dddungeongenerator.screens.HomeListAdapter;
import com.joeshuff.dddungeongenerator.util.AppPreferences;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        AppPreferences.INSTANCE.init(getApplicationContext());
        AppCompatDelegate.setDefaultNightMode(AppPreferences.INSTANCE.getDarkThemeMode());

        doUpdate();
    }

    public void genNewClicked(View view) {
        startActivity(new Intent(this, NewDungeonActivity.class));
    }

    public void doUpdate() {
        RecyclerViewEmptySupport recyclerViewEmptySupport = findViewById(R.id.homeHistoryList);
        recyclerViewEmptySupport.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEmptySupport.setEmptyView(findViewById(R.id.emptyHomeView));
        recyclerViewEmptySupport.setAdapter(new HomeListAdapter(MemoryController.getMemory(getApplicationContext())));
    }

    @Override
    protected void onResume() {
        super.onResume();
        doUpdate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.newDungeon) {
            genNewClicked(null);
            return true;
        }

        if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        return false;
    }
}
