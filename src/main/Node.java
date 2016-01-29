package main;

import org.newdawn.slick.Color;

public class Node {
	
	public static final Color ON = Color.red, OFF = new Color(0, 240, 0);
	
	public double x, y;
	public boolean clicked;
	public boolean val;
	
	public Node(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Color getColor() {
		Color c = val ? OFF:ON;
		if(clicked)
			c = c.darker();
		return c;
	}
	
	public void trigger() {
		clicked = !clicked;
	}
	
	public void reset() {
		clicked = false;
	}
	
}
