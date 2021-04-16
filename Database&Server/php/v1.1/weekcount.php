<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 

include('dbcon.php');


#$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

$sql="select m1.region as region, count(*) as count from (select region from Message_List where m_date between subdate(curdate(),6) and curdate() group by region) as m1,
(select region from Message_List where m_date between subdate(curdate(),6) and curdate()) as m2
where case when substring_index(m1.region, ' ', -1) != '전체' then m2.region in (concat(substring_index(m1.region, ' ', 1), ' 전체'), m1.region)
when substring_index(m1.region, ' ', -1)='전체' then m2.region like concat(substring_index(m1.region, ' ', 1),'%') END group by m1.region order by m1.region";
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
