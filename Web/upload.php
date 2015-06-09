<?php

function exists($name){
    return isset($_POST[$name]) && !empty($_POST[$name]);
}

function process(){
    if($_SERVER['REQUEST_METHOD'] != 'POST')
        return 0;
    if(!isset($_FILES['script_jar']) || empty($_FILES['script_jar']))
        return 1;
    if(!exists('name'))
        return 2;
    if(!exists('desc'))
        return 3;
    if(!exists('version'))
        return 4;
    if(!exists('author'))
        return 5;
    $script_jar = $_FILES['script_jar']['tmp_name'];
    $hash = md5_file($script_jar);
    if(!move_uploaded_file($_FILES['script_jar']['tmp_name'], "scripts/$hash.jar"))
        return 6;
    $name = $_POST['name'];
    $desc = $_POST['desc'];
    $version = $_POST['version'];
    $author = $_POST['author'];
    $db = new mysqli("localhost", "root", "admin", "jexcel");
    if($db -> connect_error){
        $db -> close();
        return 7;
    }
    $stmt= $db -> prepare("SELECT * FROM scripts WHERE name = ? AND version = ? AND author = ?");
    $stmt -> bind_param('sds', $name, $version, $author);
    if(!$stmt -> execute()){
        $stmt -> close();
        $db -> close();
        return 8;
    }
    $stmt -> store_result();
    if($stmt -> num_rows == 1){
        $stmt -> close();
        $db -> close();
        return 9;
    }
    $stmt -> close();
    $stmt = $db -> prepare("INSERT INTO scripts VALUES (?, ?, ?, ?, ?)");
    $stmt -> bind_param('ssdss', $name, $desc, $version, $author, $hash);
    if(!$stmt -> execute()){
        $stmt -> close();
        $db -> close();
        return 8;
    }
    $stmt -> close();
    $db -> close();
    return 10;
}

echo process();
