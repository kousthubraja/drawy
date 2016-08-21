<?php
	require_once('connect.php');
	
	$message = $_POST['msg'];

	$sql = "SELECT `GCMId` FROM `Users`";

	$result = $conn->query($sql);

	if ($result->num_rows > 0) {
		while($row = $result->fetch_assoc()) {
			$registrationIds [] = $row['GCMId'];
		}
	}
	else{
		die("NO_USERS");
	}

	function sendGCM($registrationIds, $message){
		$msg = array
		(
			'message' 	=> $message,
		);

		$fields = array
		(
			'registration_ids' 	=> $registrationIds,
			'data'			=> $msg,
	//		'time_to_live' => 0
		);
		 
		$headers = array
		(
			'Authorization: key=' . API_ACCESS_KEY,
			'Content-Type: application/json'
		);
		 
		$ch = curl_init();
		curl_setopt( $ch,CURLOPT_URL, 'https://android.googleapis.com/gcm/send' );
		curl_setopt( $ch,CURLOPT_POST, true );
		curl_setopt( $ch,CURLOPT_HTTPHEADER, $headers );
		curl_setopt( $ch,CURLOPT_RETURNTRANSFER, true );
		curl_setopt( $ch,CURLOPT_SSL_VERIFYPEER, false );
		curl_setopt( $ch,CURLOPT_POSTFIELDS, json_encode( $fields ) );
		$result = curl_exec($ch );
		curl_close( $ch );

		echo $result;
		echo json_decode($result, true);
		echo 'Finish';
	}
	
	sendGCM($registrationIds, $message);
	
	$conn->close();

?>			