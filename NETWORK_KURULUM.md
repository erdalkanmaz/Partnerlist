# Partnerlist Uygulaması - Network Ortamına Kurulum Rehberi

Bu rehber, Partnerlist uygulamasını bir network ortamına (SharePoint, network drive, vb.) kurmak ve çalıştırmak için gerekli adımları içerir.

## 📋 Gereksinimler

1. **Java 17 veya üzeri** - [Eclipse Adoptium](https://adoptium.net/) veya [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) indirip kurun
2. **Maven** (sadece derleme için gerekli, opsiyonel)
3. **Network erişimi** - Uygulamanın çalışacağı klasöre erişim

## 🚀 Kurulum Adımları

### 1. Projeyi Derleme (JAR Dosyası Oluşturma)

#### IntelliJ IDEA'dan:
1. IntelliJ IDEA'da projeyi açın
2. Sağ üst köşedeki **Maven** sekmesini açın
3. `partnerlist` > `Lifecycle` > `package` seçeneğine çift tıklayın
4. Derleme tamamlandığında `target\partnerlist-1.0.0.jar` dosyası oluşacak

#### Komut satırından:
```bash
mvn clean package
```

### 2. Network Ortamına Kopyalama

#### Seçenek A: SharePoint'e Yerleştirme
1. SharePoint'te bir klasör oluşturun (örn: "Partnerlist")
2. Şu dosyaları kopyalayın:
   - `target\partnerlist-1.0.0.jar`
   - `start-partnerlist.bat` (veya `start-partnerlist.ps1`)
   - `instance` klasörü (veritabanı için - ilk çalıştırmada otomatik oluşur)

#### Seçenek B: Network Drive'a Yerleştirme
1. Network drive'da bir klasör oluşturun (örn: `\\server\apps\Partnerlist`)
2. Tüm dosyaları bu klasöre kopyalayın

### 3. Uygulamayı Çalıştırma

#### Windows'ta Çift Tıklayarak:
1. `start-partnerlist.bat` dosyasına çift tıklayın
2. Bir komut penceresi açılacak ve uygulama başlayacak
3. Tarayıcınızda `http://localhost:8080` adresini açın

#### PowerShell ile:
1. `start-partnerlist.ps1` dosyasına sağ tıklayın
2. "PowerShell ile çalıştır" seçeneğini seçin

#### Doğrudan JAR ile:
```bash
java -jar partnerlist-1.0.0.jar
```

## 🌐 Network'te Erişim

### Tek Kullanıcılı Erişim (Localhost)
Uygulama varsayılan olarak `localhost:8080` adresinde çalışır. Sadece uygulamayı çalıştıran bilgisayardan erişilebilir.

### Network'teki Diğer Bilgisayarlardan Erişim

Uygulama zaten `0.0.0.0:8080` adresinde dinliyor (application.properties'te ayarlı). Diğer bilgisayarlardan erişmek için:

1. **Firewall ayarları**: Windows Firewall'da 8080 portunu açın:
   ```powershell
   New-NetFirewallRule -DisplayName "Partnerlist" -Direction Inbound -LocalPort 8080 -Protocol TCP -Action Allow
   ```

2. **IP adresini bulun**: Uygulamayı çalıştıran bilgisayarın IP adresini öğrenin:
   ```bash
   ipconfig
   ```
   Örnek: `192.168.1.100`

3. **Diğer bilgisayarlardan erişim**: Tarayıcıda şu adresi açın:
   ```
   http://192.168.1.100:8080
   ```

## 📁 Klasör Yapısı

Network ortamında şu klasör yapısı önerilir:

```
Partnerlist/
├── partnerlist-1.0.0.jar          # Ana uygulama dosyası
├── start-partnerlist.bat           # Başlatma script'i (Windows)
├── start-partnerlist.ps1           # Başlatma script'i (PowerShell)
├── instance/                        # Veritabanı klasörü
│   └── partnerlist.db              # SQLite veritabanı (otomatik oluşur)
└── logs/                           # Log dosyaları (opsiyonel)
```

## ⚙️ Yapılandırma

### Port Değiştirme
`src/main/resources/application.properties` dosyasında:
```properties
server.port=8080
```

### Veritabanı Konumu
Veritabanı dosyası `instance/partnerlist.db` konumunda saklanır. Bu klasörü yedeklemeyi unutmayın!

## 🔒 Güvenlik Notları

1. **Varsayılan Kullanıcı**: 
   - Kullanıcı adı: `admin`
   - Şifre: `admin`
   - **İlk girişten sonra şifreyi değiştirmeyi unutmayın!**

2. **Firewall**: Production ortamında sadece gerekli IP adreslerinden erişime izin verin.

3. **Veritabanı Yedekleme**: `instance/partnerlist.db` dosyasını düzenli olarak yedekleyin.

## 🛠️ Sorun Giderme

### "Java bulunamadı" Hatası
- Java'nın PATH'e eklendiğinden emin olun
- Komut satırında `java -version` komutunu çalıştırarak test edin

### "Port zaten kullanımda" Hatası
- Başka bir uygulama 8080 portunu kullanıyor olabilir
- `application.properties` dosyasında portu değiştirin

### "Veritabanı bulunamadı" Hatası
- `instance` klasörünün mevcut olduğundan emin olun
- Klasör yazma izinlerini kontrol edin

### Network'ten Erişilemiyor
- Windows Firewall'da 8080 portunun açık olduğunu kontrol edin
- Uygulamanın çalıştığı bilgisayarın IP adresini doğru kullandığınızdan emin olun

## 📝 Otomatik Başlatma (Windows Service)

Uygulamayı Windows Service olarak kurmak için:

### NSSM (Non-Sucking Service Manager) Kullanımı:
1. [NSSM](https://nssm.cc/download) indirin
2. Yönetici olarak komut satırını açın:
   ```bash
   nssm install PartnerlistService
   ```
3. Açılan pencerede:
   - **Path**: JAR dosyasının tam yolu
   - **Startup directory**: JAR dosyasının bulunduğu klasör
   - **Arguments**: (boş bırakın)
4. "Install service" butonuna tıklayın

### WinSW (Windows Service Wrapper) Kullanımı:
1. [WinSW](https://github.com/winsw/winsw/releases) indirin
2. `winsw.xml` dosyası oluşturun (örnek aşağıda)
3. Yönetici olarak: `winsw install`

## 🔄 Güncelleme

1. Yeni JAR dosyasını oluşturun (`mvn clean package`)
2. Eski JAR dosyasını yedekleyin
3. Yeni JAR dosyasını network klasörüne kopyalayın
4. Uygulamayı yeniden başlatın

## 📞 Destek

Sorun yaşarsanız:
1. Log dosyalarını kontrol edin
2. Komut penceresindeki hata mesajlarını not edin
3. `instance/partnerlist.db` dosyasının yedeğini alın

---

**Not**: SharePoint'in kendisi Java uygulamalarını çalıştıramaz. Uygulamayı bir Windows sunucusunda veya kullanıcı bilgisayarında çalıştırıp, SharePoint'ten link verebilirsiniz.
