
/* ==========================================================================
    LOD METRICS
============================================================================ */

SELECT *, resource_valid_count / resource_count dbpedia_hit_coverage
FROM
(
	SELECT COUNT(`resource`) resource_valid_count, (SELECT COUNT(`resource`) FROM lod_cache) resource_count
	FROM lod_cache
	WHERE direct_links != 0
) lod


--vw_lod_coverage
SELECT
(
    SELECT COUNT(`resource`) / (SELECT COUNT(`resource`) FROM lod_cache)
    FROM lod_cache
    WHERE direct_links != 0
) dbpedia_hit_coverage,
(
    SELECT COUNT(rs) / (SELECT COUNT(`resource`) FROM lod_cache WHERE direct_links != 0)
    FROM
    (
        SELECT resource1 rs
        FROM lod_cache_relation lod
        WHERE direct_links != 0
        UNION
        SELECT resource2 rs
        FROM lod_cache_relation lod
        WHERE direct_links != 0
    ) lod
) lod_direct_coverage,
(
    SELECT COUNT(rs) / (SELECT COUNT(`resource`) FROM lod_cache WHERE direct_links != 0)
    FROM
    (
        SELECT resource1 rs
        FROM lod_cache_relation lod
        WHERE indirect_links != 0
        UNION
        SELECT resource2 rs
        FROM lod_cache_relation lod
        WHERE indirect_links != 0
    ) lod
) lod_indirect_coverage,
(
    SELECT COUNT(id) / (SELECT COUNT(id) FROM movie)
    FROM movie
    WHERE EXISTS
    (
        SELECT rs
        FROM
        (
            SELECT resource1 rs
            FROM lod_cache_relation lod
            WHERE direct_links != 0
            UNION
            SELECT resource2 rs
            FROM lod_cache_relation lod
            WHERE direct_links != 0
        ) lod
        WHERE movie.tokens LIKE CONCAT('%', lod.rs ,'%')
    )
) movie_lod_direct_coverage,
(
    SELECT COUNT(id) / (SELECT COUNT(id) FROM movie)
    FROM movie
    WHERE EXISTS
    (
        SELECT rs
        FROM
        (
            SELECT resource1 rs
            FROM lod_cache_relation lod
            WHERE indirect_links != 0
            UNION
            SELECT resource2 rs
            FROM lod_cache_relation lod
            WHERE indirect_links != 0
        ) lod
        WHERE movie.tokens LIKE CONCAT('%', lod.rs ,'%')
    )
) movie_lod_indirect_coverage


/* ==========================================================================
    RECOMENDATION ANALYSIS METRICS
============================================================================ */

-- RECOMENDATION RELEVANCE BY RATINGS
SELECT ROW_NUMBER() OVER (PARTITION BY r.user_id, r.similarity ORDER BY r.user_id, r.similarity, r.score DESC) rn,
m.title, r.user_id, u.online, r.similarity, r.score,
IF(
    r.rate IS NOT NULL, IF(r.rate >= 3.5, 1, 0),
    IF((
        SELECT AVG(ra.rating)
        FROM rating ra
        WHERE ra.movie_id = r.movie_id AND ra.user_id != r.user_id
        GROUP BY ra.movie_id
    ) >= 3.5, 1, 0)
) relevance
FROM recomendation r
INNER JOIN movie m ON r.movie_id = m.id
INNER JOIN user u ON r.user_Id = u.id

-- RECOMENDATION RELEVANCE BY IMDB RATINGS
SELECT ROW_NUMBER() OVER (PARTITION BY r.user_id, r.similarity ORDER BY r.user_id, r.similarity, r.score DESC) rn,
m.title, r.user_id, r.similarity, r.score,
IF(r.rate IS NOT NULL, IF(r.rate >= 3.5, 1, 0), IF(m.imdb_rating >= 6.5, 1, 0)) relevance
FROM recomendation r
INNER JOIN movie m ON r.movie_id = m.id
INNER JOIN user u ON r.user_Id = u.id

DELETE
FROM recomendation
WHERE similarity = 'RLWS' AND user_id >= 4;
SELECT * FROM `recomendation` WHERE similarity = 'RLWS';



-- RECOMENDATION METRICS BY RANK - RLWS

SET @SIMILARITY := 'COSINE';
SET @ONLINE := 0;

SELECT a.rn, AVG(a.avg_precision_rank) map, AVG(a.reciprocal_rank) mrr
FROM
(
    SELECT a.*,
    (
        SELECT AVG(av.relevance_rank / rn)  avg_precision_rank
        FROM
        (
            SELECT a.rn, a.user_id,
            (
                SELECT COUNT(rn) qtd
                FROM vw_rec_relevance_by_rating rr
                WHERE rr.relevance = 1 AND rr.user_id = a.user_id AND rr.online = a.online AND rr.similarity = @SIMILARITY AND rr.rn <= a.rn
            ) relevance_rank
            FROM vw_rec_relevance_by_rating a
            WHERE a.similarity = @SIMILARITY AND EXISTS
            (
				SELECT *
				FROM
				(
					SELECT DISTINCT user_id
					FROM vw_rec_relevance_by_rating rr
					WHERE rr.online = @ONLINE AND rr.similarity = @SIMILARITY
					LIMIT 30
				) u
                WHERE a.user_Id = u.user_id
            )
        ) av
        WHERE av.user_id = a.user_id AND av.rn <= a.rn
        GROUP BY av.user_id
    ) avg_precision_rank,
    IFNULL(1 / (
        SELECT MIN(rn)
        FROM  vw_rec_relevance_by_rating rr
        WHERE rr.relevance = 1 AND rr.user_id = a.user_id AND rr.online = a.online AND rr.similarity = @SIMILARITY AND rr.rn <= a.rn
    ), 0) reciprocal_rank
    FROM
    (
        SELECT a.rn, a.title, a.user_id, a.online, a.similarity, a.score, a.relevance,
        a.relevance_rank / rn  precision_rank,
        IF(total_relevance = 0, 0, a.relevance_rank / total_relevance) recall_rank
        FROM
        (
            SELECT a.*,
            (
                SELECT COUNT(rr.rn)
                FROM vw_rec_relevance_by_rating rr
                WHERE rr.relevance = 1 AND rr.user_id = a.user_id AND rr.online = a.online AND rr.similarity = @SIMILARITY AND rr.rn <= a.rn
            ) relevance_rank,
            (
                SELECT COUNT(rr.rn)
                FROM vw_rec_relevance_by_rating rr
                WHERE rr.relevance = 1 AND rr.user_id = a.user_id AND rr.online = a.online AND rr.similarity = @SIMILARITY
            ) total_relevance
            FROM vw_rec_relevance_by_rating a
            WHERE a.similarity = @SIMILARITY AND EXISTS
			(
				SELECT *
				FROM
				(
					SELECT DISTINCT user_id
					FROM vw_rec_relevance_by_rating rr
					WHERE rr.online = @ONLINE AND rr.similarity = @SIMILARITY
					LIMIT 30
				) u
                WHERE a.user_Id = u.user_id
            )
        ) a
    ) a
) a
GROUP BY a.online, rn;
