package main;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class GH {

	public static ArrayList<Node> nodes = new ArrayList<>();
	public static ArrayList<Connection> cons = new ArrayList<>();

	public static void render(GameContainer gc, Graphics g) {
		Node n;
		for(int i = 0; i < GH.nodes.size(); i++) {
			n = GH.nodes.get(i);
			g.setColor(n.getColor());
			g.fillOval((int) n.x-Node.radius, (int) n.y-Node.radius, 2*Node.radius, 2*Node.radius);
			g.setColor(Color.white);
			g.drawOval((int) n.x-Node.radius, (int) n.y-Node.radius, 2*Node.radius, 2*Node.radius);
			if(APIMain.numsShown) {
				g.setColor(Color.black);
				g.drawString(""+i, (int) n.x-g.getFont().getWidth(""+i)+Node.radius/2, (int) n.y-g.getFont().getHeight(""+i)+Node.radius/2);
			}
		}
	}
	
	public static void renderDown(GameContainer gc, Graphics g) {
		Connection c;
		g.setColor(Color.white);
		for(int i = 0; i < cons.size(); i++) {
			c = cons.get(i);
			g.drawLine((int) nodes.get(c.a).x, (int) nodes.get(c.a).y, (int) nodes.get(c.b).x, (int) nodes.get(c.b).y);
		}
	}

	public static void addConnection(int a, int b) {
		if(a == b) return;
		Connection c;
		for(int i = 0; i < cons.size(); i++) {
			c = cons.get(i);
			if((c.a == a && c.b == b) || (c.a == b && c.b == a)) {
				cons.remove(i);
				break;
			}
		}
		cons.add(new Connection(a, b));
	}

	public static void removeVertex(int i) {
		nodes.remove(i);
		for(int j = 0; j < cons.size(); j++) {
			if(cons.get(j).a == i || cons.get(j).b == i) {
				cons.remove(j);
				j = j-1;
			}else {
				if(cons.get(j).a > i)
					cons.get(j).a--;
				if(cons.get(j).b > i)
					cons.get(j).b--;
			}
		}
	}

	public static void reset() {
		for(int i = nodes.size(); i > 0; i--) {
			nodes.remove(0);
		}
		for(int i = cons.size(); i > 0; i--) {
			cons.remove(0);
		}
	}

}
