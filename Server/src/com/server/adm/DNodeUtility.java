/**
 * DNodeUtility.java
 * com.adm
 * Apr 5, 2014
 * BEHROOZ SHAHRIARI
 */

package com.server.adm;


import java.util.concurrent.Callable;

import com.server.util.PackageData;



/**
 * @author B. SHAHRIARI
 * @date Apr 5, 2014
 */
public class DNodeUtility
		implements Callable < Object >
{
	
	DNode	node	= null;
	
	
	/**
	 * 
	 * @param node
	 */
	public DNodeUtility(DNode node)
	{
	
		this.node = node;
	}
	
	public void initDNode( PackageData packageData , int index , Histogram histograms[] )
	{
	
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Object call( )
	{
	
		return null;
	}
	
}
