public class DCT {
	
	
	public static void main(String[] args) {
		
		double[][] a = { {139,144,149,153,155,155,155,155},
						 {144,151,153,156,159,156,156,156},
						 {150,155,160,163,158,156,156,156},
						 {159,161,162,160,160,159,159,159},
						 {159,160,161,162,162,155,155,155},
						 {161,161,161,161,160,157,157,157},
						 {162,162,161,163,162,157,157,157},
						 {162,162,161,161,163,158,158,158}
						};
		
	    System.out.println("Original 2-D array A: ");
	    System.out.println("-----------");
	    for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.printf("%.1f   ", a[i][j]);
        }
          System.out.println();
        }	        
	        // perform Discrete Cosine Transform. 
		    GFG.dctTransform(a);
	        
	}

	
	public static class GFG {
		
	    public static int n = 8,m = 8;
	    
	    /**
	     * a is the 2D array of pixels to perform DCT on 
	     * @param a
	     */
	    static double[][] dctTransform(double a[][]){
	        int i, j, u, v;
	  
	        for(int x=0;x<8;x++) {
	           for(int y=0;y<8;y++) {
	        		   a[x][y] -= 128; 
	        	  }
	        }
	        
	        double[][] b = new double[m][n];
	  
	        double ci, cj, dct1, sum;
	  
	        for (i = 0; i < m; i++) {
	            for (j = 0; j < n; j++) {
	                if (i == 0)
	                    ci = 1 / Math.sqrt(m);
	                else
	                    ci = Math.sqrt(2) / Math.sqrt(m);
	                     
	                if (j == 0)
	                    cj = 1 / Math.sqrt(n);
	                else
	                    cj = Math.sqrt(2) / Math.sqrt(n);
	  
	                 sum = 0;
	                for (u = 0; u < m; u++) {
	                    for (v = 0; v < n; v++) {
	                        dct1 = a[u][v] * 
	                               Math.cos((2 * u + 1) * i * Math.PI / (2 * m)) * 
	                               Math.cos((2 * v + 1) * j * Math.PI / (2 * n));
	                        sum = sum + dct1;
	                    }
	                }
	                b[i][j] = ci * cj * sum;
	            }
	        }
	  
//	        System.out.println("\nDCT applied to 2-D array B: ");
//	        System.out.println("-----------");
//	        for (i = 0; i < m; i++) {
//	            for (j = 0; j < n; j++) {
//	                System.out.printf("%5.1f      ", b[i][j]);
//	        }
//	          System.out.println();
//	        }
	        
	        
	        
	     // applies the inverse Discrete Cosine Transform to 2-D array B
	        
	        return b;
	    }
	    
	    
	    static int N = 8;
	    static double[] initializeCoefficients() {
	    	double[] c = new double[8];
	    	for (int i=1;i<N;i++) {
	            c[i]=1;
	        }
	        c[0]=1/Math.sqrt(2.0);
	        return c;
	    }
	    
	    static double[][] inverseDCT(double[][] b) {
	   
	    	double[] c = new double[N];
	    	c = initializeCoefficients();
	    	//double ci,cj,sum;
	    	double[][] a = new double[N][N];
	    	
	        for (int i=0;i<N;i++) {
	          for (int j=0;j<N;j++) {
	        	  	double sum = 0.0;
	            for (int u=0;u<N;u++) {
	              for (int v=0;v<N;v++) {
//	            	  sum += ci * cj * b[u][v]
//	                		 	* Math.cos((2 * i + 1) * u * Math.PI / (2 * m)) 
//	                		 	* Math.cos((2 * j + 1) * v * Math.PI / (2 * n));
	            	  sum+=(c[u]*c[v])/4.0*Math.cos(((2*i+1)/(2.0*N))*u*Math.PI)*Math.cos(((2*j+1)/(2.0*N))*v*Math.PI)*b[u][v];
	              }
	            }
	            a[i][j]= sum;
	          }
	        }

	    	
	    for(int x=0;x<8;x++) {
	        	for(int y=0;y<8;y++) {
	        		a[x][y] += 128; 
	        	}
	    }
	        
//	    System.out.println("\nIDCT applied to 2-D array C: ");
//	    System.out.println("-----------");
//	    for (int i = 0; i < m; i++) {
//            for (int j = 0; j < n; j++) {
//                System.out.printf("%.1f   ", a[i][j]);
//        }
//          System.out.println();
//        }
	    
	    	return a;
	    } // function ends	    
	    
	    
	    
	    
	    
	}
}


