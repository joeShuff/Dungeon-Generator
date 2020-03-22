package com.joeshuff.dddungeongenerator.screens.viewmonster;

import android.net.Uri;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.gson.Gson;
import com.joeshuff.dddungeongenerator.R;
import com.joeshuff.dddungeongenerator.RecyclerViewEmptySupport;
import com.joeshuff.dddungeongenerator.generator.monsters.Monster;
import com.joeshuff.dddungeongenerator.screens.viewmonster.ActionAdapter;
import com.joeshuff.dddungeongenerator.util.FirebaseTracker;

public class MonsterActivity extends AppCompatActivity {

    Monster displayingMonster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monster_display);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            displayingMonster = new Gson().fromJson(getIntent().getStringExtra("monster"), Monster.class);
        } catch (Exception e) {}

        if (displayingMonster == null) {
            Toast.makeText(this, "No Monster Found", Toast.LENGTH_LONG).show();
            finish();
        }

        setTitle(displayingMonster.getName());

        loadMonsterDetails();
    }

    private void loadMonsterDetails() {
        ((TextView) findViewById(R.id.topDesc)).setText(displayingMonster.getSize() + " " + displayingMonster.getType() +", " +
                                                        displayingMonster.getAlignment());

        ((TextView) findViewById(R.id.armorClass)).setText(Html.fromHtml("<b>Armor Class</b>    " + displayingMonster.getArmorClass()));
        ((TextView) findViewById(R.id.hitpoints)).setText(Html.fromHtml("<b>Hit Points</b>    " + displayingMonster.getHitPoints()));
        ((TextView) findViewById(R.id.speed)).setText(Html.fromHtml("<b>Speed</b>    " + displayingMonster.getSpeed()));

        ((TextView) findViewById(R.id.strengthValue)).setText(displayingMonster.getStrength() + " " + displayingMonster.getModifierFor(displayingMonster.getStrength()));
        ((TextView) findViewById(R.id.dexValue)).setText(displayingMonster.getDexterity() + " " + displayingMonster.getModifierFor(displayingMonster.getDexterity()));
        ((TextView) findViewById(R.id.consValue)).setText(displayingMonster.getConstitution() + " " + displayingMonster.getModifierFor(displayingMonster.getConstitution()));
        ((TextView) findViewById(R.id.intelligenceValue)).setText(displayingMonster.getIntelligence() + " " + displayingMonster.getModifierFor(displayingMonster.getIntelligence()));
        ((TextView) findViewById(R.id.wisdomValue)).setText(displayingMonster.getWisdom() + " " + displayingMonster.getModifierFor(displayingMonster.getWisdom()));
        ((TextView) findViewById(R.id.charismaValue)).setText(displayingMonster.getCharisma() + " " + displayingMonster.getModifierFor(displayingMonster.getCharisma()));

        ((TextView) findViewById(R.id.savingThrows)).setText(Html.fromHtml("<b>Saving Throws</b>    " + displayingMonster.getSavingThrows()));
        ((TextView) findViewById(R.id.senses)).setText(Html.fromHtml("<b>Senses</b>    " + displayingMonster.getSenses()));
        ((TextView) findViewById(R.id.languages)).setText(Html.fromHtml("<b>Languages</b>    " + displayingMonster.getLanguages()));
        ((TextView) findViewById(R.id.challenge)).setText(Html.fromHtml("<b>Challenge</b>    " + displayingMonster.getChallengeRating() + " (" + displayingMonster.getChallengeXp() + " XP)"));

        RecyclerViewEmptySupport specialActionsList = findViewById(R.id.specialActionsList);
        specialActionsList.setEmptyView(findViewById(R.id.noSpecialActions));
        specialActionsList.setLayoutManager(new LinearLayoutManager(this));
        specialActionsList.setAdapter(new ActionAdapter(displayingMonster.getSpecialAbilities()));


        RecyclerViewEmptySupport actionsList = findViewById(R.id.actionsList);
        actionsList.setEmptyView(findViewById(R.id.noActions));
        actionsList.setLayoutManager(new LinearLayoutManager(this));
        actionsList.setAdapter(new ActionAdapter(displayingMonster.getActions()));

        RecyclerViewEmptySupport legendaryActions = findViewById(R.id.legendaryActionsList);
        legendaryActions.setEmptyView(findViewById(R.id.noLegendaryActions));
        legendaryActions.setLayoutManager(new LinearLayoutManager(this));
        legendaryActions.setAdapter(new ActionAdapter(displayingMonster.getLegendaryActions()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.monster_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            case R.id.monster_external:
                if (displayingMonster == null) return true;

                FirebaseTracker.EVENT(this, "EXTERNAL MONSTER", "Showing Monster: " + displayingMonster.getName());

                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(this, Uri.parse("https://www.dndbeyond.com/monsters/" + displayingMonster.getName().toLowerCase().replace(" ", "-")));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
