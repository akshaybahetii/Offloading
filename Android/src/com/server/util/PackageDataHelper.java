/**
 * 
 */

package com.server.util;

/**
 * @author Behrooz
 * 
 */
public class PackageDataHelper
{
	
	private PackageData	packageData	= null;
	
	private int				id;
	
	
	/**
	 * 
	 * @param img_name
	 */
	public PackageDataHelper(String img_name[] , boolean feedback , int battery_level)
	{
	
		id = 0;
		SampleData sampleData[] = new SampleData[img_name.length];
		for (String in : img_name)
			sampleData[id] = new SampleData( in );
		packageData = new PackageData( sampleData , feedback , battery_level );
		id = 0;
	}
	
	public boolean add_PixelSample( int pixels[] , int w , int h , int size )
	{
	
		if ( id >= packageData.sampleData.length ) return false;
		packageData.sampleData[id].sample_pixels = pixels;
		packageData.sampleData[id].W = w;
		packageData.sampleData[id].H = h;
		packageData.sampleData[id].data_size = size;
		id++;
		return true;
	}
	
	public PackageData getPackageData( )
	{
	
		return packageData;
	}
	
}
