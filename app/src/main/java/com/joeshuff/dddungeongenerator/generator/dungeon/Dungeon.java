package com.joeshuff.dddungeongenerator.generator.dungeon;

import android.graphics.Point;
import com.joeshuff.dddungeongenerator.GeneratingActivity;
import com.joeshuff.dddungeongenerator.util.Logs;
import com.joeshuff.dddungeongenerator.generator.features.RoomFeature;
import com.joeshuff.dddungeongenerator.generator.features.StairsFeature;
import com.joeshuff.dddungeongenerator.generator.floors.DungeonSection;
import com.joeshuff.dddungeongenerator.generator.floors.Floor;
import com.joeshuff.dddungeongenerator.generator.generating.MinSpanningTree;
import com.joeshuff.dddungeongenerator.generator.monsters.Bestiary;

import java.util.*;

public class Dungeon {

	transient public static int MAP_SIZE = 800;

	public String name;

	public int startX;
	public int startY;
	public int endX;
	public int endY;
	public int width;
	public int height;

	Environment.ENVIRONMENT_TYPE selectedEnvironment;

	Creator.CREATOR dungeonCreator;

	Purpose.PURPOSE dungeonPurpose;

	History.HISTORY dungeonHistory;

	Modifier globalModifier = new Modifier();

	Modifier userModifier = new Modifier();

	transient Random rnd; //SEED THIS IF YOU WANT THE SAME RESULTS
	String seed = "";

	List<Floor> dungeonFloors = new ArrayList<>();

	//Generation all complete
	transient boolean allCompleted = false;

//	HashMap<Integer, Giffer> floorGifs = new HashMap<>();
//	Giffer fainGiffer = new Giffer(0);

	transient GeneratingActivity activity;

	public Dungeon() {}

	public Dungeon (GeneratingActivity c, int startX, int startY, int endX, int endY) {
		activity = c;

		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		
		this.width = endX - startX;
		this.height = endY - startY;

		Bestiary.launchBestiary(c);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDungeonDescription() {
        String description = "You find yourself in a dungeon " + getSelectedEnvironment().getDescription().toLowerCase() + ".\n";

        if (getDungeonCreator() == Creator.CREATOR.NO_CREATOR) {
            description = description + "You discover that this dungeon is naturally formed.";
        } else {
            description = description + "This dungeon seems to be created by " + getDungeonCreator().getDescription() + "\n";
            description = description + "They created this dungeon as a " + getDungeonPurpose().getTitle().toLowerCase() + "\n" + getDungeonPurpose().getDescription();
        }

        return description;
    }

    public void setUserModifier(Modifier modifier) {
		this.userModifier = modifier;
	}

	public void setSeed(String seed) {
		String numberseed = "";

		for (char c : seed.toCharArray()) {
			int cVal = c;
			numberseed = numberseed + cVal;
		}

		this.seed = seed;
        rnd = new Random(stringToSeed(numberseed));
		Logs.i("Dungeon", "seed is : " + seed, null);
	}

	public static long stringToSeed(String s) {
		if (s == null) {
			return 0;
		}
		long hash = 0;
		for (char c : s.toCharArray()) {
			hash = 31L*hash + c;
		}
		return hash;
	}

	public void setRoomSize(int percentage) {
		Room.changeRoomSize(percentage / 100f);
	}

	public void setLongCorridors(boolean longCorridors) {
		MinSpanningTree.setLongCorridors(longCorridors);
	}

	public void setLinearProgression(boolean linearProgression) {
		DungeonSection.linearProgression = linearProgression;
	}

	public Environment.ENVIRONMENT_TYPE getSelectedEnvironment() {
		return selectedEnvironment;
	}

	public Creator.CREATOR getDungeonCreator() {
		return dungeonCreator;
	}

	public Purpose.PURPOSE getDungeonPurpose() {
		return dungeonPurpose;
	}

	public History.HISTORY getDungeonHistory() {
		return dungeonHistory;
	}

	public String getSeed() {
		return seed;
	}

	public void generateAttributes() {
		selectedEnvironment = Environment.generateEnvironmentType(rnd);
		dungeonCreator = Creator.generateCreator(rnd);

		List<Modifier> modifiers = new ArrayList<>();
		modifiers.add(userModifier);
		modifiers.addAll(Arrays.asList(selectedEnvironment.getModifier(), dungeonCreator.getModifier()));

		if (dungeonCreator != Creator.CREATOR.NO_CREATOR) {
			dungeonPurpose = Purpose.getPurpose(rnd);
			modifiers.add(dungeonPurpose.getModifier());

			dungeonHistory = History.getHistory(rnd);
			modifiers.add(dungeonHistory.getModifier());
		}

		globalModifier = Modifier.combineModifiers(modifiers, true);

		name = NameGenerator.generateName(this);
	}

	public void generate() {
		generateAttributes();

		Floor firstFloor = new Floor(this, rnd, 0);
		firstFloor.fillFloor();
//        firstFloor.splitFloor();

		dungeonFloors.add(firstFloor);

		branchOut();

		calculateNearestPartner();

		calculateRejectedRooms();

		triangulate();

		minSpanningTree();

		combinePaths();

		clearUnnecessaryData();

		pathFind();

		finalise();

		complete();
	}

    public Floor addFloorForLevel(int level) {
        Optional<Floor> theFloor = dungeonFloors.stream().filter(floor -> floor.getLevel() == level).findFirst();

        if (theFloor.isPresent()) {
            return theFloor.get();
        }

        Floor newFloor = new Floor(this, rnd, level);
        dungeonFloors.add(newFloor);

        return newFloor;
    }

    public Floor getLowestDungeonFloor() {
		Floor lowest = getDungeonFloors().get(0);

		for (Floor floor : getDungeonFloors()) {
			if (floor.getLevel() < lowest.getLevel()) {
				lowest = floor;
			}
		}

		return lowest;
	}

	public Floor getHighestDungeonFloor() {
		Floor highest = getDungeonFloors().get(0);

		for (Floor floor : getDungeonFloors()) {
			if (floor.getLevel() > highest.getLevel()) {
				highest = floor;
			}
		}

		return highest;
	}

	public Floor getDungeonFloorAtLevel(int level) {
		for (Floor f : getDungeonFloors()) {
			if (f.getLevel() == level) return f;
		}

		return null;
	}

	private void branchOut() {
        activity.setProgressText("Generating Rooms...");

		for (int i = 0; i < dungeonFloors.size(); i ++) {
			Logs.i("Dungeon", "generating rooms for floor " + i, null);
			dungeonFloors.get(i).branchOut();
		}
	}

    private void calculateNearestPartner() {
        activity.setProgressText("Spreading out Rooms...");

	    for (Floor floor : dungeonFloors) {
            for (DungeonSection section : floor.getSectionList()) {
                section.calculateNearestPartner();
            }
        }
    }

    private void calculateRejectedRooms() {
        for (Floor floor : dungeonFloors) {
            for (DungeonSection section : floor.getSectionList()) {
                section.calculateRejectedRooms();
            }
        }
    }

    private void triangulate() {
		activity.setProgressText("Triangulating Rooms...");

        for (Floor floor : dungeonFloors) {
            for (DungeonSection section : floor.getSectionList()) {
                section.triangulate();
            }
        }
    }

    private void minSpanningTree() {
		activity.setProgressText("Finding best corridors...");

        for (Floor floor : dungeonFloors) {
            for (DungeonSection section : floor.getSectionList()) {
                section.minSpanningTree();
            }
        }
    }

    private void combinePaths() {
        for (Floor floor : dungeonFloors) {
            for (DungeonSection section : floor.getSectionList()) {
                section.combinePaths();
            }
        }
    }

    private void pathFind() {
		activity.setProgressText("Connecting Rooms...");

        for (Floor floor : dungeonFloors) {
            for (DungeonSection section : floor.getSectionList()) {
                section.pathFind();
            }
        }
    }

    private void finalise() {
		activity.setProgressText("Generation Complete");

        for (Floor floor : dungeonFloors) {
            for (DungeonSection section : floor.getSectionList()) {
                section.finalise();
            }

			int i = 1;
			for (Room r : floor.getAllRooms()) {
				r.setId(i);
				i ++;
			}
        }
    }

    private void clearUnnecessaryData() {
        for (Floor floor : dungeonFloors) {
            for (DungeonSection section : floor.getSectionList()) {
                section.clearUnnecessaryData();
            }
        }
    }

	public void complete() {
	    allCompleted = true;

	    List<StairsFeature> stairsToDecide = new ArrayList<>();

		for (Floor f : dungeonFloors) {
		    for (Room r : f.getAllRooms()) {
		        for (RoomFeature rf : r.getFeatureList()) {
		            if (rf instanceof StairsFeature) {
                        stairsToDecide.add((StairsFeature) rf);
                    }
                }
            }
        }

        for (StairsFeature sf : stairsToDecide) sf.getConnectedRoom();

        activity.runOnUiThread(() ->activity.onCompleted());
	}

	public Room getRoomAt(int floor, int x, int y) {
	    for (Room r : getDungeonFloors().get(floor).getAllRooms()) {
	        if (r.containsGlobalPoint(new Point(x, y))) return r;
        }

        return null;
    }

	public Random getRnd() {
		return rnd;
	}

	public Modifier getGlobalModifier() {
		return globalModifier;
	}

    public List<Floor> getDungeonFloors() {
        return dungeonFloors;
    }
}