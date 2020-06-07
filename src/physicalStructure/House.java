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

import com.vividsolutions.jts.geom.Geometry;

/**
 * This class instantiates houses. Each house holds all the characteristics of the household agent in addition to the 
 * characteristics of the house the agent lives in.
 *
 */
public class House {
	private String buildingType;  // building type refers to building construction material and quality 
	private String houseCategory; // this variable holds a collection of different but similar building types
	private String houseID; // FID of the house as specified in the ESRI shapefile
	private Geometry geom; // geometry (location) of the polygon feature (building)
	private double area;  // total area of a building (area of each polygon feature)
	private double floodDepth;  // if the house is flooded, what is the flood depth estimated in the flood models
	private double buildingDamage;  // direct damage (building/structural)
	private double buildingDamageReduced;  // building damage reduced by implementing adaptation measures
	private double contentDamage;  // direct damage (properties and appliances)
	private double contentDamageReduced;  // content damage reduced by implementing adaptation measures
	private boolean implementAdaptationMeasure; // if an agent implemented a flood adaptation measure
	private boolean abandonPrimaryMeasure; // if an agent abandons a primary flood adaptation measure
	private boolean abandonSecondaryMeasure; // if an agent abandons a secondary flood adaptation measure
	private String primaryMeasure;  // the type of primary measure implemented 
	private String secondaryMeasure;  // the type of secondary measure implemented 
	private int abandonTick; // the time step (tick) that an agent abandons a measure. it is used to decide when to appraise threat and coping
	private int abandonFrequencyPrimary;  // the number of times a house may abandon a primary measure
	private int abandonFrequencySecondary;  // the number of times a house may abandon a secondary measure

	private boolean floodExperience; // if an agent lives in Wilhelmsburg when a flood event occurs. It has a value of True (Yes) or False (No)
	private String reliancePublicProtection; // reliance on public protections (in this case dike). It has a value of High, Medium or Low
	private String perceptionCC; // agent's perception of future climate change. It has a value of Yes, Uncertain or No
	private String infoSource; // agents source of information (regarding flooding and adaptation). It has a value of Autho (for authorities) or Others (for all other sources)

	private boolean floodedBefore;  // if an agent has direct flood experience. It has a value of True (Yes) or False (No)
	private boolean stateSubsidy;  // if the regional state or central government provides subsidy to flooded agents (this is a global variable but included here)
	private String houseOwnership;  // it has a value of Own or Rented
	private String householdIncome;  // it has a value of High, Medium or Low
	private boolean socialNetworkInfluence;  // it has a value of Yes or No. This variable is used only to count the number of households that are affected by the shared strategy
	
	private boolean threat;  // if agents perceive flood as a threat in Wilhelmsburg
	private boolean coping;  // if agents decide to implement a coping measure (if the threat results in a protection motivation)
	

	public House(String buildingType, String houseCategory, String houseID, Geometry geom, double area, double floodDepth, double buildingDamage, 
			double buildingDamageReduced, double contentDamage, double contentDamageReduced, boolean implementAdaptationMeasure, boolean abandonPrimaryMeasure, 
			boolean abandonSecondaryMeasure, String primaryMeasure, String secondaryMeasure, int abandonTick, int abandonFrequencyPrimary, int abandonFrequencySecondary, 
			boolean floodExperience, String reliancePublicProtection, String perceptionCC, String infoSource, boolean floodedBefore, boolean stateSubsidy, String houseOwnership, 
			String householdIncome, boolean socialNetworkInfluence, boolean threat, boolean coping) {
		super();
		this.buildingType = buildingType;
		this.houseCategory = houseCategory;
		this.houseID = houseID;
		this.geom = geom;
		this.area = area;
		this.floodDepth = floodDepth;
		this.buildingDamage = buildingDamage;
		this.buildingDamageReduced = buildingDamageReduced;
		this.contentDamage = contentDamage;
		this.contentDamageReduced = contentDamageReduced;
		this.implementAdaptationMeasure = implementAdaptationMeasure;
		this.abandonPrimaryMeasure = abandonPrimaryMeasure;
		this.abandonSecondaryMeasure = abandonSecondaryMeasure;
		this.primaryMeasure = primaryMeasure;
		this.secondaryMeasure = secondaryMeasure;
		this.abandonTick = abandonTick;
		this.abandonFrequencyPrimary = abandonFrequencyPrimary;
		this.abandonFrequencySecondary = abandonFrequencySecondary;
		this.floodExperience = floodExperience;
		this.reliancePublicProtection = reliancePublicProtection;
		this.perceptionCC = perceptionCC;
		this.infoSource = infoSource;
		this.floodedBefore = floodedBefore;
		this.stateSubsidy = stateSubsidy;
		this.houseOwnership = houseOwnership;
		this.householdIncome = householdIncome;
		this.socialNetworkInfluence = socialNetworkInfluence;
		this.threat = threat;
		this.coping = coping;
	}
	
	/***********************************************************************************************
	*********************************** Getters and Setters ****************************************
	***********************************************************************************************/

	public String getBuildingType() {
		return buildingType;
	}

	public void setBuildingType(String buildingType) {
		this.buildingType = buildingType;
	}

	public String getHouseCategory() {
		return houseCategory;
	}

	public void setHouseCategory(String houseCategory) {
		this.houseCategory = houseCategory;
	}

	public String getHouseID() {
		return houseID;
	}

	public void setHouseID(String houseID) {
		this.houseID = houseID;
	}

	public Geometry getGeom() {
		return geom;
	}

	public void setGeom(Geometry geom) {
		this.geom = geom;
	}

	public double getArea() {
		return area;
	}

	public void setArea(double area) {
		this.area = area;
	}

	public double getFloodDepth() {
		return floodDepth;
	}

	public void setFloodDepth(double floodDepth) {
		this.floodDepth = floodDepth;
	}

	public double getBuildingDamage() {
		return buildingDamage;
	}

	public void setBuildingDamage(double buildingDamage) {
		this.buildingDamage = buildingDamage;
	}

	public double getBuildingDamageReduced() {
		return buildingDamageReduced;
	}

	public void setBuildingDamageReduced(double buildingDamageReduced) {
		this.buildingDamageReduced = buildingDamageReduced;
	}

	public double getContentDamage() {
		return contentDamage;
	}

	public void setContentDamage(double inventoryDamage) {
		this.contentDamage = inventoryDamage;
	}

	public double getContentDamageReduced() {
		return contentDamageReduced;
	}

	public void setContentDamageReduced(double contentDamageReduced) {
		this.contentDamageReduced = contentDamageReduced;
	}

	public boolean isImplementAdaptationMeasure() {
		return implementAdaptationMeasure;
	}

	public void setImplementAdaptationMeasure(boolean implementAdaptationMeasure) {
		this.implementAdaptationMeasure = implementAdaptationMeasure;
	}

	public boolean isAbandonPrimaryMeasure() {
		return abandonPrimaryMeasure;
	}

	public void setAbandonPrimaryMeasure(boolean abandonPrimaryMeasure) {
		this.abandonPrimaryMeasure = abandonPrimaryMeasure;
	}

	public boolean isAbandonSecondaryMeasure() {
		return abandonSecondaryMeasure;
	}

	public void setAbandonSecondaryMeasure(boolean abandonSecondaryMeasure) {
		this.abandonSecondaryMeasure = abandonSecondaryMeasure;
	}

	public String getPrimaryMeasure() {
		return primaryMeasure;
	}

	public void setPrimaryMeasure(String primaryMeasure) {
		this.primaryMeasure = primaryMeasure;
	}

	public String getSecondaryMeasure() {
		return secondaryMeasure;
	}

	public void setSecondaryMeasure(String secondaryMeasure) {
		this.secondaryMeasure = secondaryMeasure;
	}

	public int getAbandonTick() {
		return abandonTick;
	}

	public int getAbandonFrequencyPrimary() {
		return abandonFrequencyPrimary;
	}

	public void setAbandonFrequencyPrimary(int abandonFrequencyPrimary) {
		this.abandonFrequencyPrimary = abandonFrequencyPrimary;
	}

	public int getAbandonFrequencySecondary() {
		return abandonFrequencySecondary;
	}

	public void setAbandonFrequencySecondary(int abandonFrequencySecondary) {
		this.abandonFrequencySecondary = abandonFrequencySecondary;
	}

	public void setAbandonTick(int abandonTick) {
		this.abandonTick = abandonTick;
	}

	public boolean isFloodExperience() {
		return floodExperience;
	}

	public void setFloodExperience(boolean floodExperience) {
		this.floodExperience = floodExperience;
	}

	public String getReliancePublicProtection() {
		return reliancePublicProtection;
	}

	public void setReliancePublicProtection(String reliancePublicProtection) {
		this.reliancePublicProtection = reliancePublicProtection;
	}

	public String getPerceptionCC() {
		return perceptionCC;
	}

	public void setPerceptionCC(String perceptionCC) {
		this.perceptionCC = perceptionCC;
	}

	public String getInfoSource() {
		return infoSource;
	}

	public void setInfoSource(String infoSource) {
		this.infoSource = infoSource;
	}

	public boolean isFloodedBefore() {
		return floodedBefore;
	}

	public void setFloodedBefore(boolean floodedBefore) {
		this.floodedBefore = floodedBefore;
	}

	public boolean isStateSubsidy() {
		return stateSubsidy;
	}

	public void setStateSubsidy(boolean stateSubsidy) {
		this.stateSubsidy = stateSubsidy;
	}

	public String getHouseOwnership() {
		return houseOwnership;
	}

	public void setHouseOwnership(String houseOwnership) {
		this.houseOwnership = houseOwnership;
	}

	public String getHouseholdIncome() {
		return householdIncome;
	}

	public void setHouseholdIncome(String householdIncome) {
		this.householdIncome = householdIncome;
	}

	public boolean isSocialNetworkInfluence() {
		return socialNetworkInfluence;
	}

	public void setSocialNetworkInfluence(boolean socialNetworkInfluence) {
		this.socialNetworkInfluence = socialNetworkInfluence;
	}

	public boolean isThreat() {
		return threat;
	}

	public void setThreat(boolean threat) {
		this.threat = threat;
	}

	public boolean isCoping() {
		return coping;
	}

	public void setCoping(boolean coping) {
		this.coping = coping;
	}
}
