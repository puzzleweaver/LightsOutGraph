package main;

import org.newdawn.slick.Color;

public class Node {
	
	public double x, y;
	public boolean val, clicked;
	
	public Node(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Color getColor() {
		Color c = val ? Color.red:Color.green;
		if(clicked)
			c = c.darker().darker();
		return c;
	}
	
	public void trigger() {
		clicked = !clicked;
		val = !val;
	}
	public void effect() {
		val = !val;
	}
	
	public void reset() {
		val = false;
		clicked = false;
	}
	
}
