package com.example.data.seed

import com.example.R
import com.example.data.local.entity.MediaCategory
import com.example.data.local.entity.MediaDownloadStatus
import com.example.data.local.entity.OfflineMediaEntity
import com.example.data.model.MavaCartoonEpisode
import com.example.data.model.MavaCharacterId
import com.example.data.model.MavaDiscoveryItem
import com.example.data.model.MavaEmotionType
import com.example.data.model.MavaStory
import com.example.data.model.MavaStoryPage

object MavaContentSeed {

    val stories: List<MavaStory> = listOf(
        MavaStory(
            id = "story_friendship",
            title = "جشن چتر رنگی پوفی",
            themeFa = "دوستی و مهربانی",
            mainCharacter = MavaCharacterId.PUFI,
            pages = listOf(
                MavaStoryPage(
                    MavaCharacterId.PUFI,
                    MavaEmotionType.HAPPY,
                    "سلام کوچولو! من پوفی هستم. امروز چتر صورتی‌ام را باز کردم."
                ),
                MavaStoryPage(
                    MavaCharacterId.TOTO,
                    MavaEmotionType.SAD,
                    "توتو آهسته آمد. باران آرام می‌بارید و توتو چتر نداشت."
                ),
                MavaStoryPage(
                    MavaCharacterId.PUFI,
                    MavaEmotionType.HAPPY,
                    "پوفی گفت: توتو جان بیا زیر چتر من! دو تا دوست کنار هم."
                ),
                MavaStoryPage(
                    MavaCharacterId.BOMBO,
                    MavaEmotionType.CALM,
                    "بومبو و فوفو هم دست تکان دادند و همه با لبخند چای گرم خوردند!"
                )
            )
        ),
        MavaStory(
            id = "story_ask_adult",
            title = "دست مهربان مامان و بابا",
            themeFa = "کمک خواستن از بزرگسال مورداعتماد",
            mainCharacter = MavaCharacterId.BOMBO,
            pages = listOf(
                MavaStoryPage(
                    MavaCharacterId.FOFO,
                    MavaEmotionType.SCARED,
                    "فوفو صدای باد را شنید و کمی ترسید."
                ),
                MavaStoryPage(
                    MavaCharacterId.BOMBO,
                    MavaEmotionType.CALM,
                    "بومبو گفت: وقتی می‌ترسیم، سریع دست مامان یا بابا را می‌گیریم!"
                ),
                MavaStoryPage(
                    MavaCharacterId.TOTO,
                    MavaEmotionType.CALM,
                    "نفس آرام می‌کشیم: یک، دو، سه... صدای باد فقط بازی برگ‌هاست."
                ),
                MavaStoryPage(
                    MavaCharacterId.FOFO,
                    MavaEmotionType.HAPPY,
                    "فوفو کنار بومبو و بزرگتر مهربانش لبخند زد و آرام شد."
                )
            )
        ),
        MavaStory(
            id = "story_safe_home",
            title = "جای امن توتو در خانه",
            themeFa = "رفتارهای ایمن ساده در خانه",
            mainCharacter = MavaCharacterId.TOTO,
            pages = listOf(
                MavaStoryPage(
                    MavaCharacterId.TOTO,
                    MavaEmotionType.CALM,
                    "توتو دوست دارد دور از اجاق داغ و پریز برق بازی کند."
                ),
                MavaStoryPage(
                    MavaCharacterId.PUFI,
                    MavaEmotionType.HAPPY,
                    "پوفی توپ نرمش را روی فرش نرم می‌غلتاند."
                ),
                MavaStoryPage(
                    MavaCharacterId.BOMBO,
                    MavaEmotionType.HAPPY,
                    "اگر اسباب‌بازی دور افتاد، از بزرگترها کمک می‌گیریم."
                )
            )
        )
    )

    val cartoons: List<MavaCartoonEpisode> = listOf(
        MavaCartoonEpisode(
            id = "cartoon_peppa_1",
            mediaId = "vid_mava_peppa_1",
            title = "پپا پیگ: دوست جدید (امیلی فیل)",
            character = MavaCharacterId.PUFI,
            positiveMessage = "خوش‌آمدگویی مهربانانه به دوستان جدید",
            durationLabel = "۵ دقیقه",
            bundledVideoResId = R.raw.mava_peppa_emily_elephant,
            animatedFrames = listOf(
                MavaStoryPage(MavaCharacterId.PUFI, MavaEmotionType.HAPPY, "امروز یک دوست جدید به مهدکودک آمده است!"),
                MavaStoryPage(MavaCharacterId.TOTO, MavaEmotionType.CALM, "با لبخند و آرامش به امیلی فیل خوش‌آمد می‌گوییم."),
                MavaStoryPage(MavaCharacterId.BOMBO, MavaEmotionType.HAPPY, "وقتی با هم بازی می‌کنیم، خجالت زود تمام می‌شود!")
            )
        ),
        MavaCartoonEpisode(
            id = "cartoon_peppa_2",
            mediaId = "vid_mava_peppa_2",
            title = "پپا پیگ: تعطیلات پالی طوطی",
            character = MavaCharacterId.TOTO,
            positiveMessage = "مهربانی و مراقبت از حیوانات و مهمان‌ها",
            durationLabel = "۵ دقیقه",
            bundledVideoResId = R.raw.mava_peppa_pollys_holiday,
            animatedFrames = listOf(
                MavaStoryPage(MavaCharacterId.TOTO, MavaEmotionType.HAPPY, "پالی طوطی به خانه پپا و جورج آمده تا کنارشان بماند."),
                MavaStoryPage(MavaCharacterId.FOFO, MavaEmotionType.CALM, "دوست کوچکمان را با صدای آرام و مهربان صدا می‌زنیم."),
                MavaStoryPage(MavaCharacterId.PUFI, MavaEmotionType.HAPPY, "مراقبت از مهمان‌های کوچک خیلی لذت‌بخش است!")
            )
        ),
        MavaCartoonEpisode(
            id = "cartoon_peppa_3",
            mediaId = "vid_mava_peppa_3",
            title = "پپا پیگ: بازی با حباب‌ها",
            character = MavaCharacterId.BOMBO,
            positiveMessage = "شادی ساده، نوبت‌گیری و بازی کنار خانواده",
            durationLabel = "۵ دقیقه",
            bundledVideoResId = R.raw.mava_peppa_bubbles,
            animatedFrames = listOf(
                MavaStoryPage(MavaCharacterId.BOMBO, MavaEmotionType.HAPPY, "ببین! حباب‌های رنگین‌کمانی در هوا پرواز می‌کنند."),
                MavaStoryPage(MavaCharacterId.PUFI, MavaEmotionType.HAPPY, "نوبتی فوت می‌کنیم تا حباب‌های بزرگ بسازیم."),
                MavaStoryPage(MavaCharacterId.TOTO, MavaEmotionType.CALM, "خنده‌های شاد کنار خانواده همیشه آرامش‌بخش است.")
            )
        ),
        MavaCartoonEpisode(
            id = "cartoon_1",
            mediaId = "vid_mava_cartoon_1",
            title = "لبخند صبحگاهی پوفی و بومبو",
            character = MavaCharacterId.PUFI,
            positiveMessage = "سلام‌کردن با لبخند و مهربانی",
            durationLabel = "۲ دقیقه",
            animatedFrames = listOf(
                MavaStoryPage(MavaCharacterId.PUFI, MavaEmotionType.HAPPY, "صبح بخیر! خورشید طلایی بیدار شده."),
                MavaStoryPage(MavaCharacterId.BOMBO, MavaEmotionType.HAPPY, "بومبو دست‌هایش را بالا می‌برد: بغل صبحگاهی!"),
                MavaStoryPage(MavaCharacterId.TOTO, MavaEmotionType.CALM, "یک جرعه آب خنک و لبخند به همه‌ی دوستان.")
            )
        ),
        MavaCartoonEpisode(
            id = "cartoon_2",
            mediaId = "vid_mava_cartoon_2",
            title = "ابر پنبه‌ای و نفس آرام توتو",
            character = MavaCharacterId.TOTO,
            positiveMessage = "آرام‌شدن با نفس کشیدن عمیق",
            durationLabel = "۳ دقیقه",
            animatedFrames = listOf(
                MavaStoryPage(MavaCharacterId.TOTO, MavaEmotionType.CALM, "بیا مثل ابر نرم و سبک نفس بکشیم."),
                MavaStoryPage(MavaCharacterId.FOFO, MavaEmotionType.CALM, "دم آرام... و بازدم مثل باد ملایم بهار."),
                MavaStoryPage(MavaCharacterId.PUFI, MavaEmotionType.HAPPY, "آفرین کوچولو! الان دلمان آرام و شاد است.")
            )
        )
    )

    fun defaultMavaOfflineMedia(): List<OfflineMediaEntity> = cartoons.map { ep ->
        val hasBundled = ep.bundledVideoResId != null
        OfflineMediaEntity(
            mediaId = ep.mediaId,
            category = MediaCategory.MAVA_CARTOON,
            title = "کارتون مأوا: ${ep.title}",
            description = ep.positiveMessage,
            sourceAttribution = if (hasBundled) {
                "فایل ویدئویی ارسالی کاربر (ذخیره‌شده در بسته آفلاین مأوا)"
            } else {
                "تولید اختصاصی مأوا (بدون تبلیغ و مناسب سن ۱ تا ۳ سال)"
            },
            streamUrl = null,
            localFilePath = null,
            status = if (hasBundled) MediaDownloadStatus.DOWNLOADED else MediaDownloadStatus.NOT_LINKED,
            durationLabel = ep.durationLabel
        )
    }

    val discoveryItems: List<MavaDiscoveryItem> = listOf(
        MavaDiscoveryItem("disc_rabbit", "حیوانات", "خرگوش", "پوفی با گوش‌های نرم بالا و پایین می‌پرد!", MavaCharacterId.PUFI, 587.33),
        MavaDiscoveryItem("disc_turtle", "حیوانات", "لاک‌پشت", "توتو آرام و صبور روی چمن قدم می‌زند.", MavaCharacterId.TOTO, 392.00),
        MavaDiscoveryItem("disc_bear", "حیوانات", "خرس مهربان", "بومبو دوستانش را محکم بغل می‌کند.", MavaCharacterId.BOMBO, 329.63),
        MavaDiscoveryItem("disc_fox", "حیوانات", "روباه کوچولو", "فوفو با کنجکاوی گل‌ها را بو می‌کند.", MavaCharacterId.FOFO, 523.25),
        MavaDiscoveryItem("disc_sun", "طبیعت", "خورشید", "گرم و روشن مثل لبخند!", MavaCharacterId.PUFI, 659.25),
        MavaDiscoveryItem("disc_cloud", "طبیعت", "ابر و باران آرام", "صدای نرم باران به گل‌ها آب می‌دهد.", MavaCharacterId.TOTO, 440.00),
        MavaDiscoveryItem("disc_circle", "اشکال", "دایره", "گرد مثل توپ بازی و خورشید!", MavaCharacterId.BOMBO, 493.88),
        MavaDiscoveryItem("disc_star", "اشکال", "ستاره", "درخشان در آسمان شب آرام.", MavaCharacterId.FOFO, 783.99)
    )
}
