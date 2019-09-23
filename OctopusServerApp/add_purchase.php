<?php

if(isset($_POST['service_id']) && isset($_POST['username']) && isset($_POST['order_time']) && isset($_POST['amount'])) {
    require_once('config.inc.php');
    
    $service_id = intval($_POST['service_id']); 
    $username = $_POST['username']; 
    $order_time = $_POST['order_time'];
    $amount = intval($_POST['amount']);
    
    $sql = 'INSERT INTO ordering(service_id, username, order_time, amount) '
            . 'VALUES(:service_id, :username, :order_time, :amount)';
    
    $stmt = $conn->prepare($sql);
    
    $stmt->bindParam(':service_id', $service_id);
    $stmt->bindParam(':username', $username, PDO::PARAM_STR);
    $stmt->bindParam(':order_time', $order_time, PDO::PARAM_STR);
    $stmt->bindParam(':amount', $amount);

    try {
        $success = $stmt->execute();
        $result = "true";
    } catch (Exception $ex) {
         $result = "false";
    }

    // Send result back to android.
    echo $result;
}

