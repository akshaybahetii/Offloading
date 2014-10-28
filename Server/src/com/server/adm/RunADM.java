/**
 * RunFace.java
 * com.fe
 * Apr 4, 2014
 * BEHROOZ SHAHRIARI
 */

package com.server.adm;





/**
 * @author B. SHAHRIARI
 * @date Apr 4, 2014
 */
public class RunADM
{
	
	/**
	 * @date Apr 4, 2014
	 * @user B. SHAHRIARI
	 * @param args
	 * @return void
	 */
	public static void main( String[ ] args )
	{
	
		new Thread( new ADMServer( ) ).start( );
	}
}
