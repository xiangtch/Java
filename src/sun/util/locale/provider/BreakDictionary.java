/*     */ package sun.util.locale.provider;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.IOException;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.MissingResourceException;
/*     */ import sun.text.CompactByteArray;
/*     */ import sun.text.SupplementaryCharacterData;
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
/*     */ class BreakDictionary
/*     */ {
/*  71 */   private static int supportedVersion = 1;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  77 */   private CompactByteArray columnMap = null;
/*  78 */   private SupplementaryCharacterData supplementaryCharColumnMap = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int numCols;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int numColGroups;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 103 */   private short[] table = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 108 */   private short[] rowIndex = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 116 */   private int[] rowIndexFlags = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 126 */   private short[] rowIndexFlagsIndex = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 132 */   private byte[] rowIndexShifts = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   BreakDictionary(String paramString)
/*     */     throws IOException, MissingResourceException
/*     */   {
/* 141 */     readDictionaryFile(paramString);
/*     */   }
/*     */   
/*     */   private void readDictionaryFile(final String paramString) throws IOException, MissingResourceException
/*     */   {
/*     */     BufferedInputStream localBufferedInputStream;
/*     */     try
/*     */     {
/* 149 */       localBufferedInputStream = (BufferedInputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */       {
/*     */         public BufferedInputStream run() throws Exception
/*     */         {
/* 153 */           return new BufferedInputStream(getClass().getResourceAsStream("/sun/text/resources/" + paramString));
/*     */         }
/*     */       });
/*     */     }
/*     */     catch (PrivilegedActionException localPrivilegedActionException)
/*     */     {
/* 159 */       throw new InternalError(localPrivilegedActionException.toString(), localPrivilegedActionException);
/*     */     }
/*     */     
/* 162 */     byte[] arrayOfByte1 = new byte[8];
/* 163 */     if (localBufferedInputStream.read(arrayOfByte1) != 8) {
/* 164 */       throw new MissingResourceException("Wrong data length", paramString, "");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 169 */     int i = RuleBasedBreakIterator.getInt(arrayOfByte1, 0);
/* 170 */     if (i != supportedVersion) {
/* 171 */       throw new MissingResourceException("Dictionary version(" + i + ") is unsupported", paramString, "");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 176 */     int j = RuleBasedBreakIterator.getInt(arrayOfByte1, 4);
/* 177 */     arrayOfByte1 = new byte[j];
/* 178 */     if (localBufferedInputStream.read(arrayOfByte1) != j) {
/* 179 */       throw new MissingResourceException("Wrong data length", paramString, "");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 184 */     localBufferedInputStream.close();
/*     */     
/*     */ 
/* 187 */     int m = 0;
/*     */     
/*     */ 
/*     */ 
/* 191 */     int k = RuleBasedBreakIterator.getInt(arrayOfByte1, m);
/* 192 */     m += 4;
/* 193 */     short[] arrayOfShort = new short[k];
/* 194 */     for (int n = 0; n < k; m += 2) {
/* 195 */       arrayOfShort[n] = RuleBasedBreakIterator.getShort(arrayOfByte1, m);n++;
/*     */     }
/* 197 */     k = RuleBasedBreakIterator.getInt(arrayOfByte1, m);
/* 198 */     m += 4;
/* 199 */     byte[] arrayOfByte2 = new byte[k];
/* 200 */     for (int i1 = 0; i1 < k; m++) {
/* 201 */       arrayOfByte2[i1] = arrayOfByte1[m];i1++;
/*     */     }
/* 203 */     this.columnMap = new CompactByteArray(arrayOfShort, arrayOfByte2);
/*     */     
/*     */ 
/* 206 */     this.numCols = RuleBasedBreakIterator.getInt(arrayOfByte1, m);
/* 207 */     m += 4;
/* 208 */     this.numColGroups = RuleBasedBreakIterator.getInt(arrayOfByte1, m);
/* 209 */     m += 4;
/*     */     
/*     */ 
/* 212 */     k = RuleBasedBreakIterator.getInt(arrayOfByte1, m);
/* 213 */     m += 4;
/* 214 */     this.rowIndex = new short[k];
/* 215 */     for (i1 = 0; i1 < k; m += 2) {
/* 216 */       this.rowIndex[i1] = RuleBasedBreakIterator.getShort(arrayOfByte1, m);i1++;
/*     */     }
/*     */     
/*     */ 
/* 220 */     k = RuleBasedBreakIterator.getInt(arrayOfByte1, m);
/* 221 */     m += 4;
/* 222 */     this.rowIndexFlagsIndex = new short[k];
/* 223 */     for (i1 = 0; i1 < k; m += 2) {
/* 224 */       this.rowIndexFlagsIndex[i1] = RuleBasedBreakIterator.getShort(arrayOfByte1, m);i1++;
/*     */     }
/* 226 */     k = RuleBasedBreakIterator.getInt(arrayOfByte1, m);
/* 227 */     m += 4;
/* 228 */     this.rowIndexFlags = new int[k];
/* 229 */     for (i1 = 0; i1 < k; m += 4) {
/* 230 */       this.rowIndexFlags[i1] = RuleBasedBreakIterator.getInt(arrayOfByte1, m);i1++;
/*     */     }
/*     */     
/*     */ 
/* 234 */     k = RuleBasedBreakIterator.getInt(arrayOfByte1, m);
/* 235 */     m += 4;
/* 236 */     this.rowIndexShifts = new byte[k];
/* 237 */     for (i1 = 0; i1 < k; m++) {
/* 238 */       this.rowIndexShifts[i1] = arrayOfByte1[m];i1++;
/*     */     }
/*     */     
/*     */ 
/* 242 */     k = RuleBasedBreakIterator.getInt(arrayOfByte1, m);
/* 243 */     m += 4;
/* 244 */     this.table = new short[k];
/* 245 */     for (i1 = 0; i1 < k; m += 2) {
/* 246 */       this.table[i1] = RuleBasedBreakIterator.getShort(arrayOfByte1, m);i1++;
/*     */     }
/*     */     
/*     */ 
/* 250 */     k = RuleBasedBreakIterator.getInt(arrayOfByte1, m);
/* 251 */     m += 4;
/* 252 */     int[] arrayOfInt = new int[k];
/* 253 */     for (int i2 = 0; i2 < k; m += 4) {
/* 254 */       arrayOfInt[i2] = RuleBasedBreakIterator.getInt(arrayOfByte1, m);i2++;
/*     */     }
/* 256 */     this.supplementaryCharColumnMap = new SupplementaryCharacterData(arrayOfInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final short getNextStateFromCharacter(int paramInt1, int paramInt2)
/*     */   {
/*     */     int i;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 272 */     if (paramInt2 < 65536) {
/* 273 */       i = this.columnMap.elementAt((char)paramInt2);
/*     */     } else {
/* 275 */       i = this.supplementaryCharColumnMap.getValue(paramInt2);
/*     */     }
/* 277 */     return getNextState(paramInt1, i);
/*     */   }
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
/*     */   public final short getNextState(int paramInt1, int paramInt2)
/*     */   {
/* 292 */     if (cellIsPopulated(paramInt1, paramInt2))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 299 */       return internalAt(this.rowIndex[paramInt1], paramInt2 + this.rowIndexShifts[paramInt1]);
/*     */     }
/*     */     
/* 302 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean cellIsPopulated(int paramInt1, int paramInt2)
/*     */   {
/* 314 */     if (this.rowIndexFlagsIndex[paramInt1] < 0) {
/* 315 */       return paramInt2 == -this.rowIndexFlagsIndex[paramInt1];
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 325 */     int i = this.rowIndexFlags[(this.rowIndexFlagsIndex[paramInt1] + (paramInt2 >> 5))];
/* 326 */     return (i & 1 << (paramInt2 & 0x1F)) != 0;
/*     */   }
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
/*     */   private short internalAt(int paramInt1, int paramInt2)
/*     */   {
/* 341 */     return this.table[(paramInt1 * this.numCols + paramInt2)];
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\util\locale\provider\BreakDictionary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */