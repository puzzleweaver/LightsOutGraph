package main;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class GH {

	public static ArrayList<Node> nodes = new ArrayList<>();
	public static ArrayList<ArrayList<Boolean>> cons = new ArrayList<>();
	
	public static void render(GameContainer gc, Graphics g) {
		Node n;
		for(int i = 0; i < GH.nodes.size(); i++) {
			n = GH.nodes.get(i);
			g.setColor(n.getColor());
			n.fill(g);
			g.setColor(n.sel ? Color.blue : Color.white);
			n.draw(g);
			if(APIMain.numsShown) {
				g.setColor(Color.black);
				g.drawString(""+i, (int) (n.x-g.getFont().getWidth(""+i)/2), (int) (n.y-g.getFont().getHeight(""+i)/2));
			}
		}
	}
	
	public static void renderDown(GameContainer gc, Graphics g) {
		for(int i = 0; i < cons.size(); i++) {
			for(int j = i+1; j < cons.size(); j++) {
				if(cons.get(i).get(j)) {
					g.setColor(nodes.get(i).sel && nodes.get(j).sel ? Color.blue : Color.white);
					g.drawLine((int) nodes.get(i).x, (int) nodes.get(i).y,
							(int) nodes.get(j).x, (int) nodes.get(j).y);
				}
			}
		}
	}

	public static void addConnection(int a, int b) {
		if(a == b) return;
		cons.get(a).set(b, true);
		cons.get(b).set(a, true);
	}
	public static void removeConnection(int a, int b) {
		if(a == b) return;
		cons.get(a).set(b, false);
		cons.get(b).set(a, false);
	}

	public static void removeVertex(int a) {
		nodes.remove(a);
		cons.remove(a);
		for(int i = 0; i < cons.size(); i++) {
			cons.get(i).remove(a);
		}
	}
	
	public static void addVertex(double x, double y) {
		nodes.add(new Node(x, y));
		cons.add(new ArrayList<Boolean>());
		for(int i = 0; i < cons.size()-1; i++) {
			cons.get(cons.size()-1).add(false);
			cons.get(i).add(false);
		}
		cons.get(cons.size()-1).add(false);
		
	}

	public static void reset() {
		nodes = new ArrayList<>();
		cons = new ArrayList<>();
	}
	
	public static void merge(int a, int b) {
		if(a == b) return;
		ArrayList<Boolean> newl = new ArrayList<>();
		for(int i = 0; i < cons.size(); i++) {
			if(i != a && i != b)
				newl.add(cons.get(a).get(i) || cons.get(b).get(i));
		}
		newl.add(false);
		addVertex((int) (nodes.get(a).x+nodes.get(b).x)/2, (int) (nodes.get(a).y+nodes.get(b).y)/2);
		nodes.get(nodes.size()-1).sel = true;
		removeVertex(a);
		if(a < b)
			b--;
		removeVertex(b);
		for(int i = 0; i < cons.size()-1; i++) {
			cons.get(cons.size()-1).set(i, newl.get(i));
			cons.get(i).set(cons.size()-1, newl.get(i));
		}
	}

	public static void propagate() {
		for(int i = 0; i < nodes.size(); i++) {
			nodes.get(i).val = nodes.get(i).clicked;
			for(int j = 0; j < nodes.size(); j++)
				nodes.get(i).val ^= nodes.get(j).clicked && cons.get(i).get(j);
		}
	}
	
	public static void load(String in) throws Exception {
		StringTokenizer st = new StringTokenizer(in);
		String tok = st.nextToken();
		ArrayList<Node> tNodes = new ArrayList<>();
		ArrayList<ArrayList<Boolean>> tCons = new ArrayList<>();
		while(!tok.equals(".")) {
			tNodes.add(new Node(Double.parseDouble(tok), Double.parseDouble(st.nextToken())));
			tNodes.get(tNodes.size()-1).clicked = st.nextToken().equals("0");
			tok = st.nextToken();
		}
		for(int i = 0; i < tNodes.size(); i++) {
			tCons.add(new ArrayList<Boolean>());
			tok = st.nextToken();
			for(int j = 0; j < tNodes.size(); j++) {
				tCons.get(i).add(tok.charAt(j) == '1');
			}
		}
		cons = tCons;
		nodes = tNodes;
	}

}
