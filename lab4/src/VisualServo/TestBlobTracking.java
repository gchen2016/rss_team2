package VisualServo;

import java.awt.Color;


public class TestBlobTracking extends MultipleBlobTracking {

	public TestBlobTracking(int width, int height){
		super(width,height);	
	}
	public double[] apply(Image src, Image dest, int testIndex) {
		stepTiming(); 
		
		if (useGaussianBlur) {// (Solution)
			byte[] srcArray = src.toArray();// (Solution)
			byte[] destArray = new byte[srcArray.length]; // (Solution)
			if (approximateGaussian) { // (Solution)
				GaussianBlur.applyBox(srcArray, destArray, src.getWidth(), src.getHeight());
			} // (Solution)
			else { // (Solution)
				GaussianBlur.apply(srcArray, destArray, width, height); // (Solution)
			} // (Solution)
			src = new Image(destArray, src.getWidth(), src.getHeight()); // (Solution)
		}
		
		blobPixel(src, multiBlobPixelMask); //(Solution)
		blobPresent(multiBlobPixelMask, multiImageConnected, multiBlobMask); //(Solution)

		if (dest != null) { // (Solution)
			//dest = Histogram.getHistogram(src, dest, true); // (Solution)
			markBlob(src, dest); // (Solution)
		} // (Solution)
		findFiducial(multiBlobMask,dest);

		for(int i = 0; i < multiTargetDetected.length; i++){
			int x = (int)centroidX[i];
			int y = (int)centroidY[i];
			if (x>10 && y>10){
				for(int j = 0; j< 10; j++){
					for(int k = 0; k< 10; k++){				
						dest.setPixel(x+j, y+k, (byte) 0,(byte) 0,(byte) 0);
					}
				}
			}
		}	
				return centroidX;
}


}
