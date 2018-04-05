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
		  //  GFG.dctTransform(a);
	        
	}

	
	public static class GFG {
		
	    public static int n = 8,m = 8;
	    
	    /**
	     * a is the 2D array of pixels to perform DCT on 
	     * @param a
	     */
	    static float[][] dctTransform(float a[][]){
	        int i, j, u, v;
	  
	        for(int x=0;x<8;x++) {
	           for(int y=0;y<8;y++) {
	        		   a[x][y] -= 128; 
	        	  }
	        }
	        
	        float[][] b = new float[m][n];
	  
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
	                b[i][j] = (float) (ci * cj * sum);

	            }
	        }
	  
//	        System.out.println("\nDCT applied to A 2-D array and get B: ");
//	        System.out.println("-----------");
//	        for (i = 0; i < m; i++) {
//	            for (j = 0; j < n; j++) {
//	                System.out.printf("%5.1f      ", b[i][j]);
//	        }
//	          System.out.println();
//	        }
	        
	        
	        
	     // applies the inverse Discrete Cosine Transform to 2-D array B
	        // inverseDCT(b);
	        return b;
	    }
	    
	    
	    static int N = 8;
	    static float[] initializeCoefficients() {
	    	float[] c = new float[8];
	    	for (int i=1;i<N;i++) {
	            c[i]=1;
	        }
	        c[0]=(float) (1/Math.sqrt(2.0));
	        return c;
	    }
	    
	    static float[][] inverseDCT(float[][] b) {
	   
	    	float[] c = new float[N];
	    	c = initializeCoefficients();
	    	//double ci,cj,sum;
	    	float[][] a = new float[N][N];
	    	
	        for (int i=0;i<N;i++) {
	          for (int j=0;j<N;j++) {
	        	  	float sum = (float) 0.0;
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
	        
//	    System.out.println("\nIDCT applied to B array and get C: ");
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


