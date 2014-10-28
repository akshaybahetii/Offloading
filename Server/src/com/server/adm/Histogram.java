/**
 * Histogram.java
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
public class Histogram
		implements Serializable
{
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1672001466318819012L;
	
	public float					bins[]				= null;
	
	public int						bin_width;
	
}
