/**
 * ImagePackage.java
 * com.server.util
 * Apr 4, 2014
 * BEHROOZ SHAHRIARI
 */

package com.server.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;



/**
 * @author B. SHAHRIARI
 * @date Apr 4, 2014
 */
public class ImagePackageHelper
{
	
	/**
	 * 
	 * @date Apr 4, 2014
	 * @user B. SHAHRIARI
	 * @param path
	 * @param img_name
	 * @throws IOException
	 * @return ImagePackage
	 */
	public ImagePackage getImagePackage( String path , String img_name ) throws IOException
	{
	
		//convert file into array of bytes
		File file = new File( path );
		byte[ ] data = new byte[(int) file.length( )];
		FileInputStream fileInputStream = new FileInputStream( file );
		fileInputStream.read( data );
		fileInputStream.close( );
		return new ImagePackage( data , img_name , img_name.substring( img_name.length( ) - 3 )
				.toLowerCase( ) );
	}
	
	/**
	 * 
	 * @date Apr 4, 2014
	 * @user B. SHAHRIARI
	 * @param imagePackage
	 * @param dir
	 * @param prefix
	 * @return
	 * @throws IOException
	 * @return String
	 */
	public String writeImage( ImagePackage imagePackage , String dir , String prefix )
			throws IOException
	{
	
		//convert array of bytes into file
		FileOutputStream fileOuputStream = new FileOutputStream( dir + File.separator + prefix +
																					imagePackage.image_name );
		fileOuputStream.write( imagePackage.data );
		fileOuputStream.flush( );
		fileOuputStream.close( );
		return dir + File.separator + prefix + imagePackage.image_name;
	}
	
}
