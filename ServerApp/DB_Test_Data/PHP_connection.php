<?php
	//孽府
	$query = "select * from test";
	$conn = mysqli_connect("localhost","root","");
	mysqli_select_db($conn,"test");
	
	//茄臂 柄咙巩力 秦搬规过. 
	/*
	mysql_query("set session character_set_connection=utf8;");
	mysql_query("set session character_set_results=utf8;");
	mysql_query("set session character_set_client=utf8;");
	*/
	mysqli_query($conn,"set names utf8");

	$result = mysqli_query($conn, $query);

	while ($row = mysqli_fetch_array($result, MYSQLI_ASSOC)) {
		$res['id'] = $row["num"];
		$res['content'] = urlencode($row['content']);
		$arr["result"][] = $res;
	}
	
	$json = json_encode ($arr);
	$json = urldecode ($json);
	print $json;
	mysqli_close($conn);
?>