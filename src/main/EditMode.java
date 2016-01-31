package main;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class EditMode extends Mode {
	
	private int con = -1;
	
	private int selectX, selectY, lastMouseX, lastMouseY;
	private boolean selecting;
	
	private ArrayList<ArrayList<Boolean>> copyMat = new ArrayList<>();
	private ArrayList<Node> copyNodes = new ArrayList<>();
	private double copyRadius;
	
	public EditMode() {
		super("n : normalize\n" +
				"v : add vertex on click\n" +
				"d : delete selected vertices\n" +
				"m : merge vertices\n" +
				"ctrl + a : select all vertices\n" +
				"f : flood fill select on click\n" +
				"e : extend vertices\n" +
				"x : extrude\n" +
				"k : connect all selected vertices\n" +
				"r : delete selected edges\n" +
				"i : isolate selected vertices\n" +
				"ctrl + c : copy selection\n" +
				"ctrl + v : paste",
				"EDIT");
	}
	
	public void update(Input in) {
		if(in.isKeyPressed(Input.KEY_I))
			isolateVertices();
		if(in.isKeyPressed(Input.KEY_M))
			mergeVertices();
		if(in.isKeyPressed(Input.KEY_D))
			deleteVertices();
		if(in.isKeyPressed(Input.KEY_R))
			deleteEdges();
		if(in.isKeyPressed(Input.KEY_K))
			connectVertices();
		if(in.isKeyDown(Input.KEY_N))
			normalize();
		if(in.isKeyPressed(Input.KEY_C) && in.isKeyDown(Input.KEY_LCONTROL))
			copy();
		if(in.isKeyPressed(Input.KEY_V) && in.isKeyDown(Input.KEY_LCONTROL))
			paste();
		if(in.isKeyDown(Input.KEY_E)) {
			if(in.isMousePressed(0))
				addToChain();
		}else if(in.isKeyDown(Input.KEY_V) && !in.isKeyDown(Input.KEY_LCONTROL)) {
			if(in.isMousePressed(0))
				addVertex();
		}else if(in.isKeyDown(Input.KEY_X)) {
			if(in.isMousePressed(0))
				extrude();
		}else if(in.isMousePressed(0))
			selectVertices(in.isKeyDown(Input.KEY_F), in.isKeyDown(Input.KEY_LCONTROL));
		else if(in.isMouseButtonDown(0))
			moveVertices();
		if(in.isMousePressed(1))
			startConnection();
		else if(con != -1 && !in.isMouseButtonDown(1))
			endConnection();
		if (in.isKeyPressed(Input.KEY_A) && in.isKeyDown(Input.KEY_LCONTROL))
			toggleSelectAll();
		if(selecting && !in.isMouseButtonDown(0))
			selectArea(in.isKeyDown(Input.KEY_LCONTROL));
		lastMouseX = in.getMouseX();
		lastMouseY = in.getMouseY();
	}
	public void render(GameContainer gc, Graphics g) {
		
		g.setColor(Color.yellow);
		if(gc.getInput().isKeyDown(Input.KEY_E)) {
			boolean b = false;
			for(int i = 0; i < GH.nodes.size(); i++) {
				Node n = GH.nodes.get(i);
				if((n.x-APIMain.mouseX)*(n.x-APIMain.mouseX)+(n.y-APIMain.mouseY)*(n.y-APIMain.mouseY) < Node.radius()*Node.radius()) {
					n.draw(g);
					b = true;
					break;
				}
			}
			if(!b)
				Node.draw(g, APIMain.mouseX, APIMain.mouseY);
		}else if(gc.getInput().isKeyDown(Input.KEY_X)) {
			int avgX = 0, avgY = 0;
			ArrayList<Integer> refs = new ArrayList<Integer>();
			for(int i = 0; i < GH.nodes.size(); i++) {
				if(GH.nodes.get(i).sel) {
					refs.add(i);
					avgX += GH.nodes.get(i).x;
					avgY += GH.nodes.get(i).y;
				}
			}
			if(refs.size() > 0) {
				avgX /= refs.size();
				avgY /= refs.size();
				for(int i = 0; i < refs.size(); i++)
					Node.draw(g, (int) (APIMain.mouseX + GH.nodes.get(refs.get(i)).x - avgX), 
							(int) (APIMain.mouseY + GH.nodes.get(refs.get(i)).y - avgY));
			}
		}
		if(gc.getInput().isKeyDown(Keyboard.KEY_V) && !gc.getInput().isKeyDown(Keyboard.KEY_LCONTROL))
			Node.draw(g, APIMain.mouseX, APIMain.mouseY);
		if(con != -1)
			GH.nodes.get(con).draw(g);
	}
	public void renderDown(GameContainer gc, Graphics g) {
		g.setColor(Color.yellow);
		if(selecting) {
			int x = Math.min(selectX, APIMain.mouseX);
			int y = Math.min(selectY, APIMain.mouseY);
			int w = Math.abs(APIMain.mouseX-selectX);
			int h = Math.abs(APIMain.mouseY-selectY);
			g.drawRect(x, y, w, h);
		}
		if(gc.getInput().isKeyDown(Input.KEY_E)) {
			for(int i = 0; i < GH.nodes.size(); i++) {
				if(GH.nodes.get(i).sel) {
					Node n = GH.nodes.get(i);
					g.drawLine((int) n.x, (int) n.y, APIMain.mouseX, APIMain.mouseY);
				}
			}
		}else if(gc.getInput().isKeyDown(Input.KEY_X)) {
			int avgX = 0, avgY = 0;
			ArrayList<Integer> refs = new ArrayList<Integer>();
			for(int i = 0; i < GH.nodes.size(); i++) {
				if(GH.nodes.get(i).sel) {
					refs.add(i);
					avgX += GH.nodes.get(i).x;
					avgY += GH.nodes.get(i).y;
				}
			}
			if(refs.size() > 0) {
				avgX /= refs.size();
				avgY /= refs.size();
				for(int i = 0; i < refs.size(); i++) {
					Node n1 = GH.nodes.get(refs.get(i));
					int x = (int) (APIMain.mouseX + GH.nodes.get(refs.get(i)).x - avgX); 
					int y = (int) (APIMain.mouseY + GH.nodes.get(refs.get(i)).y - avgY);
					g.drawLine((int) n1.x, (int) n1.y, x, y);
				}
				for(int i = 0; i < refs.size(); i++) {
					for(int j = i+1; j < refs.size(); j++) {
						if(GH.cons.get(refs.get(i)).get(refs.get(j))) {
							int x1 = (int) (APIMain.mouseX + GH.nodes.get(refs.get(i)).x - avgX);
							int y1 = (int) (APIMain.mouseY + GH.nodes.get(refs.get(i)).y - avgY);
							int x2 = (int) (APIMain.mouseX + GH.nodes.get(refs.get(j)).x - avgX);
							int y2 = (int) (APIMain.mouseY + GH.nodes.get(refs.get(j)).y - avgY);
							g.drawLine(x1, y1, x2, y2);
						}
					}
				}
			}
		}
		g.setColor(Color.yellow);
		if(con != -1)
			g.drawLine((int) GH.nodes.get(con).x, (int) GH.nodes.get(con).y, APIMain.mouseX, APIMain.mouseY);
	}
	
	public void normalize() {
		// TODO
	}
	
	public void addToChain() {
		int node = -1;
		for(int i = 0; i < GH.nodes.size(); i++) {
			Node n = GH.nodes.get(i);
			if((n.x-APIMain.mouseX)*(n.x-APIMain.mouseX)+(n.y-APIMain.mouseY)*(n.y-APIMain.mouseY) < Node.radius()*Node.radius()) {
				node = i;
				break;
			}
		}
		if(node == -1) {
			GH.addVertex(APIMain.mouseX, APIMain.mouseY);
			node = GH.nodes.size()-1;
		}
		for(int i = 0; i < GH.nodes.size(); i++) {
			if(GH.nodes.get(i).sel)
				GH.addConnection(node, i);
		}
		resetSelections();
		GH.nodes.get(node).sel = true;
	}
	public void connectVertices() {
		for(int i = 0; i < GH.nodes.size(); i++)
			for(int j = i+1; j < GH.nodes.size(); j++)
				if(GH.nodes.get(i).sel && GH.nodes.get(j).sel)
					GH.addConnection(i, j);
	}
	public void extrude() {
		int avgX = 0, avgY = 0;
		ArrayList<Integer> refs = new ArrayList<Integer>();
		for(int i = 0; i < GH.nodes.size(); i++) {
			if(GH.nodes.get(i).sel) {
				refs.add(i);
				avgX += GH.nodes.get(i).x;
				avgY += GH.nodes.get(i).y;
			}
		}
		if(refs.size() > 0) {
			resetSelections();
			avgX /= refs.size();
			avgY /= refs.size();
			for(int i = 0; i < refs.size(); i++) {
				GH.addVertex((int) (APIMain.mouseX + GH.nodes.get(refs.get(i)).x - avgX),
						(int) (APIMain.mouseY + GH.nodes.get(refs.get(i)).y - avgY));
				GH.nodes.get(GH.nodes.size()-1).sel = true;
				GH.addConnection(refs.get(i), GH.nodes.size()-1);
			}
			for(int i = 0; i < refs.size(); i++)
				for(int j = i+1; j < refs.size(); j++)
					if(GH.cons.get(refs.get(i)).get(refs.get(j)))
						GH.addConnection(GH.nodes.size() - refs.size() + i, GH.nodes.size() - refs.size() + j);
		}
	}
	
	public void addVertex() {
		GH.addVertex(APIMain.mouseX, APIMain.mouseY);
	}
	
	public void deleteVertices() {
		for(int i = 0; i < GH.nodes.size(); i++) {
			if(GH.nodes.get(i).sel) {
				GH.removeVertex(i);
				i--;
			}
		}
	}
	public void deleteEdges() {
		for(int i = 0; i < GH.nodes.size(); i++) {
			if(GH.nodes.get(i).sel) {
				for(int j = i+1; j < GH.nodes.size(); j++) {
					if(GH.nodes.get(j).sel)
						GH.removeConnection(i, j);
				}
			}
		}
	}
	
	public void selectVertices(boolean fill, boolean control) {
		for(int i = GH.nodes.size()-1; i >= 0; i--) {
			Node n = GH.nodes.get(i);
			if(n.check(APIMain.mouseX, APIMain.mouseY)) {
				if(!control)
					resetSelections();
				if(fill)
					floodFill(i);
				else
					GH.nodes.get(i).sel = true;
				return;
			}
		}
		selecting = true;
		selectX = APIMain.mouseX;
		selectY = APIMain.mouseY;
	}
	public void moveVertices() {
		if(!selecting) {
			for(int i = 0; i < GH.nodes.size(); i++) {
				if(GH.nodes.get(i).sel) {
					GH.nodes.get(i).x += APIMain.mouseX-lastMouseX;
					GH.nodes.get(i).y += APIMain.mouseY-lastMouseY;
				}
			}
		}
	}
	
	public void selectArea(boolean control) {
		if(!control)
			resetSelections();
		int x1 = Math.min(selectX, APIMain.mouseX);
		int y1 = Math.min(selectY, APIMain.mouseY);
		int x2 = Math.max(selectX, APIMain.mouseX);
		int y2 = Math.max(selectY, APIMain.mouseY);
		Node n;
		for(int i = 0; i < GH.nodes.size(); i++) {
			n = GH.nodes.get(i);
			if(n.x > x1 && n.x < x2 && n.y > y1 && n.y < y2 && !GH.nodes.get(i).sel) {
				GH.nodes.get(i).sel = true;
			}
		}
		selecting = false;
	}
	
	public void startConnection() {
		for(int i = 0; i < GH.nodes.size(); i++) {
			Node n = GH.nodes.get(i);
			if(n.check(APIMain.mouseX, APIMain.mouseY)) {
				con = i;
				break;
			}
		}
	}
	public void endConnection() {
		for(int i = 0; i < GH.nodes.size(); i++) {
			Node n = GH.nodes.get(i);
			if(n.check(APIMain.mouseX, APIMain.mouseY)) {
				if(GH.cons.get(con).get(i))
					GH.removeConnection(i, con);
				else
					GH.addConnection(con, i);
			}
		}
		con = -1;
	}
	
	public void isolateVertices() {
		for(int i = 0; i < GH.nodes.size(); i++) {
			if(GH.nodes.get(i).sel) {
				for(int j = 0; j < GH.nodes.size(); j++) {
					if(!GH.nodes.get(j).sel) {
						GH.removeConnection(i, j);
					}
				}
			}
		}
	}
	
	public void mergeVertices() {
		int X = 0, Y = 0, num = 0;
		int n1 = -1;
		for(int i = 0; i < GH.nodes.size(); i++) {
			if(GH.nodes.get(i).sel) {
				X += GH.nodes.get(i).x;
				Y += GH.nodes.get(i).y;
				num++;
			}
		}
		for(int i = 0; i < GH.nodes.size(); i++) {
			if(GH.nodes.get(i).sel) {
				if(n1 == -1)
					n1 = i;
				else {
					GH.merge(n1, i);
					mergeVertices();
					i = -1;
					n1 = -1;
				}
			}
		}
		if(num > 0) {
			GH.nodes.get(GH.nodes.size()-1).x = X/num;
			GH.nodes.get(GH.nodes.size()-1).y = Y/num;
		}
	}
	
	public void resetSelections() {
		for(int i = 0; i < GH.nodes.size(); i++)
			GH.nodes.get(i).sel = false;
	}
	public void toggleSelectAll() {
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
	public void floodFill(int n) {
		GH.nodes.get(n).sel = true;
		for(int i = 0; i < GH.nodes.size(); i++)
			if(GH.cons.get(n).get(i) && !GH.nodes.get(i).sel)
				floodFill(i);
	}
	
	public void copy() {
		copyNodes.clear();
		ArrayList<Integer> refs = new ArrayList<Integer>();
		copyMat.clear();
		for(int i = 0; i < GH.nodes.size(); i++)
			if(GH.nodes.get(i).sel) {
				copyNodes.add(GH.nodes.get(i).clone());
				refs.add(i);
			}
		for(int i = 0; i < copyNodes.size(); i++) {
			copyMat.add(new ArrayList<Boolean>());
			for(int j = 0; j < copyNodes.size(); j++)
				copyMat.get(i).add(GH.cons.get(refs.get(i)).get(refs.get(j)));
		}
		copyRadius = Node.radius;
	}
	public void paste() {
		resetSelections();
		double avgX = 0, avgY = 0;
		for(int i = 0; i < copyNodes.size(); i++) {
			avgX += copyNodes.get(i).x / copyNodes.size();
			avgY += copyNodes.get(i).y / copyNodes.size();
		}
		for(int i = 0; i < copyNodes.size(); i++) {
			int x = (int) ((copyNodes.get(i).x - avgX)*(Node.radius/copyRadius)+APIMain.mouseX);
			int y = (int) ((copyNodes.get(i).y - avgY)*(Node.radius/copyRadius)+APIMain.mouseY);
			GH.addVertex(x, y);
			GH.nodes.get(GH.nodes.size()-1).sel = true;
		}
		for(int i = 0; i < copyMat.size(); i++) {
			for(int j = i+1; j < copyMat.size(); j++) {
				if(copyMat.get(i).get(j)) {
					GH.addConnection(GH.nodes.size()-copyNodes.size()+i, GH.nodes.size()-copyNodes.size()+j);
				}
			}
		}
	}
	
}
