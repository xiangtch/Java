/*     */ package sun.util.locale;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class LocaleEquivalentMaps
/*     */ {
/*  41 */   static final Map<String, String> singleEquivMap = new HashMap();
/*  42 */   static final Map<String, String[]> multiEquivsMap = new HashMap();
/*  43 */   static final Map<String, String> regionVariantEquivMap = new HashMap();
/*     */   
/*     */   static
/*     */   {
/*  47 */     singleEquivMap.put("acn", "xia");
/*  48 */     singleEquivMap.put("adx", "pcr");
/*  49 */     singleEquivMap.put("ami", "i-ami");
/*  50 */     singleEquivMap.put("art-lojban", "jbo");
/*  51 */     singleEquivMap.put("ase", "sgn-us");
/*  52 */     singleEquivMap.put("ayx", "nun");
/*  53 */     singleEquivMap.put("bfi", "sgn-gb");
/*  54 */     singleEquivMap.put("bjd", "drl");
/*  55 */     singleEquivMap.put("bnn", "i-bnn");
/*  56 */     singleEquivMap.put("bzs", "sgn-br");
/*  57 */     singleEquivMap.put("cir", "meg");
/*  58 */     singleEquivMap.put("cjr", "mom");
/*  59 */     singleEquivMap.put("cka", "cmr");
/*  60 */     singleEquivMap.put("cmk", "xch");
/*  61 */     singleEquivMap.put("cmn-hans", "zh-cmn-hans");
/*  62 */     singleEquivMap.put("cmn-hant", "zh-cmn-hant");
/*  63 */     singleEquivMap.put("cmr", "cka");
/*  64 */     singleEquivMap.put("csn", "sgn-co");
/*  65 */     singleEquivMap.put("dev", "gav");
/*  66 */     singleEquivMap.put("drh", "khk");
/*  67 */     singleEquivMap.put("drl", "bjd");
/*  68 */     singleEquivMap.put("dse", "sgn-nl");
/*  69 */     singleEquivMap.put("dsl", "sgn-dk");
/*  70 */     singleEquivMap.put("fsl", "sgn-fr");
/*  71 */     singleEquivMap.put("gal", "ilw");
/*  72 */     singleEquivMap.put("gan", "zh-gan");
/*  73 */     singleEquivMap.put("gav", "dev");
/*  74 */     singleEquivMap.put("gsg", "sgn-de");
/*  75 */     singleEquivMap.put("gss", "sgn-gr");
/*  76 */     singleEquivMap.put("he", "iw");
/*  77 */     singleEquivMap.put("hle", "sca");
/*  78 */     singleEquivMap.put("hrr", "jal");
/*  79 */     singleEquivMap.put("hsn", "zh-xiang");
/*  80 */     singleEquivMap.put("i-ami", "ami");
/*  81 */     singleEquivMap.put("i-bnn", "bnn");
/*  82 */     singleEquivMap.put("i-klingon", "tlh");
/*  83 */     singleEquivMap.put("i-lux", "lb");
/*  84 */     singleEquivMap.put("i-navajo", "nv");
/*  85 */     singleEquivMap.put("i-pwn", "pwn");
/*  86 */     singleEquivMap.put("i-tao", "tao");
/*  87 */     singleEquivMap.put("i-tay", "tay");
/*  88 */     singleEquivMap.put("i-tsu", "tsu");
/*  89 */     singleEquivMap.put("ibi", "opa");
/*  90 */     singleEquivMap.put("id", "in");
/*  91 */     singleEquivMap.put("ilw", "gal");
/*  92 */     singleEquivMap.put("in", "id");
/*  93 */     singleEquivMap.put("ise", "sgn-it");
/*  94 */     singleEquivMap.put("isg", "sgn-ie");
/*  95 */     singleEquivMap.put("iw", "he");
/*  96 */     singleEquivMap.put("jal", "hrr");
/*  97 */     singleEquivMap.put("jbo", "art-lojban");
/*  98 */     singleEquivMap.put("ji", "yi");
/*  99 */     singleEquivMap.put("jsl", "sgn-jp");
/* 100 */     singleEquivMap.put("jv", "jw");
/* 101 */     singleEquivMap.put("jw", "jv");
/* 102 */     singleEquivMap.put("kgh", "kml");
/* 103 */     singleEquivMap.put("khk", "drh");
/* 104 */     singleEquivMap.put("kml", "kgh");
/* 105 */     singleEquivMap.put("lb", "i-lux");
/* 106 */     singleEquivMap.put("lcq", "ppr");
/* 107 */     singleEquivMap.put("lrr", "yma");
/* 108 */     singleEquivMap.put("meg", "cir");
/* 109 */     singleEquivMap.put("mfs", "sgn-mx");
/* 110 */     singleEquivMap.put("mo", "ro");
/* 111 */     singleEquivMap.put("mom", "cjr");
/* 112 */     singleEquivMap.put("nan", "zh-min-nan");
/* 113 */     singleEquivMap.put("nb", "no-bok");
/* 114 */     singleEquivMap.put("ncs", "sgn-ni");
/* 115 */     singleEquivMap.put("nn", "no-nyn");
/* 116 */     singleEquivMap.put("no-bok", "nb");
/* 117 */     singleEquivMap.put("no-nyn", "nn");
/* 118 */     singleEquivMap.put("nsl", "sgn-no");
/* 119 */     singleEquivMap.put("nun", "ayx");
/* 120 */     singleEquivMap.put("nv", "i-navajo");
/* 121 */     singleEquivMap.put("opa", "ibi");
/* 122 */     singleEquivMap.put("pcr", "adx");
/* 123 */     singleEquivMap.put("ppr", "lcq");
/* 124 */     singleEquivMap.put("psr", "sgn-pt");
/* 125 */     singleEquivMap.put("pwn", "i-pwn");
/* 126 */     singleEquivMap.put("ras", "tie");
/* 127 */     singleEquivMap.put("ro", "mo");
/* 128 */     singleEquivMap.put("sca", "hle");
/* 129 */     singleEquivMap.put("sfb", "sgn-be-fr");
/* 130 */     singleEquivMap.put("sfs", "sgn-za");
/* 131 */     singleEquivMap.put("sgg", "sgn-ch-de");
/* 132 */     singleEquivMap.put("sgn-be-fr", "sfb");
/* 133 */     singleEquivMap.put("sgn-be-nl", "vgt");
/* 134 */     singleEquivMap.put("sgn-br", "bzs");
/* 135 */     singleEquivMap.put("sgn-ch-de", "sgg");
/* 136 */     singleEquivMap.put("sgn-co", "csn");
/* 137 */     singleEquivMap.put("sgn-de", "gsg");
/* 138 */     singleEquivMap.put("sgn-dk", "dsl");
/* 139 */     singleEquivMap.put("sgn-es", "ssp");
/* 140 */     singleEquivMap.put("sgn-fr", "fsl");
/* 141 */     singleEquivMap.put("sgn-gb", "bfi");
/* 142 */     singleEquivMap.put("sgn-gr", "gss");
/* 143 */     singleEquivMap.put("sgn-ie", "isg");
/* 144 */     singleEquivMap.put("sgn-it", "ise");
/* 145 */     singleEquivMap.put("sgn-jp", "jsl");
/* 146 */     singleEquivMap.put("sgn-mx", "mfs");
/* 147 */     singleEquivMap.put("sgn-ni", "ncs");
/* 148 */     singleEquivMap.put("sgn-nl", "dse");
/* 149 */     singleEquivMap.put("sgn-no", "nsl");
/* 150 */     singleEquivMap.put("sgn-pt", "psr");
/* 151 */     singleEquivMap.put("sgn-se", "swl");
/* 152 */     singleEquivMap.put("sgn-us", "ase");
/* 153 */     singleEquivMap.put("sgn-za", "sfs");
/* 154 */     singleEquivMap.put("ssp", "sgn-es");
/* 155 */     singleEquivMap.put("swl", "sgn-se");
/* 156 */     singleEquivMap.put("tao", "i-tao");
/* 157 */     singleEquivMap.put("tay", "i-tay");
/* 158 */     singleEquivMap.put("tie", "ras");
/* 159 */     singleEquivMap.put("tkk", "twm");
/* 160 */     singleEquivMap.put("tlh", "i-klingon");
/* 161 */     singleEquivMap.put("tlw", "weo");
/* 162 */     singleEquivMap.put("tsu", "i-tsu");
/* 163 */     singleEquivMap.put("twm", "tkk");
/* 164 */     singleEquivMap.put("vgt", "sgn-be-nl");
/* 165 */     singleEquivMap.put("weo", "tlw");
/* 166 */     singleEquivMap.put("wuu", "zh-wuu");
/* 167 */     singleEquivMap.put("xch", "cmk");
/* 168 */     singleEquivMap.put("xia", "acn");
/* 169 */     singleEquivMap.put("yi", "ji");
/* 170 */     singleEquivMap.put("yma", "lrr");
/* 171 */     singleEquivMap.put("yos", "zom");
/* 172 */     singleEquivMap.put("yue", "zh-yue");
/* 173 */     singleEquivMap.put("zh-cmn-hans", "cmn-hans");
/* 174 */     singleEquivMap.put("zh-cmn-hant", "cmn-hant");
/* 175 */     singleEquivMap.put("zh-gan", "gan");
/* 176 */     singleEquivMap.put("zh-min-nan", "nan");
/* 177 */     singleEquivMap.put("zh-wuu", "wuu");
/* 178 */     singleEquivMap.put("zh-xiang", "hsn");
/* 179 */     singleEquivMap.put("zh-yue", "yue");
/* 180 */     singleEquivMap.put("zom", "yos");
/*     */     
/* 182 */     multiEquivsMap.put("ccq", new String[] { "rki", "ybd" });
/* 183 */     multiEquivsMap.put("cmn", new String[] { "zh-guoyu", "zh-cmn" });
/* 184 */     multiEquivsMap.put("drw", new String[] { "prs", "tnf" });
/* 185 */     multiEquivsMap.put("hak", new String[] { "i-hak", "zh-hakka" });
/* 186 */     multiEquivsMap.put("i-hak", new String[] { "hak", "zh-hakka" });
/* 187 */     multiEquivsMap.put("mry", new String[] { "mst", "myt" });
/* 188 */     multiEquivsMap.put("mst", new String[] { "mry", "myt" });
/* 189 */     multiEquivsMap.put("myt", new String[] { "mry", "mst" });
/* 190 */     multiEquivsMap.put("prs", new String[] { "drw", "tnf" });
/* 191 */     multiEquivsMap.put("rki", new String[] { "ccq", "ybd" });
/* 192 */     multiEquivsMap.put("tnf", new String[] { "prs", "drw" });
/* 193 */     multiEquivsMap.put("ybd", new String[] { "rki", "ccq" });
/* 194 */     multiEquivsMap.put("zh-cmn", new String[] { "cmn", "zh-guoyu" });
/* 195 */     multiEquivsMap.put("zh-guoyu", new String[] { "cmn", "zh-cmn" });
/* 196 */     multiEquivsMap.put("zh-hakka", new String[] { "hak", "i-hak" });
/*     */     
/* 198 */     regionVariantEquivMap.put("-alalc97", "-heploc");
/* 199 */     regionVariantEquivMap.put("-bu", "-mm");
/* 200 */     regionVariantEquivMap.put("-cd", "-zr");
/* 201 */     regionVariantEquivMap.put("-dd", "-de");
/* 202 */     regionVariantEquivMap.put("-de", "-dd");
/* 203 */     regionVariantEquivMap.put("-fr", "-fx");
/* 204 */     regionVariantEquivMap.put("-fx", "-fr");
/* 205 */     regionVariantEquivMap.put("-heploc", "-alalc97");
/* 206 */     regionVariantEquivMap.put("-mm", "-bu");
/* 207 */     regionVariantEquivMap.put("-tl", "-tp");
/* 208 */     regionVariantEquivMap.put("-tp", "-tl");
/* 209 */     regionVariantEquivMap.put("-yd", "-ye");
/* 210 */     regionVariantEquivMap.put("-ye", "-yd");
/* 211 */     regionVariantEquivMap.put("-zr", "-cd");
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\util\locale\LocaleEquivalentMaps.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */