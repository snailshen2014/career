package com.bonc.busi.backpage.bo;

import java.util.Comparator;

@SuppressWarnings("rawtypes")
public class ComparatorActivity implements Comparator {

	@Override
	public int compare(Object o1, Object o2) {
		ActivityPo act1 = (ActivityPo)o1;
		ActivityPo act2 = (ActivityPo)o2;
		return act1.getOrderStatus().toString().compareTo(act2.getOrderStatus().toString());
	}
}
