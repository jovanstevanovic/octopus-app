<?php

if(isset($_POST['firmName'])) {
    require_once('config.inc.php');
    
    $firm_name = $_POST['firmName'];
    
    $sql = 'SELECT * from services WHERE name = :firmName';
    $statement = $conn->prepare($sql);
    $statement->bindParam(':firmName', $firm_name, PDO::PARAM_STR);
    $statement->execute();
    
    if($statement->rowCount() > 0) {
        $row_all = $statement->fetchall(PDO::FETCH_ASSOC);

        foreach($row_all as $key=>$value){
          $new_arr_data[$key] =  $row_all[$key];
          $new_arr_data[$key]['image'] = base64_encode($row_all[$key]['image']);
        }   

        header('Content-type: application/json');
        echo json_encode($new_arr_data);
    } else {
        echo "No rows found!";
    }
}

