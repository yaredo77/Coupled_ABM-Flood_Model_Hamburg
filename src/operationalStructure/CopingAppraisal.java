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
 * This class computes the coping appraisal based on a decision tree (See Figure 4). Households appraise coping only if they have
 * not appraised coping before AND if they appraise flood as a threat. Coping is a function of previous direct flood experience, 
 * house ownership, household income, subsidy and social network. Those agents that are influenced by their social network may 
 * decide to appraise coping. Since, all may not follow the crowed, we also introduce a shared strategy parameter that constrains
 * the percentage of those agents that follow the crowed (the agents that already implemented a measure).  
 * 
 * Assumption:
 * 			- The sources of information does not initiate the coping appraisal process as in the original PMT study as agents 
 * 			  know the type of measure they implement.
 * 			- If a house has already appraised coping and implemented a measure, they don’t appraise coping again, unless 
 * 			  they abandon the measure, assuming that they do not implement another primary measure.
 *
 */
public class CopingAppraisal {

	// since we did not appraise coping depending on the agent attributes at t=0 (initial conditions specified at the WilhelmsburgBuilder class),
	// we start this method at t=0
	@ScheduledMethod(start=0, interval=1, priority = 30)
	public static void assessCoping() {
		/** 
		 * @params: sharedStrategyParameter
		 * This parameter represents the percentage of agents, who are potentially influenced by their social network, that follow the shared  
		 * strategy. The default value is based on the modeller's expert estimation. The parameter is set in the Repast Simphony Runtime Environment 
		 * as "Shared strategy parameter (%)" and is of type double.
		 */
		Parameters params = RunEnvironment.getInstance().getParameters();
		double sharedStrategyParam = (double)params.getValue("sharedStrategyParameter");
		for (int i = 0; i < GlobalVariables.getExistingHouseList().size(); i++) {
			House house = GlobalVariables.getExistingHouseList().get(i);
			if (!house.isCoping()) {
				// if a household agent does not appraise coping because it decided to abandon a measure at the current time step (see abandonMeasure
				// method in the ImplementAbandonMeasure class), it will not appraise coping at the same time step.
				int tick = (int)RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
				if (!(house.isAbandonPrimaryMeasure() && (house.getAbandonTick() == tick))) {
					if (house.isThreat()) {
						String socialNetwork = getSocialNetwork(house);  // this is updated in the GlobalVariables class
						double random = RandomHelper.nextDouble();
						if (house.isFloodedBefore()) {
							if (house.getHouseOwnership().equals(GlobalVariables.getOwn())) {
								// for agents that implement structural or permanent measures
								if (house.getHouseCategory().equals(GlobalVariables.getSingleFamily()) || house.getHouseCategory().equals(GlobalVariables.getBungalow()) ||
										house.getHouseCategory().equals(GlobalVariables.getIBA())) {
									if (house.getHouseholdIncome().equals(GlobalVariables.getHigh())) {
										house.setCoping(true);
									} else if (house.getHouseholdIncome().equals(GlobalVariables.getLow())) {
										if (house.isStateSubsidy()) {
											house.setCoping(true);
										} else {
											if (socialNetwork != null && socialNetwork.equals(GlobalVariables.getHigh())) {
												if (random <= sharedStrategyParam) {
													house.setCoping(true);
													house.setSocialNetworkInfluence(true);
												} else {
													house.setCoping(false);
												}
											} else if (socialNetwork == null || socialNetwork.equals(GlobalVariables.getLow())) {
												house.setCoping(false);
											} else {
												throw new IllegalArgumentException ("Define a valid value for socialNetwork!");
											}
										}
									} else {
										throw new IllegalArgumentException ("Define a valid value for householdIncome!");
									}
									// for agents that implement non-structural or temporary measures
								} else if (house.getHouseCategory().equals(GlobalVariables.getGardenHouse()) || house.getHouseCategory().equals(GlobalVariables.getApartment()) || 
										house.getHouseCategory().equals(GlobalVariables.getHighriseBuilding())) {
									house.setCoping(true);
								}
							} else if (house.getHouseOwnership().equals(GlobalVariables.getRented())) {
								// for agents that implement structural or permanent measures
								if (house.getHouseCategory().equals(GlobalVariables.getSingleFamily()) || house.getHouseCategory().equals(GlobalVariables.getBungalow()) ||
										house.getHouseCategory().equals(GlobalVariables.getIBA())) {
									if (house.getHouseholdIncome().equals(GlobalVariables.getHigh())) {
										if (socialNetwork != null && socialNetwork.equals(GlobalVariables.getHigh())) {
											if (random <= sharedStrategyParam) {
												house.setCoping(true);
												house.setSocialNetworkInfluence(true);
											} else {
												house.setCoping(false);
											}
										} else if (socialNetwork == null || socialNetwork.equals(GlobalVariables.getLow())) {
											house.setCoping(false);
										} else {
											throw new IllegalArgumentException ("Define a valid value for socialNetwork!");
										}
									} else if (house.getHouseholdIncome().equals(GlobalVariables.getLow())) {
										if (house.isStateSubsidy()) {
											if (socialNetwork != null && socialNetwork.equals(GlobalVariables.getHigh())) {
												if (random <= sharedStrategyParam) {
													house.setCoping(true);
													house.setSocialNetworkInfluence(true);
												} else {
													house.setCoping(false);
												}
											} else if (socialNetwork == null || socialNetwork.equals(GlobalVariables.getLow())) {
												house.setCoping(false);
											} else {
												throw new IllegalArgumentException ("Define a valid value for socialNetwork!");
											}
										} else {
											house.setCoping(false);
										}
									} else {
										throw new IllegalArgumentException ("Define a valid value for householdIncome!");
									}
									// for agents that implement non-structural or temporary measures
								} else if (house.getHouseCategory().equals(GlobalVariables.getGardenHouse()) || house.getHouseCategory().equals(GlobalVariables.getApartment()) || 
										house.getHouseCategory().equals(GlobalVariables.getHighriseBuilding())) {
									if (socialNetwork != null && socialNetwork.equals(GlobalVariables.getHigh())) {
										if (random <= sharedStrategyParam) {
											house.setCoping(true);
											house.setSocialNetworkInfluence(true);
										} else {
											house.setCoping(false);
										}
									} else if (socialNetwork == null || socialNetwork.equals(GlobalVariables.getLow())) {
										house.setCoping(false);
									} else {
										throw new IllegalArgumentException ("Define a valid value for socialNetwork!");
									}
								}
							} else {
								throw new IllegalArgumentException ("Define a valid value for houseOwnership!");
							}
						} else {  // if not flooded before
							if (house.getHouseOwnership().equals(GlobalVariables.getOwn())) {
								// for agents that implement structural or permanent measures
								if (house.getHouseCategory().equals(GlobalVariables.getSingleFamily()) || house.getHouseCategory().equals(GlobalVariables.getBungalow()) ||
										house.getHouseCategory().equals(GlobalVariables.getIBA())) {
									if (house.getHouseholdIncome().equals(GlobalVariables.getHigh())) {
										if (socialNetwork != null && socialNetwork.equals(GlobalVariables.getHigh())) {
											if (random <= sharedStrategyParam) {
												house.setCoping(true);
												house.setSocialNetworkInfluence(true);
											} else {
												house.setCoping(false);
											}
										} else if (socialNetwork == null || socialNetwork.equals(GlobalVariables.getLow())) {
											house.setCoping(false);
										} else {
											throw new IllegalArgumentException ("Define a valid value for socialNetwork!");
										}
									} else if (house.getHouseholdIncome().equals(GlobalVariables.getLow())) {
										if (house.isStateSubsidy()) {
											if (socialNetwork != null && socialNetwork.equals(GlobalVariables.getHigh())) {
												if (random <= sharedStrategyParam) {
													house.setCoping(true);
													house.setSocialNetworkInfluence(true);
												} else {
													house.setCoping(false);
												}
											} else if (socialNetwork == null || socialNetwork.equals(GlobalVariables.getLow())) {
												house.setCoping(false);
											} else {
												throw new IllegalArgumentException ("Define a valid value for socialNetwork!");
											}
										} else {
											house.setCoping(false);
										}
									} else {
										throw new IllegalArgumentException ("Define a valid value for householdIncome!");
									}
								// for agents that implement non-structural or temporary measures
								} else if (house.getHouseCategory().equals(GlobalVariables.getGardenHouse()) || house.getHouseCategory().equals(GlobalVariables.getApartment()) || 
										house.getHouseCategory().equals(GlobalVariables.getHighriseBuilding())) {
									if (socialNetwork != null && socialNetwork.equals(GlobalVariables.getHigh())) {
										if (random <= sharedStrategyParam) {
											house.setCoping(true);
											house.setSocialNetworkInfluence(true);
										} else {
											house.setCoping(false);
										}
									} else if (socialNetwork == null || socialNetwork.equals(GlobalVariables.getLow())) {
										house.setCoping(false);
									} else {
										throw new IllegalArgumentException ("Define a valid value for socialNetwork!");
									}
								}
							} else if (house.getHouseOwnership().equals(GlobalVariables.getRented())) {
								house.setCoping(false);
							} else {
								throw new IllegalArgumentException ("Define a valid value for houseOwnership!");
							}
						}
					} else {
						// If agents do not consider flood as a threat, they do intend to develop coping behaviour.
						house.setCoping(false);
					}
				}
			}  // If an agent already appraised coping, the assumption is that the house already implemented a measure and no need to appraise coping again
		}
	}
	
	/***********************************************************************************************
	***********************************************************************************************/
	public static String getSocialNetwork(House house) {
		String socialNetwork = null;
		if (house.getHouseCategory().equals(GlobalVariables.getSingleFamily()) || house.getHouseCategory().equals(GlobalVariables.getBungalow())) {
			socialNetwork = GlobalVariables.getSocialNetwork_SingleFamily();
		} else if (house.getHouseCategory().equals(GlobalVariables.getGardenHouse())) {
			socialNetwork = GlobalVariables.getSocialNetwork_GardenHouse();
		} else if (house.getHouseCategory().equals(GlobalVariables.getApartment()) || house.getHouseCategory().equals(GlobalVariables.getHighriseBuilding())) {
			socialNetwork = GlobalVariables.getSocialNetwork_ApartmentHighrise();
		} else if (house.getHouseCategory().equals(GlobalVariables.getIBA())) {
			socialNetwork = GlobalVariables.getSocialNetwork_IBA();
		}
		return socialNetwork;
	}
}
