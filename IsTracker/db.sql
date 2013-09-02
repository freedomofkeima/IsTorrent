--
-- Database: `IsTracker`
-- @freedomofkeima - Iskandar Setiadi ( iskandarsetiadi@students.itb.ac.id )
-- July 2013
--
CREATE DATABASE `IsTracker` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `IsTracker`;

-- --------------------------------------------------------

--
-- Table structure for table `peer_info`
--

DROP TABLE IF EXISTS peer_info;
CREATE TABLE `peer_info` (  
    `info_hash` VARCHAR(40) NOT NULL, 
    `ip` INT(11) NOT NULL, 
    `port` INT(5) NOT NULL,
     PRIMARY KEY  (`info_hash`,`ip`,`port`)
);
