PREFIX dbr: <http://dbpedia.org/resource/>
PREFIX dbo: <http://dbpedia.org/ontology/>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX dct: <http://purl.org/dc/terms/>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>

SELECT ?value
WHERE {
	{<http://dbpedia.org/resource/Buzz_Lightyear> ?property ?value.}
	FILTER((
		?property = rdf:type ||
		?property = dct:subject ||
		?property = dbo:wikiPageRedirects ||
		?property = dbo:wikiPageDisambiguates
	) && ?value != owl:Thing
	&& !STRSTARTS(STR(?value), "http://www.wikidata.org/entity/")
	&& !STRSTARTS(STR(?value), "http://wikidata.dbpedia.org/resource/"))
}

SELECT (count (distinct ?p1) as ?x)
WHERE {
	values (?r1 ?r2) {( <http://dbpedia.org/resource/Buzz_Lightyear>  <http://dbpedia.org/resource/Animation> )}
	. ?r1 ?p1 ?r2 .
	FILTER (?r1 != ?r2)
}

SELECT (count (distinct ?r3) as ?x)
WHERE {
	values (?r1) { (<http://dbpedia.org/resource/Buzz_Lightyear>)} . ?r2 ?p1 ?r1 . ?r2 ?p2 ?r3 . FILTER ( ! isLiteral(?r2) )
}

SELECT  (COUNT(DISTINCT ?p1) AS ?x)
WHERE{
	{ VALUES ( ?r1 ?r3 ) {
			( <http://dbpedia.org/resource/Buzz_Lightyear>  <http://dbpedia.org/resource/Buzz_Lightyear> )
		}
		?r2  ?p1  ?r1 ;
		?p1  ?r3
		FILTER ( ! isLiteral(?r2) )
	}
}
