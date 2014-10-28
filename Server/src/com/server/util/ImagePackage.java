/**
 * ImagePackage.java
 * com.server.util
 * Apr 4, 2014
 * BEHROOZ SHAHRIARI
 */

package com.server.util;


import java.io.Serializable;



/**
 * @author B. SHAHRIARI
 * @date Apr 4, 2014
 */
public class ImagePackage
		implements Serializable
{
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -3491984748724907807L;
	
	public byte						data[]				= null;
	
	public String					image_name;
	
	public String					image_type;
	
	
	/**
	 *
	 */
	public ImagePackage(byte data[] , String image_name , String image_type)
	{
	
		this.data = data;
		this.image_name = image_name;
		this.image_type = image_type;
	}
	
}
