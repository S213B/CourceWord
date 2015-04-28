package eBay;

import java.awt.List;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Preprocess {
	int fileNum;
	FileReader file;
	FileWriter newFile;
	JSONTokener jsonToken;
	JSONObject jsonObj;
	ArrayList<String> feature = new ArrayList<String>();
	ArrayList<String> hm = new ArrayList<String>();
	
	HashMap<String, Integer> tf = new HashMap<String, Integer>();
	HashMap<String, Integer> idf = new HashMap<String, Integer>();
	HashMap<String, Double> tfidf = new HashMap<String, Double>();
	
	public Preprocess() {
		fileNum = 0;
		try {
			this.file = new FileReader("BasicInfo.json");
			newFile = new FileWriter("NewInfo.json", true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.jsonToken = new JSONTokener(this.file);
	}
	
	private void parseJSON() {
		boolean first = true;
		
		while(jsonToken.more()) {
			for(int i = 0; i < 4; i++) {
				if(!first) {
					jsonToken.nextTo('{');
					first = false;
				}
				JSONObject jsonID;
				jsonID = new JSONObject(jsonToken);
//				try {
//					newFile.write(jsonObj.get("ItemID").toString() + "\n");
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				
				jsonToken.nextTo('{');
				jsonObj = new JSONObject(jsonToken);
				if(!hm.contains(jsonID.get("ItemID").toString())) {
					hm.add(jsonID.get("ItemID").toString());
					titleCount(jsonObj.get("Title").toString());
					fileNum++;
				}
				
				jsonToken.nextTo('{');
				jsonObj = new JSONObject(jsonToken);
//				image(jsonObj.toString());
				
				jsonToken.nextTo('{');
				jsonObj = new JSONObject(jsonToken);
			}
		}
		
		titleTFIDF();
//		try {
//			newFile.flush();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		first = true;
		try {
			file.close();
			file = new FileReader("BasicInfo.json");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jsonToken = new JSONTokener(file);
		
		while(jsonToken.more()) {
			for(int i = 0; i < 4; i++) {
				if(!first) {
					jsonToken.nextTo('{');
					first = false;
				}
				JSONObject jsonID;
				jsonID = new JSONObject(jsonToken);
//				try {
//					newFile.write(jsonObj.get("ItemID").toString() + "\n");
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				
				jsonToken.nextTo('{');
				jsonObj = new JSONObject(jsonToken);
				if(hm.contains(jsonID.get("ItemID").toString())) {
					HashMap<Integer, Integer> f = extractVec(jsonObj.get("Title").toString());
					hm.remove(jsonID.get("ItemID").toString());
					if(jsonID.get("ItemID").toString().equals("141491135219"))
						System.out.print(true);
					try {
						newFile.write(jsonID.get("ItemID").toString() + ":" + f.toString() + "\n");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				jsonToken.nextTo('{');
				jsonObj = new JSONObject(jsonToken);
//				image(jsonObj.toString());
				
				jsonToken.nextTo('{');
				jsonObj = new JSONObject(jsonToken);
			}
		}
		try {
			newFile.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private HashMap<Integer, Integer> extractVec(String string) {
		// TODO Auto-generated method stub
		HashMap<Integer, Integer> f = new HashMap<Integer, Integer>();
		string = string.replaceAll("[^-A-Za-z0-9 %]+", "");
		string = string.toLowerCase();
		for(String s : string.split(" ")) {
			int t = feature.indexOf(s);
			if(t != -1) {
				if(f.containsKey(t)) {
					f.put(t, f.get(t) + 1);
				} else {
					f.put(t, 1);
				}
			}
		}
		
		return f;
	}

	private void titleTFIDF() {
		// TODO Auto-generated method stub
		double amt = 0;
		int cnt = 0, threshold;

		for(Map.Entry<String, Integer> entry : idf.entrySet()) {
			double d = tf.get(entry.getKey()).doubleValue() * Math.log(fileNum/(double)(entry.getValue()+1));
			d = new BigDecimal(d).setScale(2, RoundingMode.HALF_UP).doubleValue();
			tfidf.put(entry.getKey(), d);
			amt += d;
			cnt++;
		}
		
		threshold = (int) (amt/(cnt*0.3));
		
		for(Map.Entry<String, Double> entry : tfidf.entrySet()) {
			if(entry.getValue() > threshold && !entry.getKey().equals("") && !entry.getKey().equals("-")) {
				feature.add(entry.getKey());
			}
		}

//		try {
//			newFile.write(tfidf.toString());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	private void image(String string) {
		// TODO Auto-generated method stub
		
	}

	private void titleCount(String string) {
		// TODO Auto-generated method stub
		HashMap<String, Integer> t = new HashMap<String, Integer>();
		string = string.replaceAll("[^-A-Za-z0-9 %]+", "");
		string = string.toLowerCase();
		for(String s : string.split(" ")) {
			if(!t.containsKey(s)) {
				t.put(s, 1);
			} else {
				t.put(s, t.get(s)+1);
			}
		}
		for(Map.Entry<String, Integer> entry : t.entrySet()) {
			if(tf.containsKey(entry.getKey())) {
				tf.put(entry.getKey(), tf.get(entry.getKey()) + entry.getValue());
				idf.put(entry.getKey(), idf.get(entry.getKey()) + 1);
			} else {
				tf.put(entry.getKey(), 1);
				idf.put(entry.getKey(), 1);
			}	
		}
	}

}
