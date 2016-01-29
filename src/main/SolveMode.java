package main;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class SolveMode extends Mode {
	
	public SolveMode() {
		super("r : reset board\n" +
				"s : solve board\n" +
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
				}
			}
		}
		
		if(in.isKeyPressed(Keyboard.KEY_R)) {
			reset();
		}
		
		if(in.isKeyPressed(Keyboard.KEY_S)) {
			solve();
		}
		
	}
	
	public void render(GameContainer gc, Graphics g) {
		
	}
	
	public void reset() {
		for(int i = 0; i < APIMain.nodes.size(); i++) {
			APIMain.nodes.get(i).reset();
		}
	}
	
	public void solve() {
		reset();
		for(int i = 0; i < APIMain.nodes.size(); i++) {
			if(APIMain.nodes.get(i).val == false) {
				solve(i);
			}
		}
	}
	public void solve(int stemNode) {
		ArrayList<Integer> nextNodes = new ArrayList<>();
		nextNodes.add(stemNode);
		int numVars = 1;
		Term[] terms = new Term[APIMain.nodes.size()];
		terms[stemNode] = new Term(false, true);
		ArrayList<Term> equations = new ArrayList<>();
		
		for(int k = 0; k < nextNodes.size(); k++) {
			int n = nextNodes.get(k);
			
			//undiscovered & discovered nodes attached to node n
			//discovered means it was put in terms of variables
			ArrayList<Integer> undiscovered = new ArrayList<>();
			ArrayList<Integer> discovered = new ArrayList<>();
			for(int i = 0; i < APIMain.cons.size(); i++) {
				Connection c = APIMain.cons.get(i);
				if(c.a == n) {
					if(terms[c.b] == null)
						undiscovered.add(c.b);
					else
						discovered.add(c.b);
				}else if(c.b == n) {
					if(terms[c.a] == null)
						undiscovered.add(c.a);
					else
						discovered.add(c.a);
				}
			}
			if(undiscovered.size() == 0) {
				//create new equation based on known data
				Term[] termsTemp = new Term[discovered.size()+1];
				termsTemp[0] = terms[n];
				for(int i = 0; i < discovered.size(); i++)
					termsTemp[i+1] = terms[discovered.get(i)];
				equations.add(addTerms(termsTemp));
			}else {
				//put the last undiscovered node in terms of variables
				Term[] termsTemp = new Term[undiscovered.size() + discovered.size()];
				termsTemp[0] = new Term(!terms[n].inverted, terms[n].b);
				//create a new variable for each node except the last undiscovered one
				for(int i = 1; i < undiscovered.size(); i++) {
					boolean[] b = new boolean[numVars+1];
					b[numVars] = true;
					termsTemp[i] = new Term(false, b);
					terms[undiscovered.get(i)] = termsTemp[i];
					numVars++;
				}
				for(int i = 0; i < discovered.size(); i++) {
					termsTemp[undiscovered.size() + i] = terms[discovered.get(i)];
				}
				terms[undiscovered.get(0)] = addTerms(termsTemp);
			}
			//do the same for all the undiscovered nodes
			for(int i = 0; i < undiscovered.size(); i++) {
				nextNodes.add(undiscovered.get(i));
			}
		}
		//actually solve the equations using gaussian elimination
		boolean A[][] = new boolean[equations.size()][equations.size()];
		boolean b[] = new boolean[equations.size()];
		for(int i = 0; i < equations.size(); i++) {
			Term t = equations.get(i);
			for(int j = 0; j < t.b.length; j++) {
				A[i][j] = t.b[j]; //maybe A[j][i] ?
			}
			b[i] = !t.inverted;
		}
		boolean[] solution = GaussianElimination.solve(A, b);
		//propogate variables to board
		for(int i = 0; i < terms.length; i++) {
			if(terms[i] != null) {
				boolean clicked = terms[i].inverted;
				for(int j = 0; j < terms[i].b.length; j++) {
					clicked ^= (terms[i].b[j] && solution[j]);
				}
				if(APIMain.nodes.get(i).clicked != clicked) {
					APIMain.nodes.get(i).trigger();
				}
			}
		}
	}
	
	//given a list of terms, create a new term where the variables are XORed
	private Term addTerms(Term ...terms) {
		int maxLength = 0;
		for(int i = 0; i < terms.length; i++)
			maxLength = Math.max(maxLength, terms[i].b.length);
		boolean[] b = new boolean[maxLength];
		boolean inverted = false;
		for(int i = 0; i < terms.length; i++) {
			inverted ^= terms[i].inverted;
			for(int j = 0; j < terms[i].b.length; j++)
				b[j] ^= terms[i].b[j];
		}
		return new Term(inverted, b);
	}
	
	private class Term {
		
		public boolean inverted;
		public boolean[] b;
		
		public Term(boolean inverted, boolean... b) {
			this.inverted = inverted;
			this.b = b;
		}
		
		public String toString() {
			String s = inverted ? "1 " : "0 ";
			for(int i = 0; i < b.length; i++) {
				s = s + (b[i] ? "1 " : "0 ");
			}
			return s;
		}
		
	}
	
}
