package main;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class SelectMode extends Mode {

	private ArrayList<Integer> selected = new ArrayList<>();
	
	private int selectX, selectY;
	private boolean selecting;
	
	public SelectMode() {
		super("ctrl+a : select all\n" +
				"f : flood fill select\n" +
				"e : extend selected vertices",
				"SELECT");
	}
	
	public void update(Input in) {
		int mouseX = in.getMouseX(), mouseY = in.getMouseY();
		//select and deselect clicked vertices
		if (in.isMousePressed(0)) {
			if(in.isKeyDown(Input.KEY_E)) {
				int node = -1;
				for(int i = 0; i < APIMain.nodes.size(); i++) {
					Node n = APIMain.nodes.get(i);
					if((n.x-mouseX)*(n.x-mouseX)+(n.y-mouseY)*(n.y-mouseY) < APIMain.radius*APIMain.radius) {
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
				boolean b = true;
				for(int i = 0; i < APIMain.nodes.size(); i++) {
					n = APIMain.nodes.get(i);
					if((n.x-mouseX)*(n.x-mouseX)+(n.y-mouseY)*(n.y-mouseY) < APIMain.radius*APIMain.radius) {
						b = false;
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
				if(b) {
					selecting = true;
					selectX = in.getMouseX();
					selectY = in.getMouseY();
				}
			}
		}
		//select everything in the area
		if(selecting && !in.isMouseButtonDown(0)) {
			if(!in.isKeyDown(Input.KEY_LCONTROL))
				selected.clear();
			int x1 = Math.min(selectX, mouseX);
			int y1 = Math.min(selectY, mouseY);
			int x2 = Math.max(selectX, mouseX);
			int y2 = Math.max(selectY, mouseY);
			Node n;
			for(int i = 0; i < APIMain.nodes.size(); i++) {
				n = APIMain.nodes.get(i);
				if(n.x > x1 && n.x < x2 && n.y > y1 && n.y < y2 && !selected.contains(i)) {
					selected.add(i);
				}
			}
			selecting = false;
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
		if(selecting) {
			int x = Math.min(selectX, mouseX);
			int y = Math.min(selectY, mouseY);
			int w = Math.abs(mouseX-selectX);
			int h = Math.abs(mouseY-selectY);
			g.drawRect(x, y, w, h);
		}
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
