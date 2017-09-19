package my.core.comparator;

import java.util.Comparator;

import my.core.vo.DataListVO;

public class KeyValueComparator implements Comparator {

	public int compare(Object o1, Object o2){
		DataListVO e1 = (DataListVO) o1;
		DataListVO e2 = (DataListVO) o2;
		if (e1.getKey().compareTo(e2.getKey()) == -1)
			return 1;
		else
			return 0;
	}
}
