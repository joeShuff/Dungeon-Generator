package com.joeshuff.dddungeongenerator.generator.dungeon;

import com.joeshuff.dddungeongenerator.generator.monsters.MonsterClass.MONSTER_CLASS;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

public class Modifier  {

    double monsterChanceModifier = 0d;

    int monsterAverageGroupSize = 0; //default 3

    double trapChanceModifier = 0d;

    int trapMagicalChance = 0; //default 50

    double treasureChanceModifier = 0d;

    int mapCoveragePercentage = 0; //default 65

    int triangulationAdditionChance = 0; //default 15

    double bossChanceModifier = 0d;

    /**
     * This is the chance of, when a stair feature is generated, those stairs going up. The change of them going down
     * is 100 - this
     */
    double growthChanceUp = 0; //default 0.5

    double stairChanceModifier = 0d;

    List<MONSTER_CLASS> preferredMonsters = new ArrayList<>();

    List<MONSTER_CLASS> blockedMonsters = new ArrayList<>();

    public void loadDefaults() {
        monsterChanceModifier = 1d;
        monsterAverageGroupSize = 3;
        trapChanceModifier = 1d;
        trapMagicalChance = 50;
        treasureChanceModifier = 1d;
        mapCoveragePercentage = 65;
        triangulationAdditionChance = 15;
        bossChanceModifier = 1d;
        growthChanceUp = 0.5d;
        stairChanceModifier = 1d;
    }

    public static Modifier combineModifiers(List<Modifier> modifiers, boolean defaults) {

        Modifier finalModifier = new Modifier();
        if (defaults) finalModifier.loadDefaults();

        OptionalDouble averageMonsterChance = modifiers.stream().filter(o -> o.monsterChanceModifier > 0d).mapToDouble(o -> o.monsterChanceModifier).average();
        OptionalDouble averageMonsterAmount = modifiers.stream().filter(o -> o.monsterAverageGroupSize > 0).mapToInt(o -> o.monsterAverageGroupSize).average();
        OptionalDouble averageTrapModifier = modifiers.stream().filter(o -> o.trapChanceModifier > 0).mapToDouble(o -> o.trapChanceModifier).average();
        OptionalDouble averageTrapMagicalChance = modifiers.stream().filter(o -> o.trapMagicalChance > 0).mapToInt(o -> o.trapMagicalChance).average();
        OptionalDouble averageTreasureChanceModifier = modifiers.stream().filter(o -> o.treasureChanceModifier > 0).mapToDouble(o -> o.treasureChanceModifier).average();
        OptionalDouble averageMapCoveragePercentage = modifiers.stream().filter(o -> o.mapCoveragePercentage > 0).mapToInt(o -> o.mapCoveragePercentage).average();
        OptionalDouble averageTriangulationPercentage = modifiers.stream().filter(o -> o.triangulationAdditionChance > 0).mapToInt(o -> o.triangulationAdditionChance).average();
        OptionalDouble averageBossChanceModifier = modifiers.stream().filter(o -> o.bossChanceModifier > 0).mapToDouble(o -> o.bossChanceModifier).average();
        OptionalDouble averageGrowthUpChance = modifiers.stream().filter(o -> o.growthChanceUp > 0).mapToDouble(o -> o.growthChanceUp).average();
        OptionalDouble averageStairsChangeModifier = modifiers.stream().filter(o -> o.stairChanceModifier > 0).mapToDouble(o -> o.stairChanceModifier).average();

        if (averageMonsterChance.isPresent()) { finalModifier.monsterChanceModifier = averageMonsterChance.getAsDouble(); }

        if (averageMonsterAmount.isPresent()) { finalModifier.monsterAverageGroupSize = (int) Math.ceil(averageMonsterAmount.getAsDouble()); }

        if (averageTrapModifier.isPresent()) { finalModifier.trapChanceModifier = averageTrapModifier.getAsDouble(); }

        if (averageTrapMagicalChance.isPresent()) { finalModifier.trapMagicalChance = (int) Math.ceil(averageTrapMagicalChance.getAsDouble()); }

        if (averageTreasureChanceModifier.isPresent()) { finalModifier.treasureChanceModifier = averageTreasureChanceModifier.getAsDouble(); }

        if (averageMapCoveragePercentage.isPresent()) { finalModifier.mapCoveragePercentage = (int) Math.ceil(averageMapCoveragePercentage.getAsDouble()); }

        if (averageTriangulationPercentage.isPresent()) { finalModifier.triangulationAdditionChance = (int) Math.ceil(averageTriangulationPercentage.getAsDouble()); }

        if (averageBossChanceModifier.isPresent()) { finalModifier.bossChanceModifier = averageBossChanceModifier.getAsDouble(); }

        if (averageGrowthUpChance.isPresent()) { finalModifier.growthChanceUp = averageGrowthUpChance.getAsDouble(); }

        if (averageStairsChangeModifier.isPresent()) {
            finalModifier.stairChanceModifier = averageStairsChangeModifier.getAsDouble();
        }

        modifiers.stream().forEach(modifier -> finalModifier.preferredMonsters.addAll(modifier.getPreferredMonsters()));
        modifiers.stream().forEach(modifier -> finalModifier.blockedMonsters.addAll(modifier.getBlockedMonsters()));

        return finalModifier;
    }

    public Modifier setMonsterChanceModifier(double monsterChanceModifier) {
        this.monsterChanceModifier = monsterChanceModifier;
        return this;
    }

    public Modifier setTrapChanceModifier(double trapChanceModifier) {
        this.trapChanceModifier = trapChanceModifier;
        return this;
    }

    public Modifier setTrapMagicalChance(int trapMagicalChance) {
        this.trapMagicalChance = trapMagicalChance;
        return this;
    }

    public Modifier setTreasureChanceModifier(double treasureChanceModifier) {
        this.treasureChanceModifier = treasureChanceModifier;
        return this;
    }

    public Modifier setMapCoveragePercentage(int mapCoveragePercentage) {
        this.mapCoveragePercentage = mapCoveragePercentage;
        return this;
    }

    public Modifier setTriangulationAdditionChance(int triangulationAdditionChance) {
        this.triangulationAdditionChance = triangulationAdditionChance;
        return this;
    }

    public Modifier setBossChanceModifier(double bossChanceModifier) {
        this.bossChanceModifier = bossChanceModifier;
        return this;
    }

    public Modifier setMonsterAverageGroupSize(int monsterAverageGroupSize) {
        this.monsterAverageGroupSize = monsterAverageGroupSize;
        return this;
    }

    public Modifier setBlockedMonsters(List<MONSTER_CLASS> blockedMonsters) {
        this.blockedMonsters = blockedMonsters;
        return this;
    }

    public Modifier setPreferredMonsters(List<MONSTER_CLASS> preferredMonsters) {
        this.preferredMonsters = preferredMonsters;
        return this;
    }

    public Modifier setGrowthChanceUp(int growthChanceUp) {
        this.growthChanceUp = growthChanceUp / 100.0f;
        return this;
    }

    public Modifier setStairChanceModifier(double stairChanceModifier) {
        this.stairChanceModifier = stairChanceModifier;
        return this;
    }

    public double getMapCoveragePercentage() {
        return mapCoveragePercentage / 100d;
    }

    public double getTriangulationAdditionChange() {
        return triangulationAdditionChance / 100d;
    }

    public double getMonsterChanceModifier() {
        return monsterChanceModifier;
    }

    public int getMonsterAverageGroupSize() {
        return monsterAverageGroupSize;
    }

    public double getTrapChanceModifier() {
        return trapChanceModifier;
    }

    public double getTrapMagicalChance() {
        return trapMagicalChance / 100d;
    }

    public double getTreasureChanceModifier() {
        return treasureChanceModifier;
    }

    public double getBossChanceModifier() {
        return bossChanceModifier;
    }

    public double getGrowthChanceUp() {
        return growthChanceUp;
    }

    public double getStairChanceModifier() {
        return stairChanceModifier;
    }

    public List<MONSTER_CLASS> getPreferredMonsters() {
        return preferredMonsters;
    }

    public List<MONSTER_CLASS> getBlockedMonsters() {
        return blockedMonsters;
    }
}
