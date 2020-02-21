-- MySQL dump 10.13  Distrib 5.5.62, for Win64 (AMD64)
--
-- Host: localhost    Database: pe_it
-- ------------------------------------------------------
-- Server version	5.7.28

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `metro_line`
--

DROP TABLE IF EXISTS `metro_line`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `metro_line` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `system_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `station_count` int(11) NOT NULL,
  `track_type` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=304 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `metro_line`
--

LOCK TABLES `metro_line` WRITE;
/*!40000 ALTER TABLE `metro_line` DISABLE KEYS */;
INSERT INTO `metro_line` VALUES (100,10,'M1',21,1),(101,10,'M2',7,1),(200,20,'1',25,3),(201,20,'2',25,1),(202,20,'3',25,1),(203,20,'3bis',4,1),(204,20,'4',27,3),(205,20,'5',22,1),(206,20,'6',28,3),(207,20,'7',38,1),(208,20,'7bis',8,1),(209,20,'8',38,1),(210,20,'9',37,1),(211,20,'10',23,1),(212,20,'11',13,3),(213,20,'12',29,1),(214,20,'13',32,1),(215,20,'14',9,3),(300,30,'1',23,1),(301,30,'2',25,2),(302,30,'3',45,2),(303,30,'6',33,1);
/*!40000 ALTER TABLE `metro_line` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `metro_system`
--

DROP TABLE IF EXISTS `metro_system`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `metro_system` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `city_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `daily_ridership` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `metro_system`
--

LOCK TABLES `metro_system` WRITE;
/*!40000 ALTER TABLE `metro_system` DISABLE KEYS */;
INSERT INTO `metro_system` VALUES (10,1,'Metro Warszawskie',568000),(20,2,'Métro de Paris',4160000),(30,3,'Chongqing Metro',1730000);
/*!40000 ALTER TABLE `metro_system` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `city`
--

DROP TABLE IF EXISTS `city`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `city` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `population` int(11) NOT NULL,
  `area` float NOT NULL,
  `link` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `city`
--

LOCK TABLES `city` WRITE;
/*!40000 ALTER TABLE `city` DISABLE KEYS */;
INSERT INTO `city` VALUES (1,'Warszawa',1748916,517.24,'http://www.um.warszawa.pl/en'),(2,'Paris',2243833,105.4,'http://paris.fr'),(3,'Chongqing',49165500,82403,NULL);
/*!40000 ALTER TABLE `city` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-02-21 18:57:53
