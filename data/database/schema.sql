-- phpMyAdmin SQL Dump
-- version 4.9.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 17, 2019 at 06:42 AM
-- Server version: 10.4.8-MariaDB
-- PHP Version: 7.3.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `the_movie_finder`
--
CREATE DATABASE IF NOT EXISTS `the_movie_finder` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin;
USE `the_movie_finder`;

-- --------------------------------------------------------

--
-- Table structure for table `idf`
--

CREATE TABLE `idf` (
  `term` varchar(255) COLLATE utf8_bin NOT NULL,
  `value` float NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `lod_cache`
--

CREATE TABLE `lod_cache` (
  `resource` varchar(255) COLLATE utf8_bin NOT NULL,
  `direct_links` int(11) NOT NULL DEFAULT 0,
  `indirect_links` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `lod_cache_relation`
--

CREATE TABLE `lod_cache_relation` (
  `resource1` varchar(255) COLLATE utf8_bin NOT NULL,
  `resource2` varchar(255) COLLATE utf8_bin NOT NULL,
  `direct_links` int(11) NOT NULL DEFAULT 0,
  `indirect_links` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `movie`
--

CREATE TABLE `movie` (
  `id` int(10) UNSIGNED NOT NULL,
  `title` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `description` varchar(5000) COLLATE utf8_bin DEFAULT NULL,
  `tokens` varchar(5000) COLLATE utf8_bin DEFAULT NULL,
  `imdb_rating` float DEFAULT NULL,
  `imdb_id` int(11) DEFAULT NULL,
  `tmdb_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `rating`
--

CREATE TABLE `rating` (
  `user_id` int(10) UNSIGNED NOT NULL,
  `movie_id` int(10) UNSIGNED NOT NULL,
  `rating` float NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `recommendation`
--

CREATE TABLE `recommendation` (
  `user_id` int(10) UNSIGNED NOT NULL,
  `movie_id` int(10) UNSIGNED NOT NULL,
  `similarity` enum('RLWS_DIRECT','RLWS_INDIRECT','COSINE') COLLATE utf8_bin NOT NULL,
  `score` float NOT NULL,
  `rate` float DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id` int(10) UNSIGNED NOT NULL,
  `name` varchar(144) COLLATE utf8_bin NOT NULL,
  `email` varchar(255) COLLATE utf8_bin NOT NULL,
  `password` varchar(512) COLLATE utf8_bin DEFAULT NULL,
  `profile_picture` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `facebook_id` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `online` tinyint(1) NOT NULL DEFAULT 0,
  `active` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `updated_at` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Triggers `user`
--
DELIMITER $$
CREATE TRIGGER `tr_user_after_update` BEFORE UPDATE ON `user` FOR EACH ROW SET NEW.updated_at = NOW()
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Stand-in structure for view `vw_lod_coverage`
-- (See below for the actual view)
--
CREATE TABLE `vw_lod_coverage` (
`dbpedia_hit_coverage` decimal(24,4)
,`lod_direct_coverage` decimal(24,4)
,`lod_indirect_coverage` decimal(24,4)
,`movie_lod_direct_coverage` decimal(24,4)
,`movie_lod_indirect_coverage` decimal(24,4)
);

-- --------------------------------------------------------

--
-- Stand-in structure for view `vw_rec_relevance_by_imdb_rating`
-- (See below for the actual view)
--
CREATE TABLE `vw_rec_relevance_by_imdb_rating` (
);

-- --------------------------------------------------------

--
-- Stand-in structure for view `vw_rec_relevance_by_rating`
-- (See below for the actual view)
--
CREATE TABLE `vw_rec_relevance_by_rating` (
);

-- --------------------------------------------------------

--
-- Structure for view `vw_lod_coverage`
--
DROP TABLE IF EXISTS `vw_lod_coverage`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_lod_coverage`  AS  select (select count(`lod_cache`.`resource`) / (select count(`lod_cache`.`resource`) from `lod_cache`) from `lod_cache` where `lod_cache`.`direct_links` <> 0) AS `dbpedia_hit_coverage`,(select count(`lod`.`rs`) / (select count(`lod_cache`.`resource`) from `lod_cache` where `lod_cache`.`direct_links` <> 0) from (select `lod`.`resource1` AS `rs` from `lod_cache_relation` `lod` where `lod`.`direct_links` <> 0 union select `lod`.`resource2` AS `rs` from `lod_cache_relation` `lod` where `lod`.`direct_links` <> 0) `lod`) AS `lod_direct_coverage`,(select count(`lod`.`rs`) / (select count(`lod_cache`.`resource`) from `lod_cache` where `lod_cache`.`direct_links` <> 0) from (select `lod`.`resource1` AS `rs` from `lod_cache_relation` `lod` where `lod`.`indirect_links` <> 0 union select `lod`.`resource2` AS `rs` from `lod_cache_relation` `lod` where `lod`.`indirect_links` <> 0) `lod`) AS `lod_indirect_coverage`,(select count(`movie`.`id`) / (select count(`movie`.`id`) from `movie`) from `movie` where exists(select `lod`.`rs` from (select `lod`.`resource1` AS `rs` from `lod_cache_relation` `lod` where `lod`.`direct_links` <> 0 union select `lod`.`resource2` AS `rs` from `lod_cache_relation` `lod` where `lod`.`direct_links` <> 0) `lod` where `movie`.`tokens` like concat('%',`lod`.`rs`,'%') limit 1)) AS `movie_lod_direct_coverage`,(select count(`movie`.`id`) / (select count(`movie`.`id`) from `movie`) from `movie` where exists(select `lod`.`rs` from (select `lod`.`resource1` AS `rs` from `lod_cache_relation` `lod` where `lod`.`indirect_links` <> 0 union select `lod`.`resource2` AS `rs` from `lod_cache_relation` `lod` where `lod`.`indirect_links` <> 0) `lod` where `movie`.`tokens` like concat('%',`lod`.`rs`,'%') limit 1)) AS `movie_lod_indirect_coverage` ;

-- --------------------------------------------------------

--
-- Structure for view `vw_rec_relevance_by_imdb_rating`
--
DROP TABLE IF EXISTS `vw_rec_relevance_by_imdb_rating`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_rec_relevance_by_imdb_rating`  AS  select row_number() over ( partition by `r`.`user_id`,`r`.`similarity` order by `r`.`user_id`,`r`.`similarity`,`r`.`score` desc) AS `rn`,`m`.`title` AS `title`,`r`.`user_id` AS `user_id`,`r`.`similarity` AS `similarity`,`r`.`score` AS `score`,if(`r`.`rate` is not null,if(`r`.`rate` >= 3.5,1,0),if(`m`.`imdb_rating` >= 6.5,1,0)) AS `relevance` from ((`recomendation` `r` join `movie` `m` on(`r`.`movie_id` = `m`.`id`)) join `user` `u` on(`r`.`user_id` = `u`.`id`)) ;

-- --------------------------------------------------------

--
-- Structure for view `vw_rec_relevance_by_rating`
--
DROP TABLE IF EXISTS `vw_rec_relevance_by_rating`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_rec_relevance_by_rating`  AS  select row_number() over ( partition by `r`.`user_id`,`r`.`similarity` order by `r`.`user_id`,`r`.`similarity`,`r`.`score` desc) AS `rn`,`m`.`title` AS `title`,`r`.`user_id` AS `user_id`,`u`.`online` AS `online`,`r`.`similarity` AS `similarity`,`r`.`score` AS `score`,if(`r`.`rate` is not null,if(`r`.`rate` >= 3.5,1,0),if((select avg(`ra`.`rating`) from `rating` `ra` where `ra`.`movie_id` = `r`.`movie_id` and `ra`.`user_id` <> `r`.`user_id` group by `ra`.`movie_id`) >= 3.5,1,0)) AS `relevance` from ((`recomendation` `r` join `movie` `m` on(`r`.`movie_id` = `m`.`id`)) join `user` `u` on(`r`.`user_id` = `u`.`id`)) ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `idf`
--
ALTER TABLE `idf`
  ADD PRIMARY KEY (`term`);

--
-- Indexes for table `lod_cache`
--
ALTER TABLE `lod_cache`
  ADD PRIMARY KEY (`resource`),
  ADD UNIQUE KEY `ux_lod_cache` (`resource`);

--
-- Indexes for table `lod_cache_relation`
--
ALTER TABLE `lod_cache_relation`
  ADD PRIMARY KEY (`resource1`,`resource2`);

--
-- Indexes for table `movie`
--
ALTER TABLE `movie`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `ux_tmdb_id` (`tmdb_id`) USING BTREE;

--
-- Indexes for table `rating`
--
ALTER TABLE `rating`
  ADD PRIMARY KEY (`user_id`,`movie_id`),
  ADD KEY `ix_user_id` (`user_id`) USING BTREE,
  ADD KEY `ix_movie_id` (`movie_id`) USING BTREE,
  ADD KEY `fk_rating_user_id` (`user_id`) USING BTREE,
  ADD KEY `fk_rating_movie_id` (`movie_id`) USING BTREE;

--
-- Indexes for table `recommendation`
--
ALTER TABLE `recommendation`
  ADD PRIMARY KEY (`user_id`,`movie_id`,`similarity`),
  ADD KEY `fk_extended_cs_recommendation_user_id` (`user_id`) USING BTREE,
  ADD KEY `fk_extended_cs_recommendation_movie_id` (`movie_id`) USING BTREE;

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `ux_user_email` (`email`) USING BTREE;

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `movie`
--
ALTER TABLE `movie`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `rating`
--
ALTER TABLE `rating`
  ADD CONSTRAINT `fk_user_movie_movie_id` FOREIGN KEY (`movie_id`) REFERENCES `movie` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_user_movie_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `recommendation`
--
ALTER TABLE `recommendation`
  ADD CONSTRAINT `fk_extended_cs_recomendation_movie_id` FOREIGN KEY (`movie_id`) REFERENCES `movie` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_extended_cs_recomendation_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
