package com.joeshuff.dddungeongenerator.generator.generating;

import android.graphics.Point;
import com.joeshuff.dddungeongenerator.generator.dungeon.Room;
import com.joeshuff.dddungeongenerator.generator.floors.DungeonSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class PathFinding {

	private static List<Room> rooms;

	public static List<List<Point>> findPaths(DungeonSection section, List<Room> rooms, List<MinSpanningTree.Edge> connectingPoints) {
		List<List<Point>> result = new ArrayList<>();

		PathFinding.rooms = rooms;

		for (MinSpanningTree.Edge e : connectingPoints) {
			result.add(aStarPath(section, e.getStart(), e.getEnd()));
		}

		return result;
	}

	/**
	 * This is the a* Path finding algorithm. It finds the best possible path from the Persons current position to the
	 * defined destination position.
	 *
	 * @param destination - The goal location
	 * @return List<Point> the list of tiles to move to, from their current location to the goal destination.
	 */
	public static List<Point> aStarPath(DungeonSection section, Point start, Point destination) {
		List<Point> emptyList = new ArrayList<>();
		if (destination == null) {
			return emptyList;
		}

		if (destination.equals(start)) {
			return emptyList;
		}

		List<Point> closedSet = new ArrayList<>();

		List<Point> openSet = new ArrayList<>();
		openSet.add(new Point(start.x, start.y));

		HashMap<Point, Integer> gScore = new HashMap<>();
		gScore.put(openSet.get(0), 0);

		HashMap<Point, Point> cameFrom = new HashMap<>();

		HashMap<Point, Double> fScore = new HashMap<>();
		fScore.put(openSet.get(0), heuristic(new Point(start.x, start.y), destination));

		while (!openSet.isEmpty()) {
			Point current = getLowestFScore(openSet, fScore);

			if (current.equals(destination)) {
				return reconstructPath(cameFrom, current);
			}

			openSet.remove(current);
			closedSet.add(current);

			List<Point> neighbours = getNeighbours(section, current);

			for (Point neighbour : neighbours) {
//				if (!getRoom().isWalkableTile(neighbour.x, neighbour.y)) continue;

				if (closedSet.contains(neighbour)) {
					continue;
				}

				int tentativeGScore = gScore.get(current) + distFromNeighbour(current, neighbour);

				if (!openSet.contains(neighbour)) {
					openSet.add(neighbour);
				} else {
					int prevScore = gScore.get(neighbour);

					if (tentativeGScore >= prevScore) {
						continue;
					}
				}

				cameFrom.put(neighbour, current);
				gScore.put(neighbour, tentativeGScore);
//				fScore.put(neighbour, gScore.get(neighbour) + heuristic(neighbour, destination));
				fScore.put(neighbour, heuristic(neighbour, destination));
			}
		}

		return emptyList;
	}

	/**
	 * This method is used to get the cheapest next node from the open list
	 *
	 * @param openSet - The open list of locations
	 * @param fScore  - The estimated scores of each node to the goal
	 * @return Point the next best node from openSet
	 */
	private static Point getLowestFScore(List<Point> openSet, HashMap<Point, Double> fScore) {
		if (openSet.isEmpty()) return null;

		Point lowest = openSet.get(0);
		double lowestInt = fScore.get(lowest);

		for (Point v : openSet) {
			if (fScore.get(v) < lowestInt) {
				lowest = v;
				lowestInt = fScore.get(v);
			}
		}

		return lowest;
	}

	/**
	 * This method gets the distance from one node to another.
	 *
	 * @param current   - One position
	 * @param neighbour - The second position
	 * @return - Integer, the distance between the 2 positions
	 */
	private static int distFromNeighbour(Point current, Point neighbour) {
		return Math.abs(current.x - neighbour.x) + Math.abs(current.y - neighbour.y);
	}

	/**
	 * This method is called once the A* Pathfinding algorithm has been completed. It reconstructs the path from the goal to the start point
	 *
	 * @param cameFrom - A map of a node(key) , and the value being the node that we came from to get to the key node.
	 * @param current  - The final node. The goal destination
	 * @return List<Point> this is the list of tiles that are needed to be walked on to reach the goal
	 */
	private static List<Point> reconstructPath(HashMap<Point, Point> cameFrom, Point current) {
		List<Point> path = new ArrayList<>();
		path.add(current);

		while (cameFrom.keySet().contains(current)) {
			current = cameFrom.get(current);
			path.add(current);
		}

		Collections.reverse(path);

		return path;
	}

	private static List<Point> getNeighbours(DungeonSection section, Point current) {
		int roomWidth = section.getWidth();
		int roomHeight = section.getHeight();

		List<Point> neighbours = new ArrayList<>();

		if (current.x + 1 <= roomWidth) { //EAST
			Point eastNeighbour = new Point(current.x + 1, current.y);

			boolean isValid = true;
			for (Room r : rooms) {
				if (r.topBorder().contains(eastNeighbour) || r.bottomBorder().contains(eastNeighbour)) {
					isValid = false;
				}
			}

			if (isValid) neighbours.add(eastNeighbour);
		}

		if (current.y + 1 <= roomHeight) { //SOUTH
			Point southNeighbour = new Point(current.x, current.y + 1);

			boolean isValid = true;
			for (Room r : rooms) {
				if (r.leftBorder().contains(southNeighbour) || r.rightBorder().contains(southNeighbour)) {
					isValid = false;
				}
			}

			if (isValid) neighbours.add(southNeighbour);
		}

		if (current.x - 1 >= 0) { //WEST
			Point westNeighbour = new Point(current.x - 1, current.y);

			boolean isValid = true;
			for (Room r : rooms) {
				if (r.topBorder().contains(westNeighbour) || r.bottomBorder().contains(westNeighbour)) {
					isValid = false;
				}
			}

			if (isValid) neighbours.add(westNeighbour);
		}

		if (current.y - 1 >= 0) { //NORTH
			Point northNeighbour = new Point(current.x, current.y - 1);

			boolean isValid = true;
			for (Room r : rooms) {
				if (r.leftBorder().contains(northNeighbour) || r.rightBorder().contains(northNeighbour)) {
					isValid = false;
				}
			}

			if (isValid) neighbours.add(northNeighbour);
		}

		return neighbours;
	}

	private static double heuristic(Point start, Point end) {
		return Math.abs(start.x - end.x) + Math.abs(start.y - end.y);
//		return Math.sqrt(Math.pow(start.x - end.x, 2) + Math.pow(start.y - end.y, 2));
	}
}
