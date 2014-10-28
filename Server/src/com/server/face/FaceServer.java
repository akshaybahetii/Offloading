/**
 * FEServer.java
 * com.adm
 * Apr 3, 2014
 * BEHROOZ SHAHRIARI
 */

package com.server.face;


import static java.lang.System.out;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.server.util.IP_Address;



/**
 * @author B. SHAHRIARI
 * @date Apr 3, 2014
 */
public class FaceServer
		implements Runnable
{
	
	private ServerSocket	serverSocket	= null;
	
	private boolean		isStopped		= false;
	
	
	/**
	 *
	 */
	public FaceServer( )
	{
	
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run( )
	{
	
		openServerSocket( );
		while ( !isStopped( ) )
		{
			Socket clientSocket = null;
			try
			{
				clientSocket = this.serverSocket.accept( );
			} catch ( IOException e )
			{
				if ( isStopped( ) )
				{
					System.out.println( "Face Server Stopped." );
					return;
				}
				throw new RuntimeException( "Error accepting client connection" , e );
			}
			new Thread( new ClientFace( clientSocket ) ).start( );
		}
		System.out.println( "Face Server Stopped." );
	}
	
	/**
	 * 
	 * @date Apr 4, 2014
	 * @user B. SHAHRIARI
	 * @return boolean
	 */
	private synchronized boolean isStopped( )
	{
	
		return this.isStopped;
	}
	
	/**
	 * 
	 * @date Apr 4, 2014
	 * @user B. SHAHRIARI
	 * @return void
	 */
	public synchronized void stop( )
	{
	
		this.isStopped = true;
		try
		{
			this.serverSocket.close( );
		} catch ( IOException e )
		{
			throw new RuntimeException( "Error closing Face server" , e );
		}
	}
	
	/**
	 * 
	 * @date Apr 4, 2014
	 * @user B. SHAHRIARI
	 * @return void
	 */
	private void openServerSocket( )
	{
	
		try
		{
			this.serverSocket = new ServerSocket( IP_Address.FACE_PORT );
			out.println( "Face Server is running on " + IP_Address.FACE_SERVER_IP + ":" +
								IP_Address.FACE_PORT );
		} catch ( IOException e )
		{
			throw new RuntimeException( "Cannot open port " + IP_Address.FACE_PORT , e );
		}
	}
}
