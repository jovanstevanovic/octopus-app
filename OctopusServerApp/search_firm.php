<?php

if(isset($_POST['firmName'])) {
    require_once('config.inc.php');
    
    $firm_name = $_POST['firmName'];
    
    if(isset($_POST['firmType'])) {
        $firm_type = $_POST['firmType'];
    
        $sql = 'SELECT * from firms WHERE FIND_IN_SET(type, :firmType) AND name = :firmName';

        $statement = $conn->prepare($sql);
        $statement->bindParam(':firmType', $firm_type, PDO::PARAM_STR);
    } else {
        $sql = 'SELECT * from firms WHERE name = :firmName';
    
        $statement = $conn->prepare($sql);
    }
    
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
} else {
    if(isset($_POST['firmType'])) {
        require_once('config.inc.php');
        
        $firm_type = $_POST['firmType'];
        
        if(isset($_POST['orderFormat']) && isset($_POST['longitude']) && isset($_POST['latitude'])) {
            $order_format = intval($_POST['orderFormat']);
            $longitude = floatval($_POST['longitude']);
            $latitude = floatval($_POST['latitude']);
            
            switch ($order_format) {
                case 0:
                    $sql = 'SELECT * from firms WHERE FIND_IN_SET(type, :firmType)'
                        . 'ORDER BY SQRT((longitude - :longitude) * (longitude - :longitude) + (latitude - :latitude) * (latitude - :latitude)) ASC';
                    
                    $statement = $conn->prepare($sql);
                    $statement->bindParam(':longitude', $longitude);
                    $statement->bindParam(':latitude', $latitude);
                    break;
                case 1:
                    $sql = 'SELECT * from firms WHERE FIND_IN_SET(type, :firmType)'
                        . 'ORDER BY SQRT((longitude - :longitude) * (longitude - :longitude) + (latitude - :latitude) * (latitude - :latitude)) DESC';
                    
                    $statement = $conn->prepare($sql);
                    $statement->bindParam(':longitude', $longitude);
                    $statement->bindParam(':latitude', $latitude);
                    break;
                case 2:
                    $sql = 'SELECT * from firms WHERE FIND_IN_SET(type, :firmType)'
                        . 'ORDER BY average_price ASC';
                    $statement = $conn->prepare($sql);
                    break;
                case 3:
                    $sql = 'SELECT * from firms WHERE FIND_IN_SET(type, :firmType)'
                        . 'ORDER BY average_price DESC';
                    $statement = $conn->prepare($sql);
                    break;
                case 4:
                    $sql = 'SELECT * from firms WHERE FIND_IN_SET(type, :firmType)'
                        . 'ORDER BY SQRT((longitude - :longitude) * (longitude - :longitude) + (latitude - :latitude) * (latitude - :latitude)) ASC'
                        . ', average_price ASC';
                    
                    $statement = $conn->prepare($sql);
                    $statement->bindParam(':longitude', $longitude);
                    $statement->bindParam(':latitude', $latitude);
                    break;
                case 5:
                    $sql = 'SELECT * from firms WHERE FIND_IN_SET(type, :firmType)'
                        . 'ORDER BY SQRT((longitude - :longitude) * (longitude - :longitude) + (latitude - :latitude) * (latitude - :latitude)) ASC'
                        . ', average_price DESC';
                    
                    $statement = $conn->prepare($sql);
                    $statement->bindParam(':longitude', $longitude);
                    $statement->bindParam(':latitude', $latitude);
                    break;
                case 6:
                    $sql = 'SELECT * from firms WHERE FIND_IN_SET(type, :firmType)'
                        . 'ORDER BY SQRT((longitude - :longitude) * (longitude - :longitude) + (latitude - :latitude) * (latitude - :latitude)) DESC'
                        . ', average_price ASC';
                    
                    $statement = $conn->prepare($sql);
                    $statement->bindParam(':longitude', $longitude);
                    $statement->bindParam(':latitude', $latitude);
                    break;
                case 7:
                    $sql = 'SELECT * from firms WHERE FIND_IN_SET(type, :firmType)'
                        . 'ORDER BY SQRT((longitude - :longitude) * (longitude - :longitude) + (latitude - :latitude) * (latitude - :latitude)) DESC'
                        . ', average_price DESC';
                    
                    $statement = $conn->prepare($sql);
                    $statement->bindParam(':longitude', $longitude);
                    $statement->bindParam(':latitude', $latitude);
                    break;
                default:
                    break;
            }
        } else {
            $sql = 'SELECT * from firms WHERE FIND_IN_SET(type, :firmType)';
            $statement = $conn->prepare($sql);
        }
        
        $statement->bindParam(':firmType', $firm_type, PDO::PARAM_STR);
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
    } else {
        require_once('config.inc.php');
        
        $sql = 'SELECT * from firms';
    
        $statement = $conn->prepare($sql);
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
}


