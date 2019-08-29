/*      */ package sun.security.jgss.spnego;
/*      */ 
/*      */ import com.sun.security.jgss.ExtendedGSSContext;
/*      */ import com.sun.security.jgss.InquireType;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.security.AccessController;
/*      */ import java.security.Provider;
/*      */ import org.ietf.jgss.ChannelBinding;
/*      */ import org.ietf.jgss.GSSContext;
/*      */ import org.ietf.jgss.GSSCredential;
/*      */ import org.ietf.jgss.GSSException;
/*      */ import org.ietf.jgss.GSSName;
/*      */ import org.ietf.jgss.MessageProp;
/*      */ import org.ietf.jgss.Oid;
/*      */ import sun.security.action.GetBooleanAction;
/*      */ import sun.security.jgss.GSSCredentialImpl;
/*      */ import sun.security.jgss.GSSManagerImpl;
/*      */ import sun.security.jgss.GSSNameImpl;
/*      */ import sun.security.jgss.GSSUtil;
/*      */ import sun.security.jgss.spi.GSSContextSpi;
/*      */ import sun.security.jgss.spi.GSSCredentialSpi;
/*      */ import sun.security.jgss.spi.GSSNameSpi;
/*      */ import sun.security.util.BitArray;
/*      */ import sun.security.util.DerOutputStream;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class SpNegoContext
/*      */   implements GSSContextSpi
/*      */ {
/*      */   private static final int STATE_NEW = 1;
/*      */   private static final int STATE_IN_PROCESS = 2;
/*      */   private static final int STATE_DONE = 3;
/*      */   private static final int STATE_DELETED = 4;
/*   54 */   private int state = 1;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   60 */   private boolean credDelegState = false;
/*   61 */   private boolean mutualAuthState = true;
/*   62 */   private boolean replayDetState = true;
/*   63 */   private boolean sequenceDetState = true;
/*   64 */   private boolean confState = true;
/*   65 */   private boolean integState = true;
/*   66 */   private boolean delegPolicyState = false;
/*      */   
/*   68 */   private GSSNameSpi peerName = null;
/*   69 */   private GSSNameSpi myName = null;
/*   70 */   private SpNegoCredElement myCred = null;
/*      */   
/*   72 */   private GSSContext mechContext = null;
/*   73 */   private byte[] DER_mechTypes = null;
/*      */   
/*      */   private int lifetime;
/*      */   
/*      */   private ChannelBinding channelBinding;
/*      */   
/*      */   private boolean initiator;
/*   80 */   private Oid internal_mech = null;
/*      */   
/*      */ 
/*      */ 
/*      */   private final SpNegoMechFactory factory;
/*      */   
/*      */ 
/*   87 */   static final boolean DEBUG = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.security.spnego.debug")))
/*      */   
/*   89 */     .booleanValue();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public SpNegoContext(SpNegoMechFactory paramSpNegoMechFactory, GSSNameSpi paramGSSNameSpi, GSSCredentialSpi paramGSSCredentialSpi, int paramInt)
/*      */     throws GSSException
/*      */   {
/*   99 */     if (paramGSSNameSpi == null)
/*  100 */       throw new IllegalArgumentException("Cannot have null peer name");
/*  101 */     if ((paramGSSCredentialSpi != null) && (!(paramGSSCredentialSpi instanceof SpNegoCredElement))) {
/*  102 */       throw new IllegalArgumentException("Wrong cred element type");
/*      */     }
/*  104 */     this.peerName = paramGSSNameSpi;
/*  105 */     this.myCred = ((SpNegoCredElement)paramGSSCredentialSpi);
/*  106 */     this.lifetime = paramInt;
/*  107 */     this.initiator = true;
/*  108 */     this.factory = paramSpNegoMechFactory;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public SpNegoContext(SpNegoMechFactory paramSpNegoMechFactory, GSSCredentialSpi paramGSSCredentialSpi)
/*      */     throws GSSException
/*      */   {
/*  117 */     if ((paramGSSCredentialSpi != null) && (!(paramGSSCredentialSpi instanceof SpNegoCredElement))) {
/*  118 */       throw new IllegalArgumentException("Wrong cred element type");
/*      */     }
/*  120 */     this.myCred = ((SpNegoCredElement)paramGSSCredentialSpi);
/*  121 */     this.initiator = false;
/*  122 */     this.factory = paramSpNegoMechFactory;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public SpNegoContext(SpNegoMechFactory paramSpNegoMechFactory, byte[] paramArrayOfByte)
/*      */     throws GSSException
/*      */   {
/*  130 */     throw new GSSException(16, -1, "GSS Import Context not available");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public final void requestConf(boolean paramBoolean)
/*      */     throws GSSException
/*      */   {
/*  138 */     if ((this.state == 1) && (isInitiator())) {
/*  139 */       this.confState = paramBoolean;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public final boolean getConfState()
/*      */   {
/*  146 */     return this.confState;
/*      */   }
/*      */   
/*      */ 
/*      */   public final void requestInteg(boolean paramBoolean)
/*      */     throws GSSException
/*      */   {
/*  153 */     if ((this.state == 1) && (isInitiator())) {
/*  154 */       this.integState = paramBoolean;
/*      */     }
/*      */   }
/*      */   
/*      */   public final void requestDelegPolicy(boolean paramBoolean)
/*      */     throws GSSException
/*      */   {
/*  161 */     if ((this.state == 1) && (isInitiator())) {
/*  162 */       this.delegPolicyState = paramBoolean;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public final boolean getIntegState()
/*      */   {
/*  169 */     return this.integState;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public final boolean getDelegPolicyState()
/*      */   {
/*  176 */     if ((isInitiator()) && (this.mechContext != null) && ((this.mechContext instanceof ExtendedGSSContext)) && ((this.state == 2) || (this.state == 3)))
/*      */     {
/*      */ 
/*  179 */       return ((ExtendedGSSContext)this.mechContext).getDelegPolicyState();
/*      */     }
/*  181 */     return this.delegPolicyState;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public final void requestCredDeleg(boolean paramBoolean)
/*      */     throws GSSException
/*      */   {
/*  190 */     if ((this.state == 1) && (isInitiator())) {
/*  191 */       this.credDelegState = paramBoolean;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public final boolean getCredDelegState()
/*      */   {
/*  198 */     if ((isInitiator()) && (this.mechContext != null) && ((this.state == 2) || (this.state == 3)))
/*      */     {
/*  200 */       return this.mechContext.getCredDelegState();
/*      */     }
/*  202 */     return this.credDelegState;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final void requestMutualAuth(boolean paramBoolean)
/*      */     throws GSSException
/*      */   {
/*  212 */     if ((this.state == 1) && (isInitiator())) {
/*  213 */       this.mutualAuthState = paramBoolean;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final boolean getMutualAuthState()
/*      */   {
/*  223 */     return this.mutualAuthState;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final Oid getMech()
/*      */   {
/*  232 */     if (isEstablished()) {
/*  233 */       return getNegotiatedMech();
/*      */     }
/*  235 */     return SpNegoMechFactory.GSS_SPNEGO_MECH_OID;
/*      */   }
/*      */   
/*      */   public final Oid getNegotiatedMech() {
/*  239 */     return this.internal_mech;
/*      */   }
/*      */   
/*      */   public final Provider getProvider() {
/*  243 */     return SpNegoMechFactory.PROVIDER;
/*      */   }
/*      */   
/*      */   public final void dispose() throws GSSException {
/*  247 */     this.mechContext = null;
/*  248 */     this.state = 4;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final boolean isInitiator()
/*      */   {
/*  258 */     return this.initiator;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final boolean isProtReady()
/*      */   {
/*  270 */     return this.state == 3;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final byte[] initSecContext(InputStream paramInputStream, int paramInt)
/*      */     throws GSSException
/*      */   {
/*  289 */     Object localObject1 = null;
/*  290 */     NegTokenInit localNegTokenInit = null;
/*  291 */     byte[] arrayOfByte1 = null;
/*  292 */     int i = 11;
/*      */     
/*  294 */     if (DEBUG) {
/*  295 */       System.out.println("Entered SpNego.initSecContext with state=" + 
/*  296 */         printState(this.state));
/*      */     }
/*  298 */     if (!isInitiator()) {
/*  299 */       throw new GSSException(11, -1, "initSecContext on an acceptor GSSContext");
/*      */     }
/*      */     try
/*      */     {
/*      */       Object localObject2;
/*  304 */       if (this.state == 1) {
/*  305 */         this.state = 2;
/*      */         
/*  307 */         i = 13;
/*      */         
/*      */ 
/*  310 */         localObject2 = getAvailableMechs();
/*  311 */         this.DER_mechTypes = getEncodedMechs((Oid[])localObject2);
/*      */         
/*      */ 
/*  314 */         this.internal_mech = localObject2[0];
/*      */         
/*      */ 
/*  317 */         arrayOfByte1 = GSS_initSecContext(null);
/*      */         
/*  319 */         i = 10;
/*      */         
/*  321 */         localNegTokenInit = new NegTokenInit(this.DER_mechTypes, getContextFlags(), arrayOfByte1, null);
/*      */         
/*  323 */         if (DEBUG) {
/*  324 */           System.out.println("SpNegoContext.initSecContext: sending token of type = " + 
/*      */           
/*  326 */             SpNegoToken.getTokenName(localNegTokenInit.getType()));
/*      */         }
/*      */         
/*  329 */         localObject1 = localNegTokenInit.getEncoded();
/*      */       }
/*  331 */       else if (this.state == 2)
/*      */       {
/*  333 */         i = 11;
/*  334 */         if (paramInputStream == null) {
/*  335 */           throw new GSSException(i, -1, "No token received from peer!");
/*      */         }
/*      */         
/*      */ 
/*  339 */         i = 10;
/*  340 */         localObject2 = new byte[paramInputStream.available()];
/*  341 */         SpNegoToken.readFully(paramInputStream, (byte[])localObject2);
/*  342 */         if (DEBUG) {
/*  343 */           System.out.println("SpNegoContext.initSecContext: process received token = " + 
/*      */           
/*  345 */             SpNegoToken.getHexBytes((byte[])localObject2));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  350 */         localObject3 = new NegTokenTarg((byte[])localObject2);
/*      */         
/*  352 */         if (DEBUG) {
/*  353 */           System.out.println("SpNegoContext.initSecContext: received token of type = " + 
/*      */           
/*  355 */             SpNegoToken.getTokenName(((NegTokenTarg)localObject3).getType()));
/*      */         }
/*      */         
/*      */ 
/*  359 */         this.internal_mech = ((NegTokenTarg)localObject3).getSupportedMech();
/*  360 */         if (this.internal_mech == null)
/*      */         {
/*  362 */           throw new GSSException(i, -1, "supported mechanism from server is null");
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  367 */         SpNegoToken.NegoResult localNegoResult = null;
/*  368 */         int j = ((NegTokenTarg)localObject3).getNegotiatedResult();
/*  369 */         switch (j) {
/*      */         case 0: 
/*  371 */           localNegoResult = SpNegoToken.NegoResult.ACCEPT_COMPLETE;
/*  372 */           this.state = 3;
/*  373 */           break;
/*      */         case 1: 
/*  375 */           localNegoResult = SpNegoToken.NegoResult.ACCEPT_INCOMPLETE;
/*  376 */           this.state = 2;
/*  377 */           break;
/*      */         case 2: 
/*  379 */           localNegoResult = SpNegoToken.NegoResult.REJECT;
/*  380 */           this.state = 4;
/*  381 */           break;
/*      */         default: 
/*  383 */           this.state = 3;
/*      */         }
/*      */         
/*      */         
/*  387 */         i = 2;
/*      */         
/*  389 */         if (localNegoResult == SpNegoToken.NegoResult.REJECT)
/*      */         {
/*  391 */           throw new GSSException(i, -1, this.internal_mech.toString());
/*      */         }
/*      */         
/*  394 */         i = 10;
/*      */         
/*  396 */         if ((localNegoResult == SpNegoToken.NegoResult.ACCEPT_COMPLETE) || (localNegoResult == SpNegoToken.NegoResult.ACCEPT_INCOMPLETE))
/*      */         {
/*      */ 
/*      */ 
/*  400 */           byte[] arrayOfByte2 = ((NegTokenTarg)localObject3).getResponseToken();
/*  401 */           if (arrayOfByte2 == null) {
/*  402 */             if (!isMechContextEstablished())
/*      */             {
/*  404 */               throw new GSSException(i, -1, "mechanism token from server is null");
/*      */             }
/*      */           }
/*      */           else {
/*  408 */             arrayOfByte1 = GSS_initSecContext(arrayOfByte2);
/*      */           }
/*      */           
/*  411 */           if (!GSSUtil.useMSInterop()) {
/*  412 */             byte[] arrayOfByte3 = ((NegTokenTarg)localObject3).getMechListMIC();
/*  413 */             if (!verifyMechListMIC(this.DER_mechTypes, arrayOfByte3)) {
/*  414 */               throw new GSSException(i, -1, "verification of MIC on MechList Failed!");
/*      */             }
/*      */           }
/*      */           
/*  418 */           if (isMechContextEstablished()) {
/*  419 */             this.state = 3;
/*  420 */             localObject1 = arrayOfByte1;
/*  421 */             if (DEBUG) {
/*  422 */               System.out.println("SPNEGO Negotiated Mechanism = " + this.internal_mech + " " + 
/*      */               
/*  424 */                 GSSUtil.getMechStr(this.internal_mech));
/*      */             }
/*      */           }
/*      */           else {
/*  428 */             localNegTokenInit = new NegTokenInit(null, null, arrayOfByte1, null);
/*      */             
/*  430 */             if (DEBUG) {
/*  431 */               System.out.println("SpNegoContext.initSecContext: continue sending token of type = " + 
/*      */               
/*  433 */                 SpNegoToken.getTokenName(localNegTokenInit.getType()));
/*      */             }
/*      */             
/*  436 */             localObject1 = localNegTokenInit.getEncoded();
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */       }
/*  442 */       else if (DEBUG) {
/*  443 */         System.out.println(this.state);
/*      */       }
/*      */       
/*  446 */       if ((DEBUG) && 
/*  447 */         (localObject1 != null)) {
/*  448 */         System.out.println("SNegoContext.initSecContext: sending token = " + 
/*  449 */           SpNegoToken.getHexBytes((byte[])localObject1));
/*      */       }
/*      */     }
/*      */     catch (GSSException localGSSException)
/*      */     {
/*  454 */       localObject3 = new GSSException(i, -1, localGSSException.getMessage());
/*  455 */       ((GSSException)localObject3).initCause(localGSSException);
/*  456 */       throw ((Throwable)localObject3);
/*      */     }
/*      */     catch (IOException localIOException) {
/*  459 */       Object localObject3 = new GSSException(11, -1, localIOException.getMessage());
/*  460 */       ((GSSException)localObject3).initCause(localIOException);
/*  461 */       throw ((Throwable)localObject3);
/*      */     }
/*      */     
/*  464 */     return (byte[])localObject1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final byte[] acceptSecContext(InputStream paramInputStream, int paramInt)
/*      */     throws GSSException
/*      */   {
/*  483 */     byte[] arrayOfByte1 = null;
/*      */     
/*  485 */     boolean bool = true;
/*      */     
/*  487 */     if (DEBUG) {
/*  488 */       System.out.println("Entered SpNegoContext.acceptSecContext with state=" + 
/*  489 */         printState(this.state));
/*      */     }
/*      */     
/*  492 */     if (isInitiator())
/*  493 */       throw new GSSException(11, -1, "acceptSecContext on an initiator GSSContext");
/*      */     try { byte[] arrayOfByte2;
/*      */       Object localObject2;
/*      */       Object localObject3;
/*      */       Object localObject4;
/*  498 */       SpNegoToken.NegoResult localNegoResult; if (this.state == 1) {
/*  499 */         this.state = 2;
/*      */         
/*      */ 
/*  502 */         arrayOfByte2 = new byte[paramInputStream.available()];
/*  503 */         SpNegoToken.readFully(paramInputStream, arrayOfByte2);
/*  504 */         if (DEBUG) {
/*  505 */           System.out.println("SpNegoContext.acceptSecContext: receiving token = " + 
/*      */           
/*  507 */             SpNegoToken.getHexBytes(arrayOfByte2));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  512 */         localObject1 = new NegTokenInit(arrayOfByte2);
/*      */         
/*  514 */         if (DEBUG) {
/*  515 */           System.out.println("SpNegoContext.acceptSecContext: received token of type = " + 
/*      */           
/*  517 */             SpNegoToken.getTokenName(((NegTokenInit)localObject1).getType()));
/*      */         }
/*      */         
/*  520 */         localObject2 = ((NegTokenInit)localObject1).getMechTypeList();
/*  521 */         this.DER_mechTypes = ((NegTokenInit)localObject1).getMechTypes();
/*  522 */         if (this.DER_mechTypes == null) {
/*  523 */           bool = false;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  531 */         localObject3 = getAvailableMechs();
/*      */         
/*  533 */         localObject4 = negotiate_mech_type((Oid[])localObject3, (Oid[])localObject2);
/*  534 */         if (localObject4 == null) {
/*  535 */           bool = false;
/*      */         }
/*      */         
/*  538 */         this.internal_mech = ((Oid)localObject4);
/*      */         
/*      */ 
/*      */         byte[] arrayOfByte3;
/*      */         
/*  543 */         if ((localObject2[0].equals(localObject4)) || (
/*  544 */           (GSSUtil.isKerberosMech(localObject2[0])) && 
/*  545 */           (GSSUtil.isKerberosMech((Oid)localObject4))))
/*      */         {
/*  547 */           if ((DEBUG) && (!((Oid)localObject4).equals(localObject2[0]))) {
/*  548 */             System.out.println("SpNegoContext.acceptSecContext: negotiated mech adjusted to " + localObject2[0]);
/*      */           }
/*      */           
/*  551 */           localObject5 = ((NegTokenInit)localObject1).getMechToken();
/*  552 */           if (localObject5 == null) {
/*  553 */             throw new GSSException(11, -1, "mechToken is missing");
/*      */           }
/*      */           
/*  556 */           arrayOfByte3 = GSS_acceptSecContext((byte[])localObject5);
/*  557 */           localObject4 = localObject2[0];
/*      */         } else {
/*  559 */           arrayOfByte3 = null;
/*      */         }
/*      */         
/*      */ 
/*  563 */         if ((!GSSUtil.useMSInterop()) && (bool)) {
/*  564 */           bool = verifyMechListMIC(this.DER_mechTypes, ((NegTokenInit)localObject1)
/*  565 */             .getMechListMIC());
/*      */         }
/*      */         
/*      */ 
/*  569 */         if (bool) {
/*  570 */           if (isMechContextEstablished()) {
/*  571 */             localNegoResult = SpNegoToken.NegoResult.ACCEPT_COMPLETE;
/*  572 */             this.state = 3;
/*      */             
/*  574 */             setContextFlags();
/*      */             
/*  576 */             if (DEBUG) {
/*  577 */               System.out.println("SPNEGO Negotiated Mechanism = " + this.internal_mech + " " + 
/*      */               
/*  579 */                 GSSUtil.getMechStr(this.internal_mech));
/*      */             }
/*      */           } else {
/*  582 */             localNegoResult = SpNegoToken.NegoResult.ACCEPT_INCOMPLETE;
/*  583 */             this.state = 2;
/*      */           }
/*      */         } else {
/*  586 */           localNegoResult = SpNegoToken.NegoResult.REJECT;
/*  587 */           this.state = 3;
/*      */         }
/*      */         
/*  590 */         if (DEBUG) {
/*  591 */           System.out.println("SpNegoContext.acceptSecContext: mechanism wanted = " + localObject4);
/*      */           
/*  593 */           System.out.println("SpNegoContext.acceptSecContext: negotiated result = " + localNegoResult);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  598 */         Object localObject5 = new NegTokenTarg(localNegoResult.ordinal(), (Oid)localObject4, arrayOfByte3, null);
/*      */         
/*  600 */         if (DEBUG) {
/*  601 */           System.out.println("SpNegoContext.acceptSecContext: sending token of type = " + 
/*      */           
/*  603 */             SpNegoToken.getTokenName(((NegTokenTarg)localObject5).getType()));
/*      */         }
/*      */         
/*  606 */         arrayOfByte1 = ((NegTokenTarg)localObject5).getEncoded();
/*      */       }
/*  608 */       else if (this.state == 2)
/*      */       {
/*  610 */         arrayOfByte2 = new byte[paramInputStream.available()];
/*  611 */         SpNegoToken.readFully(paramInputStream, arrayOfByte2);
/*  612 */         if (DEBUG) {
/*  613 */           System.out.println("SpNegoContext.acceptSecContext: receiving token = " + 
/*      */           
/*  615 */             SpNegoToken.getHexBytes(arrayOfByte2));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  620 */         localObject1 = new NegTokenTarg(arrayOfByte2);
/*      */         
/*  622 */         if (DEBUG) {
/*  623 */           System.out.println("SpNegoContext.acceptSecContext: received token of type = " + 
/*      */           
/*  625 */             SpNegoToken.getTokenName(((NegTokenTarg)localObject1).getType()));
/*      */         }
/*      */         
/*      */ 
/*  629 */         localObject2 = ((NegTokenTarg)localObject1).getResponseToken();
/*  630 */         localObject3 = GSS_acceptSecContext((byte[])localObject2);
/*  631 */         if (localObject3 == null) {
/*  632 */           bool = false;
/*      */         }
/*      */         
/*      */ 
/*  636 */         if (bool) {
/*  637 */           if (isMechContextEstablished()) {
/*  638 */             localNegoResult = SpNegoToken.NegoResult.ACCEPT_COMPLETE;
/*  639 */             this.state = 3;
/*      */           } else {
/*  641 */             localNegoResult = SpNegoToken.NegoResult.ACCEPT_INCOMPLETE;
/*  642 */             this.state = 2;
/*      */           }
/*      */         } else {
/*  645 */           localNegoResult = SpNegoToken.NegoResult.REJECT;
/*  646 */           this.state = 3;
/*      */         }
/*      */         
/*      */ 
/*  650 */         localObject4 = new NegTokenTarg(localNegoResult.ordinal(), null, (byte[])localObject3, null);
/*      */         
/*  652 */         if (DEBUG) {
/*  653 */           System.out.println("SpNegoContext.acceptSecContext: sending token of type = " + 
/*      */           
/*  655 */             SpNegoToken.getTokenName(((NegTokenTarg)localObject4).getType()));
/*      */         }
/*      */         
/*  658 */         arrayOfByte1 = ((NegTokenTarg)localObject4).getEncoded();
/*      */ 
/*      */ 
/*      */       }
/*  662 */       else if (DEBUG) {
/*  663 */         System.out.println("AcceptSecContext: state = " + this.state);
/*      */       }
/*      */       
/*  666 */       if (DEBUG) {
/*  667 */         System.out.println("SpNegoContext.acceptSecContext: sending token = " + 
/*  668 */           SpNegoToken.getHexBytes(arrayOfByte1));
/*      */       }
/*      */     }
/*      */     catch (IOException localIOException) {
/*  672 */       Object localObject1 = new GSSException(11, -1, localIOException.getMessage());
/*  673 */       ((GSSException)localObject1).initCause(localIOException);
/*  674 */       throw ((Throwable)localObject1);
/*      */     }
/*      */     
/*  677 */     if (this.state == 3)
/*      */     {
/*  679 */       setContextFlags();
/*      */     }
/*  681 */     return arrayOfByte1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private Oid[] getAvailableMechs()
/*      */   {
/*  688 */     if (this.myCred != null) {
/*  689 */       Oid[] arrayOfOid = new Oid[1];
/*  690 */       arrayOfOid[0] = this.myCred.getInternalMech();
/*  691 */       return arrayOfOid;
/*      */     }
/*  693 */     return this.factory.availableMechs;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private byte[] getEncodedMechs(Oid[] paramArrayOfOid)
/*      */     throws IOException, GSSException
/*      */   {
/*  703 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/*  704 */     for (int i = 0; i < paramArrayOfOid.length; i++) {
/*  705 */       arrayOfByte = paramArrayOfOid[i].getDER();
/*  706 */       localDerOutputStream1.write(arrayOfByte);
/*      */     }
/*      */     
/*  709 */     DerOutputStream localDerOutputStream2 = new DerOutputStream();
/*  710 */     localDerOutputStream2.write((byte)48, localDerOutputStream1);
/*  711 */     byte[] arrayOfByte = localDerOutputStream2.toByteArray();
/*  712 */     return arrayOfByte;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private BitArray getContextFlags()
/*      */   {
/*  719 */     BitArray localBitArray = new BitArray(7);
/*      */     
/*  721 */     if (getCredDelegState()) localBitArray.set(0, true);
/*  722 */     if (getMutualAuthState()) localBitArray.set(1, true);
/*  723 */     if (getReplayDetState()) localBitArray.set(2, true);
/*  724 */     if (getSequenceDetState()) localBitArray.set(3, true);
/*  725 */     if (getConfState()) localBitArray.set(5, true);
/*  726 */     if (getIntegState()) { localBitArray.set(6, true);
/*      */     }
/*  728 */     return localBitArray;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void setContextFlags()
/*      */   {
/*  736 */     if (this.mechContext != null)
/*      */     {
/*  738 */       if (this.mechContext.getCredDelegState()) {
/*  739 */         this.credDelegState = true;
/*      */       }
/*      */       
/*  742 */       if (!this.mechContext.getMutualAuthState()) {
/*  743 */         this.mutualAuthState = false;
/*      */       }
/*  745 */       if (!this.mechContext.getReplayDetState()) {
/*  746 */         this.replayDetState = false;
/*      */       }
/*  748 */       if (!this.mechContext.getSequenceDetState()) {
/*  749 */         this.sequenceDetState = false;
/*      */       }
/*  751 */       if (!this.mechContext.getIntegState()) {
/*  752 */         this.integState = false;
/*      */       }
/*  754 */       if (!this.mechContext.getConfState()) {
/*  755 */         this.confState = false;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean verifyMechListMIC(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
/*      */     throws GSSException
/*      */   {
/*  809 */     if (paramArrayOfByte2 == null) {
/*  810 */       if (DEBUG) {
/*  811 */         System.out.println("SpNegoContext: no MIC token validation");
/*      */       }
/*  813 */       return true;
/*      */     }
/*      */     
/*      */ 
/*  817 */     if (!this.mechContext.getIntegState()) {
/*  818 */       if (DEBUG) {
/*  819 */         System.out.println("SpNegoContext: no MIC token validation - mechanism does not support integrity");
/*      */       }
/*      */       
/*  822 */       return true;
/*      */     }
/*      */     
/*      */ 
/*  826 */     boolean bool = false;
/*      */     try {
/*  828 */       MessageProp localMessageProp = new MessageProp(0, true);
/*  829 */       verifyMIC(paramArrayOfByte2, 0, paramArrayOfByte2.length, paramArrayOfByte1, 0, paramArrayOfByte1.length, localMessageProp);
/*      */       
/*  831 */       bool = true;
/*      */     } catch (GSSException localGSSException) {
/*  833 */       bool = false;
/*  834 */       if (DEBUG) {
/*  835 */         System.out.println("SpNegoContext: MIC validation failed! " + localGSSException
/*  836 */           .getMessage());
/*      */       }
/*      */     }
/*  839 */     return bool;
/*      */   }
/*      */   
/*      */ 
/*      */   private byte[] GSS_initSecContext(byte[] paramArrayOfByte)
/*      */     throws GSSException
/*      */   {
/*  846 */     byte[] arrayOfByte = null;
/*      */     
/*  848 */     if (this.mechContext == null)
/*      */     {
/*      */ 
/*  851 */       localObject = this.factory.manager.createName(this.peerName.toString(), this.peerName
/*  852 */         .getStringNameType(), this.internal_mech);
/*  853 */       GSSCredentialImpl localGSSCredentialImpl = null;
/*  854 */       if (this.myCred != null)
/*      */       {
/*      */ 
/*  857 */         localGSSCredentialImpl = new GSSCredentialImpl(this.factory.manager, this.myCred.getInternalCred());
/*      */       }
/*      */       
/*  860 */       this.mechContext = this.factory.manager.createContext((GSSName)localObject, this.internal_mech, localGSSCredentialImpl, 0);
/*      */       
/*  862 */       this.mechContext.requestConf(this.confState);
/*  863 */       this.mechContext.requestInteg(this.integState);
/*  864 */       this.mechContext.requestCredDeleg(this.credDelegState);
/*  865 */       this.mechContext.requestMutualAuth(this.mutualAuthState);
/*  866 */       this.mechContext.requestReplayDet(this.replayDetState);
/*  867 */       this.mechContext.requestSequenceDet(this.sequenceDetState);
/*  868 */       if ((this.mechContext instanceof ExtendedGSSContext)) {
/*  869 */         ((ExtendedGSSContext)this.mechContext).requestDelegPolicy(this.delegPolicyState);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  875 */     if (paramArrayOfByte != null) {
/*  876 */       arrayOfByte = paramArrayOfByte;
/*      */     } else {
/*  878 */       arrayOfByte = new byte[0];
/*      */     }
/*      */     
/*      */ 
/*  882 */     Object localObject = this.mechContext.initSecContext(arrayOfByte, 0, arrayOfByte.length);
/*      */     
/*  884 */     return (byte[])localObject;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private byte[] GSS_acceptSecContext(byte[] paramArrayOfByte)
/*      */     throws GSSException
/*      */   {
/*  892 */     if (this.mechContext == null)
/*      */     {
/*  894 */       localObject = null;
/*  895 */       if (this.myCred != null)
/*      */       {
/*      */ 
/*  898 */         localObject = new GSSCredentialImpl(this.factory.manager, this.myCred.getInternalCred());
/*      */       }
/*      */       
/*  901 */       this.mechContext = this.factory.manager.createContext((GSSCredential)localObject);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  906 */     Object localObject = this.mechContext.acceptSecContext(paramArrayOfByte, 0, paramArrayOfByte.length);
/*      */     
/*  908 */     return (byte[])localObject;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static Oid negotiate_mech_type(Oid[] paramArrayOfOid1, Oid[] paramArrayOfOid2)
/*      */   {
/*  921 */     for (int i = 0; i < paramArrayOfOid1.length; i++) {
/*  922 */       for (int j = 0; j < paramArrayOfOid2.length; j++) {
/*  923 */         if (paramArrayOfOid2[j].equals(paramArrayOfOid1[i])) {
/*  924 */           if (DEBUG) {
/*  925 */             System.out.println("SpNegoContext: negotiated mechanism = " + paramArrayOfOid2[j]);
/*      */           }
/*      */           
/*  928 */           return paramArrayOfOid2[j];
/*      */         }
/*      */       }
/*      */     }
/*  932 */     return null;
/*      */   }
/*      */   
/*      */   public final boolean isEstablished() {
/*  936 */     return this.state == 3;
/*      */   }
/*      */   
/*      */   public final boolean isMechContextEstablished() {
/*  940 */     if (this.mechContext != null) {
/*  941 */       return this.mechContext.isEstablished();
/*      */     }
/*  943 */     if (DEBUG) {
/*  944 */       System.out.println("The underlying mechanism context has not been initialized");
/*      */     }
/*      */     
/*  947 */     return false;
/*      */   }
/*      */   
/*      */   public final byte[] export() throws GSSException
/*      */   {
/*  952 */     throw new GSSException(16, -1, "GSS Export Context not available");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final void setChannelBinding(ChannelBinding paramChannelBinding)
/*      */     throws GSSException
/*      */   {
/*  962 */     this.channelBinding = paramChannelBinding;
/*      */   }
/*      */   
/*      */   final ChannelBinding getChannelBinding() {
/*  966 */     return this.channelBinding;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final void requestAnonymity(boolean paramBoolean)
/*      */     throws GSSException
/*      */   {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final boolean getAnonymityState()
/*      */   {
/*  987 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void requestLifetime(int paramInt)
/*      */     throws GSSException
/*      */   {
/*  995 */     if ((this.state == 1) && (isInitiator())) {
/*  996 */       this.lifetime = paramInt;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public final int getLifetime()
/*      */   {
/* 1003 */     if (this.mechContext != null) {
/* 1004 */       return this.mechContext.getLifetime();
/*      */     }
/* 1006 */     return Integer.MAX_VALUE;
/*      */   }
/*      */   
/*      */   public final boolean isTransferable() throws GSSException
/*      */   {
/* 1011 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public final void requestSequenceDet(boolean paramBoolean)
/*      */     throws GSSException
/*      */   {
/* 1019 */     if ((this.state == 1) && (isInitiator())) {
/* 1020 */       this.sequenceDetState = paramBoolean;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public final boolean getSequenceDetState()
/*      */   {
/* 1028 */     return (this.sequenceDetState) || (this.replayDetState);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public final void requestReplayDet(boolean paramBoolean)
/*      */     throws GSSException
/*      */   {
/* 1036 */     if ((this.state == 1) && (isInitiator())) {
/* 1037 */       this.replayDetState = paramBoolean;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public final boolean getReplayDetState()
/*      */   {
/* 1045 */     return (this.replayDetState) || (this.sequenceDetState);
/*      */   }
/*      */   
/*      */   public final GSSNameSpi getTargName()
/*      */     throws GSSException
/*      */   {
/* 1051 */     if (this.mechContext != null) {
/* 1052 */       GSSNameImpl localGSSNameImpl = (GSSNameImpl)this.mechContext.getTargName();
/* 1053 */       this.peerName = localGSSNameImpl.getElement(this.internal_mech);
/* 1054 */       return this.peerName;
/*      */     }
/* 1056 */     if (DEBUG) {
/* 1057 */       System.out.println("The underlying mechanism context has not been initialized");
/*      */     }
/*      */     
/* 1060 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public final GSSNameSpi getSrcName()
/*      */     throws GSSException
/*      */   {
/* 1067 */     if (this.mechContext != null) {
/* 1068 */       GSSNameImpl localGSSNameImpl = (GSSNameImpl)this.mechContext.getSrcName();
/* 1069 */       this.myName = localGSSNameImpl.getElement(this.internal_mech);
/* 1070 */       return this.myName;
/*      */     }
/* 1072 */     if (DEBUG) {
/* 1073 */       System.out.println("The underlying mechanism context has not been initialized");
/*      */     }
/*      */     
/* 1076 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final GSSCredentialSpi getDelegCred()
/*      */     throws GSSException
/*      */   {
/* 1091 */     if ((this.state != 2) && (this.state != 3))
/* 1092 */       throw new GSSException(12);
/* 1093 */     if (this.mechContext != null)
/*      */     {
/* 1095 */       GSSCredentialImpl localGSSCredentialImpl = (GSSCredentialImpl)this.mechContext.getDelegCred();
/* 1096 */       if (localGSSCredentialImpl == null) {
/* 1097 */         return null;
/*      */       }
/*      */       
/* 1100 */       boolean bool = false;
/* 1101 */       if (localGSSCredentialImpl.getUsage() == 1) {
/* 1102 */         bool = true;
/*      */       }
/*      */       
/* 1105 */       GSSCredentialSpi localGSSCredentialSpi = localGSSCredentialImpl.getElement(this.internal_mech, bool);
/* 1106 */       SpNegoCredElement localSpNegoCredElement = new SpNegoCredElement(localGSSCredentialSpi);
/* 1107 */       return localSpNegoCredElement.getInternalCred();
/*      */     }
/* 1109 */     throw new GSSException(12, -1, "getDelegCred called in invalid state!");
/*      */   }
/*      */   
/*      */ 
/*      */   public final int getWrapSizeLimit(int paramInt1, boolean paramBoolean, int paramInt2)
/*      */     throws GSSException
/*      */   {
/* 1116 */     if (this.mechContext != null) {
/* 1117 */       return this.mechContext.getWrapSizeLimit(paramInt1, paramBoolean, paramInt2);
/*      */     }
/* 1119 */     throw new GSSException(12, -1, "getWrapSizeLimit called in invalid state!");
/*      */   }
/*      */   
/*      */ 
/*      */   public final byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp)
/*      */     throws GSSException
/*      */   {
/* 1126 */     if (this.mechContext != null) {
/* 1127 */       return this.mechContext.wrap(paramArrayOfByte, paramInt1, paramInt2, paramMessageProp);
/*      */     }
/* 1129 */     throw new GSSException(12, -1, "Wrap called in invalid state!");
/*      */   }
/*      */   
/*      */ 
/*      */   public final void wrap(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp)
/*      */     throws GSSException
/*      */   {
/* 1136 */     if (this.mechContext != null) {
/* 1137 */       this.mechContext.wrap(paramInputStream, paramOutputStream, paramMessageProp);
/*      */     } else {
/* 1139 */       throw new GSSException(12, -1, "Wrap called in invalid state!");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public final byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp)
/*      */     throws GSSException
/*      */   {
/* 1147 */     if (this.mechContext != null) {
/* 1148 */       return this.mechContext.unwrap(paramArrayOfByte, paramInt1, paramInt2, paramMessageProp);
/*      */     }
/* 1150 */     throw new GSSException(12, -1, "UnWrap called in invalid state!");
/*      */   }
/*      */   
/*      */ 
/*      */   public final void unwrap(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp)
/*      */     throws GSSException
/*      */   {
/* 1157 */     if (this.mechContext != null) {
/* 1158 */       this.mechContext.unwrap(paramInputStream, paramOutputStream, paramMessageProp);
/*      */     } else {
/* 1160 */       throw new GSSException(12, -1, "UnWrap called in invalid state!");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public final byte[] getMIC(byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp)
/*      */     throws GSSException
/*      */   {
/* 1168 */     if (this.mechContext != null) {
/* 1169 */       return this.mechContext.getMIC(paramArrayOfByte, paramInt1, paramInt2, paramMessageProp);
/*      */     }
/* 1171 */     throw new GSSException(12, -1, "getMIC called in invalid state!");
/*      */   }
/*      */   
/*      */ 
/*      */   public final void getMIC(InputStream paramInputStream, OutputStream paramOutputStream, MessageProp paramMessageProp)
/*      */     throws GSSException
/*      */   {
/* 1178 */     if (this.mechContext != null) {
/* 1179 */       this.mechContext.getMIC(paramInputStream, paramOutputStream, paramMessageProp);
/*      */     } else {
/* 1181 */       throw new GSSException(12, -1, "getMIC called in invalid state!");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public final void verifyMIC(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4, MessageProp paramMessageProp)
/*      */     throws GSSException
/*      */   {
/* 1190 */     if (this.mechContext != null) {
/* 1191 */       this.mechContext.verifyMIC(paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2, paramInt3, paramInt4, paramMessageProp);
/*      */     }
/*      */     else {
/* 1194 */       throw new GSSException(12, -1, "verifyMIC called in invalid state!");
/*      */     }
/*      */   }
/*      */   
/*      */   public final void verifyMIC(InputStream paramInputStream1, InputStream paramInputStream2, MessageProp paramMessageProp)
/*      */     throws GSSException
/*      */   {
/* 1201 */     if (this.mechContext != null) {
/* 1202 */       this.mechContext.verifyMIC(paramInputStream1, paramInputStream2, paramMessageProp);
/*      */     } else {
/* 1204 */       throw new GSSException(12, -1, "verifyMIC called in invalid state!");
/*      */     }
/*      */   }
/*      */   
/*      */   private static String printState(int paramInt)
/*      */   {
/* 1210 */     switch (paramInt) {
/*      */     case 1: 
/* 1212 */       return "STATE_NEW";
/*      */     case 2: 
/* 1214 */       return "STATE_IN_PROCESS";
/*      */     case 3: 
/* 1216 */       return "STATE_DONE";
/*      */     case 4: 
/* 1218 */       return "STATE_DELETED";
/*      */     }
/* 1220 */     return "Unknown state " + paramInt;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object inquireSecContext(InquireType paramInquireType)
/*      */     throws GSSException
/*      */   {
/* 1229 */     if (this.mechContext == null) {
/* 1230 */       throw new GSSException(12, -1, "Underlying mech not established.");
/*      */     }
/*      */     
/* 1233 */     if ((this.mechContext instanceof ExtendedGSSContext)) {
/* 1234 */       return ((ExtendedGSSContext)this.mechContext).inquireSecContext(paramInquireType);
/*      */     }
/* 1236 */     throw new GSSException(2, -1, "inquireSecContext not supported by underlying mech.");
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\security\jgss\spnego\SpNegoContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */