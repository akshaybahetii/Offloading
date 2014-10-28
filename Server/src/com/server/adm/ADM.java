/**
 * ADM.java
 * com.adm
 * Apr 3, 2014
 * BEHROOZ SHAHRIARI
 */

package com.server.adm;


import static java.lang.Math.abs;
import static java.lang.Math.exp;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.System.out;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

import com.server.face.RGB;
import com.server.util.PackageData;
import com.server.util.SampleData;



/**
 * @author B. SHAHRIARI
 * @date Apr 3, 2014
 */
public class ADM
{
	
	private static final double	THRESHOLD_1			= 0.8;
	
	private static final double	THRESHOLD_2			= 0.45;
	
	private static PackageDNode	packageDNode		= null;
	
	private Random						random				= null;
	
	private int							episodes				= 0;
	
	private static String			ADMPath				= "";
	
	private static final String	packageDNodeFile	= "data.adm";
	
	
	/**
	 *
	 */
	public ADM( )
	{
	
		random = new Random( );
		packageDNode = new PackageDNode( );
		if ( System.getProperty( "os.name" ).startsWith( "Windows" ) )
		{
			ADMPath = ADM.class.getResource( "" ).getPath( ).substring( 1 );
		}
		else
		{
			ADMPath = ADM.class.getResource( "" ).getPath( );
		}
		File file = new File( ADMPath + File.separator + packageDNodeFile );
		if ( file.exists( ) )
		{
			FileInputStream stream;
			try
			{
				stream = new FileInputStream( file );
				ObjectInputStream inputStream = new ObjectInputStream( stream );
				packageDNode = (PackageDNode) inputStream.readObject( );
				inputStream.close( );
			} catch ( FileNotFoundException e )
			{
				e.printStackTrace( );
			} catch ( ClassNotFoundException e )
			{
				e.printStackTrace( );
			} catch ( IOException e )
			{
				e.printStackTrace( );
			}
			
		}
	}
	
	/**
	 * 
	 * @date Apr 7, 2014
	 * @user B. SHAHRIARI
	 * @param packageData
	 * @param index
	 * @param cloud_time
	 * @param device_time
	 * @return Option
	 */
	protected Option getBestDestination( PackageData packageData , int index , long cloud_time ,
			long device_time )
	{
	
		double epsilon = 0.1;
		
		DNode dataDNode = makeDNode( packageData.sampleData[index] , packageData );
		
		HashMap < DNode , Double > map_Sim = new HashMap < DNode , Double >(
				packageDNode.listDNode_Data.size( ) );
		
		double sim, maxSim = 0;
		DNode bestDNode = null;
		for (DNode dNode : packageDNode.listDNode_Data)
		{
			sim = getSimilarity( dNode , dataDNode );
			//			out.println( "\t-- " + dNode.F + " " + dNode.data_size[0] + "," + dNode.data_size[1] +
			//								" " + dNode.image_width[0] + "," + dNode.image_width[1] + " " +
			//								dNode.image_height[0] + "," + dNode.image_height[1] + " -> " + sim );
			map_Sim.put( dNode , sim );
			if ( sim > maxSim )
			{
				maxSim = sim;
				bestDNode = dNode;
			}
		}
		out.println( "\tMaximum Similarity:" + maxSim );
		int thr_F = 3;
		if ( maxSim > THRESHOLD_1 && bestDNode.F < thr_F )
		{
			for (DNode dn : map_Sim.keySet( ))
				if ( map_Sim.get( dn ) > THRESHOLD_1 && dn.F >= thr_F )
				{
					bestDNode = dn;
					break;
				}
		}
		if ( maxSim < THRESHOLD_2 )
		{
			//add dataDNode to PackageDNode.
			bestDNode = dataDNode;
			//			out.println( "-- " + dataDNode.F + " " + dataDNode.data_size[0] + "," +
			//								dataDNode.data_size[1] + " " + dataDNode.image_width[0] + "," +
			//								dataDNode.image_width[1] + " " + dataDNode.image_height[0] + "," +
			//								dataDNode.image_height[1] );
			packageDNode.listDNode_Data.add( bestDNode );
			
		}
		else if ( maxSim > THRESHOLD_2 && maxSim < THRESHOLD_1 )
		{
			//Use the bestDNode or use e-greedy to choose the best.
			if ( random.nextDouble( ) < epsilon )
			{
				while ( true )
				{
					int idx = random.nextInt( packageDNode.listDNode_Data.size( ) );
					double s = map_Sim.get( packageDNode.listDNode_Data.get( idx ) );
					if ( s > THRESHOLD_2 && s < THRESHOLD_1 )
					{
						bestDNode = packageDNode.listDNode_Data.get( idx );
						break;
					}
				}
			}
		}
		sortOptions( bestDNode , cloud_time , device_time );
		return bestDNode.options[0];
	}
	
	private DNode makeDNode( SampleData sampleData , PackageData packageData )
	{
	
		DNode dataDNode = new DNode( );
		dataDNode.data_size[0] = sampleData.data_size;
		dataDNode.data_size[1] = 1 + (double) dataDNode.data_size[0] / 5;
		dataDNode.image_width[0] = sampleData.W;
		dataDNode.image_width[1] = 1 + (double) dataDNode.image_width[0] / 3;
		dataDNode.image_height[0] = sampleData.H;
		dataDNode.image_height[1] = 1 + (double) dataDNode.image_height[0] / 3;
		dataDNode.init_battery = packageData.init_battery_level;
		dataDNode.init_cpu_usage = packageData.init_cpu_usage;
		dataDNode.init_free_mem = packageData.init_free_mem;
		dataDNode.histograms = getHistograms( sampleData.sample_pixels );
		return dataDNode;
	}
	
	/**
	 * 
	 * @date Apr 7, 2014
	 * @user B. SHAHRIARI
	 * @param dNode
	 * @param cloud_time
	 * @param device_time
	 * @return void
	 */
	private void sortOptions( DNode dNode , long cloud_time , long device_time )
	{
	
		double epsilon = 0.2;
		synchronized ( dNode )
		{
			Arrays.sort( dNode.options , new Comparator < Option >( )
			{
				
				@Override
				public int compare( Option op1 , Option op2 )
				{
				
					double d = op1.dif_btr_lvl - op2.dif_btr_lvl;
					double thr = sqrt( pow( op1.mrg_diff_btr_lvl , 2 ) + pow( op2.mrg_diff_btr_lvl , 2 ) ) / 2;
					if ( d > 0 )
					{
						if ( d >= thr ) return 1;
					}
					else if ( d < 0 )
					{
						if ( abs( d ) >= thr ) return -1;
					}
					
					d = op1.diff_time - op2.diff_time;
					thr = sqrt( pow( op1.mrg_diff_time , 2 ) + pow( op2.mrg_diff_time , 2 ) ) / 2;
					if ( d > 0 )
					{
						if ( d >= thr ) return 1;
					}
					else if ( d < 0 )
					{
						if ( abs( d ) >= thr ) return -1;
					}
					
					d = op1.diff_mem - op2.diff_mem;
					thr = sqrt( pow( op1.mrg_diff_mem , 2 ) + pow( op2.mrg_diff_mem , 2 ) ) / 2;
					if ( d > 0 )
					{
						if ( d >= thr ) return 1;
					}
					else if ( d < 0 )
					{
						if ( abs( d ) >= thr ) return -1;
					}
					
					return 0;
				}
			} );
			
			if ( dNode.infant_state )
			{
				boolean use_e_greedy = false;
				double max_diff = 0;
				double min_power = Double.MAX_VALUE;
				for (int j = 0 ; j < dNode.options.length - 1 ; ++j)
				{
					for (int i = j + 1 ; i < dNode.options.length ; ++i)
					{
						double d = abs( dNode.options[i].power - dNode.options[j].power );
						max_diff = max( d , max_diff );
						min_power = min( min_power , dNode.options[i].power );
						if ( d > 10 )
						{
							use_e_greedy = true;
						}
					}
				}
				if ( min_power < 24 ) use_e_greedy = true;
				
				if ( use_e_greedy )
				{
					if ( random.nextDouble( ) < epsilon )
					{
						
						int idx = random.nextInt( dNode.options.length ) + 1;
						if ( idx >= dNode.options.length ) idx--;
						Option t = dNode.options[0];
						dNode.options[0] = dNode.options[idx];
						dNode.options[idx] = t;
					}
				}
				else
				{
					if ( max_diff < 5 ) dNode.infant_state = false;
					if ( random.nextDouble( ) < epsilon / 3 )
					{
						int idx = random.nextInt( dNode.options.length ) + 1;
						if ( idx >= dNode.options.length ) idx--;
						Option t = dNode.options[0];
						dNode.options[0] = dNode.options[idx];
						dNode.options[idx] = t;
					}
				}
				dNode.options[0].power++;
			}
			else
			{
				out.print( "-- Not Infant" );
				if ( ( dNode.options[0].type.equals( Option.CLOUD_WiFi ) || dNode.options[0].type
						.equals( Option.CLOUD_4G ) ) &&
						cloud_time > ( device_time * 1 ) / 2 &&
						random.nextDouble( ) < epsilon * 3 )
				{
					out.println( "   Go with Device" );
					for (int i = 1 ; i < dNode.options.length ; ++i)
					{
						if ( dNode.options[i].type.equals( Option.DEVICE ) )
						{
							Option t = dNode.options[i];
							dNode.options[i] = dNode.options[0];
							dNode.options[0] = t;
							break;
						}
					}
				}
				//				else
				//				{
				//					if ( random.nextDouble( ) < epsilon / 10 )
				//					{
				//						int idx = random.nextInt( dNode.options.length );
				//						Option t = dNode.options[idx];
				//						dNode.options[idx] = dNode.options[0];
				//						dNode.options[0] = t;
				//					}
				//				}
			}
		}
	}
	
	/**
	 * 
	 * @date Apr 5, 2014
	 * @user B. SHAHRIARI
	 * @param dNode
	 * @param myDNode
	 * @return double
	 */
	private double getSimilarity( DNode dNode , DNode myDNode )
	{
	
		double u1, s1;
		double u2, s2;
		double a, b, c;
		double x1, x2, xf, X, Y;
		
		double w[] =
		{
				1,
				7,
				4,
				3,
				3,
				3,
				/* btr */2.7,
				/* mem */1.4,
				/* cpu */6.5
		};
		
		double wt[] =
		{
				1,
				7,
				4,
				0,
				0,
				0,
				/* btr */0,
				/* mem */0,
				/* cpu */0
		};
		
		double sim[] = new double[w.length];
		
		u1 = dNode.data_size[0];
		s1 = dNode.data_size[1];
		u2 = myDNode.data_size[0];
		s2 = myDNode.data_size[1];
		xf = min( u1 , u2 );
		a = s2 * s2 - s1 * s1;
		b = 2 * ( u2 * s1 * s1 - u1 * s2 * s2 );
		c = ( u1 * s2 ) * ( u1 * s2 ) - ( u2 * s1 ) * ( u2 * s1 );
		x1 = ( -b + sqrt( b * b - 4 * a * c ) ) / ( 2 * a );
		x2 = ( -b - sqrt( b * b - 4 * a * c ) ) / ( 2 * a );
		if ( u1 == u2 ) x1 = x2 = u1;
		else if ( s1 == s2 )
		{
			x1 = u1 - u2;
			x2 = u2 - u1;
		}
		if ( x1 > xf ) X = x1;
		else X = x2;
		Y = exp( -pow( ( X - u1 ) / s1 , 2 ) );
		sim[0] = Y;
		//		out.println( "\t\t-- Y:" + Y + "   " + X + "  " + u1 );
		
		u1 = dNode.image_width[0];
		s1 = dNode.image_width[1];
		u2 = myDNode.image_width[0];
		s2 = myDNode.image_width[1];
		xf = min( u1 , u2 );
		a = s2 * s2 - s1 * s1;
		b = 2 * ( u2 * s1 * s1 - u1 * s2 * s2 );
		c = ( u1 * s2 ) * ( u1 * s2 ) - ( u2 * s1 ) * ( u2 * s1 );
		x1 = ( -b + sqrt( b * b - 4 * a * c ) ) / ( 2 * a );
		x2 = ( -b - sqrt( b * b - 4 * a * c ) ) / ( 2 * a );
		if ( u1 == u2 ) x1 = x2 = u1;
		else if ( s1 == s2 )
		{
			x1 = u1 - u2;
			x2 = u2 - u1;
		}
		if ( x1 > xf ) X = x1;
		else X = x2;
		Y = exp( -pow( ( X - u1 ) / s1 , 2 ) );
		sim[1] = Y;
		
		u1 = dNode.image_height[0];
		s1 = dNode.image_height[1];
		u2 = myDNode.image_height[0];
		s2 = myDNode.image_height[1];
		xf = min( u1 , u2 );
		a = s2 * s2 - s1 * s1;
		b = 2 * ( u2 * s1 * s1 - u1 * s2 * s2 );
		c = ( u1 * s2 ) * ( u1 * s2 ) - ( u2 * s1 ) * ( u2 * s1 );
		x1 = ( -b + sqrt( b * b - 4 * a * c ) ) / ( 2 * a );
		x2 = ( -b - sqrt( b * b - 4 * a * c ) ) / ( 2 * a );
		if ( u1 == u2 ) x1 = x2 = u1;
		else if ( s1 == s2 )
		{
			x1 = u1 - u2;
			x2 = u2 - u1;
		}
		if ( x1 > xf ) X = x1;
		else X = x2;
		Y = exp( -pow( ( X - u1 ) / s1 , 2 ) );
		sim[2] = Y;
		
		for (int i = 0 ; i < myDNode.histograms.length ; ++i)
		{
			double dis = 0;
			for (int j = 0 ; j < myDNode.histograms[i].bins.length ; ++j)
			{
				dis += abs( myDNode.histograms[i].bins[j] - dNode.histograms[i].bins[j] );
			}
			dis /= (double) myDNode.histograms[i].bins.length;
			sim[3 + i] = 1 - dis;
		}
		
		if ( myDNode.init_battery > 15 && dNode.init_battery > 15 )
		{
			int d = abs( myDNode.init_battery - dNode.init_battery );
			int n = d / 10;
			double d_ = (double) n / 10d;
			sim[6] = 1 - d_;
		}
		else sim[6] = 1;
		sim[7] = 1 - (double) abs( myDNode.init_cpu_usage - dNode.init_cpu_usage ) / 100d;
		sim[8] = 1 - (double) abs( myDNode.init_free_mem - dNode.init_free_mem ) / 100d;
		//		out.println( sim[7] + "  " + sim[8] );
		
		double simT = 0;
		double W = 0;
		for (int i = 0 ; i < w.length ; ++i)
		{
			W += w[i];
			simT += w[i] * sim[i];
			//			out.println( "\t" + sim[i] );
		}
		return simT / W;
	}
	
	private Histogram[ ] getHistograms( int pixels[] )
	{
	
		int rgb[][] = RGB.toRGB( pixels );
		Histogram histograms[] = new Histogram[3];
		for (int i = 0 ; i < 3 ; ++i)
		{
			histograms[i] = makeHistogram( rgb[i] , 2 , 0 , 255 );
		}
		return histograms;
	}
	
	/**
	 * 
	 * @date Apr 5, 2014
	 * @user B. SHAHRIARI
	 * @param data
	 * @param w
	 * @param low_bound
	 * @param up_bound
	 * @return Histogram
	 */
	private Histogram makeHistogram( int data[] , int w , int low_bound , int up_bound )
	{
	
		int L = up_bound - low_bound + 1;
		float bins[] = new float[L / w];
		float n = 0;
		//Normalize
		for (int i = 0 ; i < data.length ; ++i)
		{
			bins[data[i] / w]++;
			n++;
		}
		float max = -1;
		float min = 10;
		for (int i = 0 ; i < bins.length ; ++i)
		{
			bins[i] /= n;
			max = max( max , bins[i] );
			min = min( min , bins[i] );
		}
		//Set new bounds.
		for (int i = 0 ; i < bins.length ; ++i)
		{
			bins[i] = ( bins[i] / ( max - min ) ) + ( min / ( min - max ) );
		}
		Histogram histogram = new Histogram( );
		histogram.bin_width = w;
		histogram.bins = bins;
		return histogram;
	}
	
	/**
	 * @date Apr 5, 2014
	 * @user B. SHAHRIARI
	 * @param packageData
	 * @param resultList
	 * @param index
	 * @param diff_btr_lvl
	 * @param diff_mem
	 * @param compuation_time
	 * @return void
	 */
	protected void updateADM( PackageData packageData , Option[ ] resultList , int index ,
			long compuation_time , int diff_mem , int diff_btr_lvl )
	{
	
		episodes++;
		if ( episodes > 30 )
		{
			episodes = 0;
			//write in the file
			try
			{
				FileOutputStream stream = new FileOutputStream( new File( ADMPath + File.separator +
																								packageDNodeFile ) );
				ObjectOutputStream outputStream = new ObjectOutputStream( stream );
				outputStream.writeObject( packageDNode );
				outputStream.flush( );
				outputStream.close( );
			} catch ( FileNotFoundException e )
			{
				e.printStackTrace( );
			} catch ( IOException e )
			{
				e.printStackTrace( );
			}
		}
		out.println( "\nUpdating ADM for " + packageData.sampleData[index].img_name + "  " +
							packageDNode.listDNode_Data.size( ) + ".\n" );
		Option option = resultList[index];
		double d, y;
		double alpha = 0.2, beta = 0.08, thr = 0.4;
		
		d = diff_btr_lvl - option.dif_btr_lvl;
		y = exp( -( pow( d / option.mrg_diff_btr_lvl , 2 ) ) );
		if ( d > 0 && y < thr )
		{
			//we update mean and sd.
			option.dif_btr_lvl = ( option.dif_btr_lvl + diff_btr_lvl * alpha ) / ( 1 + alpha );
			option.mrg_diff_btr_lvl = sqrt( ( pow( option.mrg_diff_btr_lvl , 2 ) + ( pow( beta * d , 2 ) ) ) /
														( 1 + beta * beta ) );
			option.node.F--;
		}
		else
		{
			option.dif_btr_lvl = ( option.dif_btr_lvl + diff_btr_lvl * alpha / 2 ) / ( 1 + alpha );
			option.mrg_diff_btr_lvl = sqrt( ( pow( option.mrg_diff_btr_lvl , 2 ) + ( pow( beta * 0.5 *
																													d , 2 ) ) ) /
														( 1 + beta * beta ) );
			option.node.F++;
		}
		
		d = compuation_time - option.diff_time;
		y = exp( -( pow( d / option.mrg_diff_time , 2 ) ) );
		if ( d > 0 && y < thr )
		{
			//we update mean and sd.
			option.diff_time = ( option.diff_time + compuation_time * alpha ) / ( 1 + alpha );
			option.mrg_diff_time = sqrt( ( pow( option.mrg_diff_time , 2 ) + ( pow( beta * d , 2 ) ) ) /
													( 1 + beta * beta ) );
			option.node.F--;
		}
		else
		{
			option.diff_time = ( option.diff_time + compuation_time * alpha / 2 ) / ( 1 + alpha );
			option.mrg_diff_time = sqrt( ( pow( option.mrg_diff_time , 2 ) + ( pow( beta * 0.5 * d , 2 ) ) ) /
													( 1 + beta * beta ) );
			option.node.F++;
		}
		
		d = diff_mem - option.diff_mem;
		y = exp( -( pow( d / option.mrg_diff_mem , 2 ) ) );
		if ( d > 0 && y < thr )
		{
			//we update mean and sd.
			option.diff_mem = ( option.diff_mem + compuation_time * alpha ) / ( 1 + alpha );
			option.mrg_diff_mem = sqrt( ( pow( option.mrg_diff_mem , 2 ) + ( pow( beta * d , 2 ) ) ) /
													( 1 + beta * beta ) );
			option.node.F--;
		}
		else
		{
			option.diff_mem = ( option.diff_mem + compuation_time * alpha / 2 ) / ( 1 + alpha );
			option.mrg_diff_mem = sqrt( ( pow( option.mrg_diff_mem , 2 ) + ( pow( beta * 0.5 * d , 2 ) ) ) /
													( 1 + beta * beta ) );
			option.node.F++;
		}
		
		if ( option.node.F < -3 )
		{
			option.node.F = 1;
			DNode dataDNode = makeDNode( packageData.sampleData[index] , packageData );
			for (Option op : dataDNode.options)
			{
				if ( op.type.equals( option.type ) )
				{
					op.dif_btr_lvl = diff_btr_lvl;
					op.diff_mem = diff_mem;
					op.diff_time = compuation_time;
					op.mrg_diff_btr_lvl = ( abs( diff_btr_lvl - option.dif_btr_lvl ) != 0 ) ? abs( diff_btr_lvl -
																																option.dif_btr_lvl )
							: op.mrg_diff_btr_lvl;
					op.mrg_diff_mem = ( abs( diff_mem - option.mrg_diff_mem ) != 0 ) ? abs( diff_mem -
																													option.mrg_diff_mem )
							: op.mrg_diff_mem;
					op.mrg_diff_time = ( abs( compuation_time - option.mrg_diff_time ) != 0 ) ? abs( compuation_time -
																																option.mrg_diff_time )
							: op.mrg_diff_time;
					break;
				}
			}
			packageDNode.listDNode_Data.add( dataDNode );
		}
		
	}
}
