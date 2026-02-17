@echo off
REM Partnerlist Uygulamasını Başlatma Script'i
REM Bu dosyayı çift tıklayarak uygulamayı başlatabilirsiniz

echo ========================================
echo Partnerlist Uygulaması Başlatılıyor...
echo ========================================
echo.

REM Mevcut dizini al
cd /d "%~dp0"

REM JAR dosyasının varlığını kontrol et
if not exist "target\partnerlist-1.0.0.jar" (
    echo HATA: JAR dosyası bulunamadı!
    echo Lütfen önce 'mvn clean package' komutu ile projeyi derleyin.
    echo.
    pause
    exit /b 1
)

REM Java'nın yüklü olup olmadığını kontrol et
java -version >nul 2>&1
if errorlevel 1 (
    echo HATA: Java bulunamadı!
    echo Lütfen Java 17 veya üzeri bir sürüm yükleyin.
    echo.
    pause
    exit /b 1
)

REM Uygulamayı başlat
echo Uygulama başlatılıyor...
echo Tarayıcınızda http://localhost:8080 adresini açın
echo.
echo Durdurmak için bu pencereyi kapatın veya Ctrl+C tuşlarına basın.
echo.

java -jar target\partnerlist-1.0.0.jar

pause
