package main;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class GenMode extends Mode {

	int num = 0, x, y, r = 50;
	int kp = -1;
	
	public GenMode() {
		super("a : square grid\n" +
				"b : triangle grid",
				"GENERATE");
	}

	public void update(Input in) {
		if((kp == -1 || kp == Keyboard.KEY_A) && in.isKeyDown(Keyboard.KEY_A)) {
			kp = Keyboard.KEY_A;
			if(num == 0) {
				x = in.getMouseX();
				y = in.getMouseY();
			}
			if(in.isMousePressed(0)) {
				addToGrid();
				num++;
			}
		}else if(kp == Keyboard.KEY_A){
			num = 0;
			kp = -1;
		}
		if((kp == -1 || kp == Keyboard.KEY_B) && in.isKeyDown(Keyboard.KEY_B)) {
			kp = Keyboard.KEY_B;
			if(num == 0) {
				x = in.getMouseX();
				y = in.getMouseY();
			}
			if(in.isMousePressed(0)) {
				addToHex();
				num++;
			}
		}else if(kp == Keyboard.KEY_B){
			num = 0;
			kp = -1;
		}
	}
	
	public void addToHex() {
		for(int i = 0; i < num+1; i++) {
			GH.addVertex(x+i*r-num*0.5*r, y+0.86602540378*num*r);
			if(i != num)
				GH.addConnection(GH.nodes.size()-1, GH.nodes.size()-num-1);
			if(i != 0) {
				GH.addConnection(GH.nodes.size()-1, GH.nodes.size()-num-2);
				GH.addConnection(GH.nodes.size()-1, GH.nodes.size()-2);
			}
		}
	}
	
	public void addToGrid() {
		for(int i = 0; i < num; i++) {
			GH.addVertex(x+num*r, y+i*r);
			GH.addVertex(x+i*r, y+num*r);
			if(num > 1 && i != 0) {
				GH.addConnection(GH.nodes.size()-2, GH.nodes.size()-4);
				GH.addConnection(GH.nodes.size()-1, GH.nodes.size()-3);
			}
			if(num > 1) {
				GH.addConnection(GH.nodes.size()-2, GH.nodes.size()-num*2-1);
				if(i != num-1)
					GH.addConnection(GH.nodes.size()-1, GH.nodes.size()-num*2);
				else
					GH.addConnection(GH.nodes.size()-1, GH.nodes.size()-num*2-1);
			}else {
				GH.addConnection(GH.nodes.size()-1, GH.nodes.size()-num*2-1);
				GH.addConnection(GH.nodes.size()-2, GH.nodes.size()-num*2-1);
			}
		}
		GH.addVertex(x+num*r, y+num*r);
		if(num != 0) {
			GH.addConnection(GH.nodes.size()-1, GH.nodes.size()-3);
			GH.addConnection(GH.nodes.size()-1, GH.nodes.size()-2);
		}
	}

	public void render(GameContainer gc, Graphics g) {
		g.setColor(Color.yellow);
		
		// preview grid generation
		if(gc.getInput().isKeyDown(Keyboard.KEY_A)) {
			for(int i = 0; i < num; i++) {	
				new Node(x+num*r, y+i*r).draw(g);
				new Node(x+i*r, y+num*r).draw(g);
			}
			new Node(x+num*r, y+num*r).draw(g);
		}
		
		// preview hex generation
		if(gc.getInput().isKeyDown(Keyboard.KEY_B)) {
			for(int i = 0; i < num+1; i++) {
				new Node(x+i*r-num*0.5*r, y+0.86602540378*num*r).draw(g);
			}
		}
		
	}

}
