<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 

include('dbcon.php');

$region=isset($_GET['region']) ? $_GET['region'] : '';
#$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

$splitRegion = explode("','", $region);
$cnt = count($splitRegion);
$i = 0;
while($i < $cnt) {
    $spt = explode(" ", $splitRegion[$i]);
    if($spt[1] != "전체") {
        array_push($splitRegion, $spt[0]." 전체");
        $i = $i + 1;
    }
    else {
        array_push($splitRegion, "^".$spt[0]);
        array_splice($splitRegion, $i, 1);
        $cnt = $cnt - 1;
    }
}
$mergeRegion = implode("|", $splitRegion);
$region = $mergeRegion;

$sql="select * from Message_List where (m_date between subdate(curdate(),6) and curdate()) and (region REGEXP '$region') order by num desc, subnum asc";
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



?>