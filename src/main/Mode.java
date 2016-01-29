package main;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public abstract class Mode {
	
	public String tut, name;
	
	public Mode(String tut, String name) {
		this.tut = tut;
		this.name = name;
	}
	
	public abstract void update(Input in);
	public abstract void render(GameContainer gc, Graphics g);
	public void renderDown(GameContainer gc, Graphics g) {}
	
}
