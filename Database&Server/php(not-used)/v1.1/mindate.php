<?php 

error_reporting(E_ALL); 
ini_set('display_errors',1); 

include('dbcon.php');
    

$stmt = $con->prepare('select min(m_date) as mindate from Message_List');
$stmt->execute();

if ($stmt->rowCount() > 0)
{

    while($row=$stmt->fetch(PDO::FETCH_ASSOC))
    {
        extract($row);
    }

    header('Content-Type: application/json; charset=utf8');
    $json = json_encode(array('mindate'=>$mindate), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
    echo $json;
}

?>