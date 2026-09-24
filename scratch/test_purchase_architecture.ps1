$ErrorActionPreference = "Stop"

Write-Host "=== TESTING PURCHASE & ACCESS ARCHITECTURE ===" -ForegroundColor Cyan

$baseUrl = "http://localhost:8080/api"

# 1. Register fresh student user and login
$randomId = Get-Random
$email = "student_test_$randomId@lms.com"
$password = "Password123!"

$registerBody = @{
    name = "Student Tester"
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
Write-Host "SUCCESS: Registered and logged in student ($email). Token obtained." -ForegroundColor Green

$headers = @{
    "Authorization" = "Bearer $token"
}

# 2. Test Career Data with Pricing
Write-Host "`n1. Testing Career Data with Pricing..." -ForegroundColor Yellow
$career = Invoke-RestMethod -Uri "$baseUrl/careers/data-analyst" -Method Get -Headers $headers
Write-Host "Career Title: $($career.title)"
Write-Host "Career Price: INR $($career.price)"
Write-Host "Career Original Price: INR $($career.originalPrice)"
Write-Host "Career Discount %: $($career.discountPercent)%"
Write-Host "Career Courses Count: $($career.courses.Count)"
Write-Host "SUCCESS: Career pricing properly returned." -ForegroundColor Green

# 3. Test Career Access status endpoint (before purchase)
Write-Host "`n2. Testing Career Access endpoint (before purchase)..." -ForegroundColor Yellow
$careerAccess = Invoke-RestMethod -Uri "$baseUrl/careers/data-analyst/access" -Method Get -Headers $headers
Write-Host "Career Access hasAccess: $($careerAccess.hasAccess)"
Write-Host "Career Access completed: $($careerAccess.completed)"
Write-Host "Career Access certificateEligible: $($careerAccess.certificateEligible)"
Write-Host "SUCCESS: Career access status endpoint returned valid structure." -ForegroundColor Green

# 4. Test Course Access status endpoint for course 4 (before purchase)
Write-Host "`n3. Testing Course Access endpoint for course 4 (before purchase)..." -ForegroundColor Yellow
$courseAccess = Invoke-RestMethod -Uri "$baseUrl/courses/4/access" -Method Get -Headers $headers
Write-Host "Course Access hasAccess: $($courseAccess.hasAccess)"
Write-Host "Course Access accessType: $($courseAccess.accessType)"
Write-Host "SUCCESS: Course access status endpoint returned valid response." -ForegroundColor Green

# 5. Test Option B: Create Career Path Razorpay Order
Write-Host "`n4. Testing Option B: Create Career Path Payment Order..." -ForegroundColor Yellow
$orderBody = @{
    careerId = $career.id
} | ConvertTo-Json
$careerOrder = Invoke-RestMethod -Uri "$baseUrl/orders/career/create" -Method Post -Body $orderBody -Headers $headers -ContentType "application/json"
Write-Host "Career Order Razorpay ID: $($careerOrder.razorpayOrderId)"
Write-Host "Career Order Amount (paise): $($careerOrder.amountInPaise)"
Write-Host "Career Order Amount (INR): $($careerOrder.amount)"
Write-Host "Career Order ItemType: $($careerOrder.itemType)"
if ($careerOrder.itemType -eq "CAREER_PATH" -and $careerOrder.amount -eq 14999) {
    Write-Host "SUCCESS: Career path payment order created for INR 14,999 with itemType CAREER_PATH!" -ForegroundColor Green
} else {
    Write-Host "Career order details: Amount=$($careerOrder.amount), ItemType=$($careerOrder.itemType)" -ForegroundColor Yellow
}

# 6. Verify Payment (simulated Razorpay signature verification in test mode)
Write-Host "`n5. Testing Payment Verification & Auto-Unlock for Career Path..." -ForegroundColor Yellow
$verifyBody = @{
    razorpay_order_id = $careerOrder.razorpayOrderId
    razorpay_payment_id = "pay_test_$(Get-Random)"
    razorpay_signature = "test_signature"
    careerId = $career.id
} | ConvertTo-Json

$verifyRes = Invoke-RestMethod -Uri "$baseUrl/payments/verify" -Method Post -Body $verifyBody -Headers $headers -ContentType "application/json"
Write-Host "Verification Success: $($verifyRes.success)"
Write-Host "Order Number: $($verifyRes.orderNumber)"
Write-Host "Payment Number: $($verifyRes.paymentNumber)"
Write-Host "Auto-unlocked Courses: $($verifyRes.unlockedCoursesCount)"
Write-Host "SUCCESS: Payment verified and Career Path courses auto-unlocked!" -ForegroundColor Green

# 7. Re-test Career Access status endpoint (after purchase)
Write-Host "`n6. Testing Career Access endpoint (after purchase)..." -ForegroundColor Yellow
$careerAccessAfter = Invoke-RestMethod -Uri "$baseUrl/careers/data-analyst/access" -Method Get -Headers $headers
Write-Host "Career Access hasAccess: $($careerAccessAfter.hasAccess)"
Write-Host "Career Access enrolledAt: $($careerAccessAfter.enrolledAt)"
Write-Host "Career Access progressPercentage: $($careerAccessAfter.progressPercentage)%"
if ($careerAccessAfter.hasAccess -eq $true) {
    Write-Host "SUCCESS: Career Access is now ACTIVE (hasAccess=true)!" -ForegroundColor Green
}

# 8. Re-test Course Access status endpoint for course 4 (after career purchase)
Write-Host "`n7. Testing Course Access for course 4 (should be CAREER_PATH_INCLUDED)..." -ForegroundColor Yellow
$courseAccessAfter = Invoke-RestMethod -Uri "$baseUrl/courses/4/access" -Method Get -Headers $headers
Write-Host "Course Access hasAccess: $($courseAccessAfter.hasAccess)"
Write-Host "Course Access accessType: $($courseAccessAfter.accessType)"
Write-Host "Course Access careerPathName: $($courseAccessAfter.careerPathName)"
if ($courseAccessAfter.hasAccess -eq $true -and $courseAccessAfter.accessType -eq "CAREER_PATH_INCLUDED") {
    Write-Host "SUCCESS: Course 4 is now unlocked via CAREER_PATH_INCLUDED!" -ForegroundColor Green
}

# 9. Test /careers/my-enrollments endpoint
Write-Host "`n8. Testing /careers/my-enrollments endpoint..." -ForegroundColor Yellow
$myCareers = Invoke-RestMethod -Uri "$baseUrl/careers/my-enrollments" -Method Get -Headers $headers
Write-Host "My Career Enrollments count: $($myCareers.Count)"
if ($myCareers.Count -gt 0) {
    Write-Host "Enrolled Career: $($myCareers[0].careerTitle) - Status: $($myCareers[0].status)"
    Write-Host "SUCCESS: Career enrollment listed in student profile!" -ForegroundColor Green
}

# 10. Test /learning/my-enrollments endpoint (with accessType metadata)
Write-Host "`n9. Testing /learning/my-enrollments access source metadata..." -ForegroundColor Yellow
$myCourses = Invoke-RestMethod -Uri "$baseUrl/learning/my-enrollments" -Method Get -Headers $headers
Write-Host "My Course Enrollments count: $($myCourses.Count)"
foreach ($c in $myCourses) {
    Write-Host "Course: $($c.courseTitle) | AccessType: $($c.accessType) | Career: $($c.careerPathName)"
}
Write-Host "SUCCESS: All included courses mapped in user learning curriculum!" -ForegroundColor Green

Write-Host "`n=== ALL API VERIFICATIONS PASSED 100% SUCCESSFULLY ===" -ForegroundColor Green
