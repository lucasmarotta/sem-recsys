package br.dcc.ufba.themoviefinder.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;

@Component
public class NLPTokenizer 
{
	private Tokenizer basicTokenizer;
	private POSTaggerME speechTagger;
	private List<TokenNameFinderModel> entityModels;
	private static final List<String> SPEECH_TAGS = Arrays.asList("NN", "JJ", "JJR", "JJS");
	private static final List<String> ESCAPE_CHARS = Arrays.asList("'", "\"", ".", "-");
	//private static final Logger LOGGER = LogManager.getLogger(NLPTokenizer.class);
	
	public NLPTokenizer() throws Exception
	{
		this(SimpleTokenizer.INSTANCE);
	}
	
	public NLPTokenizer(Tokenizer basicTokenizer) throws Exception
	{
		this.basicTokenizer = basicTokenizer;
		TokenNameFinderModel personModel = new TokenNameFinderModel(getClass().getResource("/nlp_models/en-ner-person.bin"));
		TokenNameFinderModel locationModel = new TokenNameFinderModel(getClass().getResource("/nlp_models/en-ner-location.bin"));
		TokenNameFinderModel organizationModel = new TokenNameFinderModel(getClass().getResource("/nlp_models/en-ner-organization.bin"));
		entityModels = Arrays.asList(personModel, locationModel, organizationModel);
		speechTagger = new POSTaggerME(new POSModel(getClass().getResource("/nlp_models/en-pos-maxent.bin")));
	}
	
	public NLPTokenizer(Tokenizer basicTokenizer, List<TokenNameFinderModel> entityModels) throws Exception
	{
		this.basicTokenizer = basicTokenizer;
		this.entityModels = entityModels;
		speechTagger = new POSTaggerME(new POSModel(getClass().getResource("/nlp_models/en-pos-maxent.bin")));
	}
	
	public List<String> tokenize(String sentence)
	{
		List<String> movieTokens = new ArrayList<String>();
		final String[] tokens = basicTokenizer.tokenize(sentence);
		movieTokens.addAll(getEntityTokens(tokens));
		movieTokens.addAll(getSpeechTokens(tokens));
		return movieTokens;
	}
	
	private List<String> getSpeechTokens(final String[] tokens)
	{
		List<String> speechTokens = new ArrayList<String>();
		String tags[] = speechTagger.tag(tokens);
	    for (int i = 0; i < tags.length; i++){
	    	String token = tokens[i].toLowerCase();
	    	token = token.substring(0, 1).toUpperCase() + token.substring(1);
	    	if(SPEECH_TAGS.contains(tags[i]) && token.length() > 1) {
	    		speechTokens.add((token.substring(0, 1).toUpperCase() + token.substring(1)).trim());
	    	}
	    }
		return speechTokens;	
	}
	
	private List<String> getEntityTokens(final String[] tokens)
	{
		List<String> entityTokens = new ArrayList<String>();
		for (TokenNameFinderModel model : entityModels) {
			NameFinderME nameFinder = new NameFinderME(model);
			Span nameSpans[] = nameFinder.find(tokens);
			for (Span span : nameSpans) {
				String entity = "";
	            for(int index = span.getStart(); index < span.getEnd(); index++){
	            	if(!ESCAPE_CHARS.contains(tokens[index])) {
		            	entity += tokens[index];
		            	if(index + 1 < span.getEnd()) entity += "_";
	            	}
	            }
	            if(entity.length() > 0) {
		            entity = entity.trim().replaceAll("\\s", "_").replaceAll("\\_+", "_");
		            if(entity.charAt(entity.length() - 1) == '_') entity = entity.substring(0, entity.length() - 1);
		            entity = entity.replaceAll("\\?+", "").trim();
		            if(entity.length() > 1) {
			            entityTokens.add(entity);
		            }
	            }
			}
		}
		return entityTokens;
	}
}
