/**
 * TestSimulateDevice.java
 * com.server.adm
 * Apr 8, 2014
 * BEHROOZ SHAHRIARI
 */

package com.test;


import static java.lang.Math.max;
import static java.lang.System.exit;
import static java.lang.System.out;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import javax.imageio.ImageIO;

import com.server.adm.Option;
import com.server.util.IP_Address;
import com.server.util.ImagePackage;
import com.server.util.ImagePackageHelper;
import com.server.util.PackageData;
import com.server.util.SampleData;



/**
 * @author B. SHAHRIARI
 * @date Apr 8, 2014
 */
public class TestSimulateDevice
{
	
	/**
	 * @date Apr 8, 2014
	 * @user B. SHAHRIARI
	 * @param args
	 * @return void
	 */
	public static void main( String[ ] args )
	{
	
		SimulateDevice device = new SimulateDevice( );
		try
		{
			device.simulate( );
		} catch ( UnknownHostException e )
		{
			e.printStackTrace( );
		} catch ( IOException e )
		{
			e.printStackTrace( );
		} catch ( ClassNotFoundException e )
		{
			e.printStackTrace( );
		}
	}
	
}



class SimulateDevice
{
	
	public void simulate( ) throws ClassNotFoundException, IOException
	{
	
		Random random = new Random( );
		for (int ks = 0 ; ks < 30 ; ++ks)
		{
			String images[] =
			{
					"1.png",
					"1.jpg",
					"2.jpg",
					"3.jpg",
					"4.jpg",
					"5.jpg",
					"6.jpg",
					"7.jpg"
			};
			Socket admSocket = new Socket( IP_Address.ADM_SERVER_IP , IP_Address.ADM_PORT );
			ObjectOutputStream admOutputStream = new ObjectOutputStream( admSocket.getOutputStream( ) );
			ObjectInputStream admInputStream = new ObjectInputStream( admSocket.getInputStream( ) );
			
			Socket faceSocket = new Socket( IP_Address.FACE_SERVER_IP , IP_Address.FACE_PORT );
			ObjectOutputStream faceOutputStream = new ObjectOutputStream( faceSocket.getOutputStream( ) );
			ObjectInputStream faceInputStream = new ObjectInputStream( faceSocket.getInputStream( ) );
			
			out.println( "Connected to> Face:" + faceSocket.toString( ) + " ADM:" +
								admSocket.toString( ) );
			
			SampleData sampleDatas[] = new SampleData[images.length];
			for (int i = 0 ; i < images.length ; ++i)
			{
				String path = "";
				if ( System.getProperty( "os.name" ).startsWith( "Windows" ) )
				{
					path = SimulateDevice.class.getResource( images[i] ).getPath( ).substring( 1 );
				}
				else
				{
					path = SimulateDevice.class.getResource( images[i] ).getPath( );
				}
				File file = new File( path );
				//				out.println( i + " " + images[i] + "  " + path );
				BufferedImage image = null;
				try
				{
					image = ImageIO.read( file );
				} catch ( IOException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace( );
				}
				int w = image.getWidth( );
				int h = image.getHeight( );
				int step = (int) ( max( w , h ) * 0.3 );
				sampleDatas[i] = new SampleData( images[i] );
				sampleDatas[i].sample_pixels = new int[( w * h ) / ( step * step )];
				if ( step < 5 ) step = 5;
				int idx = 0;
				for (int x = 0 ; x < w ; x += step)
				{
					for (int y = 0 ; y < h ; y += step)
					{
						sampleDatas[i].sample_pixels[idx] = image.getRGB( x , y );
						idx++;
						if ( idx == sampleDatas[i].sample_pixels.length - 1 ) break;
					}
					if ( idx == sampleDatas[i].sample_pixels.length - 1 ) break;
				}
				sampleDatas[i].H = h;
				sampleDatas[i].W = w;
				sampleDatas[i].data_size = (int) ( file.length( ) / 1024 );
				
			}
			out.println( "Sending PackageData" );
			PackageData packageData = new PackageData( sampleDatas , true , 98 - ks *
																									random.nextInt( 11 ) ,
					random.nextInt( 20 ) + 5 , random.nextInt( 40 ) + 60 );
			admOutputStream.writeObject( packageData );
			admOutputStream.flush( );
			String resultADM = (String) admInputStream.readObject( );
			out.println( "Result:" + resultADM );
			ImagePackageHelper helper = new ImagePackageHelper( );
			faceOutputStream.writeInt( images.length );
			faceOutputStream.flush( );
			for (int i = 0 ; i < images.length ; ++i)
			{
				String path = "";
				if ( System.getProperty( "os.name" ).startsWith( "Windows" ) )
				{
					path = SimulateDevice.class.getResource( images[i] ).getPath( ).substring( 1 );
				}
				else
				{
					path = SimulateDevice.class.getResource( images[i] ).getPath( );
				}
				faceOutputStream.writeObject( helper.getImagePackage( path , images[i] ) );
				faceOutputStream.flush( );
				ImagePackage imagePackage = (ImagePackage) faceInputStream.readObject( );
				long time = faceInputStream.readLong( );
				//				out.println( imagePackage.image_name + " Received." );
				if ( System.getProperty( "os.name" ).startsWith( "Windows" ) )
				{
					path = SimulateDevice.class.getResource( "" ).getPath( ).substring( 1 );
				}
				else
				{
					path = SimulateDevice.class.getResource( "" ).getPath( );
				}
				helper.writeImage( imagePackage , path , "fd-" );
			}
			long timeTotal = faceInputStream.readLong( );
			String msg = (String) faceInputStream.readObject( );
			if ( !msg.equals( "END" ) ) exit( -1 );
			faceInputStream.close( );
			faceOutputStream.close( );
			faceSocket.close( );
			try
			{
				Thread.sleep( 1200 );
			} catch ( InterruptedException e )
			{
				e.printStackTrace( );
			}
			
			String hs[] = resultADM.split( "," );
			for (String s : hs)
			{
				String h[] = s.split( "-" );
				int index = Integer.parseInt( h[0] );
				admOutputStream.writeInt( index );
				admOutputStream.flush( );
				long com_time = 0;
				int diff_mem = 0, diff_btr_lvl = 0;
				if ( h[1].equals( Option.CLOUD_4G ) )
				{
					com_time = (long) ( ( sampleDatas[index].W * ( random.nextInt( 10 ) + 2 ) + 35 ) +
												( random.nextInt( 35 ) + 5 ) * sampleDatas[index].W / 2 + 100 );
					diff_mem = ( sampleDatas[index].W / 3 ) * ( random.nextInt( 7 ) + 2 );
					diff_btr_lvl = (int) ( ( sampleDatas[index].W / 3 + random
							.nextInt( sampleDatas[index].W ) ) * ( random.nextDouble( ) + 0.4 ) );
				}
				if ( h[1].equals( Option.CLOUD_WiFi ) )
				{
					com_time = (long) ( ( sampleDatas[index].W * ( random.nextInt( 10 ) + 2 ) + 35 ) +
												( random.nextInt( 15 ) + 3 ) * sampleDatas[index].W / 2 + 80 );
					diff_mem = ( sampleDatas[index].W / 3 ) * ( random.nextInt( 5 ) + 2 );
					diff_btr_lvl = (int) ( ( sampleDatas[index].W / 5 + random
							.nextInt( sampleDatas[index].W ) ) * ( random.nextDouble( ) + 0.2 ) );
				}
				if ( h[1].equals( Option.DEVICE ) )
				{
					com_time = (long) ( ( sampleDatas[index].W * sampleDatas[index].H *
													( random.nextInt( 20 ) + 2 ) + 35 ) + 380 );
					diff_mem = ( sampleDatas[index].W * sampleDatas[index].H ) *
									( random.nextInt( 7 ) + 2 );
					diff_btr_lvl = (int) ( ( sampleDatas[index].W + random
							.nextInt( sampleDatas[index].W ) ) * ( random.nextDouble( ) + 0.35 ) );
				}
				admOutputStream.writeLong( com_time );
				admOutputStream.writeInt( diff_mem );
				admOutputStream.writeInt( diff_btr_lvl );
				admOutputStream.flush( );
			}
			admOutputStream.close( );
			admInputStream.close( );
			admSocket.close( );
		}
		
	}
}
