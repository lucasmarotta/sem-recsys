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

--####################################### V1

--TOTAL NUMBER OF DIRECT INCOMING/OUTGOING RESOURCE LINKS

--TOTAL NUMBER OF DIRECT LINKS
SELECT (count (distinct ?r2) as ?x)
WHERE {
	{values (?r1) {(<http://dbpedia.org/resource/Car>)} ?r1 ?p1 ?r2 . FILTER (?r1 != ?r2) . FILTER (!isLiteral(?r2) )}
	UNION
	{values (?r1) {(<http://dbpedia.org/resource/Car>)} ?r2 ?p1 ?r1 . FILTER (?r1 != ?r2) . FILTER (!isLiteral(?r2) )}
}

--TOTAL NUMBER OF DIRECT LINKS BETWEEN TWO RESOURCES
SELECT (count(distinct ?p1) as ?x)
WHERE {
	{values (?r1 ?r2) {(<http://dbpedia.org/resource/France> <http://dbpedia.org/resource/Paris>)} ?r1 ?p1 ?r2 . FILTER (?r1 != ?r2) . FILTER (!isLiteral(?r2)) } UNION
	{values (?r1 ?r2) {(<http://dbpedia.org/resource/Paris> <http://dbpedia.org/resource/France>)} ?r1 ?p1 ?r2 . FILTER (?r1 != ?r2) . FILTER (!isLiteral(?r2)) }
}

--TOTAL NUMBER OF INDIRECT OUTGOING LINKS
SELECT (count (distinct ?r3) as ?x)
WHERE {
	{values (?r1) {(<http://dbpedia.org/resource/Paris>)} ?r2 ?p1 ?r1 . ?r2 ?p1 ?r3 . FILTER (?r1 != ?r3) . FILTER (!isLiteral(?r2) )}
}

--TOTAL NUMBER OF INDIRECT OUTGOING LINKS
SELECT (count (distinct ?r3) as ?x)
WHERE {
	{values (?r1 ?r3) {(<http://dbpedia.org/resource/Paris> <http://dbpedia.org/resource/France>)} ?r2 ?p1 ?r1 . ?r2 ?p1 ?r3 . FILTER (?r1 != ?r3) . FILTER (!isLiteral(?r2)) } UNION
}

--####################################### V2

-- COMMON PREFIXES

PREFIX  :     <http://dbpedia.org/resource/>
PREFIX  d:    <http://learningsparql.com/ns/data#>
PREFIX  owl:  <http://www.w3.org/2002/07/owl#>
PREFIX  e:    <http://learningsparql.com/ns/expenses#>
PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>
PREFIX  skos: <http://www.w3.org/2004/02/skos/core#>
PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX  dbpedia: <http://dbpedia.org/>
PREFIX  dbo:  <http://dbpedia.org/ontology/>
PREFIX  geo:  <http://www.w3.org/2003/01/geo/wgs84_pos#>
PREFIX  dct:  <http://www.w3.org/2000/01/rdf-schema#>
PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX  dbpedia2: <http://dbpedia.org/property/>
PREFIX  foaf: <http://xmlns.com/foaf/0.1/>
PREFIX  bif:  <bif:>
PREFIX  dc:   <http://purl.org/dc/elements/1.1/>

--TOTAL NUMBER OF DIRECT INCOMING/OUTGOING RESOURCE LINKS

PREFIX  :  <http://dbpedia.org/resource/>
PREFIX  dbo:  <http://dbpedia.org/ontology/>
SELECT (count (distinct ?p1) as ?x)
WHERE {
	{values (?r1) {(<http://dbpedia.org/resource/Paris>)} ?r1 ?p1 ?r2 . FILTER (?r1 != ?r2)}
	UNION
	{values (?r1) {(<http://dbpedia.org/resource/Paris>)} ?r2 ?p1 ?r1 . FILTER (?r1 != ?r2)}
	FILTER ( ?p1 != dbo:wikiPageID )
	FILTER ( ?p1 != dbo:wikiPageRevisionID )
	FILTER ( ?p1 != dbo:wikiPageRedirects )
	FILTER ( ?p1 != dbo:wikiPageExternalLink )
	FILTER ( ! isLiteral(?r2) )
}

--TOTAL NUMBER OF DIRECT INCOMING/OUTGOING LINKS BETWEEN RESOURCES

PREFIX  :  <http://dbpedia.org/resource/>
PREFIX  dbo:  <http://dbpedia.org/ontology/>
SELECT (count(distinct ?p1) as ?x)
WHERE {
	{values (?r1 ?r2) {(<http://dbpedia.org/resource/Paris> <http://dbpedia.org/resource/France>)} ?r1 ?p1 ?r2 . FILTER (?r1 != ?r2) }
	UNION
	{values (?r1 ?r2) {(<http://dbpedia.org/resource/France> <http://dbpedia.org/resource/Paris>)} ?r1 ?p1 ?r2 . FILTER (?r1 != ?r2) }
	FILTER ( ?p1 != dbo:wikiPageID )
	FILTER ( ?p1 != dbo:wikiPageRevisionID )
	FILTER ( ?p1 != dbo:wikiPageRedirects )
	FILTER ( ?p1 != dbo:wikiPageExternalLink )
	FILTER ( ! isLiteral(?r2) )
}

--TOTAL NUMBER OF INDIRECT INCOMING RESOURCES

PREFIX  :  <http://dbpedia.org/resource/>
PREFIX  dbo:  <http://dbpedia.org/ontology/>
SELECT (count (distinct ?r2) as ?x)
WHERE {
	{values (?r1) {(<http://dbpedia.org/resource/Paris>)} ?r2 ?p1 ?r1 . ?r2 ?p2 ?r3 . FILTER (?r1 != ?r3 && ?r2 != ?r1 && ?r2 != ?r3)}
	FILTER ( ?p1 != dbo:wikiPageID )
	FILTER ( ?p1 != dbo:wikiPageRevisionID )
	FILTER ( ?p1 != dbo:wikiPageRedirects )
	FILTER ( ?p1 != dbo:wikiPageExternalLink )
	FILTER ( ! isLiteral(?r2) )
	FILTER ( ?p2 != dbo:wikiPageID )
	FILTER ( ?p2 != dbo:wikiPageRevisionID )
	FILTER ( ?p2 != dbo:wikiPageRedirects )
	FILTER ( ?p2 != dbo:wikiPageExternalLink )
}

--TOTAL NUMBER OF INDIRECT INCOMING/OUTGOING LINKS BETWEEN RESOURCES

PREFIX  :  <http://dbpedia.org/resource/>
PREFIX  dbo:  <http://dbpedia.org/ontology/>
SELECT (count (distinct ?r2) as ?x)
WHERE {
	{values (?r1 ?r3) {(<http://dbpedia.org/resource/Amorality> <http://dbpedia.org/resource/James_Bond>)} ?r2 ?p1 ?r1 . ?r2 ?p2 ?r3 . FILTER (?r1 != ?r3 && ?r2 != ?r1 && ?r2 != ?r3)}
	FILTER ( ?p1 != dbo:wikiPageID )
	FILTER ( ?p1 != dbo:wikiPageRevisionID )
	FILTER ( ?p1 != dbo:wikiPageRedirects )
	FILTER ( ?p1 != dbo:wikiPageExternalLink )
	FILTER ( ! isLiteral(?r2) )
	FILTER ( ?p2 != dbo:wikiPageID )
	FILTER ( ?p2 != dbo:wikiPageRevisionID )
	FILTER ( ?p2 != dbo:wikiPageRedirects )
	FILTER ( ?p2 != dbo:wikiPageExternalLink )
}

--IS REDIRECT

PREFIX  dbo:  <http://dbpedia.org/ontology/>
PREFIX  :     <http://dbpedia.org/resource/>
SELECT (COUNT(DISTINCT ?r2) AS ?x)
WHERE {
	{VALUES ( ?r1 ?r2 ) {( :Ancient :Andy )} ?r1  ?p1  ?r2 FILTER ( ?r1 != ?r2 )}
	UNION
	{VALUES ( ?r1 ?r2 ) {( :Andy :Ancient )} ?r1  ?p1  ?r2 FILTER ( ?r1 != ?r2 )}
	FILTER ( ?p1 = dbo:wikiPageRedirects )
}


PREFIX  :  <http://dbpedia.org/resource/>
PREFIX  dbo:  <http://dbpedia.org/ontology/>

SELECT ?x, ?y WHERE {
	{
		SELECT (count (distinct ?r2) as ?x)
		WHERE {
			{values (?r1) {(<http://dbpedia.org/resource/Paris>)} ?r2 ?p1 ?r1 . ?r2 ?p2 ?r3 . FILTER (?r1 != ?r3 && ?r2 != ?r1 && ?r2 != ?r3)}
			FILTER ( ?p1 != dbo:wikiPageID )
			FILTER ( ?p1 != dbo:wikiPageRevisionID )
			FILTER ( ?p1 != dbo:wikiPageRedirects )
			FILTER ( ?p1 != dbo:wikiPageExternalLink )
			FILTER ( ! isLiteral(?r2) )
			FILTER ( ?p2 != dbo:wikiPageID )
			FILTER ( ?p2 != dbo:wikiPageRevisionID )
			FILTER ( ?p2 != dbo:wikiPageRedirects )
			FILTER ( ?p2 != dbo:wikiPageExternalLink )
		}
	}

	{
		SELECT (count (distinct ?r2) as ?y)
		WHERE {
			{values (?r1) {(<http://dbpedia.org/resource/France>)} ?r2 ?p1 ?r1 . ?r2 ?p2 ?r3 . FILTER (?r1 != ?r3 && ?r2 != ?r1 && ?r2 != ?r3)}
			FILTER ( ?p1 != dbo:wikiPageID )
			FILTER ( ?p1 != dbo:wikiPageRevisionID )
			FILTER ( ?p1 != dbo:wikiPageRedirects )
			FILTER ( ?p1 != dbo:wikiPageExternalLink )
			FILTER ( ! isLiteral(?r2) )
			FILTER ( ?p2 != dbo:wikiPageID )
			FILTER ( ?p2 != dbo:wikiPageRevisionID )
			FILTER ( ?p2 != dbo:wikiPageRedirects )
			FILTER ( ?p2 != dbo:wikiPageExternalLink )
		}
	}
}
