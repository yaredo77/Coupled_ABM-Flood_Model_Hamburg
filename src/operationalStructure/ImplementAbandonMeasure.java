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

package operationalStructure;

import physicalStructure.House;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import wilhelmsburg.GlobalVariables;

/**
 * This class executes the process between protection motivation behaviour and action (response). Since appraising coping does not necessarily
 * mean that households implement measures immediately, we introduced a delay parameter. If the value of the delay parameter is one year, the 
 * probability of adaptation in the next time step is one. The class also defines the implementation of measures based on the category of houses: 
 * 			- Single family houses install utilities in higher floors (Measure1); 
 * 			- IBA housings and bungalows implement flood adapted interior fittings (Measure2);
 * 			- Apartments and high-rise buildings implement flood barrier, especially flood protection walls (Measure3); 
 * 			- Garden houses implement flood barriers, especially sandbags and water tight windows and door sealings (Measure4); 
 * Agents may also implement secondary measure (i.e., adapted furnishing) if they have already implemented a primary measure, and if they live in 
 * one of the following house categories, in which majority have multiple floors: single family, IBA, apartment and high-rise buildings. Since most 
 * bungalows and garden houses do not have higher floors, it is not possible to implement adapted furnishing such as moving furniture to higher floors.
 * 
 * Assumption:
 * 			- Adaptation measures are effective to reduce flood damage in all flood events (perceived efficacy of measures).
 * 			- Agents are capable of successfully implementing adaptation measures (perceived self-efficacy).
 * 			- Agents do not implement temporary adaptation measures such as flood barriers and sandbags at any time step but deciding to implement 
 * 			  the measures entails they only deploy them when there is flood.
 * 			- If agents abandon measures, they only abandon non-permanent measures such as flood barriers.  
 * 			- In case of non-permanent measures, if a household agent decides to implement a measure, the decision is valid at least for a year.
 * 			- If a household agent abandons a measure, it abandons it for at least a year. 
 * 			- Household agents do not implement the same primary measure twice unless they abandon it.
 * 
 */

public class ImplementAbandonMeasure {

	// since we did not evaluate depending on the agent attributes at t=0 (initial conditions specified at the WilhelmsburgBuilder class),
	// we start this method from t=0
	@ScheduledMethod(start=0, interval=1, priority = 25)
	public static void intentionToAction() {
		/** 
		 * @params: delayParameter
		 * This parameter represents the average number of years agents take to transform the protection motivation behaviour into an action. 
		 * The probability that a motivated individual will adapt at a given year is computed as 1/delayParameter. The parameter is set in the  
		 * Repast Simphony Runtime Environment as "Delay parameter (years)" and is of type int.
		 * 
		 * @params: secondaryMeasureParameter
		 * This parameter is used to define the number of agents that implement a secondary measure. Agents implement a secondary measure only if they 
		 * already implemented a primary measure. The default value is based on the modeller's expert estimation. The parameter is set in the Repast 
		 * Simphony Runtime Environment as "Secondary measure Parameter" and is of type double.
		 */
		Parameters params = RunEnvironment.getInstance().getParameters();
		int delayParameter = (int)params.getValue("delayParameter");
		double probabilityAdaptation = 1.0/delayParameter;  // probability that a motivated individual will adapt at a given year
		double secondaryMeasureParameter = (double)params.getValue("secondaryMeasureParameter");

		for (int i = 0; i < GlobalVariables.getExistingHouseList().size(); i++) {
			House house = GlobalVariables.getExistingHouseList().get(i);
			int tick = (int)RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
			// implement primary adaptation measure
			if (house.isCoping() && house.getPrimaryMeasure().equals(GlobalVariables.getNoMeasure())) {
				double random = RandomHelper.nextDouble();
				if (random <= probabilityAdaptation) {
					house.setImplementAdaptationMeasure(true);
					if (house.isAbandonPrimaryMeasure()) {
						house.setAbandonPrimaryMeasure(false);
					}
					if (house.getHouseCategory().equals(GlobalVariables.getSingleFamily())) {
						house.setPrimaryMeasure(GlobalVariables.getMeasure1());
						updatePrimaryMeasureImplementationFrequency(house);
					} else if (house.getHouseCategory().equals(GlobalVariables.getBungalow())) {
						house.setPrimaryMeasure(GlobalVariables.getMeasure2_B());
						updatePrimaryMeasureImplementationFrequency(house);
					} else if (house.getHouseCategory().equals(GlobalVariables.getIBA())) {
						house.setPrimaryMeasure(GlobalVariables.getMeasure2_I());
						updatePrimaryMeasureImplementationFrequency(house);
					} else if (house.getHouseCategory().equals(GlobalVariables.getApartment()) || house.getHouseCategory().equals(GlobalVariables.getHighriseBuilding())) {
						house.setPrimaryMeasure(GlobalVariables.getMeasure3());
						updatePrimaryMeasureImplementationFrequency(house);
					} else if (house.getHouseCategory().equals(GlobalVariables.getGardenHouse())) {
						house.setPrimaryMeasure(GlobalVariables.getMeasure4());
						updatePrimaryMeasureImplementationFrequency(house);
					} 
				} // else do nothing
			}  // else do nothing

			// implement secondary adaptation measure. Only households that have not implemented a secondary measure AND that have already implemented a primary
			// measure satisfy the secondary measure condition.
			// Writing this if-statement outside of the above one ensures that a household may implement a secondary measure any time after it implements 
			// the primary measure
			double random = RandomHelper.nextDouble();
			if (!(house.isAbandonSecondaryMeasure() && (house.getAbandonTick() == tick))) {
				if (!house.getPrimaryMeasure().equals(GlobalVariables.getNoMeasure()) && house.getSecondaryMeasure().equals(GlobalVariables.getNoMeasure()) &&
						random <= secondaryMeasureParameter) {
					if (house.getHouseCategory().equals(GlobalVariables.getSingleFamily()) || house.getHouseCategory().equals(GlobalVariables.getIBA()) ||
							house.getHouseCategory().equals(GlobalVariables.getApartment()) || house.getHouseCategory().equals(GlobalVariables.getHighriseBuilding())) {
						house.setSecondaryMeasure(GlobalVariables.getMeasure5());
						updateSecondaryMeasureImplementationFrequency(house);
						if (house.isAbandonSecondaryMeasure()) {
							house.setAbandonSecondaryMeasure(false);
						}
					} // else do nothing
				} // else do nothing
			}
		}
	}

	/***********************************************************************************************
	***********************************************************************************************/
	// This method maps adaptation measures with the frequency of the implementation of the measures.
	public static void updatePrimaryMeasureImplementationFrequency(House house) {
		String measure = house.getPrimaryMeasure();
		if (GlobalVariables.getPrimaryMeasure_frequency().containsKey(measure))
			GlobalVariables.getPrimaryMeasure_frequency().put(measure, GlobalVariables.getPrimaryMeasure_frequency().get(measure) + 1);
		else
			GlobalVariables.getPrimaryMeasure_frequency().put(measure, 1);
	}

	/***********************************************************************************************
	***********************************************************************************************/
	// This method maps adaptation measures with the frequency of the implementation of the measures.
	public static void updateSecondaryMeasureImplementationFrequency(House house) {
		String houseCat = house.getHouseCategory();
		if (GlobalVariables.getSecondaryMeasure_frequency().containsKey(houseCat))
			GlobalVariables.getSecondaryMeasure_frequency().put(houseCat, GlobalVariables.getSecondaryMeasure_frequency().get(houseCat) + 1);
		else
			GlobalVariables.getSecondaryMeasure_frequency().put(houseCat, 1);
	}

	/***********************************************************************************************
	***********************************************************************************************/
	
	@ScheduledMethod(start=1, interval=1, priority = 40)  
	public static void abandonMeasure() {
		/** 
		 * @params: adaptationDuration
		 * Agents may forget to implement a measure or measures may fail. This parameter represents the number of consecutive years an adaptation 
		 * measure is implemented. This parameter is only for agents who implement temporary measures. The probability that an adapted agent abandons 
		 * the measure at a given year is computed as 1/adaptationDuration. The parameter is set in the Repast Simphony Runtime Environment as 
		 * "Adaptation duration (years)" and is of type int.
		 * 
		 * @params: abandonFrequencyThreshold
		 * This parameter sets a limit to the number of times an agent could abandon a measure. The assumption is that agents will not abandon a measure 
		 * if they implement the measure a number of times specified in the parameter. The parameter is set in the Repast Simphony Runtime Environment as 
		 * "Abandon frequency threshold" and is of type int.
		 */
		Parameters params = RunEnvironment.getInstance().getParameters();
		int adaptationDuration = (int)params.getValue("adaptationDuration");
		double probabilityAbandoning = 1.0/adaptationDuration; // probability that an adapted agent abandons the measure at a given year
		int abandonFrequencyThreshold = (int)params.getValue("abandonFrequencyThreshold");
		for (int i = 0; i < GlobalVariables.getExistingHouseList().size(); i++) {
			House house = GlobalVariables.getExistingHouseList().get(i);
			// a household should have implemented a measure to abandon it.  
			if (house.isImplementAdaptationMeasure()) { // this ensures, at least, a household implemented a primary measure
				if (house.getAbandonFrequencyPrimary() < abandonFrequencyThreshold && house.getAbandonFrequencySecondary() < abandonFrequencyThreshold) {
					double random = RandomHelper.nextDouble();
					// but duration of the measure (primary or secondary) should meet the probabilityAbandoning and the DURATION_THRESHOLD criteria   
					if (random <= probabilityAbandoning) {
						// if the household implemented a secondary measure, first abandon that and update the secondaryMeasure_frequency variable and the abandonTick attribute
						int tick = (int)RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
						if (!house.getSecondaryMeasure().equals(GlobalVariables.getNoMeasure())) {
							GlobalVariables.getSecondaryMeasure_frequency().put(house.getHouseCategory(), GlobalVariables.getSecondaryMeasure_frequency().get(house.getHouseCategory()) - 1);
							house.setSecondaryMeasure(GlobalVariables.getNoMeasure());
							house.setAbandonSecondaryMeasure(true);
							house.setAbandonTick(tick);
							house.setAbandonFrequencySecondary(house.getAbandonFrequencySecondary() + 1);
						// if the household has not implemented a secondary measure, abandon a non-permanent primary measure (MEASURE3 or MEASURE4) and update the 
						// primaryMeasure_frequency variable
						} else if (house.getPrimaryMeasure().equals(GlobalVariables.getMeasure3()) || house.getPrimaryMeasure().equals(GlobalVariables.getMeasure4())) {
							GlobalVariables.getPrimaryMeasure_frequency().put(house.getPrimaryMeasure(), GlobalVariables.getPrimaryMeasure_frequency().get(house.getPrimaryMeasure()) - 1);
							house.setPrimaryMeasure(GlobalVariables.getNoMeasure());
							// update abandonPrimaryMeasure to true and abandonTick to the current tick
							house.setAbandonPrimaryMeasure(true);
							house.setAbandonTick(tick);
							house.setAbandonFrequencyPrimary(house.getAbandonFrequencyPrimary() + 1);
							// in addition, update the implementAdaptationMeasure and coping household attributes to false.
							house.setImplementAdaptationMeasure(false);
							house.setCoping(false);
						}
					}
				}
			}
		}
	}
}
