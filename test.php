<?php
$conn = new mysqli('127.0.0.1', 'root', '', 'tripx_db_recovered');
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
// Set password to plain text "admin123" for test1@tripx.com
// And "user123" for demo.user2@tripx.com
$conn->query("UPDATE user SET password = 'password' WHERE email = 'test1@tripx.com'");
$conn->query("UPDATE user SET password = 'password' WHERE email = 'demo.user2@tripx.com'");

echo "Updated passwords.\n";
$conn->close();
?>
