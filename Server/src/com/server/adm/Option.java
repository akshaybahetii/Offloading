/**
 * Option.java
 * com.server.adm
 * Apr 8, 2014
 * BEHROOZ SHAHRIARI
 */

package com.server.adm;


import java.io.Serializable;



/**
 * 
 * @author B. SHAHRIARI
 * @date Apr 5, 2014
 */
public class Option
		implements Serializable
{
	
	public final static String	CLOUD_4G				= "G";
	
	public final static String	CLOUD_WiFi			= "W";
	
	public final static String	DEVICE				= "D";
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 8008799146593013485L;
	
	DNode								node					= null;
	
	String							type;												//G,W,D
																								
	double							power					= 0;
	
	double							dif_btr_lvl			= 0;
	
	double							mrg_diff_btr_lvl	= 35;
	
	double							diff_mem				= 0;
	
	double							mrg_diff_mem		= 1024 * 15;				//in KB
																								
	double							diff_time			= 0;
	
	double							mrg_diff_time		= 1000 * 60 * 1;			//in ms
}
