
package com.server.face;

/**
 * 
 * @author B. SHAHRIARI
 * @date Apr 5, 2014
 */
public class RGB
{
	
	int	red, green, blue;
	
	
	/**
	 * 
	 * @param rgb
	 */
	public RGB(int rgb)
	{
	
		red = ( rgb >> 16 ) & 0x000000FF;
		green = ( rgb >> 8 ) & 0x000000FF;
		blue = ( rgb ) & 0x000000FF;
		
	}
	
	/**
	 * 
	 * @param red
	 * @param green
	 * @param blue
	 */
	public RGB(int red , int green , int blue)
	{
	
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	private static int[ ] X1Y1( int rgb )
	{
	
		int rgb_[] =
		{
				( rgb >> 16 ) & 0x000000FF,
				( rgb >> 8 ) & 0x000000FF,
				( rgb ) & 0x000000FF
		};
		return rgb_;
	}
	
	/**
	 * 
	 * @date Apr 5, 2014
	 * @user B. SHAHRIARI
	 * @param pixels
	 * @return int[][]
	 */
	public static int[ ][ ] toRGB( int pixels[] )
	{
	
		int rgb_pixels[][] = new int[3][pixels.length];
		for (int i = 0 ; i < pixels.length ; ++i)
		{
			int arr[] = X1Y1( pixels[i] );
			for (int j = 0 ; j < 3 ; ++j)
				rgb_pixels[j][i] = arr[j];
		}
		return rgb_pixels;
	}
}
