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

import java.util.ArrayList;
//import java.util.Arrays;
import java.util.List;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
//import repast.simphony.random.RandomHelper;
import wilhelmsburg.WilhelmsburgBuilder;

/**
 * This class generates flood event series. We consider three flood scenarios as reported in (Ujeyl & Rose 2015). The storm surge    
 * scenarios are XR2010A, XR2010B and XR2010C (these storm surges have extremely low exceedance probabilities). The event series of   
 * events is randomly generated for 50 years, with exactly one entry of each scenario randomly assigned to a time step. The makeFlood()  
 * method executes only once at the beginning of the simulation creating an event series for the whole simulation.
 * 
 * Assumption:
 * 			- Only three flood event scenarios are considered. All the scenarios simulate dyke overtopping and have very low exceedance 
 * 			  probability. Dike breach is not considered in the conceptualization.
 * 
 */
public class Flood {
	private static List<Integer> eventTimeSeries = new ArrayList<Integer>();  // used in the AssessDamage Class 
	
	@ScheduledMethod(start=0, priority = 50)
	public static void makeFlood() {
		// the commented code generates the flood event series randomly for every simulation. However, it does not guarantee the same event
		// series for replications or batch runs. Hence we run the code couple of times to generate predefine event series for scenarios as 
		// shown below. 
/*		final int EVENTS = 3;
		// initialise the eventTimeSeries list
		for (int i = 0; i < WilhelmsburgBuilder.getComputationTime(); i++) {
			eventTimeSeries.add(0);
		}
		
		// initialise a storm surge scenario list as 1, 2 and 3 (representing XR2010A, XR2010B and XR2010C, respectively)
		List<Integer> surgeScenarioList = Arrays.asList(1,2,3);
		
		// randomly generate three time steps to assign when storm surges will occur 
		int temp = -1;
		for (int i = 0; i < EVENTS; i++) {
			// as the list index start from zero, the highest index value is equal to COMPUTATION_TIME-1
			int randomTimeStep = RandomHelper.nextIntFromTo(0, WilhelmsburgBuilder.getComputationTime() - 1); 
			// this is to avoid the same time step selected more than once
			if (randomTimeStep == temp) {
				i--;
			} else {
				temp = randomTimeStep;
				eventTimeSeries.set(randomTimeStep, surgeScenarioList.get(i));
			}
		}
*/		
		/** 
		 * @params: floodEventScenario
		 * the commented code above generates the flood event series randomly for every simulation. However, it does not guarantee the same event 
		 * series for replications or batch runs. Hence we run the code couple of times to generate predefine event series for scenarios as shown  
		 * below. The floodEventScenario only points to one of the predefined event series. The parameter is set in the Repast Simphony Runtime 
		 * Environment as "Flood event scenario" and is of type int.
		 * 
		 */
		Parameters params = RunEnvironment.getInstance().getParameters();
		int floodEventScenario = (int)params.getValue("floodEventScenario");

		// instantiate the eventTimeSeries list with zeros for each time step
		for (int i = 0; i < WilhelmsburgBuilder.getComputationTime(); i++) {
			eventTimeSeries.add(0);
		}
		// based on the scenario, change three of the time steps to the storm surge scenarios
		if (floodEventScenario == 1) {
			eventTimeSeries.set(6, 2);
			eventTimeSeries.set(15, 3);
			eventTimeSeries.set(28, 1);
		} else if (floodEventScenario == 2) {
			eventTimeSeries.set(1, 2);
			eventTimeSeries.set(12, 1);
			eventTimeSeries.set(33, 3);
		} else if (floodEventScenario == 3) {
			eventTimeSeries.set(10, 3);
			eventTimeSeries.set(23, 2);
			eventTimeSeries.set(36, 1);
		} else if (floodEventScenario == 4) {
			eventTimeSeries.set(15, 3);
			eventTimeSeries.set(19, 1);
			eventTimeSeries.set(39, 2);
		} else if (floodEventScenario == 5) {
			eventTimeSeries.set(7, 1);
			eventTimeSeries.set(38, 2);
			eventTimeSeries.set(44, 3);
		} else if (floodEventScenario == 6) {
			eventTimeSeries.set(6, 1);
			eventTimeSeries.set(11, 3);
			eventTimeSeries.set(31, 2);
		} else {
			throw new IllegalArgumentException ("The defined scenario is not valid. Scenario should be an integer number between 1 and 6, inclusive.");
		}
	}

	/***********************************************************************************************
	*********************************** Getters and Setters ****************************************
	***********************************************************************************************/
	public static List<Integer> getEventTimeSeries() {
		return eventTimeSeries;
	}

	public static void setEventTimeSeries(List<Integer> eventTimeSeries) {
		Flood.eventTimeSeries = eventTimeSeries;
	}
}
