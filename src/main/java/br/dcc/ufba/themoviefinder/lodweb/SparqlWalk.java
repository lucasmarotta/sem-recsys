package br.dcc.ufba.themoviefinder.lodweb;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SparqlWalk 
{
	@Value("${app.dbpedia-service-uri}") 
	public String serviceUri;
	
	@Value("${app.dbpedia-service-log-queries: false}")
	public boolean logQuery = false;
	
	@Value("${app.dbpedia-using-graph: false}")
	public boolean usingGraph = false;
	
	public static final String BASE_COUNT_QUERY = "SELECT (count (distinct ?p1) as ?x) WHERE {%s}";
	
	private static final Logger LOGGER = LogManager.getLogger(SparqlWalk.class);
	
	Model model;
	
	public SparqlWalk()
	{
		model = ModelFactory.createDefaultModel();
	}
	
	/**
	 * Set Sparql service uri 
	 * @param uri
	 */
	public void setServiceUri(String uri)
	{
		this.serviceUri = uri;
	}
	
	/**
	 * Get Sparql service uri
	 * @return
	 */
	public String getServiceUri()
	{
		return serviceUri;
	}
	
	public boolean isLogQuery() 
	{
		return logQuery;
	}

	public void setLogQuery(boolean logQuery) 
	{
		this.logQuery = logQuery;
	}

	public boolean isUsingGraph() 
	{
		return usingGraph;
	}

	public void setUsingGraph(boolean usingGraph) 
	{
		this.usingGraph = usingGraph;
	}

	public boolean isResource(String uri)
	{
		String queryString = Sparql.addService(usingGraph, serviceUri) 
				+ "SELECT (count (distinct ?p) as ?x) WHERE {<" + uri + "> ?p ?v}"
				+ Sparql.addServiceClosing(usingGraph);
		return (execCountQuery(queryString)) > 0 ? true : false;
	}
	
	public boolean isRedirect(String uri1, String uri2) 
	{
		String queryString = Sparql.addService(usingGraph, serviceUri)
				+ "SELECT (count (distinct ?r2) as ?x) WHERE { "
				
				//resources reached by indirect outgoing links
				+ "{values (?r1 ?r2) {(<" + uri1+ ">  <" + uri2+ ">)} ?r1 ?p1 ?r2 . FILTER (?r1 != ?r2)} UNION "
				
				//resources reached by direct outgoing links
				+ "{values (?r1 ?r2) {(<" + uri2+ ">  <" + uri1+ ">)} ?r1 ?p1 ?r2 . FILTER (?r1 != ?r2)} "+
				
				//Filter for wikiPageRedirects
				"FILTER(?p1 = dbo:wikiPageRedirects)}" + Sparql.addServiceClosing(usingGraph);
		return (execCountQuery(queryString)) > 0 ? true : false;	
	}
	
	/**
	 * Count the number of direct links reached to the given resource
	 * @param uri
	 * @return
	 */
	public int countDirectLinksFromResource(String uri)
	{
		String queryString = Sparql.addService(usingGraph, serviceUri)
			+ "SELECT (count (distinct ?p1) as ?x) WHERE { "
			
			//resources reached by direct incoming links
			+ "{values (?r1) {(<" + uri + ">)} ?r1 ?p1 ?r2 . FILTER (?r1 != ?r2)} UNION "
			
			//resources reached by direct outgoing links
			+ "{values (?r1) {(<" + uri + ">)} ?r2 ?p1 ?r1 . FILTER (?r1 != ?r2)} " + Sparql.addFilter("p1", "r2") + "}"
			+ Sparql.addServiceClosing(usingGraph);
		return execCountQuery(queryString);
	}
	
	/**
	 * Count number of indirect links reached to the given resource
	 * @param uri
	 * @return
	 */
	public int countIndirectLinksFromResource(String uri)
	{
		String queryString = Sparql.addService(usingGraph, serviceUri)
			+ "SELECT (count (distinct ?r2) as ?x) WHERE { "
			
			//resources reached by indirect incoming links
			+ "{values (?r1) {(<" + uri + ">)} ?r2 ?p1 ?r1 . ?r2 ?p2 ?r3 . FILTER (?r1 != ?r3 && ?r2 != ?r1 && ?r2 != ?r3)} "
			
			//resources reached by indirect outgoing links
			/*+ "{values (?r1) {(<" + uri + ">)} ?r1 ?p1 ?r2 . ?r3 ?p2 ?r2 . FILTER (?r1 != ?r3 && ?r2 != ?r1 && ?r2 != ?r3)} "*/ + Sparql.addFilter("p1", "r2") + " . " + Sparql.addFilter("p2", null) + "}"
			+ Sparql.addServiceClosing(usingGraph);
		return execCountQuery(queryString);
	}
	
	/**
	 * Count the number of direct links between 2 given resources
	 * @param uri1
	 * @param uri2
	 * @return
	 */
	public int countDirectLinksBetween2Resources(String uri1, String uri2)
	{
		String queryString = Sparql.addService(usingGraph, serviceUri)
				+ "SELECT (count (distinct ?p1) as ?x) WHERE { "
				
				//resources reached by indirect outgoing links
				+ "{values (?r1 ?r2) {(<" + uri1+ ">  <" + uri2+ ">)} ?r1 ?p1 ?r2 . FILTER (?r1 != ?r2)} UNION "
				
				//resources reached by direct outgoing links
				+ "{values (?r1 ?r2) {(<" + uri2+ ">  <" + uri1+ ">)} ?r1 ?p1 ?r2 . FILTER (?r1 != ?r2)} " + Sparql.addFilter("p1", "r2") + "}"
				+ Sparql.addServiceClosing(usingGraph);
		return execCountQuery(queryString);
	}
	
	/**
	 * Count the number of indirect incoming/outgoing links between two resources
	 * @param uri1
	 * @param uri3
	 * @return
	 */
	public int countIndirectLinksBetween2Resources(String uri1, String uri3)
	{
		String queryString = Sparql.addService(usingGraph, serviceUri)
				+ "SELECT (count (distinct ?r2) as ?x) WHERE { "
				
				//resources reached by indirect incoming links
				+ "{values (?r1 ?r3) {(<" + uri1 + "> <" + uri3 + ">)} ?r2 ?p1 ?r1 . ?r2 ?p2 ?r3 . FILTER (?r1 != ?r3 && ?r2 != ?r1 && ?r2 != ?r3)} "
				
				//resources reached by indirect outgoing links
				/*+ "{values (?r1 ?r3) {(<" + uri1 + "> <" + uri3 + ">)} ?r1 ?p1 ?r2 . ?r3 ?p2 ?r2 . FILTER (?r1 != ?r3 && ?r2 != ?r1 && ?r2 != ?r3)} "*/ + Sparql.addFilter("p1", "r2") + " . " + Sparql.addFilter("p2", null) + "}"
				+ Sparql.addServiceClosing(usingGraph);
			return execCountQuery(queryString);
	}
	
	private int execCountQuery(String queryString)
	{
		int finding = 0;
		Query query = QueryFactory.create(Sparql.addPrefix() + queryString);
		if(logQuery) {
			if(LOGGER.isInfoEnabled()) {
				LOGGER.info(query);	
			}
		}
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		try {
			ResultSet rs = qexec.execSelect();
			for (; rs.hasNext();) {
				QuerySolution rb = rs.nextSolution();
				RDFNode x = rb.get("x");
				finding = (int) x.asLiteral().getValue();
			}
		} finally {
			qexec.close();
		}
		return finding;
	}
}
