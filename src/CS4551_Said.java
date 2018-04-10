import java.util.Scanner;

public class CS4551_Said {
	
	static int multiple = 8;
	static int[] zeros = {0,0,0};
	static int[] originalImageObjRGB = new int[3];
	static int widthDiff, heightDiff; 
	final static int[][] luminY = {
			{4,4,4,8,8,16,16,32},
			{4,4,4,8,8,16,16,32},
			{4,4,8,8,16,16,32,32},
			{8,8,8,16,16,32,32,32},
			{8,8,16,16,32,32,32,32},
			{16,16,16,32,32,32,32,32},
			{16,16,32,32,32,32,32,32},
			{32,32,32,32,32,32,32,32,32}};
	
	final static int[][] ChromCbCr = {
			{8,8,8,16,32,32,32,32},
			{8,8,8,16,32,32,32,32},
			{8,8,16,32,32,32,32,32},
			{16,16,32,32,32,32,32,32},
			{32,32,32,32,32,32,32,32},
			{32,32,32,32,32,32,32,32},
			{32,32,32,32,32,32,32,32},
			{32,32,32,32,32,32,32,32}	};
	/** 
	 * resize the image if it's not multiple of 8 in W and H.
	 * if not, the image will be increased with Zeros values. 
	 * 
	 * @param imageName
	 * 
	 */
	 static void readandresizetheimage(String imageName) { 
		
		Image originalImageObj = new Image(imageName); 						// reading the image from the command line argu
		
		// make sure the image is multiple of 8 in W and H
		if(originalImageObj.getW()%multiple != 0 & originalImageObj.getH()%multiple != 0) {
			 widthDiff = originalImageObj.getW()%8;
			 heightDiff = originalImageObj.getH()%8;
			
			int newW = originalImageObj.getW()+(multiple-widthDiff);    // resize the image W
			int newH = originalImageObj.getH()+(multiple-heightDiff);   // resize the image H
			
			Image newImageObj = new Image(newW,newH);  // create the new resized image.
			
			for(int width=0; width<newW; width++) {
				for(int height=0; height<newH; height++) {
					if(width > originalImageObj.getW()-1 || height > originalImageObj.getH()-1) {  // if the pixel position is new then 0
						newImageObj.setPixel(width, height, zeros);
					}else {   												 // else we have the original image pixels values.
						originalImageObj.getPixel(width, height, originalImageObjRGB);
						newImageObj.setPixel(width, height, originalImageObjRGB);
					} // end of if
				}  // end of height
			} // end of width
			convertFromRGBtoYCbCr(newImageObj); 				// if we resized the original image. (newImageObj)
		}else { 
			convertFromRGBtoYCbCr(originalImageObj);          // if we didn't resize the original image (imageObj)
		} // resize image (if)
	} // function ends 
	
	 
	 
	/**
	 * Algorithm for converting from RGB to YCbCr. 
	 * @param image
	 */
		 static void convertFromRGBtoYCbCr(Image image) {
			
			 // Initialization
			int[] rgb = new int[3];
			double[][][] pixelsArray = new double[image.getW()][image.getH()][3];
			
			
			for(int width=0; width<image.getW(); width++){    		
				for(int height=0; height<image.getH(); height++) {
					image.getPixel(width, height, rgb);  
					double[] YCbCr = convertToYCbCr(rgb);// Find the YCbCr values.
					
					YCbCr[0] -= 128; 
					YCbCr[1] -= 0.5;
					YCbCr[2] -= 0.5;
					
					pixelsArray[width][height][0] =  YCbCr[0];
					pixelsArray[width][height][1] =  YCbCr[1];
					pixelsArray[width][height][2] =  YCbCr[2];
				} 
			} // end for loop
			 
			
			subsample(pixelsArray);   					

		} 												// end convertFromRGBtoYCbCr

		
		 /**
			 * Apply the equations to find the values for [Y][Cb][Cr]
			 * 
			 * @param rgb array values
			 * @return YCbCr array values 
			 */
			 static double[] convertToYCbCr(int[] rgb) {
				 
				 // Initialization 
				 double[] newYCbCr = new double[3];
				 
				 newYCbCr[0] = (rgb[0] * 0.299) + (rgb[1] * 0.5870) + (rgb[2] * 0.1140);
				 newYCbCr[1] = (rgb[0] * -0.1687) + (rgb[1] * -0.3313) + (rgb[2] * 0.5);
				 newYCbCr[2] = (rgb[0] * .5) + (rgb[1] * -0.4187) + (rgb[2] * -0.0813);
				
				if(newYCbCr[0] > 255) newYCbCr[0] = 255;
				if(newYCbCr[0] < 0) newYCbCr[0] = 0;
				if(newYCbCr[1] > 127.5) newYCbCr[1] =  127.5;
				if(newYCbCr[1] < -127.5) newYCbCr[1] =  -127.5;
				if(newYCbCr[2] > 127.5) newYCbCr[2] =  127.5;
				if(newYCbCr[2] < -127.5) newYCbCr[2] = -127.5;
				
				return newYCbCr; 
			} // end function calculateYCbCr
			 	
				 /**
				 * apply subsample 4:2:0 algorithm and show the image.
				 * 
				 * @param Image image
				 */
				 static void subsample(double[][][] pixelValues) {
					
//					 // Initialization
					int width,height, CbCrwidth=pixelValues.length/2, CbCrheight=pixelValues[0].length/2;
					if(CbCrheight%multiple != 0) CbCrheight += (8-(CbCrheight%8));
					if(CbCrwidth%multiple != 0) CbCrwidth += (8-(CbCrwidth%8));
					double[][] yValues = new double[pixelValues.length][pixelValues[0].length];   // 256x280
					double[][] CbValues = new double[CbCrwidth][CbCrheight];    
					double[][] CrValues = new double[CbCrwidth][CbCrheight];
					
					
					// extract Y values
					for(width=0; width<pixelValues.length; width++) {
						for(height=0; height<pixelValues[0].length; height++) {
							yValues[width][height] = pixelValues[width][height][0];
						} // height
					} // end width.
					
					// extract Cb, Cr values
					for(width=0; width<yValues.length;width+=2) {
						for(height=0; height<yValues[0].length;height+=2) {
							
							double avrCb = (pixelValues[width][height][1] +
									       pixelValues[width+1][height][1] + 
									       pixelValues[width][height+1][1] +
									       pixelValues[width+1][height+1][1]) / 4;
							
							double avrCr = (pixelValues[width][height][2] +
								       pixelValues[width+1][height][2] + 
								       pixelValues[width][height+1][2] +
								       pixelValues[width+1][height+1][2]) / 4;
							
							
							CbValues[width/2][height/2] = avrCb;
							CrValues[width/2][height/2] = avrCr;
						}
					} 
					
					// do DCT to yValues, CbValues, CrValues
					Scanner sc = new Scanner(System.in);
					System.out.println("please chosse N from 1 - 5: ");
					int n = sc.nextInt();
					double[][] dctYValues = applyDCT(n, true, yValues);
					double[][] dctCbValues = applyDCT(n, false, CbValues);
					double[][] dctCrValues = applyDCT(n, false, CrValues);
					
					
					

					double[][] inverseDCTY = applyInverseDCT(n, true, dctYValues);
					double[][] inverseDCTCb = applyInverseDCT(n, false, dctCbValues);
					double[][] inverseDCTCr = applyInverseDCT(n, false, dctCrValues);
					
					
					supersample(inverseDCTY, inverseDCTCb,inverseDCTCr);
					
					sc.close();
				} // end of subsample function.
				 
				 
				 
				 
				 /**
					 *  Applies Supersample algorithm on YCbCr values to get RGB
					 * @param image
					 */
					static void supersample(double[][] y, double[][] cb, double[][] cr) {
						
						// Initialization
						int height, width;
						double[][] originalCb = new double[y.length][y[0].length];
						double[][] originalCr = new double[y.length][y[0].length];
						
						
						for(width=0; width<y.length; width+=2) {
							for(height=0; height<y[0].length; height+=2) {
								
								double cbvalue = cb[width/2][height/2];
								double crvalue = cr[width/2][height/2];
								
								originalCb[width][height] = cbvalue;
								originalCr[width][height] = crvalue;
							
								originalCb[width + 1][height] = cbvalue;
								originalCr[width + 1][height] = crvalue;
							
								originalCb[width][height + 1] = cbvalue;
								originalCr[width][height + 1] = crvalue;
							 
								originalCb[width + 1][height + 1] = cbvalue;
								originalCr[width + 1][height + 1] = crvalue;	
							
								
							} // height
						} // end width.
						
						Image image = new Image(y.length, y[0].length);
						double[] ycbcr = new double[3];
						for(width=0; width<y.length; width++) {
							for(height=0; height<y[0].length; height++) {
								ycbcr[0] = y[width][height] + 128;
								ycbcr[1] = originalCb[width][height] + .5;
								ycbcr[2] = originalCr[width][height] + .5;
								int[] rgb = convertToRGB(ycbcr);     // convert to RGB matrix equation.
								image.setPixel(width, height, rgb);
							}
						}
						
						Image newImage = reshapeToTheOriginalImageSize(image);
						newImage.display();
						
					} // supersample ends
					
					
					/**
					 * apply the equation to convert from YCbCr to RGB
					 * 
					 * @param RGBValues
					 * @return RGB values from YCbCr values
					 */
					static int[] convertToRGB(double[] yCbCr) {
						
						//Initialization
						double[] newRGBvalues = new double[3];
					
						newRGBvalues[0] = Math.round((yCbCr[0]* 1) + (yCbCr[1] * 0) + (yCbCr[2] * 1.4020));
						newRGBvalues[1] = Math.round((yCbCr[0] * 1) + (yCbCr[1] * -0.3441) + (yCbCr[2] * -0.7141));
						newRGBvalues[2] = Math.round((yCbCr[0] * 1) + (yCbCr[1] * 1.7720) + (yCbCr[2] * 0));
						
						if(newRGBvalues[0] > 255) newRGBvalues[0]=255;
						if(newRGBvalues[1] > 255) newRGBvalues[1]=255;
						if(newRGBvalues[2] > 255) newRGBvalues[2]=255;
						if(newRGBvalues[0] < 0) newRGBvalues[0]=0;
						if(newRGBvalues[1] < 0) newRGBvalues[1]=0;
						if(newRGBvalues[2] < 0) newRGBvalues[2]=0;

						int[] rgb = new int[3];
						rgb[0] = (int) newRGBvalues[0];
						rgb[1] = (int) newRGBvalues[1];
						rgb[2] = (int) newRGBvalues[2];
						
						return rgb;
					}  // calculateSupersample ends
					
					
					/**
					 * 
					 * @param YCbCrValues before DCT
					 * @param images 
					 * @return values of YCbCr after DCT
					 */
					static double[][] applyDCT(int n, boolean YorCbCr, double[][] YCbCrValues) {
						
						// Initialization
						int height, width, startH=0, startW=0, w=0, h=0, rounds=(YCbCrValues.length/8)*(YCbCrValues[0].length/8);
						double[][] arr8x8forDCT = new double[8][8];
						
						
						// start taking 8x8 arrays from the original image
						while(rounds > 0) {   														// the number of 8x8 2D arrays in the image HxW size 
							while(startW < YCbCrValues.length && startH < YCbCrValues[0].length) {   // till we reach the Width of the image.
								
								for(width=startW; width<startW+8;width++) {		// 0 - 7, 8 - 15, 16 - 23 . . . 
									for(height=startH; height<startH+8;height++) {
										arr8x8forDCT[w][h++] = YCbCrValues[width][height];
									}	// end width
									w++;
									h=0;
								} // end height 
								
								
									// apply DCT to 8x8 2D array.
									DCT.GFG dctObj = new DCT.GFG();
									arr8x8forDCT = dctObj.dctTransform(arr8x8forDCT);
									h=0;w=0;
							       
								//	Quantized(Fuv) = round(Fuv /Q’uv). Q’uv = Quv*2n
									
										// replace with the new values after DCT 
									for(width=startW; width<startW+8;width++) {		// 0 - 7, 8 - 15, 16 - 23 . . . 
										for(height=startH; height<startH+8;height++) {
											int hh = h++;
											if(YorCbCr) { 
												YCbCrValues[width][height] = Math.round(arr8x8forDCT[w][hh]/(luminY[w][hh]*Math.pow(2, n)));
											}else {
												YCbCrValues[width][height] = Math.round(arr8x8forDCT[w][hh]/(ChromCbCr[w][hh]*Math.pow(2, n)));
											}
											}	// end width
											w++;
											h=0;
										} // end height
									
									h=0;
									w=0;
									startH+=8;
							} // inner while
							
							startW+=8;
							startH=0;
							rounds--;
						} // rounds 
						
						
						return YCbCrValues;
					}
					
					
					/**
					 * applying the Inverse DCT algorithm on the image
					 * 
					 * @param YCbCrValues
					 * @param image
					 * @return YCbCr values after the DCT inverse
					 */
					static double[][] applyInverseDCT(int n, boolean YorCbCr, double[][] YCbCrValues){
					
						// Initialization
									int height, width, startH=0, startW=0, w=0, h=0, rounds=(YCbCrValues.length/8)*(YCbCrValues[0].length/8);
									double[][] arr8x8forDCT = new double[8][8];
									
									
									while(rounds > 0) {   // the number of 8x8 2D arrays in the image HxW size 
										while(startW < YCbCrValues.length && startH < YCbCrValues[0].length) {   // till we reach the Width of the image.
											
											for(width=startW; width<startW+8;width++) {		// 0 - 7, 8 - 15, 16 - 23 . . . 
												for(height=startH; height<startH+8;height++) {
													int hh = h++;
													if(YorCbCr) {
														arr8x8forDCT[w][hh] = YCbCrValues[width][height]*(luminY[w][hh]*Math.pow(2, n));
													}else {
														arr8x8forDCT[w][hh] = YCbCrValues[width][height]*(ChromCbCr[w][hh]*Math.pow(2, n));
													}
												}	// end width
												h=0;
												w++;
											} // end height 
											
											
												// apply DCT to 8x8 2D array.
												DCT.GFG dctObj = new DCT.GFG();
												arr8x8forDCT = dctObj.idct(arr8x8forDCT);
												h=0;w=0;
												
												// replace with the new values after DCT 
												for(width=startW; width<startW+8;width++) {		// 0 - 7, 8 - 15, 16 - 23 . . . 
													for(height=startH; height<startH+8;height++) {
															YCbCrValues[width][height] = arr8x8forDCT[w][h++];
														}	// end width
														w++;
														h=0;
													} // end height
												
												h=0;
												w=0;
												startH+=8;
										} // inner while
										
										startW+=8;
										startH=0;
										rounds--;
									} // rounds 
									
									
						return YCbCrValues;
					}
					/**
					 * 
					 * @param image
					 * @return newImage with original size
					 */
					static Image reshapeToTheOriginalImageSize(Image image) {
						//Initialization
						int originalW, originalH;
						originalW = multiple-widthDiff;
						originalH = multiple-heightDiff;
						Image newImage = new Image(image.getW()-originalW,image.getH()-originalH);
						int width, height;
						int[] rgb = new int[3];
						
						
						for(width=0;width<newImage.getW();width++) {
							for(height=0;height<newImage.getH();height++) {
								image.getPixel(width, height, rgb);
								newImage.setPixel(width, height, rgb);
							}
						}
						
						return newImage;
					}
		
	public static void main(String[] args) {
		readandresizetheimage(args[0]);
			
	}
}
