# IntelliJ IDEA'da Java Spring Boot Projesi Kurulum Kılavuzu

Bu kılavuz, IntelliJ IDEA'da Java Spring Boot projesini çalıştırma adımlarını içerir.

## 📋 İçindekiler
1. [Projeyi IntelliJ'de Açma](#1-projeyi-intellijde-açma)
2. [Maven Bağımlılıklarını Yükleme](#2-maven-bağımlılıklarını-yükleme)
3. [Projeyi Çalıştırma](#3-projeyi-çalıştırma)
4. [Excel Verilerini Yükleme](#4-excel-verilerini-yükleme)
5. [Sorun Giderme](#5-sorun-giderme)

---

## 1. Projeyi IntelliJ'de Açma

### Adım 1: Projeyi Aç
1. IntelliJ IDEA'yı açın
2. `File` → `Open` menüsüne tıklayın
3. `C:\Users\ErdalKanmaz\Intellij_Projects\Partnerlist` klasörünü seçin
4. `OK` butonuna tıklayın

### Adım 2: Maven Projesi Olarak Tanıma
1. IntelliJ, projeyi algıladığında bir pencere açılabilir
2. "Trust Project" butonuna tıklayın
3. Eğer "Import Maven Project" sorusu çıkarsa, `Yes` deyin
4. IntelliJ otomatik olarak `pom.xml` dosyasını tanıyacak ve Maven projesi olarak yapılandıracak

---

## 2. Maven Bağımlılıklarını Yükleme

### Yöntem 1: Otomatik Yükleme (Önerilen)

IntelliJ IDEA genellikle `pom.xml` dosyasını açtığınızda otomatik olarak bağımlılıkları yükler. Sağ üst köşede bir bildirim görünebilir:

- **"Maven projects need to be imported"** mesajı görürseniz, `Import Changes` butonuna tıklayın
- Veya `Enable Auto-Import` linkine tıklayarak otomatik yüklemeyi etkinleştirin

### Yöntem 2: Manuel Yükleme

1. Sağ taraftaki **Maven** tool window'u açın (yoksa `View` → `Tool Windows` → `Maven`)
2. Proje adının yanındaki **🔄 (Reload)** butonuna tıklayın
3. Veya `pom.xml` dosyasına sağ tıklayıp `Maven` → `Reload Project` seçin

### Yükleme Kontrolü

Maven bağımlılıkları yüklenirken:
- Alt kısımdaki **Maven** sekmesinde ilerlemeyi görebilirsiniz
- "BUILD SUCCESS" mesajı göründüğünde yükleme tamamlanmıştır
- İlk yükleme 2-5 dakika sürebilir (internet hızına bağlı)

---

## 3. Projeyi Çalıştırma

### Adım 1: Ana Sınıfı Bulma

1. Proje ağacında `src/main/java/com/partnerlist/PartnerlistApplication.java` dosyasını bulun
2. Dosyaya çift tıklayarak açın

### Adım 2: Run Configuration Oluşturma

1. `PartnerlistApplication.java` dosyasını açın
2. Sağ üstteki yeşil **▶ (Run)** butonunun yanındaki ok'a tıklayın
3. `Edit Configurations...` seçeneğine tıklayın
4. Sol üstteki **+** butonuna tıklayın
5. `Application` seçeneğini seçin
6. Ayarlar:
   - **Name:** `Partner List App`
   - **Main class:** `com.partnerlist.PartnerlistApplication`
   - **Working directory:** `$PROJECT_DIR$`
   - **Use classpath of module:** `partnerlist`
7. `OK` butonuna tıklayın

### Adım 3: Uygulamayı Başlatma

1. Yeşil **▶ (Run)** butonuna tıklayın
2. Veya `Shift+F10` tuşlarına basın
3. Alt kısımdaki **Run** sekmesinde çıktıları görebilirsiniz

### Başarılı Çalıştırma Kontrolü

Aşağıdaki mesajı görmelisiniz:
```
Started PartnerlistApplication in X.XXX seconds
```

Ve tarayıcıda `http://localhost:8080` adresine gittiğinizde giriş sayfası görünmelidir.

### İlk Giriş

- **Kullanıcı Adı:** `admin`
- **Şifre:** `admin123`

**ÖNEMLİ:** İlk girişten sonra şifreyi değiştirmeniz önerilir!

---

## 4. Excel Verilerini Yükleme

### Adım 1: Excel Dosyasını Hazırlama

Excel dosyanızda şu sütunlar olmalı:
- **Partner/Agent** (Firma Adı) - ZORUNLU
- **Web** (Web Adresi) - ZORUNLU
- **e-mail** (E-posta) - ZORUNLU
- **Adress** (Adres) - ZORUNLU
- **Person** (Personel Adı) - Opsiyonel
- Diğer sütunlar opsiyonel

### Adım 2: Web Arayüzünden Yükleme

1. Uygulamaya giriş yapın (`http://localhost:8080`)
2. Üst menüden **"Excel Import"** linkine tıklayın
3. **"Dosya Seç"** butonuna tıklayın ve Excel dosyanızı seçin
4. **"Yükle"** butonuna tıklayın
5. İşlem tamamlandığında başarı mesajı görünecektir
6. **"Firmalar"** sayfasına giderek yüklenen verileri kontrol edin

---

## 5. Sorun Giderme

### Maven Bağımlılıkları Yüklenmiyor

**Sorun:** Bağımlılıklar indirilemiyor veya hata veriyor

**Çözüm:**
1. `File` → `Settings` → `Build, Execution, Deployment` → `Build Tools` → `Maven`
2. "Maven home directory" yolunu kontrol edin
3. "User settings file" yolunu kontrol edin
4. `pom.xml` dosyasına sağ tıklayıp `Maven` → `Reload Project`

### Port Kullanımda Hatası

**Sorun:** `Port 8080 is already in use`

**Çözüm:**
1. `src/main/resources/application.properties` dosyasını açın
2. `server.port=8080` satırını `server.port=8081` olarak değiştirin
3. Uygulamayı yeniden başlatın
4. Tarayıcıda `http://localhost:8081` adresine gidin

### Veritabanı Hatası

**Sorun:** `SQLite database not found` veya benzeri hata

**Çözüm:**
1. `instance` klasörünün proje kökünde olduğundan emin olun
2. Klasör yoksa oluşturun: `New` → `Directory` → `instance`
3. Uygulamayı yeniden başlatın (veritabanı otomatik oluşturulur)

### Java Sürüm Uyumsuzluğu

**Sorun:** `Java version X is required but Y is found`

**Çözüm:**
1. `File` → `Project Structure` → `Project`
2. "SDK" bölümünden Java 17 veya üzeri bir sürüm seçin
3. "Language level" bölümünden "17" seçin
4. `OK` ve uygulamayı yeniden başlatın

### Spring Boot Başlamıyor

**Sorun:** Uygulama başlamıyor veya hata veriyor

**Çözüm:**
1. **Run** sekmesindeki hata mesajlarını kontrol edin
2. En yaygın sorunlar:
   - Port kullanımda
   - Veritabanı bağlantı hatası
   - Eksik bağımlılık
3. Hata mesajını okuyun ve ilgili çözümü uygulayın

---

## ✅ Kontrol Listesi

Kurulumun başarılı olduğunu kontrol etmek için:

- [ ] Proje IntelliJ'de açıldı
- [ ] Maven bağımlılıkları yüklendi (Maven tool window'da yeşil tik)
- [ ] `PartnerlistApplication.java` dosyası bulundu
- [ ] Run configuration oluşturuldu
- [ ] Uygulama başlatıldı ve "Started" mesajı görüldü
- [ ] Tarayıcıda `http://localhost:8080` açıldı
- [ ] Giriş sayfası görünüyor
- [ ] Admin/admin123 ile giriş yapıldı
- [ ] Excel import sayfası açılıyor

---

## 📝 Notlar

- **Port:** Varsayılan port 8080'dir. Değiştirmek için `application.properties` dosyasını düzenleyin
- **Veritabanı:** SQLite veritabanı `instance/partnerlist.db` dosyasında saklanır
- **Network Erişimi:** Uygulama `0.0.0.0:8080` üzerinde çalışır, network üzerinden erişilebilir
- **Hot Reload:** Spring Boot DevTools sayesinde kod değişikliklerinde otomatik yeniden başlatma yapılır

---

**İyi çalışmalar! 🚀**
