package org.hyperion.rs2.pf;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Represents a path found by a <code>PathFinder</code> between two points.
 * @author Graham Edgecombe
 *
 */
public class Path {
	
	/**
	 * The queue of points.
	 */
	private final Deque<Point> tiles = new LinkedList<Point>();
	
	/**
	 * Creates an empty path.
	 */
	public Path() {
		
	}
	
	/**
	 * Adds a point onto the queue.
	 * @param p The point to add.
	 */
	public void addPoint(Point p) {
		Point p2 = tiles.peekLast();
		if(p2 != null && p2.equals(p)) {
			return;
		} else {
			tiles.add(p);
		}
	}
	
	/**
	 * Gets the deque backing this path.
	 * @return The deque backing this path.
	 */
	public Deque<Point> getPoints() {
		return tiles;
	}
	
}
