<?php 

    error_reporting(E_ALL); 
    ini_set('display_errors',1); 

    include('dbcon.php');
        

    $stmt = $con->prepare('select * from Message_List');
    $stmt->execute();

    if ($stmt->rowCount() > 0)
    {
        $data = array(); 

        while($row=$stmt->fetch(PDO::FETCH_ASSOC))
        {
            extract($row);
    
            array_push($data, 
                array('NUM'=>$NUM,
                'SUBNUM'=>$SUBNUM,
                'M_DATE'=>$M_DATE,
                'M_TIME'=>$M_TIME,
                'REGION'=>$REGION,
                'CONTENT'=>$CONTENT,
                'EVENT'=>$EVENT
            ));
        }

        header('Content-Type: application/json; charset=utf8');
        $json = json_encode(array("spark"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
        echo $json;
    }

?>