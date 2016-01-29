package main;

import org.newdawn.slick.Color;

public class Node {
	
	public double x, y;
	public boolean clicked;
	public boolean val;
	
	public Node(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Color getColor() {
		Color c = val ? Color.green:Color.red;
		if(clicked)
			c = c.darker().darker();
		return c;
	}
	
	public void trigger() {
		clicked = !clicked;
	}
	
	public void reset() {
		clicked = false;
	}
	
}
