package br.dcc.ufba.themoviefinder.services;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DBPediaService 
{
	@Value("${app.dbpedia-service-uri}") 
	public String url;
	
	@Value("${app.dbpedia-service-timeout}") 
	public String timeout;
	
	@Value("${app.dbpedia-service-log-queries: false}")
	public boolean logQuery;
	
	public static final String BASE_DBP_QUERY = "PREFIX dbr: <http://dbpedia.org/resource/>\r\n" + 
			"PREFIX dbo: <http://dbpedia.org/ontology/>\r\n" + 
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" + 
			"PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n" + 
			"PREFIX dct: <http://purl.org/dc/terms/>\r\n" + 
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n" + 
			"\r\n" + 
			"SELECT ?value\r\n" + 
			"WHERE {\r\n" + 
			"	%s \r\n" + 
			"	FILTER((\r\n" + 
			"		?property = rdf:type ||\r\n" + 
			"		?property = dct:subject ||\r\n" + 
			"		?property = dbo:wikiPageRedirects ||\r\n" + 
			"		?property = dbo:wikiPageDisambiguates\r\n" + 
			"	) && ?value != owl:Thing\r\n" + 
			"	&& !STRSTARTS(STR(?value), \"http://www.wikidata.org/entity/\")\r\n" + 
			"	&& !STRSTARTS(STR(?value), \"http://wikidata.dbpedia.org/resource/\"))\r\n" + 
			"}";
	
	private static final Logger LOGGER = LogManager.getLogger(DBPediaService.class);
	
	public List<String> getResourceTokens(List<String> resources)
	{
		List<String> dbPediaTokens = new ArrayList<String>();
		int size = resources.size();
		
		if(size > 0) {
			String resourceQuery = "";
			for (int i = 0; i < size; i++) {
				resourceQuery += "{<http://dbpedia.org/resource/"+resources.get(i)+"> ?property ?value.} ";
				if(i+1 < size) {
					resourceQuery += "UNION ";
				}
			}
			resourceQuery = String.format(BASE_DBP_QUERY, resourceQuery);
			if(logQuery) LOGGER.info(resourceQuery);
			List<QuerySolution> result = query(resourceQuery);
			return parseResultVarName(result, "?value");
		}
		return dbPediaTokens;
	}
	
	public List<QuerySolution> query(String query)
	{
		QueryEngineHTTP qExec = null;
		try {
			qExec = new QueryEngineHTTP(url, query);
			qExec.addParam("timeout", timeout);
	        ResultSet rs = qExec.execSelect();
	        return ResultSetFormatter.toList(rs);
		} catch(Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if(qExec != null) qExec.close();
		}
		return new ArrayList<QuerySolution>();
	}
	
	public List<String> parseResultVarName(List<QuerySolution> result, String varName)
	{
		if(result != null && varName != null) {
			List<String> resultVarName = new ArrayList<String>();
			for (QuerySolution querySolution : result) {
				RDFNode rdfNode = querySolution.get(varName);
				String varNameValue = rdfNode.toString();
				if(!varNameValue.isEmpty()) {
					String[] varNameValueParts = varNameValue.split("/|#");
					if(varNameValueParts.length > 0) {
						varNameValue = varNameValueParts[varNameValueParts.length - 1];
						if(!varNameValue.isEmpty()) {
							varNameValueParts = varNameValue.split("@");
							if(varNameValueParts.length > 0) {
								varNameValue = varNameValueParts[0];
							}
							
							try {
								varNameValue = URLDecoder.decode(varNameValue, "UTF-8");
							} catch (UnsupportedEncodingException e) {
								LOGGER.error(e.getMessage(), e);
							}
							
							resultVarName.add(varNameValue.trim());
						}
					}
				}
			}
			return resultVarName;
		} else {
			throw new IllegalArgumentException("result and varName must not be null");
		}
	}
}
