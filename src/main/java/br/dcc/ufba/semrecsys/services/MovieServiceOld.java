package br.dcc.ufba.semrecsys.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.dcc.ufba.semrecsys.models.Movie;

public class MovieServiceOld 
{
	
	public static final String DB_SERVICE_URL = "http://dbpedia.org/sparql";
	public static final String BASE_DBP_QUERY = "PREFIX dbr: <http://dbpedia.org/resource/>\r\n" + 
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" + 
			"PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n" + 
			"PREFIX dct: <http://purl.org/dc/terms/>\r\n" + 
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n" + 
			"\r\n" + 
			"SELECT ?property ?value\r\n" + 
			"WHERE {\r\n" + 
			"	<http://dbpedia.org/resource/%s> ?property ?value.\r\n" + 
			"	FILTER(?property=rdf:type || ?property=dct:subject)\r\n" + 
			"}";
	private static final Logger LOGGER = LogManager.getLogger(MovieServiceOld.class);
	
	public List<Movie> getMovies()
	{
		List<Movie> movies = new ArrayList<Movie>();
		
		//DUMP dos filmes do meu perfil do Facebook
		
		Movie m1 = new Movie();
		m1.setTitle("The Girl with the Dragon Tattoo");
		m1.setDescription("This English-language adaptation of the Swedish novel by Stieg Larsson follows a disgraced journalist, Mikael Blomkvist (Daniel Craig), as he investigates the disappearance of a wealthy patriarch's niece from 40 years ago. He is aided by the pierced, tattooed, punk computer hacker named Lisbeth Salander (Rooney Mara). As they work together in the investigation, Blomkvist and Salander uncover immense corruption beyond anything they have ever imagined.");
		
		Movie m2 = new Movie();
		m2.setTitle("The Matrix");
		m2.setDescription("Thomas A. Anderson is a man living two lives. By day he is an average computer programmer and by night a hacker known as Neo. Neo has always questioned his reality, but the truth is far beyond his imagination. Neo finds himself targeted by the police when he is contacted by Morpheus, a legendary computer hacker branded a terrorist by the government. Morpheus awakens Neo to the real world, a ravaged wasteland where most of humanity have been captured by a race of machines that live off of the humans' body heat and electrochemical energy and who imprison their minds within an artificial reality known as the Matrix. As a rebel against the machines, Neo must return to the Matrix and confront the agents: super-powerful computer programs devoted to snuffing out Neo and the entire human rebellion.");
		
		Movie m3 = new Movie();
		m3.setTitle("The Adjustment Bureau");
		m3.setDescription("Do we control our destiny, or do unseen forces manipulate us? A man glimpses the future Fate has planned for him and realizes he wants something else. To get it, he must pursue across, under and through the streets of modern-day New York the only woman he's ever loved. On the brink of winning a seat in the U.S. Senate, ambitious politician David Norris (Matt Damon) meets beautiful contemporary ballet dancer Elise Sellas (Emily Blunt), a woman like none he's ever known. But just as he realizes he's falling for her, mysterious men conspire to keep the two apart. David learns he is up against the agents of Fate itself, the men of The Adjustment Bureau, who will do everything in their considerable power to prevent David and Elise from being together. In the face of overwhelming odds, he must either let her go and accept a predetermined path... or risk everything to defy Fate and be with her.");
		
		movies.addAll(Arrays.asList(m1, m2, m3));
		return movies;
	}
	
	public Properties getResourceProperties(Movie movie)
	{
		Properties props = new Properties();
		for (String token : movie.getTokensList()) 
		{
			System.out.println(String.format(BASE_DBP_QUERY, token));
			Query query = QueryFactory.create(String.format(BASE_DBP_QUERY, token));
			try {
				QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", DB_SERVICE_URL);
				((QueryEngineHTTP) qexec).addParam("timeout", "6000");
				
	            ResultSet rs = qexec.execSelect();
	            ResultSetFormatter.out(System.out, rs, query);
	            break;
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		return props;
	}
	
}
