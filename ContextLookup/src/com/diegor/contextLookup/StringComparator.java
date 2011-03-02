package com.diegor.contextLookup;

import net.rim.device.api.util.Comparator;

public final class StringComparator implements Comparator {

	public StringComparator() {
		// TODO Auto-generated constructor stub
	}

public int compare(Object o1, Object o2){
		String s1=(String)o1; String s2=(String)o2;
		int result = s1.compareTo(s2);
		if(result < 0)
			return -1;
		else if(result == 0)
			return 0;
		else
			return 1;
	}

}
