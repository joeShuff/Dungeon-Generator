package com.joeshuff.dddungeongenerator;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon;
import com.joeshuff.dddungeongenerator.generator.features.*;
import com.joeshuff.dddungeongenerator.generator.models.RuntimeTypeAdapterFactory;
import com.joeshuff.dddungeongenerator.memory.MemoryController;
import com.joeshuff.dddungeongenerator.memory.MemoryGeneration;

import java.io.IOException;

public class GeneratingActivity extends AppCompatActivity {

    transient boolean leftMidGeneration = false;

    transient TextView progressText;

    transient static RuntimeTypeAdapterFactory<RoomFeature> roomFeatureAdapter =
            RuntimeTypeAdapterFactory.of(RoomFeature.class, "type")
                    .registerSubtype(TreasureFeature.class, "Treasure")
                    .registerSubtype(MonsterFeature.class, "Monster")
                    .registerSubtype(StairsFeature.class, "Stairs")
                    .registerSubtype(TrapFeature.class, "Trap");

    Dungeon dungeon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generating);

        progressText = findViewById(R.id.progressText);

        startGenerating();
    }

    private void startGenerating() {
        dungeon = new Dungeon(this, 0, 0, 800, 800);

        MemoryGeneration instructions = new Gson().fromJson(getIntent().getStringExtra("instructions"), MemoryGeneration.class);

        dungeon.setSeed(instructions.getSeed());
        dungeon.setRoomSize(instructions.getRoomSize());
        dungeon.setLongCorridors(instructions.isLongCorridors());
        dungeon.setLinearProgression(instructions.isLoops());
        dungeon.setUserModifier(instructions.getUserModifier());

        new Thread(() -> dungeon.generate()).start();

//        new Handler().postDelayed(() -> onCompleted(), 5000);
    }

    public void onCompleted() {
        Intent results = new Intent(this, ResultsActivity.class);

        Gson gson = new GsonBuilder().registerTypeAdapterFactory(roomFeatureAdapter).create();

        MemoryController.saveToSharedPreferences(getApplicationContext(), "RECENT_DUNGEON", gson.toJson(dungeon));

//        try {
//            results.putExtra("dungeon", gson.toJson(dungeon));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        startActivity(results);
        finish();
    }

    public void setProgressText(String text) {
        System.out.println(text);
        runOnUiThread(() -> progressText.setText(text));
    }

    @Override
    protected void onPause() {
        super.onPause();

        leftMidGeneration = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (leftMidGeneration) {
            Toast.makeText(this, "You left wtf", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Generating, please don't exit the app", Toast.LENGTH_SHORT).show();
    }
}