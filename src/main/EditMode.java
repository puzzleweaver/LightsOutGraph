package main;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class EditMode extends Mode {

	private int eid = -1;
	private int con = -1;
	
	public EditMode() {
		super("n : normalize\n" +
				"v : add vertex on click\n" +
				"d : erase\n" +
				"c : center frame around vertices\n" +
				"1 : create square grid\n",
				"EDIT");
	}

	public void update(Input in) {

		if(in.isKeyDown(Keyboard.KEY_N))
			normalize();
		
		if(in.isKeyPressed(Keyboard.KEY_1))
			generateGrid();
		
		if(!in.isKeyDown(Keyboard.KEY_E))
			eid = -1;
		if(in.isKeyDown(Keyboard.KEY_E) && in.isMousePressed(0))
			addToChain();
		
		else if(in.isKeyDown(Keyboard.KEY_V) && in.isMousePressed(0))
			addVertice();
		else if(in.isKeyDown(Keyboard.KEY_D) && in.isMousePressed(0))
			deleteVertices();
		else if(in.isMouseButtonDown(0))
			selectVertices();
		else
			APIMain.sel = -1;
		
		if(in.isMousePressed(1))
			startConnection();
		else if(con != -1 && !in.isMouseButtonDown(1))
			endConnection();

	}
	public void render(GameContainer gc, Graphics g) {
		if(con != -1) {
			g.setColor(Color.gray);
			g.drawLine((int) APIMain.nodes.get(con).x, (int) APIMain.nodes.get(con).y, gc.getInput().getMouseX(), gc.getInput().getMouseY());
		}
		if(gc.getInput().isKeyDown(Keyboard.KEY_V)) {
			g.setColor(Color.gray);
			g.drawOval(gc.getInput().getMouseX()-APIMain.radius, gc.getInput().getMouseY()-APIMain.radius, 2*APIMain.radius, 2*APIMain.radius);
		}
	}

	public void generateGrid() {

		for(int j = 0; j < 5; j++) {
			for(int i = 0; i < 5; i++) {
				APIMain.nodes.add(new Node(APIMain.mouseX-125+50*i, APIMain.mouseY-125+50*j));
				if(i != 0)
					APIMain.cons.add(new Connection(APIMain.nodes.size()-2, APIMain.nodes.size()-1));
				if(j != 0)
					APIMain.cons.add(new Connection(APIMain.nodes.size()-6, APIMain.nodes.size()-1));
			}
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
			a.x -= 0.0005*(a.x-b.x)*len;
			a.y -= 0.0005*(a.y-b.y)*len;
			b.x += 0.0005*(a.x-b.x)*len;
			b.y += 0.0005*(a.y-b.y)*len;
		}
	}
	
	public void addToChain() {
		if(eid == -1) {
			APIMain.nodes.add(new Node(APIMain.mouseX, APIMain.mouseY));
			eid = APIMain.nodes.size()-1;
		}else {
			APIMain.nodes.add(new Node(APIMain.mouseX, APIMain.mouseY));
			eid = APIMain.nodes.size()-1;
			APIMain.cons.add(new Connection(APIMain.nodes.size()-1, APIMain.nodes.size()-2));
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

}
