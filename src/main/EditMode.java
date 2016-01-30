package main;

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
				if(n.check(mouseX, mouseY)) {
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
				if(n.check(mouseX, mouseY)) {
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
		// TODO
	}

	public void addToChain() {
		if(eid == -1) {
			int x = APIMain.mouseX, y = APIMain.mouseY;
			for(int i = 0; i < GH.nodes.size(); i++) {
				Node n = GH.nodes.get(i);
				if(n.check(x, y)) {
					eid = i;
					return;
				}
			}
			GH.addVertex(x, y);
			eid = GH.nodes.size()-1;
		}else {
			int x = APIMain.mouseX, y = APIMain.mouseY;
			for(int i = 0; i < GH.nodes.size(); i++) {
				Node n = GH.nodes.get(i);
				if(n.check(x, y)) {
					GH.addConnection(eid, i);
					eid = i;
					return;
				}
			}
			GH.addVertex(APIMain.mouseX, APIMain.mouseY);
			GH.addConnection(eid, GH.nodes.size()-1);
			eid = GH.nodes.size()-1;
		}
	}

	public void addVertice() {
		GH.addVertex(APIMain.mouseX, APIMain.mouseY);
	}

	public void deleteVertices() {
		int x = APIMain.mouseX, y = APIMain.mouseY;
		for(int i = 0; i < GH.nodes.size(); i++) {
			Node n = GH.nodes.get(i);
			if(n.check(x, y)) {
				GH.removeVertex(i);
				break;
			}
		}
	}

	public void selectVertices() {
		if(APIMain.sel == -1) {
			for(int i = 0; i < GH.nodes.size(); i++) {
				Node n = GH.nodes.get(i);
				if(n.check(APIMain.mouseX, APIMain.mouseY)) {
					APIMain.sel = i;
					break;
				}
			}
		}
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
				if(GH.conss.get(con).get(i))
					GH.removeConnection(i, con);
				else
					GH.addConnection(con, i);
			}
		}
		con = -1;
	}
	public void startMerge() {
		int x = APIMain.mouseX, y = APIMain.mouseY;
		for(int i = 0; i < GH.nodes.size(); i++) {
			Node n = GH.nodes.get(i);
			if(n.check(x, y)) {
				merg = i;
				return;
			}
		}
	}
	public void endMerge() {
		int x = APIMain.mouseX, y = APIMain.mouseY;
		for(int i = 0; i < GH.nodes.size(); i++) {
			Node n = GH.nodes.get(i);
			if(n.check(x, y)) {
				GH.merge(i, merg);
			}
		}
		merg = -1;
	}

}
