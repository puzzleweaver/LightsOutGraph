package main;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class GH {

	public static ArrayList<Node> nodes = new ArrayList<>();
	public static ArrayList<ArrayList<Boolean>> conss = new ArrayList<>();

	public static void render(GameContainer gc, Graphics g) {
		Node n;
		for(int i = 0; i < GH.nodes.size(); i++) {
			n = GH.nodes.get(i);
			g.setColor(n.getColor());
			n.fill(g);
			g.setColor(Color.white);
			n.draw(g);
			if(APIMain.numsShown) {
				g.setColor(Color.black);
				g.drawString(""+i, (int) n.x-g.getFont().getWidth(""+i)+Node.radius/2, (int) n.y-g.getFont().getHeight(""+i)+Node.radius/2);
			}
		}
	}
	
	public static void renderDown(GameContainer gc, Graphics g) {
		Connection c;
		g.setColor(Color.white);
		for(int i = 0; i < conss.size(); i++) {
			for(int j = i+1; j < conss.size(); j++) {
				if(conss.get(i).get(j))
					g.drawLine((int) nodes.get(i).x, (int) nodes.get(i).y,
							(int) nodes.get(j).x, (int) nodes.get(j).y);
			}
		}
	}

	public static void addConnection(int a, int b) {
		if(a == b) return;
		conss.get(a).set(b, true);
		conss.get(b).set(a, true);
	}
	public static void removeConnection(int a, int b) {
		if(a == b) return;
		conss.get(a).set(b, false);
		conss.get(b).set(a, false);
	}

	public static void removeVertex(int a) {
		nodes.remove(a);
		conss.remove(a);
		for(int i = 0; i < conss.size(); i++) {
			conss.get(i).remove(a);
		}
	}
	
	public static void addVertex(int x, int y) {
		nodes.add(new Node(x, y));
		conss.add(new ArrayList<Boolean>());
		for(int i = 0; i < conss.size()-1; i++) {
			conss.get(conss.size()-1).add(false);
			conss.get(i).add(false);
		}
		conss.get(conss.size()-1).add(false);
		
	}

	public static void reset() {
		nodes = new ArrayList<>();
		conss = new ArrayList<>();
	}
	
	public static void merge(int a, int b) {
		if(a == b) return;
		ArrayList<Boolean> newl = new ArrayList<>();
		for(int i = 0; i < conss.size(); i++) {
			if(i != a && i != b)
				newl.add(conss.get(a).get(i) || conss.get(b).get(i));
		}
		newl.add(false);
		addVertex((int) (nodes.get(a).x+nodes.get(b).x)/2, (int) (nodes.get(a).y+nodes.get(b).y)/2);
		removeVertex(a);
		if(a < b)
			b--;
		removeVertex(b);
		for(int i = 0; i < conss.size()-1; i++) {
			conss.get(conss.size()-1).set(i, newl.get(i));
			conss.get(i).set(conss.size()-1, newl.get(i));
		}
	}

	public static void propagate() {
		Connection c;
		for(int i = 0; i < nodes.size(); i++) {
			for(int j = 0; j < nodes.size(); j++)
				nodes.get(i).val ^= nodes.get(j).clicked;
		}
	}

}
