<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 

include('dbcon.php');



//GET 값을 읽어온다.
$region=isset($_GET['region']) ? $_GET['region'] : '';
$s_date=isset($_GET['s_date']) ? $_GET['s_date'] : '';
$e_date=isset($_GET['e_date']) ? $_GET['e_date'] : '';
$event=isset($_GET['event']) ? $_GET['event'] : '';
$page=isset($_GET['page']) ? $_GET['page'] : '';
$offset = ($page - 1) * 10;
#$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if (($region != "") && ($s_date != "") && ($e_date != "") && ($event != "") && ($page != "")){ 

    if($page == 1) {
        $sql="select count(*) as count from Message_List where (region in('$region')) and (m_date between '$s_date' and '$e_date') and (event in('$event'))";
        $stmt = $con->prepare($sql);
        $stmt->execute();

       

    
        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){
    
            extract($row);
            
            $count = $row["count"];

            echo $count;
            echo "건의 검색 결과가 있습니다.\n";

        }
    }

    $sql="select * from Message_List where (region in('$region')) and (m_date between '$s_date' and '$e_date') and (event in('$event')) limit $offset, 10";
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

      <form action="<?php $_PHP_SELF ?>" method="get">
         시작 DATE: <input type = "date" name = "s_date" />
         <input type = "submit" />
      </form>

      <form action="<?php $_PHP_SELF ?>" method="get">
         종료 DATE: <input type = "date" name = "e_date" />
         <input type = "submit" />
      </form>

      <form action="<?php $_PHP_SELF ?>" method="get">
         사건: <input type = "text" name = "evnet" />
         <input type = "submit" />
      </form>

      <form action="<?php $_PHP_SELF ?>" method="get">
         page: <input type = "text" name = "page" />
         <input type = "submit" />
      </form>
   
   </body>
</html>
<?php
}

   
?>