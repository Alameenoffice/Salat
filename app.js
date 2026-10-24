// ============================================================================
// مواقيت الموصل - أم الربيعين (PWA Engine - Works 100% Offline)
// صدقة جارية عن أحمد أبو رسلان
// ============================================================================

const KAABA_LAT = 21.422487;
const KAABA_LNG = 39.826206;

const MOSUL_DISTRICTS = [
  { id: 'old_mosul_nuri', nameArabic: 'الموصل القديمة (الجامع النوري)', sideLabel: 'الساحل الأيمن', latitude: 36.3432, longitude: 43.1270, elevationMeters: 228, landmarkArabic: 'الجامع النوري الكبير ومنارة الحدباء التاريخية' },
  { id: 'nabi_yunus', nameArabic: 'حي النبي يونس والفيصلية', sideLabel: 'الساحل الأيسر', latitude: 36.3556, longitude: 43.1558, elevationMeters: 245, landmarkArabic: 'جامع النبي يونس عليه السلام على التل الأثري' },
  { id: 'cultural_group', nameArabic: 'المجموعة الثقافية وجامعة الموصل', sideLabel: 'الساحل الأيسر', latitude: 36.3775, longitude: 43.1445, elevationMeters: 232, landmarkArabic: 'جامع الموصل الكبير والحرم الجامعي' },
  { id: 'zuhoor_muthanna', nameArabic: 'حي الزهور والمثنى', sideLabel: 'الساحل الأيسر', latitude: 36.3720, longitude: 43.1750, elevationMeters: 240, landmarkArabic: 'قلب الساحل الأيسر النابض بالحياة والمساجد العامرة' },
  { id: 'dawasa_ghizlani', nameArabic: 'الدواسة والطيران والجوسق', sideLabel: 'الساحل الأيمن', latitude: 36.3265, longitude: 43.1362, elevationMeters: 222, landmarkArabic: 'جامع النبي شيت ومركز مدينة الموصل التجاري' },
  { id: 'arabi_rashidiya', nameArabic: 'حي العربي والرشيدية', sideLabel: 'شمال الموصل', latitude: 36.4120, longitude: 43.1310, elevationMeters: 238, landmarkArabic: 'الضفاف الشمالية لنهر دجلة وبساتين الموصل' },
  { id: 'hammam_al_alil', nameArabic: 'حمام العليل', sideLabel: 'جنوب الموصل', latitude: 36.1575, longitude: 43.2561, elevationMeters: 205, landmarkArabic: 'العيون الكبريتية الطبيعية وضفاف دجلة الجنوبية' },
  { id: 'bashiqa_bahzani', nameArabic: 'بعشيقة وبحزاني', sideLabel: 'شرق الموصل (سهل نينوى)', latitude: 36.4530, longitude: 43.3415, elevationMeters: 320, landmarkArabic: 'بساتين الزيتون وسفوح جبل مقلوب' },
  { id: 'tal_afar', nameArabic: 'تلعفر', sideLabel: 'غرب نينوى', latitude: 36.3742, longitude: 42.4486, elevationMeters: 410, landmarkArabic: 'قلعة تلعفر التاريخية ومساجدها العريقة' },
  { id: 'hamdaniya_bartella', nameArabic: 'الحمدانية وبرطلة', sideLabel: 'سهل نينوى', latitude: 36.2690, longitude: 43.3770, elevationMeters: 295, landmarkArabic: 'حاضرة سهل نينوى التاريخية' }
];

const CALCULATION_METHODS = [
  { id: 101, titleArabic: 'ديوان الوقف السني العراقي (توقيت الموصل المعتمد)', descriptionArabic: 'زاوية الفجر 18° وزاوية العشاء 17.5° مع مراعاة ارتفاع مدينة الموصل', fajrAngle: 18.0, ishaAngle: 17.5, ishaFixedMinutes: 0 },
  { id: 3, titleArabic: 'رابطة العالم الإسلامي', descriptionArabic: 'زاوية الفجر 18° وزاوية العشاء 17°', fajrAngle: 18.0, ishaAngle: 17.0, ishaFixedMinutes: 0 },
  { id: 4, titleArabic: 'تقويم أم القرى (مكة المكرمة)', descriptionArabic: 'زاوية الفجر 18.5° والعشاء 90 دقيقة بعد المغرب', fajrAngle: 18.5, ishaAngle: 0.0, ishaFixedMinutes: 90 },
  { id: 5, titleArabic: 'الهيئة المصرية العامة للمساحة', descriptionArabic: 'زاوية الفجر 19.5° وزاوية العشاء 17.5°', fajrAngle: 19.5, ishaAngle: 17.5, ishaFixedMinutes: 0 }
];

const ASR_METHODS = [
  { id: 0, titleArabic: 'الجمهور (الشافعي، المالكي، الحنبلي)', descriptionArabic: 'يدخل وقت العصر إذا صار ظل الشيء مثله بعد فيء الزوال', shadowFactor: 1 },
  { id: 1, titleArabic: 'المذهب الحنفي', descriptionArabic: 'يدخل وقت العصر إذا صار ظل الشيء مثليه بعد فيء الزوال', shadowFactor: 2 }
];

const PRAYERS_META = [
  { key: 'FAJR', icon: '🌅', arabicName: 'الفجر', subtitle: 'بزوغ الفجر الصادق على ضفاف دجلة', iqamaMinutes: 25, sunnahBefore: 'ركعتان مؤكدة', sunnahAfter: 'لا توجد', heritageNote: 'يُرفع أذان الفجر في جوامع الموصل بالمقام الموصلي الخاشع متبعاً بتسابيح الفجر الموصلية المتوارثة.' },
  { key: 'SUNRISE', icon: '☀️', arabicName: 'الشروق', subtitle: 'إشراق الشمس وصلاة الضحى بعد ١٥ دقيقة', iqamaMinutes: 0, sunnahBefore: 'صلاة الإشراق / الضحى', sunnahAfter: 'مثنى مثنى', heritageNote: 'وقت شروق الشمس في سماء أم الربيعين، ويبدأ وقت صلاة الضحى بعد ارتفاع الشمس قيد رمح (حوالي ١٥ دقيقة).' },
  { key: 'DHUHR', icon: '🕌', arabicName: 'الظهر', subtitle: 'زوال الشمس عن كبد السماء', iqamaMinutes: 15, sunnahBefore: '٤ ركعات مؤكدة', sunnahAfter: 'ركعتان مؤكدة', heritageNote: 'تُقام صلاة الجمعة والظهر في الجامع النوري الكبير وجامع النبي يونس وسائر مساجد الساحلين الأيمن والأيسر.' },
  { key: 'ASR', icon: '🌤️', arabicName: 'العصر', subtitle: 'الصلاة الوسطى في رحاب المساجد', iqamaMinutes: 15, sunnahBefore: '٤ ركعات مستحبة', sunnahAfter: 'لا توجد', heritageNote: 'تشتهر مساجد الموصل بحلقات الذكر وتلاوة القرآن الكريم بين صلاتي العصر والمغرب.' },
  { key: 'MAGHRIB', icon: '🌇', arabicName: 'المغرب', subtitle: 'غروب الشمس خلف تلال الموصل', iqamaMinutes: 10, sunnahBefore: 'ركعتان خفيفتان', sunnahAfter: 'ركعتان مؤكدة', heritageNote: 'عند غروب الشمس يصدح أذان المغرب في أرجاء الموصل، وتُعمر الموائد في رمضان بالمدقوقة الموصلية وشراب الزبيب.' },
  { key: 'ISHA', icon: '🌙', arabicName: 'العشاء', subtitle: 'مغيب الشفق الأحمر وسكون الليل', iqamaMinutes: 15, sunnahBefore: 'ركعتان مستحبة', sunnahAfter: 'ركعتان + الوتر', heritageNote: 'تُختم الصلوات الخمس بصلاة العشاء والوتر، وفي شهر رمضان تُقام صلاة التراويح بالتلاوة العراقية العذبة.' }
];

// 4. Prayer Motivators & Spiritual Illuminations (مشجعات ومحفزات الصلاة والسنن الرواتب)
const PRAYER_MOTIVATORS = [
  {
    hadith: '«مَنْ صَلَّى الْبَرْدَيْنِ دَخَلَ الْجَنَّةَ» — أي صلاتي الفجر والعصر في جماعة ووقتهما.',
    source: 'متفق عليه • إضاءة للمحافظة على صلاتي الفجر والعصر',
    tip: 'ركعتا الفجر خيرٌ من الدنيا وما فيها؛ حافظ على سنة الفجر القبلية ولا تدعها حضراً ولا سفراً.'
  },
  {
    hadith: '«مَنْ ثَابَرَ عَلَى ثِنْتَيْ عَشْرَةَ رَكْعَةً مِنَ السُّنَّةِ بَنَى اللَّهُ لَهُ بَيْتًا فِي الْجَنَّةِ: أَرْبَعِ رَكَعَاتٍ قَبْلَ الظُّهْرِ، وَرَكْعَتَيْنِ بَعْدَهَا، وَرَكْعَتَيْنِ بَعْدَ الْمَغْرِبِ، وَرَكْعَتَيْنِ بَعْدَ الْعِشَاءِ، وَرَكْعَتَيْنِ قَبْلَ الْفَجْرِ».',
    source: 'رواه الترمذي وصححه الألباني • فضل السنن الرواتب الاثنتي عشرة',
    tip: 'اجعل لك نصيباً يومياً من السنن الرواتب؛ فإنها تجبر النقص في الفرائض وترفع الدرجات.'
  },
  {
    hadith: '«أَحَبُّ الأَعْمَالِ إِلَى اللَّهِ الصَّلاَةُ عَلَى وَقْتِهَا»',
    source: 'متفق عليه • التبكير إلى الصلاة عند سماع النداء',
    tip: 'التهيؤ للصلاة بالوضوء قبل الأذان بدقائق يمنحك السكينة والخشوع وأجر انتظار الصلاة.'
  },
  {
    hadith: '«أَقْرَبُ مَا يَكُونُ الْعَبْدُ مِنْ رَبِّهِ وَهُوَ سَاجِدٌ فَأَكْثِرُوا الدُّعَاءَ»',
    source: 'رواه مسلم • اغتنام السجود بالدعاء الصادق',
    tip: 'في كل سجدة فرصة لرفع حاجاتك إلى الله؛ ادعُ لنفسك ولأهلك ولمدينة الموصل وأهلها.'
  },
  {
    hadith: '«بَشِّرِ الْمَشَّائِينَ فِي الظُّلَمِ إِلَى الْمَسَاجِدِ بِالنُّورِ التَّامِّ يَوْمَ الْقِيَامَةِ»',
    source: 'رواه أبو داود والترمذي • فضل صلاتي الفجر والعشاء',
    tip: 'كل خطوة إلى المسجد ترفع درجة وتحط خطيئة وتكتب لك نوراً تاماً.'
  }
];

const RAWATIB_ITEMS = [
  { id: 'raw_fajr', label: '٢ قبل الفجر' },
  { id: 'raw_dhuhr_1', label: '٤ قبل الظهر' },
  { id: 'raw_dhuhr_2', label: '٢ بعد الظهر' },
  { id: 'raw_maghrib', label: '٢ بعد المغرب' },
  { id: 'raw_isha', label: '٢ بعد العشاء' },
  { id: 'raw_witr', label: 'صلاة الوتر' }
];

// 3. Daily Renewed Athkar by Day of Week (Sunday 0 -> Saturday 6, non-repeating packages)
const WEEKLY_RENEWED_ATHKAR = {
  0: { // الأحد
    dayTitle: 'ورد وأذكار يوم الأحد • التوحيد والملك لله',
    themeNote: 'باقة يوم الأحد الخاصة بتجديد التوحيد والتوكل على الحي القيوم في مطلع الأسبوع',
    items: [
      { id: 'sun_1', title: 'ورد التوحيد والملك يوم الأحد', arabicText: 'لَا إِلَهَ إِلَّا اللهُ الْمَلِكُ الْحَقُّ الْمُبِينُ، لَا إِلَهَ إِلَّا اللهُ الْحَكَمُ الْعَدْلُ الْمَتِينُ، رَبُّنَا وَرَبُّ آبَائِنَا الْأَوَّلِينَ.', virtue: 'ورد يومي يورث اليقين والبركة في الرزق وتيسير الأمور', targetCount: 33 },
      { id: 'sun_2', title: 'تسبيح الحي القيوم', arabicText: 'يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ، أَصْلِحْ لِي شَأْنِي كُلَّهُ وَلَا تَكِلْنِي إِلَى نَفْسِي طَرْفَةَ عَيْنٍ.', virtue: 'من أعظم أدعية الكفاية والتوكل في بداية الأسبوع', targetCount: 7 },
      { id: 'sun_3', title: 'دعاء الفتح والتيسير ليوم الأحد', arabicText: 'اللَّهُمَّ افْتَحْ لَنَا فِي هَذَا الْيَوْمِ أَبْوَابَ فَضْلِكَ، وَأَنْزِلْ عَلَيْنَا فِيهِ بَرَكَاتِكَ، وَوَفِّقْنَا فِيهِ لِمُوجِبَاتِ مَرْضَاتِكَ.', virtue: 'استفتاح مبارك لأعمال الأسبوع', targetCount: 3 }
    ]
  },
  1: { // الإثنين
    dayTitle: 'ورد وأذكار يوم الإثنين • يوم تُعرض فيه الأعمال',
    themeNote: 'باقة يوم الإثنين المبارك المستحب فيه الإكثار من الحمد والصلاة على النبي ﷺ',
    items: [
      { id: 'mon_1', title: 'الحمد الكثير الطيب المبارك', arabicText: 'الْحَمْدُ لِلَّهِ حَمْداً كَثِيراً طَيِّباً مُبَارَكاً فِيهِ، مِلْءَ السَّمَاوَاتِ وَمِلْءَ الْأَرْضِ وَمِلْءَ مَا بَيْنَهُمَا وَمِلْءَ مَا شِئْتَ مِنْ شَيْءٍ بَعْدُ.', virtue: 'قال النبي ﷺ: لقد رأيت اثني عشر ملكاً يبتدرونها أيهم يرفعها', targetCount: 33 },
      { id: 'mon_2', title: 'الاستغفار الجامع يوم عرض الأعمال', arabicText: 'أَسْتَغْفِرُ اللهَ الْعَظِيمَ الَّذِي لَا إِلَهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ وَأَتُوبُ إِلَيْهِ، غَفَّارَ الذُّنُوبِ وَسَتَّارَ الْعُيُوبِ.', virtue: 'تُعرض الأعمال يوم الإثنين والخميس فيُستحب ختمها بالاستغفار', targetCount: 33 },
      { id: 'mon_3', title: 'دعاء قبول العمل الصالح', arabicText: 'اللَّهُمَّ إِنِّي أَسْأَلُكَ عِلْماً نَافِعاً، وَرِزْقاً طَيِّباً، وَعَمَلاً مُتَقَبَّلاً.', virtue: 'دعاء نبوي جامع للخير في الدين والدنيا', targetCount: 7 }
    ]
  },
  2: { // الثلاثاء
    dayTitle: 'ورد وأذكار يوم الثلاثاء • الحوقلة والفرج والسكينة',
    themeNote: 'باقة يوم الثلاثاء الخاصة بكنوز الجنة ودفع الهموم وتفريج الكربات',
    items: [
      { id: 'tue_1', title: 'كنز من كنوز الجنة (الحوقلة)', arabicText: 'لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللهِ الْعَلِيِّ الْعَظِيمِ.', virtue: 'كنز من كنوز الجنة وباب عظيم للفرج والإعانة على الطاعات', targetCount: 33 },
      { id: 'tue_2', title: 'دعاء نبي الله يونس في نينوى', arabicText: 'لَا إِلَهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ.', virtue: 'دعوة ذي النون صاحب نينوى، ما دعا بها مهموم إلا فرّج الله همه', targetCount: 33 },
      { id: 'tue_3', title: 'الاعتصام بالله الكافي', arabicText: 'حَسْبِيَ اللهُ لَا إِلَهَ إِلَّا هُوَ عَلَيْهِ تَوَكَّلْتُ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ.', virtue: 'من قالها سبع مرات كفاه الله ما أهمه من أمر الدنيا والآخرة', targetCount: 7 }
    ]
  },
  3: { // الأربعاء
    dayTitle: 'ورد وأذكار يوم الأربعاء • النور والهداية والعافية',
    themeNote: 'باقة يوم الأربعاء للتسبيح المضاعف وسؤال الله النور والعفو والعافية',
    items: [
      { id: 'wed_1', title: 'التسبيح المضاعف العظيم', arabicText: 'سُبْحَانَ اللهِ وَبِحَمْدِهِ، عَدَدَ خَلْقِهِ، وَرِضَا نَفْسِهِ، وَزِنَةَ عَرْشِهِ، وَمِدَادَ كَلِمَاتِهِ.', virtue: 'يعدل ساعات طويلة من الذكر في الأجر والمثوبة', targetCount: 33 },
      { id: 'wed_2', title: 'سؤال العفو والعافية في الدارين', arabicText: 'اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي الدُّنْيَا وَالْآخِرَةِ، اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي دِينِي وَدُنْيَايَ وَأَهْلِي وَمَالِي.', virtue: 'لم يسأل العبد ربه شيئاً أفضل من العفو والعافية', targetCount: 7 },
      { id: 'wed_3', title: 'دعاء النور في القلب والبصر', arabicText: 'اللَّهُمَّ اجْعَلْ فِي قَلْبِي نُوراً، وَفِي لِسَانِي نُوراً، وَفِي سَمْعِي نُوراً، وَفِي بَصَرِي نُوراً، وَاجْعَلْ لِي نُوراً.', virtue: 'من أدعية النبي ﷺ عند الخروج إلى صلاة الفجر', targetCount: 3 }
    ]
  },
  4: { // الخميس
    dayTitle: 'ورد وأذكار يوم الخميس • غراس الجنة وختام الأعمال',
    themeNote: 'باقة يوم الخميس المبارك لرفع الأعمال الصالحة والإقبال على ليلة الجمعة',
    items: [
      { id: 'thu_1', title: 'غراس الجنة والباقيات الصالحات', arabicText: 'سُبْحَانَ اللهِ، وَالْحَمْدُ لِلَّهِ، وَلَا إِلَهَ إِلَّا اللهُ، وَاللهُ أَكْبَرُ، وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللهِ.', virtue: 'هنّ الباقيات الصالحات وأحب الكلام إلى الله', targetCount: 33 },
      { id: 'thu_2', title: 'كلمة التوحيد المنجية', arabicText: 'لَا إِلَهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ.', virtue: 'كانت له عدل عشر رقاب وكتبت له مائة حسنة وحرزاً يومه ذلك', targetCount: 33 },
      { id: 'thu_3', title: 'دعاء ليلة الجمعة المباركة', arabicText: 'اللَّهُمَّ اخْتِمْ لَنَا هَذَا الْأُسْبُوعَ بِمَغْفِرَةٍ تَمْحُو بِهَا ذُنُوبَنَا، وَرَحْمَةٍ تَشْرَحُ بِهَا صُدُورَنَا، وَتَقَبَّلْ صَالِحَ أَعْمَالِنَا.', virtue: 'استعداد روحي لليلة الجمعة ويومها', targetCount: 3 }
    ]
  },
  5: { // الجمعة
    dayTitle: 'ورد وأذكار يوم الجمعة • سيد الأيام والصلاة على النبي ﷺ',
    themeNote: 'باقة يوم الجمعة المبارك في رحاب جوامع الموصل، الإكثار من الصلاة على النبي ﷺ وساعة الإجابة',
    items: [
      { id: 'fri_1', title: 'الصلاة الإبراهيمية على النبي ﷺ', arabicText: 'اللَّهُمَّ صَلِّ وَسَلِّمْ وَبَارِكْ عَلَى نَبِيِّنَا مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ، كَمَا صَلَّيْتَ وَبَارَكْتَ عَلَى إِبْرَاهِيمَ وَعَلَى آلِ إِبْرَاهِيمَ إِنَّكَ حَمِيدٌ مَجِيدٌ.', virtue: 'قال ﷺ: أكثروا عليّ من الصلاة يوم الجمعة وليلة الجمعة', targetCount: 100 },
      { id: 'fri_2', title: 'دعاء ساعة الإجابة يوم الجمعة', arabicText: 'اللَّهُمَّ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ، يَا حَيُّ يَا قَيُّومُ، أَسْأَلُكَ فِي هَذِهِ السَّاعَةِ الْمُبَارَكَةِ أَنْ تَغْفِرَ لَنَا وَلِوَالِدِينَا، وَأَنْ تَجْعَلَ هَذَا الْعَمَلَ صَدَقَةً جَارِيَةً مَقْبُولَةً عَنْ أَحْمَد أَبُو رَسْلَان.', virtue: 'في الجمعة ساعة لا يوافقها عبد مسلم يسأل الله خيراً إلا أعطاه إياه', targetCount: 7 },
      { id: 'fri_3', title: 'تذكير بسنن يوم الجمعة', arabicText: 'سُنَنُ الْجُمُعَةِ: قِرَاءَةُ سُورَةِ الْكَهْفِ، الِاغْتِسَالُ وَالتَّطَيُّبُ، التَّبْكِيرُ إِلَى الْمَسْجِدِ، وَكَثْرَةُ الصَّلَاةِ عَلَى النَّبِيِّ ﷺ.', virtue: 'من قرأ سورة الكهف يوم الجمعة أضاء له من النور ما بين الجمعتين', targetCount: 1 }
    ]
  },
  6: { // السبت
    dayTitle: 'ورد وأذكار يوم السبت • الشكر والثبات وحسن الخاتمة',
    themeNote: 'باقة يوم السبت لإتمام الأسبوع بشكر النعم والثبات على الطاعة',
    items: [
      { id: 'sat_1', title: 'كلمتان حبيبتان إلى الرحمن', arabicText: 'سُبْحَانَ اللهِ وَبِحَمْدِهِ، سُبْحَانَ اللهِ الْعَظِيمِ.', virtue: 'خفيفتان على اللسان ثقيلتان في الميزان حبيبتان إلى الرحمن', targetCount: 33 },
      { id: 'sat_2', title: 'دعاء الإعانة على الذكر والشكر', arabicText: 'اللَّهُمَّ أَعِنِّي عَلَى ذِكْرِكَ، وَشُكْرِكَ، وَحُسْنِ عِبَادَتِكَ.', virtue: 'وصية النبي ﷺ لمعاذ بن جبل رضي الله عنه دبر كل صلاة', targetCount: 7 },
      { id: 'sat_3', title: 'دعاء الثبات على الدين والهدى', arabicText: 'يَا مُقَلِّبَ الْقُلُوبِ ثَبِّتْ قَلْبِي عَلَى دِينِكَ، وَيَا مُصَرِّفَ الْقُلُوبِ صَرِّفْ قَلْبِي عَلَى طَاعَتِكَ.', virtue: 'أكثر دعاء النبي ﷺ لثبات القلب واليقين', targetCount: 7 }
    ]
  }
};

const MOSUL_HISTORIC_MOSQUES = [
  { id: 'al_nuri', nameArabic: 'الجامع النوري الكبير ومنارة الحدباء', foundedEra: 'تأسس عام 568 هـ / 1172 م (نور الدين زنكي)', locationArabic: 'الموصل القديمة - الساحل الأيمن', architecturalFeature: 'يشتهر بمنارته الحدباء المزخرفة بالآجر الموصلي بسبعة أحزمة هندسية فريدة ومحرابه الرخامي النفيس.', distanceFromCenterKm: 'قلب الموصل القديمة', qiblaNote: 'محرابه التاريخي موجه بدقة فلكية عالية نحو الكعبة المشرفة بزاوية 191.8°.' },
  { id: 'nabi_yunus_mosque', nameArabic: 'جامع النبي يونس (عليه السلام)', foundedEra: 'موقع تاريخي وأثري عريق فوق تل التوبة', locationArabic: 'الساحل الأيسر - حي النبي يونس', architecturalFeature: 'يعلو تلاً أثرياً مطلاً على مدينة الموصل ويعد من أبرز المعالم الروحية والتاريخية لأهل نينوى.', distanceFromCenterKm: '2.8 كم شرق دجلة', qiblaNote: 'منارة وقبة شامخة تشرف على أفق الساحل الأيسر.' },
  { id: 'mosul_grand_mosque', nameArabic: 'جامع الموصل الكبير (جامع الرحمة)', foundedEra: 'صرح معماري حديث وكبير', locationArabic: 'الساحل الأيسر - قرب جسر السويس ومنطقة الثقافة', architecturalFeature: 'أكبر مساجد الموصل مساحةً، يقع على ضفاف نهر دجلة ويتميز بقبابه ومآذنه الشاهقة.', distanceFromCenterKm: '1.5 كم على ضفة دجلة', qiblaNote: 'يستقبل آلاف المصلين في صلوات الجمعة والعيدين والتراويح.' },
  { id: 'nabi_sheet', nameArabic: 'جامع النبي شيت (عليه السلام)', foundedEra: 'القرن الحادي عشر الهجري / السابع عشر الميلادي', locationArabic: 'الساحل الأيمن - قرب الدواسة', architecturalFeature: 'يتميز بعمارته الموصلية الأصيلة واستخدام حجر الفرش الموصلي (المرمر) في أروقته ومحرابه.', distanceFromCenterKm: '1.2 كم جنوب البلدة القديمة', qiblaNote: 'مركز علمي وروحي عريق لتعليم القرآن الكريم والعلوم الشرعية.' },
  { id: 'al_aghwat', nameArabic: 'جامع الأغوات التاريخي', foundedEra: 'تأسس عام 1114 هـ / 1702 م (العهد الجليلي)', locationArabic: 'الساحل الأيمن - باب الجسر القديم', architecturalFeature: 'تحفة معمارية من العهد الجليلي في الموصل، يضم مدرسة دينية تاريخية وقبة مهيبة.', distanceFromCenterKm: '0.9 كم على ضفة النهر', qiblaNote: 'يقع عند مدخل الجسر العتيق المطل على نهر دجلة.' },
  { id: 'umayyad_mosque', nameArabic: 'الجامع الأموي (جامع المصفي)', foundedEra: 'أول مسجد بني في الموصل عام 16 هـ / 637 م', locationArabic: 'الموصل القديمة - محلة الساعة', architecturalFeature: 'أسسه عتبة بن فرقد السلمي، وعُرف لاحقاً بجامع المصفي، ويضم أقدم المحاريب الموصلية.', distanceFromCenterKm: '0.6 كم من الجامع النوري', qiblaNote: 'أول قبلة خُطّت للمسلمين في مدينة الموصل عند فتحها.' }
];

const TASBIH_PHRASES = [
  { arabic: 'سُبْحَانَ اللهِ وَبِحَمْدِهِ', meaning: 'كلمتان خفيفتان على اللسان ثقيلتان في الميزان', target: 33 },
  { arabic: 'الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ', meaning: 'تملأ الميزان حمداً وثناءً', target: 33 },
  { arabic: 'اللهُ أَكْبَرُ كَبِيراً', meaning: 'تكبير وتعظيم لله جل جلاله', target: 33 },
  { arabic: 'أَسْتَغْفِرُ اللهَ وَأَتُوبُ إِلَيْهِ', meaning: 'استغفار وتوبة ومغفرة للذنوب', target: 100 },
  { arabic: 'اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ', meaning: 'من صلى عليّ صلاة صلى الله عليه بها عشراً', target: 33 },
  { arabic: 'لَا إِلَهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ', meaning: 'دعوة نبي الله يونس عليه السلام في نينوى', target: 33 }
];

const ATHKAR_LIST = [
  { id: 'post_1', category: 'أذكار بعد الصلاة', title: 'الاستغفار والسلام', arabicText: 'أَسْتَغْفِرُ اللهَ (ثلاثاً) .. اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ، تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ.', virtue: 'سنة نبوية ثابتة عقب التسليم من كل صلاة مفروضة', targetCount: 1 },
  { id: 'post_2', category: 'أذكار بعد الصلاة', title: 'التسبيح والتحميد والتكبير', arabicText: 'سُبْحَانَ اللهِ، وَالْحَمْدُ لِلَّهِ، وَاللهُ أَكْبَرُ', virtue: 'غُفرت خطاياه وإن كانت مثل زبد البحر', targetCount: 33 },
  { id: 'post_3', category: 'أذكار بعد الصلاة', title: 'تمام المائة بالتهليل', arabicText: 'لَا إِلَهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ.', virtue: 'تمام المائة بعد التسبيح والتحميد والتكبير', targetCount: 1 },
  { id: 'morn_1', category: 'أذكار الصباح', title: 'سيد الاستغفار', arabicText: 'اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ لَكَ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ.', virtue: 'من قالها موقناً بها حين يصبح وحين يمسي كان من أهل الجنة', targetCount: 1 },
  { id: 'morn_2', category: 'أذكار الصباح', title: 'أصبحنا وأصبح الملك لله', arabicText: 'أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ.', virtue: 'حفظ وبركة في أول النهار بعد صلاة الفجر', targetCount: 1 },
  { id: 'morn_3', category: 'أذكار الصباح', title: 'سبحان الله وبحمده', arabicText: 'سُبْحَانَ اللهِ وَبِحَمْدِهِ، عَدَدَ خَلْقِهِ، وَرِضَا نَفْسِهِ، وَزِنَةَ عَرْشِهِ، وَمِدَادَ كَلِمَاتِهِ.', virtue: 'تقال ثلاث مرات بعد صلاة الفجر والشروق', targetCount: 3 },
  { id: 'even_1', category: 'أذكار المساء', title: 'أمسينا وأمسى الملك لله', arabicText: 'أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ وَالْحَمْدُ لِلَّهِ، رَبِّ أَسْأَلُكَ خَيْرَ مَا فِي هَذِهِ اللَّيْلَةِ وَخَيْرَ مَا بَعْدَهَا، وَأَعُوذُ بِكَ مِنْ شَرِّ مَا فِي هَذِهِ اللَّيْلَةِ وَشَرِّ مَا بَعْدَهَا.', virtue: 'حفظ وأمان في المساء والليل', targetCount: 1 },
  { id: 'even_2', category: 'أذكار المساء', title: 'التعوذ بكلمات الله التامات', arabicText: 'أَعُوذُ بِكَلِمَاتِ اللهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ، بِسْمِ اللهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ.', virtue: 'لم يضره شيء في ليلته ولا نهاره', targetCount: 3 },
  { id: 'mosul_1', category: 'أدعية الموصل', title: 'دعاء لأهل الموصل والعراق', arabicText: 'اللَّهُمَّ احْفَظْ مَدِينَةَ الْمَوْصِلِ وَأَهْلَهَا، وَاجْعَلْهَا بَلَداً آمِناً مُطْمَئِنّاً سَخَاءً رَخَاءً، وَاعْمُرْ مَسَاجِدَهَا بِذِكْرِكَ وَطَاعَتِكَ، وَبَارِكْ فِي دِجْلَتِهَا وَأَرْضِهَا وَثِمَارِهَا يَا أَرْحَمَ الرَّاحِمِينَ.', virtue: 'دعاء بالبركة والأمان لأم الربيعين وأهلها', targetCount: 1 },
  { id: 'mosul_2', category: 'أدعية الموصل', title: 'دعاء الصدقة الجارية', arabicText: 'اللَّهُمَّ اغْفِرْ لِعَبْدِكَ أَحْمَد أَبُو رَسْلَان، وَارْفَعْ دَرَجَتَهُ فِي الْمَهْدِيِّينَ، وَافْسَحْ لَهُ فِي قَبْرِهِ وَنَوِّرْ لَهُ فِيهِ، وَاجْعَلْ هَذَا الْعَمَلَ شَافِعاً وَنُوراً وَصَدَقَةً جَارِيَةً لَهُ يَا رَبَّ الْعَالَمِينَ.', virtue: 'دعاء بالرحمة والمغفرة ورفعة الدرجات', targetCount: 1 }
];

const ARABIC_DAYS = ['الأحد', 'الإثنين', 'الثلاثاء', 'الأربعاء', 'الخميس', 'الجمعة', 'السبت'];
const ARABIC_GREG_MONTHS = ['كانون الثاني (يناير)', 'شباط (فبراير)', 'آذار (مارس)', 'نيسان (أبريل)', 'أيار (مايو)', 'حزيران (يونيو)', 'تموز (يوليو)', 'آب (أغسطس)', 'أيلول (سبتمبر)', 'تشرين الأول (أكتوبر)', 'تشرين الثاني (نوفمبر)', 'كانون الأول (ديسمبر)'];
const HIJRI_MONTHS = ['محرم', 'صفر', 'ربيع الأول', 'ربيع الآخر', 'جمادى الأولى', 'جمادى الآخرة', 'رجب', 'شعبان', 'رمضان', 'شوال', 'ذو القعدة', 'ذو الحجة'];

function getMosulNowParts() {
  const now = new Date();
  const utcMs = now.getTime() + now.getTimezoneOffset() * 60000;
  return new Date(utcMs + 3 * 3600000);
}

const initialBaghdadDay = getMosulNowParts().getDay();

// State Management with LocalStorage Persistence
const state = {
  tab: 'PRAYERS',
  settingsTab: 0,
  dayOffset: 0,
  expandedPrayerKey: null,
  districtId: localStorage.getItem('mosul_district_id') || 'old_mosul_nuri',
  methodId: parseInt(localStorage.getItem('mosul_method_id') || '101', 10),
  asrId: parseInt(localStorage.getItem('mosul_asr_id') || '0', 10),
  darkTheme: localStorage.getItem('mosul_dark_theme') !== 'false',
  offsets: JSON.parse(localStorage.getItem('mosul_offsets') || '{"FAJR":0,"SUNRISE":0,"DHUHR":0,"ASR":0,"MAGHRIB":0,"ISHA":0}'),
  notifs: JSON.parse(localStorage.getItem('mosul_notifs') || '{"FAJR":true,"SUNRISE":false,"DHUHR":true,"ASR":true,"MAGHRIB":true,"ISHA":true}'),
  totalTasbih: parseInt(localStorage.getItem('mosul_total_tasbih') || '0', 10),
  sessionTasbih: 0,
  tasbihIndex: 0,
  athkarCategory: 'ورد اليوم المتجدد',
  selectedWeekdayWird: initialBaghdadDay,
  athkarCounts: {},
  motivatorIndex: initialBaghdadDay % PRAYER_MOTIVATORS.length,
  dailyPrayersDone: JSON.parse(localStorage.getItem('mosul_prayers_done') || '{}'),
  rawatibDone: JSON.parse(localStorage.getItem('mosul_rawatib_done') || '{}'),
  compassAzimuth: 191.8,
  onlineCache: {},
  lastTriggeredAdhanKey: null
};

function saveSettings() {
  localStorage.setItem('mosul_district_id', state.districtId);
  localStorage.setItem('mosul_method_id', String(state.methodId));
  localStorage.setItem('mosul_asr_id', String(state.asrId));
  localStorage.setItem('mosul_dark_theme', String(state.darkTheme));
  localStorage.setItem('mosul_offsets', JSON.stringify(state.offsets));
  localStorage.setItem('mosul_notifs', JSON.stringify(state.notifs));
  localStorage.setItem('mosul_total_tasbih', String(state.totalTasbih));
  localStorage.setItem('mosul_prayers_done', JSON.stringify(state.dailyPrayersDone));
  localStorage.setItem('mosul_rawatib_done', JSON.stringify(state.rawatibDone));
}

function toArabicDigits(input) {
  const map = ['٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩'];
  return String(input).replace(/[0-9]/g, (d) => map[+d]);
}

// ============================================================================
// 2. Web Audio API Adhan & Alert Synthesizer (محرك الأذان الصوتي والمرئي)
// ============================================================================
let adhanAudioCtx = null;
let adhanStopFlag = false;
let adhanTimeouts = [];

function playMaqamNote(ctx, freq, startTime, duration, peakGain = 0.18) {
  const osc1 = ctx.createOscillator();
  const osc2 = ctx.createOscillator();
  const gain = ctx.createGain();

  osc1.type = 'sine';
  osc2.type = 'triangle';
  osc1.frequency.setValueAtTime(freq, startTime);
  osc2.frequency.setValueAtTime(freq * 2, startTime);

  gain.gain.setValueAtTime(0.001, startTime);
  gain.gain.exponentialRampToValueAtTime(peakGain, startTime + 0.12);
  gain.gain.exponentialRampToValueAtTime(peakGain * 0.65, startTime + duration * 0.7);
  gain.gain.exponentialRampToValueAtTime(0.0001, startTime + duration);

  osc1.connect(gain);
  osc2.connect(gain);
  gain.connect(ctx.destination);

  osc1.start(startTime);
  osc2.start(startTime);
  osc1.stop(startTime + duration);
  osc2.stop(startTime + duration);
}

window.triggerAdhanAlert = function(prayerNameArabic = 'الظهر', isManualPreview = false) {
  const overlay = document.getElementById('adhan-alert-overlay');
  const titleEl = document.getElementById('adhan-overlay-title');
  const subEl = document.getElementById('adhan-overlay-sub');
  const district = MOSUL_DISTRICTS.find((d) => d.id === state.districtId) || MOSUL_DISTRICTS[0];

  titleEl.textContent = `حان الآن موعد أذان ${prayerNameArabic}`;
  subEl.textContent = `حسب التوقيت المحلي لمدينة الموصل • ${district.nameArabic}`;
  overlay.classList.add('open');

  if (navigator.vibrate) {
    navigator.vibrate([250, 120, 250, 120, 400]);
  }

  // Browser Notification if permitted
  if (!isManualPreview && 'Notification' in window && Notification.permission === 'granted') {
    try {
      new Notification(`حان الآن موعد أذان ${prayerNameArabic} في الموصل`, {
        body: `${district.nameArabic} • حي على الصلاة، حي على الفلاح`,
        icon: './icon-192.png'
      });
    } catch (_) {}
  }

  // Synthesize reverent Maqam Hijaz Adhan Chime & Melodic Takbir via Web Audio API
  stopAdhanAudioOnly();
  adhanStopFlag = false;

  try {
    const AudioContextClass = window.AudioContext || window.webkitAudioContext;
    if (AudioContextClass) {
      adhanAudioCtx = new AudioContextClass();
      const now = adhanAudioCtx.currentTime;

      // Maqam Hijaz scale frequencies (D4, Eb4, F#4, G4, A4, Bb4, C5, D5)
      const hijazPhrase = [
        { f: 293.66, t: 0.0, d: 0.85 },   // D4 - اللـ
        { f: 369.99, t: 0.85, d: 0.85 },  // F#4 - ـه أكـ
        { f: 392.00, t: 1.70, d: 1.10 },  // G4 - ـبر
        { f: 440.00, t: 2.95, d: 0.75 },  // A4 - اللـ
        { f: 392.00, t: 3.70, d: 0.65 },  // G4 - ـه
        { f: 369.99, t: 4.35, d: 0.85 },  // F#4 - أكـ
        { f: 311.13, t: 5.20, d: 0.65 },  // Eb4
        { f: 293.66, t: 5.85, d: 1.45 },  // D4 - ـبر

        // Second phrase: أشهد أن لا إله إلا الله / حي على الصلاة
        { f: 392.00, t: 7.60, d: 0.80 },  // G4
        { f: 440.00, t: 8.40, d: 0.90 },  // A4
        { f: 466.16, t: 9.30, d: 0.85 },  // Bb4
        { f: 440.00, t: 10.15, d: 0.85 }, // A4
        { f: 392.00, t: 11.00, d: 0.80 }, // G4
        { f: 369.99, t: 11.80, d: 0.90 }, // F#4
        { f: 293.66, t: 12.70, d: 1.80 }  // D4
      ];

      hijazPhrase.forEach((n) => {
        playMaqamNote(adhanAudioCtx, n.f, now + n.t, n.d, 0.16);
      });
    }
  } catch (_) {}
};

function stopAdhanAudioOnly() {
  adhanStopFlag = true;
  adhanTimeouts.forEach((id) => clearTimeout(id));
  adhanTimeouts = [];
  if (adhanAudioCtx) {
    try { adhanAudioCtx.close(); } catch (_) {}
    adhanAudioCtx = null;
  }
}

window.dismissAdhanAlert = function() {
  stopAdhanAudioOnly();
  document.getElementById('adhan-alert-overlay').classList.remove('open');
};

// Astronomical Math Helpers
function dsin(d) { return Math.sin((d * Math.PI) / 180); }
function dcos(d) { return Math.cos((d * Math.PI) / 180); }
function dtan(d) { return Math.tan((d * Math.PI) / 180); }
function dasin(x) { return (Math.asin(x) * 180) / Math.PI; }
function dacos(x) { return (Math.acos(x) * 180) / Math.PI; }
function datan(x) { return (Math.atan(x) * 180) / Math.PI; }
function datan2(y, x) { return (Math.atan2(y, x) * 180) / Math.PI; }
function normalizeAngle(a) { const m = a % 360; return m < 0 ? m + 360 : m; }

function gregorianToJulianDay(year, month, day) {
  let y = year, m = month;
  if (m <= 2) { y -= 1; m += 12; }
  const a = Math.floor(y / 100);
  const b = 2 - a + Math.floor(a / 4);
  return Math.floor(365.25 * (y + 4716)) + Math.floor(30.6001 * (m + 1)) + day + b - 1524.5;
}

function gregorianToHijri(year, month, day) {
  const jd = Math.floor(gregorianToJulianDay(year, month, day) + 0.5);
  const lInit = jd - 1948440 + 10632;
  const n = Math.floor((lInit - 1) / 10631);
  const l1 = lInit - 10631 * n + 354;
  const j = Math.floor((10985 - l1) / 5316) * Math.floor((50 * l1) / 17719) + Math.floor(l1 / 5670) * Math.floor((43 * l1) / 15238);
  const l2 = l1 - Math.floor((30 - j) / 15) * Math.floor((17719 * j) / 50) - Math.floor(j / 16) * Math.floor((15238 * j) / 43) + 29;
  const hMonth = Math.floor((24 * l2) / 709);
  const hDay = l2 - Math.floor((709 * hMonth) / 24);
  const hYear = 30 * n + j - 30;
  return {
    day: Math.max(1, Math.min(30, hDay)),
    monthIndex: Math.max(0, Math.min(11, hMonth - 1)),
    year: hYear
  };
}

function computeAstronomicalMinutes(year, month, day, district, method, asrMethod) {
  const tzHours = 3.0;
  const jd = gregorianToJulianDay(year, month, day) - district.longitude / (15.0 * 24.0);
  const d = jd - 2451545.0;

  const g = normalizeAngle(357.529 + 0.98560028 * d);
  const q = normalizeAngle(280.459 + 0.98564736 * d);
  const l = normalizeAngle(q + 1.915 * dsin(g) + 0.020 * dsin(2 * g));
  const e = 23.439 - 0.00000036 * d;
  const ra = ((datan2(dcos(e) * dsin(l), dcos(l)) / 15.0) + 24.0) % 24.0;
  const decl = dasin(dsin(e) * dsin(l));
  let eqTime = q / 15.0 - ra;
  if (eqTime > 12.0) eqTime -= 24.0;
  if (eqTime < -12.0) eqTime += 24.0;

  const solarNoonHours = 12.0 + tzHours - (district.longitude / 15.0) - eqTime;
  const dhuhrHours = solarNoonHours + (2.0 / 60.0);
  const horizonAltitude = 0.833 + 0.0347 * Math.sqrt(Math.max(0, district.elevationMeters));

  function hourAngleForZenith(angleBelow) {
    const num = -dsin(angleBelow) - dsin(district.latitude) * dsin(decl);
    const den = dcos(district.latitude) * dcos(decl);
    const cosH = Math.max(-1.0, Math.min(1.0, num / den));
    return dacos(cosH) / 15.0;
  }

  const sunriseDiff = hourAngleForZenith(horizonAltitude);
  const sunriseHours = solarNoonHours - sunriseDiff;
  const maghribHours = solarNoonHours + sunriseDiff + (2.0 / 60.0);
  const fajrHours = solarNoonHours - hourAngleForZenith(method.fajrAngle);

  const shadowRatio = asrMethod.shadowFactor + dtan(Math.abs(district.latitude - decl));
  const asrAlt = datan(1.0 / shadowRatio);
  const asrNum = dsin(asrAlt) - dsin(district.latitude) * dsin(decl);
  const asrDen = dcos(district.latitude) * dcos(decl);
  const asrDiff = dacos(Math.max(-1.0, Math.min(1.0, asrNum / asrDen))) / 15.0;
  const asrHours = solarNoonHours + asrDiff;

  const ishaHours = method.ishaFixedMinutes > 0
    ? maghribHours + (method.ishaFixedMinutes / 60.0)
    : solarNoonHours + hourAngleForZenith(method.ishaAngle);

  function toMin(h) {
    const norm = ((h % 24.0) + 24.0) % 24.0;
    return Math.round(norm * 60.0) % 1440;
  }

  return {
    FAJR: toMin(fajrHours),
    SUNRISE: toMin(sunriseHours),
    DHUHR: toMin(dhuhrHours),
    ASR: toMin(asrHours),
    MAGHRIB: toMin(maghribHours),
    ISHA: toMin(ishaHours)
  };
}

function format12h(totalMin) {
  const mNorm = ((totalMin % 1440) + 1440) % 1440;
  const h24 = Math.floor(mNorm / 60);
  const m = mNorm % 60;
  const period = h24 < 12 ? 'ص' : 'م';
  const h12 = h24 === 0 ? 12 : (h24 > 12 ? h24 - 12 : h24);
  const str = `${String(h12).padStart(2, '0')}:${String(m).padStart(2, '0')}`;
  return { formatted: toArabicDigits(str), period };
}

function formatCountdown(sec) {
  const h = Math.floor(sec / 3600);
  const m = Math.floor((sec % 3600) / 60);
  const s = Math.floor(sec % 60);
  return toArabicDigits(`${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`);
}

function calculateQiblaBearing(lat, lng) {
  const latRad = (lat * Math.PI) / 180;
  const lngRad = (lng * Math.PI) / 180;
  const kLat = (KAABA_LAT * Math.PI) / 180;
  const kLng = (KAABA_LNG * Math.PI) / 180;
  const dLng = kLng - lngRad;
  const y = Math.sin(dLng);
  const x = Math.cos(latRad) * Math.tan(kLat) - Math.sin(latRad) * Math.cos(dLng);
  return ((Math.atan2(y, x) * 180) / Math.PI + 360) % 360;
}

function calculateDistanceToKaabaKm(lat, lng) {
  const R = 6371;
  const dLat = ((KAABA_LAT - lat) * Math.PI) / 180;
  const dLng = ((KAABA_LNG - lng) * Math.PI) / 180;
  const a = Math.sin(dLat / 2) ** 2 + Math.cos((lat * Math.PI) / 180) * Math.cos((KAABA_LAT * Math.PI) / 180) * Math.sin(dLng / 2) ** 2;
  return Math.round(R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
}

function getCurrentSchedule() {
  const district = MOSUL_DISTRICTS.find((d) => d.id === state.districtId) || MOSUL_DISTRICTS[0];
  const method = CALCULATION_METHODS.find((m) => m.id === state.methodId) || CALCULATION_METHODS[0];
  const asrMethod = ASR_METHODS.find((a) => a.id === state.asrId) || ASR_METHODS[0];

  const mosulNow = getMosulNowParts();
  const targetDate = new Date(mosulNow.getTime() + state.dayOffset * 86400000);
  const year = targetDate.getFullYear();
  const month = targetDate.getMonth() + 1;
  const day = targetDate.getDate();
  const weekdayIndex = targetDate.getDay();
  const dayOfWeek = ARABIC_DAYS[weekdayIndex];

  const cacheKey = `${district.id}_${method.id}_${asrMethod.id}_${year}_${month}_${day}`;
  const rawMinutes = state.onlineCache[cacheKey] || computeAstronomicalMinutes(year, month, day, district, method, asrMethod);

  const nowMinutes = mosulNow.getHours() * 60 + mosulNow.getMinutes();
  const nowSeconds = nowMinutes * 60 + mosulNow.getSeconds();

  const entries = PRAYERS_META.map((meta) => {
    const offset = state.offsets[meta.key] || 0;
    const totalMin = (rawMinutes[meta.key] + offset + 1440) % 1440;
    const t = format12h(totalMin);
    return {
      ...meta,
      totalMin,
      formatted12h: t.formatted,
      periodLabel: t.period,
      offset,
      notifEnabled: !!state.notifs[meta.key]
    };
  });

  // Check if a prayer time has just arrived in Mosul to trigger automatic Adhan Alert
  if (state.dayOffset === 0 && mosulNow.getSeconds() < 3) {
    entries.forEach((e) => {
      if (e.key !== 'SUNRISE' && e.notifEnabled && e.totalMin === nowMinutes) {
        const triggerKey = `${year}_${month}_${day}_${e.key}`;
        if (state.lastTriggeredAdhanKey !== triggerKey) {
          state.lastTriggeredAdhanKey = triggerKey;
          triggerAdhanAlert(e.arabicName, false);
        }
      }
    });
  }

  const nextEntry = entries.find((e) => e.totalMin * 60 > nowSeconds) || entries[0];
  const prevEntries = entries.filter((e) => e.totalMin * 60 <= nowSeconds);
  const prevEntry = prevEntries.length > 0 ? prevEntries[prevEntries.length - 1] : entries[entries.length - 1];

  const nextSec = nextEntry.totalMin * 60;
  const secondsUntilNext = nextSec > nowSeconds ? nextSec - nowSeconds : (86400 - nowSeconds) + nextSec;
  const prevSec = prevEntry.totalMin * 60;
  const intervalSec = Math.max(60, nextSec > prevSec ? nextSec - prevSec : (86400 - prevSec) + nextSec);
  const progress = Math.max(0, Math.min(1, (intervalSec - secondsUntilNext) / intervalSec));

  const fajrMin = entries[0].totalMin;
  const maghribMin = entries[4].totalMin;
  const imsakMin = (fajrMin - 10 + 1440) % 1440;
  const nightDur = ((fajrMin + 1440) - maghribMin) % 1440;
  const midnightMin = (maghribMin + Math.floor(nightDur / 2)) % 1440;
  const lastThirdMin = (maghribMin + Math.floor((nightDur * 2) / 3)) % 1440;

  const imsakT = format12h(imsakMin);
  const midT = format12h(midnightMin);
  const thirdT = format12h(lastThirdMin);

  const hijri = gregorianToHijri(year, month, day);
  const hijriMonthName = HIJRI_MONTHS[hijri.monthIndex];

  return {
    district,
    method,
    asrMethod,
    weekdayIndex,
    dayOfWeek,
    hijriFormatted: `${toArabicDigits(hijri.day)} ${hijriMonthName} ${toArabicDigits(hijri.year)} هـ`,
    gregorianFormatted: `${toArabicDigits(day)} ${ARABIC_GREG_MONTHS[month - 1]} ${toArabicDigits(year)} م`,
    entries,
    nextEntry,
    secondsUntilNext,
    progress,
    imsak: `${imsakT.formatted} ${imsakT.period}`,
    midnight: `${midT.formatted} ${midT.period}`,
    lastThird: `${thirdT.formatted} ${thirdT.period}`
  };
}

// UI Rendering Functions
function renderHome() {
  const sched = getCurrentSchedule();
  document.getElementById('district-chip-name').textContent = sched.district.nameArabic;
  document.getElementById('district-side-badge').textContent = sched.district.sideLabel;
  document.getElementById('next-prayer-name').textContent = `صلاة ${sched.nextEntry.arabicName}`;
  document.getElementById('next-prayer-time').textContent = `${sched.nextEntry.formatted12h} ${sched.nextEntry.periodLabel}`;
  document.getElementById('next-prayer-subtitle').textContent = sched.nextEntry.subtitle;
  document.getElementById('hijri-date-display').textContent = `${sched.dayOfWeek} • ${sched.hijriFormatted}`;
  document.getElementById('greg-date-display').textContent = sched.gregorianFormatted;
  document.getElementById('iqama-pill-display').textContent = `الإقامة ${toArabicDigits(sched.nextEntry.iqamaMinutes)} د`;

  // Hero sky theme class
  const banner = document.getElementById('hero-banner');
  banner.className = 'hero-banner';
  if (sched.nextEntry.key === 'FAJR' || sched.nextEntry.key === 'SUNRISE') banner.classList.add('sky-fajr');
  else if (sched.nextEntry.key === 'MAGHRIB') banner.classList.add('sky-maghrib');
  else if (sched.nextEntry.key === 'ISHA') banner.classList.add('sky-isha');
  else banner.classList.add('sky-dhuhr');

  updateCountdownOnly(sched);

  document.getElementById('aux-imsak').textContent = sched.imsak;
  document.getElementById('aux-midnight').textContent = sched.midnight;
  document.getElementById('aux-last-third').textContent = sched.lastThird;

  const navLabel = document.getElementById('date-nav-label');
  const resetBtn = document.getElementById('reset-today-btn');
  if (state.dayOffset === 0) {
    navLabel.textContent = `مواقيت اليوم (${sched.dayOfWeek})`;
    resetBtn.style.display = 'none';
  } else {
    navLabel.textContent = `${sched.dayOfWeek} • ${sched.gregorianFormatted}`;
    resetBtn.style.display = 'inline-block';
  }

  // Render Prayer Cards
  const listEl = document.getElementById('prayers-list');
  listEl.innerHTML = sched.entries.map((e) => {
    const isNext = e.key === sched.nextEntry.key;
    const isExpanded = state.expandedPrayerKey === e.key;
    const offsetStr = e.offset !== 0 ? `<div style="font-size:0.72rem;color:var(--secondary-color)">تعديل ${e.offset > 0 ? '+' : ''}${toArabicDigits(e.offset)} د</div>` : '';
    const subText = e.iqamaMinutes > 0
      ? `الإقامة بعد ${toArabicDigits(e.iqamaMinutes)} دقيقة • السنن: ${e.sunnahBefore}`
      : e.subtitle;

    return `
      <div class="prayer-card ${isNext ? 'is-next' : ''} ${isExpanded ? 'expanded' : ''}" onclick="togglePrayerExpand('${e.key}')">
        <div class="prayer-card-main">
          <div class="prayer-info-right">
            <div class="prayer-icon-circle">${e.icon}</div>
            <div>
              <div class="prayer-name-row">
                <span class="prayer-name-text">${e.arabicName}</span>
                ${isNext ? '<span class="next-badge">الصلاة القادمة</span>' : ''}
              </div>
              <div class="prayer-meta-text">${subText}</div>
            </div>
          </div>

          <div class="prayer-time-left">
            <div style="text-align:left">
              <div>
                <span class="prayer-time-digits">${e.formatted12h}</span>
                <span class="prayer-period-label">${e.periodLabel}</span>
              </div>
              ${offsetStr}
            </div>
            <button class="notif-btn ${e.notifEnabled ? '' : 'off'}" onclick="event.stopPropagation(); togglePrayerNotif('${e.key}', '${e.arabicName}')" title="تفعيل/معاينة تنبيه الأذان">
              🔔
            </button>
          </div>
        </div>
        <div class="prayer-detail-box">
          <div class="sunnah-row">
            <span>السنة القبلية: ${e.sunnahBefore}</span>
            <span>السنة البعدية: ${e.sunnahAfter}</span>
          </div>
          <p style="color:var(--text-secondary);margin-bottom:8px">${e.heritageNote}</p>
          <button class="today-link-btn" onclick="event.stopPropagation(); triggerAdhanAlert('${e.arabicName}', true)">🔊 استماع ومعاينة تنبيه أذان ${e.arabicName}</button>
        </div>
      </div>
    `;
  }).join('');

  // Render Prayer Motivator & Spiritual Illumination Card (مشجعات ومحفزات الصلاة والسنن الرواتب)
  renderMotivatorCard();

  document.getElementById('footer-method-title').textContent = sched.method.titleArabic;
  document.getElementById('footer-method-sub').textContent = `${sched.asrMethod.titleArabic} • الارتفاع ${toArabicDigits(sched.district.elevationMeters)}م فوق سطح البحر`;
}

function renderMotivatorCard() {
  const motivator = PRAYER_MOTIVATORS[state.motivatorIndex % PRAYER_MOTIVATORS.length];
  document.getElementById('motivator-hadith').textContent = motivator.hadith;
  document.getElementById('motivator-source').textContent = motivator.source;
  document.getElementById('motivator-tip').textContent = `💡 إضاءة اليوم: ${motivator.tip}`;

  const fivePrayers = [
    { key: 'FAJR', name: 'الفجر' },
    { key: 'DHUHR', name: 'الظهر' },
    { key: 'ASR', name: 'العصر' },
    { key: 'MAGHRIB', name: 'المغرب' },
    { key: 'ISHA', name: 'العشاء' }
  ];

  const fardGrid = document.getElementById('fard-tracker-grid');
  fardGrid.innerHTML = fivePrayers.map((p) => {
    const done = !!state.dailyPrayersDone[p.key];
    return `
      <div class="tracker-pill ${done ? 'done' : ''}" onclick="toggleFardDone('${p.key}')">
        <div style="font-size:1.05rem">${done ? '✅' : '🕌'}</div>
        <div style="font-size:0.78rem;font-weight:700;margin-top:2px">${p.name}</div>
      </div>
    `;
  }).join('');

  const rawatibStrip = document.getElementById('rawatib-tracker-strip');
  rawatibStrip.innerHTML = RAWATIB_ITEMS.map((r) => {
    const done = !!state.rawatibDone[r.id];
    return `
      <div class="rawatib-chip ${done ? 'done' : ''}" onclick="toggleRawatibDone('${r.id}')">
        ${done ? '★ ' : '☆ '}${r.label}
      </div>
    `;
  }).join('');
}

window.nextMotivatorQuote = function() {
  state.motivatorIndex = (state.motivatorIndex + 1) % PRAYER_MOTIVATORS.length;
  renderMotivatorCard();
};

window.toggleFardDone = function(key) {
  state.dailyPrayersDone[key] = !state.dailyPrayersDone[key];
  saveSettings();
  if (navigator.vibrate) navigator.vibrate(20);
  renderMotivatorCard();
};

window.toggleRawatibDone = function(id) {
  state.rawatibDone[id] = !state.rawatibDone[id];
  saveSettings();
  if (navigator.vibrate) navigator.vibrate(15);
  renderMotivatorCard();
};

function updateCountdownOnly(schedOpt) {
  const sched = schedOpt || getCurrentSchedule();
  const el = document.getElementById('countdown-digits');
  if (el) el.textContent = formatCountdown(sched.secondsUntilNext);

  const circle = document.getElementById('countdown-progress-circle');
  if (circle) {
    const circumference = 2 * Math.PI * 44;
    const offset = circumference * (1 - sched.progress);
    circle.style.strokeDasharray = `${circumference}`;
    circle.style.strokeDashoffset = `${offset}`;
  }
}

function renderQibla() {
  const district = MOSUL_DISTRICTS.find((d) => d.id === state.districtId) || MOSUL_DISTRICTS[0];
  const bearing = calculateQiblaBearing(district.latitude, district.longitude);
  const distKm = calculateDistanceToKaabaKm(district.latitude, district.longitude);

  document.getElementById('qibla-subtitle').textContent = `${district.nameArabic} • زاوية القبلة ${toArabicDigits(Math.round(bearing))}° جنوب الغرب`;
  document.getElementById('metric-qibla-deg').textContent = `${toArabicDigits(Math.round(bearing))}°`;
  document.getElementById('metric-heading-deg').textContent = `${toArabicDigits(Math.round(state.compassAzimuth))}°`;
  document.getElementById('metric-kaaba-km').textContent = `${toArabicDigits(distKm)} كم`;

  const rawDiff = Math.abs(state.compassAzimuth - bearing) % 360;
  const err = rawDiff > 180 ? 360 - rawDiff : rawDiff;
  const aligned = err <= 6.5;

  const qiblaCard = document.getElementById('qibla-main-card');
  const statusText = document.getElementById('qibla-status-text');
  if (aligned) {
    qiblaCard.classList.add('aligned');
    statusText.textContent = 'أنت تتجه الآن نحو الكعبة المشرفة مباشرة ✓';
  } else {
    qiblaCard.classList.remove('aligned');
    statusText.textContent = 'أدر الهاتف أو المؤشر حتى ينطبق السهم الذهبي مع العلامة العلوية';
  }

  const dialGroup = document.getElementById('compass-rotating-dial');
  const needleGroup = document.getElementById('compass-qibla-needle');
  if (dialGroup) dialGroup.setAttribute('transform', `rotate(${-state.compassAzimuth} 124 124)`);
  if (needleGroup) needleGroup.setAttribute('transform', `rotate(${bearing} 124 124)`);

  const mosquesContainer = document.getElementById('mosques-list');
  if (!mosquesContainer.innerHTML.trim()) {
    mosquesContainer.innerHTML = MOSUL_HISTORIC_MOSQUES.map((m) => `
      <div class="mosque-card">
        <div style="display:flex;justify-content:space-between;align-items:center;gap:8px;margin-bottom:4px">
          <h3 style="font-size:1.05rem;color:var(--primary-color)">${m.nameArabic}</h3>
          <span class="side-badge">${m.distanceFromCenterKm}</span>
        </div>
        <div style="font-size:0.78rem;color:var(--text-secondary);margin-bottom:8px">${m.locationArabic} • ${m.foundedEra}</div>
        <p style="font-size:0.88rem;margin-bottom:6px">${m.architecturalFeature}</p>
        <div style="font-size:0.8rem;color:var(--secondary-color);font-weight:600">${m.qiblaNote}</div>
      </div>
    `).join('');
  }
}

function renderAthkar() {
  const phrase = TASBIH_PHRASES[state.tasbihIndex] || TASBIH_PHRASES[0];
  document.getElementById('total-tasbih-label').textContent = `إجمالي تسبيحاتك المحفوظة: ${toArabicDigits(state.totalTasbih)}`;
  document.getElementById('session-tasbih-digits').textContent = toArabicDigits(state.sessionTasbih);
  document.getElementById('tasbih-target-label').textContent = `الهدف ${toArabicDigits(phrase.target)}`;
  document.getElementById('current-tasbih-arabic').textContent = phrase.arabic;
  document.getElementById('current-tasbih-meaning').textContent = phrase.meaning;

  const tasbihChips = document.getElementById('tasbih-phrases-chips');
  tasbihChips.innerHTML = TASBIH_PHRASES.map((p, idx) => `
    <button class="chip-btn ${idx === state.tasbihIndex ? 'active' : ''}" onclick="selectTasbihPhrase(${idx})">${p.arabic}</button>
  `).join('');

  const categories = ['ورد اليوم المتجدد', 'أذكار بعد الصلاة', 'أذكار الصباح', 'أذكار المساء', 'أدعية الموصل'];
  document.getElementById('athkar-cat-chips').innerHTML = categories.map((cat) => `
    <button class="chip-btn ${cat === state.athkarCategory ? 'active' : ''}" onclick="selectAthkarCat('${cat}')">${cat}</button>
  `).join('');

  const weekdayPickerBox = document.getElementById('weekday-wird-picker');
  const cardsListEl = document.getElementById('athkar-cards-list');

  if (state.athkarCategory === 'ورد اليوم المتجدد') {
    weekdayPickerBox.style.display = 'block';
    const dayPackage = WEEKLY_RENEWED_ATHKAR[state.selectedWeekdayWird] || WEEKLY_RENEWED_ATHKAR[0];
    const todayBaghdadIdx = getMosulNowParts().getDay();

    document.getElementById('weekday-chips-row').innerHTML = ARABIC_DAYS.map((dName, idx) => `
      <button class="chip-btn ${idx === state.selectedWeekdayWird ? 'active' : ''}" onclick="selectWeekdayWird(${idx})">
        ${dName}${idx === todayBaghdadIdx ? ' (اليوم)' : ''}
      </button>
    `).join('');

    document.getElementById('weekday-wird-title').textContent = dayPackage.dayTitle;
    document.getElementById('weekday-wird-note').textContent = dayPackage.themeNote;

    cardsListEl.innerHTML = dayPackage.items.map((item) => renderAthkarCardHtml(item)).join('');
  } else {
    weekdayPickerBox.style.display = 'none';
    const filtered = ATHKAR_LIST.filter((a) => a.category === state.athkarCategory);
    cardsListEl.innerHTML = filtered.map((item) => renderAthkarCardHtml(item)).join('');
  }
}

function renderAthkarCardHtml(item) {
  const count = state.athkarCounts[item.id] || 0;
  const done = count >= item.targetCount;
  const pct = Math.min(100, Math.round((count / item.targetCount) * 100));
  return `
    <div class="athkar-card ${done ? 'completed' : ''}" onclick="incrementAthkar('${item.id}', ${item.targetCount})">
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px">
        <strong style="color:var(--primary-color)">${item.title}</strong>
        <span class="side-badge">${done ? 'تم بحمد الله ✓ ' : ''}${toArabicDigits(count)} / ${toArabicDigits(item.targetCount)}</span>
      </div>
      <p class="amiri-font" style="font-size:1.34rem;line-height:2;margin-bottom:8px">${item.arabicText}</p>
      <div style="font-size:0.8rem;color:var(--text-secondary)">${item.virtue}</div>
      <div class="progress-bar-track">
        <div class="progress-bar-fill" style="width:${pct}%"></div>
      </div>
    </div>
  `;
}

window.selectWeekdayWird = function(dayIdx) {
  state.selectedWeekdayWird = dayIdx;
  renderAthkar();
};

function renderSettingsAndCalendar() {
  const isSettings = state.settingsTab === 0;
  document.getElementById('subtab-settings-btn').classList.toggle('active', isSettings);
  document.getElementById('subtab-calendar-btn').classList.toggle('active', !isSettings);
  document.getElementById('settings-pane').style.display = isSettings ? 'block' : 'none';
  document.getElementById('calendar-pane').style.display = isSettings ? 'none' : 'block';

  if (isSettings) {
    document.getElementById('settings-districts-list').innerHTML = MOSUL_DISTRICTS.map((d) => `
      <div class="district-option ${d.id === state.districtId ? 'selected' : ''}" onclick="selectDistrict('${d.id}')">
        <div>
          <div style="font-weight:700">${d.nameArabic}</div>
          <div style="font-size:0.78rem;color:var(--text-secondary)">${d.sideLabel} • ${d.landmarkArabic}</div>
        </div>
        ${d.id === state.districtId ? '<span style="color:var(--primary-color);font-weight:700">مُختار</span>' : ''}
      </div>
    `).join('');

    document.getElementById('settings-methods-list').innerHTML = CALCULATION_METHODS.map((m) => `
      <div class="district-option ${m.id === state.methodId ? 'selected' : ''}" onclick="selectMethod(${m.id})">
        <div>
          <div style="font-weight:700">${m.titleArabic}</div>
          <div style="font-size:0.78rem;color:var(--text-secondary)">${m.descriptionArabic}</div>
        </div>
      </div>
    `).join('');

    document.getElementById('settings-asr-list').innerHTML = ASR_METHODS.map((a) => `
      <div class="district-option ${a.id === state.asrId ? 'selected' : ''}" onclick="selectAsr(${a.id})">
        <div>
          <div style="font-weight:700">${a.titleArabic}</div>
          <div style="font-size:0.78rem;color:var(--text-secondary)">${a.descriptionArabic}</div>
        </div>
      </div>
    `).join('');

    document.getElementById('settings-offsets-list').innerHTML = PRAYERS_META.map((p) => {
      const off = state.offsets[p.key] || 0;
      const sign = off > 0 ? '+' : '';
      return `
        <div style="display:flex;justify-content:space-between;align-items:center;padding:8px 0;border-bottom:1px solid var(--outline-color)">
          <span style="font-weight:700">${p.icon} ${p.arabicName}</span>
          <div style="display:flex;align-items:center;gap:10px">
            <button class="icon-btn" style="width:34px;height:34px" onclick="adjustOffset('${p.key}', -1)">-</button>
            <span style="min-width:56px;text-align:center;font-weight:700">${sign}${toArabicDigits(off)} د</span>
            <button class="icon-btn" style="width:34px;height:34px" onclick="adjustOffset('${p.key}', 1)">+</button>
          </div>
        </div>
      `;
    }).join('');
  } else {
    const district = MOSUL_DISTRICTS.find((d) => d.id === state.districtId) || MOSUL_DISTRICTS[0];
    const method = CALCULATION_METHODS.find((m) => m.id === state.methodId) || CALCULATION_METHODS[0];
    const asrMethod = ASR_METHODS.find((a) => a.id === state.asrId) || ASR_METHODS[0];
    const mosulNow = getMosulNowParts();
    const year = mosulNow.getFullYear();
    const month = mosulNow.getMonth() + 1;
    const todayDay = mosulNow.getDate();
    const daysInMonth = new Date(year, month, 0).getDate();

    let rowsHtml = '';
    for (let dayNum = 1; dayNum <= daysInMonth; dayNum++) {
      const raw = computeAstronomicalMinutes(year, month, dayNum, district, method, asrMethod);
      const hijri = gregorianToHijri(year, month, dayNum);
      const isToday = dayNum === todayDay;
      const f = (k) => format12h(raw[k] + (state.offsets[k] || 0)).formatted;
      rowsHtml += `
        <tr class="${isToday ? 'is-today' : ''}">
          <td>${toArabicDigits(dayNum)} (${toArabicDigits(hijri.day)} ${HIJRI_MONTHS[hijri.monthIndex]})</td>
          <td>${f('FAJR')}</td>
          <td>${f('SUNRISE')}</td>
          <td>${f('DHUHR')}</td>
          <td>${f('ASR')}</td>
          <td>${f('MAGHRIB')}</td>
          <td>${f('ISHA')}</td>
        </tr>
      `;
    }
    document.getElementById('monthly-timetable-body').innerHTML = rowsHtml;
  }
}

// Event Handlers
window.switchTab = function(tab) {
  state.tab = tab;
  document.querySelectorAll('.screen-section').forEach((s) => s.classList.remove('active'));
  document.getElementById(`screen-${tab}`).classList.add('active');
  document.querySelectorAll('.nav-item').forEach((b) => b.classList.toggle('active', b.dataset.tab === tab));
  if (tab === 'PRAYERS') renderHome();
  else if (tab === 'QIBLA') renderQibla();
  else if (tab === 'ATHKAR') renderAthkar();
  else if (tab === 'SETTINGS') renderSettingsAndCalendar();
};

window.changeDayOffset = function(delta) {
  state.dayOffset = Math.max(-60, Math.min(60, state.dayOffset + delta));
  renderHome();
};

window.resetToday = function() {
  state.dayOffset = 0;
  renderHome();
};

window.togglePrayerExpand = function(key) {
  state.expandedPrayerKey = state.expandedPrayerKey === key ? null : key;
  renderHome();
};

window.togglePrayerNotif = function(key, arabicName) {
  state.notifs[key] = !state.notifs[key];
  saveSettings();
  if (state.notifs[key] && 'Notification' in window && Notification.permission === 'default') {
    Notification.requestPermission();
  }
  renderHome();
};

window.toggleTheme = function() {
  state.darkTheme = !state.darkTheme;
  document.body.classList.toggle('light-theme', !state.darkTheme);
  saveSettings();
};

window.openDistrictModal = function() {
  const modal = document.getElementById('district-modal');
  const list = document.getElementById('modal-districts-list');
  list.innerHTML = MOSUL_DISTRICTS.map((d) => `
    <div class="district-option ${d.id === state.districtId ? 'selected' : ''}" onclick="selectDistrict('${d.id}'); closeDistrictModal();">
      <div>
        <div style="font-weight:700">${d.nameArabic}</div>
        <div style="font-size:0.78rem;color:var(--text-secondary)">${d.sideLabel} • ${d.landmarkArabic}</div>
      </div>
      ${d.id === state.districtId ? '<span style="color:var(--primary-color);font-weight:700">مُختار</span>' : ''}
    </div>
  `).join('');
  modal.classList.add('open');
};

window.closeDistrictModal = function() {
  document.getElementById('district-modal').classList.remove('open');
};

window.selectDistrict = function(id) {
  state.districtId = id;
  saveSettings();
  renderHome();
  if (state.tab === 'SETTINGS') renderSettingsAndCalendar();
};

window.selectMethod = function(id) {
  state.methodId = id;
  saveSettings();
  renderSettingsAndCalendar();
};

window.selectAsr = function(id) {
  state.asrId = id;
  saveSettings();
  renderSettingsAndCalendar();
};

window.adjustOffset = function(key, delta) {
  state.offsets[key] = Math.max(-30, Math.min(30, (state.offsets[key] || 0) + delta));
  saveSettings();
  renderSettingsAndCalendar();
};

window.resetAllOffsets = function() {
  state.offsets = { FAJR: 0, SUNRISE: 0, DHUHR: 0, ASR: 0, MAGHRIB: 0, ISHA: 0 };
  saveSettings();
  renderSettingsAndCalendar();
};

window.switchSettingsSubtab = function(idx) {
  state.settingsTab = idx;
  renderSettingsAndCalendar();
};

window.onCompassSliderChange = function(val) {
  state.compassAzimuth = parseFloat(val);
  renderQibla();
};

window.requestHardwareCompass = function() {
  if (typeof DeviceOrientationEvent !== 'undefined' && typeof DeviceOrientationEvent.requestPermission === 'function') {
    DeviceOrientationEvent.requestPermission()
      .then((res) => {
        if (res === 'granted') bindDeviceOrientation();
      })
      .catch(() => {});
  } else {
    bindDeviceOrientation();
  }
};

function bindDeviceOrientation() {
  window.addEventListener('deviceorientationabsolute', handleOrientation, true);
  window.addEventListener('deviceorientation', handleOrientation, true);
}

function handleOrientation(e) {
  let heading = null;
  if (typeof e.webkitCompassHeading === 'number') {
    heading = e.webkitCompassHeading;
  } else if (typeof e.alpha === 'number') {
    heading = (360 - e.alpha) % 360;
  }
  if (heading !== null) {
    state.compassAzimuth = heading;
    const slider = document.getElementById('compass-slider');
    if (slider) slider.value = Math.round(heading);
    if (state.tab === 'QIBLA') renderQibla();
  }
}

window.selectTasbihPhrase = function(idx) {
  state.tasbihIndex = idx;
  state.sessionTasbih = 0;
  renderAthkar();
};

window.incrementTasbih = function() {
  state.sessionTasbih += 1;
  state.totalTasbih += 1;
  saveSettings();
  if (navigator.vibrate) navigator.vibrate(25);
  renderAthkar();
};

window.resetTasbihSession = function() {
  state.sessionTasbih = 0;
  renderAthkar();
};

window.selectAthkarCat = function(cat) {
  state.athkarCategory = cat;
  renderAthkar();
};

window.incrementAthkar = function(id, target) {
  const cur = state.athkarCounts[id] || 0;
  if (cur < target) {
    state.athkarCounts[id] = cur + 1;
    if (navigator.vibrate) navigator.vibrate(18);
    renderAthkar();
  }
};

window.syncOnlineTimings = async function() {
  const statusEl = document.getElementById('sync-status-banner');
  statusEl.textContent = 'جاري مطابقة المواقيت مع الخادم الفلكي لمدينة الموصل...';
  statusEl.classList.add('visible');

  try {
    const district = MOSUL_DISTRICTS.find((d) => d.id === state.districtId) || MOSUL_DISTRICTS[0];
    const method = CALCULATION_METHODS.find((m) => m.id === state.methodId) || CALCULATION_METHODS[0];
    const asrMethod = ASR_METHODS.find((a) => a.id === state.asrId) || ASR_METHODS[0];
    const mosulNow = getMosulNowParts();
    const targetDate = new Date(mosulNow.getTime() + state.dayOffset * 86400000);
    const d = String(targetDate.getDate()).padStart(2, '0');
    const m = String(targetDate.getMonth() + 1).padStart(2, '0');
    const y = targetDate.getFullYear();
    const apiMethod = method.id === 101 ? 3 : method.id;

    const url = `https://api.aladhan.com/v1/timings/${d}-${m}-${y}?latitude=${district.latitude}&longitude=${district.longitude}&method=${apiMethod}&school=${asrMethod.id}&timezonestring=Asia/Baghdad`;
    const res = await fetch(url);
    const json = await res.json();
    const t = json?.data?.timings;
    if (t) {
      const parseMin = (str) => {
        const clean = str.split(' ')[0].split(':');
        return parseInt(clean[0], 10) * 60 + parseInt(clean[1], 10);
      };
      const cacheKey = `${district.id}_${method.id}_${asrMethod.id}_${y}_${targetDate.getMonth() + 1}_${targetDate.getDate()}`;
      state.onlineCache[cacheKey] = {
        FAJR: parseMin(t.Fajr),
        SUNRISE: parseMin(t.Sunrise),
        DHUHR: parseMin(t.Dhuhr),
        ASR: parseMin(t.Asr),
        MAGHRIB: parseMin(t.Maghrib),
        ISHA: parseMin(t.Isha)
      };
      statusEl.textContent = 'تمت مطابقة المواقيت بنجاح مع الخادم الفلكي لمدينة الموصل ✓';
      renderHome();
    }
  } catch (_) {
    statusEl.textContent = 'يعمل التطبيق بالحساب الفلكي المحلي الدقيق لمدينة الموصل (بدون إنترنت) ✓';
  }
  setTimeout(() => statusEl.classList.remove('visible'), 4500);
};

// PWA Install Prompt Handling
let deferredPrompt = null;
window.addEventListener('beforeinstallprompt', (e) => {
  e.preventDefault();
  deferredPrompt = e;
  const banner = document.getElementById('pwa-install-banner');
  if (banner) banner.classList.remove('hidden');
});

window.installPwaApp = async function() {
  if (deferredPrompt) {
    deferredPrompt.prompt();
    await deferredPrompt.userChoice;
    deferredPrompt = null;
    document.getElementById('pwa-install-banner').classList.add('hidden');
  } else {
    alert('لتثبيت التطبيق على هاتفك: اضغط على قائمة المتصفح (⋮ أو زر المشاركة) ثم اختر «إضافة إلى الشاشة الرئيسية / تثبيت التطبيق».');
  }
};

// Initialization
document.addEventListener('DOMContentLoaded', () => {
  document.body.classList.toggle('light-theme', !state.darkTheme);
  renderHome();
  bindDeviceOrientation();
  setInterval(() => updateCountdownOnly(), 1000);

  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register('./sw.js').catch(() => {});
  }
});
