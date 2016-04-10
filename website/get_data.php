<?php
define('HOST','localhost');
define('USER','root');
define('PASS','root');
define('DB','dt');
 
$con = mysqli_connect(HOST,USER,PASS,DB);
 
$sql = "select * from tb_katadasar";
 
$res = mysqli_query($con,$sql);
 
$result = array();
 
while($row = mysqli_fetch_array($res)){
array_push($result,
array('ID'=>$row[0],
'KATA'=>$row[1],
'VIDEO'=>$row[2]
));
}
 
echo json_encode(array("RESULTS"=>$result));
 
mysqli_close($con);
 
?>