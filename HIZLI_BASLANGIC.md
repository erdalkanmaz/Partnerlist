# 🚀 Partnerlist - Hızlı Başlangıç Rehberi

## ⚡ 5 Dakikada Çalıştırma

### 1. Projeyi Derleyin
```bash
mvn clean package
```

### 2. Başlatma Script'ini Çalıştırın
- **Windows:** `start-partnerlist.bat` dosyasına çift tıklayın
- **PowerShell:** `start-partnerlist.ps1` dosyasına sağ tıklayıp "PowerShell ile çalıştır" seçin

### 3. Tarayıcıda Açın
```
http://localhost:8080
```

### 4. Giriş Yapın
- **Kullanıcı adı:** `admin`
- **Şifre:** `admin`

✅ **Hazır!** Artık uygulamayı kullanabilirsiniz.

---

## 📦 Network Ortamına Kurulum

Detaylı kurulum için:
- **Genel Network Kurulumu:** `NETWORK_KURULUM.md`
- **SharePoint Kurulumu:** `SHAREPOINT_KURULUM.md`

---

## 🔧 Gereksinimler

- Java 17 veya üzeri
- Windows, Linux veya macOS
- En az 512 MB RAM
- 100 MB disk alanı

---

## 📝 Notlar

- Veritabanı `instance/partnerlist.db` dosyasında saklanır
- İlk çalıştırmada `instance` klasörü otomatik oluşur
- Uygulama varsayılan olarak `0.0.0.0:8080` adresinde dinler (network erişimi için hazır)

---

**Sorun mu yaşıyorsunuz?** `NETWORK_KURULUM.md` dosyasındaki "Sorun Giderme" bölümüne bakın.
