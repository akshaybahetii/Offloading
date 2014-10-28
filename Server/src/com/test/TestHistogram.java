/**
 * TestHistogram.java
 * com.test
 * Apr 5, 2014
 * BEHROOZ SHAHRIARI
 */

package com.test;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.server.adm.ADM;



/**
 * @author B. SHAHRIARI
 * @date Apr 5, 2014
 */
public class TestHistogram
{
	
	/**
	 * @date Apr 5, 2014
	 * @user B. SHAHRIARI
	 * @param args
	 * @return void
	 * @throws IOException
	 */
	public static void main( String[ ] args ) throws IOException
	{
	
		String image_name = "7.jpg";
		String path = "";
		if ( System.getProperty( "os.name" ).startsWith( "Windows" ) )
		{
			path = TestHistogram.class.getResource( image_name ).getPath( ).substring( 1 );
		}
		else
		{
			path = TestHistogram.class.getResource( image_name ).getPath( );
		}
		BufferedImage image = ImageIO.read( new File( path ) );
		int w = image.getWidth( );
		int h = image.getHeight( );
		int pixels[] = new int[w * h];
		image.getRGB( 0 , 0 , w , h , pixels , 0 , w );
		ADM adm = new ADM( );
		//change the visibility of getHistograms to public.
		//		Histogram histograms[] = adm.getHistograms( pixels );
		//		out.println( "Red_Histogram Green_Histogram Blue_Histogram" );
		//		for (int i = 0 ; i < histograms[0].bins.length ; ++i)
		//		{
		//			out.println( histograms[0].bins[i] + " " + histograms[1].bins[i] + " " +
		//								histograms[2].bins[i] );
		//		}
		//		out.println( );
	}
}
