<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 

include('dbcon.php');


#$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

$sql="select count(NUM) as count, region from Message_List group by region";
$stmt = $con->prepare($sql);
$stmt->execute();
 
if ($stmt->rowCount() == 0){

    echo "찾을 수 없습니다.";
}
else{

   	$data = array(); 

    while($row=$stmt->fetch(PDO::FETCH_ASSOC)){

        extract($row);

        array_push($data, 
            array('count'=>$row["count"],
                'region'=>$row["region"]
        ));
    }


    header('Content-Type: application/json; charset=utf8');
    $json = json_encode(array("spark"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
    echo $json;
        
}



?>
