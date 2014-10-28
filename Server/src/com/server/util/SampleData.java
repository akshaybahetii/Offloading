/**
 * SampleData.java
 * face
 * Apr 3, 2014
 * BEHROOZ SHAHRIARI
 */

package com.server.util;


import java.io.Serializable;



/**
 * @author B. SHAHRIARI
 * @date Apr 3, 2014
 */
public class SampleData
		implements Serializable
{
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 6050868744295782492L;
	
	/**
	 * Name of image.
	 */
	public String					img_name				= null;
	
	public int						sample_pixels[]	= null;
	
	public int						data_size;											//in KB
																								
	public int						W;
	
	public int						H;
	
	
	/**
	 * 
	 * @param img_name
	 */
	public SampleData(String img_name)
	{
	
		this.img_name = img_name;
	}
}
