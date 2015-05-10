package priv;

import java.util.HashMap;
import java.util.Iterator;

public class HoldDown implements Runnable {
	
	private HashMap<String, Boolean> names;
	
	public HoldDown(HashMap<String, Boolean> names) {
		this.names = names;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Iterator<String> keys = names.keySet().iterator();
		while(keys.hasNext()) {
			String key = keys.next();
			Host.holdList.remove(key);
		}
	}

}
