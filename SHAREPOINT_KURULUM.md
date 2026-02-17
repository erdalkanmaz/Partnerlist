# SharePoint Ortamında Partnerlist Kurulumu

## ⚠️ Önemli Not

**SharePoint'in kendisi Java uygulamalarını çalıştıramaz.** SharePoint sadece web içeriği barındırır. Java Spring Boot uygulaması için bir Windows sunucusu veya kullanıcı bilgisayarı gereklidir.

## 🎯 Önerilen Yaklaşım

### Senaryo 1: SharePoint'ten Link Verme (En Kolay)

1. **Uygulamayı bir Windows sunucusuna veya kullanıcı bilgisayarına kurun**
   - Detaylar için `NETWORK_KURULUM.md` dosyasına bakın

2. **SharePoint'te bir sayfa oluşturun**
   - SharePoint'te yeni bir sayfa oluşturun
   - "Web Part" ekleyin ve "Link" veya "Button" ekleyin
   - Link adresi: `http://SUNUCU_IP:8080` (örn: `http://192.168.1.100:8080`)

3. **Kullanıcılar SharePoint'ten linke tıklayarak uygulamaya erişir**

### Senaryo 2: SharePoint Document Library'de Dosya Olarak Barındırma

1. **SharePoint Document Library'de klasör oluşturun**
   - Örnek: "Partnerlist Application"

2. **Gerekli dosyaları yükleyin:**
   - `partnerlist-1.0.0.jar`
   - `start-partnerlist.bat`
   - `NETWORK_KURULUM.md` (kullanım kılavuzu)

3. **Kullanıcılara talimat verin:**
   - SharePoint'ten `start-partnerlist.bat` dosyasını indirip çalıştırmaları
   - Veya dosyayı SharePoint'ten açıp "Download" yapmaları

## 📋 Adım Adım Kurulum

### 1. Uygulamayı Hazırlama

```bash
# Projeyi derleyin
mvn clean package

# Oluşan dosyalar:
# - target/partnerlist-1.0.0.jar
# - start-partnerlist.bat
# - start-partnerlist.ps1
```

### 2. SharePoint'e Yükleme

#### Yöntem A: Document Library
1. SharePoint'te bir Document Library oluşturun
2. Tüm dosyaları bu library'ye yükleyin
3. Kullanıcılara erişim izni verin

#### Yöntem B: OneDrive for Business
1. OneDrive klasörüne dosyaları kopyalayın
2. SharePoint'ten OneDrive'a link verin

### 3. Kullanıcı Erişimi

**Seçenek 1: Batch Dosyasını Çalıştırma**
- SharePoint'ten `start-partnerlist.bat` dosyasını indirin
- Dosyaya çift tıklayın
- Uygulama başlayacak ve tarayıcıda açılacak

**Seçenek 2: Doğrudan JAR Çalıştırma**
- SharePoint'ten `partnerlist-1.0.0.jar` dosyasını indirin
- Komut satırında: `java -jar partnerlist-1.0.0.jar`

## 🌐 Network Erişimi İçin Yapılandırma

### Uygulamayı Network'te Erişilebilir Yapma

1. **Uygulamayı çalıştıran bilgisayarın IP adresini öğrenin:**
   ```bash
   ipconfig
   ```

2. **Windows Firewall'da port açın:**
   ```powershell
   New-NetFirewallRule -DisplayName "Partnerlist" -Direction Inbound -LocalPort 8080 -Protocol TCP -Action Allow
   ```

3. **SharePoint'te link oluşturun:**
   - Link: `http://BILGISAYAR_IP:8080`
   - Örnek: `http://192.168.1.100:8080`

## 🔐 Güvenlik Önerileri

1. **Kullanıcı Kimlik Doğrulama:**
   - Uygulama zaten Spring Security ile korumalı
   - Varsayılan: `admin/admin`
   - İlk girişten sonra şifreyi değiştirin

2. **Network Güvenliği:**
   - Sadece gerekli kullanıcılara erişim izni verin
   - Firewall kurallarını sıkılaştırın
   - HTTPS kullanımı için reverse proxy (nginx, IIS) kullanabilirsiniz

3. **Veritabanı Yedekleme:**
   - `instance/partnerlist.db` dosyasını düzenli yedekleyin
   - SharePoint Document Library'ye yedek kopyalarını yükleyebilirsiniz

## 📱 SharePoint Modern Page'de Kullanım

### SharePoint Modern Page'e Link Ekleme:

1. SharePoint sayfasını düzenleme moduna alın
2. "+" (Add a web part) butonuna tıklayın
3. "Text" veya "Button" web part'ını seçin
4. Link ekleyin: `http://SUNUCU_IP:8080`
5. Metin: "Partnerlist Uygulamasını Aç"

### SharePoint Quick Links ile:

1. Quick Links web part'ını ekleyin
2. Yeni link ekleyin
3. URL: `http://SUNUCU_IP:8080`
4. Başlık: "Partnerlist"

## 🚀 Otomatik Başlatma (Sunucu için)

Eğer uygulama bir Windows sunucusunda çalışacaksa:

### Windows Service Olarak Kurulum:

1. **NSSM kullanarak:**
   ```bash
   nssm install PartnerlistService
   ```
   - Path: JAR dosyasının tam yolu
   - Startup directory: JAR dosyasının klasörü

2. **WinSW kullanarak:**
   - `winsw.xml` dosyasını düzenleyin
   - Yönetici olarak: `winsw install`

## 📊 Veritabanı Yönetimi

### Veritabanı Konumu:
- Dosya: `instance/partnerlist.db`
- Bu dosya uygulamanın çalıştığı klasörde oluşur

### Yedekleme:
1. Uygulamayı durdurun
2. `instance/partnerlist.db` dosyasını kopyalayın
3. SharePoint Document Library'ye yedek olarak yükleyin

### Geri Yükleme:
1. Uygulamayı durdurun
2. Yedek dosyayı `instance/` klasörüne kopyalayın
3. Uygulamayı yeniden başlatın

## ❓ Sık Sorulan Sorular

**S: SharePoint'te doğrudan çalıştıramaz mıyım?**
C: Hayır, SharePoint Java uygulamalarını çalıştıramaz. Bir Windows sunucusu veya kullanıcı bilgisayarı gereklidir.

**S: Birden fazla kullanıcı aynı anda kullanabilir mi?**
C: Evet, uygulama network'te çalıştığında tüm kullanıcılar aynı veritabanını paylaşır.

**S: Veritabanı nerede saklanır?**
C: Uygulamanın çalıştığı bilgisayarda `instance/partnerlist.db` dosyası olarak.

**S: Uygulamayı nasıl güncellerim?**
C: Yeni JAR dosyasını oluşturup eski dosyayı değiştirin ve uygulamayı yeniden başlatın.

---

**Daha fazla bilgi için:** `NETWORK_KURULUM.md` dosyasına bakın.
