/*     */ package sun.nio.cs;
/*     */ 
/*     */ import java.nio.charset.Charset;
/*     */ import sun.util.PreHashedMap;
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
/*     */ public class StandardCharsets
/*     */   extends FastCharsetProvider
/*     */ {
/*  39 */   static final String[] aliases_US_ASCII = { "iso-ir-6", "ANSI_X3.4-1986", "ISO_646.irv:1991", "ASCII", "ISO646-US", "us", "IBM367", "cp367", "csASCII", "default", "646", "iso_646.irv:1983", "ANSI_X3.4-1968", "ascii7" };
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
/*  56 */   static final String[] aliases_UTF_8 = { "UTF8", "unicode-1-1-utf-8" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  61 */   static final String[] aliases_CESU_8 = { "CESU8", "csCESU-8" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  66 */   static final String[] aliases_UTF_16 = { "UTF_16", "utf16", "unicode", "UnicodeBig" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  73 */   static final String[] aliases_UTF_16BE = { "UTF_16BE", "ISO-10646-UCS-2", "X-UTF-16BE", "UnicodeBigUnmarked" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  80 */   static final String[] aliases_UTF_16LE = { "UTF_16LE", "X-UTF-16LE", "UnicodeLittleUnmarked" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  86 */   static final String[] aliases_UTF_16LE_BOM = { "UnicodeLittle" };
/*     */   
/*     */ 
/*     */ 
/*  90 */   static final String[] aliases_UTF_32 = { "UTF_32", "UTF32" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  95 */   static final String[] aliases_UTF_32LE = { "UTF_32LE", "X-UTF-32LE" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 100 */   static final String[] aliases_UTF_32BE = { "UTF_32BE", "X-UTF-32BE" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 105 */   static final String[] aliases_UTF_32LE_BOM = { "UTF_32LE_BOM", "UTF-32LE-BOM" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 110 */   static final String[] aliases_UTF_32BE_BOM = { "UTF_32BE_BOM", "UTF-32BE-BOM" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 115 */   static final String[] aliases_ISO_8859_1 = { "iso-ir-100", "ISO_8859-1", "latin1", "l1", "IBM819", "cp819", "csISOLatin1", "819", "IBM-819", "ISO8859_1", "ISO_8859-1:1987", "ISO_8859_1", "8859_1", "ISO8859-1" };
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
/* 132 */   static final String[] aliases_ISO_8859_2 = { "iso8859_2", "8859_2", "iso-ir-101", "ISO_8859-2", "ISO_8859-2:1987", "ISO8859-2", "latin2", "l2", "ibm912", "ibm-912", "cp912", "912", "csISOLatin2" };
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
/* 148 */   static final String[] aliases_ISO_8859_4 = { "iso8859_4", "iso8859-4", "8859_4", "iso-ir-110", "ISO_8859-4", "ISO_8859-4:1988", "latin4", "l4", "ibm914", "ibm-914", "cp914", "914", "csISOLatin4" };
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
/* 164 */   static final String[] aliases_ISO_8859_5 = { "iso8859_5", "8859_5", "iso-ir-144", "ISO_8859-5", "ISO_8859-5:1988", "ISO8859-5", "cyrillic", "ibm915", "ibm-915", "cp915", "915", "csISOLatinCyrillic" };
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
/* 179 */   static final String[] aliases_ISO_8859_7 = { "iso8859_7", "8859_7", "iso-ir-126", "ISO_8859-7", "ISO_8859-7:1987", "ELOT_928", "ECMA-118", "greek", "greek8", "csISOLatinGreek", "sun_eu_greek", "ibm813", "ibm-813", "813", "cp813", "iso8859-7" };
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
/* 198 */   static final String[] aliases_ISO_8859_9 = { "iso8859_9", "8859_9", "iso-ir-148", "ISO_8859-9", "ISO_8859-9:1989", "ISO8859-9", "latin5", "l5", "ibm920", "ibm-920", "920", "cp920", "csISOLatin5" };
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
/* 214 */   static final String[] aliases_ISO_8859_13 = { "iso8859_13", "8859_13", "iso_8859-13", "ISO8859-13" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 221 */   static final String[] aliases_ISO_8859_15 = { "ISO_8859-15", "8859_15", "ISO-8859-15", "ISO8859_15", "ISO8859-15", "IBM923", "IBM-923", "cp923", "923", "LATIN0", "LATIN9", "L9", "csISOlatin0", "csISOlatin9", "ISO8859_15_FDIS" };
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
/* 239 */   static final String[] aliases_KOI8_R = { "koi8_r", "koi8", "cskoi8r" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 245 */   static final String[] aliases_KOI8_U = { "koi8_u" };
/*     */   
/*     */ 
/*     */ 
/* 249 */   static final String[] aliases_MS1250 = { "cp1250", "cp5346" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 254 */   static final String[] aliases_MS1251 = { "cp1251", "cp5347", "ansi-1251" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 260 */   static final String[] aliases_MS1252 = { "cp1252", "cp5348" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 265 */   static final String[] aliases_MS1253 = { "cp1253", "cp5349" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 270 */   static final String[] aliases_MS1254 = { "cp1254", "cp5350" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 275 */   static final String[] aliases_MS1257 = { "cp1257", "cp5353" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 280 */   static final String[] aliases_IBM437 = { "cp437", "ibm437", "ibm-437", "437", "cspc8codepage437", "windows-437" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 289 */   static final String[] aliases_IBM737 = { "cp737", "ibm737", "ibm-737", "737" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 296 */   static final String[] aliases_IBM775 = { "cp775", "ibm775", "ibm-775", "775" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 303 */   static final String[] aliases_IBM850 = { "cp850", "ibm-850", "ibm850", "850", "cspc850multilingual" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 311 */   static final String[] aliases_IBM852 = { "cp852", "ibm852", "ibm-852", "852", "csPCp852" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 319 */   static final String[] aliases_IBM855 = { "cp855", "ibm-855", "ibm855", "855", "cspcp855" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 327 */   static final String[] aliases_IBM857 = { "cp857", "ibm857", "ibm-857", "857", "csIBM857" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 335 */   static final String[] aliases_IBM858 = { "cp858", "ccsid00858", "cp00858", "858", "PC-Multilingual-850+euro" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 343 */   static final String[] aliases_IBM862 = { "cp862", "ibm862", "ibm-862", "862", "csIBM862", "cspc862latinhebrew" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 352 */   static final String[] aliases_IBM866 = { "cp866", "ibm866", "ibm-866", "866", "csIBM866" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 360 */   static final String[] aliases_IBM874 = { "cp874", "ibm874", "ibm-874", "874" };
/*     */   
/*     */ 
/*     */   private static final class Aliases
/*     */     extends PreHashedMap<String>
/*     */   {
/*     */     private static final int ROWS = 1024;
/*     */     
/*     */     private static final int SIZE = 211;
/*     */     
/*     */     private static final int SHIFT = 0;
/*     */     
/*     */     private static final int MASK = 1023;
/*     */     
/*     */ 
/*     */     private Aliases()
/*     */     {
/* 377 */       super(211, 0, 1023);
/*     */     }
/*     */     
/*     */     protected void init(Object[] paramArrayOfObject) {
/* 381 */       paramArrayOfObject[1] = { "csisolatin0", "iso-8859-15" };
/* 382 */       paramArrayOfObject[2] = { "csisolatin1", "iso-8859-1" };
/* 383 */       paramArrayOfObject[3] = { "csisolatin2", "iso-8859-2" };
/* 384 */       paramArrayOfObject[5] = { "csisolatin4", "iso-8859-4" };
/* 385 */       paramArrayOfObject[6] = { "csisolatin5", "iso-8859-9" };
/* 386 */       paramArrayOfObject[10] = { "csisolatin9", "iso-8859-15" };
/* 387 */       paramArrayOfObject[19] = { "unicodelittle", "x-utf-16le-bom" };
/* 388 */       paramArrayOfObject[24] = { "iso646-us", "us-ascii" };
/* 389 */       paramArrayOfObject[25] = { "iso_8859-7:1987", "iso-8859-7" };
/* 390 */       paramArrayOfObject[26] = { "912", "iso-8859-2" };
/* 391 */       paramArrayOfObject[28] = { "914", "iso-8859-4" };
/* 392 */       paramArrayOfObject[29] = { "915", "iso-8859-5" };
/* 393 */       paramArrayOfObject[55] = { "920", "iso-8859-9" };
/* 394 */       paramArrayOfObject[58] = { "923", "iso-8859-15" };
/* 395 */       paramArrayOfObject[86] = { "csisolatincyrillic", "iso-8859-5", { "8859_1", "iso-8859-1" } };
/*     */       
/* 397 */       paramArrayOfObject[87] = { "8859_2", "iso-8859-2" };
/* 398 */       paramArrayOfObject[89] = { "8859_4", "iso-8859-4" };
/* 399 */       paramArrayOfObject[90] = { "813", "iso-8859-7", { "8859_5", "iso-8859-5" } };
/*     */       
/* 401 */       paramArrayOfObject[92] = { "8859_7", "iso-8859-7" };
/* 402 */       paramArrayOfObject[94] = { "8859_9", "iso-8859-9" };
/* 403 */       paramArrayOfObject[95] = { "iso_8859-1:1987", "iso-8859-1" };
/* 404 */       paramArrayOfObject[96] = { "819", "iso-8859-1" };
/* 405 */       paramArrayOfObject[106] = { "unicode-1-1-utf-8", "utf-8" };
/* 406 */       paramArrayOfObject[121] = { "x-utf-16le", "utf-16le" };
/* 407 */       paramArrayOfObject[125] = { "ecma-118", "iso-8859-7" };
/* 408 */       paramArrayOfObject[''] = { "koi8_r", "koi8-r" };
/* 409 */       paramArrayOfObject[''] = { "koi8_u", "koi8-u" };
/* 410 */       paramArrayOfObject[''] = { "cp912", "iso-8859-2" };
/* 411 */       paramArrayOfObject[''] = { "cp914", "iso-8859-4" };
/* 412 */       paramArrayOfObject[''] = { "cp915", "iso-8859-5" };
/* 413 */       paramArrayOfObject['ª'] = { "cp920", "iso-8859-9" };
/* 414 */       paramArrayOfObject['­'] = { "cp923", "iso-8859-15" };
/* 415 */       paramArrayOfObject['±'] = { "utf_32le_bom", "x-utf-32le-bom" };
/* 416 */       paramArrayOfObject['À'] = { "utf_16be", "utf-16be" };
/* 417 */       paramArrayOfObject['Ç'] = { "cspc8codepage437", "ibm437", { "ansi-1251", "windows-1251" } };
/*     */       
/* 419 */       paramArrayOfObject['Í'] = { "cp813", "iso-8859-7" };
/* 420 */       paramArrayOfObject['Ó'] = { "850", "ibm850", { "cp819", "iso-8859-1" } };
/*     */       
/* 422 */       paramArrayOfObject['Õ'] = { "852", "ibm852" };
/* 423 */       paramArrayOfObject['Ø'] = { "855", "ibm855" };
/* 424 */       paramArrayOfObject['Ú'] = { "857", "ibm857", { "iso-ir-6", "us-ascii" } };
/*     */       
/* 426 */       paramArrayOfObject['Û'] = { "858", "ibm00858", { "737", "x-ibm737" } };
/*     */       
/* 428 */       paramArrayOfObject['á'] = { "csascii", "us-ascii" };
/* 429 */       paramArrayOfObject['ô'] = { "862", "ibm862" };
/* 430 */       paramArrayOfObject['ø'] = { "866", "ibm866" };
/* 431 */       paramArrayOfObject['ý'] = { "x-utf-32be", "utf-32be" };
/* 432 */       paramArrayOfObject['þ'] = { "iso_8859-2:1987", "iso-8859-2" };
/* 433 */       paramArrayOfObject['ă'] = { "unicodebig", "utf-16" };
/* 434 */       paramArrayOfObject['č'] = { "iso8859_15_fdis", "iso-8859-15" };
/* 435 */       paramArrayOfObject['ĕ'] = { "874", "x-ibm874" };
/* 436 */       paramArrayOfObject['Ę'] = { "unicodelittleunmarked", "utf-16le" };
/* 437 */       paramArrayOfObject['ě'] = { "iso8859_1", "iso-8859-1" };
/* 438 */       paramArrayOfObject['Ĝ'] = { "iso8859_2", "iso-8859-2" };
/* 439 */       paramArrayOfObject['Ğ'] = { "iso8859_4", "iso-8859-4" };
/* 440 */       paramArrayOfObject['ğ'] = { "iso8859_5", "iso-8859-5" };
/* 441 */       paramArrayOfObject['ġ'] = { "iso8859_7", "iso-8859-7" };
/* 442 */       paramArrayOfObject['ģ'] = { "iso8859_9", "iso-8859-9" };
/* 443 */       paramArrayOfObject['Ħ'] = { "ibm912", "iso-8859-2" };
/* 444 */       paramArrayOfObject['Ĩ'] = { "ibm914", "iso-8859-4" };
/* 445 */       paramArrayOfObject['ĩ'] = { "ibm915", "iso-8859-5" };
/* 446 */       paramArrayOfObject['ı'] = { "iso_8859-13", "iso-8859-13" };
/* 447 */       paramArrayOfObject['ĳ'] = { "iso_8859-15", "iso-8859-15" };
/* 448 */       paramArrayOfObject['ĸ'] = { "greek8", "iso-8859-7", { "646", "us-ascii" } };
/*     */       
/* 450 */       paramArrayOfObject['Ł'] = { "ibm-912", "iso-8859-2" };
/* 451 */       paramArrayOfObject['Ń'] = { "ibm920", "iso-8859-9", { "ibm-914", "iso-8859-4" } };
/*     */       
/* 453 */       paramArrayOfObject['ń'] = { "ibm-915", "iso-8859-5" };
/* 454 */       paramArrayOfObject['Ņ'] = { "l1", "iso-8859-1" };
/* 455 */       paramArrayOfObject['ņ'] = { "cp850", "ibm850", { "ibm923", "iso-8859-15", { "l2", "iso-8859-2" } } };
/*     */       
/*     */ 
/* 458 */       paramArrayOfObject['Ň'] = { "cyrillic", "iso-8859-5" };
/* 459 */       paramArrayOfObject['ň'] = { "cp852", "ibm852", { "l4", "iso-8859-4" } };
/*     */       
/* 461 */       paramArrayOfObject['ŉ'] = { "l5", "iso-8859-9" };
/* 462 */       paramArrayOfObject['ŋ'] = { "cp855", "ibm855" };
/* 463 */       paramArrayOfObject['ō'] = { "cp857", "ibm857", { "l9", "iso-8859-15" } };
/*     */       
/* 465 */       paramArrayOfObject['Ŏ'] = { "cp858", "ibm00858", { "cp737", "x-ibm737" } };
/*     */       
/* 467 */       paramArrayOfObject['Ő'] = { "iso_8859_1", "iso-8859-1" };
/* 468 */       paramArrayOfObject['œ'] = { "koi8", "koi8-r" };
/* 469 */       paramArrayOfObject['ŕ'] = { "775", "ibm775" };
/* 470 */       paramArrayOfObject['ř'] = { "iso_8859-9:1989", "iso-8859-9" };
/* 471 */       paramArrayOfObject['Ş'] = { "ibm-920", "iso-8859-9" };
/* 472 */       paramArrayOfObject['š'] = { "ibm-923", "iso-8859-15" };
/* 473 */       paramArrayOfObject['Ŧ'] = { "ibm813", "iso-8859-7" };
/* 474 */       paramArrayOfObject['ŧ'] = { "cp862", "ibm862" };
/* 475 */       paramArrayOfObject['ū'] = { "cp866", "ibm866" };
/* 476 */       paramArrayOfObject['Ŭ'] = { "ibm819", "iso-8859-1" };
/* 477 */       paramArrayOfObject['ź'] = { "ansi_x3.4-1968", "us-ascii" };
/* 478 */       paramArrayOfObject['Ɓ'] = { "ibm-813", "iso-8859-7" };
/* 479 */       paramArrayOfObject['Ƈ'] = { "ibm-819", "iso-8859-1" };
/* 480 */       paramArrayOfObject['ƈ'] = { "cp874", "x-ibm874" };
/* 481 */       paramArrayOfObject['ƕ'] = { "iso-ir-100", "iso-8859-1" };
/* 482 */       paramArrayOfObject['Ɩ'] = { "iso-ir-101", "iso-8859-2" };
/* 483 */       paramArrayOfObject['Ƙ'] = { "437", "ibm437" };
/* 484 */       paramArrayOfObject['ƥ'] = { "iso-8859-15", "iso-8859-15" };
/* 485 */       paramArrayOfObject['Ƭ'] = { "latin0", "iso-8859-15" };
/* 486 */       paramArrayOfObject['ƭ'] = { "latin1", "iso-8859-1" };
/* 487 */       paramArrayOfObject['Ʈ'] = { "latin2", "iso-8859-2" };
/* 488 */       paramArrayOfObject['ư'] = { "latin4", "iso-8859-4" };
/* 489 */       paramArrayOfObject['Ʊ'] = { "latin5", "iso-8859-9" };
/* 490 */       paramArrayOfObject['ƴ'] = { "iso-ir-110", "iso-8859-4" };
/* 491 */       paramArrayOfObject['Ƶ'] = { "latin9", "iso-8859-15" };
/* 492 */       paramArrayOfObject['ƶ'] = { "ansi_x3.4-1986", "us-ascii" };
/* 493 */       paramArrayOfObject['ƻ'] = { "utf-32be-bom", "x-utf-32be-bom" };
/* 494 */       paramArrayOfObject['ǈ'] = { "cp775", "ibm775" };
/* 495 */       paramArrayOfObject['Ǚ'] = { "iso-ir-126", "iso-8859-7" };
/* 496 */       paramArrayOfObject['ǟ'] = { "ibm850", "ibm850" };
/* 497 */       paramArrayOfObject['ǡ'] = { "ibm852", "ibm852" };
/* 498 */       paramArrayOfObject['Ǥ'] = { "ibm855", "ibm855" };
/* 499 */       paramArrayOfObject['Ǧ'] = { "ibm857", "ibm857" };
/* 500 */       paramArrayOfObject['ǧ'] = { "ibm737", "x-ibm737" };
/* 501 */       paramArrayOfObject['Ƕ'] = { "utf_16le", "utf-16le" };
/* 502 */       paramArrayOfObject['Ǻ'] = { "ibm-850", "ibm850" };
/* 503 */       paramArrayOfObject['Ǽ'] = { "ibm-852", "ibm852" };
/* 504 */       paramArrayOfObject['ǿ'] = { "ibm-855", "ibm855" };
/* 505 */       paramArrayOfObject['Ȁ'] = { "ibm862", "ibm862" };
/* 506 */       paramArrayOfObject['ȁ'] = { "ibm-857", "ibm857" };
/* 507 */       paramArrayOfObject['Ȃ'] = { "ibm-737", "x-ibm737" };
/* 508 */       paramArrayOfObject['Ȅ'] = { "ibm866", "ibm866" };
/* 509 */       paramArrayOfObject['Ȉ'] = { "unicodebigunmarked", "utf-16be" };
/* 510 */       paramArrayOfObject['ȋ'] = { "cp437", "ibm437" };
/* 511 */       paramArrayOfObject['Ȍ'] = { "utf16", "utf-16" };
/* 512 */       paramArrayOfObject['ȕ'] = { "iso-ir-144", "iso-8859-5" };
/* 513 */       paramArrayOfObject['ș'] = { "iso-ir-148", "iso-8859-9" };
/* 514 */       paramArrayOfObject['ț'] = { "ibm-862", "ibm862" };
/* 515 */       paramArrayOfObject['ȟ'] = { "ibm-866", "ibm866" };
/* 516 */       paramArrayOfObject['ȡ'] = { "ibm874", "x-ibm874" };
/* 517 */       paramArrayOfObject['ȳ'] = { "x-utf-32le", "utf-32le" };
/* 518 */       paramArrayOfObject['ȼ'] = { "ibm-874", "x-ibm874" };
/* 519 */       paramArrayOfObject['Ƚ'] = { "iso_8859-4:1988", "iso-8859-4" };
/* 520 */       paramArrayOfObject['Ɂ'] = { "default", "us-ascii" };
/* 521 */       paramArrayOfObject['Ɇ'] = { "utf32", "utf-32" };
/* 522 */       paramArrayOfObject['ɇ'] = { "pc-multilingual-850+euro", "ibm00858" };
/* 523 */       paramArrayOfObject['Ɍ'] = { "elot_928", "iso-8859-7" };
/* 524 */       paramArrayOfObject['ɑ'] = { "csisolatingreek", "iso-8859-7" };
/* 525 */       paramArrayOfObject['ɖ'] = { "csibm857", "ibm857" };
/* 526 */       paramArrayOfObject['ɡ'] = { "ibm775", "ibm775" };
/* 527 */       paramArrayOfObject['ɩ'] = { "cp1250", "windows-1250" };
/* 528 */       paramArrayOfObject['ɪ'] = { "cp1251", "windows-1251" };
/* 529 */       paramArrayOfObject['ɫ'] = { "cp1252", "windows-1252" };
/* 530 */       paramArrayOfObject['ɬ'] = { "cp1253", "windows-1253" };
/* 531 */       paramArrayOfObject['ɭ'] = { "cp1254", "windows-1254" };
/* 532 */       paramArrayOfObject['ɰ'] = { "csibm862", "ibm862", { "cp1257", "windows-1257" } };
/*     */       
/* 534 */       paramArrayOfObject['ɴ'] = { "csibm866", "ibm866", { "cesu8", "cesu-8" } };
/*     */       
/* 536 */       paramArrayOfObject['ɸ'] = { "iso8859_13", "iso-8859-13" };
/* 537 */       paramArrayOfObject['ɺ'] = { "iso8859_15", "iso-8859-15", { "utf_32be", "utf-32be" } };
/*     */       
/* 539 */       paramArrayOfObject['ɻ'] = { "utf_32be_bom", "x-utf-32be-bom" };
/* 540 */       paramArrayOfObject['ɼ'] = { "ibm-775", "ibm775" };
/* 541 */       paramArrayOfObject['ʎ'] = { "cp00858", "ibm00858" };
/* 542 */       paramArrayOfObject['ʝ'] = { "8859_13", "iso-8859-13" };
/* 543 */       paramArrayOfObject['ʞ'] = { "us", "us-ascii" };
/* 544 */       paramArrayOfObject['ʟ'] = { "8859_15", "iso-8859-15" };
/* 545 */       paramArrayOfObject['ʤ'] = { "ibm437", "ibm437" };
/* 546 */       paramArrayOfObject['ʧ'] = { "cp367", "us-ascii" };
/* 547 */       paramArrayOfObject['ʮ'] = { "iso-10646-ucs-2", "utf-16be" };
/* 548 */       paramArrayOfObject['ʿ'] = { "ibm-437", "ibm437" };
/* 549 */       paramArrayOfObject['ˆ'] = { "iso8859-13", "iso-8859-13" };
/* 550 */       paramArrayOfObject['ˈ'] = { "iso8859-15", "iso-8859-15" };
/* 551 */       paramArrayOfObject['˜'] = { "iso_8859-5:1988", "iso-8859-5" };
/* 552 */       paramArrayOfObject['˝'] = { "unicode", "utf-16" };
/* 553 */       paramArrayOfObject['̀'] = { "greek", "iso-8859-7" };
/* 554 */       paramArrayOfObject['̆'] = { "ascii7", "us-ascii" };
/* 555 */       paramArrayOfObject['̍'] = { "iso8859-1", "iso-8859-1" };
/* 556 */       paramArrayOfObject['̎'] = { "iso8859-2", "iso-8859-2" };
/* 557 */       paramArrayOfObject['̏'] = { "cskoi8r", "koi8-r" };
/* 558 */       paramArrayOfObject['̐'] = { "iso8859-4", "iso-8859-4" };
/* 559 */       paramArrayOfObject['̑'] = { "iso8859-5", "iso-8859-5" };
/* 560 */       paramArrayOfObject['̓'] = { "iso8859-7", "iso-8859-7" };
/* 561 */       paramArrayOfObject['̕'] = { "iso8859-9", "iso-8859-9" };
/* 562 */       paramArrayOfObject['̭'] = { "ccsid00858", "ibm00858" };
/* 563 */       paramArrayOfObject['̲'] = { "cspc862latinhebrew", "ibm862" };
/* 564 */       paramArrayOfObject['̀'] = { "ibm367", "us-ascii" };
/* 565 */       paramArrayOfObject['͂'] = { "iso_8859-1", "iso-8859-1" };
/* 566 */       paramArrayOfObject['̓'] = { "iso_8859-2", "iso-8859-2", { "x-utf-16be", "utf-16be" } };
/*     */       
/* 568 */       paramArrayOfObject['̈́'] = { "sun_eu_greek", "iso-8859-7" };
/* 569 */       paramArrayOfObject['ͅ'] = { "iso_8859-4", "iso-8859-4" };
/* 570 */       paramArrayOfObject['͆'] = { "iso_8859-5", "iso-8859-5" };
/* 571 */       paramArrayOfObject['͈'] = { "cspcp852", "ibm852", { "iso_8859-7", "iso-8859-7" } };
/*     */       
/* 573 */       paramArrayOfObject['͊'] = { "iso_8859-9", "iso-8859-9" };
/* 574 */       paramArrayOfObject['͋'] = { "cspcp855", "ibm855" };
/* 575 */       paramArrayOfObject['͎'] = { "windows-437", "ibm437" };
/* 576 */       paramArrayOfObject['͑'] = { "ascii", "us-ascii" };
/* 577 */       paramArrayOfObject['͟'] = { "cscesu-8", "cesu-8" };
/* 578 */       paramArrayOfObject['ͱ'] = { "utf8", "utf-8" };
/* 579 */       paramArrayOfObject['΀'] = { "iso_646.irv:1983", "us-ascii" };
/* 580 */       paramArrayOfObject['΍'] = { "cp5346", "windows-1250" };
/* 581 */       paramArrayOfObject['Ύ'] = { "cp5347", "windows-1251" };
/* 582 */       paramArrayOfObject['Ώ'] = { "cp5348", "windows-1252" };
/* 583 */       paramArrayOfObject['ΐ'] = { "cp5349", "windows-1253" };
/* 584 */       paramArrayOfObject['Ν'] = { "iso_646.irv:1991", "us-ascii" };
/* 585 */       paramArrayOfObject['Φ'] = { "cp5350", "windows-1254" };
/* 586 */       paramArrayOfObject['Ω'] = { "cp5353", "windows-1257" };
/* 587 */       paramArrayOfObject['ΰ'] = { "utf_32le", "utf-32le" };
/* 588 */       paramArrayOfObject['ν'] = { "utf_16", "utf-16" };
/* 589 */       paramArrayOfObject['ϡ'] = { "cspc850multilingual", "ibm850" };
/* 590 */       paramArrayOfObject['ϱ'] = { "utf-32le-bom", "x-utf-32le-bom" };
/* 591 */       paramArrayOfObject['Ϸ'] = { "utf_32", "utf-32" };
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static final class Classes
/*     */     extends PreHashedMap<String>
/*     */   {
/*     */     private static final int ROWS = 32;
/*     */     private static final int SIZE = 39;
/*     */     private static final int SHIFT = 1;
/*     */     private static final int MASK = 31;
/*     */     
/*     */     private Classes()
/*     */     {
/* 606 */       super(39, 1, 31);
/*     */     }
/*     */     
/*     */     protected void init(Object[] paramArrayOfObject) {
/* 610 */       paramArrayOfObject[0] = { "ibm862", "IBM862" };
/* 611 */       paramArrayOfObject[2] = { "ibm866", "IBM866", { "utf-32", "UTF_32", { "utf-16le", "UTF_16LE" } } };
/*     */       
/*     */ 
/* 614 */       paramArrayOfObject[3] = { "windows-1251", "MS1251", { "windows-1250", "MS1250" } };
/*     */       
/* 616 */       paramArrayOfObject[4] = { "windows-1253", "MS1253", { "windows-1252", "MS1252", { "utf-32be", "UTF_32BE" } } };
/*     */       
/*     */ 
/* 619 */       paramArrayOfObject[5] = { "windows-1254", "MS1254", { "utf-16", "UTF_16" } };
/*     */       
/* 621 */       paramArrayOfObject[6] = { "windows-1257", "MS1257" };
/* 622 */       paramArrayOfObject[7] = { "utf-16be", "UTF_16BE" };
/* 623 */       paramArrayOfObject[8] = { "iso-8859-2", "ISO_8859_2", { "iso-8859-1", "ISO_8859_1" } };
/*     */       
/* 625 */       paramArrayOfObject[9] = { "iso-8859-4", "ISO_8859_4", { "utf-8", "UTF_8" } };
/*     */       
/* 627 */       paramArrayOfObject[10] = { "iso-8859-5", "ISO_8859_5" };
/* 628 */       paramArrayOfObject[11] = { "x-ibm874", "IBM874", { "iso-8859-7", "ISO_8859_7" } };
/*     */       
/* 630 */       paramArrayOfObject[12] = { "iso-8859-9", "ISO_8859_9" };
/* 631 */       paramArrayOfObject[14] = { "x-ibm737", "IBM737" };
/* 632 */       paramArrayOfObject[15] = { "ibm850", "IBM850" };
/* 633 */       paramArrayOfObject[16] = { "ibm852", "IBM852", { "ibm775", "IBM775" } };
/*     */       
/* 635 */       paramArrayOfObject[17] = { "iso-8859-13", "ISO_8859_13", { "us-ascii", "US_ASCII" } };
/*     */       
/* 637 */       paramArrayOfObject[18] = { "ibm855", "IBM855", { "ibm437", "IBM437", { "iso-8859-15", "ISO_8859_15" } } };
/*     */       
/*     */ 
/* 640 */       paramArrayOfObject[19] = { "ibm00858", "IBM858", { "ibm857", "IBM857", { "x-utf-32le-bom", "UTF_32LE_BOM" } } };
/*     */       
/*     */ 
/* 643 */       paramArrayOfObject[22] = { "x-utf-16le-bom", "UTF_16LE_BOM" };
/* 644 */       paramArrayOfObject[23] = { "cesu-8", "CESU_8" };
/* 645 */       paramArrayOfObject[24] = { "x-utf-32be-bom", "UTF_32BE_BOM" };
/* 646 */       paramArrayOfObject[28] = { "koi8-r", "KOI8_R" };
/* 647 */       paramArrayOfObject[29] = { "koi8-u", "KOI8_U" };
/* 648 */       paramArrayOfObject[31] = { "utf-32le", "UTF_32LE" };
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static final class Cache
/*     */     extends PreHashedMap<Charset>
/*     */   {
/*     */     private static final int ROWS = 32;
/*     */     private static final int SIZE = 39;
/*     */     private static final int SHIFT = 1;
/*     */     private static final int MASK = 31;
/*     */     
/*     */     private Cache()
/*     */     {
/* 663 */       super(39, 1, 31);
/*     */     }
/*     */     
/*     */     protected void init(Object[] paramArrayOfObject) {
/* 667 */       paramArrayOfObject[0] = { "ibm862", null };
/* 668 */       paramArrayOfObject[2] = { "ibm866", null, { "utf-32", null, { "utf-16le", null } } };
/*     */       
/*     */ 
/* 671 */       paramArrayOfObject[3] = { "windows-1251", null, { "windows-1250", null } };
/*     */       
/* 673 */       paramArrayOfObject[4] = { "windows-1253", null, { "windows-1252", null, { "utf-32be", null } } };
/*     */       
/*     */ 
/* 676 */       paramArrayOfObject[5] = { "windows-1254", null, { "utf-16", null } };
/*     */       
/* 678 */       paramArrayOfObject[6] = { "windows-1257", null };
/* 679 */       paramArrayOfObject[7] = { "utf-16be", null };
/* 680 */       paramArrayOfObject[8] = { "iso-8859-2", null, { "iso-8859-1", null } };
/*     */       
/* 682 */       paramArrayOfObject[9] = { "iso-8859-4", null, { "utf-8", null } };
/*     */       
/* 684 */       paramArrayOfObject[10] = { "iso-8859-5", null };
/* 685 */       paramArrayOfObject[11] = { "x-ibm874", null, { "iso-8859-7", null } };
/*     */       
/* 687 */       paramArrayOfObject[12] = { "iso-8859-9", null };
/* 688 */       paramArrayOfObject[14] = { "x-ibm737", null };
/* 689 */       paramArrayOfObject[15] = { "ibm850", null };
/* 690 */       paramArrayOfObject[16] = { "ibm852", null, { "ibm775", null } };
/*     */       
/* 692 */       paramArrayOfObject[17] = { "iso-8859-13", null, { "us-ascii", null } };
/*     */       
/* 694 */       paramArrayOfObject[18] = { "ibm855", null, { "ibm437", null, { "iso-8859-15", null } } };
/*     */       
/*     */ 
/* 697 */       paramArrayOfObject[19] = { "ibm00858", null, { "ibm857", null, { "x-utf-32le-bom", null } } };
/*     */       
/*     */ 
/* 700 */       paramArrayOfObject[22] = { "x-utf-16le-bom", null };
/* 701 */       paramArrayOfObject[23] = { "cesu-8", null };
/* 702 */       paramArrayOfObject[24] = { "x-utf-32be-bom", null };
/* 703 */       paramArrayOfObject[28] = { "koi8-r", null };
/* 704 */       paramArrayOfObject[29] = { "koi8-u", null };
/* 705 */       paramArrayOfObject[31] = { "utf-32le", null };
/*     */     }
/*     */   }
/*     */   
/*     */   public StandardCharsets()
/*     */   {
/* 711 */     super("sun.nio.cs", new Aliases(null), new Classes(null), new Cache(null));
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\cs\StandardCharsets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */