fileUpload.php
<?php

$target_path = "uploads/";
 
// response array
$response = array();
 
// getting server ip address
$server_ip = gethostbyname(gethostname());
 
// upload file url
$file_upload_url = 'http://' . $server_ip . '/' . 'VideoUpload' . '/' . $target_path;
 
 
if (isset($_FILES['image']['name'])) {
    $target_path = $target_path . basename($_FILES['image']['name']);
 
    // reading paremeters
    $title = isset($_POST['title']) ? $_POST['title'] : '';
    $email = isset($_POST['email']) ? $_POST['email'] : '';
    $password = isset($_POST['password']) ? $_POST['password'] : '';
 
    $response['file_name'] = basename($_FILES['image']['name']);
    $response['title'] = $title;
    $response['email'] = $email;
    $response['password'] = $password;
 
    try {
        if (!move_uploaded_file($_FILES['image']['tmp_name'], $target_path)) {
            
            $response['error'] = true;
            $response['message'] = 'Could not move the file!';
        }
 
        // File successfully uploaded
        $response['message'] = 'File uploaded successfully!';
        $response['error'] = false;
        $response['file_path'] = $file_upload_url . basename($_FILES['image']['name']);
    } catch (Exception $e) {
        $response['error'] = true;
        $response['message'] = $e->getMessage();
    }
} else {
    $response['error'] = true;
    $response['message'] = 'Not received any file!';
}
 
echo json_encode($response);
?>