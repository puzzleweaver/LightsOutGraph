package main;

import java.util.Random;

public class GaussianElimination {
	
	private static Random r = new Random();
	
    public static boolean[] solve(boolean[][] A, boolean[] b) {
    	int N = b.length;
    	int P = 0;
    	for(int p = 0; p < N; p++) {
    		//find pivot row and swap
    		int max = P;
    		boolean bool = true;
            for (int i = P; i < N; i++) {
                if (A[i][p]) {
                    max = i;
                    bool = false;
                    break;
                }
            }
            if(bool) {
            	continue;
            }
			
            //swap rows
            boolean[] temp = A[P]; A[P] = A[max]; A[max] = temp;
            boolean   t    = b[P]; b[P] = b[max]; b[max] = t;
            
            // pivot within A and b
            for (int i = P + 1; i < N; i++) {
            	if(A[i][p]) {
	                b[i] ^= b[P];
	                for (int j = p; j < N; j++) {
	                    A[i][j] ^= A[P][j];
		            }
            	}
	        }
            P++;
    	}
    	
    	//back substitution
    	boolean[] x = new boolean[N];
    	for(int i = 0; i < x.length; i++) {
    		x[i] = r.nextBoolean();
    	}
    	for(int i = N-1; i >= 0; i--) {
    		for(int j = i; j < N; j++) {
    			if(A[i][j]) {
    				x[j] = false;
    				for(int k = j+1; k < N; k++)
    					x[j] ^= A[i][k] && x[k];
    				x[j] ^= b[i];
    				break;
    			}
    		}
    	}
    	
    	return x;
    }
    
}