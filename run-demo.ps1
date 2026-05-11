param(
    [int]$Port = 3000
)

$env:PORT = "$Port"
Write-Host "Starting HelpDesk demo server at http://localhost:$Port"
node "$PSScriptRoot\demo-server.js"
