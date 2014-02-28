package metaphorDemo;

import java.io.*;
import java.util.*;

import pitt.search.semanticvectors.CloseableVectorStore;
import pitt.search.semanticvectors.FlagConfig;
import pitt.search.semanticvectors.LuceneUtils;
import pitt.search.semanticvectors.ObjectVector;
import pitt.search.semanticvectors.VectorSearcher;
import pitt.search.semanticvectors.VectorStoreReader;
import pitt.search.semanticvectors.ZeroVectorException;
import pitt.search.semanticvectors.SearchResult;

import org.json.simple.*;
import org.apache.lucene.*;

public class SimVecBean {
	/* path to the term vectors store created by Lucene */
	//private static String pathToTermVec = DataSource.RESOURCE_PATH + DataSource.TERM_Vector;
	/* path to the document vectors store created by Lucene */
	//private static String pathToDocVec = DataSource.RESOURCE_PATH + DataSource.DOC_Vector;
	
	
	/* specify the number of search result returned */
	private static final String numsearchresults = "10";
	/** Principal vector store for finding query vectors. */
    private static CloseableVectorStore queryVecReader = null;
    /** 
     * Vector store for searching. Defaults to being the same as queryVecReader.
     * May be different from queryVecReader, e.g., when using terms to search for documents.
     */
    private static CloseableVectorStore searchVecReader = null;
    
    private static LuceneUtils luceneUtils;
	
    /* Search the semantic vector by the user input sentence, use Consine Similarity
     * @param sentence A metaphor sentence from user
     * @return A list of SearchResult which will be further processed by other methods
     */
	private static LinkedList<SearchResult> searchByTerms(String sentence, String language)
	{
			
		if(sentence == "" || sentence == null) {
			return null;
		}
		
		String[] queryTerms = sentence.split("\\s");
		
		/* create arguments for the vector searcher */
		String[] args = new String[6];
		args[0] = "-queryvectorfile";
		args[1] = DataSource.RESOURCE_PATH + "semantic_vec/" + language + "_" + DataSource.TERM_Vector;
		args[2] = "-searchvectorfile";
		args[3] = DataSource.RESOURCE_PATH + "semantic_vec/" + language + "_" + DataSource.DOC_Vector;
		args[4] = "-numsearchresults";
		args[5] = numsearchresults;
		
		/* Setup query vector and search vector.
		 */
		FlagConfig flagConfig = FlagConfig.getFlagConfig(args);
		try {
			queryVecReader = VectorStoreReader.openVectorStore(flagConfig.queryvectorfile(), flagConfig);
			searchVecReader = VectorStoreReader.openVectorStore(flagConfig.searchvectorfile(), flagConfig);

		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		/* Perform search and get search results*/
		VectorSearcher vecSearcher = null;
		LinkedList<SearchResult> results = new LinkedList<SearchResult>();
		try {
			/*Use Cosine similarity as measure score */
			vecSearcher = new VectorSearcher.VectorSearcherCosine(queryVecReader, searchVecReader, luceneUtils, flagConfig, queryTerms);
		}
		catch(ZeroVectorException ze) {
			ze.getMessage();
		}
		results = vecSearcher.getNearestNeighbors(Integer.parseInt(numsearchresults));
		
		queryVecReader.close();
		searchVecReader.close();
		
		return results;
	}
	
	/* Search the sentence and produce A JSON string for the search result 
	 * @param sentence User input metaphor
	 * @return A String in JSON format
	 * */

	public static JSONArray getSearchResults(String sentence, String language)
	{
		LinkedList<SearchResult> results = searchByTerms(sentence, language);
		JSONArray resultList = new JSONArray();
		for(SearchResult sr : results) {
			String term = ((ObjectVector)sr.getObjectVector()).getObject().toString();
			
			//file name in the format mp[\d]+.txt, so we can get the document ID
			int docId = Integer.parseInt(term.substring(term.lastIndexOf('/') + 3, term.lastIndexOf('.')));
		//	System.out.print(term + " ");
		//	System.out.println(docId);
			JSONObject mymap = MetaphorCorpus.getItemAt(language, docId);
			mymap.put("score", sr.getScore());
			resultList.add(mymap);
		}
		return resultList;
	}
	
	public static void main(String[] args)
	{
		//System.out.println(SimVecBean.getSearchResults("fee are like water in a reservoir", "EN").toString());
		//System.out.println(SimVecBean.getSearchResults("tengo mucho que hacer", "SP").toString());
		//FlagConfig flagConfig = FlagConfig.getFlagConfig(args);
		//flagConfig.
		String lang = "RU";
		int size = MetaphorCorpus.getSizeOfMetaphor(lang);
		JSONArray results = null;
		String scv1 = "sentiment_compare_valence_" + lang + ".txt";
		//String scv2 = "sentiment_compare_valence_3.txt";
		//String scp1 = 
		// cat file |sed 's/[2-3]/1/g'|sort|uniq -c|sort -nr
		int start = 0;
		try {
			PrintWriter out = new PrintWriter("/Users/AceYan/Documents/NLP_DR/final_experiment/semantic_indexing/" + scv1);
			for(int i = start; i < size; i++) {
				results = SimVecBean.getSearchResults(MetaphorCorpus.getCleanedMetaphorAt(lang, i), lang);
				out.print(MetaphorCorpus.getItemAt(lang, i).get("valence"));
				for(int j = 0; j < results.size() && j < 10; j++) {
					out.print( " " + ((JSONObject) results.get(j)).get("valence").toString() );
				}
				out.println();
				System.out.println("processed: line " + i);
			}
			System.out.println("processed lines: " + size);
			out.close();
		}
		catch(IOException err) {
			System.out.println(err.getMessage());
		}
	}
	
}
