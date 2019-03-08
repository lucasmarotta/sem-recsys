package br.dcc.ufba.themoviefinder.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

@Component
public class NLPTokenizer 
{
	private Tokenizer basicTokenizer;
	private POSTaggerME speechTagger;
	private List<TokenNameFinderModel> entityModels;
	private static final List<String> SPEECH_TAGS = Arrays.asList("NN", "NNP", "NNPS", "NNS", "JJ", "JJR", "JJS", "FW", "VB");
	private static final List<String> ESCAPE_CHARS = Arrays.asList("'", "‘", "’", "\"", ",", "_", "?", "!", "@", "$", "&", "*", "%", "#");
	private static final List<String> ESCAPE_WORDS = Arrays.asList("do", "are", "fu", "go", "be", "get", "de", "or", "but", "the", "and");
	
	public NLPTokenizer() throws Exception
	{
		this(SimpleTokenizer.INSTANCE);
	}
	
	public NLPTokenizer(Tokenizer basicTokenizer) throws Exception
	{
		this.basicTokenizer = basicTokenizer;
	}
	
	public static String caseFormatToDBPedia(String value)
	{
		return Arrays.asList(value.toLowerCase().replaceAll("\\s+", " ").split(" ")).stream().map(word -> StringUtils.capitalize(word)).collect(Collectors.joining("_"));
	}	
	
	public void loadModels() throws Exception
	{
		loadModels(getEntityModels(Arrays.asList("en-ner-person.bin", "en-ner-location.bin", "en-ner-organization.bin")));
	}
	
	public void loadModels(List<TokenNameFinderModel> entityModels) throws Exception
	{
		this.entityModels = entityModels;
		basicTokenizer = new TokenizerME(new TokenizerModel(getClass().getResource("/nlp_models/en-token.bin")));
		speechTagger = new POSTaggerME(new POSModel(getClass().getResource("/nlp_models/en-pos-maxent.bin")));
	}
	
	public void unloadModels()
	{
		entityModels = null;
		speechTagger = null;
		System.gc();
	}
	
	public boolean isModelsLoaded()
	{
		return ObjectUtils.allNotNull(entityModels, speechTagger);
	}
	
	public List<String> tokenize(String sentence)
	{
		if(isModelsLoaded()) {
			//sentence = org.apache.commons.codec.binary.StringUtils.newStringUtf8(sentence.getBytes(Charset.forName("windows-1252")));
			sentence = sentence.replaceAll("\\s+", " ");
			List<String> tokens = new ArrayList<String>();
			String[] sentenceTokens = basicTokenizer.tokenize(sentence);	
			tokens.addAll(getSpeechTokens(sentenceTokens));
			List<String> entityTokens = getEntityTokens(sentenceTokens).stream().filter(((Predicate<String>) tokens::contains).negate()).collect(Collectors.toList());
			for (String entity : entityTokens) {
				List<Integer> tokensIndex = Arrays.asList(entity.split(" ")).stream().map(tokens::indexOf).filter(index -> index != -1).collect(Collectors.toList());
				if(isSequence(tokensIndex)) {
					tokens.removeAll(tokensIndex.stream().map(tokens::get).collect(Collectors.toList()));
				}
			}
			tokens.addAll(entityTokens);
			return tokens.stream().map(NLPTokenizer::caseFormatToDBPedia).collect(Collectors.toList());
		} else {
			throw new IllegalStateException("models are not loaded");
		}
	}
	
	private List<String> getSpeechTokens(String[] tokens)
	{
		List<String> speechTokens = new ArrayList<String>();
		String tags[] = speechTagger.tag(tokens);
	    for (int i = 0; i < tags.length; i++){
	    	String token = tokens[i].toLowerCase().replace("’s", "").replaceAll("\\.+", ".").replaceAll("[" + ESCAPE_CHARS.stream().map(ch -> "\\" + ch).collect(Collectors.joining("")) + "]+", "").trim();
	    	if(token.length() > 1) {
    			if(token.length() > 4 && StringUtils.countMatches(token, '.') == 1) {
    				token = token.replace(".", "");
    			}
    			if(token.charAt(0) == '.') {
    				token = token.substring(1);
    			}
    	    	if(token.length() > 1 && SPEECH_TAGS.contains(tags[i]) && ! ESCAPE_WORDS.stream().anyMatch(token::equals) && ! StringUtils.isNumeric(token) && token.replace(".", "").length() > 1) {
    	    		speechTokens.add(token);
    	    	}
	    	}
	    }
		return speechTokens;	
	}
	
	private List<TokenNameFinderModel> getEntityModels(List<String> nerResources) throws IOException
	{
		List<TokenNameFinderModel> nerModels = new ArrayList<TokenNameFinderModel>();
		for (String resource : nerResources) {
			nerModels.add(new TokenNameFinderModel(getClass().getResource("/nlp_models/"+resource)));
		}
		return nerModels;
	}
	
	private List<String> getEntityTokens(String[] tokens)
	{
		List<String> entityTokens = new ArrayList<String>();
		for (TokenNameFinderModel model : entityModels) {
			NameFinderME nameFinder = new NameFinderME(model);
			Span nameSpans[] = nameFinder.find(tokens);
			for (Span span : nameSpans) {
				String entity = "";
	            for(int index = span.getStart(); index < span.getEnd(); index++) {
	            	if(! ESCAPE_CHARS.contains(tokens[index])) {
		            	entity += tokens[index];
		            	if(index + 1 < span.getEnd()) entity += " ";
	            	}
	            }
	            if(entity.length() > 0) {
		            entity = entity.replaceAll("[" + ESCAPE_CHARS.stream().map(ch -> "\\" + ch).collect(Collectors.joining("")) + "]+", "").trim();
		            entity = entity.toLowerCase().replaceAll("\\s+", " ").replaceAll("\\_+", " ").trim();
		            if(! Arrays.asList("dr.", "mr.", "jr.", "sr.").stream().anyMatch(entity::contains)) {
		            	entity = entity.replaceAll("\\.+", "");
		            }
		            if(entity.length() > 1 && ! StringUtils.isNumeric(entity) && ! ESCAPE_WORDS.stream().anyMatch(entity::equals)) {
		            	entityTokens.add(entity);
		            }
	            }
			}
		}
		return entityTokens;
	}
	
	private boolean isSequence(List<Integer> values)
	{
		int size = values.size();
		if(size > 1) {
			int first = values.get(0);
			int last = values.get(size - 1);
			if(first + size - 1 == last) {
				return true;
			}	
		}
		return false;
	}
}
