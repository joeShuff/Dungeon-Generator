package com.joeshuff.dddungeongenerator.memory;

import androidx.annotation.NonNull;
import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon;
import com.joeshuff.dddungeongenerator.generator.dungeon.Modifier;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MemoryGeneration implements Comparable<MemoryGeneration> {

    int epoch;
    private String generatedAt;

    private String seed;
    private int roomSize;
    private boolean longCorridors;
    private boolean loops = false;
    private double monsterFrequency = 1.0f;
    private double treasureFrequency = 1.0f;
    private double trapFrequency = 1.0f;
    private double stairsFrequency = 1.0f;
    private int depth = 50;

    public MemoryGeneration(String seed,
                            int roomSize,
                            boolean longCorridors,
                            double monsterFrequency,
                            double trapFrequency,
                            double treasureFrequency,
                            double stairsFrequency,
                            int depth) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy | HH:mm:ss");
        epoch = (int) (System.currentTimeMillis() / 1000d);
        generatedAt = sdf.format(new Date());

        this.seed = seed;
        this.roomSize = roomSize;
        this.longCorridors = longCorridors;
        this.monsterFrequency = monsterFrequency;
        this.trapFrequency = trapFrequency;
        this.treasureFrequency = treasureFrequency;
        this.stairsFrequency = stairsFrequency;
        this.depth = depth;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public String getSeed() {
        return seed;
    }

    public int getRoomSize() {
        return roomSize;
    }

    public boolean isLongCorridors() {
        return longCorridors;
    }

    public boolean isLoops() {
        return loops;
    }

    public Modifier getUserModifier() {
        return new Modifier()
                .setMonsterChanceModifier(monsterFrequency)
                .setTrapChanceModifier(trapFrequency)
                .setTreasureChanceModifier(treasureFrequency)
                .setStairChanceModifier(stairsFrequency)
                .setGrowthChanceUp(depth);
    }

    public double getMonsterFrequency() {
        return monsterFrequency;
    }

    public double getTreasureFrequency() {
        return treasureFrequency;
    }

    public double getTrapFrequency() {
        return trapFrequency;
    }

    public double getStairsFrequency() {
        return stairsFrequency;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MemoryGeneration)) return false;
        return ((MemoryGeneration) obj).epoch == epoch;
    }

    @Override
    public int compareTo(@NonNull MemoryGeneration o) {
        int x = o.epoch;
        int y = epoch;

        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    public String workOutDungeonName() {
        Dungeon test = new Dungeon();
        test.setSeed(getSeed());

        test.generateAttributes();
        return test.getName();
    }
}
