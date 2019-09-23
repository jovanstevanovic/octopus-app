<?php 

if(isset($_POST['firm_name']) && isset($_POST['rank']) && isset($_POST['type']) && isset($_POST['time'])
        && isset($_POST['descr']) && isset($_POST['price'])
        && isset($_POST['longitude']) && isset($_POST['latitude'])) {
    require_once('config.inc.php');
    
    $firm_name = $_POST['firm_name']; 
    $rank = intval($_POST['rank']); 
    $type = $_POST['type'];
    $time = $_POST['time'];
    $descr = $_POST['descr'];
    $price = intval($_POST['price']);
    $longitude = floatval($_POST['longitude']);
    $latitude = floatval($_POST['latitude']);
    
    $sql = 'INSERT INTO firms(name, rank, type, work_time, image, discription, longitude, latitude, average_price) '
            . 'VALUES(:firm_name, :rank, :type, :work_time, null, :descr , :longitude, :latitude, :average_price)';
    
    $stmt = $conn->prepare($sql);
    
    $stmt->bindParam(':firm_name', $firm_name, PDO::PARAM_STR);
    $stmt->bindParam(':rank', $rank);
    $stmt->bindParam(':type', $type, PDO::PARAM_STR);
    $stmt->bindParam(':work_time', $time, PDO::PARAM_STR);
    $stmt->bindParam(':descr', $descr, PDO::PARAM_STR);
    $stmt->bindParam(':average_price', $price);
    $stmt->bindParam(':longitude', $longitude);
    $stmt->bindParam(':latitude', $latitude);
    
    try {
        $success = $stmt->execute();
        $result = "Firm is succesfully inserted! :)";
    } catch (Exception $ex) { 
        $result = "Error during new firm creation! :(";
    }
    
    echo $result;
    echo '<br>';
}
?>

<html>
    <head>
        <meta charset="UTF-8">
        <title>Octopus</title>
    </head>
    <body>
        <strong>
            Forma za unos nove firme
        </strong>
        <form method="POST">
            <table>
                <tr>
                    <td>
                        Ime firme:
                    </td>
                    <td>
                        <input type="text" name="firm_name">
                    </td>
                </tr>
                <tr>
                    <td>
                        Rang:
                    </td>
                    <td>
                        <input type="text" name="rank">
                    </td>
                </tr>
                <tr>
                    <td>
                        Tip:
                    </td>
                    <td>
                        <input type="text" name="type">
                    </td>
                </tr>
                <tr>
                    <td>
                        Radno vreme:
                    </td>
                    <td>
                        <input type="text" name="time">
                    </td>
                </tr>
                <tr>
                    <td>
                        Opis:
                    </td>
                    <td>
                        <input type="text" name="descr">
                    </td>
                </tr>
                <tr>
                    <td>
                        Prosecna cena:
                    </td>
                    <td>
                        <input type="text" name="price">
                    </td>
                </tr>
                <tr>
                    <td>
                        Longitude:
                    </td>
                    <td>
                        <input type="text" name="longitude">
                    </td>
                </tr>
                <tr>
                    <td>
                        Latitude:
                    </td>
                    <td>
                        <input type="text" name="latitude">
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <input type="submit" name="addNewFirmButton" value="Dodaj firmu"/>
                    </td>
                </tr>
            </table>
        </form>
    </body>
</html>