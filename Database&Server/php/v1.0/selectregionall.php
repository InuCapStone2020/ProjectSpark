<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 

include('dbcon.php');



//GET 값을 읽어온다.
$region=isset($_GET['region']) ? $_GET['region'] : '';
#$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if ($region != "" ){ 

    $sql="select * from Message_List where region in('$region')";
    $stmt = $con->prepare($sql);
    $stmt->execute();
 
    if ($stmt->rowCount() == 0){

        echo "'";
        echo $region;
        echo "'은 찾을 수 없습니다.";
    }
	else{

   		$data = array(); 

        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){

        	extract($row);

            array_push($data, 
                array('NUM'=>$row["NUM"],
                'SUBNUM'=>$row["SUBNUM"],
                'M_DATEW'=>$row["M_DATE"],
                'M_TIME'=>$row["M_TIME"],
                'REGION'=>$row["REGION"],
                'CONTENT'=>$row["CONTENT"],
                'EVENT'=>$row["EVENT"]
            ));
        }


        header('Content-Type: application/json; charset=utf8');
        $json = json_encode(array("spark"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
        echo $json;
        
    }
}
else {
    echo "선택 지역을 추가하세요 ";
}

?>



<?php

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if (!$android){
?>

<html>
   <body>
   
      <form action="<?php $_PHP_SELF ?>" method="get">
         지역 이름: <input type = "text" name = "region" />
         <input type = "submit" />
      </form>
   
   </body>
</html>
<?php
}

   
?>