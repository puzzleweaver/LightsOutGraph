package main;

public class GaussianElimination {
	
    public static boolean[] solve(boolean[][] A, boolean[] b) {
    	int N = b.length;
    	for(int p = 0; p < N; p++) {
    		//find pivot row and swap
    		int max = p;
            for (int i = p; i < N; i++) {
                if (A[i][p]) {
                    max = i;
                    break;
                }
            }
			
            boolean[] temp = A[p]; A[p] = A[max]; A[max] = temp;
            boolean   t    = b[p]; b[p] = b[max]; b[max] = t;
            
            // pivot within A and b
            for (int i = p + 1; i < N; i++) {
            	if(A[i][p]) {
	                b[i] = b[i] != b[p];
	                for (int j = p; j < N; j++) {
	                    A[i][j] = A[i][j] != A[p][j];
		            }
            	}
	        }
    	}
    	
    	// back substitution
        boolean[] x = new boolean[N];
        for (int i = N - 1; i >= 0; i--) {
        	boolean sum = false;
            for (int j = i + 1; j < N; j++) {
                sum = sum != (A[i][j] && x[j]);
            }
            x[i] = (b[i] != sum);
        }
        return x;
    }
    
}