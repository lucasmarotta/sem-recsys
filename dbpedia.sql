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
