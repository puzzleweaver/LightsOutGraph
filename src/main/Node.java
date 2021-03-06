package main;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class Node {
	
	public static final Color ON = Color.red, OFF = new Color(0, 240, 0);
	
	public double x, y;
	public boolean clicked;
	public boolean val;
	public boolean sel;
	
	public static double radius = 15;
	
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
	
	public void fill(Graphics g) {
		g.fillOval((int) (x-radius()), (int) (y-radius()), (int) (2*radius()), (int) (2*radius()));
	}
	public void draw(Graphics g) {
		g.drawOval((int) (x-radius()), (int) (y-radius()), (int) (2*radius()), (int) (2*radius()));
	}
	public static void draw(Graphics g, int x, int y) {
		g.drawOval((int) (x-radius()), (int) (y-radius()), (int) (2*radius()), (int) (2*radius()));
	}
	
	public boolean check(int nx, int ny) {
		return (nx-x)*(nx-x)+(ny-y)*(ny-y) < radius()*radius();
	}
	
	public Node clone() {
		Node n = new Node(x, y);
		n.clicked = clicked;
		return n;
	}
	
	public static int radius() {
		return (int) Math.max(10, radius);
	}
	
}
