package com.example.data.seed

import com.example.data.local.entity.CrisisChecklistEntity
import com.example.data.local.entity.EmergencyKitItemEntity
import com.example.data.local.entity.KitCategory
import com.example.data.local.entity.ShelterPointEntity
import com.example.data.local.entity.ShelterType
import com.example.data.local.entity.UrbanReportCategory
import com.example.data.local.entity.UrbanReportEntity
import com.example.data.local.entity.UrbanReportStatus
import com.example.data.model.CrisisGuide
import com.example.data.model.CrisisType
import com.example.data.model.EmergencyContactNumber
import com.example.data.model.QuizQuestion
import com.example.data.model.QuizTopic
import com.example.data.model.SampleAdvisory
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

    val sampleAdvisories: List<SampleAdvisory> = listOf(
        SampleAdvisory(
            id = "demo_adv_1",
            title = "نمونه اطلاعیه تمرینی آمادگی زلزله",
            body = "این مورد یک نمونه آزمایشی برای نمایش ساختار اطلاعیه‌هاست. در نسخه بدون اتصال به سرور رسمی، هیچ هشدار واقعی دریافت نشده است.",
            levelLabel = "داده آزمایشی / نمونه",
            isDemoData = true
        ),
        SampleAdvisory(
            id = "demo_adv_2",
            title = "نمونه توصیه پیشگیرانه بارندگی و آب‌گرفتگی",
            body = "پاکسازی آبروها و بررسی کیف اضطراری به صورت دوره‌ای توصیه می‌شود. اطلاعات رسمی را تنها از منابع معتبر پیگیری کنید.",
            levelLabel = "داده آزمایشی / نمونه",
            isDemoData = true
        )
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
                "هشدار: اگر لباس شما آتش گرفت، ندوید! بایستید، روی زمین دراز بکشید و بغلتید (Stop, Drop, Roll).",
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
                "سقف، دودکش‌ها و شاخه‌های شکسته اطراف منزل را به دقت بررسی کنید.",
                "خسارات شهری و انسداد معابر را از بخش گزارش مشکلات شهری ثبت کنید."
            ),
            commonMistakes = listOf(
                "پناه‌گرفتن زیر درختان تنومند هنگام رعدوبرق.",
                "تردد نزدیک داربست‌ها، تابلوهای تبلیغاتی و دیوارهای نیمه‌کاره.",
                "باز کردن پنجره برای تماشای طوفان."
            ),
            safetyWarnings = listOf(
                "هشدار: تندبادهای ناگهانی می‌توانند اشیای سنگین را پرتاب کنند؛ تنها در فضای سرپوشیده ایمن بمانید."
            ),
            recommendedTrainingId = "crisis_safety_principles"
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
            recommendedTrainingId = "first_aid_poisoning"
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
            recommendedTrainingId = "crisis_safety_principles"
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
            recommendedTrainingId = "crisis_safety_principles"
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
                "در حوادث زیرساختی یا گودبرداری، محیط خطر را با فاصله ایمن ترک کرده و گزارش ثبت کنید."
            ),
            afterActions = listOf(
                "از انتشار اخبار تأییدنشده و شایعات خودداری کنید و تنها به منابع رسمی اعتماد کنید.",
                "در صورت مشاهده انسداد یا خرابی معبر، گزارش مستند در بخش گزارش مشکلات شهری ثبت نمایید.",
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
        // غذایی و آب
        EmergencyKitItemEntity(name = "آب آشامیدنی بسته‌بندی (حداقل ۳ لیتر برای هر نفر در روز)", category = KitCategory.FOOD_WATER, quantity = "۹ لیتر / نفر", isPrepared = false),
        EmergencyKitItemEntity(name = "کنسروهای آماده مصرف بدون نیاز به پخت + دربازکن", category = KitCategory.FOOD_WATER, quantity = "۶ عدد", isPrepared = false),
        EmergencyKitItemEntity(name = "خرما، مغزیجات، بیسکویت خشک و غذای پرانرژی", category = KitCategory.FOOD_WATER, quantity = "۳ بسته", isPrepared = false),
        EmergencyKitItemEntity(name = "غذای ویژه نوزاد، سالمند یا بیمار در صورت نیاز", category = KitCategory.FOOD_WATER, quantity = "به میزان لازم", isPrepared = false),

        // کمک‌های اولیه و دارو
        EmergencyKitItemEntity(name = "باند کشی، گاز استریل، چسب زخم و بتادین", category = KitCategory.FIRST_AID, quantity = "۱ ست کامل", isPrepared = false),
        EmergencyKitItemEntity(name = "داروهای شخصی ضروری خانواده (حداقل برای ۷ روز)", category = KitCategory.FIRST_AID, quantity = "نسخه ۷ روزه", isPrepared = false),
        EmergencyKitItemEntity(name = "مسکن ساده، قرص ORS و پماد سوختگی", category = KitCategory.FIRST_AID, quantity = "۱ بسته", isPrepared = false),
        EmergencyKitItemEntity(name = "قیچی پانسمان، پنس، دستکش لاتکس و دماسنج", category = KitCategory.FIRST_AID, quantity = "۱ ست", isPrepared = false),

        // ارتباطی و ابزار
        EmergencyKitItemEntity(name = "چراغ‌قوه LED با باتری اضافه یا شارژی", category = KitCategory.COMMUNICATION, quantity = "۲ عدد", isPrepared = false),
        EmergencyKitItemEntity(name = "پاوربانک پرشده و کابل شارژ مقاوم", category = KitCategory.COMMUNICATION, quantity = "۱ عدد (۲۰ هزار)", isPrepared = false),
        EmergencyKitItemEntity(name = "سوت امدادی برای اعلام موقعیت زیر آوار", category = KitCategory.COMMUNICATION, quantity = "۱ عدد برای هر نفر", isPrepared = false),
        EmergencyKitItemEntity(name = "رادیوی جیبی باتری‌خور برای دریافت اخبار رسمی در قطعی اینترنت", category = KitCategory.COMMUNICATION, quantity = "۱ دستگاه", isPrepared = false),
        EmergencyKitItemEntity(name = "کپی مدارک هویتی، پول نقد و شماره تماس‌های خانواده در کاور ضدآب", category = KitCategory.COMMUNICATION, quantity = "۱ پوشه", isPrepared = false),

        // بهداشتی
        EmergencyKitItemEntity(name = "ماسک تنفسی استاندارد N95 برای گردوغبار و دود", category = KitCategory.HYGIENE, quantity = "۱۰ عدد", isPrepared = false),
        EmergencyKitItemEntity(name = "ژل ضدعفونی‌کننده دست و دستمال مرطوب", category = KitCategory.HYGIENE, quantity = "۲ عدد", isPrepared = false),
        EmergencyKitItemEntity(name = "کیسه زباله مقاوم، دستمال کاغذی و اقلام بهداشتی شخصی", category = KitCategory.HYGIENE, quantity = "۱ بسته", isPrepared = false),
        EmergencyKitItemEntity(name = "پتوی فویل حرارتی (پتوی نجات) و پانچو بارانی", category = KitCategory.HYGIENE, quantity = "۲ عدد", isPrepared = false)
    )

    fun defaultSampleShelters(nowMillis: Long): List<ShelterPointEntity> = listOf(
        ShelterPointEntity(
            name = "بیمارستان نمونه مرکزی (داده آزمایشی)",
            type = ShelterType.HOSPITAL,
            address = "منطقه مرکزی - بلوار سلامت (نقطه نمونه برای نمایش ساختار نقشه)",
            latitude = 35.7020,
            longitude = 51.3910,
            capacityNote = "بخش اورژانس فعال ۲۴ ساعته (نمونه)",
            phone = "115",
            isVerified = false,
            isDemoData = true,
            cachedAtMillis = nowMillis
        ),
        ShelterPointEntity(
            name = "پایگاه امداد هلال احمر منطقه (داده آزمایشی)",
            type = ShelterType.RELIEF_CENTER,
            address = "خیابان امداد، تقاطع میدان قدس (نقطه نمونه)",
            latitude = 35.6960,
            longitude = 51.4025,
            capacityNote = "اقلام امدادی و اسکان اضطراری (نمونه)",
            phone = "112",
            isVerified = false,
            isDemoData = true,
            cachedAtMillis = nowMillis
        ),
        ShelterPointEntity(
            name = "بوستان و فضای باز اسکان امن (داده آزمایشی)",
            type = ShelterType.SAFE_ZONE,
            address = "بلوار سبز، پارک بزرگ شهر (نقطه نمونه)",
            latitude = 35.7085,
            longitude = 51.3845,
            capacityNote = "فضای باز بدون ساختمان مرتفع اطراف (نمونه)",
            phone = "",
            isVerified = false,
            isDemoData = true,
            cachedAtMillis = nowMillis
        ),
        ShelterPointEntity(
            name = "ایستگاه آتش‌نشانی شماره ۱ (داده آزمایشی)",
            type = ShelterType.RELIEF_CENTER,
            address = "خیابان آزادی، نبش کوچه ایمنی (نقطه نمونه)",
            latitude = 35.6915,
            longitude = 51.3790,
            capacityNote = "واحد واکنش سریع نجات شهری (نمونه)",
            phone = "125",
            isVerified = false,
            isDemoData = true,
            cachedAtMillis = nowMillis
        )
    )

    fun defaultSampleReports(nowMillis: Long): List<UrbanReportEntity> = listOf(
        UrbanReportEntity(
            category = UrbanReportCategory.ROAD_BLOCKAGE,
            title = "نمونه گزارش: انسداد موقت خیابان فرعی",
            description = "این رکورد یک داده آزمایشی برای نمایش ساختار پیگیری گزارش‌هاست و وضعیت رسمی شهر را نشان نمی‌دهد.",
            imagePath = null,
            latitude = 35.7010,
            longitude = 51.3940,
            locationConsentGiven = false,
            status = UrbanReportStatus.SUBMITTED_UNVERIFIED,
            isDemoData = true,
            createdAtMillis = nowMillis - 3600_000L
        ),
        UrbanReportEntity(
            category = UrbanReportCategory.FLOODING,
            title = "نمونه گزارش: آب‌گرفتگی زیرگذر",
            description = "رکورد آزمایشی برای آزمایش فیلتر و برچسب‌گذاری اطلاعات تأییدنشده.",
            imagePath = null,
            latitude = 35.6945,
            longitude = 51.3880,
            locationConsentGiven = false,
            status = UrbanReportStatus.SUBMITTED_UNVERIFIED,
            isDemoData = true,
            createdAtMillis = nowMillis - 7200_000L
        )
    )

    val trainingLessons: List<TrainingLesson> = listOf(
        TrainingLesson(
            id = "first_aid_bleeding",
            title = "کنترل خونریزی و پانسمان اضطراری",
            category = "کمک‌های اولیه",
            durationMinutes = 4,
            summary = "اصول پایه مهار خونریزی شدید با فشار مستقیم تا رسیدن نیروهای اورژانس.",
            steps = listOf(
                "در صورت امکان دستکش یک‌بارمصرف بپوشید و ایمنی صحنه را بررسی کنید.",
                "با گاز استریل یا پارچه تمیز، فشار مستقیم و مداوم روی محل زخم وارد کنید.",
                "در صورت خیس‌شدن پانسمان، لایه اول را برندارید؛ لایه جدید روی آن بگذارید.",
                "اگر شکستگی مشکوک نیست، عضو آسیب‌دیده را کمی بالاتر از سطح قلب نگه دارید."
            ),
            warningNote = "از بستن شریان‌بند (تورنیکه) بدون آموزش تخصصی خودداری کنید مگر در خونریزی‌های کنترل‌نشدنی تهدیدکننده حیات.",
            sourceNote = "برگرفته از اصول عمومی کمک‌های اولیه هلال احمر و صلیب سرخ",
            relatedCrisis = CrisisType.EARTHQUAKE
        ),
        TrainingLesson(
            id = "first_aid_cpr",
            title = "آشنایی با احیای قلبی-ریوی پایه (CPR)",
            category = "کمک‌های اولیه",
            durationMinutes = 6,
            summary = "مراحل اولیه ارزیابی هوشیاری، تماس با ۱۱۵ و ماساژ قلبی برای افراد بالغ.",
            steps = listOf(
                "ایمنی محیط را چک کرده و با ضربه آرام به شانه و صدا زدن بلند، هوشیاری فرد را بسنجید.",
                "در صورت عدم پاسخ و نبود تنفس طبیعی، فوراً با ۱۱۵ تماس بگیرید و بلندگو را روشن کنید.",
                "کف یک دست را در مرکز قفسه سینه بگذارید و دست دیگر را روی آن قفل کنید.",
                "با آرنج صاف، فشار منظم و عمودی (حدود ۱۰۰ تا ۱۲۰ بار در دقیقه) وارد کنید تا امداد برسد."
            ),
            warningNote = "در فرد هوشیار یا دارای تنفس طبیعی ماساژ قلبی انجام ندهید.",
            sourceNote = "اصول عمومی احیای پایه جمعیت هلال احمر",
            relatedCrisis = CrisisType.FLOOD
        ),
        TrainingLesson(
            id = "first_aid_burns",
            title = "اقدامات صحیح در سوختگی‌های سطحی و متوسط",
            category = "کمک‌های اولیه",
            durationMinutes = 3,
            summary = "کارهایی که باید بلافاصله انجام دهید و باورهای غلطی که نباید اجرا شوند.",
            steps = listOf(
                "منبع حرارت را دور کرده و محل سوختگی را ۱۰ تا ۲۰ دقیقه زیر آب خنک (نه یخ) بگیرید.",
                "پیش از تورم، انگشتر، ساعت و لباس غیرچسبیده را به آرامی جدا کنید.",
                "روی سوختگی را با پوشش تمیز و غیرپرزدار به صورت آزاد بپوشانید.",
                "در سوختگی‌های وسیع، عمقی یا صورت/دست‌ها فوراً به اورژانس مراجعه کنید."
            ),
            warningNote = "هرگز از خمیردندان، یخ، کره یا روغن روی محل سوختگی استفاده نکنید و تاول‌ها را نترکانید.",
            sourceNote = "راهنمای عمومی مراقبت‌های اولیه اورژانس",
            relatedCrisis = CrisisType.FIRE
        ),
        TrainingLesson(
            id = "first_aid_fracture",
            title = "بی‌حرکت‌سازی شکستگی و مراقبت از آسیب ستون فقرات",
            category = "کمک‌های اولیه",
            durationMinutes = 4,
            summary = "نحوه صحیح حفاظت از عضو آسیب‌دیده در حوادث زلزله و تصادفات.",
            steps = listOf(
                "از جابه‌جا کردن مصدوم مشکوک به آسیب گردن یا کمر جز در خطر فوری (آتش/آوار) خودداری کنید.",
                "عضو مشکوک به شکستگی را در همان وضعیت که هست با آتل نرم یا تخته و باند ببندید.",
                "آتل باید مفصل بالا و پایین محل شکستگی را دربرگیرد بدون آنکه جریان خون را قطع کند.",
                "انگشتان عضو آتل‌بندی‌شده را از نظر رنگ‌پریدگی یا بی‌حسی کنترل کنید."
            ),
            warningNote = "هرگز سعی نکنید استخوان جابه‌جاشده یا بیرون‌زده را جا بیندازید.",
            sourceNote = "آموزش عمومی امداد و نجات",
            relatedCrisis = CrisisType.URBAN_INCIDENT
        ),
        TrainingLesson(
            id = "first_aid_poisoning",
            title = "برخورد با خفگی و مسمومیت گازی (مونوکسید کربن)",
            category = "ایمنی و امداد",
            durationMinutes = 4,
            summary = "شناسایی علائم مسمومیت خاموش با مونوکسید کربن و خروج ایمن.",
            steps = listOf(
                "در مسمومیت گازی بلافاصله در و پنجره‌ها را باز کرده و فرد را به هوای آزاد منتقل کنید.",
                "با ۱۱۵ تماس بگیرید و از روشن‌کردن وسایل برقی در صورت احتمال نشت گاز شهری خودداری کنید.",
                "در خفگی با جسم خارجی در بزرگسال هوشیار، از ضربه بین دو کتف و مانور هایملیخ استفاده کنید."
            ),
            warningNote = "علائم اولیه مسمومیت مونوکسید شامل سردرد، سرگیجه و تهوع است؛ سریع محیط را ترک کنید.",
            sourceNote = "دستورالعمل عمومی ایمنی زمستانی و اورژانس",
            relatedCrisis = CrisisType.SNOW_BLIZZARD
        ),
        TrainingLesson(
            id = "crisis_safety_principles",
            title = "اصول مدیریت استرس و تصمیم‌گیری در ۷۲ ساعت اول بحران",
            category = "آمادگی بحران",
            durationMinutes = 5,
            summary = "چگونه در زمان قطع برق و اینترنت، اطلاعات درست را حفظ کنیم و آرام بمانیم.",
            steps = listOf(
                "سه نفس عمیق بکشید و قبل از هر اقدام شتاب‌زده، خطرات فوری اطراف (آتش، برق، گاز) را بررسی کنید.",
                "به جای تماس‌های صوتی مکرر، از پیامک متنی کوتاه استفاده کنید چون در ترافیک شبکه شانس ارسال بیشتری دارد.",
                "از اطلاعات تأییدنشده شبکه‌های اجتماعی دوری کنید و رادیو یا پیام‌های رسمی را ملاک قرار دهید.",
                "موجودی آب و باتری تلفن همراه را سهمیه‌بندی کنید."
            ),
            warningNote = "حفظ آرامش شما به کودکان و سالمندان خانواده کمک می‌کند تصمیم‌های ایمن‌تری بگیرند.",
            sourceNote = "اصول تاب‌آوری شهری و مدیریت بحران خانواده",
            relatedCrisis = CrisisType.POWER_OUTAGE
        )
    )

    val quizTopics: List<QuizTopic> = listOf(
        QuizTopic("quiz_earthquake", "آزمون آمادگی زلزله", "سنجش واکنش صحیح هنگام لرزش و پس‌لرزه", CrisisType.EARTHQUAKE),
        QuizTopic("quiz_first_aid", "آزمون کمک‌های اولیه", "سنجش دانش کنترل خونریزی، سوختگی و مسمومیت", CrisisType.FIRE),
        QuizTopic("quiz_flood_urban", "آزمون سیل و حوادث شهری", "تصمیم‌گیری ایمن در آب‌گرفتگی و نشت گاز", CrisisType.FLOOD)
    )

    val quizQuestions: List<QuizQuestion> = listOf(
        QuizQuestion(
            id = "q_eq_1",
            topicId = "quiz_earthquake",
            question = "هنگام وقوع زلزله در طبقه سوم ساختمان، بهترین و ایمن‌ترین واکنش فوری چیست؟",
            options = listOf(
                "دویدن سریع به سمت راه‌پله و خروج",
                "پناه‌گیری زیر میز محکم یا کنار دیوار داخلی و پوشاندن سر و گردن",
                "استفاده از آسانسور برای رسیدن سریع به همکف",
                "ایستادن جلوی پنجره برای بررسی وضعیت خیابان"
            ),
            correctIndex = 1,
            explanation = "بیشترین آسیب‌ها هنگام دویدن به سمت پله‌ها و سقوط اشیا رخ می‌دهد؛ پناه‌گیری فوری تا پایان لرزش ایمن‌ترین کار است."
        ),
        QuizQuestion(
            id = "q_eq_2",
            topicId = "quiz_earthquake",
            question = "پس از توقف لرزش زلزله، برای اطلاع‌رسانی به اعضای خانواده چه روشی مناسب‌تر است؟",
            options = listOf(
                "ارسال پیامک کوتاه یا پیام متنی در شبکه‌های کم‌مصرف",
                "تماس صوتی طولانی با همه بستگان",
                "بازگشت فوری به ساختمان ترک‌خورده برای برداشتن وسایل",
                "استفاده از فندک در راه‌پله تاریک"
            ),
            correctIndex = 0,
            explanation = "پیامک پهنای باند بسیار کمتری نیاز دارد و در زمان شلوغی شبکه شانس ارسال بالاتری دارد."
        ),
        QuizQuestion(
            id = "q_eq_3",
            topicId = "quiz_earthquake",
            question = "چه زمانی باید شیر اصلی گاز را پس از زلزله قطع کرد؟",
            options = listOf(
                "فقط اگر بوی گاز یا صدای نشت یا آسیب مشکوک به لوله‌ها وجود دارد",
                "باید هر روز بدون دلیل قطع شود",
                "هنگام لرزش شدید قبل از پناه‌گیری",
                "فقط با روشن کردن کبریت و تست شعله"
            ),
            correctIndex = 0,
            explanation = "اگر بوی گاز حس کردید یا صدای نشت شنیدید، شیر اصلی را ببندید؛ وصل مجدد گاز باید با بررسی ایمنی انجام شود."
        ),
        QuizQuestion(
            id = "q_fa_1",
            topicId = "quiz_first_aid",
            question = "اولین اقدام صحیح برای سوختگی سطحی با آب داغ روی دست چیست؟",
            options = listOf(
                "مالیدن خمیردندان یا کره روی پوست",
                "گذاشتن مستقیم تکه‌های یخ روی محل سوختگی",
                "گرفتن محل سوختگی زیر آب خنک جاری به مدت ۱۰ تا ۲۰ دقیقه",
                "ترکاندن تاول‌ها با سوزن"
            ),
            correctIndex = 2,
            explanation = "آب خنک جاری حرارت را از بافت می‌گیرد و آسیب را کاهش می‌دهد؛ یخ و خمیردندان آسیب را بیشتر می‌کنند."
        ),
        QuizQuestion(
            id = "q_fa_2",
            topicId = "quiz_first_aid",
            question = "اگر در حال کنترل خونریزی شدید با گاز استریل بودید و گاز آغشته به خون شد، چه کار باید کرد؟",
            options = listOf(
                "گاز اول را بردارید و زخم را با آب بشویید",
                "گاز اول را سر جای خود بگذارید و لایه جدیدی روی آن اضافه کرده و فشار دهید",
                "زخم را باز بگذارید تا خشک شود",
                "پودر قهوه روی زخم بریزید"
            ),
            correctIndex = 1,
            explanation = "برداشتن لایه اول باعث کنده شدن لخته‌های تشکیل‌شده و تشدید خونریزی می‌شود."
        ),
        QuizQuestion(
            id = "q_fa_3",
            topicId = "quiz_first_aid",
            question = "کدام مورد نشانه خطرناک مسمومیت با گاز مونوکسید کربن در فضای بسته است؟",
            options = listOf(
                "سردرد ناگهانی، سرگیجه، تهوع و خواب‌آلودگی هم‌زمان چند نفر",
                "احساس تشنگی پس از پیاده‌روی",
                "بوی تند و تیز سرکه در آشپزخانه",
                "خنک شدن هوای اتاق"
            ),
            correctIndex = 0,
            explanation = "مونوکسید کربن بی‌رنگ و بی‌بوست و معمولاً با علائم شبیه آنفولانزا یا سردرد و خواب‌آلودگی جمعی بروز می‌کند."
        ),
        QuizQuestion(
            id = "q_fl_1",
            topicId = "quiz_flood_urban",
            question = "هنگام مواجهه با خیابان آب‌گرفته هنگام رانندگی، صحیح‌ترین تصمیم چیست؟",
            options = listOf(
                "افزایش سرعت برای عبور سریع از میان آب",
                "توقف، دور زدن و انتخاب مسیر جایگزین یا انتقال به نقطه مرتفع",
                "ماندن در زیرگذر تا کاهش آب",
                "خاموش کردن چراغ‌های خودرو و ادامه مسیر"
            ),
            correctIndex = 1,
            explanation = "آب جاری می‌تواند خودرو را جابه‌جا کند یا حفره‌های خطرناک خیابان را پنهان سازد."
        ),
        QuizQuestion(
            id = "q_fl_2",
            topicId = "quiz_flood_urban",
            question = "در صورت ورود به خانه و استشمام بوی شدید گاز شهری، کدام کار ممنوع است؟",
            options = listOf(
                "باز کردن در و پنجره‌ها",
                "بستن شیر اصلی گاز",
                "زدن کلید چراغ یا استفاده از آسانسور",
                "خروج از ساختمان و تماس با ۱۹۴ از فضای باز"
            ),
            correctIndex = 2,
            explanation = "هر کلید برقی یا زنگ می‌تواند جرقه‌ای ایجاد کند که در حضور گاز متراکم خطر انفجار دارد."
        ),
        QuizQuestion(
            id = "q_fl_3",
            topicId = "quiz_flood_urban",
            question = "شماره تماس مستقیم سازمان آتش‌نشانی و خدمات ایمنی در ایران کدام است؟",
            options = listOf(
                "۱۲۵",
                "۱۱۸",
                "۱۳۳",
                "۱۴۱"
            ),
            correctIndex = 0,
            explanation = "شماره ۱۲۵ برای آتش‌نشانی، ۱۱۲ برای هلال احمر، ۱۱۵ برای اورژانس و ۱۱۰ برای پلیس است."
        )
    )
}
