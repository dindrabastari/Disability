<?php
define('HOST','localhost');
define('USER','root');
define('PASS','root');
define('DB','dt');

$con = mysqli_connect(HOST,USER,PASS,DB);

$sql = "SELECT * FROM tb_katadasar";

$res = mysqli_query($con,$sql);

$result = array();

header("Content-type: application/json");

while($row = mysqli_fetch_array($res)){
  array_push($result,
    array('ID'=>$row[0],
    'KATA'=>$row[1],
    'LAFAL'=>$row[2],
    'DESKRIPSI'=>$row[3],
    'VIDEO'=>"http://10.0.3.2/dt/video/".$row[4]
  ));
}

echo json_encode(array("RESULTS"=>$result));

mysqli_close($con);

?>
