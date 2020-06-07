/**
 * Copyright (c) [2018] [Yared Abayneh Abebe]
 *
 * This file is part of Coupled_ABM-Flood_Model for Wilhelmsburg, Hamburg, Germany.
 * Coupled_ABM-Flood_Model is free software licensed under the CC BY-NC-SA 4.0
 * You are free to:
 *	Share — copy and redistribute the material in any medium or format
 *   Adapt — remix, transform, and build upon the material
 * The licensor cannot revoke these freedoms as long as you follow the license terms.
 *	Attribution — You must give appropriate credit, provide a link to the license, 
 *				  and indicate if changes were made. You may do so in any reasonable 
 *				  manner, but not in any way that suggests the licensor endorses you 
 *				  or your use.
 *	NonCommercial — You may not use the material for commercial purposes.
 *	ShareAlike — If you remix, transform, or build upon the material, you must distribute 
 *				 your contributions under the same license as the original. 
 *  Full license description: https://creativecommons.org/licenses/by-nc-sa/4.0/
 */
 
package aggregateDataCollection;

import physicalStructure.Flood;
import physicalStructure.House;
import repast.simphony.data2.AggregateDataSource;
import repast.simphony.engine.environment.RunEnvironment;

/**
 * This class records the content damage at a time step. To save time, compute only when there is a flood event.
 */
public class TotalContentDamage implements AggregateDataSource{
	@Override
	public String getId() {
		return "Content damage (per tick)";
	}

	@Override
	public Class<?> getDataType() {
		return Integer.class;
	}

	@Override
	public Class<?> getSourceType() {
		return House.class;
	}

	@Override
	public Object get(Iterable<?> objs, int size) {
		double totalContentDamage = 0;
		int tick = (int)RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		if (tick > 0 && Flood.getEventTimeSeries().get(tick-1) > 0) {
			for (Object o: objs) {
				totalContentDamage += ((House) o).getContentDamage();
			}
		}
		return totalContentDamage;
	}

	@Override
	public void reset() {
	}
}
