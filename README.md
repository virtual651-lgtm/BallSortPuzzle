# Renk Tüpleri – Android

Kotlin + Jetpack Compose + Material 3 + Hilt + DataStore ile geliştirilmiş çevrimdışı Android bulmaca oyunu.

## Özellikler
- **Saf Kotlin Domain Katmanı:** `GameLogic`, `LevelGenerator`, `DifficultyCurve`, `Solver` (A* + simetri indirgemesi) ve `StarCalculator`.
- **120+ Deterministik Seviye + Günlük Meydan Okuma:** Tarih tabanlı günlük seviye ve kademeli zorluk eğrisi.
- **Animasyon & Geri Bildirim:** Tüpler arası yay (arc) uçuş animasyonu, geçersiz hamle sallanması, konfeti, ses (`SoundPlayer`) ve titreşim (`Haptics`).
- **Erişilebilirlik & Çoklu Dil:** Renk körlüğü sembol modu, TalkBack açıklamaları, Türkçe ve İngilizce dil desteği.
- **Ücretsiz Güncelleme Kontrolü:** `UpdateChecker`, GitHub üzerindeki `version.json` dosyasını okuyarak yeni APK çıktığında ana menüde bildirim gösterebilir.

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
1. Kendi GitHub reponuzu oluşturup bu projeyi yükleyin.
2. `app/build.gradle.kts` içindeki `UPDATE_CHECK_URL` alanını kendi `raw.githubusercontent.com/.../version.json` adresinizle güncelleyin.
3. Yeni güncelleme çıkarırken:
   - `app/build.gradle.kts` içinde `versionCode` (ör. `2`) ve `versionName` (ör. `"1.0.1"`) değerlerini artırın.
   - **Aynı `.keystore` dosyasıyla** imzalı Release APK üretin (`Build > Generate Signed Bundle / APK`).
   - APK'yı GitHub **Releases** bölümüne yükleyin.
   - Repodaki `version.json` dosyasındaki `latestVersionCode`, `versionName` ve `apkUrl` alanlarını güncelleyin.
   - Oyuncular uygulamayı açtığında ana menüde otomatik olarak *"Yeni Güncelleme Mevcut"* kartını görecektir.

