package main;

import org.newdawn.slick.Color;

public class Node {
	
	public double x, y;
	
	public Node(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Color getColor() {
		return Color.black;
	}
	
}
