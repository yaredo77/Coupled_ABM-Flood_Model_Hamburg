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

import physicalStructure.House;
import repast.simphony.data2.AggregateDataSource;

/**
 * This class records those households that appraised coping because of the shared strategies conceptualised
 * using the social network variable. Though households do no have direct flood experience, they may appraise   
 * coping due to the social network influence. Another conditions in which household agents appraise coping due 
 * to the influence of the social network are: (i) when they have direct flood experience AND they live in own 
 * house AND they have low income AND there is no subsidy; (ii) when they have direct flood experience AND they 
 * live in rented house AND they have high income; and (iii) when they have direct flood experience AND they live
 * in a rented house AND they have low income but there is subsidy.    
 *
 */
public class SocialNetworkYes implements AggregateDataSource{
	@Override
	public String getId() {
		return "Coping due to SN (cum)";
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
		int count = 0;
		for (Object o: objs) {
			if (((House) o).isSocialNetworkInfluence()) {
				count++;
			}
		}
		return count;
	}

	@Override
	public void reset() {
	}
}
