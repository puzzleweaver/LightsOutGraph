package main;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class SelectMode extends Mode {

	private ArrayList<Integer> selected = new ArrayList<>();

	public SelectMode() {
		super("ctrl+a : select all\n" +
				"f : flood fill select\n" +
				"e : extend selected vertices",
				"SELECT");
	}

	public void update(Input in) {
		//select and deselect clicked vertices
		boolean mousePressed = in.isMousePressed(0);
		if (mousePressed) {
			int x = in.getMouseX(), y = in.getMouseY();
			if(in.isKeyDown(Input.KEY_E)) {
				int node = -1;
				for(int i = 0; i < APIMain.nodes.size(); i++) {
					Node n = APIMain.nodes.get(i);
					if((n.x-x)*(n.x-x)+(n.y-y)*(n.y-y) < APIMain.radius*APIMain.radius) {
						node = i;
						break;
					}
				}
				if(node == -1) {
					APIMain.nodes.add(new Node(in.getMouseX(), in.getMouseY()));
					node = APIMain.nodes.size()-1;
				}
				for(int i = 0; i < selected.size(); i++) {
					if(node != selected.get(i)) {
						APIMain.cons.add(new Connection(node, selected.get(i)));
					}
				}
				selected.clear();
			}else {
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
		}
		// select all or deselect all on CTRL+A
		if (in.isKeyDown(Input.KEY_LCONTROL) && in.isKeyPressed(Input.KEY_A)) {
			boolean b = selected.size() < APIMain.nodes.size();
			selected.clear();
			if (b)
				for (int i = 0; i < APIMain.nodes.size(); i++)
					selected.add(i);
		}
	}

	public void render(GameContainer gc, Graphics g) {
		int mouseX = gc.getInput().getMouseX();
		int mouseY = gc.getInput().getMouseY();
		g.setColor(Color.blue.brighter());
		for (int i = 0; i < selected.size(); i++) {
			Node n = APIMain.nodes.get(selected.get(i));
			g.drawOval((int) (n.x - APIMain.radius), (int) (n.y - APIMain.radius), 2 * APIMain.radius,
					2 * APIMain.radius);
		}
		g.setColor(Color.yellow);
		
		if(gc.getInput().isKeyDown(Input.KEY_E)) {
			boolean b = false;
			for(int i = 0; i < APIMain.nodes.size(); i++) {
				Node n = APIMain.nodes.get(i);
				if((n.x-mouseX)*(n.x-mouseX)+(n.y-mouseY)*(n.y-mouseY) < APIMain.radius*APIMain.radius) {
					g.drawOval((int) (n.x-APIMain.radius), (int) (n.y-APIMain.radius), 2*APIMain.radius, 2*APIMain.radius);
					b = true;
					break;
				}
			}
			if(!b)
				g.drawOval(mouseX-APIMain.radius, mouseY-APIMain.radius, 2*APIMain.radius, 2*APIMain.radius);
		}
	}
	
	public void renderDown(GameContainer gc, Graphics g) {
		int mouseX = gc.getInput().getMouseX();
		int mouseY = gc.getInput().getMouseY();
		g.setColor(Color.blue.brighter());
		Connection c;
		for (int i = 0; i < APIMain.cons.size(); i++) {
			c = APIMain.cons.get(i);
			if (selected.contains(c.a) && selected.contains(c.b)) {
				Node n1 = APIMain.nodes.get(c.a);
				Node n2 = APIMain.nodes.get(c.b);
				g.drawLine((int) n1.x, (int) n1.y, (int) n2.x, (int) n2.y);
			}
		}
		g.setColor(Color.yellow);
		if(gc.getInput().isKeyDown(Input.KEY_E)) {
			for(int i = 0; i < selected.size(); i++) {
				Node n = APIMain.nodes.get(selected.get(i));
				g.drawLine((int) n.x, (int) n.y, mouseX, mouseY);
			}
		}
	}

	public void floodFill(int n) {
		selected.add(n);
		Connection c;
		for (int i = 0; i < APIMain.cons.size(); i++) {
			c = APIMain.cons.get(i);
			if (c.a == n) {
				if (!selected.contains(c.b))
					floodFill(c.b);
			} else if (c.b == n) {
				if (!selected.contains(c.a))
					floodFill(c.a);
			}
		}
	}

}
