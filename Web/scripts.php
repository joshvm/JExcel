<?php

$db = new mysqli("localhost", "root", "admin", "jexcel");
if($db -> connect_error){
    echo "-1";
    $db -> close();
    return;
}
$rs = $db -> query("SELECT * FROM scripts");
$array = [];
while($row = $rs -> fetch_row()){
    $array[] = array(
        "name" => $row[0],
        "desc" => $row[1],
        "version" => floatval($row[2]),
        "author" => $row[3],
        "hash" => $row[4]
    );
}
echo json_encode($array);
$rs -> close();
$db -> close();