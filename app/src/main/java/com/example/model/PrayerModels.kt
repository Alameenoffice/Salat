package com.example.model

enum class PrayerType(
    val arabicName: String,
    val subtitle: String,
    val iqamaDefaultMinutes: Int,
    val sunnahBefore: String,
    val sunnahAfter: String,
    val mosulHeritageNote: String
) {
    FAJR(
        arabicName = "الفجر",
        subtitle = "بزوغ الفجر الصادق على ضفاف دجلة",
        iqamaDefaultMinutes = 25,
        sunnahBefore = "ركعتان مؤكدة",
        sunnahAfter = "لا توجد",
        mosulHeritageNote = "يُرفع أذان الفجر في جوامع الموصل بالمقام الموصلي الخاشع متبعاً بتسابيح الفجر الموصلية المتوارثة."
    ),
    SUNRISE(
        arabicName = "الشروق",
        subtitle = "إشراق الشمس وصلاة الضحى بعد ١٥ دقيقة",
        iqamaDefaultMinutes = 0,
        sunnahBefore = "صلاة الإشراق / الضحى",
        sunnahAfter = "مثنى مثنى",
        mosulHeritageNote = "وقت شروق الشمس في سماء أم الربيعين، ويبدأ وقت صلاة الضحى بعد ارتفاع الشمس قيد رمح (حوالي ١٥ دقيقة)."
    ),
    DHUHR(
        arabicName = "الظهر",
        subtitle = "زوال الشمس عن كبد السماء",
        iqamaDefaultMinutes = 15,
        sunnahBefore = "٤ ركعات مؤكدة",
        sunnahAfter = "ركعتان مؤكدة",
        mosulHeritageNote = "تُقام صلاة الجمعة والظهر في الجامع النوري الكبير وجامع النبي يونس وسائر مساجد الساحلين الأيمن والأيسر."
    ),
    ASR(
        arabicName = "العصر",
        subtitle = "الصلاة الوسطى في رحاب المساجد",
        iqamaDefaultMinutes = 15,
        sunnahBefore = "٤ ركعات مستحبة",
        sunnahAfter = "لا توجد",
        mosulHeritageNote = "تشتهر مساجد الموصل بحلقات الذكر وتلاوة القرآن الكريم بين صلاتي العصر والمغرب."
    ),
    MAGHRIB(
        arabicName = "المغرب",
        subtitle = "غروب الشمس خلف تلال الموصل",
        iqamaDefaultMinutes = 10,
        sunnahBefore = "ركعتان خفيفتان",
        sunnahAfter = "ركعتان مؤكدة",
        mosulHeritageNote = "عند غروب الشمس يصدح أذان المغرب في أرجاء الموصل، وتُعمر الموائد في رمضان بالمدقوقة الموصلية وشراب الزبيب."
    ),
    ISHA(
        arabicName = "العشاء",
        subtitle = "مغيب الشفق الأحمر وسكون الليل",
        iqamaDefaultMinutes = 15,
        sunnahBefore = "ركعتان مستحبة",
        sunnahAfter = "ركعتان + الوتر",
        mosulHeritageNote = "تُختم الصلوات الخمس بصلاة العشاء والوتر، وفي شهر رمضان تُقام صلاة التراويح بالتلاوة العراقية العذبة."
    )
}

data class PrayerTimeEntry(
    val type: PrayerType,
    val hour24: Int,
    val minute: Int,
    val formatted12h: String,
    val periodLabel: String, // ص / م
    val totalMinutesFromMidnight: Int,
    val offsetMinutes: Int,
    val notificationEnabled: Boolean,
    val isNextPrayer: Boolean = false,
    val isPassed: Boolean = false
)

data class DailyPrayerSchedule(
    val gregorianDateFormatted: String,
    val hijriDateFormatted: String,
    val hijriMonthName: String,
    val hijriDay: Int,
    val hijriYear: Int,
    val dayOfWeekArabic: String,
    val district: MosulDistrict,
    val prayers: List<PrayerTimeEntry>,
    val imsakFormatted: String,
    val midnightFormatted: String,
    val lastThirdFormatted: String,
    val nextPrayer: PrayerType,
    val secondsUntilNextPrayer: Long,
    val progressToNextPrayer: Float,
    val isSyncedOnline: Boolean = false
)

enum class CalculationMethodOption(
    val id: Int,
    val titleArabic: String,
    val descriptionArabic: String,
    val fajrAngle: Double,
    val ishaAngle: Double,
    val ishaFixedMinutes: Int = 0
) {
    IRAQI_SUNNI_ENDOWMENT(
        id = 101,
        titleArabic = "ديوان الوقف السني العراقي (توقيت الموصل المعتمد)",
        descriptionArabic = "زاوية الفجر 18° وزاوية العشاء 17.5° مع مراعاة ارتفاع مدينة الموصل",
        fajrAngle = 18.0,
        ishaAngle = 17.5
    ),
    MUSLIM_WORLD_LEAGUE(
        id = 3,
        titleArabic = "رابطة العالم الإسلامي",
        descriptionArabic = "زاوية الفجر 18° وزاوية العشاء 17°",
        fajrAngle = 18.0,
        ishaAngle = 17.0
    ),
    UMM_AL_QURA(
        id = 4,
        titleArabic = "تقويم أم القرى (مكة المكرمة)",
        descriptionArabic = "زاوية الفجر 18.5° والعشاء 90 دقيقة بعد المغرب",
        fajrAngle = 18.5,
        ishaAngle = 0.0,
        ishaFixedMinutes = 90
    ),
    EGYPTIAN_GENERAL_AUTHORITY(
        id = 5,
        titleArabic = "الهيئة المصرية العامة للمساحة",
        descriptionArabic = "زاوية الفجر 19.5° وزاوية العشاء 17.5°",
        fajrAngle = 19.5,
        ishaAngle = 17.5
    )
}

enum class AsrJuristicMethod(
    val id: Int,
    val titleArabic: String,
    val descriptionArabic: String,
    val shadowFactor: Int
) {
    SHAFII(
        id = 0,
        titleArabic = "الجمهور (الشافعي، المالكي، الحنبلي)",
        descriptionArabic = "يدخل وقت العصر إذا صار ظل الشيء مثله بعد فيء الزوال",
        shadowFactor = 1
    ),
    HANAFI(
        id = 1,
        titleArabic = "المذهب الحنفي",
        descriptionArabic = "يدخل وقت العصر إذا صار ظل الشيء مثليه بعد فيء الزوال",
        shadowFactor = 2
    )
}

data class MosulDistrict(
    val id: String,
    val nameArabic: String,
    val sideLabel: String,
    val latitude: Double,
    val longitude: Double,
    val elevationMeters: Double,
    val landmarkArabic: String
)

val MOSUL_DISTRICTS = listOf(
    MosulDistrict(
        id = "old_mosul_nuri",
        nameArabic = "الموصل القديمة (الجامع النوري)",
        sideLabel = "الساحل الأيمن",
        latitude = 36.3432,
        longitude = 43.1270,
        elevationMeters = 228.0,
        landmarkArabic = "الجامع النوري الكبير ومنارة الحدباء التاريخية"
    ),
    MosulDistrict(
        id = "nabi_yunus",
        nameArabic = "حي النبي يونس والفيصلية",
        sideLabel = "الساحل الأيسر",
        latitude = 36.3556,
        longitude = 43.1558,
        elevationMeters = 245.0,
        landmarkArabic = "جامع النبي يونس عليه السلام على التل الأثري"
    ),
    MosulDistrict(
        id = "cultural_group",
        nameArabic = "المجموعة الثقافية وجامعة الموصل",
        sideLabel = "الساحل الأيسر",
        latitude = 36.3775,
        longitude = 43.1445,
        elevationMeters = 232.0,
        landmarkArabic = "الجامع الكبير (جامع الموصل الكبير) والحرم الجامعي"
    ),
    MosulDistrict(
        id = "zuhoor_muthanna",
        nameArabic = "حي الزهور والمثنى",
        sideLabel = "الساحل الأيسر",
        latitude = 36.3720,
        longitude = 43.1750,
        elevationMeters = 240.0,
        landmarkArabic = "قلب الساحل الأيسر النابض بالحياة والمساجد العامرة"
    ),
    MosulDistrict(
        id = "dawasa_ghizlani",
        nameArabic = "الدواسة والطيران والجوسق",
        sideLabel = "الساحل الأيمن",
        latitude = 36.3265,
        longitude = 43.1362,
        elevationMeters = 222.0,
        landmarkArabic = "جامع النبي شيت ومركز مدينة الموصل التجاري"
    ),
    MosulDistrict(
        id = "arabi_rashidiya",
        nameArabic = "حي العربي والرشيدية",
        sideLabel = "شمال الموصل",
        latitude = 36.4120,
        longitude = 43.1310,
        elevationMeters = 238.0,
        landmarkArabic = "الضفاف الشمالية لنهر دجلة وبساتين الموصل"
    ),
    MosulDistrict(
        id = "hammam_al_alil",
        nameArabic = "حمام العليل",
        sideLabel = "جنوب الموصل",
        latitude = 36.1575,
        longitude = 43.2561,
        elevationMeters = 205.0,
        landmarkArabic = "العيون الكبريتية الطبيعية وضفاف دجلة الجنوبية"
    ),
    MosulDistrict(
        id = "bashiqa_bahzani",
        nameArabic = "بعشيقة وبحزاني",
        sideLabel = "شرق الموصل (سهل نينوى)",
        latitude = 36.4530,
        longitude = 43.3415,
        elevationMeters = 320.0,
        landmarkArabic = "بساتين الزيتون وسفوح جبل مقلوب"
    ),
    MosulDistrict(
        id = "tal_afar",
        nameArabic = "تلعفر",
        sideLabel = "غرب نينوى",
        latitude = 36.3742,
        longitude = 42.4486,
        elevationMeters = 410.0,
        landmarkArabic = "قلعة تلعفر التاريخية ومساجدها العريقة"
    ),
    MosulDistrict(
        id = "hamdaniya_bartella",
        nameArabic = "الحمدانية وبرطلة",
        sideLabel = "سهل نينوى",
        latitude = 36.2690,
        longitude = 43.3770,
        elevationMeters = 295.0,
        landmarkArabic = "حاضرة سهل نينوى التاريخية"
    )
)

data class MosulHistoricMosque(
    val id: String,
    val nameArabic: String,
    val foundedEra: String,
    val locationArabic: String,
    val architecturalFeature: String,
    val distanceFromCenterKm: String,
    val qiblaNote: String
)

val MOSUL_HISTORIC_MOSQUES = listOf(
    MosulHistoricMosque(
        id = "al_nuri",
        nameArabic = "الجامع النوري الكبير ومنارة الحدباء",
        foundedEra = "تأسس عام 568 هـ / 1172 م (نور الدين زنكي)",
        locationArabic = "الموصل القديمة - الساحل الأيمن",
        architecturalFeature = "يشتهر بمنارته الحدباء المزخرفة بالآجر الموصلي بسبعة أحزمة هندسية فريدة ومحرابه الرخامي النفيس.",
        distanceFromCenterKm = "قلب الموصل القديمة",
        qiblaNote = "محرابه التاريخي موجه بدقة فلكية عالية نحو الكعبة المشرفة بزاوية 191.8°."
    ),
    MosulHistoricMosque(
        id = "nabi_yunus_mosque",
        nameArabic = "جامع النبي يونس (عليه السلام)",
        foundedEra = "موقع تاريخي وأثري عريق فوق تل التوبة",
        locationArabic = "الساحل الأيسر - حي النبي يونس",
        architecturalFeature = "يعلو تلاً أثرياً مطلاً على مدينة الموصل ويعد من أبرز المعالم الروحية والتاريخية لأهل نينوى.",
        distanceFromCenterKm = "2.8 كم شرق دجلة",
        qiblaNote = "منارة وقبة شامخة تشرف على أفق الساحل الأيسر."
    ),
    MosulHistoricMosque(
        id = "mosul_grand_mosque",
        nameArabic = "جامع الموصل الكبير (جامع الرحمة)",
        foundedEra = "صرح معماري حديث وكبير",
        locationArabic = "الساحل الأيسر - قرب جسر السويس ومنطقة الثقافة",
        architecturalFeature = "أكبر مساجد الموصل مساحةً، يقع على ضفاف نهر دجلة ويتميز بقبابه ومآذنه الشاهقة.",
        distanceFromCenterKm = "1.5 كم على ضفة دجلة",
        qiblaNote = "يستقبل آلاف المصلين في صلوات الجمعة والعيدين والتراويح."
    ),
    MosulHistoricMosque(
        id = "nabi_sheet",
        nameArabic = "جامع النبي شيت (عليه السلام)",
        foundedEra = "القرن الحادي عشر الهجري / السابع عشر الميلادي",
        locationArabic = "الساحل الأيمن - قرب الدواسة",
        architecturalFeature = "يتميز بعمارته الموصلية الأصيلة واستخدام حجر الفرش الموصلي (المرمر) في أروقته ومحرابه.",
        distanceFromCenterKm = "1.2 كم جنوب البلدة القديمة",
        qiblaNote = "مركز علمي وروحي عريق لتعليم القرآن الكريم والعلوم الشرعية."
    ),
    MosulHistoricMosque(
        id = "al_aghwat",
        nameArabic = "جامع الأغوات التاريخي",
        foundedEra = "تأسس عام 1114 هـ / 1702 م (العهد الجليلي)",
        locationArabic = "الساحل الأيمن - باب الجسر القديم",
        architecturalFeature = "تحفة معمارية من العهد الجليلي في الموصل، يضم مدرسة دينية تاريخية وقبة مهيبة.",
        distanceFromCenterKm = "0.9 كم على ضفة النهر",
        qiblaNote = "يقع عند مدخل الجسر العتيق المطل على نهر دجلة."
    ),
    MosulHistoricMosque(
        id = "umayyad_mosque",
        nameArabic = "الجامع الأموي (جامع المصفي)",
        foundedEra = "أول مسجد بني في الموصل عام 16 هـ / 637 م",
        locationArabic = "الموصل القديمة - محلة الساعة",
        architecturalFeature = "أسسه عتبة بن فرقد السلمي، وعُرف لاحقاً بجامع المصفي، ويضم أقدم المحاريب الموصلية.",
        distanceFromCenterKm = "0.6 كم من الجامع النوري",
        qiblaNote = "أول قبلة خُطّت للمسلمين في مدينة الموصل عند فتحها."
    )
)

data class AthkarItem(
    val id: String,
    val category: String,
    val title: String,
    val arabicText: String,
    val virtue: String,
    val targetCount: Int
)

val DEFAULT_ATHKAR_LIST = listOf(
    AthkarItem(
        id = "post_prayer_1",
        category = "أذكار بعد الصلاة",
        title = "الاستغفار والسلام",
        arabicText = "أَسْتَغْفِرُ اللهَ (ثلاثاً) .. اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ، تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ.",
        virtue = "سنة نبوية ثابتة عقب التسليم من كل صلاة مفروضة",
        targetCount = 1
    ),
    AthkarItem(
        id = "post_prayer_2",
        category = "أذكار بعد الصلاة",
        title = "التسبيح والتحميد والتكبير",
        arabicText = "سُبْحَانَ اللهِ، وَالْحَمْدُ لِلَّهِ، وَاللهُ أَكْبَرُ",
        virtue = "غُفرت خطاياه وإن كانت مثل زبد البحر",
        targetCount = 33
    ),
    AthkarItem(
        id = "post_prayer_3",
        category = "أذكار بعد الصلاة",
        title = "تمام المائة بالتهليل",
        arabicText = "لَا إِلَهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ.",
        virtue = "تمام المائة بعد التسبيح والتحميد والتكبير",
        targetCount = 1
    ),
    AthkarItem(
        id = "morning_1",
        category = "أذكار الصباح",
        title = "سيد الاستغفار",
        arabicText = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ لَكَ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ.",
        virtue = "من قالها موقناً بها حين يصبح وحين يمسي كان من أهل الجنة",
        targetCount = 1
    ),
    AthkarItem(
        id = "morning_2",
        category = "أذكار الصباح",
        title = "أصبحنا وأصبح الملك لله",
        arabicText = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ.",
        virtue = "حفظ وبركة في أول النهار بعد صلاة الفجر",
        targetCount = 1
    ),
    AthkarItem(
        id = "morning_3",
        category = "أذكار الصباح",
        title = "سبحان الله وبحمده",
        arabicText = "سُبْحَانَ اللهِ وَبِحَمْدِهِ، عَدَدَ خَلْقِهِ، وَرِضَا نَفْسِهِ، وَزِنَةَ عَرْشِهِ، وَمِدَادَ كَلِمَاتِهِ.",
        virtue = "تقال ثلاث مرات بعد صلاة الفجر والشروق",
        targetCount = 3
    ),
    AthkarItem(
        id = "evening_1",
        category = "أذكار المساء",
        title = "أمسينا وأمسى الملك لله",
        arabicText = "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ وَالْحَمْدُ لِلَّهِ، رَبِّ أَسْأَلُكَ خَيْرَ مَا فِي هَذِهِ اللَّيْلَةِ وَخَيْرَ مَا بَعْدَهَا، وَأَعُوذُ بِكَ مِنْ شَرِّ مَا فِي هَذِهِ اللَّيْلَةِ وَشَرِّ مَا بَعْدَهَا.",
        virtue = "حفظ وأمان في المساء والليل",
        targetCount = 1
    ),
    AthkarItem(
        id = "evening_2",
        category = "أذكار المساء",
        title = "التعوذ بكلمات الله التامات",
        arabicText = "أَعُوذُ بِكَلِمَاتِ اللهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ، بِسْمِ اللهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ.",
        virtue = "لم يضره شيء في ليلته ولا نهاره",
        targetCount = 3
    ),
    AthkarItem(
        id = "mosul_dua_1",
        category = "أدعية الموصل",
        title = "دعاء لأهل الموصل والعراق",
        arabicText = "اللَّهُمَّ احْفَظْ مَدِينَةَ الْمَوْصِلِ وَأَهْلَهَا، وَاجْعَلْهَا بَلَداً آمِناً مُطْمَئِنّاً سَخَاءً رَخَاءً، وَاعْمُرْ مَسَاجِدَهَا بِذِكْرِكَ وَطَاعَتِكَ، وَبَارِكْ فِي دِجْلَتِهَا وَأَرْضِهَا وَثِمَارِهَا يَا أَرْحَمَ الرَّاحِمِينَ.",
        virtue = "دعاء بالبركة والأمان لأم الربيعين وأهلها",
        targetCount = 1
    ),
    AthkarItem(
        id = "mosul_dua_2",
        category = "أدعية الموصل",
        title = "دعاء نبي الله يونس عليه السلام",
        arabicText = "لَا إِلَهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ.",
        virtue = "دعوة ذي النون (نبي الله يونس صاحب نينوى) ما دعا بها مكروب إلا فرّج الله كربه",
        targetCount = 7
    )
)
