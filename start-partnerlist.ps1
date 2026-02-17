# Partnerlist Uygulamasını Başlatma PowerShell Script'i
# Bu dosyayı sağ tıklayıp "PowerShell ile çalıştır" seçeneğini kullanabilirsiniz

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Partnerlist Uygulaması Başlatılıyor..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Mevcut dizine geç
Set-Location $PSScriptRoot

# JAR dosyasının varlığını kontrol et
if (-not (Test-Path "target\partnerlist-1.0.0.jar")) {
    Write-Host "HATA: JAR dosyası bulunamadı!" -ForegroundColor Red
    Write-Host "Lütfen önce 'mvn clean package' komutu ile projeyi derleyin." -ForegroundColor Yellow
    Write-Host ""
    Read-Host "Devam etmek için Enter'a basın"
    exit 1
}

# Java'nın yüklü olup olmadığını kontrol et
try {
    $javaVersion = java -version 2>&1
    Write-Host "Java bulundu: $($javaVersion[0])" -ForegroundColor Green
} catch {
    Write-Host "HATA: Java bulunamadı!" -ForegroundColor Red
    Write-Host "Lütfen Java 17 veya üzeri bir sürüm yükleyin." -ForegroundColor Yellow
    Write-Host ""
    Read-Host "Devam etmek için Enter'a basın"
    exit 1
}

# Uygulamayı başlat
Write-Host "Uygulama başlatılıyor..." -ForegroundColor Green
Write-Host "Tarayıcınızda http://localhost:8080 adresini açın" -ForegroundColor Yellow
Write-Host ""
Write-Host "Durdurmak için bu pencereyi kapatın veya Ctrl+C tuşlarına basın." -ForegroundColor Yellow
Write-Host ""

java -jar target\partnerlist-1.0.0.jar

Read-Host "`nUygulama durduruldu. Devam etmek için Enter'a basın"
