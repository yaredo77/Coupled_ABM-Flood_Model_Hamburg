/**
 * Copyright (c) [2019] [Yared Abayneh Abebe]
 *
 * This file is part of Coupled_ABM-Flood_Model for Wilhelmsburg, Hamburg, Germany.
 * Coupled_ABM-Flood_Model is free software licensed under the CC BY-NC-SA 4.0
 * You are free to:
 *	 Share � copy and redistribute the material in any medium or format
 *   Adapt � remix, transform, and build upon the material
 * The licensor cannot revoke these freedoms as long as you follow the license terms.
 *	 Attribution � You must give appropriate credit, provide a link to the license, 
 *				  and indicate if changes were made. You may do so in any reasonable 
 *				  manner, but not in any way that suggests the licensor endorses you 
 *				  or your use.
 *	 NonCommercial � You may not use the material for commercial purposes.
 *	 ShareAlike � If you remix, transform, or build upon the material, you must distribute 
 *				 your contributions under the same license as the original. 
 *   Full license description: https://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package wilhelmsburg;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
//import java.util.Map;
//
//import org.apache.commons.collections15.Transformer;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

//import edu.uci.ics.jung.algorithms.metrics.Metrics;
//import edu.uci.ics.jung.algorithms.shortestpath.DistanceStatistics;
//import edu.uci.ics.jung.graph.Graph;
import physicalStructure.Flood;
import physicalStructure.House;
import repast.simphony.context.Context;
import repast.simphony.context.space.gis.GeographyFactoryFinder;
//import repast.simphony.context.space.graph.ContextJungNetwork;
//import repast.simphony.context.space.graph.NetworkBuilder;
//import repast.simphony.context.space.graph.NetworkGenerator;
//import repast.simphony.context.space.graph.WattsBetaSmallWorldGenerator;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.gis.GeographyParameters;
//import repast.simphony.space.graph.RepastEdge;

/**
 * This class builds the Context and Geography of RS on which agents will be populated first. Then, loads shapefiles of polygon feature
 * that instantiates the household agents and their houses.
 *
 * Assumption:
 * 			- The whole building is considered as one household agent for houses which are not categorised as detached. This is due to 
 * 			  the lack of data on the number of apartments in each building.
 * 			- Each polygon feature represents one household agent. In the case of multi-storey buildings, the agent represents the household(s) 
 * 			  living on the ground floor.
 * 
 */
public class WilhelmsburgBuilder implements ContextBuilder<Object>{
	public static Geography<Object> geography;
	public static Context<Object> context;
	private static final int COMPUTATION_TIME = 50;
	// these strings represent the field names of the input shapefiles 
	private static final String TYPENUMBER = "Typ_Nr";
	private static final String MUSTERHAUS = "musterhaus";
	private static final String HOUSECATEGORY = "HouseCat";
	private static final String HOUSEID = "HouseID";
	private static final String AREA = "AREA";
	private static final String SHAPEAREA = "Shape_Area";

	@Override
	public Context<Object> build(Context<Object> con) {
		// create geography projection
		context = con;
		GeographyParameters<Object> geoParams = new GeographyParameters<Object>();
		geography = GeographyFactoryFinder.createGeographyFactory(null).createGeography("Geography", context, geoParams);
		geography.setCRS("EPSG:25832");
		
		// Clear static data members to freshly initialise the context
		Flood.getEventTimeSeries().clear();
		GlobalVariables.getExistingHouseList().clear();
		GlobalVariables.getPrimaryMeasure_frequency().clear();
		GlobalVariables.getSecondaryMeasure_frequency().clear();
		GlobalVariables.setSocialNetwork_ApartmentHighrise(null);
		GlobalVariables.setSocialNetwork_GardenHouse(null);
		GlobalVariables.setSocialNetwork_IBA(null);
		GlobalVariables.setSocialNetwork_SingleFamily(null);
		GlobalVariables.setSocialNetwork_Bungalow(null);
		
		// Load Features from shapefiles and add them (except all the building location point features) to the context and geography created above 
		loadFeatures("data/GIS_data/Wohnbebauung_KGV_Wsbg_utm.shp", context, geography, 0); // All buildings on the island (polygon)
		loadFeatures("data/GIS_data/Whmbg_neue_Wohngebaeude.shp", context, geography, 1); // New development locations on the island (polygon)
	
		/************************
		 * The code below creates a small-world network of agents randomly based on an average degree and a rewinding probability specified.
		 * Since we do not have enough information to instantiate the graph, we decided not to implement the network in this model. This is mainly to
		 * reduce the level of assumptions and uncertainties while developing the model.
		 * 
		// get the average degree and rewinding probabilities of the network 
		Parameters params = RunEnvironment.getInstance().getParameters();
		double beta = (double)params.getValue("beta");
		int degree = (int)params.getValue("degree");
		
		// create an undirected small-world network of given rewinding probability (beta) and average degree of the network
		NetworkGenerator<Object> networkGenerator = new WattsBetaSmallWorldGenerator<Object>(beta, degree, false);
		NetworkBuilder<Object> networkBuilder = new NetworkBuilder<Object> ("Small-world network", context, false);
		networkBuilder.setGenerator(networkGenerator);
		ContextJungNetwork<Object> network = (ContextJungNetwork<Object>) networkBuilder.buildNetwork(); // cast repast networks (Network<T>) to a JUNG network (ContextJungNetwork<T>)
		
		// convert the ContextJungNetwork to a JUNG graph to calculate graph statistics and metrices
		Graph<Object, RepastEdge<Object>> graph = network.getGraph();
		
		// compute the clustering coefficient (CC): compute the CC of each vertex and then compute the average CC (which is the CC of the graph)   
		Map<Object, Double> ccVertices = Metrics.clusteringCoefficients(graph);
		double cc_vSum = 0;
		for (double cc_v : ccVertices.values()) {
			cc_vSum += cc_v;
		}
		double ccGraph = cc_vSum/ccVertices.values().size();
		
		// compute the average distance
		Transformer<Object, Double> LL = DistanceStatistics.averageDistances(graph);
		
		
		// for all the house objects in the context, define their neighbours
		for (Object obj : context) {
			if (obj instanceof House) {  // this is an extra check but the only Objects in the context are of House objects
				House house = (House) obj;  // parse obj to a House Class
				// find the neighbours (adjacent) nodes to each node
				Iterable<Object> iterableNeighbours = network.getAdjacent(house);
				for (Object neighbour : iterableNeighbours) {
					house.getNeighbours().add((House)neighbour);
				}
			}
		}
		**************************/
 
		// Terminate the simulation after 50 ticks
		RunEnvironment.getInstance().endAt(COMPUTATION_TIME);
		
		return context;
	}
	
	/***********************************************************************************************
	***********************************************************************************************/
	private void loadFeatures (String filename, Context<Object> context, Geography<Object> geography, int flag){
		
		// Locate the shapefile
		URL url = null;
		try {
			url = new File(filename).toURI().toURL();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		List<SimpleFeature> features = new ArrayList<SimpleFeature>();
		
		// Try to load the shapefiles
		SimpleFeatureIterator fiter = null;
		ShapefileDataStore store = null;
		store = new ShapefileDataStore(url);

		try {
			fiter = store.getFeatureSource().getFeatures().features();

			while(fiter.hasNext()){
				features.add(fiter.next());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			fiter.close();
			store.dispose();
		}
		
		/** read the initial conditions 
		 * @params: FEthreshold_Yes
		 * This parameter defines the initial percentage of households with flood experience. The base value is based on the 2011 
		 * census data (age group in Wilhelmsburg) and the last major flood in Wilhelmsburg. The parameter is set in the Repast Simphony
		 * Runtime Environment as "Flood Experience Yes threshold (%)" and is of type double.
		 * 
		 * @params: CCthreshold_Yes & CCthreshold_Uncertain
		 * These parameters define the initial percentage of households with YES and UNCERTAIN climate change perception. The base 
		 * values are based on the NatCen Social Research, 2017. If the sum of the two parameters is less than 100%, the remaining is the 
		 * percentage of people who do not perceive climate change as a source of threat. The parameters are set in the Repast Simphony 
		 * Runtime Environment as "CC perception Yes threshold (%)"and "CC perception uncertain threshold (%)". Both parameters are of type 
		 * double.
		 * 
		 * @params: SoI_Authorities
		 * This parameter defines the initial percentage of households in which their source of information regarding flood risk management
		 * is from the authorities. The default value is based on modeller's estimation. The parameter is set in Repast Simphony Runtime 
		 * Environment as "Source of information from authorities (%)" and is of type double.
		 * 
		 * @params: HO_Own
		 * This parameter defines the initial percentage of households that own the houses they occupy. The default value is based on  
		 * the 2011 census data (apartments according to use). The parameter is set in the Repast Simphony Runtime Environment as "House 
		 * ownership Own(%)" and is of type double.
		 * 
		 * @params: HI_Low
		 * This parameter defines the initial percentage of households with LOW income. The default value is based on the 2011 census   
		 * data (apartments according to use). The parameter is set in the Repast Simphony Runtime Environment as "House ownership Own(%)" 
		 * and is of type double.
		 * 
		 * @params: stateSubsidy_scenario
		 * This parameter defines the State subsidy scenario. It has a value of 1, 2 or 3 representing no subsidy, subsidy only for flooded  
		 * houses and subsidy for all houses that consider flood as a threat. The parameter is set in the Repast Simphony Runtime Environment  
		 * as "State subsidy scenario (1, 2 or 3)" and is of type int.
		 */
		
		Parameters params = RunEnvironment.getInstance().getParameters();
		double iniPercentageHHwithFE_Yes = (double)params.getValue("FEthreshold_Yes");
		double iniPercentageHHwithCCthreshold_Yes = (double)params.getValue("CCthreshold_Yes");
		double iniPercentageHHwithCCthreshold_Uncertain = (double)params.getValue("CCthreshold_Uncertain");
		double iniPercentageHHwithSoI_Authorities = (double)params.getValue("SoI_Authorities");
		double iniPercentageHHwithHO_Own = (double)params.getValue("HO_Own");
		double iniPercentageHHwithHI_Low = (double)params.getValue("HI_Low");
		int stateSubsidy_scenario = (int)params.getValue("stateSubsidy_scenario");
		
		// For each feature in the file
		for (SimpleFeature feature : features){
			Geometry geom = (Geometry)feature.getDefaultGeometry();
			
			if (!geom.isValid()) {
				throw new IllegalArgumentException ("Invalid geometry: " + feature.getID());
			}

			// create polygon features of existing buildings 
			if (geom instanceof MultiPolygon){
				MultiPolygon mp = (MultiPolygon)feature.getDefaultGeometry();
				geom = (Polygon)mp.getGeometryN(0);

				// Read the feature attributes and assign to the respective polygon feature
				String buildingType = null;
				double area = 0;
				if (flag == 0) {
					buildingType = (String) feature.getAttribute(TYPENUMBER);
					area = (double) feature.getAttribute(AREA);
				} else if (flag == 1) {
					buildingType = (String) feature.getAttribute(MUSTERHAUS);
					area = (double) feature.getAttribute(SHAPEAREA);
				} else {
					throw new IllegalArgumentException ("Define flag!" + filename);
				}
				String houseCategory = (String) feature.getAttribute(HOUSECATEGORY);
				String houseID = (String) feature.getAttribute(HOUSEID);
				
				// Initialise the other attributes
				if (!buildingType.isEmpty()) {
					// Initially, there is no flooding. Hence, the structural and inventory damages for all agents are zero.
					// Agents also do not need to implement measure.
					double floodDepth = 0.0;
					double buildingDamage = 0.0;
					double buildingDamageReduced = 0.0;
					double contentDamage = 0.0;
					double contentDamageReduced = 0.0;
					boolean implementAdaptationMeasure = false;
					boolean abandonPrimaryMeasure = false;
					boolean abandonSecondaryMeasure = false;
					String primaryMeasure = GlobalVariables.getNoMeasure();
					String secondaryMeasure = GlobalVariables.getNoMeasure();
					int abandonTick = 0;
					int abandonFrequencyPrimary = 0;
					int abandonFrequencySecondary = 0;
					
					// Only a percentage of households have flood experience, and that depends on the iniPercentageHHwithFE initial condition
					double random1 = RandomHelper.nextDouble();
					boolean floodExperience = false;
					if (random1 <= iniPercentageHHwithFE_Yes) {
						floodExperience = true;
					}
					
					// this is the personal flood experience (PFE) and initialised as "No" for all agents
					boolean floodedBefore = false; 
					
					// the reliance on public protection depends on the floodExperience and floodedBefore parameters
					String reliancePublicProtection = GlobalVariables.getHigh();  // if floodExperience = false, reliancePublicProtection = High
					if (floodExperience) {
						if (floodedBefore) {
							reliancePublicProtection = GlobalVariables.getLow();
						} else {
							reliancePublicProtection = GlobalVariables.getMedium();
						}
					}
					
					// the perception on CC depends on the iniPercentageHHwithCCthreshold parameters
					double iniPercentageHHwithCCthreshold_No = iniPercentageHHwithCCthreshold_Yes + iniPercentageHHwithCCthreshold_Uncertain;
					String perceptionCC = GlobalVariables.getNo();  // if random > iniPercentageHHwithCCthreshold_No, perceptionCC = No
					double random2 = RandomHelper.nextDouble();
					if (random2 <= iniPercentageHHwithCCthreshold_Yes) {
						perceptionCC = GlobalVariables.getYes();
					} else if (iniPercentageHHwithCCthreshold_Yes < random2 && random2 <= iniPercentageHHwithCCthreshold_No) {
						perceptionCC = GlobalVariables.getUncertain();
					}

					// the source of information is based on the iniPercentageHHwithSoI_authorities parameter
					String infoSource = GlobalVariables.getOthers();  // if random > iniPercentageHHwithSoI_authorities, infoSource = Others
					double random3 = RandomHelper.nextDouble();
					if (random3 <= iniPercentageHHwithSoI_Authorities) {
						infoSource = GlobalVariables.getAuthorities();
					}
					
					// the state subsidy is initialised based on the scenario set
					Boolean stateSubsidy = null;
					if (stateSubsidy_scenario == 1) {
						stateSubsidy = false;
					} else if (stateSubsidy_scenario == 2) {
						if (floodedBefore) {
							stateSubsidy = true;
						} else {
							stateSubsidy = false;
						}
					} else if (stateSubsidy_scenario == 3) {
						stateSubsidy = true;
					} else {
						throw new IllegalArgumentException ("Invalid State subsidy scenario. The value should be an integer between 1 and 3, inclusive.");
					}
					
					// the house ownership is randomised based on the iniPercentageHHwithHO_Own parameter
					String houseOwnership = GlobalVariables.getRented();  // if random > iniPercentageHHwithHO_Own, houseOwnership = rented
					double random4 = RandomHelper.nextDouble();
					if (random4 <= iniPercentageHHwithHO_Own) {
						houseOwnership = GlobalVariables.getOwn();
					}

					// the household income is randomised based on the iniPercentageHHwithHI parameters
					String householdIncome = GlobalVariables.getHigh();  // if random > iniPercentageHHwithCCthreshold_High, perceptionCC = No
					double random5 = RandomHelper.nextDouble();
					if (random5 <= iniPercentageHHwithHI_Low) {
						householdIncome = GlobalVariables.getLow();
					} 
					
					// initially, there is no social influence
					boolean socialNetworkInfluence = false;

					// threat and coping are initialised as false but will be appraised based on the threat and coping factors at t=1.
					boolean threat = false;
					boolean coping = false;

					//instantiate houses with the above initial values.
					House iniHouse = new House(buildingType, houseCategory, houseID, geom, area, floodDepth, buildingDamage, buildingDamageReduced, 
							contentDamage, contentDamageReduced, implementAdaptationMeasure, abandonPrimaryMeasure, abandonSecondaryMeasure, primaryMeasure, 
							secondaryMeasure, abandonTick, abandonFrequencyPrimary, abandonFrequencySecondary, floodExperience, reliancePublicProtection, 
							perceptionCC, infoSource, floodedBefore, stateSubsidy, houseOwnership, householdIncome, socialNetworkInfluence, threat, coping);
					
					// add the instantiated houses to the context and the geography
					context.add(iniHouse);
					geography.move(iniHouse, geom);
					// add the instantiated houses to the existingHouses list, as well
					GlobalVariables.getExistingHouseList().add(iniHouse);
				}
			} else { // If geometry is not a polygon feature, if it is undefined or if there is any issue with the geometry, print error.
				throw new IllegalArgumentException("Error creating agent for  " + geom + ". Check if geometry is not a polygon.");
			}
		}				
	}

	/***********************************************************************************************
	*********************************** Getters and Setters ****************************************
	***********************************************************************************************/

	public static int getComputationTime() {
		return COMPUTATION_TIME;
	}
}


