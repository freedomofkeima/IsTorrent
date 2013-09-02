<?php
/*
 * Announce Torrent Class
 * @freedomofkeima - Iskandar Setiadi ( iskandarsetiadi@students.itb.ac.id )
 * July 2013
 *
 */

require_once 'db.php'; /* Database settings */

/* Resolve Host IP Address */
function resolve_ip($host) {
	$ip = ip2long($host);
	if (($ip === false) || ($ip == -1)) { /* If IP Address not detected properly */
		$ip = ip2long(gethostbyname($host));
		if (($ip === false) || ($ip == -1)) {
			return false;
		}
	}
	return $ip;
}

/* Content Header Type */
header('Content-Type: text/plain');

/* Validate Request */
if ((empty($_GET['info_hash'])) || (empty($_GET['port'])) || (!is_numeric($_GET['port']))){
  exit("Invalid Request");
}

/* Resolve IP */
$ip = resolve_ip(empty($_GET['ip']) ? $_SERVER['REMOTE_ADDR'] : $_GET['ip']);
if ($ip === false) {
	exit("unable to resolve host name"); /* Exit in case HostName is not resolved properly */
}

/* Connect to Database */
@mysql_pconnect($db_ip, $db_user, $db_pass) or exit("database unavailable");
@mysql_select_db($db_database) or exit("database unavailable");

$announce_interval = 60; /* Announce interval */

/* Update Database */
$columns = '`info_hash`, `ip`, `port`';
$values = '\'' . mysql_real_escape_string($_GET['info_hash'])  . '\', ' . $ip . ', ' . $_GET['port'];
@mysql_query("INSERT IGNORE INTO `$db_table` ($columns) VALUES ($values);") or exit("Database update error");


/* Retrieve Peers from Database */
$peers = array(); /* Initialize */


/* Fetch peers Data from Database, Select max 50 entries by random */
$query = @mysql_query("SELECT `ip`, `port` FROM `$db_table` WHERE `info_hash` = '" . mysql_real_escape_string($_GET['info_hash']) . "' ORDER BY RAND() LIMIT 50;") or exit("database error");

while ($array = mysql_fetch_assoc($query)) {
	$peers[] = array('ip' => long2ip($array['ip']), 'port' => intval($array['port']));
}

/* Return peers to Client */
echo json_encode($peers); //delete it later
return $peers;
?>
