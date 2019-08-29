/*    */ package sun.nio.cs;
/*    */ 
/*    */ import java.nio.charset.Charset;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ abstract class Unicode
/*    */   extends Charset
/*    */   implements HistoricallyNamedCharset
/*    */ {
/*    */   public Unicode(String paramString, String[] paramArrayOfString)
/*    */   {
/* 34 */     super(paramString, paramArrayOfString);
/*    */   }
/*    */   
/*    */   public boolean contains(Charset paramCharset) {
/* 38 */     return ((paramCharset instanceof US_ASCII)) || ((paramCharset instanceof ISO_8859_1)) || ((paramCharset instanceof ISO_8859_15)) || ((paramCharset instanceof MS1252)) || ((paramCharset instanceof UTF_8)) || ((paramCharset instanceof UTF_16)) || ((paramCharset instanceof UTF_16BE)) || ((paramCharset instanceof UTF_16LE)) || ((paramCharset instanceof UTF_16LE_BOM)) || 
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 47 */       (paramCharset.name().equals("GBK")) || 
/* 48 */       (paramCharset.name().equals("GB18030")) || 
/* 49 */       (paramCharset.name().equals("ISO-8859-2")) || 
/* 50 */       (paramCharset.name().equals("ISO-8859-3")) || 
/* 51 */       (paramCharset.name().equals("ISO-8859-4")) || 
/* 52 */       (paramCharset.name().equals("ISO-8859-5")) || 
/* 53 */       (paramCharset.name().equals("ISO-8859-6")) || 
/* 54 */       (paramCharset.name().equals("ISO-8859-7")) || 
/* 55 */       (paramCharset.name().equals("ISO-8859-8")) || 
/* 56 */       (paramCharset.name().equals("ISO-8859-9")) || 
/* 57 */       (paramCharset.name().equals("ISO-8859-13")) || 
/* 58 */       (paramCharset.name().equals("JIS_X0201")) || 
/* 59 */       (paramCharset.name().equals("x-JIS0208")) || 
/* 60 */       (paramCharset.name().equals("JIS_X0212-1990")) || 
/* 61 */       (paramCharset.name().equals("GB2312")) || 
/* 62 */       (paramCharset.name().equals("EUC-KR")) || 
/* 63 */       (paramCharset.name().equals("x-EUC-TW")) || 
/* 64 */       (paramCharset.name().equals("EUC-JP")) || 
/* 65 */       (paramCharset.name().equals("x-euc-jp-linux")) || 
/* 66 */       (paramCharset.name().equals("KOI8-R")) || 
/* 67 */       (paramCharset.name().equals("TIS-620")) || 
/* 68 */       (paramCharset.name().equals("x-ISCII91")) || 
/* 69 */       (paramCharset.name().equals("windows-1251")) || 
/* 70 */       (paramCharset.name().equals("windows-1253")) || 
/* 71 */       (paramCharset.name().equals("windows-1254")) || 
/* 72 */       (paramCharset.name().equals("windows-1255")) || 
/* 73 */       (paramCharset.name().equals("windows-1256")) || 
/* 74 */       (paramCharset.name().equals("windows-1257")) || 
/* 75 */       (paramCharset.name().equals("windows-1258")) || 
/* 76 */       (paramCharset.name().equals("windows-932")) || 
/* 77 */       (paramCharset.name().equals("x-mswin-936")) || 
/* 78 */       (paramCharset.name().equals("x-windows-949")) || 
/* 79 */       (paramCharset.name().equals("x-windows-950")) || 
/* 80 */       (paramCharset.name().equals("windows-31j")) || 
/* 81 */       (paramCharset.name().equals("Big5")) || 
/* 82 */       (paramCharset.name().equals("Big5-HKSCS")) || 
/* 83 */       (paramCharset.name().equals("x-MS950-HKSCS")) || 
/* 84 */       (paramCharset.name().equals("ISO-2022-JP")) || 
/* 85 */       (paramCharset.name().equals("ISO-2022-KR")) || 
/* 86 */       (paramCharset.name().equals("x-ISO-2022-CN-CNS")) || 
/* 87 */       (paramCharset.name().equals("x-ISO-2022-CN-GB")) || 
/* 88 */       (paramCharset.name().equals("Big5-HKSCS")) || 
/* 89 */       (paramCharset.name().equals("x-Johab")) || 
/* 90 */       (paramCharset.name().equals("Shift_JIS"));
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\cs\Unicode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */