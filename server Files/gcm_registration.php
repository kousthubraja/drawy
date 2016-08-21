<?php
	require_once("connect.php");
	
	$GCMId = $_POST['GCMId'];
	$Phone = $_POST['Phone'];
	
	$sql = "INSERT INTO `Drawy`.`Users` (`UID` ,`GCMId` ,`Phone`) VALUES (NULL , '$GCMId', '$Phone');";
	
	if ($conn->query($sql) === TRUE) {
		$UID = $conn -> insert_id;
		echo $UID;
	} else {
		echo "INSERT_FAIL";
	}
	
?>