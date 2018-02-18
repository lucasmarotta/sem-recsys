package br.dcc.ufba.semrecsys.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.SimpleTokenizer;

public class MovieTokenizer 
{
	private POSModel tagModel;
	private static final List<String> DEFAULT_TAGGS = Arrays.asList("NN", "NNP", "NNPS", "JJ", "JJR", "JJS");
	private static final Logger LOGGER = LogManager.getLogger(MovieTokenizer.class);
	
	public MovieTokenizer()
	{
		try {
			tagModel = new POSModel(getClass().getResource("/nlp_models/en-pos-maxent.bin").openStream());
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	public List<String> generateTokens(String text)
	{
		List<String> textTokens = new ArrayList<String>();
		if(tagModel != null) {
			
			POSTaggerME tagger = new POSTaggerME(tagModel);
			SimpleTokenizer tokenizer= SimpleTokenizer.INSTANCE;
			String tokens[] = tokenizer.tokenize(text);
			String tags[] = tagger.tag(tokens);
		    for (int i = 0; i < tags.length; i++){
		    	String token = tokens[i].toLowerCase();
		    	token = token.substring(0, 1).toUpperCase() + token.substring(1);
		    	if(DEFAULT_TAGGS.contains(tags[i]) && token.length() > 1 && !textTokens.contains(token)) {
		    		//System.out.println("token: "+tags[i]+" token:"+token);
		    		textTokens.add(token.substring(0, 1).toUpperCase() + token.substring(1));
		    	}
		    }
		}
		return textTokens;
	}
}
