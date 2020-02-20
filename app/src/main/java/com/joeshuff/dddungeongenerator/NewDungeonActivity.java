package com.joeshuff.dddungeongenerator;

import android.content.Intent;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.*;
import com.google.gson.Gson;
import com.joeshuff.dddungeongenerator.memory.MemoryController;
import com.joeshuff.dddungeongenerator.memory.MemoryGeneration;
import com.warkiz.tickseekbar.OnSeekChangeListener;
import com.warkiz.tickseekbar.SeekParams;
import com.warkiz.tickseekbar.TickSeekBar;
import org.json.JSONException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NewDungeonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Configure Dungeon");

        loadUI();
        initialiseControls();
    }

    TextInputEditText dungeonSeed;

    AppCompatSeekBar roomSize;

    ScrollView scrollingContent;

    SwitchCompat longerCorridorSwitch;
    TextView longCorridorStatus;

    LinearLayout scrollDownView;

    TickSeekBar monsterFrequency;
    TickSeekBar treasureFrequency;
    TickSeekBar trapFrequency;
    TickSeekBar dungeonDepth;

    MaterialButton generateButton;

    private void loadUI() {
        dungeonSeed = findViewById(R.id.seedInput);

        roomSize = findViewById(R.id.roomSizeBar);

        longerCorridorSwitch = findViewById(R.id.longerCorridorsSwitch);
        longCorridorStatus = findViewById(R.id.longerCorridorsStatus);

        scrollingContent = findViewById(R.id.scrollContent);
        scrollDownView = findViewById(R.id.scrollDownPrompt);

        monsterFrequency = findViewById(R.id.monster_frequency);
        treasureFrequency = findViewById(R.id.treasure_frequency);
        trapFrequency = findViewById(R.id.trap_frequency);
        dungeonDepth = findViewById(R.id.dungeon_depth);

        generateButton = findViewById(R.id.generateButton);
    }

    private void initialiseControls() {
        scrollingContent.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            View view = (View) scrollingContent.getChildAt(scrollingContent.getChildCount()-1);
            int diff = (view.getBottom()-(scrollingContent.getHeight()+scrollY));

            if (diff == 0) {
                //HIDE TEXT
                scrollDownView.animate().alpha(0f).setDuration(200).start();
            }
            else {
                scrollDownView.animate().alpha(1f).setDuration(200).start();
            }
        });

        scrollingContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int viewHeight = scrollingContent.getMeasuredHeight();
                int contentHeight = scrollingContent.getChildAt(0).getHeight();
                if(viewHeight - contentHeight < 0) {
                    // scrollable
                } else {
                    scrollDownView.animate().alpha(0).start();
                }
                scrollingContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        longerCorridorSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                longCorridorStatus.setText("The longest corridors will be prioritised");
            } else {
                longCorridorStatus.setText("The shortest corridors will be prioritised");
            }
        });

        generateButton.setOnClickListener(e -> genButtonClicked());
    }

    private String generateRandomDungeonName() {
        return "Awesome Dungeon Name";
    }

    private void genButtonClicked() {
        Intent generator = new Intent(this, GeneratingActivity.class);

        String seed = dungeonSeed.getText().toString();

        if (seed.isEmpty()) {
            seed = System.currentTimeMillis() + "";

            List<Character> list = seed.chars().mapToObj(c -> new Character((char) c)).collect(Collectors.toList());
            Collections.shuffle(list);
            StringBuilder sb = new StringBuilder();
            list.forEach(c -> sb.append(c));

            seed = sb.toString();
        }

        FirebaseTracker.EVENT(this, "DungeonCreate", "CreatedWithSeed:" + seed);

        MemoryGeneration memory = new MemoryGeneration(seed,
                roomSize.getProgress(),
                longerCorridorSwitch.isChecked(),
                0.5 + ((monsterFrequency.getProgressFloat() / 100.0f) * 2.5f),
                0.5 + ((trapFrequency.getProgressFloat() / 100.0f) * 2.5f),
                0.5 + ((treasureFrequency.getProgressFloat() / 100.0f) * 2.5f),
                1 + (Math.abs(dungeonDepth.getProgress() - 50) / 50) * 2,
                dungeonDepth.getProgress()
        );

        try {
            MemoryController.addToMemory(getApplicationContext(), memory);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        generator.putExtra("instructions", new Gson().toJson(memory));

        startActivity(generator);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }

        return false;
    }
}
