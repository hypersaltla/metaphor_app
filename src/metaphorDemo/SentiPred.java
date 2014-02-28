package metaphorDemo;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import metaphorDemo.interfaces.FeatGenerator;

public class SentiPred {
	
	/*
	 * A wrapper method that calls Jython object that generate LIWC feature for a sentence
	 * @param lang, language of the sentence
	 * @param sentence, the user input sentence
	 * @return a line of LIWC feature
	 */
	public String generateLIWCFeat(String lang, String sentence)
	{
		JythonObjectFactory factory = new JythonObjectFactory(
	            FeatGenerator.class, "FeatGeneratorLIWC", "GenerateLIWCFeat");

	    FeatGenerator featGenLIWC = (FeatGenerator) factory.createObject();
	    String [] params = new String[2];
	    params[0] = lang;
	    params[1] = DataSource.RESOURCE_PATH + "liwc_dict/";
	    featGenLIWC.setParams(params);
	    
	    featGenLIWC.loadResource();
	    return (String) featGenLIWC.generateFeat(sentence);

	}
	
	public String multiclassLogisticPred(String lang, String sentence)
	{
		return null;
	}
	
	public String SVMRegressionPred(String lang, String sentence)
	{
		return null;
	}
	
}
