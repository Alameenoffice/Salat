// ============================================================================
// مواقيت الموصل - أم الربيعين (Progressive Web App Engine - Works 100% Offline)
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
  { key: 'FAJR', arabicName: 'الفجر', subtitle: 'بزوغ الفجر الصادق على ضفاف دجلة', iqamaMinutes: 25, sunnahBefore: 'ركعتان مؤكدة', sunnahAfter: 'لا توجد', heritageNote: 'يُرفع أذان الفجر في جوامع الموصل بالمقام الموصلي الخاشع متبعاً بتسابيح الفجر الموصلية المتوارثة.' },
  { key: 'SUNRISE', arabicName: 'الشروق', subtitle: 'إشراق الشمس وصلاة الضحى بعد ١٥ دقيقة', iqamaMinutes: 0, sunnahBefore: 'صلاة الإشراق / الضحى', sunnahAfter: 'مثنى مثنى', heritageNote: 'وقت شروق الشمس في سماء أم الربيعين، ويبدأ وقت صلاة الضحى بعد ارتفاع الشمس قيد رمح (حوالي ١٥ دقيقة).' },
  { key: 'DHUHR', arabicName: 'الظهر', subtitle: 'زوال الشمس عن كبد السماء', iqamaMinutes: 15, sunnahBefore: '٤ ركعات مؤكدة', sunnahAfter: 'ركعتان مؤكدة', heritageNote: 'تُقام صلاة الجمعة والظهر في الجامع النوري الكبير وجامع النبي يونس وسائر مساجد الساحلين الأيمن والأيسر.' },
  { key: 'ASR', arabicName: 'العصر', subtitle: 'الصلاة الوسطى في رحاب المساجد', iqamaMinutes: 15, sunnahBefore: '٤ ركعات مستحبة', sunnahAfter: 'لا توجد', heritageNote: 'تشتهر مساجد الموصل بحلقات الذكر وتلاوة القرآن الكريم بين صلاتي العصر والمغرب.' },
  { key: 'MAGHRIB', arabicName: 'المغرب', subtitle: 'غروب الشمس خلف تلال الموصل', iqamaMinutes: 10, sunnahBefore: 'ركعتان خفيفتان', sunnahAfter: 'ركعتان مؤكدة', heritageNote: 'عند غروب الشمس يصدح أذان المغرب في أرجاء الموصل، وتُعمر الموائد في رمضان بالمدقوقة الموصلية وشراب الزبيب.' },
  { key: 'ISHA', arabicName: 'العشاء', subtitle: 'مغيب الشفق الأحمر وسكون الليل', iqamaMinutes: 15, sunnahBefore: 'ركعتان مستحبة', sunnahAfter: 'ركعتان + الوتر', heritageNote: 'تُختم الصلوات الخمس بصلاة العشاء والوتر، وفي شهر رمضان تُقام صلاة التراويح بالتلاوة العراقية العذبة.' }
];

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
  { id: 'mosul_2', category: 'أدعية الموصل', title: 'دعاء نبي الله يونس عليه السلام', arabicText: 'لَا إِلَهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ.', virtue: 'دعوة ذي النون (نبي الله يونس صاحب نينوى) ما دعا بها مكروب إلا فرّج الله كربه', targetCount: 7 }
];

const ARABIC_DAYS = ['الأحد', 'الإثنين', 'الثلاثاء', 'الأربعاء', 'الخميس', 'الجمعة', 'السبت'];
const ARABIC_GREG_MONTHS = ['كانون الثاني (يناير)', 'شباط (فبراير)', 'آذار (مارس)', 'نيسان (أبريل)', 'أيار (مايو)', 'حزيران (يونيو)', 'تموز (يوليو)', 'آب (أغسطس)', 'أيلول (سبتمبر)', 'تشرين الأول (أكتوبر)', 'تشرين الثاني (نوفمبر)', 'كانون الأول (ديسمبر)'];
const HIJRI_MONTHS = ['محرم', 'صفر', 'ربيع الأول', 'ربيع الآخر', 'جمادى الأولى', 'جمادى الآخرة', 'رجب', 'شعبان', 'رمضان', 'شوال', 'ذو القعدة', 'ذو الحجة'];

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
  athkarCategory: 'أذكار بعد الصلاة',
  athkarCounts: {},
  compassAzimuth: 191.8,
  hasHardwareCompass: false,
  onlineCache: {}
};

function saveSettings() {
  localStorage.setItem('mosul_district_id', state.districtId);
  localStorage.setItem('mosul_method_id', String(state.methodId));
  localStorage.setItem('mosul_asr_id', String(state.asrId));
  localStorage.setItem('mosul_dark_theme', String(state.darkTheme));
  localStorage.setItem('mosul_offsets', JSON.stringify(state.offsets));
  localStorage.setItem('mosul_notifs', JSON.stringify(state.notifs));
  localStorage.setItem('mosul_total_tasbih', String(state.totalTasbih));
}

function toArabicDigits(input) {
  const map = ['٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩'];
  return String(input).replace(/[0-9]/g, (d) => map[+d]);
}

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

function getMosulNowParts() {
  // Always compute current time in Asia/Baghdad (UTC+3)
  const now = new Date();
  const utcMs = now.getTime() + now.getTimezoneOffset() * 60000;
  const baghdadDate = new Date(utcMs + 3 * 3600000);
  return baghdadDate;
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
  const dayOfWeek = ARABIC_DAYS[targetDate.getDay()];

  const cacheKey = `${district.id}_${method.id}_${asrMethod.id}_${year}_${month}_${day}`;
  const rawMinutes = state.onlineCache[cacheKey] || computeAstronomicalMinutes(year, month, day, district, method, asrMethod);

  const nowSeconds = (mosulNow.getHours() * 60 + mosulNow.getMinutes()) * 60 + mosulNow.getSeconds();

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
            <div class="prayer-icon-circle">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 3a9 9 0 1 0 9 9c0-.46-.04-.92-.1-1.36a5.389 5.389 0 0 1-4.4 2.26 5.403 5.403 0 0 1-3.14-9.8c-.44-.06-.9-.1-1.36-.1z"/>
              </svg>
            </div>
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
            <button class="notif-btn ${e.notifEnabled ? '' : 'off'}" onclick="event.stopPropagation(); togglePrayerNotif('${e.key}')" title="تنبيه الأذان">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 22c1.1 0 2-.9 2-2h-4c0 1.1.89 2 2 2zm6-6v-5c0-3.07-1.64-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.63 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z"/>
              </svg>
            </button>
          </div>
        </div>
        <div class="prayer-detail-box">
          <div class="sunnah-row">
            <span>السنة القبلية: ${e.sunnahBefore}</span>
            <span>السنة البعدية: ${e.sunnahAfter}</span>
          </div>
          <p style="color:var(--text-secondary)">${e.heritageNote}</p>
        </div>
      </div>
    `;
  }).join('');

  document.getElementById('footer-method-title').textContent = sched.method.titleArabic;
  document.getElementById('footer-method-sub').textContent = `${sched.asrMethod.titleArabic} • الارتفاع ${toArabicDigits(sched.district.elevationMeters)}م فوق سطح البحر`;
}

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
    statusText.textContent = 'أنت تتجه الآن نحو الكعبة المشرفة مباشرة';
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

  const categories = ['أذكار بعد الصلاة', 'أذكار الصباح', 'أذكار المساء', 'أدعية الموصل'];
  document.getElementById('athkar-cat-chips').innerHTML = categories.map((cat) => `
    <button class="chip-btn ${cat === state.athkarCategory ? 'active' : ''}" onclick="selectAthkarCat('${cat}')">${cat}</button>
  `).join('');

  const filtered = ATHKAR_LIST.filter((a) => a.category === state.athkarCategory);
  document.getElementById('athkar-cards-list').innerHTML = filtered.map((item) => {
    const count = state.athkarCounts[item.id] || 0;
    const done = count >= item.targetCount;
    const pct = Math.min(100, Math.round((count / item.targetCount) * 100));
    return `
      <div class="athkar-card ${done ? 'completed' : ''}" onclick="incrementAthkar('${item.id}', ${item.targetCount})">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px">
          <strong style="color:var(--primary-color)">${item.title}</strong>
          <span class="side-badge">${toArabicDigits(count)} / ${toArabicDigits(item.targetCount)}</span>
        </div>
        <p class="amiri-font" style="font-size:1.32rem;line-height:2;margin-bottom:8px">${item.arabicText}</p>
        <div style="font-size:0.8rem;color:var(--text-secondary)">${item.virtue}</div>
        <div class="progress-bar-track">
          <div class="progress-bar-fill" style="width:${pct}%"></div>
        </div>
      </div>
    `;
  }).join('');
}

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
          <span style="font-weight:700">${p.arabicName}</span>
          <div style="display:flex;align-items:center;gap:10px">
            <button class="icon-btn" style="width:34px;height:34px" onclick="adjustOffset('${p.key}', -1)">-</button>
            <span style="min-width:56px;text-align:center;font-weight:700">${sign}${toArabicDigits(off)} د</span>
            <button class="icon-btn" style="width:34px;height:34px" onclick="adjustOffset('${p.key}', 1)">+</button>
          </div>
        </div>
      `;
    }).join('');
  } else {
    // Render Full Monthly Timetable for Mosul
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

window.togglePrayerNotif = function(key) {
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
  if (banner) banner.classList.add('visible');
});

window.installPwaApp = async function() {
  if (deferredPrompt) {
    deferredPrompt.prompt();
    await deferredPrompt.userChoice;
    deferredPrompt = null;
    document.getElementById('pwa-install-banner').classList.remove('visible');
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
