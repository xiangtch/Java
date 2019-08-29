/*     */ package sun.security.provider.certpath;
/*     */ 
/*     */ import java.security.cert.PolicyNode;
/*     */ import java.security.cert.PolicyQualifierInfo;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
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
/*     */ final class PolicyNodeImpl
/*     */   implements PolicyNode
/*     */ {
/*     */   private static final String ANY_POLICY = "2.5.29.32.0";
/*     */   private PolicyNodeImpl mParent;
/*     */   private HashSet<PolicyNodeImpl> mChildren;
/*     */   private String mValidPolicy;
/*     */   private HashSet<PolicyQualifierInfo> mQualifierSet;
/*     */   private boolean mCriticalityIndicator;
/*     */   private HashSet<String> mExpectedPolicySet;
/*     */   private boolean mOriginalExpectedPolicySet;
/*     */   private int mDepth;
/*  69 */   private boolean isImmutable = false;
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
/*     */   PolicyNodeImpl(PolicyNodeImpl paramPolicyNodeImpl, String paramString, Set<PolicyQualifierInfo> paramSet, boolean paramBoolean1, Set<String> paramSet1, boolean paramBoolean2)
/*     */   {
/*  93 */     this.mParent = paramPolicyNodeImpl;
/*  94 */     this.mChildren = new HashSet();
/*     */     
/*  96 */     if (paramString != null) {
/*  97 */       this.mValidPolicy = paramString;
/*     */     } else {
/*  99 */       this.mValidPolicy = "";
/*     */     }
/* 101 */     if (paramSet != null) {
/* 102 */       this.mQualifierSet = new HashSet(paramSet);
/*     */     } else {
/* 104 */       this.mQualifierSet = new HashSet();
/*     */     }
/* 106 */     this.mCriticalityIndicator = paramBoolean1;
/*     */     
/* 108 */     if (paramSet1 != null) {
/* 109 */       this.mExpectedPolicySet = new HashSet(paramSet1);
/*     */     } else {
/* 111 */       this.mExpectedPolicySet = new HashSet();
/*     */     }
/* 113 */     this.mOriginalExpectedPolicySet = (!paramBoolean2);
/*     */     
/*     */ 
/* 116 */     if (this.mParent != null) {
/* 117 */       this.mDepth = (this.mParent.getDepth() + 1);
/* 118 */       this.mParent.addChild(this);
/*     */     } else {
/* 120 */       this.mDepth = 0;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   PolicyNodeImpl(PolicyNodeImpl paramPolicyNodeImpl1, PolicyNodeImpl paramPolicyNodeImpl2)
/*     */   {
/* 133 */     this(paramPolicyNodeImpl1, paramPolicyNodeImpl2.mValidPolicy, paramPolicyNodeImpl2.mQualifierSet, paramPolicyNodeImpl2.mCriticalityIndicator, paramPolicyNodeImpl2.mExpectedPolicySet, false);
/*     */   }
/*     */   
/*     */ 
/*     */   public PolicyNode getParent()
/*     */   {
/* 139 */     return this.mParent;
/*     */   }
/*     */   
/*     */   public Iterator<PolicyNodeImpl> getChildren()
/*     */   {
/* 144 */     return Collections.unmodifiableSet(this.mChildren).iterator();
/*     */   }
/*     */   
/*     */   public int getDepth()
/*     */   {
/* 149 */     return this.mDepth;
/*     */   }
/*     */   
/*     */   public String getValidPolicy()
/*     */   {
/* 154 */     return this.mValidPolicy;
/*     */   }
/*     */   
/*     */   public Set<PolicyQualifierInfo> getPolicyQualifiers()
/*     */   {
/* 159 */     return Collections.unmodifiableSet(this.mQualifierSet);
/*     */   }
/*     */   
/*     */   public Set<String> getExpectedPolicies()
/*     */   {
/* 164 */     return Collections.unmodifiableSet(this.mExpectedPolicySet);
/*     */   }
/*     */   
/*     */   public boolean isCritical()
/*     */   {
/* 169 */     return this.mCriticalityIndicator;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 181 */     StringBuilder localStringBuilder = new StringBuilder(asString());
/*     */     
/* 183 */     for (PolicyNodeImpl localPolicyNodeImpl : this.mChildren) {
/* 184 */       localStringBuilder.append(localPolicyNodeImpl);
/*     */     }
/* 186 */     return localStringBuilder.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   boolean isImmutable()
/*     */   {
/* 192 */     return this.isImmutable;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   void setImmutable()
/*     */   {
/* 200 */     if (this.isImmutable)
/* 201 */       return;
/* 202 */     for (PolicyNodeImpl localPolicyNodeImpl : this.mChildren) {
/* 203 */       localPolicyNodeImpl.setImmutable();
/*     */     }
/* 205 */     this.isImmutable = true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void addChild(PolicyNodeImpl paramPolicyNodeImpl)
/*     */   {
/* 215 */     if (this.isImmutable) {
/* 216 */       throw new IllegalStateException("PolicyNode is immutable");
/*     */     }
/* 218 */     this.mChildren.add(paramPolicyNodeImpl);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void addExpectedPolicy(String paramString)
/*     */   {
/* 230 */     if (this.isImmutable) {
/* 231 */       throw new IllegalStateException("PolicyNode is immutable");
/*     */     }
/* 233 */     if (this.mOriginalExpectedPolicySet) {
/* 234 */       this.mExpectedPolicySet.clear();
/* 235 */       this.mOriginalExpectedPolicySet = false;
/*     */     }
/* 237 */     this.mExpectedPolicySet.add(paramString);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void prune(int paramInt)
/*     */   {
/* 246 */     if (this.isImmutable) {
/* 247 */       throw new IllegalStateException("PolicyNode is immutable");
/*     */     }
/*     */     
/* 250 */     if (this.mChildren.size() == 0) {
/* 251 */       return;
/*     */     }
/* 253 */     Iterator localIterator = this.mChildren.iterator();
/* 254 */     while (localIterator.hasNext()) {
/* 255 */       PolicyNodeImpl localPolicyNodeImpl = (PolicyNodeImpl)localIterator.next();
/* 256 */       localPolicyNodeImpl.prune(paramInt);
/*     */       
/*     */ 
/* 259 */       if ((localPolicyNodeImpl.mChildren.size() == 0) && (paramInt > this.mDepth + 1)) {
/* 260 */         localIterator.remove();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   void deleteChild(PolicyNode paramPolicyNode)
/*     */   {
/* 270 */     if (this.isImmutable) {
/* 271 */       throw new IllegalStateException("PolicyNode is immutable");
/*     */     }
/* 273 */     this.mChildren.remove(paramPolicyNode);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   PolicyNodeImpl copyTree()
/*     */   {
/* 283 */     return copyTree(null);
/*     */   }
/*     */   
/*     */   private PolicyNodeImpl copyTree(PolicyNodeImpl paramPolicyNodeImpl) {
/* 287 */     PolicyNodeImpl localPolicyNodeImpl1 = new PolicyNodeImpl(paramPolicyNodeImpl, this);
/*     */     
/* 289 */     for (PolicyNodeImpl localPolicyNodeImpl2 : this.mChildren) {
/* 290 */       localPolicyNodeImpl2.copyTree(localPolicyNodeImpl1);
/*     */     }
/*     */     
/* 293 */     return localPolicyNodeImpl1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   Set<PolicyNodeImpl> getPolicyNodes(int paramInt)
/*     */   {
/* 303 */     HashSet localHashSet = new HashSet();
/* 304 */     getPolicyNodes(paramInt, localHashSet);
/* 305 */     return localHashSet;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void getPolicyNodes(int paramInt, Set<PolicyNodeImpl> paramSet)
/*     */   {
/* 314 */     if (this.mDepth == paramInt) {
/* 315 */       paramSet.add(this);
/*     */     } else {
/* 317 */       for (PolicyNodeImpl localPolicyNodeImpl : this.mChildren) {
/* 318 */         localPolicyNodeImpl.getPolicyNodes(paramInt, paramSet);
/*     */       }
/*     */     }
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
/*     */   Set<PolicyNodeImpl> getPolicyNodesExpected(int paramInt, String paramString, boolean paramBoolean)
/*     */   {
/* 337 */     if (paramString.equals("2.5.29.32.0")) {
/* 338 */       return getPolicyNodes(paramInt);
/*     */     }
/* 340 */     return getPolicyNodesExpectedHelper(paramInt, paramString, paramBoolean);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private Set<PolicyNodeImpl> getPolicyNodesExpectedHelper(int paramInt, String paramString, boolean paramBoolean)
/*     */   {
/* 347 */     HashSet localHashSet = new HashSet();
/*     */     
/* 349 */     if (this.mDepth < paramInt) {
/* 350 */       for (PolicyNodeImpl localPolicyNodeImpl : this.mChildren) {
/* 351 */         localHashSet.addAll(localPolicyNodeImpl.getPolicyNodesExpectedHelper(paramInt, paramString, paramBoolean));
/*     */ 
/*     */       }
/*     */       
/*     */     }
/* 356 */     else if (paramBoolean) {
/* 357 */       if (this.mExpectedPolicySet.contains("2.5.29.32.0")) {
/* 358 */         localHashSet.add(this);
/*     */       }
/* 360 */     } else if (this.mExpectedPolicySet.contains(paramString)) {
/* 361 */       localHashSet.add(this);
/*     */     }
/*     */     
/*     */ 
/* 365 */     return localHashSet;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   Set<PolicyNodeImpl> getPolicyNodesValid(int paramInt, String paramString)
/*     */   {
/* 377 */     HashSet localHashSet = new HashSet();
/*     */     
/* 379 */     if (this.mDepth < paramInt) {
/* 380 */       for (PolicyNodeImpl localPolicyNodeImpl : this.mChildren) {
/* 381 */         localHashSet.addAll(localPolicyNodeImpl.getPolicyNodesValid(paramInt, paramString));
/*     */       }
/*     */       
/* 384 */     } else if (this.mValidPolicy.equals(paramString)) {
/* 385 */       localHashSet.add(this);
/*     */     }
/*     */     
/* 388 */     return localHashSet;
/*     */   }
/*     */   
/*     */   private static String policyToString(String paramString) {
/* 392 */     if (paramString.equals("2.5.29.32.0")) {
/* 393 */       return "anyPolicy";
/*     */     }
/* 395 */     return paramString;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   String asString()
/*     */   {
/* 403 */     if (this.mParent == null) {
/* 404 */       return "anyPolicy  ROOT\n";
/*     */     }
/* 406 */     StringBuilder localStringBuilder = new StringBuilder();
/* 407 */     int i = 0; for (int j = getDepth(); i < j; i++) {
/* 408 */       localStringBuilder.append("  ");
/*     */     }
/* 410 */     localStringBuilder.append(policyToString(getValidPolicy()));
/* 411 */     localStringBuilder.append("  CRIT: ");
/* 412 */     localStringBuilder.append(isCritical());
/* 413 */     localStringBuilder.append("  EP: ");
/* 414 */     for (String str : getExpectedPolicies()) {
/* 415 */       localStringBuilder.append(policyToString(str));
/* 416 */       localStringBuilder.append(" ");
/*     */     }
/* 418 */     localStringBuilder.append(" (");
/* 419 */     localStringBuilder.append(getDepth());
/* 420 */     localStringBuilder.append(")\n");
/* 421 */     return localStringBuilder.toString();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\provider\certpath\PolicyNodeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */