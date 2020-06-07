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

package wilhelmsburg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import operationalStructure.AssessDamage;
import physicalStructure.Flood;
import physicalStructure.House;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;

/**
 * This class updates the household agents (houses) attributes.
 *
 */
public class GlobalVariables {
	private static List<House> existingHouseList = new ArrayList<House>(); // a list of all existing houses
	// this stores measure types and cumulative frequency of each measure implemented
	private static HashMap<String, Integer> primaryMeasure_frequency = new HashMap<String, Integer>(); 
	// this stores house category and the frequency of the secondary measure (MEASURE5) implemented by each house category
	private static HashMap<String, Integer> secondaryMeasure_frequency = new HashMap<String, Integer>(); 
	private static String socialNetwork_SingleFamily;
	private static String socialNetwork_Bungalow;
	private static String socialNetwork_GardenHouse;
	private static String socialNetwork_ApartmentHighrise;
	private static String socialNetwork_IBA;
	
	private static final String XR2010A = "data/GIS_data/Wilhelmsburg_XR_2010_A.tif";  // scenario 1
	private static final String XR2010B = "data/GIS_data/Wilhelmsburg_XR_2010_B.tif";  // scenario 2
	private static final String XR2010C = "data/GIS_data/Wilhelmsburg_XR_2010_C.tif";  // scenario 3

	private static final String HIGH = "High";
	private static final String MEDIUM = "Medium";
	private static final String LOW = "Low";  
	private static final String YES = "Yes";
	private static final String NO = "No";
	private static final String UNCERTAIN = "Uncertain";
	private static final String OWN = "Own";
	private static final String RENTED = "Rented";
	private static final String AUTHORITIES = "Authorities";
	private static final String OTHERS = "Others";
	
	private static final String SINGLE_FAMILY = "Single family";
	private static final String BUNGALOW = "Bungalow";
	private static final String GARDEN_HOUSE = "Garden house";
	private static final String APARTMENT = "Apartment";
	private static final String HIGHRISE_BUILDING = "Highrise building";
	private static final String IBA = "IBA";

	// primary, permanent (semi-permanent) measures
	private static final String MEASURE1 = "IUHS";  // install utilities in higher storeys (e.g., heating, energy, gas and water supply installations in higher floors) 
	private static final String MEASURE2_I = "FAIF_I";  // flood adapted interior fitting (e.g., walls and floors made of waterproofed building materials)
	private static final String MEASURE2_B = "FAIF_B";  // flood adapted interior fitting (e.g., walls and floors made of waterproofed building materials)
	// primary, temporary measures
	private static final String MEASURE3 = "FB1";  // flood barrier (flood protection walls) 
	private static final String MEASURE4 = "FB2";  // flood barrier (sandbags, and water tight windows and door sealings) 
	// secondary, temporary measure 
	private static final String MEASURE5 = "AF";  // adapted furnishing (furniture raised to avoid flood damage, raised electrical appliances) 
	
	private static final String NO_MEASURE = "No measure";
	
	// the number of houses per category is hard-coded as the building stock does not change throughout the simulation period.
	private static final int TOTAL_SINGLEFAMILY = 3219;
	private static final int TOTAL_BUNGALOW = 792;
	private static final int TOTAL_GARDENHOUSE = 2907;
	private static final int TOTAL_APARTMENT_HIGHRISEBUILDING = 921;
	private static final int TOTAL_IBA = 20;


	/** update house attributes that do not depend on the occurrence of a flood event **/
	// since agent attributes that are updated in this method are initialised at t=0, we start to update them at t=1
	@ScheduledMethod(start=1, interval=1, priority = 45)
	public static void updateAgentAttributes1() {
		/** 
		 * @params: CCthreshold_Yes & CCthreshold_Uncertain
		 * These parameters define the initial percentage of households with YES and UNCERTAIN climate change perception. The base 
		 * values are based on the NatCen Social Research, 2017. If the sum of the two parameters is less than 100%, the remaining is the 
		 * percentage of people who do not perceive climate change as a source of threat. The parameters are set in the Repast Simphony 
		 * Runtime Environment as "CC perception Yes threshold (%)"and "CC perception uncertain threshold (%)". Both parameters are of type 
		 * double.
		 * 
		 * @params: CC_Update
		 * The perception of agents on climate change as a source of threat changes over time. The CC_update parameter defines this change. 
		 * Assuming that agents may update their CC attribute at least once every CC_Update years, there is a probability of 1/CC_Update at
		 * every time step to update the attribute. The parameter is set in Repast Simphony Runtime Environment as "CC perception update 
		 * (years)" and is of type int.
		 * 
		 * @params: SoI_Authorities
		 * This parameter defines the initial percentage of households whose source of information regarding flood risk management is the 
		 * authorities. The base value is based on modeller's estimation. The parameter is set in Repast Simphony Runtime Environment  
		 * as "Source of information from authorities (%)" and is of type double.
		 * 
		 * @params: SoI_Update
		 * Agents source of information may also change over the simulation period. The SoI_update parameter defines this change. Assuming 
		 * that agents may update their SoI at least once every SoI_Update years, there is a probability of 1/SoI_Update at every time step 
		 * to update the attribute. The parameter is set in Repast Simphony Runtime Environment as "Source of information update (years)" and 
		 * is of type int. 
		 * 
		 * @params: updateHO
		 * This parameter defines the change in house ownership of a percentage of household agents. The parameter is set in Repast Simphony 
		 * Runtime Environment as "Update house ownership" and is of type double.
		 * 
		 * @params: updateHI
		 * This parameter defines the change in income of a percentage of household agents. The parameter is set in Repast Simphony Runtime 
		 * Environment as "Update household income" and is of type double.
		 */
		
		Parameters params = RunEnvironment.getInstance().getParameters();
		int CC_Update = (int)params.getValue("CC_Update");
		int SoI_Update = (int)params.getValue("SoI_Update");
		double iniPercentageHHwithCCthreshold_Yes = (double)params.getValue("CCthreshold_Yes");
		double iniPercentageHHwithCCthreshold_Uncertain = (double)params.getValue("CCthreshold_Uncertain");
		double iniPercentageHHwithCCthreshold_No = iniPercentageHHwithCCthreshold_Yes + iniPercentageHHwithCCthreshold_Uncertain;
		double iniPercentageHHwithSoI_Authorities = (double)params.getValue("SoI_Authorities");
		double updateHO = (double)params.getValue("updateHO");
		double updateHI = (double)params.getValue("updateHI");

		// update the perceptionCC, SoI, house ownership, household income, flood depth, and building and content damages attributes of households. 
		for (int i = 0; i < GlobalVariables.getExistingHouseList().size(); i++) {
			House house = GlobalVariables.getExistingHouseList().get(i);
			double random1 = RandomHelper.nextDouble();
			double random2 = RandomHelper.nextDouble();
			
			// update CC perception based on the CC_Update parameter
			double probability_perceptionCCupdate = 1.0/CC_Update;
			if (random1 <= probability_perceptionCCupdate) {
				if (random2 <= iniPercentageHHwithCCthreshold_Yes) {  
					house.setPerceptionCC(YES);
				} else if (iniPercentageHHwithCCthreshold_Yes < random2 && random2 <= iniPercentageHHwithCCthreshold_No) {
					house.setPerceptionCC(UNCERTAIN);
				} else {
					house.setPerceptionCC(NO);
				}
			}
			
			// update SoI based on the SoI_Update parameter
			double probability_SoIupdate = 1.0/SoI_Update;
			if (random1 <= probability_SoIupdate) {
				if (random2 <= iniPercentageHHwithSoI_Authorities) {  
					house.setInfoSource(AUTHORITIES);
				} else {
					house.setInfoSource(OTHERS);
				}
			}
			
			// update house ownership based on the updateHO parameter
			double random3 = RandomHelper.nextDouble();
			if (random3 <= updateHO) {
				if (house.getHouseOwnership().equals(OWN)) {
					house.setHouseOwnership(RENTED);
				} else if (house.getHouseOwnership().equals(RENTED)) {
					house.setHouseOwnership(OWN);
				}
			}

			// update household income based on the updateHI parameter
			double random4 = RandomHelper.nextDouble();
			if (random4 <= updateHI) {
				if (house.getHouseholdIncome().equals(HIGH)) {
					house.setHouseholdIncome(LOW);
				} else if (house.getHouseholdIncome().equals(LOW)) {
					house.setHouseholdIncome(HIGH);
				}
			}
			
			// set the flood depth and the damages to zero at the beginning of each time step. If there is flood in a given time step,
			// these attributes are updated in the updateAgentAttributes2 method below.
			house.setFloodDepth(0.0);
			house.setBuildingDamage(0.0);
			house.setBuildingDamageReduced(0.0);
			house.setContentDamage(0.0);
			house.setContentDamageReduced(0.0);
		}
	}

	
	/** Update the attributes that change only if there is a storm surge at the "current" time step **/
	// since there is no flooding at t=0, we start this method at t=1
	@ScheduledMethod(start=1, interval=1, priority = 20)
	public static void updateAgentAttributes2() {
		final double floodThreshold = 0.1;  // This is an assumption that every house is elevated by 10cm. The 0.1 value is based on the damage curves.
		final double maxStructuralMeasureHeight = 1.0; // The maximum height of dry waterproofing is approximately 1m above the ground (see Kreibich et al 2005, p.119)
		int tick = (int)RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		int stormSurgeEvent = Flood.getEventTimeSeries().get(tick-1);  // as the list index starts at zero, the index equivalent to the tick is at tick-1

		/** 
		 * @params: stateSubsidy_scenario
		 * This is a policy lever that defines the type of subsidy the State of Hamburg provides. It has a value of 1, 2 and 3 representing no subsidy,    
		 * subsidy only for flooded houses and subsidy for all agents, respectively. The parameter is set in the Repast Simphony Runtime Environment 
		 * as "State subsidy scenario (1, 2 or 3)" and is of type int.
		 */

		Parameters params = RunEnvironment.getInstance().getParameters();
		int stateSubsidy_scenario = (int)params.getValue("stateSubsidy_scenario");

		// check if there is a storm surge event at the "current" time step. If so, update the following attributes: flood depth, floodExperience, 
		// floodedBefore, subsidy and the attributes related to damage
		if (stormSurgeEvent > 0) {
			String floodMap = null;
			if (stormSurgeEvent == 1) {
				floodMap = XR2010A;
			} else if (stormSurgeEvent == 2) {
				floodMap = XR2010B;  
			} else if (stormSurgeEvent == 3) {
				floodMap = XR2010C;
			} 
			for (int i = 0; i < GlobalVariables.getExistingHouseList().size(); i++) {
				House house = GlobalVariables.getExistingHouseList().get(i);
				// assess flood depth 
				double floodDepth = physicalStructure.FloodMap.getFloodDepth(floodMap, house.getGeom());

				// update the floodExperience house attribute. Assumption: all agents that live in Wilhelmsburg when a flood event occurs have 
				// flood experience although they may not be directly flooded. 
				house.setFloodExperience(true);

				// Compute the structural and inventory damage of the house. The attributes are updated in the respective methods.
				// And, update the floodedBefore (this is a direct flood experience) and stateSubsidy house attributes. 
				if (floodDepth > floodThreshold) {
					house.setFloodDepth(floodDepth);
					double damageB = AssessDamage.computeBuildingDamage(house);
					double damageC = AssessDamage.computeContentDamage(house);
					house.setBuildingDamage(damageB);
					house.setContentDamage(damageC);

					// For those houses that implemented a measure, recalculate the damage considering the damage saved by implementing a measure. 
					// MEASURE1 (install utilities in higher storeys) reduces the structural damage by 36% while it does not have effect on reducing the inventory damage
					// MEASURE2_I & MEASURE2_B (adapted interior fittings) reduces both the building and inventory damages by 53%  
					// MEASURE3 (flood barriers: flood protection walls) reduces the flood depth by the maxStructuralMeasureHeight
					// MEASURE4 (flood barriers: sandbags & water tight windows and door sealings) reduces the structural damage by 29% while it does not have effect on reducing the inventory damage.
					// MEASURE5 (adapted furnishing) does not contribute to reducing structural measures, but it reduces the inventory damage by 77%
					if (house.isImplementAdaptationMeasure()) {
						if (house.getPrimaryMeasure().equals(MEASURE3)) {
							if (floodDepth > maxStructuralMeasureHeight) {
								// update the house floodDepth and direct flood experience attributes
								floodDepth = floodDepth - maxStructuralMeasureHeight;
								house.setFloodDepth(floodDepth);
								house.setFloodedBefore(true);
								double damageB_prime = AssessDamage.computeBuildingDamage(house);
								double damageC_prime = AssessDamage.computeContentDamage(house);
								double damageB_saved = damageB - damageB_prime;
								double damageC_saved = damageC - damageC_prime;
								house.setBuildingDamage(damageB_prime);
								house.setBuildingDamageReduced(damageB_saved);
								house.setContentDamage(damageC_prime);
								house.setContentDamageReduced(damageC_saved);
							} else {
								// update the house floodDepth attribute. the direct flood experience attribute stays the same because if the
								// house was flooded before, it had direct experience (TRUE) and if not directly flooded before, the attribute
								// stays as FALSE
								floodDepth = 0.0;
								house.setFloodDepth(floodDepth);
								house.setBuildingDamage(0.0);
								house.setBuildingDamageReduced(damageB);
								house.setContentDamage(0.0);
								house.setContentDamageReduced(damageC);
							}
						} else {
							// update the household direct flood experience attributes
							house.setFloodedBefore(true);
							// recalculate damages
							if (house.getPrimaryMeasure().equals(MEASURE1)) {
								house.setBuildingDamage(damageB * 0.64);
								house.setBuildingDamageReduced(damageB * 0.36);
							} else if (house.getPrimaryMeasure().equals(MEASURE2_I) || house.getPrimaryMeasure().equals(MEASURE2_B)) {
								house.setBuildingDamage(damageB * 0.47);
								house.setBuildingDamageReduced(damageB * 0.53);
								house.setContentDamage(damageC * 0.47);
								house.setContentDamageReduced(damageC * 0.53);
							} else if (house.getPrimaryMeasure().equals(MEASURE4)) {
								house.setBuildingDamage(damageB * 0.71);
								house.setBuildingDamageReduced(damageB * 0.29);
							} 
						}
						
						// since all houses that implemented a secondary measure should have a primary one, there is no need to update
						// house floodDepth and direct flood experience attributes
						if (house.getSecondaryMeasure().equals(MEASURE5)) {
							house.setContentDamage(damageC * 0.23); 
							house.setContentDamageReduced(damageC * 0.77);
						}					
					} else {
						// if agents have not implemented any adaptation measure or if they already abandoned measures, there is no need to recalculate 
						// actual and saved damages. We only update the household direct flood experience attributes.
						house.setFloodedBefore(true);
					}

					// If the state provides subsidy for houses that are directly flooded (stateSubsidy_scenario == 2), update that attribute. In the other
					// scenarios, the policy lever affects all the agents and hence the value at the initialisation stays the same irrespective of agents
					// direct flood experience. 
					// As all houses within the outer if-statement experience flood, there is no need to check the floodedBefore attribute.
					if (stateSubsidy_scenario == 2) {
						house.setStateSubsidy(true); 
					}
				}

				// update the reliancePublicProtection house attribute
				if (house.isFloodExperience()) {
					if (house.isFloodedBefore()) {
						house.setReliancePublicProtection(LOW);
					} else {
						house.setReliancePublicProtection(MEDIUM);
					}
				}  // else (if the agent does not have flood experience), the reliance on public protection stays as before.
			}  // else (if stormSurgeScenario = 0), all the attributes mentioned in the if-block stay the same 
		}
	}

	
	/***********************************************************************************************
	***********************************************************************************************/
	// This method updates the proportion of households that implemented an adaptation measure from the total
	// number of houses of the same category. The assumption is that agents can be influenced by all the agents
	// in the same category.
	@ScheduledMethod(start=0, interval=1, priority = 15)
	public static void updateSocialNetwork() {
		/** 
		 * @params: socialNetwork_threshold
		 * This parameter is used to evaluate whether "most" household agents implement a measure and others will follow suit. If the percentage of    
		 * agents who implement a measure in a house category is greater than this parameter, the social network attribute of all households in that
		 * house category is changed to "High". The base value of the parameter is based on the modeller's expert estimation. The parameter is set in 
		 * the Repast Simphony Runtime Environment as "Social network threshold (%)" and is of type double.
		 */

		Parameters params = RunEnvironment.getInstance().getParameters();
		double socialNetwork_threshold = (double)params.getValue("socialNetwork_threshold");
		for (Map.Entry<String, Integer> e : primaryMeasure_frequency.entrySet()) {
			if (e.getKey().equals(MEASURE1)) {
				double proportion = ((double)e.getValue())/TOTAL_SINGLEFAMILY;
				if (proportion > socialNetwork_threshold) {
					socialNetwork_SingleFamily = GlobalVariables.getHigh();
				} else {
					socialNetwork_SingleFamily = GlobalVariables.getLow();
				}
			} else if (e.getKey().equals(MEASURE2_I)) {
				double proportion = ((double)e.getValue())/TOTAL_IBA;
				if (proportion > socialNetwork_threshold) {
					socialNetwork_IBA = GlobalVariables.getHigh();
				} else {
					socialNetwork_IBA = GlobalVariables.getLow();
				}
			} else if (e.getKey().equals(MEASURE2_B)) {
				double proportion = ((double)e.getValue())/TOTAL_BUNGALOW;
				if (proportion > socialNetwork_threshold) {
					socialNetwork_Bungalow = GlobalVariables.getHigh();
				} else {
					socialNetwork_Bungalow = GlobalVariables.getLow();
				}
			} else if (e.getKey().equals(MEASURE3)) {
				double proportion = ((double)e.getValue())/TOTAL_APARTMENT_HIGHRISEBUILDING;
				if (proportion > socialNetwork_threshold) {
					socialNetwork_ApartmentHighrise = GlobalVariables.getHigh();
				} else {
					socialNetwork_ApartmentHighrise = GlobalVariables.getLow();
				}
			} else if (e.getKey().equals(MEASURE4)) {
				double proportion = ((double)e.getValue())/TOTAL_GARDENHOUSE;
				if (proportion > socialNetwork_threshold) {
					socialNetwork_GardenHouse = GlobalVariables.getHigh();
				} else {
					socialNetwork_GardenHouse = GlobalVariables.getLow();
				}
			} 
		}
	}

	/***********************************************************************************************
	*********************************** Getters and Setters ****************************************
	***********************************************************************************************/
	public static List<House> getExistingHouseList() {
		return existingHouseList;
	}

	public static void setExistingHouseList(List<House> existingHouseList) {
		GlobalVariables.existingHouseList = existingHouseList;
	}

	public static HashMap<String, Integer> getPrimaryMeasure_frequency() {
		return primaryMeasure_frequency;
	}

	public static void setPrimaryMeasure_frequency(HashMap<String, Integer> primaryMeasure_frequency) {
		GlobalVariables.primaryMeasure_frequency = primaryMeasure_frequency;
	}

	public static HashMap<String, Integer> getSecondaryMeasure_frequency() {
		return secondaryMeasure_frequency;
	}

	public static void setSecondaryMeasure_frequency(HashMap<String, Integer> secondaryMeasure_frequency) {
		GlobalVariables.secondaryMeasure_frequency = secondaryMeasure_frequency;
	}

	public static String getSocialNetwork_SingleFamily() {
		return socialNetwork_SingleFamily;
	}

	public static void setSocialNetwork_SingleFamily(String socialNetwork_SingleFamily) {
		GlobalVariables.socialNetwork_SingleFamily = socialNetwork_SingleFamily;
	}

	public static String getSocialNetwork_Bungalow() {
		return socialNetwork_Bungalow;
	}

	public static void setSocialNetwork_Bungalow(String socialNetwork_Bungalow) {
		GlobalVariables.socialNetwork_Bungalow = socialNetwork_Bungalow;
	}

	public static String getSocialNetwork_GardenHouse() {
		return socialNetwork_GardenHouse;
	}

	public static void setSocialNetwork_GardenHouse(String socialNetwork_GardenHouse) {
		GlobalVariables.socialNetwork_GardenHouse = socialNetwork_GardenHouse;
	}

	public static String getSocialNetwork_ApartmentHighrise() {
		return socialNetwork_ApartmentHighrise;
	}

	public static void setSocialNetwork_ApartmentHighrise(String socialNetwork_ApartmentHighrise) {
		GlobalVariables.socialNetwork_ApartmentHighrise = socialNetwork_ApartmentHighrise;
	}

	public static String getSocialNetwork_IBA() {
		return socialNetwork_IBA;
	}

	public static void setSocialNetwork_IBA(String socialNetwork_IBA) {
		GlobalVariables.socialNetwork_IBA = socialNetwork_IBA;
	}

	public static String getHigh() {
		return HIGH;
	}

	public static String getMedium() {
		return MEDIUM;
	}

	public static String getLow() {
		return LOW;
	}

	public static String getYes() {
		return YES;
	}

	public static String getNo() {
		return NO;
	}

	public static String getUncertain() {
		return UNCERTAIN;
	}

	public static String getOwn() {
		return OWN;
	}

	public static String getRented() {
		return RENTED;
	}

	public static String getAuthorities() {
		return AUTHORITIES;
	}

	public static String getOthers() {
		return OTHERS;
	}

	public static String getSingleFamily() {
		return SINGLE_FAMILY;
	}

	public static String getBungalow() {
		return BUNGALOW;
	}

	public static String getGardenHouse() {
		return GARDEN_HOUSE;
	}

	public static String getApartment() {
		return APARTMENT;
	}

	public static String getHighriseBuilding() {
		return HIGHRISE_BUILDING;
	}

	public static String getIBA() {
		return IBA;
	}

	public static String getMeasure1() {
		return MEASURE1;
	}

	public static String getMeasure2_I() {
		return MEASURE2_I;
	}

	public static String getMeasure2_B() {
		return MEASURE2_B;
	}

	public static String getMeasure3() {
		return MEASURE3;
	}

	public static String getMeasure4() {
		return MEASURE4;
	}

	public static String getMeasure5() {
		return MEASURE5;
	}

	public static String getNoMeasure() {
		return NO_MEASURE;
	}
	
}
