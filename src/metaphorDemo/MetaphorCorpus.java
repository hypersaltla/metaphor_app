package metaphorDemo;

//import java.lang.reflect.Type;
import java.io.*;
import java.util.*;

//import com.google.gson.*;
//import com.google.gson.reflect.TypeToken;
import org.json.simple.*;

public class MetaphorCorpus {
	//private static String file = DataSource.RESOURCE_PATH + DataSource.MST;
	private static HashMap<String, JSONArray> metaphorCorpus = new HashMap<String, JSONArray>();
	private static HashMap<String, JSONArray> sourceList = new HashMap<String, JSONArray>();
	private static HashMap<String, JSONObject> sourceToTarget = new HashMap<String, JSONObject>();
	
	//public static int clickCnt = 0;
	
	static {
		loadCorpus("EN");
		loadCorpus("SP");
		loadCorpus("FA");
		loadCorpus("RU");
	}
	
	/*The static code loads the metaphorCorpus into the in-memory JSON data structures
	 * @param language, the corpus of the specified language will be load
	 */
	private static void loadCorpus(String language)
	{
		String file = DataSource.RESOURCE_PATH + "metaphor_corpus/" + language + "_" + DataSource.MST;
		try {
		
			JSONArray corpus = (JSONArray) JSONValue.parse(new BufferedReader(new FileReader(file)));
			JSONArray source_list = new JSONArray();
			JSONObject source_to_target = new JSONObject();
			
			int metaphor_id = 0;
			for(Object item : corpus) {
				JSONObject jsonItem = (JSONObject) item;
				Object source = jsonItem.get("source");
				Object target = jsonItem.get("target");
				metaphor_id++;
				if(source.equals("") || target.equals("")) {
					continue;
				}
				if(!source_to_target.containsKey(source)) {
					source_list.add(source);
					source_to_target.put(source, new JSONObject());
				}
				JSONObject targets = (JSONObject) source_to_target.get(source);
				if(!targets.containsKey(target)) {
					targets.put(target, new JSONArray());
				}
				JSONArray idList = (JSONArray) targets.get(target);
				idList.add(metaphor_id - 1);
				targets.put(target, idList);
				source_to_target.put(source, targets);
			}
			
			metaphorCorpus.put(language, corpus);
			sourceList.put(language, source_list);
			sourceToTarget.put(language, source_to_target);
			//...put more corpus from different languages
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// stop the server if the metaphor corpus fails to be loaded
			System.exit(1);
		}
		
	}
	
	/* This method returns an item in the Metaphor JSON File.
	 * @param index, The index of the item in the JSON array.
	 * @param language, the language of the corpus.
	 * @return A JSONObject which is a map.
	 * @throw IndexOutOfBoundsException
	 */
	public static JSONObject getItemAt(String language, int index) throws IndexOutOfBoundsException {
		return (JSONObject) metaphorCorpus.get(language).get(index);
	}
	
	/* The following two methods return a metaphor (either cleaned or raw) in the Metaphor JSON File.
	 * @param index, The index of the item in the JSON array.
	 * @param language, the language of the corpus.
	 * @return A String represents the raw metaphor sentence.
	 * @throw IndexOutOfBoundsException
	 */
	public static String getMetaphorAt(String language, int index) throws IndexOutOfBoundsException {
		JSONObject item = (JSONObject) metaphorCorpus.get(language).get(index);
		return (String) item.get("raw_metaphor");
	}
	
	public static String getCleanedMetaphorAt(String language, int index) throws IndexOutOfBoundsException {
		JSONObject item = (JSONObject) metaphorCorpus.get(language).get(index);
		return (String) item.get("metaphor");
	}
	
	/* the method returns the targets and metaphor item ids to the corresponding sources
	 * @param language
	 * @param source
	 * @return a JSONObject
	 */
	
	public static JSONObject getTargets(String language, String source)
	{
		return (JSONObject) sourceToTarget.get(language).get(source);
	}
	
	/* get the source to target mappings through a list of source
	 * @param language
	 * @param source_list, a list of sources in JSONArray type
	 * @return a JSONObject stores mappings
	 */
	public static JSONObject getSourceTargetMappings(String language, JSONArray source_list) 
	{
		JSONObject mappings = new JSONObject();
		for(Object item : source_list) {
			mappings.put(item, sourceToTarget.get(language).get(item));
		}
		return mappings;
	}
	/* the method get a list of data items according the id list
	 * @param language
	 * @param idList, a JSONArray of ids
	 * @return the JSONArray of items list
	 */
	public static JSONArray getItemsByIndexes(String language, JSONArray idList)
	{
		JSONArray items = new JSONArray();
		for(Object id : idList) {
			items.add(metaphorCorpus.get(language).get(Integer.parseInt((String) id)));
		} 
		return items;
	}

	/* The following getter method will get the data items within a range with a language
	 * @param start, the start of the item index, inclusive
	 * @param end, the end of the item index, exclusive
	 * @param language, the language of the metaphor corpus
	 * @return a JSON Array denote the item list
	 * @throw IndexOutOfBoundsException
	 */
	
	public static JSONArray getItemsAtRange(int start, int end, String language) throws IndexOutOfBoundsException
	{
		JSONArray metaList = new JSONArray();
		for(int i = start; i < end; i++) {
			metaList.add(metaphorCorpus.get(language).get(i));
		}
		return metaList;
	}
	
	public static JSONArray getSourcesAtRange(int start, int end, String language) throws IndexOutOfBoundsException
	{
		JSONArray source_list = new JSONArray();
		for(int i = start; i < end; i++) {
			source_list.add(sourceList.get(language).get(i));
		}
		return source_list;
	}
	
	/* The following getter return the size of metaphor corpus
	 * @param language
	 * @return int, size of metaphor corpus
	 */
	public static int getSizeOfMetaphor(String language) 
	{
		return metaphorCorpus.get(language).size();
	}
	
	public static int getSizeOfSources(String language)
	{
		return sourceList.get(language).size();
	}
	
	/*
	public static void main(String[] args)
	{
		//System.out.println(getItemAt("SP", 100));
		//System.out.println(getMetaphorAt("EN", 11));
		//System.out.println(getTargets("EN","only treading water").toString());
		//System.out.println(getTargets("SP","solidas bases").toString());
		//System.out.println(getItemsAtRange(9000, 20000, "kk"));
		//System.out.println(getSourcesAtRange(0,40,"EN"));
		JSONArray array = (JSONArray) getSourcesAtRange(0,10,"EN");
		System.out.println(getSourceTargetMappings("EN", array).toString());
		System.out.println(getItemAt("EN", 6292));
		//System.out.println(getSizeOfMetaphor("EN") + " " + getSizeOfSources("EN"));
		//System.out.println(getMetaphorAt("SP", 0));
		//System.out.println(getMetaphorAt("FA", 0));
		//System.out.println(getMetaphorAt("RU", 0));
		//String ru_str = getMetaphorAt("RU", 0);
		//System.out.println(ru_str.charAt(2));
		//System.out.println(ru_str.codePointAt(2));
		//char [] arr= Character.toChars(1024);
	}
	*/
}
