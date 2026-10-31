# دليل تشغيل مشروع FirstAid Hero 🩺 (خاص بأعضاء الفريق)
## SVU - BAIT_IWB303 - Fall F25

يقدم هذا الدليل خطوات تشغيل المشروع بالكامل لأي زميل في الفريق سواء عبر **Android Studio** أو عبر **سطر الأوامر (Terminal)** دون أي مشاكل توافقية.

---

## ⚡ الطريقة الأولى: التشغيل عبر Android Studio (الموصى بها)

1. **فتح المشروع**:
   * شغّل برنامج **Android Studio**.
   * اختر **File** $\rightarrow$ **Open...**
   * حدد مجلد المشروع `IWB303` واضغط **OK**.

2. **المزامنة التلقائية (Gradle Sync)**:
   * سيقوم Android Studio تلقائياً بالتعرف على مسار الـ SDK الخاص بجهازك وتوليد ملف `local.properties` المناسب لك فوراً.
   * انتظر حتى تكتمل عملية الـ Gradle Sync في الأسفل بنجاح (`BUILD SUCCESSFUL`).

3. **التشغيل على الهاتف أو المحاكي (Run)**:
   * وصل هاتفك عبر USB وتأكد من تفعيل **USB Debugging** (أو شغّل محاكي Android Emulator).
   * اضغط على زر التشغيل الأخضر **▶ (Run 'app')** في الشريط العلوي (أو اختصار `Shift + F10`).
   * سيتم تثبيت التطبيق وفتحه مباشرة على شاشة جهازك.

---

## 💻 الطريقة الثانية: البناء والتثبيت عبر سطر الأوامر (Terminal / CLI)

### 1. إعداد مسار الـ SDK (`local.properties`):
إذا كنت تبني المشروع من سطر الأوامر، تأكد من وجود ملف `local.properties` يحتوي على مسار الـ Android SDK الخاص بجهازك:
* **ويندوز (Windows)**:
  ```properties
  sdk.dir=C\:\\Users\\<اسم_المستخدم_الخاص_بك>\\AppData\\Local\\Android\\Sdk
  ```
* **ماك (macOS)**:
  ```properties
  sdk.dir=/Users/<your_username>/Library/Android/sdk
  ```
* **لينكس (Linux)**:
  ```properties
  sdk.dir=/home/<your_username>/Android/Sdk
  ```
*(يوجد ملف قالب جاهز باسم `local.properties.example` يمكنك نسخه وتعديل الاسم فقط).*

### 2. تجميع ملف الـ APK:
* **على Windows (PowerShell أو CMD)**:
  ```powershell
  .\gradlew.bat assembleDebug
  ```
* **على macOS / Linux**:
  ```bash
  ./gradlew assembleDebug
  ```
ستجد ملف الـ APK الناتج في المسار:
```
app/build/outputs/apk/debug/app-debug.apk
```

### 3. تثبيت وتشغيل التطبيق على الهاتف مباشرة عبر ADB:
```bash
adb install -r -d app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.example.firstaidhero/.MainActivity
```

---

## 🛠️ حل المشاكل الشائعة (Troubleshooting)

### 1. خطأ "SDK location not found":
* **السبب**: ملف `local.properties` مفقود أو يشير إلى مسار جهاز آخر (ملف `local.properties` لا يتم رفعه إلى Git افتراضياً لأنه خاص بكل جهاز).
* **الحل**: انسخ ملف `local.properties.example` إلى `local.properties` وضع مسار مجلد الـ Sdk الخاص بجهازك، أو افتح المشروع في Android Studio ليتم توليده تلقائياً.

### 2. خطأ "INSTALL_FAILED_USER_RESTRICTED" على أجهزة شاومي (Xiaomi / Redmi):
* **السبب**: حماية نظام MIUI / HyperOS تتطلب إذناً يدوياً لتثبيت التطبيقات عبر USB.
* **الحل**: 
  1. فعّل خيار **Install via USB** (التثبيت عبر USB) من خيارات المطور (Developer Options).
  2. أو ببساطة: انسخ ملف `app-debug.apk` إلى هاتفك وافتحه عبر **مدير الملفات (File Manager)** واضغط تثبيت.

### 3. إصدار جافا (Java Version):
* يتطلب المشروع **Java 17 (JDK 17)** أو أحدث. إذا ظهر خطأ متعلق بـ JVM، تأكد في Android Studio من الذهاب إلى:
  `Settings` $\rightarrow$ `Build, Execution, Deployment` $\rightarrow$ `Build Tools` $\rightarrow$ `Gradle` $\rightarrow$ اختر **Gradle JDK: JDK 17**.

---

## 📱 اختبار الشاشات الخمس المطلوبة في الوظيفة

| الشاشة | ما يتم اختباره |
| :--- | :--- |
| **الرئيسية (Search Dashboard)** | جرب البحث الفوري عن كلمات مثل "لدغ"، "غصة"، "حروق" وشاهد الاستجابة اللحظية. |
| **خطوات الإسعاف (Action Steps)** | اضغط على أي حالة لمشاهدة الخطوات المرتبة كبطاقات واضحة بدون مشاكل التمرير، وزر الاتصال السريع بـ 101. |
| **أرقام الطوارئ (Emergency Dialer)** | اضغط على زر `طوارئ 🚨` لإضافة وحذف أرقام أطباء أو أقارب واختبار الاتصال المباشر. |
| **سجل الحالات المعالجة (Case Logs)** | اضغط على `📝 سجل الحالات` لتوثيق إجراء إسعافي بالوصف والتاريخ والإجراء، ثم جرب البحث في السجلات. |
| **التقارير والإحصائيات (Reports)** | اضغط على `📊 الإحصاءات والتقارير` لمشاهدة توزيع الحالات حسب مستوى الخطورة (حرج / متوسط) باستخدام استعلامات المجاميع `GROUP BY`. |
