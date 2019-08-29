/*     */ package sun.security.util;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.security.AlgorithmParameters;
/*     */ import java.security.CryptoPrimitive;
/*     */ import java.security.Key;
/*     */ import java.security.PublicKey;
/*     */ import java.security.Timestamp;
/*     */ import java.security.cert.CertPathValidatorException;
/*     */ import java.security.cert.CertPathValidatorException.BasicReason;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.Calendar.Builder;
/*     */ import java.util.Collection;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.TimeZone;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
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
/*     */ public class DisabledAlgorithmConstraints
/*     */   extends AbstractAlgorithmConstraints
/*     */ {
/*  61 */   private static final Debug debug = Debug.getInstance("certpath");
/*     */   
/*     */ 
/*     */ 
/*     */   public static final String PROPERTY_CERTPATH_DISABLED_ALGS = "jdk.certpath.disabledAlgorithms";
/*     */   
/*     */ 
/*     */ 
/*     */   public static final String PROPERTY_TLS_DISABLED_ALGS = "jdk.tls.disabledAlgorithms";
/*     */   
/*     */ 
/*     */ 
/*     */   public static final String PROPERTY_JAR_DISABLED_ALGS = "jdk.jar.disabledAlgorithms";
/*     */   
/*     */ 
/*     */   private final String[] disabledAlgorithms;
/*     */   
/*     */ 
/*     */   private final Constraints algorithmConstraints;
/*     */   
/*     */ 
/*     */ 
/*     */   public DisabledAlgorithmConstraints(String paramString)
/*     */   {
/*  85 */     this(paramString, new AlgorithmDecomposer());
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
/*     */   public DisabledAlgorithmConstraints(String paramString, AlgorithmDecomposer paramAlgorithmDecomposer)
/*     */   {
/*  98 */     super(paramAlgorithmDecomposer);
/*  99 */     this.disabledAlgorithms = getAlgorithms(paramString);
/* 100 */     this.algorithmConstraints = new Constraints(this.disabledAlgorithms);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final boolean permits(Set<CryptoPrimitive> paramSet, String paramString, AlgorithmParameters paramAlgorithmParameters)
/*     */   {
/* 110 */     if (!checkAlgorithm(this.disabledAlgorithms, paramString, this.decomposer)) {
/* 111 */       return false;
/*     */     }
/*     */     
/* 114 */     if (paramAlgorithmParameters != null) {
/* 115 */       return this.algorithmConstraints.permits(paramString, paramAlgorithmParameters);
/*     */     }
/*     */     
/* 118 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final boolean permits(Set<CryptoPrimitive> paramSet, Key paramKey)
/*     */   {
/* 127 */     return checkConstraints(paramSet, "", paramKey, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final boolean permits(Set<CryptoPrimitive> paramSet, String paramString, Key paramKey, AlgorithmParameters paramAlgorithmParameters)
/*     */   {
/* 138 */     if ((paramString == null) || (paramString.length() == 0)) {
/* 139 */       throw new IllegalArgumentException("No algorithm name specified");
/*     */     }
/*     */     
/* 142 */     return checkConstraints(paramSet, paramString, paramKey, paramAlgorithmParameters);
/*     */   }
/*     */   
/*     */   public final void permits(ConstraintsParameters paramConstraintsParameters) throws CertPathValidatorException
/*     */   {
/* 147 */     permits(paramConstraintsParameters.getAlgorithm(), paramConstraintsParameters);
/*     */   }
/*     */   
/*     */   public final void permits(String paramString1, Key paramKey, AlgorithmParameters paramAlgorithmParameters, String paramString2)
/*     */     throws CertPathValidatorException
/*     */   {
/* 153 */     permits(paramString1, new ConstraintsParameters(paramString1, paramAlgorithmParameters, paramKey, paramString2 == null ? "generic" : paramString2));
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
/*     */   public final void permits(String paramString, ConstraintsParameters paramConstraintsParameters)
/*     */     throws CertPathValidatorException
/*     */   {
/* 167 */     this.algorithmConstraints.permits(paramString, paramConstraintsParameters);
/*     */   }
/*     */   
/*     */   public boolean checkProperty(String paramString)
/*     */   {
/* 172 */     paramString = paramString.toLowerCase(Locale.ENGLISH);
/* 173 */     for (String str : this.disabledAlgorithms) {
/* 174 */       if (str.toLowerCase(Locale.ENGLISH).indexOf(paramString) >= 0) {
/* 175 */         return true;
/*     */       }
/*     */     }
/* 178 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean checkConstraints(Set<CryptoPrimitive> paramSet, String paramString, Key paramKey, AlgorithmParameters paramAlgorithmParameters)
/*     */   {
/* 186 */     if (paramKey == null) {
/* 187 */       throw new IllegalArgumentException("The key cannot be null");
/*     */     }
/*     */     
/*     */ 
/* 191 */     if ((paramString != null) && (paramString.length() != 0) && 
/* 192 */       (!permits(paramSet, paramString, paramAlgorithmParameters))) {
/* 193 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 198 */     if (!permits(paramSet, paramKey.getAlgorithm(), null)) {
/* 199 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 203 */     return this.algorithmConstraints.permits(paramKey);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class Constraints
/*     */   {
/* 226 */     private Map<String, List<Constraint>> constraintsMap = new HashMap();
/*     */     
/*     */     private static class Holder {
/* 229 */       private static final Pattern DENY_AFTER_PATTERN = Pattern.compile("denyAfter\\s+(\\d{4})-(\\d{2})-(\\d{2})");
/*     */     }
/*     */     
/*     */     public Constraints(String[] paramArrayOfString) {
/*     */       label717:
/* 234 */       for (String str1 : paramArrayOfString)
/* 235 */         if ((str1 != null) && (!str1.isEmpty()))
/*     */         {
/*     */ 
/*     */ 
/* 239 */           str1 = str1.trim();
/* 240 */           if (DisabledAlgorithmConstraints.debug != null) {
/* 241 */             DisabledAlgorithmConstraints.debug.println("Constraints: " + str1);
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 246 */           int k = str1.indexOf(' ');
/* 247 */           String str2 = AlgorithmDecomposer.hashName((k > 0 ? str1
/* 248 */             .substring(0, k) : str1)
/*     */             
/* 250 */             .toUpperCase(Locale.ENGLISH));
/*     */           
/* 252 */           List localList = (List)this.constraintsMap.getOrDefault(str2, new ArrayList(1));
/*     */           
/*     */ 
/*     */ 
/* 256 */           for (Object localObject1 = AlgorithmDecomposer.getAliases(str2).iterator(); ((Iterator)localObject1).hasNext();) { localObject2 = (String)((Iterator)localObject1).next();
/* 257 */             this.constraintsMap.putIfAbsent(localObject2, localList);
/*     */           }
/*     */           Object localObject2;
/* 260 */           if (k <= 0) {
/* 261 */             localList.add(new DisabledConstraint(str2));
/*     */           }
/*     */           else
/*     */           {
/* 265 */             localObject1 = str1.substring(k + 1);
/*     */             
/*     */ 
/* 268 */             Object localObject3 = null;
/*     */             
/* 270 */             int m = 0;
/*     */             
/* 272 */             int n = 0;
/*     */             
/* 274 */             for (String str3 : ((String)localObject1).split("&")) {
/* 275 */               str3 = str3.trim();
/*     */               
/*     */ 
/* 278 */               if (str3.startsWith("keySize")) {
/* 279 */                 if (DisabledAlgorithmConstraints.debug != null) {
/* 280 */                   DisabledAlgorithmConstraints.debug.println("Constraints set to keySize: " + str3);
/*     */                 }
/*     */                 
/* 283 */                 StringTokenizer localStringTokenizer = new StringTokenizer(str3);
/* 284 */                 if (!"keySize".equals(localStringTokenizer.nextToken())) {
/* 285 */                   throw new IllegalArgumentException("Error in security property. Constraint unknown: " + str3);
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/* 291 */                 localObject2 = new KeySizeConstraint(str2, Constraint.Operator.of(localStringTokenizer.nextToken()), Integer.parseInt(localStringTokenizer.nextToken()));
/*     */               }
/* 293 */               else if (str3.equalsIgnoreCase("jdkCA")) {
/* 294 */                 if (DisabledAlgorithmConstraints.debug != null) {
/* 295 */                   DisabledAlgorithmConstraints.debug.println("Constraints set to jdkCA.");
/*     */                 }
/* 297 */                 if (m != 0) {
/* 298 */                   throw new IllegalArgumentException("Only one jdkCA entry allowed in property. Constraint: " + str1);
/*     */                 }
/*     */                 
/*     */ 
/* 302 */                 localObject2 = new jdkCAConstraint(str2);
/* 303 */                 m = 1;
/*     */               } else {
/* 305 */                 if (str3.startsWith("denyAfter")) {
/*     */                   Matcher localMatcher;
/* 307 */                   if ((localMatcher = Holder.DENY_AFTER_PATTERN.matcher(str3)).matches()) {
/* 308 */                     if (DisabledAlgorithmConstraints.debug != null) {
/* 309 */                       DisabledAlgorithmConstraints.debug.println("Constraints set to denyAfter");
/*     */                     }
/* 311 */                     if (n != 0) {
/* 312 */                       throw new IllegalArgumentException("Only one denyAfter entry allowed in property. Constraint: " + str1);
/*     */                     }
/*     */                     
/*     */ 
/* 316 */                     int i3 = Integer.parseInt(localMatcher.group(1));
/* 317 */                     int i4 = Integer.parseInt(localMatcher.group(2));
/* 318 */                     int i5 = Integer.parseInt(localMatcher.group(3));
/* 319 */                     localObject2 = new DenyAfterConstraint(str2, i3, i4, i5);
/*     */                     
/* 321 */                     n = 1;
/* 322 */                     break label717; } } if (str3.startsWith("usage")) {
/* 323 */                   String[] arrayOfString3 = str3.substring(5).trim().split(" ");
/* 324 */                   localObject2 = new UsageConstraint(str2, arrayOfString3);
/* 325 */                   if (DisabledAlgorithmConstraints.debug != null) {
/* 326 */                     DisabledAlgorithmConstraints.debug.println("Constraints usage length is " + arrayOfString3.length);
/*     */                   }
/*     */                 } else {
/* 329 */                   throw new IllegalArgumentException("Error in security property. Constraint unknown: " + str3);
/*     */                 }
/*     */               }
/*     */               
/*     */ 
/*     */ 
/* 335 */               if (localObject3 == null) {
/* 336 */                 localList.add(localObject2);
/*     */               } else {
/* 338 */                 ((Constraint)localObject3).nextConstraint = ((Constraint)localObject2);
/*     */               }
/* 340 */               localObject3 = localObject2;
/*     */             }
/*     */           }
/*     */         }
/*     */     }
/*     */     
/*     */     private List<Constraint> getConstraints(String paramString) {
/* 347 */       return (List)this.constraintsMap.get(paramString);
/*     */     }
/*     */     
/*     */     public boolean permits(Key paramKey)
/*     */     {
/* 352 */       List localList = getConstraints(paramKey.getAlgorithm());
/* 353 */       if (localList == null) {
/* 354 */         return true;
/*     */       }
/* 356 */       for (Constraint localConstraint : localList) {
/* 357 */         if (!localConstraint.permits(paramKey)) {
/* 358 */           if (DisabledAlgorithmConstraints.debug != null) {
/* 359 */             DisabledAlgorithmConstraints.debug.println("keySizeConstraint: failed key constraint check " + 
/* 360 */               KeyUtil.getKeySize(paramKey));
/*     */           }
/* 362 */           return false;
/*     */         }
/*     */       }
/* 365 */       return true;
/*     */     }
/*     */     
/*     */     public boolean permits(String paramString, AlgorithmParameters paramAlgorithmParameters)
/*     */     {
/* 370 */       List localList = getConstraints(paramString);
/* 371 */       if (localList == null) {
/* 372 */         return true;
/*     */       }
/*     */       
/* 375 */       for (Constraint localConstraint : localList) {
/* 376 */         if (!localConstraint.permits(paramAlgorithmParameters)) {
/* 377 */           if (DisabledAlgorithmConstraints.debug != null) {
/* 378 */             DisabledAlgorithmConstraints.debug.println("keySizeConstraint: failed algorithm parameters constraint check " + paramAlgorithmParameters);
/*     */           }
/*     */           
/*     */ 
/* 382 */           return false;
/*     */         }
/*     */       }
/*     */       
/* 386 */       return true;
/*     */     }
/*     */     
/*     */     public void permits(String paramString, ConstraintsParameters paramConstraintsParameters)
/*     */       throws CertPathValidatorException
/*     */     {
/* 392 */       X509Certificate localX509Certificate = paramConstraintsParameters.getCertificate();
/*     */       
/* 394 */       if (DisabledAlgorithmConstraints.debug != null) {
/* 395 */         DisabledAlgorithmConstraints.debug.println("Constraints.permits(): " + paramString + " Variant: " + paramConstraintsParameters
/* 396 */           .getVariant());
/*     */       }
/*     */       
/*     */ 
/* 400 */       HashSet localHashSet = new HashSet();
/* 401 */       if (paramString != null) {
/* 402 */         localHashSet.addAll(AlgorithmDecomposer.decomposeOneHash(paramString));
/*     */       }
/*     */       
/*     */ 
/* 406 */       if (localX509Certificate != null) {
/* 407 */         localHashSet.add(localX509Certificate.getPublicKey().getAlgorithm());
/*     */       }
/* 409 */       if (paramConstraintsParameters.getPublicKey() != null) {
/* 410 */         localHashSet.add(paramConstraintsParameters.getPublicKey().getAlgorithm());
/*     */       }
/*     */       
/* 413 */       for (String str : localHashSet) {
/* 414 */         List localList = getConstraints(str);
/* 415 */         if (localList != null)
/*     */         {
/*     */ 
/* 418 */           for (Constraint localConstraint : localList) {
/* 419 */             localConstraint.permits(paramConstraintsParameters);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static abstract class Constraint
/*     */   {
/*     */     String algorithm;
/*     */     
/*     */ 
/*     */ 
/* 436 */     Constraint nextConstraint = null;
/*     */     
/*     */     static enum Operator
/*     */     {
/* 440 */       EQ, 
/* 441 */       NE, 
/* 442 */       LT, 
/* 443 */       LE, 
/* 444 */       GT, 
/* 445 */       GE;
/*     */       
/*     */       private Operator() {}
/* 448 */       static Operator of(String paramString) { switch (paramString) {
/*     */         case "==": 
/* 450 */           return EQ;
/*     */         case "!=": 
/* 452 */           return NE;
/*     */         case "<": 
/* 454 */           return LT;
/*     */         case "<=": 
/* 456 */           return LE;
/*     */         case ">": 
/* 458 */           return GT;
/*     */         case ">=": 
/* 460 */           return GE;
/*     */         }
/*     */         
/* 463 */         throw new IllegalArgumentException("Error in security property. " + paramString + " is not a legal Operator");
/*     */       }
/*     */     }
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
/*     */     public boolean permits(Key paramKey)
/*     */     {
/* 479 */       return true;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean permits(AlgorithmParameters paramAlgorithmParameters)
/*     */     {
/* 491 */       return true;
/*     */     }
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
/*     */     public abstract void permits(ConstraintsParameters paramConstraintsParameters)
/*     */       throws CertPathValidatorException;
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
/*     */     boolean next(ConstraintsParameters paramConstraintsParameters)
/*     */       throws CertPathValidatorException
/*     */     {
/* 527 */       if (this.nextConstraint != null) {
/* 528 */         this.nextConstraint.permits(paramConstraintsParameters);
/* 529 */         return true;
/*     */       }
/* 531 */       return false;
/*     */     }
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
/*     */     boolean next(Key paramKey)
/*     */     {
/* 549 */       if ((this.nextConstraint != null) && (this.nextConstraint.permits(paramKey))) {
/* 550 */         return true;
/*     */       }
/* 552 */       return false;
/*     */     }
/*     */     
/*     */     String extendedMsg(ConstraintsParameters paramConstraintsParameters) {
/* 556 */       return 
/*     */       
/*     */ 
/*     */ 
/* 560 */         " used with certificate: " + paramConstraintsParameters.getCertificate().getSubjectX500Principal() + (paramConstraintsParameters.getVariant() != "generic" ? ".  Usage was " + paramConstraintsParameters.getVariant() : ".");
/*     */     }
/*     */   }
/*     */   
/*     */   private static class jdkCAConstraint
/*     */     extends Constraint
/*     */   {
/*     */     jdkCAConstraint(String paramString)
/*     */     {
/* 569 */       super();
/* 570 */       this.algorithm = paramString;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void permits(ConstraintsParameters paramConstraintsParameters)
/*     */       throws CertPathValidatorException
/*     */     {
/* 581 */       if (DisabledAlgorithmConstraints.debug != null) {
/* 582 */         DisabledAlgorithmConstraints.debug.println("jdkCAConstraints.permits(): " + this.algorithm);
/*     */       }
/*     */       
/*     */ 
/* 586 */       if (paramConstraintsParameters.isTrustedMatch()) {
/* 587 */         if (next(paramConstraintsParameters)) {
/* 588 */           return;
/*     */         }
/*     */         
/*     */ 
/* 592 */         throw new CertPathValidatorException("Algorithm constraints check failed on certificate anchor limits. " + this.algorithm + extendedMsg(paramConstraintsParameters), null, null, -1, BasicReason.ALGORITHM_CONSTRAINED);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class DenyAfterConstraint
/*     */     extends Constraint
/*     */   {
/*     */     private Date denyAfterDate;
/*     */     
/* 604 */     private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d HH:mm:ss z yyyy");
/*     */     
/*     */     DenyAfterConstraint(String paramString, int paramInt1, int paramInt2, int paramInt3) {
/* 607 */       super();
/*     */       
/*     */ 
/* 610 */       this.algorithm = paramString;
/*     */       
/* 612 */       if (DisabledAlgorithmConstraints.debug != null) {
/* 613 */         DisabledAlgorithmConstraints.debug.println("DenyAfterConstraint read in as:  year " + paramInt1 + ", month = " + paramInt2 + ", day = " + paramInt3);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 618 */       Calendar localCalendar = new Builder().setTimeZone(TimeZone.getTimeZone("GMT")).setDate(paramInt1, paramInt2 - 1, paramInt3).build();
/*     */       
/* 620 */       if ((paramInt1 > localCalendar.getActualMaximum(1)) || 
/* 621 */         (paramInt1 < localCalendar.getActualMinimum(1))) {
/* 622 */         throw new IllegalArgumentException("Invalid year given in constraint: " + paramInt1);
/*     */       }
/*     */       
/* 625 */       if ((paramInt2 - 1 > localCalendar.getActualMaximum(2)) || 
/* 626 */         (paramInt2 - 1 < localCalendar.getActualMinimum(2))) {
/* 627 */         throw new IllegalArgumentException("Invalid month given in constraint: " + paramInt2);
/*     */       }
/*     */       
/* 630 */       if ((paramInt3 > localCalendar.getActualMaximum(5)) || 
/* 631 */         (paramInt3 < localCalendar.getActualMinimum(5))) {
/* 632 */         throw new IllegalArgumentException("Invalid Day of Month given in constraint: " + paramInt3);
/*     */       }
/*     */       
/*     */ 
/* 636 */       this.denyAfterDate = localCalendar.getTime();
/* 637 */       if (DisabledAlgorithmConstraints.debug != null) {
/* 638 */         DisabledAlgorithmConstraints.debug.println("DenyAfterConstraint date set to: " + dateFormat
/* 639 */           .format(this.denyAfterDate));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void permits(ConstraintsParameters paramConstraintsParameters)
/*     */       throws CertPathValidatorException
/*     */     {
/*     */       Date localDate;
/*     */       
/*     */ 
/*     */ 
/*     */       String str;
/*     */       
/*     */ 
/*     */ 
/* 657 */       if (paramConstraintsParameters.getJARTimestamp() != null) {
/* 658 */         localDate = paramConstraintsParameters.getJARTimestamp().getTimestamp();
/* 659 */         str = "JAR Timestamp date: ";
/* 660 */       } else if (paramConstraintsParameters.getPKIXParamDate() != null) {
/* 661 */         localDate = paramConstraintsParameters.getPKIXParamDate();
/* 662 */         str = "PKIXParameter date: ";
/*     */       } else {
/* 664 */         localDate = new Date();
/* 665 */         str = "Current date: ";
/*     */       }
/*     */       
/* 668 */       if (!this.denyAfterDate.after(localDate)) {
/* 669 */         if (next(paramConstraintsParameters)) {
/* 670 */           return;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 676 */         throw new CertPathValidatorException("denyAfter constraint check failed: " + this.algorithm + " used with Constraint date: " + dateFormat.format(this.denyAfterDate) + "; " + str + dateFormat.format(localDate) + extendedMsg(paramConstraintsParameters), null, null, -1, BasicReason.ALGORITHM_CONSTRAINED);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean permits(Key paramKey)
/*     */     {
/* 687 */       if (next(paramKey)) {
/* 688 */         return true;
/*     */       }
/* 690 */       if (DisabledAlgorithmConstraints.debug != null) {
/* 691 */         DisabledAlgorithmConstraints.debug.println("DenyAfterConstraints.permits(): " + this.algorithm);
/*     */       }
/*     */       
/* 694 */       return this.denyAfterDate.after(new Date());
/*     */     }
/*     */   }
/*     */   
/*     */   private static class UsageConstraint
/*     */     extends Constraint
/*     */   {
/*     */     String[] usages;
/*     */     
/*     */     UsageConstraint(String paramString, String[] paramArrayOfString)
/*     */     {
/* 705 */       super();
/* 706 */       this.algorithm = paramString;
/* 707 */       this.usages = paramArrayOfString;
/*     */     }
/*     */     
/*     */     public void permits(ConstraintsParameters paramConstraintsParameters)
/*     */       throws CertPathValidatorException
/*     */     {
/* 713 */       for (String str1 : this.usages)
/*     */       {
/* 715 */         String str2 = null;
/* 716 */         if (str1.compareToIgnoreCase("TLSServer") == 0) {
/* 717 */           str2 = "tls server";
/* 718 */         } else if (str1.compareToIgnoreCase("TLSClient") == 0) {
/* 719 */           str2 = "tls client";
/* 720 */         } else if (str1.compareToIgnoreCase("SignedJAR") == 0) {
/* 721 */           str2 = "plugin code signing";
/*     */         }
/*     */         
/* 724 */         if (DisabledAlgorithmConstraints.debug != null) {
/* 725 */           DisabledAlgorithmConstraints.debug.println("Checking if usage constraint \"" + str2 + "\" matches \"" + paramConstraintsParameters
/* 726 */             .getVariant() + "\"");
/*     */           
/*     */ 
/* 729 */           ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
/* 730 */           PrintStream localPrintStream = new PrintStream(localByteArrayOutputStream);
/* 731 */           new Exception().printStackTrace(localPrintStream);
/* 732 */           DisabledAlgorithmConstraints.debug.println(localByteArrayOutputStream.toString());
/*     */         }
/* 734 */         if (paramConstraintsParameters.getVariant().compareTo(str2) == 0) {
/* 735 */           if (next(paramConstraintsParameters)) {
/* 736 */             return;
/*     */           }
/*     */           
/*     */ 
/* 740 */           throw new CertPathValidatorException("Usage constraint " + str1 + " check failed: " + this.algorithm + extendedMsg(paramConstraintsParameters), null, null, -1, BasicReason.ALGORITHM_CONSTRAINED);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class KeySizeConstraint
/*     */     extends Constraint
/*     */   {
/*     */     private int minSize;
/*     */     
/*     */     private int maxSize;
/*     */     
/* 755 */     private int prohibitedSize = -1;
/*     */     private int size;
/*     */     
/* 758 */     public KeySizeConstraint(String paramString, Operator paramOperator, int paramInt) { super();
/* 759 */       this.algorithm = paramString;
/* 760 */       switch (DisabledAlgorithmConstraints.1.$SwitchMap$sun$security$util$DisabledAlgorithmConstraints$Constraint$Operator[paramOperator.ordinal()]) {
/*     */       case 1: 
/* 762 */         this.minSize = 0;
/* 763 */         this.maxSize = Integer.MAX_VALUE;
/* 764 */         this.prohibitedSize = paramInt;
/* 765 */         break;
/*     */       case 2: 
/* 767 */         this.minSize = paramInt;
/* 768 */         this.maxSize = paramInt;
/* 769 */         break;
/*     */       case 3: 
/* 771 */         this.minSize = paramInt;
/* 772 */         this.maxSize = Integer.MAX_VALUE;
/* 773 */         break;
/*     */       case 4: 
/* 775 */         this.minSize = (paramInt + 1);
/* 776 */         this.maxSize = Integer.MAX_VALUE;
/* 777 */         break;
/*     */       case 5: 
/* 779 */         this.minSize = 0;
/* 780 */         this.maxSize = paramInt;
/* 781 */         break;
/*     */       case 6: 
/* 783 */         this.minSize = 0;
/* 784 */         this.maxSize = (paramInt > 1 ? paramInt - 1 : 0);
/* 785 */         break;
/*     */       
/*     */       default: 
/* 788 */         this.minSize = Integer.MAX_VALUE;
/* 789 */         this.maxSize = -1;
/*     */       }
/*     */       
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void permits(ConstraintsParameters paramConstraintsParameters)
/*     */       throws CertPathValidatorException
/*     */     {
/* 803 */       Object localObject = null;
/* 804 */       if (paramConstraintsParameters.getPublicKey() != null) {
/* 805 */         localObject = paramConstraintsParameters.getPublicKey();
/* 806 */       } else if (paramConstraintsParameters.getCertificate() != null) {
/* 807 */         localObject = paramConstraintsParameters.getCertificate().getPublicKey();
/*     */       }
/* 809 */       if ((localObject != null) && (!permitsImpl((Key)localObject))) {
/* 810 */         if (this.nextConstraint != null) {
/* 811 */           this.nextConstraint.permits(paramConstraintsParameters);
/* 812 */           return;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 817 */         throw new CertPathValidatorException("Algorithm constraints check failed on keysize limits. " + this.algorithm + " " + KeyUtil.getKeySize((Key)localObject) + "bit key" + extendedMsg(paramConstraintsParameters), null, null, -1, BasicReason.ALGORITHM_CONSTRAINED);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean permits(Key paramKey)
/*     */     {
/* 829 */       if ((this.nextConstraint != null) && (this.nextConstraint.permits(paramKey))) {
/* 830 */         return true;
/*     */       }
/* 832 */       if (DisabledAlgorithmConstraints.debug != null) {
/* 833 */         DisabledAlgorithmConstraints.debug.println("KeySizeConstraints.permits(): " + this.algorithm);
/*     */       }
/*     */       
/* 836 */       return permitsImpl(paramKey);
/*     */     }
/*     */     
/*     */     public boolean permits(AlgorithmParameters paramAlgorithmParameters)
/*     */     {
/* 841 */       String str = paramAlgorithmParameters.getAlgorithm();
/* 842 */       if (!this.algorithm.equalsIgnoreCase(paramAlgorithmParameters.getAlgorithm()))
/*     */       {
/*     */ 
/* 845 */         Collection localCollection = AlgorithmDecomposer.getAliases(this.algorithm);
/* 846 */         if (!localCollection.contains(str)) {
/* 847 */           return true;
/*     */         }
/*     */       }
/*     */       
/* 851 */       int i = KeyUtil.getKeySize(paramAlgorithmParameters);
/* 852 */       if (i == 0)
/* 853 */         return false;
/* 854 */       if (i > 0) {
/* 855 */         return (i >= this.minSize) && (i <= this.maxSize) && (this.prohibitedSize != i);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 860 */       return true;
/*     */     }
/*     */     
/*     */     private boolean permitsImpl(Key paramKey)
/*     */     {
/* 865 */       if (this.algorithm.compareToIgnoreCase(paramKey.getAlgorithm()) != 0) {
/* 866 */         return true;
/*     */       }
/*     */       
/* 869 */       this.size = KeyUtil.getKeySize(paramKey);
/* 870 */       if (this.size == 0)
/* 871 */         return false;
/* 872 */       if (this.size > 0) {
/* 873 */         return (this.size >= this.minSize) && (this.size <= this.maxSize) && (this.prohibitedSize != this.size);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 878 */       return true;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class DisabledConstraint extends Constraint
/*     */   {
/*     */     DisabledConstraint(String paramString)
/*     */     {
/* 886 */       super();
/* 887 */       this.algorithm = paramString;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void permits(ConstraintsParameters paramConstraintsParameters)
/*     */       throws CertPathValidatorException
/*     */     {
/* 895 */       throw new CertPathValidatorException("Algorithm constraints check failed on disabled algorithm: " + this.algorithm + extendedMsg(paramConstraintsParameters), null, null, -1, BasicReason.ALGORITHM_CONSTRAINED);
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean permits(Key paramKey)
/*     */     {
/* 901 */       return false;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\util\DisabledAlgorithmConstraints.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */