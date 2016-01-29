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
		if(!in.isKeyDown(Keyboard.KEY_M))
			merg = -1;
		if(in.isKeyDown(Keyboard.KEY_E)) {
			if(in.isMousePressed(0))
				addToChain();
		}
		else if(in.isKeyDown(Keyboard.KEY_M)) {
			if(in.isMousePressed(0) || (!in.isMouseButtonDown(0) && merg != -1))
				merge();
		}
		else if(in.isKeyDown(Keyboard.KEY_V)) {
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
			reset();

	}
	public void render(GameContainer gc, Graphics g) {
		int mouseX = gc.getInput().getMouseX();
		int mouseY = gc.getInput().getMouseY();
		g.setColor(Color.yellow);
		if(gc.getInput().isKeyDown(Keyboard.KEY_V)) {
			g.drawOval(mouseX-APIMain.radius, mouseY-APIMain.radius, 2*APIMain.radius, 2*APIMain.radius);
		}
		if(con != -1) {
			g.drawOval((int) (APIMain.nodes.get(con).x-APIMain.radius), (int) (APIMain.nodes.get(con).y-APIMain.radius), 2*APIMain.radius, 2*APIMain.radius);
		}
		if(eid != -1) {
			g.drawOval((int) (APIMain.nodes.get(eid).x-APIMain.radius), (int) (APIMain.nodes.get(eid).y-APIMain.radius), 2*APIMain.radius, 2*APIMain.radius);
			boolean drawn = false;
			for(int i = 0; i < APIMain.nodes.size(); i++) {
				Node n = APIMain.nodes.get(i);
				if((n.x-mouseX)*(n.x-mouseX)+(n.y-mouseY)*(n.y-mouseY) < APIMain.radius*APIMain.radius) {
					g.drawOval((int) (n.x-APIMain.radius), (int) (n.y-APIMain.radius), 2*APIMain.radius, 2*APIMain.radius);
					drawn = true;
					break;
				}
			}
			if(!drawn)
				g.drawOval((int) (mouseX-APIMain.radius), (int) (mouseY-APIMain.radius), 2*APIMain.radius, 2*APIMain.radius);
		}
		if(merg != -1) {
			g.drawOval((int) (APIMain.nodes.get(merg).x-APIMain.radius), (int) (APIMain.nodes.get(merg).y-APIMain.radius), 2*APIMain.radius, 2*APIMain.radius);
			for(int i = 0; i < APIMain.nodes.size(); i++) {
				Node n = APIMain.nodes.get(i);
				if((n.x-mouseX)*(n.x-mouseX)+(n.y-mouseY)*(n.y-mouseY) < APIMain.radius*APIMain.radius) {
					g.drawOval((int) (n.x-APIMain.radius), (int) (n.y-APIMain.radius), 2*APIMain.radius, 2*APIMain.radius);
					g.drawOval((int) ((n.x+APIMain.nodes.get(merg).x)/2-APIMain.radius), (int) ((n.y+APIMain.nodes.get(merg).y)/2-APIMain.radius), 2*APIMain.radius, 2*APIMain.radius);
					break;
				}
			}
		}
	}
	public void renderDown(GameContainer gc, Graphics g) {
		int mouseX = gc.getInput().getMouseX();
		int mouseY = gc.getInput().getMouseY();
		g.setColor(Color.yellow);
		if(con != -1) {
			g.drawLine((int) APIMain.nodes.get(con).x, (int) APIMain.nodes.get(con).y, mouseX, mouseY);
		}
		if(eid != -1) {
			g.drawLine((int) APIMain.nodes.get(eid).x, (int) APIMain.nodes.get(eid).y, mouseX, mouseY);
		}
		if(merg != -1) {
			g.drawLine((int) APIMain.nodes.get(merg).x, (int) APIMain.nodes.get(merg).y, mouseX, mouseY);
		}
	}
	
	public void normalize() {
		//  normalize functionality
		Node a, b;
		for(int i = 0; i < APIMain.cons.size(); i++) {
			Connection c = APIMain.cons.get(i);
			a = APIMain.nodes.get(c.a);
			b = APIMain.nodes.get(c.b);
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
			for(int i = 0; i < APIMain.nodes.size(); i++) {
				Node n = APIMain.nodes.get(i);
				if((n.x-x)*(n.x-x)+(n.y-y)*(n.y-y) < APIMain.radius*APIMain.radius) {
					eid = i;
					return;
				}
			}
			APIMain.nodes.add(new Node(APIMain.mouseX, APIMain.mouseY));
			eid = APIMain.nodes.size()-1;
		}else {
			int x = APIMain.mouseX, y = APIMain.mouseY;
			for(int i = 0; i < APIMain.nodes.size(); i++) {
				Node n = APIMain.nodes.get(i);
				if((n.x-x)*(n.x-x)+(n.y-y)*(n.y-y) < APIMain.radius*APIMain.radius) {
					if(i == eid) return;
					for(int j = 0; j < APIMain.cons.size(); j++) {
						if(APIMain.cons.get(j).a == i && APIMain.cons.get(j).b == eid ||
								APIMain.cons.get(j).b == i && APIMain.cons.get(j).a == eid) {
							eid = i;
							return;
						}
					}
					APIMain.cons.add(new Connection(eid, i));
					eid = i;
					return;
				}
			}
			APIMain.nodes.add(new Node(APIMain.mouseX, APIMain.mouseY));
			APIMain.cons.add(new Connection(eid, APIMain.nodes.size()-1));
			eid = APIMain.nodes.size()-1;
		}
	}
	
	public void addVertice() {
		APIMain.nodes.add(new Node(APIMain.mouseX, APIMain.mouseY));
	}
	
	public void deleteVertices() {
		int x = APIMain.mouseX, y = APIMain.mouseY;
		for(int i = 0; i < APIMain.nodes.size(); i++) {
			Node n = APIMain.nodes.get(i);
			if((n.x-x)*(n.x-x)+(n.y-y)*(n.y-y) < APIMain.radius*APIMain.radius) {
				removeVertice(i);
				break;
			}
		}
	}
	
	public void selectVertices() {
		if(APIMain.sel == -1) {
			for(int i = 0; i < APIMain.nodes.size(); i++) {
				Node n = APIMain.nodes.get(i);
				if((n.x-APIMain.mouseX)*(n.x-APIMain.mouseX)+(n.y-APIMain.mouseY)*(n.y-APIMain.mouseY) < APIMain.radius*APIMain.radius) {
					APIMain.sel = i;
					break;
				}
			}
		}
	}
	
	public void startConnection() {
		for(int i = 0; i < APIMain.nodes.size(); i++) {
			Node n = APIMain.nodes.get(i);
			if((n.x-APIMain.mouseX)*(n.x-APIMain.mouseX)+(n.y-APIMain.mouseY)*(n.y-APIMain.mouseY) < APIMain.radius*APIMain.radius) {
				con = i;
				break;
			}
		}
	}
	public void endConnection() {
		for(int i = 0; i < APIMain.nodes.size(); i++) {
			if(i == con) continue;
			Node n = APIMain.nodes.get(i);
			if((n.x-APIMain.mouseX)*(n.x-APIMain.mouseX)+(n.y-APIMain.mouseY)*(n.y-APIMain.mouseY) < APIMain.radius*APIMain.radius) {
				boolean add = true;
				for(int j = 0; j < APIMain.cons.size(); j++) {
					if(((i == APIMain.cons.get(j).a) && (con == APIMain.cons.get(j).b)) || ((i == APIMain.cons.get(j).b) && (con == APIMain.cons.get(j).a))) {
						APIMain.cons.remove(j);
						add = false;
						break;
					}
				}
				if(add) {
					APIMain.cons.add(new Connection(con, i));
					break;
				}
			}
		}
		con = -1;
	}
	public void merge() {
		if(merg == -1) {
			int x = APIMain.mouseX, y = APIMain.mouseY;
			for(int i = 0; i < APIMain.nodes.size(); i++) {
				Node n = APIMain.nodes.get(i);
				if((n.x-x)*(n.x-x)+(n.y-y)*(n.y-y) < APIMain.radius*APIMain.radius) {
					merg = i;
					return;
				}
			}
		}else {
			Connection c;
			int x = APIMain.mouseX, y = APIMain.mouseY;
			for(int i = 0; i < APIMain.nodes.size(); i++) {
				Node n = APIMain.nodes.get(i);
				if((n.x-x)*(n.x-x)+(n.y-y)*(n.y-y) < APIMain.radius*APIMain.radius) {
					if(i == merg) {
						merg = -1;
						return;
					}
					ArrayList<Integer> ids = new ArrayList<>();
					boolean[] list = new boolean[APIMain.nodes.size()];
					for(int j = 0; j < APIMain.cons.size(); j++) {
						c = APIMain.cons.get(j);
						if(list[c.a] || list[c.b]) continue;
						if(c.a == i && c.b != merg || c.a == merg && c.b != i) {
							ids.add(c.b);
							list[c.b] = true;
						}
						if(c.b == i && c.a != merg || c.b == merg && c.a != i) {
							ids.add(c.a);
							list[c.a] = true;
						}
					}
					APIMain.nodes.add(new Node((int) (APIMain.nodes.get(i).x+APIMain.nodes.get(merg).x)/2,
							(int) (APIMain.nodes.get(i).y+APIMain.nodes.get(merg).y)/2));
					for(int j = 0; j < ids.size(); j++) {
						APIMain.cons.add(new Connection(APIMain.nodes.size()-1, ids.get(j)));
					}
					removeVertice(i);
					if(i < merg)
						merg--;
					removeVertice(merg);
					merg = -1;
					return;
				}
			}
		}
	}
	public void reset() {
		for(int i = APIMain.nodes.size(); i > 0; i--) {
			APIMain.nodes.remove(0);
		}
		for(int i = APIMain.cons.size(); i > 0; i--) {
			APIMain.cons.remove(0);
		}
	}
	
	public void removeVertice(int i) {
		APIMain.nodes.remove(i);
		for(int j = 0; j < APIMain.cons.size(); j++) {
			if(APIMain.cons.get(j).a == i || APIMain.cons.get(j).b == i) {
				APIMain.cons.remove(j);
				j = j-1;
			}else {
				if(APIMain.cons.get(j).a > i)
					APIMain.cons.get(j).a--;
				if(APIMain.cons.get(j).b > i)
					APIMain.cons.get(j).b--;
			}
		}
	}
	
}
