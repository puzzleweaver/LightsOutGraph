package main;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.swing.UIManager;

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
	public static int mouseX, mouseY, mouseWheel;
	public static final Mode[] modes = {new EditMode(), new GenMode(), new SolveMode()};
	public static int mode = 0, notifyTimer = 0;
	public static String note;

	public static boolean tutActive = false, numsShown = false;
	public static String help = "h : help",
			tutPref = "h : help\n" + 
					"arrow keys : switch modes\n" +
					"q : rotate with mouse wheel\n" +
					"1 : save state\n" +
					"2 : load state\n" +
					"3 : toggle show numbers\n" +
					"esc : exit\n" +
					"space : center frame around vertices\n";

	public static int defaultLen = 100, border = 100;

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
		super("Lights Out Graph Program");
		System.out.println(modes[0].name);
	}

	public void init(GameContainer gc) throws SlickException {
		gc.setShowFPS(false);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public void update(GameContainer gc, int arg1) throws SlickException {
		Input in = gc.getInput();

		GH.propagate();

		// pan function
		if(in.isMouseButtonDown(2)) {
			for(int i = 0; i < GH.nodes.size(); i++) {
				GH.nodes.get(i).x -= mouseX-in.getMouseX();
				GH.nodes.get(i).y -= mouseY-in.getMouseY();
			}
		}

		mouseX = in.getMouseX();
		mouseY = in.getMouseY();

		// zoom and rotate functions
		mouseWheel = Mouse.getDWheel();
		if(mouseWheel != 0) {
			if(in.isKeyDown(Keyboard.KEY_Q)) {
				double d, t;
				for(int i = 0; i < GH.nodes.size(); i++) {
					GH.nodes.get(i).x = GH.nodes.get(i).x-w/2;
					GH.nodes.get(i).y = GH.nodes.get(i).y-h/2;
					d = Math.hypot(GH.nodes.get(i).x, GH.nodes.get(i).y);
					t = Math.atan2(GH.nodes.get(i).y, GH.nodes.get(i).x);
					t += mouseWheel < 0 ? -0.1:0.1;
					GH.nodes.get(i).x = Math.cos(t)*d+w/2;
					GH.nodes.get(i).y = Math.sin(t)*d+h/2;
				}
			}else {
				for(int i = 0; i < GH.nodes.size(); i++) {
					GH.nodes.get(i).x = (GH.nodes.get(i).x-mouseX)*(mouseWheel < 0 ? 0.9:1.111)+mouseX;
					GH.nodes.get(i).y = (GH.nodes.get(i).y-mouseY)*(mouseWheel < 0 ? 0.9:1.111)+mouseY;
				}
			}
		}
		//  toggle help menu activation
		if(in.isKeyPressed(Keyboard.KEY_H))
			tutActive = !tutActive;
		if(in.isKeyPressed(Keyboard.KEY_1))
			saveData();
		if(in.isKeyPressed(Keyboard.KEY_2))
			loadData();
		if(in.isKeyPressed(Keyboard.KEY_3))
			numsShown = !numsShown;


		modes[mode].update(in);

		//  exit functionality
		if(in.isKeyDown(Keyboard.KEY_ESCAPE)) {
			System.exit(0);
		}
		// center functionality
		if(in.isKeyDown(Keyboard.KEY_SPACE))
			center();

		if(in.isKeyPressed(Keyboard.KEY_RIGHT))
			mode = (mode+1)%modes.length;
		if(in.isKeyPressed(Keyboard.KEY_LEFT))
			mode = (mode+modes.length-1)%modes.length;

	}

	public static void center() {
		double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE,
				maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;
		for(int i = 0; i < GH.nodes.size(); i++) {
			if(GH.nodes.get(i).x < minX)
				minX = GH.nodes.get(i).x;
			if(GH.nodes.get(i).x > maxX)
				maxX = GH.nodes.get(i).x;
			if(GH.nodes.get(i).y < minY)
				minY = GH.nodes.get(i).y;
			if(GH.nodes.get(i).y > maxY)
				maxY = GH.nodes.get(i).y;
		}
		double d = Math.max(maxX-minX, maxY-minY);
		for(int i = 0; i < GH.nodes.size(); i++) {
			GH.nodes.get(i).x = (GH.nodes.get(i).x-minX)*(w-2*border)/d+border;
			GH.nodes.get(i).y = (GH.nodes.get(i).y-minY)*(h-2*border)/d+border;
		}
	}

	public void render(GameContainer gc, Graphics g) throws SlickException {
		g.setAntiAlias(true);
		g.setLineWidth(3);
		
		GH.renderDown(gc, g);

		modes[mode].renderDown(gc, g);

		GH.render(gc, g);

		modes[mode].render(gc, g);

		g.setColor(Color.white);
		String str = (tutActive ? (tutPref + modes[mode].tut):help);
		g.drawRect(5, 5, g.getFont().getWidth(str)+20, g.getFont().getHeight(str)+20);
		g.setColor(Color.black);
		g.fillRect(5, 5, g.getFont().getWidth(str)+20, g.getFont().getHeight(str)+20);
		g.setColor(Color.white);
		g.drawString(str, 15, 15);
		
		if(notifyTimer != 0) {
			notifyTimer--;
			g.setColor(Color.black);
			g.fillRect(w/2-g.getFont().getWidth(note)/2-10, h/2-g.getFont().getHeight(note)/2-10, g.getFont().getWidth(note)+20, g.getFont().getHeight(note)+20);
			g.setColor(Color.white);
			g.drawRect(w/2-g.getFont().getWidth(note)/2-10, h/2-g.getFont().getHeight(note)/2-10, g.getFont().getWidth(note)+20, g.getFont().getHeight(note)+20);
			g.drawString(note, w/2-g.getFont().getWidth(note)/2, h/2-g.getFont().getHeight(note)/2);
		}
		
		int x = 5;
		for(int i = 0; i < modes.length; i++) {
			str = modes[i].name;
			g.setColor(i == mode ? Color.yellow:Color.white);
			g.drawRect(x, h-g.getFont().getHeight(str)-25, g.getFont().getWidth(str)+20, g.getFont().getHeight(str)+20);
			g.setColor(Color.black);
			g.fillRect(x, h-g.getFont().getHeight(str)-25, g.getFont().getWidth(str)+20, g.getFont().getHeight(str)+20);
			g.setColor(i == mode ? Color.yellow:Color.white);
			g.drawString(str, x+10, h-g.getFont().getHeight(str)-15);
			x += g.getFont().getWidth(str)+20;
		}
	}

	public void saveData() {
		String out = "";
		for(int i = 0; i < GH.nodes.size(); i++) {
			out += GH.nodes.get(i).x + " " + GH.nodes.get(i).y + " " + (GH.nodes.get(i).clicked?0:1) + " ";
		}
		out += ".\n";
		for(int i = 0; i < GH.cons.size(); i++) {
			for(int j = 0; j < GH.cons.size(); j++) {
				out += GH.cons.get(i).get(j) ? "1":"0";
			}
			out += "\n";
		}
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(promptForFile(true));
		}catch(Exception e) {
			notify("NO FILE CHOSEN");
			return;
		}
		pw.print(out);
		pw.close();
		notify("SAVED SUCCESSFULLY");
	}
	public void loadData() {
		String in;
		try {
			in = new String(Files.readAllBytes(Paths.get(promptForFile(false))));//JFileChooser.OPEN_DIALOG))));
		} catch (Exception e) {
			notify("NO FILE CHOSEN");
			return;
		}
		try {
			GH.load(in);
		}catch(Exception e) {
			notify("INCORRECTLY FORMATTED FILE");
			return;
		}
		notify("FILE LOADED SUCCESSFULLY");
	}
	
	public String promptForFile(boolean saving) {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode( JFileChooser.FILES_ONLY );
//		fc.setDialogTitle(title);
		if((saving ? fc.showSaveDialog(null):fc.showOpenDialog(null)) == JFileChooser.APPROVE_OPTION) {
			return fc.getSelectedFile().getAbsolutePath();
		}
		return null;
	}
	
	public void notify(String str) {
		note = str;
		notifyTimer = 200;
	}

}
