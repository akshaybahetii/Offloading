/**
 * DNode.java
 * com.adm
 * Apr 5, 2014
 * BEHROOZ SHAHRIARI
 */

package com.server.adm;


import java.io.Serializable;



/**
 * @author B. SHAHRIARI
 * @date Apr 5, 2014
 */
public class DNode
		implements Serializable
{
	
	public int			F					= 3;		//-3,10
															
	public double		data_size[]		= null;
	
	public double		image_width[]	= null;
	
	public double		image_height[]	= null;
	
	public Histogram	histograms[]	= null;
	
	public int			init_battery;
	
	public int			init_cpu_usage;
	
	public int			init_free_mem;			//in %
															
	Option				options[]		= null;
	
	boolean				infant_state	= true;
	
	
	public DNode( )
	{
	
		F = 0;
		data_size = new double[2];//average, std
		image_height = new double[2];
		image_width = new double[2];
		histograms = new Histogram[3];//Red, Green, Blue
		options = new Option[3];
		for (int i = 0 ; i < options.length ; ++i)
		{
			options[i] = new Option( );
			options[i].node = this;
		}
		options[0].type = Option.CLOUD_WiFi;
		options[1].type = Option.CLOUD_4G;
		options[2].type = Option.DEVICE;
	}
}
