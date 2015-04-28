package eBay;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Analyze {
	FileReader file = null;
	BufferedReader reader = null;
	HashMap<String, Object> mem = new HashMap<String, Object>(); 
	public Analyze() {
		String line;
		try {
			this.file = new FileReader("NewInfo.json");
			reader = new BufferedReader(this.file);
			while((line = reader.readLine()) != null) {
				HashMap<Integer, Integer> thm = new HashMap<Integer, Integer>();
				String[] s = line.split(":");
				String key = s[0];
				s[1] = s[1].substring(1, s[1].length()-1);
				for(String t : s[1].split(", ")) {
					if(!t.equals(""))
						thm.put(Integer.valueOf(t.split("=")[0]), Integer.valueOf(t.split("=")[1]));
				}
				mem.put(key, thm);
			}
			reader.close();
			file.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void findSimilar(String input) {
		HashMap<Integer, Integer> itemFeature;
		ArrayList<String> similarID = new ArrayList<String>();
		ArrayList<Double> similarV = new ArrayList<Double>();
		if(mem.containsKey(input)) {
			itemFeature = (HashMap<Integer, Integer>) mem.get(input);
			for(Map.Entry<String, Object> entry : mem.entrySet()) {
				HashMap<Integer, Integer> thm = (HashMap<Integer, Integer>) entry.getValue();
				thm.keySet().retainAll(itemFeature.keySet());
				int a = 0, b = 0, c = 0;
				for(Map.Entry<Integer, Integer> t : thm.entrySet()) {
					int m = itemFeature.get(t.getKey());
					int n = t.getValue();
					a += m*n;
					b += m*m;
					c += n*n;
				}
				if(a != 0) {
					double r = a/(Math.sqrt(b)+Math.sqrt(c));
					if(similarV.size() >= 5) {
						double j = r;
						int idx = -1;
						for(int i = 0; i < similarV.size(); i++) {
							if(similarV.get(i) < j) {
								j = similarV.get(i);
								idx = i;
							}
						}
						if(idx != -1 && !entry.getKey().equals(input)) {
							similarV.remove(idx);
							similarID.remove(idx);
							similarID.add(entry.getKey());
							similarV.add(r);
						}
					} else {
						similarID.add(entry.getKey());
						similarV.add(r);
					}
				}
			}
			if(similarID.size() > 0) {
				for(int i = 0; i < similarID.size(); i++) {
					System.out.println("www.ebay.com/itm/" + similarID.get(i));
				}
			} else {
				System.out.println("No Match.");
			}
		} else {
			System.out.println("Wrong itemID.");
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Analyze al = new Analyze();
		Scanner in = new Scanner(System.in);
		String input = in.nextLine();
		al.findSimilar(input);
	}

}
