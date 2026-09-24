$body = @{
    name = "Test Student Auth"
    email = "test_auth_$(Get-Random)@lms.com"
    password = "Password123!"
    role = "STUDENT"
} | ConvertTo-Json

$res = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method Post -Body $body -ContentType "application/json"
$res | ConvertTo-Json
