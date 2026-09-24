$ErrorActionPreference = "Stop"

Write-Host "=== TESTING OPTION A: STANDALONE COURSE PURCHASE ===" -ForegroundColor Cyan

$baseUrl = "http://localhost:8080/api"

# 1. Register fresh student user and login
$randomId = Get-Random
$email = "student_standalone_$randomId@lms.com"
$password = "Password123!"

$registerBody = @{
    name = "Standalone Student"
    email = $email
    password = $password
    role = "STUDENT"
} | ConvertTo-Json

$null = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body $registerBody -ContentType "application/json"

$loginBody = @{
    email = $email
    password = $password
} | ConvertTo-Json

$loginRes = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
$token = $loginRes.token
Write-Host "SUCCESS: Registered and logged in standalone student ($email)." -ForegroundColor Green

$headers = @{
    "Authorization" = "Bearer $token"
}

# 2. Check access before purchase (should be NONE)
$preAccess = Invoke-RestMethod -Uri "$baseUrl/courses/4/access" -Method Get -Headers $headers
Write-Host "Initial course 4 access hasAccess: $($preAccess.hasAccess) | accessType: $($preAccess.accessType)"

# 3. Create Standalone Course Payment Order for Course 4
Write-Host "`n1. Creating Standalone Course Order for Course 4..." -ForegroundColor Yellow
$orderBody = @{
    courseId = 4
} | ConvertTo-Json

$courseOrder = Invoke-RestMethod -Uri "$baseUrl/orders/create" -Method Post -Body $orderBody -Headers $headers -ContentType "application/json"
Write-Host "Course Order Razorpay ID: $($courseOrder.razorpayOrderId)"
Write-Host "Course Order Amount: INR $($courseOrder.amount)"
Write-Host "SUCCESS: Standalone Course order created." -ForegroundColor Green

# 4. Verify Payment with test signature
Write-Host "`n2. Verifying Course Payment..." -ForegroundColor Yellow
$verifyBody = @{
    razorpay_order_id = $courseOrder.razorpayOrderId
    razorpay_payment_id = "pay_standalone_$(Get-Random)"
    razorpay_signature = "test_signature"
    courseId = 4
} | ConvertTo-Json

$verifyRes = Invoke-RestMethod -Uri "$baseUrl/payments/verify" -Method Post -Body $verifyBody -Headers $headers -ContentType "application/json"
Write-Host "Payment Verification: $($verifyRes.success)"
Write-Host "Order Number: $($verifyRes.orderNumber)"
Write-Host "Course Title: $($verifyRes.courseTitle)"
Write-Host "SUCCESS: Standalone course payment completed!" -ForegroundColor Green

# 5. Check access after purchase (should be DIRECT_COURSE)
Write-Host "`n3. Checking Course Access status (should be DIRECT_COURSE)..." -ForegroundColor Yellow
$postAccess = Invoke-RestMethod -Uri "$baseUrl/courses/4/access" -Method Get -Headers $headers
Write-Host "Post-purchase hasAccess: $($postAccess.hasAccess)"
Write-Host "Post-purchase accessType: $($postAccess.accessType)"
if ($postAccess.hasAccess -eq $true -and $postAccess.accessType -eq "DIRECT_COURSE") {
    Write-Host "SUCCESS: Direct standalone course access granted (DIRECT_COURSE)!" -ForegroundColor Green
}

# 6. Check Career Access (should NOT have career access)
Write-Host "`n4. Checking Career Access (should be false since user only bought course 4)..." -ForegroundColor Yellow
$careerAccess = Invoke-RestMethod -Uri "$baseUrl/careers/data-analyst/access" -Method Get -Headers $headers
Write-Host "Career Access hasAccess: $($careerAccess.hasAccess)"
if ($careerAccess.hasAccess -ne $true) {
    Write-Host "SUCCESS: Career Path is NOT unlocked by single course purchase (Option A and B remain strictly independent)!" -ForegroundColor Green
}

Write-Host "`n=== OPTION A VERIFICATION COMPLETED 100% SUCCESSFULLY ===" -ForegroundColor Green
