<?php

include 'config.inc.php';

if(isset($_POST['username']) && isset($_POST['password']) && isset($_POST['mail'])) {
     $username = $_POST['username'];
     $password = $_POST['password'];
     $mail = $_POST['mail'];
     
     $sql = 'INSERT INTO user(username, password, mail) VALUES(:username, :password, :mail)';
     $stmt = $conn->prepare($sql);
     $stmt->bindParam(':username', $username, PDO::PARAM_STR);
     $stmt->bindParam(':password', $password, PDO::PARAM_STR);
     $stmt->bindParam(':mail', $mail, PDO::PARAM_STR);
     
     try {
         $success = $stmt->execute();
         $result = "true";
     } catch (Exception $ex) {
          $result = "false";
     }

     // Send result back to android.
     echo $result;
} else {
    // Check whether username or password is set from android. If condition is true, then try to find user.
    if(isset($_POST['username']) && isset($_POST['password'])) {
        $username = $_POST['username'];
        $password = $_POST['password'];

        $sql = 'SELECT * FROM user WHERE username = :username AND password = :password';
        $stmt = $conn->prepare($sql);
        $stmt->bindParam(':username', $username, PDO::PARAM_STR);
        $stmt->bindParam(':password', $password, PDO::PARAM_STR);
        $stmt->execute();
        
        if($stmt->rowCount() > 0) {
            $result="true";	
        }  
        else {
            $result="false";
        }  

        // Send result back to android.
        echo $result;
   }
}

