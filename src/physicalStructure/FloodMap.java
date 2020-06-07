/**
 * Copyright (c) [2019] [Yared Abayneh Abebe]
 *
 * This file is part of Coupled_ABM-Flood_Model for Wilhelmsburg, Hamburg, Germany.
 * Coupled_ABM-Flood_Model is free software licensed under the CC BY-NC-SA 4.0
 * You are free to:
 *	 Share — copy and redistribute the material in any medium or format
 *   Adapt — remix, transform, and build upon the material
 * The licensor cannot revoke these freedoms as long as you follow the license terms.
 *	 Attribution — You must give appropriate credit, provide a link to the license, 
 *				  and indicate if changes were made. You may do so in any reasonable 
 *				  manner, but not in any way that suggests the licensor endorses you 
 *				  or your use.
 *	 NonCommercial — You may not use the material for commercial purposes.
 *	 ShareAlike — If you remix, transform, or build upon the material, you must distribute 
 *				 your contributions under the same license as the original. 
 *   Full license description: https://creativecommons.org/licenses/by-nc-sa/4.0/
 */
 
package physicalStructure;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.grid.io.GridFormatFinder;
//import org.geotools.data.simple.SimpleFeatureCollection;
//import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.factory.Hints;
import org.geotools.gce.arcgrid.ArcGridReader;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffWriteParams;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.geometry.DirectPosition2D;
//import org.geotools.process.raster.PolygonExtractionProcess;
import org.geotools.referencing.CRS;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
//import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.DirectPosition;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

//import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * This class has two methods. The first one, getFloodDepth(String, Geometry), loads and reads computed flood maps at a given tick 
 * and returns the flood depth of a house. The flood depth is the maximum of the flood depths read at the vertices of a polygon feature
 * that represents the house. It is called by every household agent from the GlobalVariables.updateAgentAttributes() method.
 * 
 * The second method, convertASCIIToRaster(), is used to convert an ASCII 2D hydrodynamic model result to a geotiff format that can
 * be read by Geotool library methods. This method runs only once. If the flood map changes due to changes in the urban environment
 * (for the same rainfall event), this method may need to be executed every time there is a flood event.
 */

public class FloodMap {
	// this method is called from GlobalVariables.updateAgentAttributes() method
	public static double getFloodDepth(String floodMap, Geometry geom) {
		double floodDepth = 0;
		double[] value = null;
		URL url = null;
		try {
			// Locate the raster file
			try {
				url = new File(floodMap).toURI().toURL();  
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}

			// Get the format (in this case Geotiff)
			AbstractGridFormat format = GridFormatFinder.findFormat(url);
		    GridCoverage2DReader reader = format.getReader(url);
		    
		    // access the grid data values using a two-dimensional grid coverage 
		    if (reader != null) {
		    	GridCoverage2D coverage = reader.read(null);
		    	Coordinate[] geomVertices = geom.getCoordinates();
		    	// The flood depth is the maximum of the flood depths read at the vertices of a polygon feature that represents the house.
		    	for (Coordinate coordinate : geomVertices) {
		    		DirectPosition pos = new DirectPosition2D(coordinate.x, coordinate.y);
		    		value = (double[])coverage.evaluate(pos);
		    		if (floodDepth < value[0]) {
		    			floodDepth = value[0];
		    		}
		    	}
		    } else {
		    	throw new IOException("No reader");
		    }
		} catch (IOException e) {
			System.out.println("flood map is not available");
			e.printStackTrace();
		}
		return floodDepth;
	}

	/***********************************************************************************************
	***********************************************************************************************/
	// This method uses geotools GeoTiffWriter to convert maximum depth ascii grid format to geotiff. We use this method only once at the
	// beginning of the simulation
	//@ScheduledMethod(start=1, priority = 45)
	public static void convertASCIIToRaster() throws IOException {
		URL url = null;
		try {
			url = new File("data/SDK/Wilhelmsburg_XR_2010_C_Mesh_23_ManningM_6-60_inundation_2mGrid.asc").toURI().toURL();  // TODO: assign the file name to a final string variable
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		
	    // if the CRS of the ASCII file is known, set the CRS manually using CRS Hints
		CoordinateReferenceSystem crs = null;
	    try {
	    	//String code = CRS.lookupIdentifier(crs, true);  // get EPSG of a projection
			crs = CRS.decode("EPSG:25832");  // EPSG:32620 corresponds to UTM20 North
		} catch (NoSuchAuthorityCodeException e) {
			e.printStackTrace();
		} catch (FactoryException e) {
			e.printStackTrace();
		}
	    
	    // create hints with default CRS key and pass the value as default CRS of the reader 
	    Hints hints = new Hints(Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM, crs);
	    ArcGridReader ascReader = new ArcGridReader(url, hints);
        
	    if (ascReader != null) {
		    GridCoverage2D coverage = (GridCoverage2D) ascReader.read(null);
		    // output file and delete if it already exists (to avoid re-writing)
		    File floodMap_tiff = new File("data/SDK/Wilhelmsburg_XR_2010_C_Mesh_23_ManningM_6-60_inundation_2mGrid.tif");  // TODO: assign the file name to a final string variable
	        if (floodMap_tiff.exists())
	            floodMap_tiff.delete();
	        //getting a format
	        GeoTiffFormat tiffFormat = new GeoTiffFormat();
	        //getting the write parameters
	        GeoTiffWriteParams wp = new GeoTiffWriteParams();
	        //setting compression to LZW
	        wp.setCompressionMode(GeoTiffWriteParams.MODE_EXPLICIT);
	        wp.setCompressionType("LZW");
	        //setting the write parameters for this geotiff
            final ParameterValueGroup params = tiffFormat.getWriteParameters();
            params.parameter(AbstractGridFormat.GEOTOOLS_WRITE_PARAMS.getName().toString()).setValue(wp);
	        
	        GeoTiffWriter tiffWriter = (GeoTiffWriter)tiffFormat.getWriter(floodMap_tiff);
	        tiffWriter.write(coverage, (GeneralParameterValue[]) params.values().toArray(new GeneralParameterValue[1]));
	    } 
 	}
}
