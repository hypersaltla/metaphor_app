package metaphorDemo;

//import org.json.simple.*;
import java.io.*;
import java.util.*;

/* A singleton class, support read-only access
 * 
 * */
public class DomainStat {
	// A HashMap used for storing domain statistics for fast lookup
	private static HashMap<String, HashMap<String, ArrayList<Integer>>> statTables = null; 
	private static DomainStat instance = null;
	
	//Constructor
	protected DomainStat() {
		statTables = new HashMap<String, HashMap<String, ArrayList<Integer>>>();
		loadDomainStat("EN");
		loadDomainStat("SP");
		loadDomainStat("FA");
		loadDomainStat("RU");
	}
	
	public static DomainStat getInstance() {
		if(statTables == null) {
			instance = new DomainStat();
		}
		return instance;
	}
	
	private static void loadDomainStat(String lang)
	{
		if(statTables == null) {
			return;
		}
		BufferedReader reader = null;
		HashMap<String, ArrayList<Integer>> statTable = new HashMap<String, ArrayList<Integer>>();
		try {
			reader = new BufferedReader(new FileReader(DataSource.RESOURCE_PATH + "domain_stats/" + lang + "_" + DataSource.DOMAIN_STAT));
			String line = null;
			while((line = reader.readLine()) != null) {
				String[] arr = line.split("\t");
				//length of the array should be 8, one for domain name, seven for valence scores
				if(arr.length != 8) {
					continue;
				}
				if(!statTable.containsKey(arr[0])) {
					ArrayList<Integer> stats = new ArrayList<Integer>();
					for(int i = 1; i <= 7; i++) {
						stats.add(Integer.parseInt(arr[i]));
					}
					statTable.put(arr[0], stats);
				}
			}
			statTables.put(lang, statTable);
		}
		catch(IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public ArrayList<Integer> get(String key, String lang) {
		return statTables.get(lang).get(key);
	}

	/*
	public static void main(String[] args) {
		DomainStat inst = DomainStat.getInstance();
		ArrayList<Integer> arr = inst.get("politics", "EN");
		ArrayList<Integer> arr1 = inst.get("politics", "SP");
		System.out.println(arr.toString());
		System.out.println(arr1.toString());
	}
	*/
}
