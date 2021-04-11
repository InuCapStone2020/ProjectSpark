<?php  
error_reporting(E_ALL); 
ini_set('display_errors',1); 

include('dbcon.php');



//GET 값을 읽어온다.
$region=isset($_GET['region']) ? $_GET['region'] : '';
$s_date=isset($_GET['s_date']) ? $_GET['s_date'] : '';
if($s_date == "") {
    $s_date = '2000-01-01';
}
$e_date=isset($_GET['e_date']) ? $_GET['e_date'] : '';
if($e_date == "") {
    $e_date = '9999-12-31';
}
$event=isset($_GET['event']) ? $_GET['event'] : '';
$page=isset($_GET['page']) ? $_GET['page'] : '';
$offset = ($page - 1) * 10;
#$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


if (($region != "") && ($event != "") && ($page != "")){ 

    if($region != "전체 전체") {
        #region case
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
        

        #page == 1 then count row.
        if($page == 1) {
            $sql="select count(*) as count from Message_List where (region REGEXP '$region') and (m_date between '$s_date' and '$e_date') and (event in('$event'))";
            $stmt = $con->prepare($sql);
            $stmt->execute();

            while($row=$stmt->fetch(PDO::FETCH_ASSOC)){ 
                extract($row);          
            }
            header('Content-Type: application/json; charset=utf8');
            $json = json_encode(array('count'=>$count), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
            echo $json;
            echo "\n";
        }

        $sql="select * from Message_List where (region REGEXP '$region') and (m_date between '$s_date' and '$e_date') and (event in('$event')) limit $offset, 10";
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
        #page == 1 then count row.
        if($page == 1) {
            $sql="select count(*) as count from Message_List where (m_date between '$s_date' and '$e_date') and (event in('$event'))";
            $stmt = $con->prepare($sql);
            $stmt->execute();

            while($row=$stmt->fetch(PDO::FETCH_ASSOC)){ 
                extract($row);          
            }
            header('Content-Type: application/json; charset=utf8');
            $json = json_encode(array('count'=>$count), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
            echo $json;
            echo "\n";
        }

        $sql="select * from Message_List where (m_date between '$s_date' and '$e_date') and (event in('$event')) limit $offset, 10";
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
}
else {
    echo "input condition";
}

?>

