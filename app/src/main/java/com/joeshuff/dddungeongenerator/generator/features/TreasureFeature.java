package com.joeshuff.dddungeongenerator.generator.features;

import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TreasureFeature implements RoomFeature {

    transient static List<String> treasureDescriptions = Arrays.asList("There is an abundance of treasure in this room.",
            "There is a shimmering chest in the corner of the room.",
            "There appears to be gold coins pouring out of a statues mouth in the centre of the room.",
            "There is a larger fountain in the centre which appeared to be a wishing fountain."
    );

    String description;

    public TreasureFeature(String seed) {
        this.description = treasureDescriptions.get(new Random(Dungeon.stringToSeed(seed)).nextInt(treasureDescriptions.size()));
    }

    @Override
    public int getPageForMoreInfo() {
        return 0;
    }

    @Override
    public String getFeatureName() {
        return "Treasure";
    }

    @Override
    public String getFeatureDescription() {
        return description;
    }
}
