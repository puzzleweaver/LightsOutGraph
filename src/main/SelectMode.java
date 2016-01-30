package main;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class SelectMode extends Mode {
	
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
				for(int i = 0; i < GH.nodes.size(); i++) {
					Node n = GH.nodes.get(i);
					if((n.x-mouseX)*(n.x-mouseX)+(n.y-mouseY)*(n.y-mouseY) < Node.radius*Node.radius) {
						node = i;
						break;
					}
				}
				if(node == -1) {
					GH.addVertex(mouseX, mouseY);
					node = GH.nodes.size()-1;
				}
				for(int i = 0; i < GH.nodes.size(); i++) {
					if(GH.nodes.get(i).sel)
						GH.addConnection(node, i);
				}
				resetSelections();
			}else {
				Node n;
				boolean b = true;
				for(int i = 0; i < GH.nodes.size(); i++) {
					n = GH.nodes.get(i);
					if((n.x-mouseX)*(n.x-mouseX)+(n.y-mouseY)*(n.y-mouseY) < Node.radius*Node.radius) {
						b = false;
						if(in.isKeyDown(Input.KEY_F)) {
							if(!in.isKeyDown(Input.KEY_LCONTROL))
								resetSelections();
							floodFill(i);
						}else if(GH.nodes.get(i).sel)
							GH.nodes.get(i).sel = false;
						else {
							if(!in.isKeyDown(Input.KEY_LCONTROL))
								resetSelections();
							GH.nodes.get(i).sel = true;
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
				resetSelections();
			int x1 = Math.min(selectX, mouseX);
			int y1 = Math.min(selectY, mouseY);
			int x2 = Math.max(selectX, mouseX);
			int y2 = Math.max(selectY, mouseY);
			Node n;
			for(int i = 0; i < GH.nodes.size(); i++) {
				n = GH.nodes.get(i);
				if(n.x > x1 && n.x < x2 && n.y > y1 && n.y < y2 && !GH.nodes.get(i).sel) {
					GH.nodes.get(i).sel = true;
				}
			}
			selecting = false;
		}
		// select all or deselect all on CTRL+A
		if (in.isKeyPressed(Input.KEY_A) && in.isKeyDown(Input.KEY_LCONTROL)) {
			boolean b = false;
			for(int i = 0; i < GH.nodes.size(); i++) {
				if(!GH.nodes.get(i).sel) {
					b = true;
					break;
				}
			}
			if (b)
				for (int i = 0; i < GH.nodes.size(); i++)
					GH.nodes.get(i).sel = true;
			else
				resetSelections();
		}
	}
	
	public void render(GameContainer gc, Graphics g) {
		int mouseX = gc.getInput().getMouseX();
		int mouseY = gc.getInput().getMouseY();
		
		g.setColor(Color.blue.brighter());
		for (int i = 0; i < GH.nodes.size(); i++)
			if(GH.nodes.get(i).sel)
				GH.nodes.get(i).draw(g);
		
		g.setColor(Color.yellow);
		if(gc.getInput().isKeyDown(Input.KEY_E)) {
			boolean b = false;
			for(int i = 0; i < GH.nodes.size(); i++) {
				Node n = GH.nodes.get(i);
				if((n.x-mouseX)*(n.x-mouseX)+(n.y-mouseY)*(n.y-mouseY) < Node.radius*Node.radius) {
					n.draw(g);
					b = true;
					break;
				}
			}
			if(!b)
				Node.draw(g, mouseX, mouseY);
		}
	}
	
	public void renderDown(GameContainer gc, Graphics g) {
		int mouseX = gc.getInput().getMouseX();
		int mouseY = gc.getInput().getMouseY();
		g.setColor(Color.blue.brighter());
		for (int i = 0; i < GH.nodes.size(); i++)
			if(GH.nodes.get(i).sel)
				for(int j = 0; j < GH.nodes.size(); j++)
					if(GH.nodes.get(j).sel && GH.cons.get(i).get(j)) {
						Node n1 = GH.nodes.get(i);
						Node n2 = GH.nodes.get(j);
						g.drawLine((int) n1.x, (int) n1.y, (int) n2.x, (int) n2.y);
					}
		g.setColor(Color.yellow);
		if(selecting) {
			int x = Math.min(selectX, mouseX);
			int y = Math.min(selectY, mouseY);
			int w = Math.abs(mouseX-selectX);
			int h = Math.abs(mouseY-selectY);
			g.drawRect(x, y, w, h);
		}
		if(gc.getInput().isKeyDown(Input.KEY_E))
			for(int i = 0; i < GH.nodes.size(); i++)
				if(GH.nodes.get(i).sel) {
					Node n = GH.nodes.get(i);
					g.drawLine((int) n.x, (int) n.y, mouseX, mouseY);
				}
	}
	
	public void floodFill(int n) {
		GH.nodes.get(n).sel = true;
		for(int i = 0; i < GH.nodes.size(); i++)
			if(GH.cons.get(n).get(i) && !GH.nodes.get(i).sel)
				floodFill(i);
	}
	
	public void resetSelections() {
		for(int i = 0; i < GH.nodes.size(); i++)
			GH.nodes.get(i).sel = false;
	}
	
}
