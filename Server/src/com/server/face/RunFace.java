/**
 * RunFace.java
 * com.fe
 * Apr 4, 2014
 * BEHROOZ SHAHRIARI
 */

package com.server.face;


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

import org.opencv.core.Core;



/**
 * @author B. SHAHRIARI
 * @date Apr 4, 2014
 */
public class RunFace
{
	
	static
	{
		try
		{
			URL url = RunFace.class.getProtectionDomain( ).getCodeSource( ).getLocation( ); //Gets the path
			String jarPath = null;
			try
			{
				jarPath = URLDecoder.decode( url.getFile( ) , "UTF-8" ); //Should fix it to be read correctly by the system
			} catch ( UnsupportedEncodingException e )
			{
				e.printStackTrace( );
			}
			
			String parentPath = new File( jarPath ).getParentFile( ).getPath( ); //Path of the jar
			parentPath = parentPath + File.separator;
			
			System.out.println( "Path: " + parentPath );
			System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
			
		} catch ( UnsatisfiedLinkError e )
		{
			System.err.println( "Native code library failed to load.\n" + e );
			System.exit( 1 );
		}
	}
	
	
	/**
	 * @date Apr 4, 2014
	 * @user B. SHAHRIARI
	 * @param args
	 * @return void
	 */
	public static void main( String[ ] args )
	{
	
		new Thread( new FaceServer( ) ).start( );
	}
}
