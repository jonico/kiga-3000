# Host: localhost
# Database: Kindergarten
# Table: 'Karteikarte'
# 
USE Kindergarten;

CREATE TABLE `Karteikarte` (
  `id` int(11) NOT NULL auto_increment,
  `gruppe` tinyint(3) unsigned NOT NULL default '0',
  `lastaccess` date NOT NULL default '0000-00-00',
  `kindgeburt` date NOT NULL default '0000-00-00',
  `eintritt` date NOT NULL default '0000-00-00',
  `eintrittsgrund` varchar(50) NOT NULL default '',
  `austritt` date NOT NULL default '0000-00-00',
  `austrittsgrund` varchar(50) NOT NULL default '',
  `kindnachname` varchar(50) NOT NULL default '',
  `kindvorname` varchar(50) NOT NULL default '',
  `geburtsort` varchar(100) NOT NULL default '',
  `kindwohnung` varchar(100) NOT NULL default '',
  `religion` varchar(25) NOT NULL default '',
  `staat` varchar(40) NOT NULL default '',
  `kindtelefon` varchar(30) NOT NULL default '',
  `vatername` varchar(70) NOT NULL default '',
  `vatergeburt` date NOT NULL default '0000-00-00',
  `vaterberuf` varchar(50) NOT NULL default '',
  `muttername` varchar(70) NOT NULL default '',
  `muttergeburt` date NOT NULL default '0000-00-00',
  `mutterberuf` varchar(70) NOT NULL default '',
  `sorgeperson` varchar(100) NOT NULL default '',
  `arbeitsort1` varchar(50) NOT NULL default '',
  `arbeittel1` varchar(30) NOT NULL default '',
  `arbeitsort2` varchar(50) NOT NULL default '',
  `arbeittel2` varchar(30) NOT NULL default '',
  `geschwgeburt` varchar(50) NOT NULL default '',
  `famstand` tinyint(3) unsigned NOT NULL default '0',
  `elternort` varchar(100) NOT NULL default '',
  `elterntel` varchar(30) NOT NULL default '',
  `anzahlgeschw` tinyint(3) unsigned NOT NULL default '0',
  `impfung` varchar(100) NOT NULL default '',
  `tetanuszeit` varchar(50) NOT NULL default '',
  `krankheiten` smallint(6) NOT NULL default '0',
  `wkrankheit` varchar(50) NOT NULL default '',
  `gesundheit` varchar(255) NOT NULL default '',
  `arztort` varchar(50) NOT NULL default '',
  `arzttel` varchar(30) NOT NULL default '',
  `krankenkasse` varchar(30) NOT NULL default '',
  `sonstiges` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`id`),
  KEY `gruppe` (`gruppe`,`lastaccess`,`kindgeburt`,`kindnachname`)
) TYPE=MyISAM COMMENT='Daten der Kinder'; 

