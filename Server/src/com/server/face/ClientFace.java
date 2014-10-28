/**
 * ClientFace.java
 * com.fe
 * Apr 4, 2014
 * BEHROOZ SHAHRIARI
 */

package com.server.face;


import static java.lang.System.currentTimeMillis;
import static java.lang.System.out;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.highgui.Highgui;
import org.opencv.objdetect.CascadeClassifier;

import com.server.util.ImagePackage;
import com.server.util.ImagePackageHelper;



/**
 * @author B. SHAHRIARI
 * @date Apr 4, 2014
 */
public class ClientFace
		implements Runnable
{
	
	String					source;
	
	private Socket			clientSocket;
	
	private InputStream	inputStream		= null;
	
	private OutputStream	outputStream	= null;
	
	
	/**
	 * @param clientSocket
	 * 
	 */
	public ClientFace(Socket clientSocket)
	{
	
		this.clientSocket = clientSocket;
		source = clientSocket.getInetAddress( ) + "-" + clientSocket.getPort( );
		out.println( "New connection " + source );
		try
		{
			inputStream = clientSocket.getInputStream( );
			outputStream = clientSocket.getOutputStream( );
		} catch ( IOException e )
		{
			e.printStackTrace( );
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run( )
	{
	
		long time = currentTimeMillis( );
		try
		{
			process( );
			inputStream.close( );
			outputStream.flush( );
			outputStream.close( );
			clientSocket.close( );
		} catch ( IOException e )
		{
			e.printStackTrace( );
		} catch ( ClassNotFoundException e )
		{
			e.printStackTrace( );
		}
		time = currentTimeMillis( ) - time;
		out.println( "Request processed: " + time + " (ms)" );
	}
	
	/**
	 * 
	 * @date Apr 4, 2014
	 * @user B. SHAHRIARI
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @return void
	 */
	private void process( ) throws IOException, ClassNotFoundException
	{
	
		long time0 = currentTimeMillis( );
		
		ObjectOutputStream objectOutputStream = new ObjectOutputStream( outputStream );
		ObjectInputStream objectInputStream = new ObjectInputStream( inputStream );
		
		int number_ImagePackages = objectInputStream.readInt( );
		//		out.println( "Num Images: " + number_ImagePackages );
		ImagePackage imagePackage = null;
		File root = new File( ClientFace.class.getProtectionDomain( ).getCodeSource( ).getLocation( )
				.getPath( ) ).getParentFile( );
		String dir = root.getAbsolutePath( );
		File dirFile = new File( dir + File.separator + source );
		if ( !dirFile.exists( ) ) dirFile.mkdir( );
		//		out.println( dirFile );
		
		CascadeClassifier faceDetector1 = null;
		CascadeClassifier faceDetector2 = null;
		CascadeClassifier faceDetector3 = null;
		
		if ( System.getProperty( "os.name" ).startsWith( "Windows" ) )
		{
			faceDetector1 = new CascadeClassifier( ClientFace.class
					.getResource( "haarcascade_frontalface_alt.xml" ).getPath( ).substring( 1 ) );
			faceDetector2 = new CascadeClassifier( ClientFace.class
					.getResource( "haarcascade_frontalface_alt2.xml" ).getPath( ).substring( 1 ) );
			faceDetector3 = new CascadeClassifier( ClientFace.class
					.getResource( "lbpcascade_frontalface.xml" ).getPath( ).substring( 1 ) );
		}
		else
		{
			faceDetector1 = new CascadeClassifier( ClientFace.class.getResource(
					"haarcascade_frontalface_alt.xml" ).getPath( ) );
			faceDetector2 = new CascadeClassifier( ClientFace.class.getResource(
					"haarcascade_frontalface_alt2.xml" ).getPath( ) );
			faceDetector3 = new CascadeClassifier( ClientFace.class.getResource(
					"lbpcascade_frontalface.xml" ).getPath( ) );
		}
		
		ImagePackageHelper helper = new ImagePackageHelper( );
		
		for (int imgID = 0 ; imgID < number_ImagePackages ; ++imgID)
		{
			//			out.println( "\tImage number:" + imgID );
			long time1 = currentTimeMillis( );
			imagePackage = (ImagePackage) objectInputStream.readObject( );
			
			String path = helper.writeImage( imagePackage , dirFile.getAbsolutePath( ) , "" );
			
			Mat image = Highgui.imread( path );
			
			MatOfRect faceDetections1 = new MatOfRect( );
			faceDetector1.detectMultiScale( image , faceDetections1 );
			
			MatOfRect faceDetections2 = new MatOfRect( );
			faceDetector2.detectMultiScale( image , faceDetections2 );
			
			MatOfRect faceDetections3 = new MatOfRect( );
			faceDetector3.detectMultiScale( image , faceDetections3 );
			
			Rect faces1[] = faceDetections1.toArray( );
			Rect faces2[] = faceDetections2.toArray( );
			Rect faces3[] = faceDetections3.toArray( );
			
			int face1 = faces1.length;
			int face2 = faces2.length;
			int face3 = faces3.length;
			
			//			out.println( face1 + " " + face2 + " " + face3 );
			{
				fade( path , faces1 , imagePackage.image_type );
				fade( path , faces2 , imagePackage.image_type );
				fade( path , faces3 , imagePackage.image_type );
			}
			imagePackage = helper.getImagePackage( path , imagePackage.image_name );
			objectOutputStream.writeObject( imagePackage );
			objectOutputStream.flush( );
			time1 = currentTimeMillis( ) - time1;
			objectOutputStream.writeLong( time1 );
			objectOutputStream.flush( );
		}
		time0 = currentTimeMillis( ) - time0;
		objectOutputStream.writeLong( time0 );
		objectOutputStream.writeObject( new String( "END" ) );
		objectOutputStream.flush( );
		objectOutputStream.close( );
		objectInputStream.close( );
		File fs[] = dirFile.listFiles( );
		for (File f : fs)
			f.delete( );
		dirFile.delete( );
	}
	
	/**
	 * @date Apr 5, 2014
	 * @user B. SHAHRIARI
	 * @param path
	 * @param faces1
	 * @return void
	 */
	private void fade( String path , Rect[ ] faces , String image_format )
	{
	
		try
		{
			BufferedImage image = ImageIO.read( new File( path ) );
			int w = image.getWidth( );
			int h = image.getHeight( );
			int pixels[] = new int[w * h];
			boolean mask[] = new boolean[w * h];
			Arrays.fill( mask , true );
			for (Rect r : faces)
			{
				for (int i = r.x ; i <= r.x + r.width ; i++)
				{
					for (int j = r.y ; j < r.y + r.height ; ++j)
					{
						mask[i + j * w] = false;
					}
				}
			}
			double filter[] =
			{
					0.01,
					0.4,
					0.01,
					0.4,
					1.0,
					0.4,
					0.01,
					0.4,
					0.01
			};
			image.getRGB( 0 , 0 , w , h , pixels , 0 , w );
			RGB rgb[] = new RGB[w * h];
			for (int x = 0 ; x < w ; ++x)
			{
				for (int y = 0 ; y < h ; ++y)
				{
					rgb[x + w * y] = new RGB( pixels[x + w * y] );
				}
			}
			for (int x = 0 ; x < w ; ++x)
			{
				for (int y = 0 ; y < h ; ++y)
				{
					if ( mask[x + y * w] )
					{
						int r = 0, g = 0, b = 0;
						double n = 0;
						for (int i = -1 ; i <= 1 ; ++i)
						{
							for (int j = -1 ; j <= 1 ; ++j)
							{
								int x_ = x + i;
								int y_ = y + j;
								if ( x_ < 0 || x_ >= w || y_ < 0 || y_ >= h ) continue;
								r += rgb[x_ + w * y_].red * filter[( i + 1 ) + 3 * ( j + 1 )];
								g += rgb[x_ + w * y_].green * filter[( i + 1 ) + 3 * ( j + 1 )];
								b += rgb[x_ + w * y_].blue * filter[( i + 1 ) + 3 * ( j + 1 )];
								n += filter[( i + 1 ) + 3 * ( j + 1 )] + 0.1;
							}
						}
						r /= n;
						g /= n;
						b /= n;
						pixels[x + w * y] = new Color( r , g , b ).getRGB( );
					}
				}
			}
			
			image.setRGB( 0 , 0 , w , h , pixels , 0 , w );
			ImageIO.write( image , image_format , new File( path ) );
			
		} catch ( IOException e )
		{
			e.printStackTrace( );
		}
		
	}
}
