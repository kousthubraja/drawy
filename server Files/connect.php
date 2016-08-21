<?php
	
	define( 'API_ACCESS_KEY', 'AIzaSyBPAeXn9RdKVC0TQHRmwgl2Dl-4AdRLCVI' );
	
	$host = "localhost";
	$user = "drawy";
	$pass = "drawpass";
	$db = "Drawy";

	$conn = new mysqli($host, $user, $pass, $db);
	
	if ($conn->connect_error) {
		die("Connection failed: " . $conn->connect_error);
	}
?>