/**
 * ListDNode.java
 * com.adm
 * Apr 4, 2014
 * BEHROOZ SHAHRIARI
 */

package com.server.adm;


import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;



/**
 * @author B. SHAHRIARI
 * @date Apr 4, 2014
 */
public class PackageDNode
		implements Serializable
{
	
	public CopyOnWriteArrayList < DNode >	listDNode_Data	= null;
	
	
	/**
	 *
	 */
	public PackageDNode( )
	{
	
		listDNode_Data = new CopyOnWriteArrayList < DNode >( );
	}
	
}
