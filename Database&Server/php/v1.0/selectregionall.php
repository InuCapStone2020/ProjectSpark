<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 

include('dbcon.php');



//POST 값을 읽어온다.
$REGION=isset($_POST['REGION']) ? $_POST['REGION'] : '';
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


if ($REGION != "" ){ 

    $sql="select * from Message_List where REGION in('$REGION')";
    $stmt = $con->prepare($sql);
    $stmt->execute();
 
    if ($stmt->rowCount() == 0){

        echo "'";
        echo $REGION;
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


        if (!$android) {
            echo "<pre>"; 
            print_r($data); 
            echo '</pre>';
        }else
        {
            header('Content-Type: application/json; charset=utf8');
            $json = json_encode(array("spark"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
            echo $json;
        }
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
   
      <form action="<?php $_PHP_SELF ?>" method="POST">
         지역 이름: <input type = "text" name = "REGION" />
         <input type = "submit" />
      </form>
   
   </body>
</html>
<?php
}

   
?>