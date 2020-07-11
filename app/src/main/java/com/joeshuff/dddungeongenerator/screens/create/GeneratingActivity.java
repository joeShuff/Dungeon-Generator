package com.joeshuff.dddungeongenerator.screens.create;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.joeshuff.dddungeongenerator.R;
import com.joeshuff.dddungeongenerator.screens.viewdungeon.ResultsActivity;
import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon;
import com.joeshuff.dddungeongenerator.generator.features.*;
import com.joeshuff.dddungeongenerator.generator.models.RuntimeTypeAdapterFactory;
import com.joeshuff.dddungeongenerator.memory.MemoryController;
import com.joeshuff.dddungeongenerator.memory.MemoryGeneration;
import com.joeshuff.dddungeongenerator.util.Logs;

public class GeneratingActivity extends AppCompatActivity {

    transient boolean leftMidGeneration = false;

    transient TextView progressText;

    transient Trace myTrace;

    public transient static RuntimeTypeAdapterFactory<RoomFeature> roomFeatureAdapter =
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

        myTrace = FirebasePerformance.getInstance().newTrace("generate_dungeon");

        startGenerating();
    }

    private void startGenerating() {
        MemoryGeneration instructions = new Gson().fromJson(getIntent().getStringExtra("instructions"), MemoryGeneration.class);

        dungeon = new Dungeon(this, 0, 0, 800, 800, instructions.getSeed());

        dungeon.setRoomSize(instructions.getRoomSize());
        dungeon.setLongCorridors(instructions.isLongCorridors());
        dungeon.setLinearProgression(instructions.isLoops());
        dungeon.setUserModifier(instructions.getUserModifier());

        if (myTrace != null) myTrace.start();

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

        if (myTrace != null) myTrace.stop();

        startActivity(results);
        finish();
    }

    public void setProgressText(String text) {
        Logs.i("GenerationUpdate", text, null);
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
            Toast.makeText(this, "You interrupted generation.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Generating, please don't exit the app", Toast.LENGTH_SHORT).show();
    }
}
