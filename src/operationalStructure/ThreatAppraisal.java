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
import repast.simphony.engine.schedule.ScheduledMethod;
import wilhelmsburg.GlobalVariables;

/**
 * This class computes the threat appraisal based on a decision tree (See Figure 3). Households appraise threat every time step.
 *
 */
public class ThreatAppraisal {
	
	// since we did not appraise threat depending on the agent attributes at t=0 (initial conditions specified at the WilhelmsburgBuilder class),
	// we start this method at t=0
	@ScheduledMethod(start=0, interval=1, priority = 35)
	public static void assessThreat() {
		for (int i = 0; i < GlobalVariables.getExistingHouseList().size(); i++) {
			House house = GlobalVariables.getExistingHouseList().get(i);
			if (house.isFloodExperience()) {   
				if (house.getReliancePublicProtection().equals(GlobalVariables.getLow())) {
					house.setThreat(true);
				} else if (house.getReliancePublicProtection().equals(GlobalVariables.getMedium())) {
					if (house.getPerceptionCC().equals(GlobalVariables.getYes())) {
						house.setThreat(true);
					} else if (house.getPerceptionCC().equals(GlobalVariables.getUncertain())) {
						if (house.getInfoSource().equals(GlobalVariables.getOthers())) {
							house.setThreat(true);
						} else if (house.getInfoSource().equals(GlobalVariables.getAuthorities())) {
							house.setThreat(false);
						} else {
							throw new IllegalArgumentException ("Define a valid value for infoSource!");
						}
					} else if (house.getPerceptionCC().equals(GlobalVariables.getNo())) {
						house.setThreat(false);
					} else {
						throw new IllegalArgumentException ("Define a valid value for perceptionCC!");
					}
				} else if (house.getReliancePublicProtection().equals(GlobalVariables.getHigh())) {
					throw new IllegalArgumentException ("If the agent has experienced flooding, the reliance on public protection cannot be High!");
				} else {
					throw new IllegalArgumentException ("Define a valid value for reliancePublicProtection!");
				}
			} else if (!house.isFloodExperience()) {
				if (house.getReliancePublicProtection().equals(GlobalVariables.getHigh())) {
					house.setThreat(false);
				} else if (house.getReliancePublicProtection().equals(GlobalVariables.getMedium()) || house.getReliancePublicProtection().equals(GlobalVariables.getLow())) {
					throw new IllegalArgumentException ("If the agent has never experienced flooding, the reliance on public protection cannot be Medium or Low!");
				} else {
					throw new IllegalArgumentException ("Define a valid value for reliancePublicProtection!");
				}
			} else {
				throw new IllegalArgumentException ("Define a valid value for floodExperience!");
			}
		}
	}
}
