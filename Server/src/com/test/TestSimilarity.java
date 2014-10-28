/**
 * DONiiA
 * TestSimilarity.java
 * com.server.adm
 * Apr 5, 2014
 * BEHROOZ SHAHRIARI
 */

package com.test;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.server.adm.ADM;
import com.server.adm.DNode;
import com.server.util.PackageData;
import com.server.util.SampleData;



/**
 * @author B. SHAHRIARI
 * @date Apr 5, 2014
 */
public class TestSimilarity
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
	
		ADM adm = new ADM( );
		SampleData data1, data2;
		DNode node1, node2;
		{
			String image_name = "7.jpg";
			data1 = new SampleData( image_name );
			String path = "";
			if ( System.getProperty( "os.name" ).startsWith( "Windows" ) )
			{
				path = TestHistogram.class.getResource( image_name ).getPath( ).substring( 1 );
			}
			else
			{
				path = TestHistogram.class.getResource( image_name ).getPath( );
			}
			File file = new File( path );
			data1.data_size = (int) ( file.length( ) / 1024 );
			BufferedImage image = ImageIO.read( new File( path ) );
			int w = image.getWidth( );
			int h = image.getHeight( );
			int pixels[] = new int[w * h];
			image.getRGB( 0 , 0 , w , h , pixels , 0 , w );
			data1.H = h;
			data1.W = w;
			data1.sample_pixels = pixels;
			PackageData packageData = new PackageData( null , true , 75 , 80 , 90 );
			
			//change the visibility of makeDNode to public.
			//			node1 = adm.makeDNode( data1 , packageData );
		}
		{
			String image_name = "3.jpg";
			data2 = new SampleData( image_name );
			String path = "";
			if ( System.getProperty( "os.name" ).startsWith( "Windows" ) )
			{
				path = TestHistogram.class.getResource( image_name ).getPath( ).substring( 1 );
			}
			else
			{
				path = TestHistogram.class.getResource( image_name ).getPath( );
			}
			File file = new File( path );
			data2.data_size = (int) ( file.length( ) / 1024 );
			BufferedImage image = ImageIO.read( new File( path ) );
			int w = image.getWidth( );
			int h = image.getHeight( );
			int pixels[] = new int[w * h];
			image.getRGB( 0 , 0 , w , h , pixels , 0 , w );
			data2.H = h;
			data2.W = w;
			data2.sample_pixels = pixels;
			PackageData packageData = new PackageData( null , true , 70 , 15 , 88 );
			//change the visibility of makeDNode to public.
			//			node2 = adm.makeDNode( data2 , packageData );
		}
		/*
		 * uncomment and change the function to public
		 */
		//		out.println( adm.getSimilarity( node1 , node2 ) );
	}
}
