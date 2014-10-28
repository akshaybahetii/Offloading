/**
 * TextClientFaceDetector.java
 * com.test
 * Apr 4, 2014
 * BEHROOZ SHAHRIARI
 */

package com.test;


import static java.lang.System.out;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.server.util.IP_Address;
import com.server.util.ImagePackage;
import com.server.util.ImagePackageHelper;



/**
 * @author B. SHAHRIARI
 * @date Apr 4, 2014
 */
public class TestClientFaceDetector
{
	
	/**
	 * 
	 */
	public TestClientFaceDetector( )
	{
	
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @date Apr 4, 2014
	 * @user B. SHAHRIARI
	 * @param args
	 * @return void
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main( String[ ] args ) throws IOException, ClassNotFoundException
	{
	
		String names[] =
		{
				"1.png",
				"1.jpg",
				"2.jpg",
				"3.jpg",
				"4.jpg"
		};
		Socket socket = new Socket( IP_Address.FACE_SERVER_IP , IP_Address.FACE_PORT );
		ObjectOutputStream outputStream = new ObjectOutputStream( socket.getOutputStream( ) );
		ObjectInputStream inputStream = new ObjectInputStream( socket.getInputStream( ) );
		
		//		out.println( "Sending number of images" );
		outputStream.writeInt( names.length );
		outputStream.flush( );
		ImagePackageHelper helper = new ImagePackageHelper( );
		//		out.println( "Sending Images..." );
		for (int i = 0 ; i < names.length ; ++i)
		{
			if ( System.getProperty( "os.name" ).startsWith( "Windows" ) )
			{
				outputStream.writeObject( helper.getImagePackage( TestClientFaceDetector.class
						.getResource( names[i] ).getPath( ).substring( 1 ) , names[i] ) );
			}
			else
			{
				outputStream.writeObject( helper.getImagePackage( TestClientFaceDetector.class
						.getResource( names[i] ).getPath( ) , names[i] ) );
			}
			outputStream.flush( );
			ImagePackage imagePackage = (ImagePackage) inputStream.readObject( );
			if ( System.getProperty( "os.name" ).startsWith( "Windows" ) )
			{
				helper.writeImage( imagePackage , TestClientFaceDetector.class.getResource( "" )
						.getPath( ).substring( 1 ) , "fd-" );
			}
			else
			{
				helper.writeImage( imagePackage , TestClientFaceDetector.class.getResource( "" )
						.getPath( ) , "fd-" );
			}
			long compute_time1 = inputStream.readLong( );
			out.println( "\nTime for " + names[i] + ": " + compute_time1 + " (ms)" );
		}
		long compute_time0 = inputStream.readLong( );
		out.println( "\nTotal Time: " + compute_time0 + " (ms)" );
		String msg = (String) inputStream.readObject( );
		if ( !msg.equals( "END" ) ) throw new IOException( "Server and Clinet are not Sync!" );
		inputStream.close( );
		outputStream.close( );
		socket.close( );
	}
}
