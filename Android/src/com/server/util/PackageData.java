/**
 * PackageData.java
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
public class PackageData
		implements Serializable
{
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 2448005592948909277L;
	
	public SampleData				sampleData[]		= null;
	
	public boolean					feedback				= true;
	
	//	public boolean					wifi					= true;
	
	//	public String					device_name;
	
	public int						init_battery_level;
	
	public int						init_cpu_usage;
	
	public int						init_free_mem;
	
	
	/**
	 * 
	 * 
	 * @param sampleData
	 * @param feedback
	 * @param battery_level
	 */
	public PackageData(SampleData sampleData[] , boolean feedback , int init_battery_level ,
								int init_cpu_usage , int init_free_mem)
	{
	
		this.sampleData = sampleData;
		this.feedback = feedback;
		//		this.wifi = wifi;
		//		this.device_name = device_name;
		this.init_battery_level = init_battery_level;
		this.init_cpu_usage = init_cpu_usage;
		this.init_free_mem = init_free_mem;
	}
	
}
