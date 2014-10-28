/**
 * 
 */

package com.cs218.taz;


import com.server.util.PackageData;
import com.server.util.PackageDataHelper;
import com.server.util.SampleData;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;



/**
 * @author Behrooz
 * 
 */
public class ExtractSample
{
	
	/**
	 * 
	 */
	public ExtractSample( )
	{
	
	}
	
	/**
	 * This function receive a list of path to images and re-sample and make a
	 * bitmap for each one, then collect a sample data of its pixels' data. We
	 * need this sample data for decision maker to form a histogram based on the
	 * data to make a decision.
	 * 
	 * @param paths
	 *           a list if path for each image in the list.
	 * @param imgNames
	 * @return
	 */
	public PackageData getSampleData( String paths[] , String imgNames[] , boolean feeback ,
			int btr_level )
	{
	
		BitmapFactory.Options options = new BitmapFactory.Options( );
		PackageDataHelper helper = new PackageDataHelper( imgNames , feeback , btr_level );
		for (String p : paths)
		{
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile( p , options );
			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize( options , 200 , 200 );
			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			Bitmap bitmap = BitmapFactory.decodeFile( p , options );
			int w = bitmap.getWidth( );
			int h = bitmap.getHeight( );
			helper.add_PixelSample( readPixels( bitmap , 10 , w , h ) , w , h , bitmap.getByteCount( ) );
		}
		return helper.getPackageData( );
	}
	
	/**
	 * 
	 * @param bitmap
	 * @param steps
	 * @return
	 */
	private int[ ] readPixels( Bitmap bitmap , int steps , int w , int h )
	{
	
		int pixels[] = new int[( 1 + ( w / steps ) ) * ( 1 + ( h / steps ) )];
		int id = 0;
		for (int x = steps / 2 ; x < w ; x += steps)
		{
			for (int y = steps / 2 ; y < h ; y += steps)
			{
				int rgb = bitmap.getPixel( x , y );
				pixels[id] = rgb;
				id++;
			}
		}
		return pixels;
	}
	
	/**
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private int calculateInSampleSize( BitmapFactory.Options options , int reqWidth , int reqHeight )
	{
	
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if ( height > reqHeight || width > reqWidth )
		{
			
			final int halfHeight = height / 1;
			final int halfWidth = width / 1;
			
			// Calculate the largest inSampleSize value that is a power of 2
			// and
			// keeps both
			// height and width larger than the requested height and width.
			while ( ( halfHeight / inSampleSize ) > reqHeight &&
						( halfWidth / inSampleSize ) > reqWidth )
			{
				inSampleSize *= 2;
			}
		}
		
		return inSampleSize;
	}
	
}
