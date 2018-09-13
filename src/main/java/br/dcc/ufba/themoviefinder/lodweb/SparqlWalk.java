package br.dcc.ufba.themoviefinder.lodweb;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
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
	public String uri;
	
	@Value("${app.dbpedia-service-timeout}") 
	public String timeout;
	
	@Value("${app.dbpedia-service-log-queries: false}")
	public boolean logQuery;
	
	@Value("${app.dbpedia-using-graph: false}")
	public boolean usingGraph;
	
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
	public void setUri(String uri)
	{
		this.uri = uri;
	}
	
	/**
	 * Get Sparql service uri
	 * @return
	 */
	public String getUri()
	{
		return uri;
	}
	
	/**
	 * Count the number of direct links (RDF properties) between 2 given resources
	 * @param uri1
	 * @param uri2
	 * @return
	 */
	public int countDirectLinksBetween2Resources(String uri1, String uri2) 
	{
		int finding = 0;
		if(uri1 != null && uri2 != null) {
			StringBuilder queryString = new StringBuilder();
			queryString.append(Sparql.addService(usingGraph, uri));
			queryString.append(" SELECT (count (distinct ?p1) as ?x) WHERE { values (?r1 ?r2) {( <" + uri1+ ">  <" + uri2+ "> )} " + ". ?r1 ?p1 ?r2 . FILTER (?r1 != ?r2)}"); 
			queryString.append(Sparql.addServiceClosing(usingGraph));
			Query query = QueryFactory.create(Sparql.addPrefix()+queryString.toString());
			if(logQuery) {
				LOGGER.info(query);
			}
			QueryExecution qexec = QueryExecutionFactory.create(query, model);
			try {
				ResultSet rs = qexec.execSelect();
				for (; rs.hasNext();) {
					QuerySolution rb = rs.nextSolution();
					RDFNode x = rb.get("x");
					finding = (int) x.asLiteral().getValue();
				}
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			} finally {
				qexec.close();
			}	
		} else {
			throw new NullPointerException("uri1 and uri2 must not be null");
		}
		return finding;
	}
	
	/**
	 * Count the number of direct links (RDF properties) between N given resources
	 * @param resource
	 * @param resourceList
	 * @param wrapResource
	 * @return
	 */
	public int countDirectLinksBetweenNResources(String resource, List<String> resourceList, boolean wrapResource)
	{
		int finding = 0;
		if(ObjectUtils.allNotNull(resource, resourceList)) {
			int size = resourceList.size();
			if(size > 0) {
				if(wrapResource) {
					resource = Sparql.wrapStringAsResource(resource);
				}
				String queryString = new StringBuilder().append(Sparql.addService(usingGraph, uri)).append(BASE_COUNT_QUERY).toString();
				StringBuilder unionQuery = new StringBuilder();
				for (int i = 0; i < size; i++) {
					String resource3 = resourceList.get(i);
					if(wrapResource) {
						resource3 = Sparql.wrapStringAsResource(resource3);
					}
					unionQuery.append("{values (?r1 ?r2) {( <" + resource + ">  <" + resource3 + "> )} . ?r1 ?p1 ?r2 . FILTER (?r1 != ?r2)}");
					if(i+1 < size) {
						unionQuery.append("UNION ");
					}
				}
				queryString = String.format(queryString, unionQuery.toString()) + Sparql.addServiceClosing(usingGraph);
				Query query = QueryFactory.create(Sparql.addPrefix()+queryString);
				if(logQuery) {
					LOGGER.info(query);
				}
				QueryExecution qexec = QueryExecutionFactory.create(query, model);
				try {
					ResultSet rs = qexec.execSelect();
					for (; rs.hasNext();) {
						QuerySolution rb = rs.nextSolution();
						RDFNode x = rb.get("x");
						finding = (int) x.asLiteral().getValue();
					}
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				} finally {
					qexec.close();
				}				
			}
			
		} else {
			throw new NullPointerException("resourceUri and resourceUriList must not be null");
		}
		return finding;
	}
	
	/**
	 * Count the number of indirect outgoing links (RDF properties) between two resources
	 * @param uri1
	 * @param uri3
	 * @return
	 */
	public int countTotalNumberOfIndirectOutgoingLinksBetween2Resources(String uri1, String uri3) {
		int finding = 0;
		StringBuilder queryString = new StringBuilder();
		queryString.append(Sparql.addService(usingGraph, uri));				
		queryString.append("SELECT (count (*) as ?x) WHERE { values (?r1 ?r3) { ( <" + uri1 + ">  <" + uri3 + ">  )} ?r2 ?p1 ?r1 . ?r2 ?p1 ?r3 . ");
		queryString.append(Sparql.addFilter(null, null, "r2"));
		queryString.append("}");
		queryString.append(Sparql.addServiceClosing(usingGraph));
		Query query = QueryFactory.create(Sparql.addPrefix()+queryString.toString());
		if(logQuery) {
			//LOGGER.info(query);
		}
		
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		try {
			ResultSet rs = qexec.execSelect();
			for (; rs.hasNext();) {
				QuerySolution rb = rs.nextSolution();
				RDFNode x = rb.get("x");
				finding = (int) x.asLiteral().getValue();
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			qexec.close();
		}
		
		return finding;
	}
	
	/**
	 * Count the number of indirect outgoing links (RDF properties) between two resources
	 * @param resource
	 * @param resourceList
	 * @param wrapResource
	 * @return
	 */
	public int countTotalNumberOfIndirectOutgoingLinksBetweenNResources(String resource, List<String> resourceList, boolean wrapResource) 
	{
		int finding = 0;
		if(ObjectUtils.allNotNull(resource, resourceList)) {
			int size = resourceList.size();
			if(size > 0) {
				if(wrapResource) {
					resource = Sparql.wrapStringAsResource(resource);
				}
				String queryString = new StringBuilder().append(Sparql.addService(usingGraph, uri)).append(BASE_COUNT_QUERY).toString();
				StringBuilder unionQuery = new StringBuilder();
				for (int i = 0; i < size; i++) {
					String resource3 = resourceList.get(i);
					if(wrapResource) {
						resource3 = Sparql.wrapStringAsResource(resource3);
					}
					unionQuery.append("{values (?r1 ?r3) { ( <" + resource + ">  <" + resource3 + ">  )} ?r2 ?p1 ?r1 . ?r2 ?p1 ?r3 . ");
					unionQuery.append(Sparql.addFilter(null, null, "r2")).append("}");
					if(i+1 < size) {
						unionQuery.append("UNION ");
					}
				}
				queryString = String.format(queryString, unionQuery.toString()) + Sparql.addServiceClosing(usingGraph);
				Query query = QueryFactory.create(Sparql.addPrefix()+queryString.toString());
				if(logQuery) {
					LOGGER.info(query);
				}
				QueryExecution qexec = QueryExecutionFactory.create(query, model);
				try {
					ResultSet rs = qexec.execSelect();
					for (; rs.hasNext();) {
						QuerySolution rb = rs.nextSolution();
						RDFNode x = rb.get("x");
						finding = (int) x.asLiteral().getValue();
					}
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				} finally {
					qexec.close();
				}
			}
		}
		return finding;
	}
}
