# Partner List Management System - Java Spring Boot

Müşteri ve partner listesi yönetim sistemi. Java Spring Boot ile geliştirilmiştir.

## 🚀 Özellikler

- ✅ Firma ve personel kayıt işlemleri (CRUD)
- ✅ Kullanıcı girişi ve yetkilendirme (Spring Security)
- ✅ Gelişmiş arama ve filtreleme
- ✅ Çoklu firma seçimi (checkbox)
- ✅ Raporlama ve yazdırma
- ✅ Excel'den toplu veri yükleme (Apache POI)
- ✅ Şube desteği (aynı firma, farklı adresler)
- ✅ Network ortamında kullanım için hazır

## 📋 Teknolojiler

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** (Güvenlik)
- **Spring Data JPA** (Veritabanı erişimi)
- **Thymeleaf** (HTML template engine)
- **SQLite** (Veritabanı)
- **Apache POI** (Excel import)
- **Maven** (Bağımlılık yönetimi)

## 🛠️ Kurulum

Detaylı kurulum kılavuzu için `JAVA_KURULUM.md` dosyasına bakın.

### Hızlı Başlangıç

1. **Projeyi IntelliJ IDEA'da açın**
   - `File` → `Open` → Proje klasörünü seçin

2. **Maven bağımlılıklarını yükleyin**
   - `pom.xml` dosyasına sağ tıklayın → `Maven` → `Reload Project`
   - Veya sağ taraftaki Maven tool window'dan reload yapın

3. **Uygulamayı çalıştırın**
   - `PartnerlistApplication.java` dosyasını açın
   - Yeşil ▶ (Run) butonuna tıklayın
   - Veya `Shift+F10` tuşlarına basın

4. **Tarayıcıda açın**
   - `http://localhost:8080` adresine gidin
   - Giriş: **admin** / **admin123**

## 📁 Proje Yapısı

```
Partnerlist/
├── src/main/java/com/partnerlist/
│   ├── PartnerlistApplication.java    # Ana uygulama sınıfı
│   ├── controller/                    # Controller'lar
│   │   ├── HomeController.java
│   │   ├── CompanyController.java
│   │   ├── ContactController.java
│   │   ├── SearchController.java
│   │   ├── PrintController.java
│   │   ├── LoginController.java
│   │   └── ExcelImportController.java
│   ├── model/                         # Entity sınıfları
│   │   ├── User.java
│   │   ├── Company.java
│   │   └── Contact.java
│   ├── repository/                    # Repository arayüzleri
│   │   ├── UserRepository.java
│   │   ├── CompanyRepository.java
│   │   └── ContactRepository.java
│   ├── service/                       # Service katmanı
│   │   ├── UserService.java
│   │   ├── CompanyService.java
│   │   ├── ContactService.java
│   │   └── ExcelImportService.java
│   └── config/                        # Konfigürasyon
│       ├── SecurityConfig.java
│       └── DataInitializer.java
├── src/main/resources/
│   ├── templates/                     # Thymeleaf template'leri
│   │   ├── base.html
│   │   ├── login.html
│   │   ├── index.html
│   │   ├── companies.html
│   │   ├── company_detail.html
│   │   ├── company_form.html
│   │   ├── contact_form.html
│   │   ├── search.html
│   │   ├── search_results.html
│   │   ├── print.html
│   │   └── import_excel.html
│   ├── static/                        # CSS ve JS
│   │   ├── css/style.css
│   │   └── js/main.js
│   └── application.properties         # Uygulama ayarları
├── pom.xml                            # Maven bağımlılıkları
└── instance/                          # Veritabanı (otomatik oluşur)
    └── partnerlist.db
```

## 🔐 Varsayılan Kullanıcı

İlk çalıştırmada otomatik olarak oluşturulur:
- **Kullanıcı Adı:** `admin`
- **Şifre:** `admin123`

**ÖNEMLİ:** İlk girişten sonra şifreyi değiştirin!

## 📊 Veritabanı

SQLite veritabanı `instance/partnerlist.db` dosyasında saklanır. İlk çalıştırmada otomatik oluşturulur.

## 📤 Excel Import

Excel dosyalarınızı web arayüzünden yükleyebilirsiniz:

1. Uygulamaya giriş yapın
2. Üst menüden **"Excel Import"** linkine tıklayın
3. Excel dosyanızı seçin (.xlsx formatında)
4. **"Yükle"** butonuna tıklayın

### Excel Formatı

Excel dosyanızda şu sütunlar olmalı:
- **Partner/Agent** (Firma Adı) - ZORUNLU
- **Web** (Web Adresi) - ZORUNLU
- **e-mail** (E-posta) - ZORUNLU
- **Adress** (Adres) - ZORUNLU
- **Person** (Personel Adı) - Opsiyonel
- Diğer sütunlar opsiyonel

## 🌐 Network Erişimi

Uygulama varsayılan olarak `0.0.0.0:8080` üzerinde çalışır, bu sayede network üzerindeki diğer bilgisayarlardan erişilebilir:

```
http://[SERVER_IP]:8080
```

## ⚙️ Yapılandırma

`src/main/resources/application.properties` dosyasından ayarları değiştirebilirsiniz:

- **Port:** `server.port=8080`
- **Veritabanı:** `spring.datasource.url=jdbc:sqlite:instance/partnerlist.db`

## 🔧 Geliştirme

### Hot Reload

Spring Boot DevTools sayesinde kod değişikliklerinde otomatik yeniden başlatma yapılır.

### Debug Modu

IntelliJ IDEA'da debug modunda çalıştırmak için:
1. `PartnerlistApplication.java` dosyasını açın
2. Sol taraftaki satır numaralarına tıklayarak breakpoint ekleyin
3. Yeşil 🐛 (Debug) butonuna tıklayın

## 📝 Notlar

- **Java Sürümü:** Java 17 veya üzeri gerekir
- **Maven:** Proje Maven ile yönetilir
- **IDE:** IntelliJ IDEA önerilir (Java projeleri için optimize edilmiştir)

## 🆘 Yardım

Sorun yaşarsanız:
1. `JAVA_KURULUM.md` dosyasındaki "Sorun Giderme" bölümüne bakın
2. Run sekmesindeki hata mesajlarını kontrol edin
3. Maven bağımlılıklarının yüklendiğinden emin olun

---

**İyi çalışmalar! 🚀**
