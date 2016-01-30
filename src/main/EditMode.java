package main;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class EditMode extends Mode {

	private int eid = -1, con = -1, merg = -1;

	public EditMode() {
		super("n : normalize\n" +
				"v : add vertex on click\n" +
				"d : erase\n" +
				"c : center frame around vertices\n" +
				"r : reset vertices\n" + 
				"m : merge vertices\n",
				"EDIT");
	}

	public void update(Input in) {
		if(in.isKeyDown(Keyboard.KEY_N))
			normalize();

		if(!in.isKeyDown(Keyboard.KEY_E))
			eid = -1;
		if(in.isKeyDown(Keyboard.KEY_E)) {
			if(in.isMousePressed(0))
				addToChain();
		}else if(in.isKeyDown(Keyboard.KEY_M)) {
			if(in.isMousePressed(0))
				startMerge();
			if(!in.isMouseButtonDown(0) && merg != -1)
				endMerge();
		}else if(in.isKeyDown(Keyboard.KEY_V)) {
			if(in.isMousePressed(0))
				addVertice();
		}
		else if(in.isKeyDown(Keyboard.KEY_D) && in.isMouseButtonDown(0))
			deleteVertices();
		else if(in.isMouseButtonDown(0))
			selectVertices();
		else
			APIMain.sel = -1;

		if(in.isMousePressed(1))
			startConnection();
		else if(con != -1 && !in.isMouseButtonDown(1))
			endConnection();

		if(in.isKeyPressed(Keyboard.KEY_R))
			GH.reset();
	}
	public void render(GameContainer gc, Graphics g) {
		int mouseX = gc.getInput().getMouseX();
		int mouseY = gc.getInput().getMouseY();
		g.setColor(Color.yellow);
		if(gc.getInput().isKeyDown(Keyboard.KEY_V))
			Node.draw(g, mouseX, mouseY);
		if(con != -1)
			GH.nodes.get(con).draw(g);
		if(eid != -1) {
			GH.nodes.get(eid).draw(g);
			boolean drawn = false;
			for(int i = 0; i < GH.nodes.size(); i++) {
				Node n = GH.nodes.get(i);
				if((n.x-mouseX)*(n.x-mouseX)+(n.y-mouseY)*(n.y-mouseY) < Node.radius*Node.radius) {
					n.draw(g);
					drawn = true;
					break;
				}
			}
			if(!drawn)
				Node.draw(g, mouseX, mouseY);
		}
		if(merg != -1) {
			GH.nodes.get(merg).draw(g);
			for(int i = 0; i < GH.nodes.size(); i++) {
				Node n = GH.nodes.get(i);
				if((n.x-mouseX)*(n.x-mouseX)+(n.y-mouseY)*(n.y-mouseY) < Node.radius*Node.radius) {
					n.draw(g);
					Node.draw(g, (int) (n.x+GH.nodes.get(merg).x)/2,
							(int) (n.y+GH.nodes.get(merg).y)/2);
					break;
				}
			}
		}
	}
	public void renderDown(GameContainer gc, Graphics g) {
		int mouseX = gc.getInput().getMouseX();
		int mouseY = gc.getInput().getMouseY();
		g.setColor(Color.yellow);
		if(con != -1)
			g.drawLine((int) GH.nodes.get(con).x, (int) GH.nodes.get(con).y, mouseX, mouseY);
		if(eid != -1)
			g.drawLine((int) GH.nodes.get(eid).x, (int) GH.nodes.get(eid).y, mouseX, mouseY);
		if(merg != -1)
			g.drawLine((int) GH.nodes.get(merg).x, (int) GH.nodes.get(merg).y, mouseX, mouseY);
	}

	public void normalize() {
		//  normalize functionality
		Node a, b;
		for(int i = 0; i < GH.cons.size(); i++) {
			Connection c = GH.cons.get(i);
			a = GH.nodes.get(c.a);
			b = GH.nodes.get(c.b);
			double len = Math.hypot(a.x-b.x, a.y-b.y)-APIMain.defaultLen;
			a.x -= 0.0004*(a.x-b.x)*len;
			a.y -= 0.0004*(a.y-b.y)*len;
			b.x += 0.0004*(a.x-b.x)*len;
			b.y += 0.0004*(a.y-b.y)*len;
		}
	}

	public void addToChain() {
		if(eid == -1) {
			int x = APIMain.mouseX, y = APIMain.mouseY;
			for(int i = 0; i < GH.nodes.size(); i++) {
				Node n = GH.nodes.get(i);
				if((n.x-x)*(n.x-x)+(n.y-y)*(n.y-y) < Node.radius*Node.radius) {
					eid = i;
					return;
				}
			}
			GH.nodes.add(new Node(APIMain.mouseX, APIMain.mouseY));
			eid = GH.nodes.size()-1;
		}else {
			int x = APIMain.mouseX, y = APIMain.mouseY;
			for(int i = 0; i < GH.nodes.size(); i++) {
				Node n = GH.nodes.get(i);
				if((n.x-x)*(n.x-x)+(n.y-y)*(n.y-y) < Node.radius*Node.radius) {
					GH.addConnection(eid, i);
					eid = i;
					return;
				}
			}
			GH.nodes.add(new Node(APIMain.mouseX, APIMain.mouseY));
			GH.addConnection(eid, eid = GH.nodes.size()-1);
		}
	}

	public void addVertice() {
		GH.nodes.add(new Node(APIMain.mouseX, APIMain.mouseY));
	}

	public void deleteVertices() {
		int x = APIMain.mouseX, y = APIMain.mouseY;
		for(int i = 0; i < GH.nodes.size(); i++) {
			Node n = GH.nodes.get(i);
			if((n.x-x)*(n.x-x)+(n.y-y)*(n.y-y) < Node.radius*Node.radius) {
				GH.removeVertex(i);
				break;
			}
		}
	}

	public void selectVertices() {
		if(APIMain.sel == -1) {
			for(int i = 0; i < GH.nodes.size(); i++) {
				Node n = GH.nodes.get(i);
				if((n.x-APIMain.mouseX)*(n.x-APIMain.mouseX)+(n.y-APIMain.mouseY)*(n.y-APIMain.mouseY) < Node.radius*Node.radius) {
					APIMain.sel = i;
					break;
				}
			}
		}
	}

	public void startConnection() {
		for(int i = 0; i < GH.nodes.size(); i++) {
			Node n = GH.nodes.get(i);
			if((n.x-APIMain.mouseX)*(n.x-APIMain.mouseX)+(n.y-APIMain.mouseY)*(n.y-APIMain.mouseY) < Node.radius*Node.radius) {
				con = i;
				break;
			}
		}
	}
	public void endConnection() {
		for(int i = 0; i < GH.nodes.size(); i++) {
			if(i == con) continue;
			Node n = GH.nodes.get(i);
			if((n.x-APIMain.mouseX)*(n.x-APIMain.mouseX)+(n.y-APIMain.mouseY)*(n.y-APIMain.mouseY) < Node.radius*Node.radius) {
				boolean add = true;
				for(int j = 0; j < GH.cons.size(); j++) {
					if(((i == GH.cons.get(j).a) && (con == GH.cons.get(j).b)) || ((i == GH.cons.get(j).b) && (con == GH.cons.get(j).a))) {
						GH.cons.remove(j);
						add = false;
						break;
					}
				}
				if(add) {
					GH.cons.add(new Connection(con, i));
					break;
				}
			}
		}
		con = -1;
	}
	public void startMerge() {
		int x = APIMain.mouseX, y = APIMain.mouseY;
		for(int i = 0; i < GH.nodes.size(); i++) {
			Node n = GH.nodes.get(i);
			if((n.x-x)*(n.x-x)+(n.y-y)*(n.y-y) < Node.radius*Node.radius) {
				merg = i;
				return;
			}
		}
	}
	public void endMerge() {
		Connection c;
		int x = APIMain.mouseX, y = APIMain.mouseY;
		for(int i = 0; i < GH.nodes.size(); i++) {
			Node n = GH.nodes.get(i);
			if((n.x-x)*(n.x-x)+(n.y-y)*(n.y-y) < Node.radius*Node.radius) {
				if(i == merg) {
					merg = -1;
					return;
				}
				ArrayList<Integer> ids = new ArrayList<>();
				for(int j = 0; j < GH.cons.size(); j++) {
					c = GH.cons.get(j);
					if(c.a == i && c.b != merg || c.a == merg && c.b != i) {
						ids.add(c.b);
					}
					if(c.b == i && c.a != merg || c.b == merg && c.a != i) {
						ids.add(c.a);
					}
				}
				GH.nodes.add(new Node((int) (GH.nodes.get(i).x+GH.nodes.get(merg).x)/2,
						(int) (GH.nodes.get(i).y+GH.nodes.get(merg).y)/2));
				for(int j = 0; j < ids.size(); j++) {
					GH.addConnection(GH.nodes.size()-1, ids.get(j));
				}
				GH.removeVertex(i);
				if(i < merg)
					merg--;
				GH.removeVertex(merg);
			}
		}
		merg = -1;
	}

}
