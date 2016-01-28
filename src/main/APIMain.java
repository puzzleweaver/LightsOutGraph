package main;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

public class APIMain extends BasicGame {

	//	public static int w = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
	//			h = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	public static int w = 600, h = 600;
	public static ArrayList<Node> nodes = new ArrayList<>();
	public static ArrayList<Connection> cons = new ArrayList<>();
	public static Random r = new Random();

	public static boolean tutActive = false;
	public static String help = "h : help",
			tut = "h : help\n" + 
					"n : normalize\n" +
					"v : add vertex on click\n" +
					"d : remove vertex on click\n" +
					"esc : exit\n" +
					"  <more coming soon...>";

	public static int radius = 10, defaultLen = 100, sel = -1, con = -1;

	public static void main(String[] args) {
		try {
			AppGameContainer app = new AppGameContainer(new APIMain());
			app.setDisplayMode(w, h, false);
			app.setMinimumLogicUpdateInterval(15);
			app.start();
		}catch(SlickException e) {
			System.out.println(e);
		}
	}

	public APIMain(){
		super("YOU'RE A BLIZZARD HARRY");
	}

	public void init(GameContainer arg0) throws SlickException {
		for(int i = 0; i < 10; i++) {
			nodes.add(new Node(r.nextInt(w), r.nextInt(h)));
		}
	}

	public void update(GameContainer gc, int arg1) throws SlickException {
		Input in = gc.getInput();
		
		// keyboard poop
		//  toggle help menu activation
		if(in.isKeyPressed(Keyboard.KEY_H)) {
			tutActive = !tutActive;
		}
		//  normalize functionality
		if(in.isKeyDown(Keyboard.KEY_N)) {
			for(int i = 0; i < cons.size(); i++) {
				Connection c = cons.get(i);
				Node a = nodes.get(c.a), b = nodes.get(c.b);
				double len = Math.hypot(a.x-b.x, a.y-b.y)-defaultLen;
				a.x -= 0.0005*(a.x-b.x)*len;
				a.y -= 0.0005*(a.y-b.y)*len;
				b.x += 0.0005*(a.x-b.x)*len;
				b.y += 0.0005*(a.y-b.y)*len;
			}
		}
		//  exit functionality
		if(in.isKeyDown(Keyboard.KEY_ESCAPE)) {
			System.exit(0);
		}
		//  add vertices
		if(in.isKeyDown(Keyboard.KEY_V) && in.isMousePressed(0)) {
			nodes.add(new Node(in.getMouseX(), in.getMouseY()));
		}
		//  delete vertices
		if(in.isKeyDown(Keyboard.KEY_D) && in.isMouseButtonDown(0)) {
			int x = in.getMouseX(), y = in.getMouseY();
			for(int i = 0; i < nodes.size(); i++) {
				Node n = nodes.get(i);
				if((n.x-x)*(n.x-x)+(n.y-y)*(n.y-y) < radius*radius) {
					nodes.remove(i);
					for(int j = 0; j < cons.size(); j++) {
						if(cons.get(j).a == i || cons.get(j).b == i) {
							cons.remove(j);
							j = j-1;
						}
					}
					break;
				}
			}
		}

		// mouse poop
		int x = in.getMouseX(), y = in.getMouseY();
		if(in.isMouseButtonDown(0)) {
			if(sel == -1) {
				for(int i = 0; i < nodes.size(); i++) {
					Node n = nodes.get(i);
					if((n.x-x)*(n.x-x)+(n.y-y)*(n.y-y) < radius*radius) {
						sel = i;
						break;
					}
				}
			}
		}else {
			sel = -1;
		}
		if(con == -1 && in.isMouseButtonDown(1)) {
			for(int i = 0; i < nodes.size(); i++) {
				Node n = nodes.get(i);
				if((n.x-x)*(n.x-x)+(n.y-y)*(n.y-y) < radius*radius) {
					con = i;
					break;
				}
			}
		}else if(con != -1 && !in.isMouseButtonDown(1)) {
			for(int i = 0; i < nodes.size(); i++) {
				if(i == con) continue;
				Node n = nodes.get(i);
				if((n.x-x)*(n.x-x)+(n.y-y)*(n.y-y) < radius*radius) {
					boolean add = true;
					for(int j = 0; j < cons.size(); j++) {
						if(((i == cons.get(j).a) && (con == cons.get(j).b)) || ((i == cons.get(j).b) && (con == cons.get(j).a))) {
							cons.remove(j);
							add = false;
							break;
						}
					}
					if(add) {
						cons.add(new Connection(con, i));
						break;
					}
				}
			}
			con = -1;
		}
		if(sel != -1){
			nodes.get(sel).x = in.getMouseX();
			nodes.get(sel).y = in.getMouseY();
		}
	}

	public void render(GameContainer gc, Graphics g) throws SlickException {
		Node n;
		Connection c;
		if(gc.getInput().isKeyDown(Keyboard.KEY_V)) {
			g.setColor(Color.darkGray.brighter());
			g.drawOval(gc.getInput().getMouseX()-radius, gc.getInput().getMouseY()-radius, 2*radius, 2*radius);
		}
		if(sel != -1) {
			g.setColor(Color.red);
			g.fillOval((int) nodes.get(sel).x-radius, (int) nodes.get(sel).y-radius, 2*radius, 2*radius);
		}
		if(con != -1) {
			g.setColor(Color.darkGray);
			g.fillOval((int) nodes.get(con).x-radius, (int) nodes.get(con).y-radius, 2*radius, 2*radius);
			g.drawLine((int) nodes.get(con).x, (int) nodes.get(con).y, gc.getInput().getMouseX(), gc.getInput().getMouseY());
		}
		g.setColor(Color.white);
		for(int i = 0; i < nodes.size(); i++) {
			n = nodes.get(i);
			g.drawOval((int) n.x-radius, (int) n.y-radius, 2*radius, 2*radius);
		}
		for(int i = 0; i < cons.size(); i++) {
			c = cons.get(i);
			g.drawLine((int) nodes.get(cons.get(i).a).x, (int) nodes.get(cons.get(i).a).y, (int) nodes.get(cons.get(i).b).x, (int) nodes.get(cons.get(i).b).y);
		}
		g.drawString(tutActive ? tut:help, 15, 30);
	}

	private class Connection {
		public int a, b;
		public Connection(int a, int b) {
			this.a = a;
			this.b = b;
		}
	}
	private class Node {
		public double x, y;
		public Node(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

}
