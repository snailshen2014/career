package com.bonc.busi.backpage.bo;

import java.util.Comparator;

public class StaticsticCompator implements Comparator {

	@Override
	public int compare(Object o1, Object o2) {
		ActivityStatistics activityStatistics1 = (ActivityStatistics)o1;
		ActivityStatistics activityStatistics2 = (ActivityStatistics)o2;
		return activityStatistics1.getDate().compareTo(activityStatistics2.getDate());
	}

}
