package main;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class SolveMode extends Mode {

	public SolveMode() {
		super("r : reset board\n" +
				"<More soon?>\n",
				"SOLVE");
	}

	public void update(Input in) {
		
		if(in.isMousePressed(0)) {
			int x = in.getMouseX(), y = in.getMouseY();
			Node n;
			Connection c;
			for(int i = 0; i < APIMain.nodes.size(); i++) {
				n = APIMain.nodes.get(i);
				if((n.x-x)*(n.x-x)+(n.y-y)*(n.y-y) < APIMain.radius*APIMain.radius) {
					APIMain.nodes.get(i).trigger();
					for(int j = 0; j < APIMain.cons.size(); j++) {
						c = APIMain.cons.get(j);
						if(c.a == i)
							APIMain.nodes.get(c.b).effect();
						if(c.b == i)
							APIMain.nodes.get(c.a).effect();
					}
				}
			}
		}
		
		if(in.isKeyPressed(Keyboard.KEY_R)) {
			for(int i = 0; i < APIMain.nodes.size(); i++) {
				APIMain.nodes.get(i).reset();
			}
		}
		
	}
	
	public void render(GameContainer gc, Graphics g) {
		
	}

}
