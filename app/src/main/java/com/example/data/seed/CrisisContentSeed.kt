package com.example.data.seed

import com.example.data.local.entity.CrisisChecklistEntity
import com.example.data.local.entity.EmergencyKitItemEntity
import com.example.data.local.entity.KitCategory
import com.example.data.local.entity.MediaCategory
import com.example.data.local.entity.MediaDownloadStatus
import com.example.data.local.entity.OfflineMediaEntity
import com.example.data.model.CrisisGuide
import com.example.data.model.CrisisType
import com.example.data.model.EmergencyContactNumber
import com.example.data.model.QuizQuestion
import com.example.data.model.QuizTopic
import com.example.data.model.TrainingLesson

object CrisisContentSeed {

    val emergencyNumbers: List<EmergencyContactNumber> = listOf(
        EmergencyContactNumber("ems", "اورژانس پزشکی", "115", "امداد پزشکی و انتقال مصدومان", isPrimary = true),
        EmergencyContactNumber("red_crescent", "هلال احمر", "112", "امداد و نجات در حوادث و جاده‌ها", isPrimary = true),
        EmergencyContactNumber("fire", "آتش‌نشانی و نجات", "125", "حریق، محبوس‌شدن و نجات شهری", isPrimary = true),
        EmergencyContactNumber("police", "فوریت‌های پلیسی", "110", "امنیت، تصادفات و انسداد مسیر", isPrimary = true),
        EmergencyContactNumber("gas", "امداد گاز", "194", "نشت گاز، بوی گاز و قطع ایمن", isPrimary = false),
        EmergencyContactNumber("power", "حوادث برق", "121", "قطع برق، سقوط سیم و ترانس", isPrimary = false),
        EmergencyContactNumber("water", "امداد آب و فاضلاب", "122", "ترکیدگی لوله و آب‌گرفتگی شبکه", isPrimary = false)
    )

    val guides: Map<CrisisType, CrisisGuide> = mapOf(
        CrisisType.EARTHQUAKE to CrisisGuide(
            type = CrisisType.EARTHQUAKE,
            beforeActions = listOf(
                "لوازم سنگین، قفسه‌ها و آبگرمکن را با بست مناسب به دیوار مهار کنید.",
                "نقاط امن خانه (زیر میز محکم، کنار ستون اصلی و دور از شیشه) را به همه اعضا نشان دهید.",
                "محل شیر اصلی گاز، کنتور برق و فلکه آب را یاد بگیرید و به خانواده آموزش دهید.",
                "کیف اضطراری ۷۲ ساعته و کفش ساق‌دار و چراغ‌قوه را کنار تخت قرار دهید."
            ),
            duringActions = listOf(
                "قانون طلایی را اجرا کنید: پناه بگیرید (خم شوید)، سر و گردن را بپوشانید و پایه میز یا سازه محکم را محکم بگیرید.",
                "در داخل ساختمان بمانید و تا توقف کامل لرزش به سمت در خروجی یا راه‌پله ندوید.",
                "اگر بیرون هستید به فضای باز بروید و از ساختمان‌ها، تابلوها و تیرهای برق فاصله بگیرید.",
                "در خودرو توقف ایمن داشته باشید، زیر پل یا کنار دیوار نایستید و داخل خودرو بمانید."
            ),
            afterActions = listOf(
                "خود و اطرافیان را از نظر جراحت بررسی کنید و در صورت نیاز کمک‌های اولیه ارائه دهید.",
                "در صورت استشمام بوی گاز یا مشاهده آسیب، شیر اصلی گاز و برق را قطع کرده و از شعله استفاده نکنید.",
                "برای پس‌لرزه‌ها آماده باشید؛ تنها از مسیرهای ایمن و بدون استفاده از آسانسور خارج شوید.",
                "به‌جای تماس‌های طولانی، با پیامک کوتاه وضعیت ایمنی خود را به خانواده اطلاع دهید."
            ),
            commonMistakes = listOf(
                "استفاده از آسانسور هنگام لرزش یا بلافاصله پس از زلزله.",
                "دویدن به سمت راه‌پله یا پریدن از بالکن و پنجره در حین لرزه.",
                "ایستادن در چارچوب درهای غیرایمن قدیمی یا کنار نمای شیشه‌ای ساختمان.",
                "روشن کردن کبریت یا فندک در فضای بسته پس از زلزله بدون بررسی نشت گاز."
            ),
            safetyWarnings = listOf(
                "هشدار: تا زمانی که ایمنی سازه بررسی نشده، به ساختمان‌های دارای ترک عمیق بازنگردید.",
                "پس‌لرزه‌ها می‌توانند تا ساعت‌ها و روزها پس از زمین‌لرزه اصلی رخ دهند."
            ),
            recommendedTrainingId = "first_aid_bleeding"
        ),
        CrisisType.FLOOD to CrisisGuide(
            type = CrisisType.FLOOD,
            beforeActions = listOf(
                "ناودان‌ها، آبروهای حیاط و مسیرهای تخلیه آب اطراف خانه را تمیز نگه دارید.",
                "مدارک هویتی، داروها و وسایل ارزشمند را در کیسه‌های ضدآب و طبقات بالاتر قرار دهید.",
                "مسیرهای تخلیه به ارتفاعات امن محله را از پیش شناسایی کنید."
            ),
            duringActions = listOf(
                "بلافاصله به نقاط مرتفع حرکت کنید و در زیرزمین یا طبقات همکف در معرض آب‌گرفتگی نمانید.",
                "هرگز با پای پیاده یا با خودرو از آب جاری عبور نکنید؛ عمق و سرعت آب فریبنده است.",
                "اگر آب وارد منزل شد و دسترسی ایمن دارید، برق اصلی و گاز را قطع کنید."
            ),
            afterActions = listOf(
                "از آب لوله‌کشی تا اعلام رسمی سلامت آن برای آشامیدن استفاده نکنید و از آب بسته‌بندی بهره ببرید.",
                "مراقب کابل‌های برق افتاده، حیوانات موذی و گودال‌های پنهان زیر گل‌ولای باشید.",
                "قبل از استفاده مجدد از لوازم برقی خیس‌شده، آن‌ها را توسط متخصص بررسی کنید."
            ),
            commonMistakes = listOf(
                "رانندگی در معابر آب‌گرفته یا پل‌های غرق‌شده.",
                "نزدیک شدن به حاشیه رودخانه‌ها و مسیل‌ها برای تماشا یا فیلم‌برداری.",
                "لمس تابلوهای برق یا کلیدهای مرطوب با دست یا پای خیس."
            ),
            safetyWarnings = listOf(
                "هشدار: تنها ۱۵ سانتی‌متر آب با جریان تند می‌تواند تعادل یک فرد بالغ را برهم بزند.",
                "سیلاب ناگهانی در دره‌ها و بستر خشک رودخانه‌ها بدون بارش محلی هم رخ می‌دهد."
            ),
            recommendedTrainingId = "first_aid_cpr"
        ),
        CrisisType.FIRE to CrisisGuide(
            type = CrisisType.FIRE,
            beforeActions = listOf(
                "کپسول آتش‌نشانی مناسب منزل تهیه کرده و طرز کار آن (کشیدن ضامن، نشانه به پایه شعله) را تمرین کنید.",
                "حسگر دود نصب کنید و مسیرهای خروج اضطراری را همواره بدون مانع نگه دارید.",
                "از اتصال چندراهی‌های غیراستاندارد به وسایل پرمصرف مثل بخاری برقی خودداری کنید."
            ),
            duringActions = listOf(
                "خونسرد بمانید، آژیر یا هشدار دهید و فوراً ساختمان را از نزدیک‌ترین راه خروج ایمن ترک کنید.",
                "در فضاهای پر از دود، خمیده یا سینه‌خیز حرکت کنید تا هوای تمیزتر نزدیک کف زمین استنشاق شود.",
                "پیش از باز کردن هر در، با پشت دست دمای آن را لمس کنید؛ اگر داغ بود در را باز نکنید و درزها را با پارچه خیس ببندید."
            ),
            afterActions = listOf(
                "تا اجازه رسمی مأموران آتش‌نشانی به داخل ساختمان برنگردید.",
                "در صورت استنشاق دود یا سوختگی، فوراً از خدمات اورژانس پزشکی کمک بگیرید.",
                "گزارش ایمنی تأسیسات برق و گاز را پیش از سکونت مجدد بررسی کنید."
            ),
            commonMistakes = listOf(
                "بازگشت به داخل ساختمان برای برداشتن وسایل شخصی یا مدارک.",
                "ریختن آب روی آتش ناشی از روغن داغ یا تجهیزات برقی متصل به برق.",
                "استفاده از آسانسور به جای راه‌پله اضطراری."
            ),
            safetyWarnings = listOf(
                "هشدار: اگر لباس شما آتش گرفت، ندوید! بایستید، روی زمین دراز بکشید و بغلتید.",
                "گازهای سمی ناشی از دود در حریق‌های خانگی خطرناک‌تر از خود شعله هستند."
            ),
            recommendedTrainingId = "first_aid_burns"
        ),
        CrisisType.STORM to CrisisGuide(
            type = CrisisType.STORM,
            beforeActions = listOf(
                "اشیای سبک بالکن و حیاط مانند گلدان، صندلی و تجهیزات رها را به داخل منتقل یا محکم کنید.",
                "وضعیت درختان خشک یا شاخه‌های مشرف به سقف و پنجره را بررسی کنید.",
                "چراغ‌قوه و پاوربانک را شارژ کنید تا در صورت قطع برق آماده باشید."
            ),
            duringActions = listOf(
                "در فضای بسته بمانید، پرده‌ها را بکشید و از پنجره‌های بزرگ و شیشه‌ای فاصله بگیرید.",
                "از استفاده از وسایل برقی متصل به پریز در زمان رعدوبرق شدید خودداری کنید.",
                "در فضای باز به زیر درختان بلند، دکل‌ها و سازه‌های فلزی موقت پناه نبرید."
            ),
            afterActions = listOf(
                "به سیم‌های برق قطع‌شده یا آویزان دست نزنید و حداقل ۱۰ متر فاصله بگیرید.",
                "سقف، دودکش‌ها و شاخه‌های شکسته اطراف منزل را به دقت بررسی کنید."
            ),
            commonMistakes = listOf(
                "پناه‌گرفتن زیر درختان تنومند هنگام رعدوبرق.",
                "تردد نزدیک داربست‌ها، تابلوهای تبلیغاتی و دیوارهای نیمه‌کاره.",
                "باز کردن پنجره برای تماشای طوفان."
            ),
            safetyWarnings = listOf(
                "هشدار: تندبادهای ناگهانی می‌توانند اشیای سنگین را پرتاب کنند؛ تنها در فضای سرپوشیده ایمن بمانید."
            ),
            recommendedTrainingId = "crisis_stress_management"
        ),
        CrisisType.SNOW_BLIZZARD to CrisisGuide(
            type = CrisisType.SNOW_BLIZZARD,
            beforeActions = listOf(
                "زنجیر چرخ، پتو، جیره خشک، آب، بیلچه و پاوربانک در خودرو قرار دهید.",
                "از سلامت بخاری‌ها و دودکش‌ها برای پیشگیری از مسمومیت با مونوکسید کربن مطمئن شوید.",
                "عایق‌بندی لوله‌های آب در معرض یخ‌زدگی را انجام دهید."
            ),
            duringActions = listOf(
                "از سفرهای غیرضروری بین‌شهری خودداری کنید و در صورت گرفتارشدن در کولاک داخل خودرو بمانید.",
                "اگر موتور خودرو روشن است، لوله اگزوز را از برف پاک نگه دارید و پنجره را کمی باز بگذارید تا مونوکسید وارد نشود.",
                "لباس چندلایه و خشک بپوشید و با حرکت ملایم دست و پا جریان خون را حفظ کنید."
            ),
            afterActions = listOf(
                "در برف‌روبی بام و معابر مراقب سقوط بهمن کوچک از شیروانی‌ها و لیزخوردن باشید.",
                "به آرامی فرد دچار سرمازدگی را به محیط گرم ببرید و از مالش مستقیم پوست یخ‌زده پرهیز کنید.",
                "وضعیت لوله‌های آب و گرمایش را از نظر ترکیدگی بررسی کنید."
            ),
            commonMistakes = listOf(
                "ترک کردن خودرو در کولاک شدید و دید صفر که خطر گم‌شدن و یخ‌زدگی دارد.",
                "استفاده از ذغال یا اجاق غیراستاندارد در فضای بسته و بدون تهویه.",
                "ریختن آب داغ روی دست و پای سرمازده."
            ),
            safetyWarnings = listOf(
                "هشدار: مسمومیت با مونوکسید کربن بی‌بو و بی‌رنگ است؛ تهویه ایمن وسایل گرمایشی حیاتی است."
            ),
            recommendedTrainingId = "first_aid_co_poisoning"
        ),
        CrisisType.POWER_OUTAGE to CrisisGuide(
            type = CrisisType.POWER_OUTAGE,
            beforeActions = listOf(
                "چراغ‌قوه شارژی یا باتری‌خور و پاوربانک پرشده در دسترس داشته باشید.",
                "برای بیماران وابسته به تجهیزات برقی، برنامه پشتیبان و باتری ذخیره یا هماهنگی با مرکز درمانی داشته باشید.",
                "شماره حوادث برق (۱۲۱) و موقعیت کلیدهای فیوز تابلو برق را بدانید."
            ),
            duringActions = listOf(
                "وسایل برقی حساس مثل یخچال، کولر و تلویزیون را از پریز بکشید تا هنگام وصل ناگهانی برق آسیب نبینند.",
                "در یخچال و فریزر را تا حد امکان بسته نگه دارید تا سرمای داخل حفظ شود.",
                "از چراغ‌قوه به جای شمع استفاده کنید؛ گوشی را روی حالت صرفه‌جویی باتری بگذارید."
            ),
            afterActions = listOf(
                "پس از وصل برق، وسایل برقی را با فاصله چند دقیقه و به تدریج متصل کنید.",
                "مواد غذایی یخچال را که بیش از ۴ ساعت در دمای بالا مانده‌اند از نظر سلامت بررسی کنید.",
                "در صورت نوسان شدید برق یا بوی سوختگی از تابلو، کلید اصلی را قطع و با ۱۲۱ تماس بگیرید."
            ),
            commonMistakes = listOf(
                "روشن کردن شمع در کنار پرده، کاغذ یا هنگام خواب.",
                "استفاده از ژنراتور بنزینی یا گازی داخل خانه، پارکینگ یا بالکن بسته (خطر مرگبار مونوکسید).",
                "باز و بسته کردن مداوم در یخچال."
            ),
            safetyWarnings = listOf(
                "هشدار: هرگز ژنراتور را در فضای سرپوشیده روشن نکنید؛ گاز مونوکسید کربن کشنده است."
            ),
            recommendedTrainingId = "crisis_stress_management"
        ),
        CrisisType.AIR_POLLUTION to CrisisGuide(
            type = CrisisType.AIR_POLLUTION,
            beforeActions = listOf(
                "ماسک استاندارد فیلتردار (N95 یا FFP2) به تعداد کافی برای اعضای خانواده تهیه کنید.",
                "فیلترهای دستگاه تصفیه هوا یا سیستم تهویه منزل را به موقع تعویض کنید.",
                "داروهای استنشاقی بیماران تنفسی و قلبی را همیشه در دسترس نگه دارید."
            ),
            duringActions = listOf(
                "درهای بالکن و پنجره‌ها را بسته نگه دارید و از تردد غیرضروری در فضای باز خودداری کنید.",
                "از ورزش سنگین در فضای باز بپرهیزید؛ تنفس عمیق ذرات معلق بیشتری وارد ریه می‌کند.",
                "کودکان، سالمندان، زنان باردار و بیماران قلبی-تنفسی حتماً در محیط بسته بمانند."
            ),
            afterActions = listOf(
                "پس از بهبود شاخص کیفیت هوا، منزل را با باز کردن کوتاه‌مدت پنجره‌ها تهویه کنید.",
                "مصرف آب و مایعات کافی را برای پاکسازی مجاری تنفسی ادامه دهید.",
                "در صورت تنگی نفس، سرفه مداوم یا درد قفسه سینه به پزشک مراجعه کنید."
            ),
            commonMistakes = listOf(
                "استفاده از ماسک پارچه‌ای ساده یا ماسک جراحی شل به جای ماسک N95 در هوای بسیار ناسالم.",
                "پیاده‌روی و دویدن صبحگاهی در روزهای وارونگی دما و شاخص بالا.",
                "پخت‌وپز با دود زیاد یا مصرف دخانیات در فضای بسته."
            ),
            safetyWarnings = listOf(
                "هشدار: بیماران آسمی و قلبی در روزهای شاخص ناسالم باید برنامه درمانی خود را دقیق دنبال کنند."
            ),
            recommendedTrainingId = "first_aid_choking"
        ),
        CrisisType.URBAN_INCIDENT to CrisisGuide(
            type = CrisisType.URBAN_INCIDENT,
            beforeActions = listOf(
                "شماره‌های ضروری شهری (۱۱۵، ۱۲۵، ۱۱۰، ۱۹۴، ۱۲۱، ۱۲۲) را در حافظه و کارت جیبی داشته باشید.",
                "شیر قطع گاز و فیوز برق واحد خود را علامت‌گذاری کنید.",
                "مسیرهای جایگزین تردد در محله را بشناسید."
            ),
            duringActions = listOf(
                "در نشت گاز: هیچ کلید برقی را روشن یا خاموش نکنید، پنجره‌ها را باز کنید، شیر گاز را ببندید و از ساختمان خارج شوید.",
                "در تصادفات یا ریزش سازه: از تجمع در محل حادثه خودداری کنید تا مسیر خودروهای امدادی باز بماند.",
                "در حوادث زیرساختی یا گودبرداری، محیط خطر را با فاصله ایمن ترک کنید."
            ),
            afterActions = listOf(
                "از انتشار اخبار تأییدنشده و شایعات خودداری کنید و تنها به منابع رسمی اعتماد کنید.",
                "به نیروهای امدادی اجازه دهید عملیات ایمن‌سازی را کامل انجام دهند."
            ),
            commonMistakes = listOf(
                "تجمع و توقف خودرو برای فیلم‌برداری از حوادث شهری و بستن مسیر آمبولانس و آتش‌نشانی.",
                "فشار دادن زنگ در یا روشن کردن چراغ هنگام استشمام بوی شدید گاز.",
                "اقدام به جابه‌جایی مصدوم تصادف با آسیب گردن یا ستون فقرات بدون مهارت تخصصی."
            ),
            safetyWarnings = listOf(
                "هشدار: کوچک‌ترین جرقه الکتریکی در محیط آلوده به گاز می‌تواند باعث انفجار شود."
            ),
            recommendedTrainingId = "first_aid_fracture"
        )
    )

    fun defaultCrisisChecklists(): List<CrisisChecklistEntity> {
        val list = mutableListOf<CrisisChecklistEntity>()
        var order = 0
        fun add(crisis: CrisisType, vararg items: String) {
            items.forEach { text ->
                list.add(
                    CrisisChecklistEntity(
                        crisisId = crisis.id,
                        title = text,
                        isChecked = false,
                        sortOrder = order++
                    )
                )
            }
        }
        add(
            CrisisType.EARTHQUAKE,
            "مهار کردن کتابخانه، کمد و تلویزیون به دیوار",
            "تعیین نقطه امن پناه‌گیری در هر اتاق",
            "آشنایی همه اعضا با محل قطع شیر گاز و برق",
            "قرار دادن کفش و چراغ‌قوه کنار تخت خواب"
        )
        add(
            CrisisType.FLOOD,
            "پاکسازی ناودان‌ها و کف‌شوی‌های پشت‌بام و حیاط",
            "قرار دادن مدارک مهم در کاور ضدآب",
            "شناسایی نقطه مرتفع امن نزدیک منزل"
        )
        add(
            CrisisType.FIRE,
            "تهیه و بررسی تاریخ شارژ کپسول آتش‌نشانی",
            "تست باتری حسگر دود منزل",
            "باز نگه داشتن راهرو و مسیر خروج اضطراری"
        )
        add(
            CrisisType.STORM,
            "محکم‌کردن گلدان‌ها و وسایل بالکن",
            "شارژ کامل پاوربانک و چراغ‌قوه اضطراری",
            "بررسی شاخه‌های خشک مشرف به پنجره"
        )
        add(
            CrisisType.SNOW_BLIZZARD,
            "قرار دادن زنجیر چرخ و پتوی اضافه در خودرو",
            "بررسی دودکش و تهویه وسایل گرمایشی",
            "عایق‌کاری لوله‌های آب در فضای باز"
        )
        add(
            CrisisType.POWER_OUTAGE,
            "آماده‌سازی چراغ اضطراری و باتری یدک",
            "تهیه محافظ برق استاندارد برای یخچال",
            "ثبت شماره ۱۲۱ حوادث برق در دسترسی سریع"
        )
        add(
            CrisisType.AIR_POLLUTION,
            "تهیه ماسک N95 / FFP2 برای اعضای خانواده",
            "بررسی فیلتر تصفیه هوا و درزگیر پنجره",
            "تأمین ذخیره داروی بیماران تنفسی"
        )
        add(
            CrisisType.URBAN_INCIDENT,
            "برچسب‌زدن روی شیر اصلی گاز و کلید فیوز",
            "یادداشت شماره‌های ۱۱۵، ۱۲۵، ۱۱۰ و ۱۹۴",
            "آموزش عدم استفاده از کلید برق هنگام بوی گاز"
        )
        return list
    }

    fun defaultKitItems(): List<EmergencyKitItemEntity> = listOf(
        EmergencyKitItemEntity(name = "آب آشامیدنی بسته‌بندی (حداقل ۳ لیتر برای هر نفر در روز)", category = KitCategory.FOOD_WATER, quantity = "۹ لیتر / نفر", isPrepared = false),
        EmergencyKitItemEntity(name = "کنسروهای آماده مصرف بدون نیاز به پخت + دربازکن", category = KitCategory.FOOD_WATER, quantity = "۶ عدد", isPrepared = false),
        EmergencyKitItemEntity(name = "خرما، مغزیجات، بیسکویت خشک و غذای پرانرژی", category = KitCategory.FOOD_WATER, quantity = "۳ بسته", isPrepared = false),
        EmergencyKitItemEntity(name = "غذای ویژه نوزاد، سالمند یا بیمار در صورت نیاز", category = KitCategory.FOOD_WATER, quantity = "به میزان لازم", isPrepared = false),

        EmergencyKitItemEntity(name = "باند کشی، گاز استریل، چسب زخم و بتادین", category = KitCategory.FIRST_AID, quantity = "۱ ست کامل", isPrepared = false),
        EmergencyKitItemEntity(name = "داروهای شخصی ضروری خانواده (حداقل برای ۷ روز)", category = KitCategory.FIRST_AID, quantity = "نسخه ۷ روزه", isPrepared = false),
        EmergencyKitItemEntity(name = "مسکن ساده، قرص ORS و پماد سوختگی", category = KitCategory.FIRST_AID, quantity = "۱ بسته", isPrepared = false),
        EmergencyKitItemEntity(name = "قیچی پانسمان، پنس، دستکش لاتکس و دماسنج", category = KitCategory.FIRST_AID, quantity = "۱ ست", isPrepared = false),

        EmergencyKitItemEntity(name = "چراغ‌قوه LED با باتری اضافه یا شارژی", category = KitCategory.COMMUNICATION, quantity = "۲ عدد", isPrepared = false),
        EmergencyKitItemEntity(name = "پاوربانک پرشده و کابل شارژ مقاوم", category = KitCategory.COMMUNICATION, quantity = "۱ عدد (۲۰ هزار)", isPrepared = false),
        EmergencyKitItemEntity(name = "سوت امدادی برای اعلام موقعیت زیر آوار", category = KitCategory.COMMUNICATION, quantity = "۱ عدد برای هر نفر", isPrepared = false),
        EmergencyKitItemEntity(name = "رادیوی جیبی باتری‌خور برای دریافت اخبار رسمی در قطعی اینترنت", category = KitCategory.COMMUNICATION, quantity = "۱ دستگاه", isPrepared = false),
        EmergencyKitItemEntity(name = "کپی مدارک هویتی، پول نقد و شماره تماس‌های خانواده در کاور ضدآب", category = KitCategory.COMMUNICATION, quantity = "۱ پوشه", isPrepared = false),

        EmergencyKitItemEntity(name = "ماسک تنفسی استاندارد N95 برای گردوغبار و دود", category = KitCategory.HYGIENE, quantity = "۱۰ عدد", isPrepared = false),
        EmergencyKitItemEntity(name = "ژل ضدعفونی‌کننده دست و دستمال مرطوب", category = KitCategory.HYGIENE, quantity = "۲ عدد", isPrepared = false),
        EmergencyKitItemEntity(name = "کیسه زباله مقاوم، دستمال کاغذی و اقلام بهداشتی شخصی", category = KitCategory.HYGIENE, quantity = "۱ بسته", isPrepared = false),
        EmergencyKitItemEntity(name = "پتوی فویل حرارتی (پتوی نجات) و پانچو بارانی", category = KitCategory.HYGIENE, quantity = "۲ عدد", isPrepared = false)
    )

    // All 7 required First Aid & Crisis training lessons
    val trainingLessons: List<TrainingLesson> = listOf(
        TrainingLesson(
            id = "first_aid_bleeding",
            mediaId = "vid_first_aid_bleeding",
            title = "۱. کنترل خونریزی و پانسمان اضطراری",
            category = "کمک‌های اولیه",
            durationMinutes = 4,
            summary = "اصول پایه مهار خونریزی با فشار مستقیم و تمیز تا رسیدن نیروهای اورژانس.",
            steps = listOf(
                "ابتدا ایمنی صحنه را بررسی کنید و در صورت دسترسی دستکش یک‌بارمصرف بپوشید.",
                "با گاز استریل یا پارچه تمیز، فشار مستقیم و مداوم روی محل زخم وارد کنید.",
                "در صورت خیس‌شدن پانسمان، لایه اول را برندارید؛ لایه جدید روی آن اضافه کنید تا لخته تخریب نشود.",
                "اگر آسیب در اندام است و شکستگی مشکوک نیست، عضو را آرام بالاتر از سطح قلب نگه دارید."
            ),
            warningNote = "از بستن شریان‌بند (تورنیکه) بدون آموزش تخصصی پرهیز کنید؛ این آموزش جایگزین دوره‌های عملی امداد نیست.",
            sourceNote = "اصول عمومی کمک‌های اولیه جمعیت هلال احمر و دستورالعمل پایه صلیب سرخ (IFRC)",
            lastReviewedDate = "بازبینی علمی: آذر ۱۴۰۵",
            relatedCrisis = CrisisType.EARTHQUAKE,
            relatedQuizTopicId = "quiz_bleeding"
        ),
        TrainingLesson(
            id = "first_aid_cpr",
            mediaId = "vid_first_aid_cpr",
            title = "۲. احیای قلبی-ریوی پایه (CPR)",
            category = "کمک‌های اولیه",
            durationMinutes = 6,
            summary = "ارزیابی هوشیاری، تماس فوری با ۱۱۵ و مراحل ماساژ قلبی استاندارد برای بزرگسالان.",
            steps = listOf(
                "ایمنی محیط را بسنجید، به آرامی به شانه فرد ضربه بزنید و با صدای بلند او را صدا کنید.",
                "در صورت عدم پاسخ و نبود تنفس طبیعی، فوراً با ۱۱۵ تماس بگیرید و بلندگو را فعال کنید.",
                "پاشنه یک دست را در مرکز قفسه سینه بگذارید و دست دیگر را روی آن قفل کنید.",
                "با بازوهای صاف، فشار عمودی و منظم (حدود ۱۰۰ تا ۱۲۰ بار در دقیقه، عمق ۵ سانتی‌متر) تا رسیدن امداد ادامه دهید."
            ),
            warningNote = "در فرد هوشیار یا دارای تنفس طبیعی هرگز ماساژ قلبی انجام ندهید و حتماً برای تسلط عملی در دوره‌های حضوری شرکت کنید.",
            sourceNote = "راهنمای احیای پایه عمومی جمعیت هلال احمر و اصول AHA برای عموم",
            lastReviewedDate = "بازبینی علمی: آذر ۱۴۰۵",
            relatedCrisis = CrisisType.FLOOD,
            relatedQuizTopicId = "quiz_cpr"
        ),
        TrainingLesson(
            id = "first_aid_burns",
            mediaId = "vid_first_aid_burns",
            title = "۳. اقدامات صحیح در سوختگی",
            category = "کمک‌های اولیه",
            durationMinutes = 3,
            summary = "خنک‌سازی ایمن محل سوختگی و پرهیز از باورهای نادرست خانگی.",
            steps = listOf(
                "منبع حرارت را دور کرده و محل سوختگی را ۱۰ تا ۲۰ دقیقه زیر آب خنک جاری (نه یخ) قرار دهید.",
                "پیش از ایجاد تورم، ساعت، انگشتر و لباس غیرچسبیده به زخم را به آرامی جدا کنید.",
                "روی سوختگی را با پانسمان استریل یا پوشش تمیز غیرپرزدار به‌صورت آزاد بپوشانید.",
                "در سوختگی‌های وسیع، عمقی، الکتریکی یا ناحیه صورت و دست‌ها فوراً با ۱۱۵ تماس بگیرید."
            ),
            warningNote = "هرگز خمیردندان، یخ، کره، روغن یا پودر روی سوختگی نگذارید و تاول‌ها را نترکانید.",
            sourceNote = "دستورالعمل عمومی مراقبت‌های اولیه اورژانس و هلال احمر",
            lastReviewedDate = "بازبینی علمی: آذر ۱۴۰۵",
            relatedCrisis = CrisisType.FIRE,
            relatedQuizTopicId = "quiz_burns"
        ),
        TrainingLesson(
            id = "first_aid_fracture",
            mediaId = "vid_first_aid_fracture",
            title = "۴. شکستگی و آتل‌بندی ایمن",
            category = "کمک‌های اولیه",
            durationMinutes = 5,
            summary = "اصول بی‌حرکت‌سازی عضو آسیب‌دیده و حفاظت از آسیب‌های احتمالی ستون فقرات.",
            steps = listOf(
                "مصدوم مشکوک به آسیب گردن یا کمر را جابه‌جا نکنید مگر در خطر فوری مثل حریق یا ریزش آوار.",
                "عضو مشکوک به شکستگی را در همان وضعیت قرارگیری با آتل نرم یا تخته و باند ثابت کنید.",
                "طول آتل باید مفصل بالا و پایین محل شکستگی را بپوشاند بدون اینکه به جریان خون فشار بیاورد.",
                "انتهای انگشتان عضو بسته‌شده را از نظر رنگ، گرما و حس لمس بررسی کنید."
            ),
            warningNote = "هرگز تلاش نکنید استخوان جابه‌جاشده یا بیرون‌زده را جا بیندازید.",
            sourceNote = "آموزش عمومی امداد و نجات جمعیت هلال احمر",
            lastReviewedDate = "بازبینی علمی: آذر ۱۴۰۵",
            relatedCrisis = CrisisType.URBAN_INCIDENT,
            relatedQuizTopicId = "quiz_fracture"
        ),
        TrainingLesson(
            id = "first_aid_choking",
            mediaId = "vid_first_aid_choking",
            title = "۵. برخورد با خفگی (انسداد راه هوایی)",
            category = "کمک‌های اولیه",
            durationMinutes = 4,
            summary = "تشخیص انسداد کامل راه هوایی و اجرای ایمن ضربه بین کتف‌ها و مانور فشار شکمی.",
            steps = listOf(
                "اگر فرد قادر به سرفه قوی و صحبت است، او را به ادامه سرفه تشویق کنید و به پشتش ضربه نزنید.",
                "اگر نمی‌تواند نفس بکشد، سرفه کند یا حرف بزند (علامت دست روی گلو)، فوراً کمک بخواهید.",
                "در بزرگسال هوشیار: ۵ ضربه محکم با پاشنه دست بین دو کتف وارد کنید (بدن کمی به جلو خم شود).",
                "در صورت باز نشدن راه هوایی، تا ۵ فشار شکمی (مانور هایملیخ) بالای ناف و رو به داخل و بالا انجام دهید."
            ),
            warningNote = "برای نوزادان زیر یک سال از فشار شکمی استفاده نکنید؛ روش ویژه نوزاد تنها با آموزش تخصصی و نظارت انجام شود.",
            sourceNote = "دستورالعمل امداد پایه انسداد راه هوایی (هلال احمر / IFRC)",
            lastReviewedDate = "بازبینی علمی: آذر ۱۴۰۵",
            relatedCrisis = CrisisType.AIR_POLLUTION,
            relatedQuizTopicId = "quiz_choking"
        ),
        TrainingLesson(
            id = "first_aid_co_poisoning",
            mediaId = "vid_first_aid_co_poisoning",
            title = "۶. مسمومیت با مونوکسید کربن (CO)",
            category = "کمک‌های اولیه",
            durationMinutes = 4,
            summary = "شناخت نشانه‌های قاتل خاموش، خروج فوری به هوای آزاد و پیشگیری در فصل سرد.",
            steps = listOf(
                "نشانه‌های هشدار شامل سردرد، سرگیجه، تهوع، ضعف و خواب‌آلودگی ناگهانی چند نفر در یک فضاست.",
                "بلافاصله درها و پنجره‌ها را باز کرده و همه افراد را به هوای آزاد منتقل کنید.",
                "وسیله گرمایشی مشکوک را خاموش کنید و در صورت احتمال نشت گاز شهری از زدن کلید برق خودداری کنید.",
                "فوراً با ۱۱۵ تماس بگیرید؛ حتی پس از بهبود ظاهری، بررسی پزشکی و اکسیژن‌درمانی ضروری است."
            ),
            warningNote = "مونوکسید کربن بی‌رنگ و بی‌بوست؛ بوی گاز شهری نشانه گاز متان/مرکاپتان است و نبود بو به معنای نبود CO نیست.",
            sourceNote = "بخشنامه ایمنی زمستانی اورژانس کشور و علوم پایه تنفسی",
            lastReviewedDate = "بازبینی علمی: آذر ۱۴۰۵",
            relatedCrisis = CrisisType.SNOW_BLIZZARD,
            relatedQuizTopicId = "quiz_co_poisoning"
        ),
        TrainingLesson(
            id = "crisis_stress_management",
            mediaId = "vid_crisis_stress",
            title = "۷. مدیریت استرس و تصمیم‌گیری در بحران",
            category = "آمادگی روانی و بحران",
            durationMinutes = 5,
            summary = "تکنیک‌های آرام‌سازی فوری در ۷۲ ساعت اول و تصمیم‌گیری منطقی برای خود و خانواده.",
            steps = listOf(
                "پیش از هر اقدام شتاب‌زده، ۱۰ ثانیه توقف کنید و ۳ نفس عمیق شکمی (۴ ثانیه دم، ۴ ثانیه بازدم آرام) بکشید.",
                "اولویت‌ها را به ترتیب چک کنید: ۱. ایمنی فوری (آتش، آوار، گاز، برق)، ۲. بررسی سلامت اعضا، ۳. خروج ایمن.",
                "در زمان شلوغی شبکه، به جای تماس طولانی از پیامک کوتاه استفاده کنید.",
                "با لحن آرام و جملات کوتاه به کودکان و سالمندان احساس امنیت و جهت بدهید."
            ),
            warningNote = "تصمیم‌گیری عجولانه ناشی از ترس (مانند دویدن در راه‌پله هنگام زلزله) اصلی‌ترین عامل آسیب در حوادث است.",
            sourceNote = "اصول حمایت روانی اولیه (PFA) و تاب‌آوری در بحران",
            lastReviewedDate = "بازبینی علمی: آذر ۱۴۰۵",
            relatedCrisis = CrisisType.POWER_OUTAGE,
            relatedQuizTopicId = "quiz_stress"
        )
    )

    // Transparently declared offline media inventory: no fake URLs!
    fun defaultOfflineMedia(): List<OfflineMediaEntity> = trainingLessons.map { lesson ->
        OfflineMediaEntity(
            mediaId = lesson.mediaId,
            category = MediaCategory.FIRST_AID,
            title = "ویدئوی آموزشی: ${lesson.title}",
            description = lesson.summary,
            sourceAttribution = "${lesson.sourceNote} • ${lesson.lastReviewedDate}",
            streamUrl = null, // No unverified fake URL; ready for verified CMS/CDN or local MP4 import
            localFilePath = null,
            status = MediaDownloadStatus.NOT_LINKED,
            durationLabel = "${lesson.durationMinutes} دقیقه"
        )
    }

    val quizTopics: List<QuizTopic> = listOf(
        QuizTopic("quiz_bleeding", "آزمون کنترل خونریزی", "مهارت پانسمان و فشار مستقیم", CrisisType.EARTHQUAKE),
        QuizTopic("quiz_cpr", "آزمون احیای قلبی-ریوی", "تشخیص توقف تنفس و ماساژ قلبی", CrisisType.FLOOD),
        QuizTopic("quiz_burns", "آزمون مراقبت سوختگی", "اقدامات فوری و باورهای نادرست", CrisisType.FIRE),
        QuizTopic("quiz_fracture", "آزمون شکستگی و آتل‌بندی", "بی‌حرکت‌سازی و حفاظت ستون فقرات", CrisisType.URBAN_INCIDENT),
        QuizTopic("quiz_choking", "آزمون رفع خفگی", "تشخیص انسداد و اقدام صحیح", CrisisType.AIR_POLLUTION),
        QuizTopic("quiz_co_poisoning", "آزمون مسمومیت مونوکسید کربن", "علائم و خروج ایمن", CrisisType.SNOW_BLIZZARD),
        QuizTopic("quiz_stress", "آزمون تصمیم‌گیری در بحران", "آرامش، ارتباط و اولویت‌بندی", CrisisType.POWER_OUTAGE),
        QuizTopic("quiz_earthquake", "آزمون جامع زلزله", "واکنش ایمن پیش، حین و پس از لرزه", CrisisType.EARTHQUAKE)
    )

    val quizQuestions: List<QuizQuestion> = listOf(
        QuizQuestion(
            id = "q_bl_1", topicId = "quiz_bleeding",
            question = "اگر گاز استریل روی زخم دچار خونریزی کاملاً خیس شد، چه اقدامی صحیح است؟",
            options = listOf(
                "گاز اول را بردارید و گاز خشک بگذارید",
                "گاز اول را برندارید و لایه تمیز جدیدی روی آن بگذارید و فشار دهید",
                "زخم را با آب گرم بشویید",
                "بلافاصله پودر قهوه روی زخم بریزید"
            ),
            correctIndex = 1,
            explanation = "برداشتن لایه اول باعث کنده شدن لخته‌های اولیه و بازگشت خونریزی شدید می‌شود."
        ),
        QuizQuestion(
            id = "q_cpr_1", topicId = "quiz_cpr",
            question = "آهنگ استاندارد فشار قفسه سینه در احیای قلبی-ریوی پایه بزرگسالان چند بار در دقیقه است؟",
            options = listOf("۳۰ تا ۴۰ بار", "۶۰ بار", "۱۰۰ تا ۱۲۰ بار", "۲۰۰ بار"),
            correctIndex = 2,
            explanation = "سرعت مناسب ماساژ قلبی حدود ۱۰۰ تا ۱۲۰ فشار در دقیقه با بازگشت کامل قفسه سینه است."
        ),
        QuizQuestion(
            id = "q_bu_1", topicId = "quiz_burns",
            question = "بهترین اقدام در دقایق اولیه سوختگی حرارتی سطحی چیست؟",
            options = listOf(
                "مالیدن خمیردندان یا کره",
                "قرار دادن یخ مستقیم روی پوست",
                "گرفتن محل زیر آب خنک جاری برای ۱۰ تا ۲۰ دقیقه",
                "ترکاندن تاول‌ها"
            ),
            correctIndex = 2,
            explanation = "آب خنک جاری حرارت باقی‌مانده در بافت را کاهش می‌دهد؛ یخ و خمیردندان به بافت آسیب می‌زنند."
        ),
        QuizQuestion(
            id = "q_fr_1", topicId = "quiz_fracture",
            question = "طول آتل مناسب برای بی‌حرکت‌سازی یک استخوان شکسته باید چگونه باشد؟",
            options = listOf(
                "فقط روی محل شکستگی قرار بگیرد",
                "مفصل بالا و پایین استخوان آسیب‌دیده را دربرگیرد",
                "به حدی سفت بسته شود که نبض قطع شود",
                "ابتدا استخوان را جا بیندازیم و بعد ببندیم"
            ),
            correctIndex = 1,
            explanation = "آتل باید مفاصل دو طرف شکستگی را بی‌حرکت کند بدون آنکه جریان خون را مسدود نماید."
        ),
        QuizQuestion(
            id = "q_ch_1", topicId = "quiz_choking",
            question = "اگر فردی هنگام غذا خوردن سرفه‌های قوی و صدادار می‌کند، صحیح‌ترین کار چیست؟",
            options = listOf(
                "فوراً ضربه محکم به کمرش بزنیم",
                "او را به ادامه سرفه تشویق کنیم و آرام بمانیم",
                "به او آب فراوان بدهیم",
                "انگشت را داخل گلویش ببریم"
            ),
            correctIndex = 1,
            explanation = "سرفه قوی نشانه انسداد ناقص است و مؤثرترین راه خروج جسم خارجی همان سرفه طبیعی است."
        ),
        QuizQuestion(
            id = "q_co_1", topicId = "quiz_co_poisoning",
            question = "کدام علامت زنگ خطر مسمومیت هم‌زمان با مونوکسید کربن در خانه است؟",
            options = listOf(
                "سردرد، سرگیجه، تهوع و خواب‌آلودگی هم‌زمان چند عضو خانواده",
                "بوی تند و تیز گاز آشپزخانه",
                "سرد شدن بیش از حد اتاق",
                "احساس گرسنگی ناگهانی"
            ),
            correctIndex = 0,
            explanation = "مونوکسید کربن بی‌بو و بی‌رنگ است و با کاهش اکسیژن‌رسانی به مغز باعث سردرد و خواب‌آلودگی جمعی می‌شود."
        ),
        QuizQuestion(
            id = "q_st_1", topicId = "quiz_stress",
            question = "در ترافیک شدید مخابراتی پس از بحران، کدام روش ارتباطی شانس موفقیت بالاتری دارد؟",
            options = listOf(
                "تماس صوتی مکرر و طولانی",
                "ارسال پیامک کوتاه متنی",
                "تماس تصویری",
                "انتشار شایعات برای اطلاع‌رسانی"
            ),
            correctIndex = 1,
            explanation = "پیامک متنی پهنای باند بسیار کمی مصرف می‌کند و در شبکه‌های شلوغ بهتر ارسال می‌شود."
        ),
        QuizQuestion(
            id = "q_eq_1", topicId = "quiz_earthquake",
            question = "هنگام وقوع زلزله در طبقه سوم ساختمان، بهترین واکنش فوری چیست؟",
            options = listOf(
                "دویدن به سمت راه‌پله",
                "پناه‌گیری زیر میز محکم یا کنار ستون داخلی و پوشاندن سر و گردن",
                "استفاده از آسانسور",
                "ایستادن کنار پنجره شیشه‌ای"
            ),
            correctIndex = 1,
            explanation = "پناه‌گیری در جای امن تا توقف لرزش از آسیب ناشی از سقوط اشیا و سقوط در پله‌ها جلوگیری می‌کند."
        )
    )
}
