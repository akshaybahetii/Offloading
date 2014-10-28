/**
 * TestImagePackageHelper.java
 * com.test
 * Apr 4, 2014
 * BEHROOZ SHAHRIARI
 */

package com.test;


import java.io.IOException;

import com.server.util.ImagePackage;
import com.server.util.ImagePackageHelper;



/**
 * @author B. SHAHRIARI
 * @date Apr 4, 2014
 */
public class TestImagePackageHelper
{
	
	/**
	 *
	 */
	public TestImagePackageHelper( )
	{
	
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @date Apr 4, 2014
	 * @user B. SHAHRIARI
	 * @param args
	 * @return void
	 */
	public static void main( String[ ] args )
	{
	
		String image_name = "2.jpg";
		ImagePackageHelper helper = new ImagePackageHelper( );
		try
		{
			ImagePackage imagePackage = helper.getImagePackage( TestImagePackageHelper.class
					.getResource( image_name ).getPath( ).substring( 1 ) , image_name );
			helper.writeImage( imagePackage , TestImagePackageHelper.class.getResource( "" ).getPath( )
					.substring( 1 ) , "" );
		} catch ( IOException e )
		{
			e.printStackTrace( );
		}
		
	}
	
}
