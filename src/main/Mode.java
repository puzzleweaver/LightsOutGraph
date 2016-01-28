package main;

import org.newdawn.slick.Input;

public abstract class Mode {
	
	public String tut, name;
	
	public Mode(String tut, String name) {
		this.tut = tut;
		this.name = name;
	}
	
	public abstract void update(Input in);
	
}
