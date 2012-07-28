package tyrfing.common.struct;

import java.util.Comparator;

public class ReversePriorityComparator implements Comparator<Prioritizable> {

	@Override
	public int compare(Prioritizable object1, Prioritizable object2) {
		if (object1.getPriority() < object2.getPriority()) return 1;
		if (object1.getPriority() > object2.getPriority()) return -1;
		return 0;
	}
	
	

}
