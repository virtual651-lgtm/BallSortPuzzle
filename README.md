# Renk Tüpleri – Android (v1.2.0)

Kotlin + Jetpack Compose + Material 3 + Hilt + DataStore ile geliştirilmiş modern, çevrimdışı ve LAN destekli Android bulmaca oyunu.

## Özellikler

- **Royal Pacing & Saraylar Sistemi (Sonsuz Ölçeklenebilir):** 
  - Her 10 bölümde bir dinamik ritim: 5. Bölüm Zor 🟣, 9. Bölüm Süper Zor 🔴, 10. Bölüm Hükümdar / Vezir Boss Düellosu 👑.
  - Her 100 bölümde bir Çağ değişimi: 1–100 Şah Sarayı, 101–200 Sultan Divanı, 201–300 Han Otağı, 301–400 Halife Köşkü (binlerce bölüme uzanan algoritmik yapı).
- **%100 Çözülebilir Seviye Motoru:** `LevelGenerator` ve `Solver` ile matematiksel olarak doğrulanmış, ek tüp almadan tamamen çözülebilir tahtalar.
- **Altın & Büyük Hazine Sandığı Ödül Ekonomisi:**
  - Yıldızlara göre altın kazanımı (10 / 20 / 30 Altın) ve çarpanlar (Zor 2x, Süper Zor 3x).
  - Boss geçildiğinde patlayan Büyük Hazine Sandığı (Altın + Jetonlar).
  - Eski oyuncular için geriye dönük hoş geldin altın bonusu (Sıfır veri kaybı).
- **Taktiksel Dengeleme & Hak Sınırları:**
  - Bölüm başı 3 adet ücretsiz Geri Alma hakkı; tüp ekleme ve fazladan geri alma için taktiksel altın/jeton kullanımı.
- **Wi-Fi Düello (LAN) + Kesintisiz Rövanş (Rematch):**
  - Aynı yerel ağda QR kodla anında eşleşme.
  - Maç bittiğinde bağlantı kopmaz; tek tuşla sıfırdan QR okutmadan sınırsız rövanş oynanabilir.
- **Animasyon & Geri Bildirim:** Tüpler arası yay (arc) uçuş animasyonu, geçersiz hamle sallanması, konfeti, ses (`SoundPlayer`) ve titreşim (`Haptics`).
- **Erişilebilirlik & Çoklu Dil:** Renk körlüğü sembol modu, TalkBack açıklamaları, Türkçe ve İngilizce dil desteği.
- **Uygulama İçi Otomatik Güncelleme:** GitHub Releases üzerinden APK indirme ve kurulum desteği.

## Android Studio'da Çalıştırma

1. Android Studio (Ladybug veya üstü) ile bu klasörü (`BallSortPuzzle`) açın.
2. Gradle senkronizasyonunun tamamlanmasını bekleyin.
3. Bir Android emülatör veya USB hata ayıklaması açık cihaz seçip **Run (`app`)** tuşuna basın.

## Testleri Çalıştırma

```bash
./gradlew testDebugUnitTest
./gradlew assembleDebug
```

## Ücretsiz Güncelleme Nasıl Yayınlanır? (GitHub Releases)

1. `app/build.gradle.kts` içinde `versionCode` ve `versionName` artırılır (örn: v1.2.0, versionCode 8).
2. `version.json` dosyasında `latestVersionCode`, `versionName` ve güncelleme notları güncellenir.
3. APK derlenip GitHub Releases bölümüne `app-release.apk` adıyla yüklenir.
4. Kodlar GitHub reposuna push edilir (`git push`).
5. Kullanıcılar oyunu açtığında güncelleme penceresiyle karşılaşır ve tek dokunuşla yeni sürüme geçer.
