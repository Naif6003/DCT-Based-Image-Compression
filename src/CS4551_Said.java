public class CS4551_Said {
	
	static int multiple = 8;
	static int[] zeros = new int[3];
	static int[] originalImageObjRGB = new int[3];
	static int widthDiff, heightDiff; 
	
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
		if(imageObj.getW()%multiple != 0 & imageObj.getH()%multiple != 0) {
			 widthDiff = imageObj.getW()%8;
			 heightDiff = imageObj.getH()%8;
			
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
			convertFromRGBtoYCbCr(newImageObj); // if we resized the original image. (newImageObj)
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
			float[] rgb = new float[3];
			
			
			for(int width=0; width<image.getW(); width++){    		
				for(int height=0; height<image.getH(); height++) {
					image.getPixel(width, height, rgb);  
					float[] newYCbCr = calculatesubsampleYCbCr(rgb);// Find the YCbCr values.
					image.setPixel(width, height, newYCbCr);
				} 
			} // end for loop
			
			
// #######################  calling the functioin for Subsampling and applying DCT on the image. ######################### 
			
			subsample(image);   						       			// apply subsampling on it
			extractYCbCrValuesAndApplyDCT(image,flag);    				// applyDCT 
			
// ####################### apply the reverse functions on image ############################
		
			flag = false;
			extractYCbCrValuesAndApplyDCT(image,flag);    		    		// apply  inverseDCT
			supersample(image);										   	 // get back the RGB values from YCbCr values. (Supersample)
			
			float[] ycbcr = new float[3];
			for(int width=0; width<image.getW(); width++){    		
				for(int height=0; height<image.getH(); height++) {
					image.getPixel(width, height, ycbcr);  
					float[] rgbAfterYCbCr = calculateSupersample(ycbcr); 				// Find the YCbCr values.
					image.setPixel(width, height, rgbAfterYCbCr);
				} 
			} // end for loop
			
			
			Image finalImage = reshapeToTheOriginalImageSize(image);
			
			finalImage.display();
		} 												// end convertFromRGBtoYCbCr

		
		 /**
			 * Apply the equations to find the values for [Y][Cb][Cr]
			 * 
			 * @param rgb array values
			 * @return YCbCr array values 
			 */
			 static float[] calculatesubsampleYCbCr(float[] rgb) {
				 
				 // Initialization 
				 float[] newYCbCr = new float[3];
				 double[][] matrixValues = { {0.2990, 0.5870, 0.1140 },
					    					{-0.1687, -0.3313, 0.5000},
					    					{0.5000, -0.4187, -0.0813}};
				 
					
				newYCbCr[0] = (float) (matrixValues[0][0] * rgb[0] + matrixValues[0][1] * rgb[1] + matrixValues[0][2] * rgb[2]);
				newYCbCr[2] = (float) (matrixValues[1][0] * rgb[0] + matrixValues[1][1] * rgb[1] + matrixValues[1][2] * rgb[2]);
				newYCbCr[1] = (float) (matrixValues[2][0] * rgb[0] + matrixValues[2][1] * rgb[1] + matrixValues[2][2] * rgb[2]);
				
				newYCbCr[0] -= 128; 
				newYCbCr[1] -= 0.5;
				newYCbCr[2] -= 0.5;
				
//				if(newYCbCr[0] > 255) newYCbCr[0] = 255;
//				if(newYCbCr[0] < 0) newYCbCr[0] = 0;
//				
//				if(newYCbCr[1] > 127.5) newYCbCr[1] = (float) 127.5;
//				if(newYCbCr[1] < -125.5) newYCbCr[1] = (float) -127.5;
//				
//				if(newYCbCr[2] > 127.5) newYCbCr[2] = (float) 127.5;
//				if(newYCbCr[2] < -125.5) newYCbCr[2] = (float) -127.5;
				
				
//				System.out.println(newYCbCr[2] + " Y ");
//				System.out.println(newYCbCr[1] + " Cb ");
//				System.out.println(newYCbCr[2] + " Cr ");
				
				return newYCbCr; 
			} // end function calculateYCbCr
			 
			 /**
				 * apply the equation to convert from YCbCr to RGB
				 * 
				 * @param YCbCrValues
				 * @return RGB values from YCbCr values
				 */
				static float[] calculateSupersample(float[] YCbCrValues) {
					
					//Initialization
					float[] rgbValues = new float[3];
					double[][] matrixValues = {
												{1.0000 , 0 , 1.4020},
												{1.0000 , -0.3441 , -0.7141},
												{1.0000 , 1.7720, 0 }
											};
					
					YCbCrValues[0] += 127;
					YCbCrValues[1] += 0.5;
					YCbCrValues[2] += 0.5;
					
					rgbValues[0] = (float) (matrixValues[0][0] * YCbCrValues[0] + matrixValues[0][1] * YCbCrValues[1] + matrixValues[0][2] * YCbCrValues[2]);
					rgbValues[2] = (float) (matrixValues[1][0] * YCbCrValues[0] + matrixValues[1][1] * YCbCrValues[1] + matrixValues[1][2] * YCbCrValues[2]);
					rgbValues[1] = (float) (matrixValues[2][0] * YCbCrValues[0] + matrixValues[2][1] * YCbCrValues[1] + matrixValues[2][2] * YCbCrValues[2]);
					
					
//						System.out.println(rgbValues[0] + " Y ");
//						System.out.println(rgbValues[1] + " Cb ");
//						System.out.println(rgbValues[2] + " Cr ");
					
					if(rgbValues[0] > 255) rgbValues[0]=255;
					if(rgbValues[1] > 255) rgbValues[1]=255;
					if(rgbValues[2] > 255) rgbValues[2]=255;
					if(rgbValues[0] < 0) rgbValues[0]=0;
					if(rgbValues[1] < 0) rgbValues[1]=0;
					if(rgbValues[2] < 0) rgbValues[2]=0;
					
					
					return rgbValues;
				}  // calculateSupersample ends
			 
		 
			 /**
				 * apply subsample 4:2:0 algorithm and show the image.
				 * 
				 * @param Image image
				 */
				 static void subsample(Image image) {
					// Initialization
					float[] YCbCr1 = new float[3];
					float[] YCbCr2 = new float[3];
					float[] YCbCr3 = new float[3];
					float[] YCbCr4 = new float[3];
					
					
					for(int width=0; width<image.getW(); width+=2) {
						for(int height=0; height<image.getH(); height+=2) {
							image.getPixel(width, height, YCbCr1);
							
							if(height+1 < image.getH())
								image.getPixel(width, height+1, YCbCr2);
								
							if(width+1 <image.getW())
								image.getPixel(width+1, height, YCbCr3);
								
							if(height+1 < image.getH() && width+1 < image.getW())
								image.getPixel(width+1, height+1, YCbCr4);
								

							float avrCb = (YCbCr1[1]+YCbCr2[1]+YCbCr3[1]+YCbCr4[1])/4;
							float avrCr = (YCbCr1[2]+YCbCr2[2]+YCbCr3[2]+YCbCr4[2])/4;
							
							YCbCr1[1] = avrCr;
							YCbCr1[2] = avrCb;
							
							image.setPixel(width, height, YCbCr1);
						} // height
					} // end width.
					
				} // end of subsample function.
			 
			 
			 
		 /**
			 *  Applies Supersample algorithm on YCbCr values to get RGB
			 * @param image
			 */
			static void supersample(Image image) {
				
				// Initialization
				int height, width;
				float[] YCbCr1 = new float[3];
				float[] YCbCr2 = new float[3];
				float[] YCbCr3 = new float[3];
				float[] YCbCr4 = new float[3];
				
				
				for(width=0; width<image.getW(); width+=2) {
					for(height=0; height<image.getH(); height+=2) {
						image.getPixel(width, height, YCbCr1);
						
						if(height+1 < image.getH())
							image.getPixel(width, height+1, YCbCr2);
							
						if(width+1 <image.getW())
							image.getPixel(width+1, height, YCbCr3);
							
						if(height+1 < image.getH() && width+1 < image.getW())
							image.getPixel(width+1, height+1, YCbCr4);
						
						YCbCr2[1] = YCbCr1[2];
						YCbCr2[2] = YCbCr1[1];
						YCbCr3[1] = YCbCr1[2];
						YCbCr3[2] = YCbCr1[1];
						YCbCr4[1] = YCbCr1[2];
						YCbCr4[2] = YCbCr1[1];
						
						
						if(height+1 < image.getH())
							image.setPixel(width, height+1, YCbCr2);
							
						if(width+1 <image.getW())
							image.setPixel(width+1, height, YCbCr3);
							
						if(height+1 < image.getH() && width+1 < image.getW())
							image.setPixel(width+1, height+1, YCbCr4);
						
						
						
					} // height
				} // end width.
				
			} // supersample ends
			
		
		/**
		 *  This method will create a 2D array for each Y, Cb, Cr 
		 *  then apply DCT or InverseDCT
		 *  
		 * @param image
		 * @return Image object after applying DCT
		 */
		static void extractYCbCrValuesAndApplyDCT(Image image, boolean flag) {
			
			// Initialization
			int height,width;
			float[][] Y2dvalues = new float[image.getW()][image.getH()];
			float[][] Cd2dvalues = new float[image.getW()][image.getH()];
			float[][] Cr2dvalues = new float[image.getW()][image.getH()];
			int[] YCbCrpixels = new int[3];
			float[][] newY2dvalues = new float[image.getW()][image.getH()];
			float[][] newCb2dvalues = new float[image.getW()][image.getH()];
			float[][] newCr2dvalues = new float[image.getW()][image.getH()];
			
			
			// extract the pixels values from the image Y, Cb , Cr
			for(width=0; width<image.getW(); width++) {
					for(height=0;height<image.getH();height++) {
						image.getPixel(width, height, YCbCrpixels);
								Y2dvalues[width][height] = YCbCrpixels[0];
								Cd2dvalues[width][height] = YCbCrpixels[1];
								Cr2dvalues[width][height] = YCbCrpixels[2];
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
	
			width=0;height=0;
//		 	Return the new values to the original image. 
			for(width=0; width<image.getW(); width++) {
					for(height=0;height<image.getH();height++) {
						image.getPixel(width, height, YCbCrpixels);
								YCbCrpixels[0] = (int) newY2dvalues[width][height];
								YCbCrpixels[2] = (int) newCb2dvalues[width][height];
								YCbCrpixels[1] = (int) newCr2dvalues[width][height];
								
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
		static float[][] applyDCT(float[][] YCbCrValues, Image image) {
			
			// Initialization
			int height, width, startH=0, startW=0, w=0, h=0, rounds=(image.getH()/8)*(image.getW()/8);
			float[][] arr8x8forDCT = new float[8][8];
			
			
			// start taking 8x8 arrays from the original image
			while(rounds > 0) {   														// the number of 8x8 2D arrays in the image HxW size 
				while(startW <  image.getW() && startH < image.getH()) {   // till we reach the Width of the image.
					
					for(width=startW; width<startW+8;width++) {		// 0 - 7, 8 - 15, 16 - 23 . . . 
						for(height=startH; height<startH+8;height++) {
							arr8x8forDCT[w][h++] = YCbCrValues[width][height];
						}	// end width
						w++;
						h=0;
					} // end height 
					
					
						// apply DCT to 8x8 2D array.
						DCT.GFG dctObj = new DCT.GFG();
						dctObj.dctTransform(arr8x8forDCT);
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
		 * applying the Inverse DCT algorithm on the image
		 * 
		 * @param YCbCrValues
		 * @param image
		 * @return YCbCr values after the DCT inverse
		 */
		static float[][] applyInverseDCT(float[][] YCbCrValues, Image image){
		
			// Initialization
						int height, width, startH=0, startW=0, w=0, h=0, rounds=(image.getH()/8)*(image.getW()/8);
						float[][] arr8x8forDCT = new float[8][8];
						
						
						while(rounds > 0) {   // the number of 8x8 2D arrays in the image HxW size 
							while(startW < image.getW() && startH < image.getH()) {   // till we reach the Width of the image.
								
								for(width=startW; width<startW+8;width++) {		// 0 - 7, 8 - 15, 16 - 23 . . . 
									for(height=startH; height<startH+8;height++) {
										arr8x8forDCT[w][h++] = YCbCrValues[width][height];
									}	// end width
									h=0;
									w++;
								} // end height 
								
								
									// apply DCT to 8x8 2D array.
									DCT.GFG dctObj = new DCT.GFG();
									dctObj.inverseDCT(arr8x8forDCT);
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
