PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX dbo: <http://dbpedia.org/ontology/>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX foaf: <http://xmlns.com/foaf/0.1/>
PREFIX dct: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX dc: <http://purl.org/dc/elements/1.1/>
PREFIX bif: <bif:>
PREFIX : <http://dbpedia.org/resource/>
PREFIX dbpedia2: <http://dbpedia.org/property/>
PREFIX dbpedia: <http://dbpedia.org/>
PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>
PREFIX e: <http://learningsparql.com/ns/expenses#>
PREFIX d: <http://learningsparql.com/ns/data#>
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

--TOTAL NUMBER OF DIRECT LINKS
SELECT (count (distinct ?r2) as ?x)
WHERE {
	{values (?r1) {(<http://dbpedia.org/resource/Plant>)} ?r1 ?p1 ?r2 . FILTER (?r1 != ?r2) . FILTER (!isLiteral(?r2) )}
	UNION
	{values (?r1) {(<http://dbpedia.org/resource/Plant>)} ?r2 ?p1 ?r1 . FILTER (?r1 != ?r2) . FILTER (!isLiteral(?r2) )}
}

--TOTAL NUMBER OF DIRECT LINKS BETWEEN TWO RESOURCES
SELECT (count(distinct ?p1) as ?x)
WHERE {
	{values (?r1 ?r2) {(<http://dbpedia.org/resource/Plant> <http://dbpedia.org/resource/Coconut>)} ?r1 ?p1 ?r2 . FILTER (?r1 != ?r2) . FILTER (!isLiteral(?r2)) } UNION
	{values (?r1 ?r2) {(<http://dbpedia.org/resource/Coconut> <http://dbpedia.org/resource/Plant>)} ?r1 ?p1 ?r2 . FILTER (?r1 != ?r2) . FILTER (!isLiteral(?r2)) }
}

--TOTAL NUMBER OF INDIRECT OUTGOING LINKS
SELECT (count (distinct ?r3) as ?x)
WHERE {
	{values (?r1) {(<http://dbpedia.org/resource/Plant>)} ?r2 ?p1 ?r1 . ?r2 ?p1 ?r3 . FILTER (?r1 != ?r3) . FILTER (!isLiteral(?r2) )}
}

--TOTAL NUMBER OF INDIRECT OUTGOING LINKS
SELECT (count (distinct ?p1) as ?x)
WHERE {
	{values (?r1 ?r3) {(<http://dbpedia.org/resource/Plant> <http://dbpedia.org/resource/Coconut>)} ?r2 ?p1 ?r1 . ?r2 ?p1 ?r3 . FILTER (?r1 != ?r3) . FILTER (!isLiteral(?r2) )}
}

movie 2276, 3626
