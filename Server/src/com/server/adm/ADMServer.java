/**
 * ADMServer.java
 * com.adm
 * Apr 4, 2014
 * BEHROOZ SHAHRIARI
 */

package com.server.adm;


import static java.lang.System.out;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.server.util.IP_Address;



/**
 * @author B. SHAHRIARI
 * @date Apr 4, 2014
 */
public class ADMServer
		implements Runnable
{
	
	private ServerSocket	serverSocket	= null;
	
	private boolean		isStopped		= false;
	
	private ADM				adm				= null;
	
	
	/**
	 * 
	 * @param serverPort
	 */
	public ADMServer( )
	{
	
		adm = new ADM( );
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
					System.out.println( "Server Stopped." );
					return;
				}
				throw new RuntimeException( "Error accepting client connection" , e );
			}
			new Thread( new ClientADM( clientSocket , adm ) ).start( );
		}
		System.out.println( "Server Stopped." );
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
			throw new RuntimeException( "Error closing server" , e );
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
			this.serverSocket = new ServerSocket( IP_Address.ADM_PORT );
			out.println( "ADM Server is running on " + IP_Address.ADM_SERVER_IP + ":" +
								IP_Address.ADM_PORT );
		} catch ( IOException e )
		{
			throw new RuntimeException( "Cannot open port " + IP_Address.ADM_PORT , e );
		}
	}
	
}
