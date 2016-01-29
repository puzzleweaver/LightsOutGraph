package main;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class SelectMode extends Mode {
	
	private ArrayList<Integer> selected = new ArrayList<>();
	
	private int eid = -1;
	
	public SelectMode() {
		super("ctrl+a : select all\nf : flood fill select\ne : extend selected vertices", "SELECT");
	}
	
	public void update(Input in) {
		//select and deselect clicked vertices
		boolean mousePressed = in.isMousePressed(0);
		if(mousePressed) {
			int x = in.getMouseX(), y = in.getMouseY();
			Node n;
			for(int i = 0; i < APIMain.nodes.size(); i++) {
				n = APIMain.nodes.get(i);
				if((n.x-x)*(n.x-x)+(n.y-y)*(n.y-y) < APIMain.radius*APIMain.radius) {
					if(in.isKeyDown(Input.KEY_F)) {
						selected.clear();
						floodFill(i);
					}else if(selected.contains(i))
						selected.remove((Integer) i);
					else {
						if(!in.isKeyDown(Input.KEY_LCONTROL))
							selected.clear();
						selected.add(i);
					}
					break;
				}
			}
		}
		//select all or deselect all on CTRL+A
		if(in.isKeyDown(Input.KEY_LCONTROL) && in.isKeyPressed(Input.KEY_A)) {
			boolean b = selected.size() < APIMain.nodes.size();
			selected.clear();
			if(b)
				for(int i = 0; i < APIMain.nodes.size(); i++)
					selected.add(i);
		}
		//extend all selected vertices
		if(in.isKeyDown(Input.KEY_E)) {
			if(mousePressed) {
				APIMain.nodes.add(new Node(in.getMouseX(), in.getMouseY()));
				for(int i = 0; i < selected.size(); i++) {
					APIMain.cons.add(new Connection(APIMain.nodes.size()-1, selected.get(i)));
				}
				selected.clear();
			}
		}
	}
	
	public void render(GameContainer gc, Graphics g) {
		g.setColor(Color.blue.brighter());
		for(int i = 0; i < selected.size(); i++) {
			Node n = APIMain.nodes.get(selected.get(i));
			g.drawOval((int) (n.x-APIMain.radius), (int) (n.y-APIMain.radius), 2*APIMain.radius, 2*APIMain.radius);
		}
		Connection c;
		for(int i = 0; i < APIMain.cons.size(); i++) {
			c = APIMain.cons.get(i);
			if(selected.contains(c.a) && selected.contains(c.b)) {
				Node n1 = APIMain.nodes.get(c.a);
				Node n2 = APIMain.nodes.get(c.b);
				g.drawLine((int) n1.x, (int) n1.y, (int) n2.x, (int) n2.y);
			}
		}
	}
	
	public void floodFill(int n) {
		selected.add(n);
		Connection c;
		for(int i = 0; i < APIMain.cons.size(); i++) {
			c = APIMain.cons.get(i);
			if(c.a == n) {
				if(!selected.contains(c.b))
					floodFill(c.b);
			}else if(c.b == n) {
				if(!selected.contains(c.a))
					floodFill(c.a);
			}
		}
	}
	
}
