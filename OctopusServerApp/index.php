<?php

include 'config.inc.php';

if(isset($_POST['username']) && isset($_POST['password'])) {
    $username = $_POST['username'];
    $password = $_POST['password'];

    $sql = 'SELECT * FROM user WHERE username = :username AND password = :password';
    $stmt = $conn->prepare($sql);
    $stmt->bindParam(':username', $username, PDO::PARAM_STR);
    $stmt->bindParam(':password', $password, PDO::PARAM_STR);
    $stmt->execute();

    if($stmt->rowCount() > 0) {
        echo 'Successed to login! :)';
        header("Location: add_new_firm.php");
    }  
    else {
        echo 'Fail to login! Wrong password or username! :(';
    }
}
?>

<html>
    <head>
        <meta charset="UTF-8">
        <title>Octopus</title>
    </head>
    <body> 
        <strong>
            Welcome to Octopus! :)
        </strong>
        <form method="POST">
            <table>
                <tr>
                    <td>
                        Korisnicko ime:
                    </td>
                    <td>
                        <input type="text" name="username">
                    </td>
                </tr>
                <tr>
                    <td>
                        Lozinka:
                    </td>
                    <td>
                        <input type="password" name="password">
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <input type="submit" name="loginButton" value="Log in"/>
                    </td>
                </tr>
            </table>
        </form>
    </body>
</html>