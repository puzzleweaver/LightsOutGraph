package main;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
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
	public static int mouseX, mouseY, mouseWheel;
	public static final Mode[] modes = {new EditMode(), new SolveMode()};
	public static int mode = 0;

	public static boolean tutActive = false;
	public static String help = "h : help",
			tutPref = "h : help\n" + 
					"t : switch mode\n" +
					"3 : show numbers\n" +
					"esc : exit\n";

	public static int radius = 15, defaultLen = 100, border = 100, sel = -1;

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
		System.out.println(modes[0].name);
	}

	public void init(GameContainer arg0) throws SlickException {
		
	}

	public void update(GameContainer gc, int arg1) throws SlickException {
		Input in = gc.getInput();

		// pan function
		if(in.isMouseButtonDown(2)) {
			for(int i = 0; i < nodes.size(); i++) {
				nodes.get(i).x -= mouseX-in.getMouseX();
				nodes.get(i).y -= mouseY-in.getMouseY();
			}
		}

		mouseX = in.getMouseX();
		mouseY = in.getMouseY();

		// zoom function
		mouseWheel = Mouse.getDWheel();
		if(mouseWheel != 0) {
			if(mouseWheel < 0) {
				for(int i = 0; i < nodes.size(); i++) {
					nodes.get(i).x = (nodes.get(i).x-mouseX)*0.9+mouseX;
					nodes.get(i).y = (nodes.get(i).y-mouseY)*0.9+mouseY;
				}
			}else {
				for(int i = 0; i < nodes.size(); i++) {
					nodes.get(i).x = (nodes.get(i).x-mouseX)*1.111+mouseX;
					nodes.get(i).y = (nodes.get(i).y-mouseY)*1.111+mouseY;
				}
			}
		}
		//  toggle help menu activation
		if(in.isKeyPressed(Keyboard.KEY_H)) {
			tutActive = !tutActive;
		}
		
		modes[mode].update(in);
		
		//  exit functionality
		if(in.isKeyDown(Keyboard.KEY_ESCAPE)) {
			System.exit(0);
		}
		// center functionality
		if(in.isKeyDown(Keyboard.KEY_C)) {
			double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE,
					maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;
			for(int i = 0; i < nodes.size(); i++) {
				if(nodes.get(i).x < minX)
					minX = nodes.get(i).x;
				if(nodes.get(i).x > maxX)
					maxX = nodes.get(i).x;
				if(nodes.get(i).y < minY)
					minY = nodes.get(i).y;
				if(nodes.get(i).y > maxY)
					maxY = nodes.get(i).y;
			}
			double d = Math.max(maxX-minX, maxY-minY);
			for(int i = 0; i < nodes.size(); i++) {
				nodes.get(i).x = (nodes.get(i).x-minX)*(w-2*border)/d+border;
				nodes.get(i).y = (nodes.get(i).y-minY)*(h-2*border)/d+border;
			}
		}

		// make selected vertex follow the mouse
		if(sel != -1){
			nodes.get(sel).x = in.getMouseX();
			nodes.get(sel).y = in.getMouseY();
		}
		
		if(in.isKeyPressed(Keyboard.KEY_T))
			mode = (mode+1)%modes.length;
		
	}

	public void render(GameContainer gc, Graphics g) throws SlickException {
		g.setAntiAlias(true);
		Node n;
		Connection c;
		g.setColor(Color.white);
		for(int i = 0; i < cons.size(); i++) {
			c = cons.get(i);
			g.drawLine((int) nodes.get(c.a).x, (int) nodes.get(c.a).y, (int) nodes.get(c.b).x, (int) nodes.get(c.b).y);
		}
		for(int i = 0; i < nodes.size(); i++) {
			n = nodes.get(i);
			g.setColor(n.getColor());
			g.fillOval((int) n.x-radius, (int) n.y-radius, 2*radius, 2*radius);
			g.setColor(Color.white);
			g.drawOval((int) n.x-radius, (int) n.y-radius, 2*radius, 2*radius);
			if(gc.getInput().isKeyDown(Keyboard.KEY_3))
				g.drawString(""+i, (int) n.x-g.getFont().getWidth(""+i)+radius/2, (int) n.y-g.getFont().getHeight(""+i)+radius/2);
		}
		
		modes[mode].render(gc, g);
		
		g.setColor(Color.white);
		String str = tutActive ? (tutPref + modes[mode].tut):help;
		g.drawRect(15, 30, g.getFont().getWidth(str)+10, g.getFont().getHeight(str)+10);
		g.setColor(Color.black);
		g.fillRect(15, 30, g.getFont().getWidth(str)+10, g.getFont().getHeight(str)+10);
		g.setColor(Color.white);
		g.drawString(str, 15, 30);
		g.drawString(modes[mode].name, h-10-g.getFont().getWidth(modes[mode].name), h-20);
	}

}
