package com.joeshuff.dddungeongenerator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.joeshuff.dddungeongenerator.memory.MemoryController;
import com.joeshuff.dddungeongenerator.screens.HomeListAdapter;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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

        if (item.getItemId() == R.id.licences) {
            startActivity(new Intent(this, SRDActivity.class));
        }

        return false;
    }
}
