/**
 * Client.java
 * com.adm
 * Apr 4, 2014
 * BEHROOZ SHAHRIARI
 */

package com.server.adm;


import static java.lang.System.currentTimeMillis;
import static java.lang.System.out;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.server.util.PackageData;



/**
 * @author B. SHAHRIARI
 * @date Apr 4, 2014
 */
public class ClientADM
		implements Runnable
{
	
	String					source;
	
	private Socket			clientSocket;
	
	private InputStream	inputStream		= null;
	
	private OutputStream	outputStream	= null;
	
	private ADM				adm				= null;
	
	
	/**
	 * 
	 * @param clientSocket
	 * @param string
	 */
	public ClientADM(Socket clientSocket , ADM adm)
	{
	
		this.adm = adm;
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
	
	private void process( ) throws IOException, ClassNotFoundException
	{
	
		ObjectOutputStream objectOutputStream = new ObjectOutputStream( outputStream );
		ObjectInputStream objectInputStream = new ObjectInputStream( inputStream );
		
		PackageData packageData = (PackageData) objectInputStream.readObject( );
		int num_data = packageData.sampleData.length;
		String resultADM = "";
		Option resultList[] = new Option[num_data];
		long cloud_time = 0;
		long device_time = 0;
		for (int i = 0 ; i < num_data ; ++i)
		{
			resultList[i] = adm.getBestDestination( packageData , i , cloud_time , device_time );
			resultADM += i + "-" + resultList[i].type + ",";
			if ( resultList[i].type.equals( Option.CLOUD_4G ) ||
					resultList[i].type.equals( Option.CLOUD_WiFi ) )
				cloud_time += resultList[i].diff_time;
			if ( resultList[i].type.equals( Option.DEVICE ) ) device_time += resultList[i].diff_time;
			out.println( "Cloud Time:" + cloud_time + "  Device Time:" + device_time );
			/*
			 * Send back the resultADM to device.
			 * Then device based on the decision send the images to appropriate
			 * destination for processing.
			 * If feedback is on device wont close the connection, it just wait
			 * until it receive the result images and then send the time and new
			 * battery level to here so ADM can update itself.
			 */
		}
		objectOutputStream.writeObject( resultADM );
		objectOutputStream.flush( );
		if ( packageData.feedback )
		{
			for (int i = 0 ; i < num_data ; ++i)
			{
				/*
				 * get the feedback to update.
				 */
				int index = objectInputStream.readInt( );
				long compuation_time = objectInputStream.readLong( );
				int diff_mem = objectInputStream.readInt( );
				int diff_btr_lvl = objectInputStream.readInt( );
				adm.updateADM( packageData , resultList , index , compuation_time , diff_mem ,
						diff_btr_lvl );
			}
		}
		objectOutputStream.flush( );
		objectOutputStream.close( );
		objectInputStream.close( );
	}
}
