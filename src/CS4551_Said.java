public class CS4551_Said {
	
	private static int multiple = 8;
	private static int[] zeros = new int[3];
	private static int[] originalImageObjRGB = new int[3];
	private static int[] YCbCr = new int[3];
	/** 
	 * resize the image if it's not multiple of 8 in W and H.
	 * if not, the image will be increased with Zeros values. 
	 * 
	 * @param imageName
	 * 
	 */
	 static void readandresizetheimage(String imageName) {
		
		Image imageObj = new Image(imageName); // reading the image from the command line argu
		
		// make sure the image is multiple of 8 in W and H
		if(imageObj.getW()%multiple != 0 || imageObj.getH()%multiple != 0) {
			int widthDiff = imageObj.getW()%8;
			int heightDiff = imageObj.getH()%8;
			
			int newW = imageObj.getW()+(multiple-widthDiff);    // resize the image W
			int newH = imageObj.getH()+(multiple-heightDiff);   // resize the image H
			
			Image newImageObj = new Image(newW,newH);  // create the new resized image.
			
			for(int width=0; width<newW; width++) {
				for(int height=0; height<newH; height++) {
					if(width > imageObj.getW()-1 || height > imageObj.getH()-1) {  // if the pixel position is new then 0
						newImageObj.setPixel(width, height, zeros);
					}else {   												 // else we have the original image pixels values.
						imageObj.getPixel(width, height, originalImageObjRGB);
						newImageObj.setPixel(width, height, originalImageObjRGB);
					} // end of if
				}  // end of height
			} // end of width
			
			convertFromRGBtoYCbCr(newImageObj);   // if we resized the original image. (newImageObj)
		}else { 
			convertFromRGBtoYCbCr(imageObj);          // if we didn't resize the original image (imageObj)
		} // resize image (if)
	} // function ends 
	
	/**
	 * Algorithm for converting from RGB to YCbCr. 
	 * @param image
	 */
		 static void convertFromRGBtoYCbCr(Image image) {
			
			 // Initialization
			 boolean flag = true;
			
			int[] rgb = new int[3];
			for(int width=0; width<image.getW(); width++){    		// loop over all the pixels in the image.
				for(int height=0; height<image.getH(); height++) {
					image.getPixel(width, height, rgb);  
					int[] newYCbCr = calculateYCbCr(rgb);       // Find the YCbCr values.
					image.setPixel(width, height, newYCbCr);
				} 
			} // end for loop
			
			
			// #######################  calling the functioin for Subsampling and applying DCT on the image. ######################### 
			
			subsample(image);   						// apply subsampling on it
			extractYCbCrValues(image,flag);    				// applyDCT 
			
			// ####################### apply the reverse functions on image ############################
			
			flag = false;
			extractYCbCrValues(image,flag);    				// apply  inverseDCT
			supersample(image); 								// get back the RGB values from YCbCr values. (Supersample)
			
			image.display();
		} 												// end convertFromRGBtoYCbCr
		
		/**
		 * Apply the equations to find the values for [Y][Cb][Cr]
		 * 
		 * @param rgb array values
		 * @return YCbCr array values 
		 */
		 static int[] calculateYCbCr(int[] rgb) {
			double[][] matrixValues = { {0.2990, 0.5870, 0.1140 },
				    					{-0.1687, -0.3313, 0.5000},
				    					{0.5000, -0.4187, -0.0813}
				    												};
			    
			
			YCbCr[0] = (int) (matrixValues[0][0] * rgb[0] + matrixValues[0][1] * rgb[1] + matrixValues[0][2] * rgb[2]);
			YCbCr[1] = (int) (matrixValues[1][0] * rgb[0] + matrixValues[1][1] * rgb[1] + matrixValues[1][2] * rgb[2]);
			YCbCr[2] = (int) (matrixValues[2][0] * rgb[0] + matrixValues[2][1] * rgb[1] + matrixValues[2][2] * rgb[2]);
			
			// Subtract 128 from Y and 0.5 form Cb and Cr to make all of them in the range of [-128,127]						
			YCbCr[0] -= 128;   // Y
			YCbCr[1] -= 0.5;   // Cb 
			YCbCr[2] -= 0.5;   // Cr
			
			return YCbCr; 
		} // end function calculateYCbCr

		
		
		/**
		 * 	apply subsample 4:2:0 algorithm and show the image. 
		 * 
		 * @param YCbCr
		 * 
		 */
		 static void subsample(Image image) {
			
			int[] YCbCrcolor = new int[3];
			int[] YCbCr = new int[3];
			for(int width=0; width<image.getW(); width+=2) {
				for(int height=0; height<image.getH(); height+=2) {
					image.getPixel(width, height, YCbCrcolor);
					
					if(height+1 < image.getH())
						image.getPixel(width+1, height, YCbCr);
						YCbCr[1] = YCbCrcolor[1];
						YCbCr[2] = YCbCrcolor[2];
						image.getPixel(width+1, height, YCbCr);
					
					if(height+1 <image.getW())
						image.getPixel(width, height+1, YCbCr);
						YCbCr[1] = YCbCrcolor[1];
						YCbCr[2] = YCbCrcolor[2];
						image.setPixel(width, height+1, YCbCr);
						
					if(height+1 < image.getH() && width+1 < image.getW())
						image.getPixel(width+1, height+1, YCbCr);
						YCbCr[1] = YCbCrcolor[1];
						YCbCr[2] = YCbCrcolor[2];
						image.setPixel(width+1, height+1, YCbCr);

				} // height
			} // end width.
			
		} // end of subsample function.
		
		
		/**
		 *  This method will create a 2D array for each Y, Cb, Cr 
		 * @param image
		 * @return Image object after applying DCT
		 */
		static void extractYCbCrValues(Image image, boolean flag) {
			
			// Initialization
			int height,width=0, h, w;
			double[][] Y2dvalues = new double[image.getH()][image.getW()];
			double[][] Cd2dvalues = new double[image.getH()][image.getW()];
			double[][] Cr2dvalues = new double[image.getH()][image.getW()];
			int[] YCbCrpixels = new int[3];
			double[][] newY2dvalues = new double[image.getH()][image.getW()];
			double[][] newCb2dvalues = new double[image.getH()][image.getW()];
			double[][] newCr2dvalues = new double[image.getH()][image.getW()];
			
			// extract the pixels values from the image Y, Cb , Cr
			for(height=0; height<image.getH(); height++) {
					for(width=0;width<image.getW();width++) {
						image.getPixel(width, height, YCbCrpixels);
								Y2dvalues[height][width] = YCbCrpixels[0];
								Cd2dvalues[height][width] = YCbCrpixels[1];
								Cr2dvalues[height][width] = YCbCrpixels[2];
					}
				}
			
			
			// apply DCT if flag is true or false for inverseDCT.
			if(flag) {
				newY2dvalues = applyDCT(Y2dvalues,image);
				newCb2dvalues = applyDCT(Cd2dvalues,image);
				newCr2dvalues = applyDCT(Cr2dvalues,image);
			}else {
				newY2dvalues = applyInverseDCT(Y2dvalues,image);
				newCb2dvalues = applyInverseDCT(Cd2dvalues,image);
				newCr2dvalues = applyInverseDCT(Cr2dvalues,image);
			}			
			
			
//		 	Return the new values to the original image. 
			for(height=0; height<image.getH(); height++) {
					for(width=0;width<image.getW();width++) {
						image.getPixel(width, height, YCbCrpixels);
								YCbCrpixels[0] = (int) newY2dvalues[height][width];
								YCbCrpixels[1] = (int) newCb2dvalues[height][width];
								YCbCrpixels[2] = (int) newCr2dvalues[height][width];
						image.setPixel(width, height, YCbCrpixels);
					}
				}
				
		} // end of extractYCbCrValues function 
		
		/**
		 * 
		 * @param YCbCrValues before DCT
		 * @param images 
		 * @return values of YCbCr after DCT
		 */
		static double[][] applyDCT(double[][] YCbCrValues, Image image) {
			
			// Initialization
			int height, width, startH=0, startW=0, w=0, h=0, rounds=(image.getH()/8)*(image.getW()/8);
			double[][] arr8x8forDCT = new double[8][8];
			
			
			while(rounds >= 0) {   // the number of 8x8 2D arrays in the image HxW size 
				while(startW+8 <= image.getW() && startH+8 <= image.getH()) {   // till we reach the Width of the image.
					
					for(height=startH; height<startH+8;height++) {			// 0 - 7, 8 - 15, 16 - 23 . . .
						for(width=startW; width<startW+8;width++) {
							arr8x8forDCT[h][w++] = YCbCrValues[height][width];
						}	// end width
						h++;
						w=0;
					} // end height 
					
					
						// apply DCT to 8x8 2D array.
						DCT.GFG dctObj = new DCT.GFG();
						double[][] newYCbCrvalues = dctObj.dctTransform(arr8x8forDCT);
						h=0;w=0;
						
							// replace with the new values after DCT 
							for(height=startH; height<startH+8;height++) {		// 0 - 7, 8 - 15, 16 - 23 . . . 
								for(width=startW; width<startW+8;width++) {
									YCbCrValues[height][width] = newYCbCrvalues[h][w++];
								}	// end width
								w=0;
								h++;
							} // end height
						
						h=0;
						startW+=8;
				} // inner while
				
			startH+=8;
			startW=0;
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
		static double[][] applyInverseDCT(double[][] YCbCrValues, Image image){
		
			// Initialization
						int height, width, startH=0, startW=0, w=0, h=0, rounds=(image.getH()/8)*(image.getW()/8);
						double[][] arr8x8forDCT = new double[8][8];
						
						
						while(rounds >= 0) {   // the number of 8x8 2D arrays in the image HxW size 
							while(startW+8 <= image.getW() && startH+8 <= image.getH()) {   // till we reach the Width of the image.
								
								for(height=startH; height<startH+8;height++) {			// 0 - 7, 8 - 15, 16 - 23 . . .
									for(width=startW; width<startW+8;width++) {
										arr8x8forDCT[h][w++] = YCbCrValues[height][width];
									}	// end width
									h++;
									w=0;
								} // end height 
								
								
									// apply DCT to 8x8 2D array.
									DCT.GFG dctObj = new DCT.GFG();
									double[][] newYCbCrvalues = dctObj.inverseDCT(arr8x8forDCT);
									h=0;w=0;
									System.out.println(startH + " this is startH ");
										// replace with the new values after DCT 
										for(height=startH; height<startH+8;height++) {		// 0 - 7, 8 - 15, 16 - 23 . . . 
											for(width=startW; width<startW+8;width++) {
												YCbCrValues[height][width] = newYCbCrvalues[h][w++];
											}	// end width
											w=0;
											h++;
										} // end height
									
									h=0;
									startW+=8;
							} // inner while
							
						startH+=8;
						startW=0;
						rounds--;
						} // rounds 
			
			
			
			
			return YCbCrValues;
		}
		
		
		
		/**
		 *  Applies Supersample algorithm on YCbCr values to get RGB
		 * @param image
		 */
		static void supersample(Image image) {
			
			// Initialization
			int[] YCbCrValues = new int[3];
			int height, width;
			
			// assign the new values to the image
			for(width=0; width<image.getW(); width++) {
				for(height=0; height<image.getH(); height++) {
					image.getPixel(width, height, YCbCrValues);
					int[] rgb = calculateSupersample(YCbCrValues);
					image.setPixel(width, height, rgb);
				}
			}
			
		} // supersample ends
		
		
		
		
		/**
		 * apply the equation to convert from YCbCr to RGB
		 * 
		 * @param YCbCrValues
		 * @return RGB values from YCbCr values
		 */
		static int[] calculateSupersample(int[] YCbCrValues) {
			
			//Initialization
			int[] rgbValues = new int[3];
			double[][] matrixValues = {
					{1.0000 , 0 , 1.4020},
					{1.0000 , -0.3441 , -0.7141},
					{1.0000 , 1.7720, 0 }
			};
			
			
			YCbCrValues[0] += 128;
			YCbCrValues[1] += 0.5;
			YCbCrValues[2] += 0.5;
			
			rgbValues[0] = (int) (matrixValues[0][0] * YCbCrValues[0] + matrixValues[0][1] * YCbCrValues[1] + matrixValues[0][2] * YCbCrValues[2]);
			rgbValues[1] = (int) (matrixValues[1][0] * YCbCrValues[0] + matrixValues[1][1] * YCbCrValues[1] + matrixValues[1][2] * YCbCrValues[2]);
			rgbValues[2] = (int) (matrixValues[2][0] * YCbCrValues[0] + matrixValues[2][1] * YCbCrValues[1] + matrixValues[2][2] * YCbCrValues[2]);
			
			if(rgbValues[0] > 255) rgbValues[0]=255;
			if(rgbValues[1] > 255) rgbValues[1]=255;
			if(rgbValues[2] > 255) rgbValues[2]=255;
			
			
			return rgbValues;
		}  // calculateSupersample ends
		
	public static void main(String[] args) {
		readandresizetheimage(args[0]);
			
	}
}
