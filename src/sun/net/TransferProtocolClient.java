/*     */ package sun.net;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Vector;
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
/*     */ public class TransferProtocolClient
/*     */   extends NetworkClient
/*     */ {
/*     */   static final boolean debug = false;
/*  45 */   protected Vector<String> serverResponse = new Vector(1);
/*     */   
/*     */ 
/*     */ 
/*     */   protected int lastReplyCode;
/*     */   
/*     */ 
/*     */ 
/*     */   public int readServerResponse()
/*     */     throws IOException
/*     */   {
/*  56 */     StringBuffer localStringBuffer = new StringBuffer(32);
/*     */     
/*  58 */     int j = -1;
/*     */     
/*     */ 
/*     */ 
/*  62 */     this.serverResponse.setSize(0);
/*     */     int k;
/*  64 */     for (;;) { int i; if ((i = this.serverInput.read()) != -1) {
/*  65 */         if ((i == 13) && 
/*  66 */           ((i = this.serverInput.read()) != 10)) {
/*  67 */           localStringBuffer.append('\r');
/*     */         }
/*  69 */         localStringBuffer.append((char)i);
/*  70 */         if (i != 10)
/*     */           continue;
/*     */       }
/*  73 */       String str = localStringBuffer.toString();
/*  74 */       localStringBuffer.setLength(0);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*  79 */       if (str.length() == 0) {
/*  80 */         k = -1;
/*     */       } else {
/*     */         try {
/*  83 */           k = Integer.parseInt(str.substring(0, 3));
/*     */         } catch (NumberFormatException localNumberFormatException) {
/*  85 */           k = -1;
/*     */         }
/*     */         catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException) {}
/*     */         
/*  89 */         continue;
/*     */       }
/*     */       
/*  92 */       this.serverResponse.addElement(str);
/*  93 */       if (j != -1)
/*     */       {
/*  95 */         if ((k == j) && (
/*  96 */           (str.length() < 4) || (str.charAt(3) != '-')))
/*     */         {
/*     */ 
/*     */ 
/* 100 */           j = -1;
/* 101 */           break;
/*     */         }
/* 103 */       } else { if ((str.length() < 4) || (str.charAt(3) != '-')) break;
/* 104 */         j = k;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 111 */     return this.lastReplyCode = k;
/*     */   }
/*     */   
/*     */   public void sendServer(String paramString)
/*     */   {
/* 116 */     this.serverOutput.print(paramString);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getResponseString()
/*     */   {
/* 124 */     return (String)this.serverResponse.elementAt(0);
/*     */   }
/*     */   
/*     */   public Vector<String> getResponseStrings()
/*     */   {
/* 129 */     return this.serverResponse;
/*     */   }
/*     */   
/*     */   public TransferProtocolClient(String paramString, int paramInt) throws IOException
/*     */   {
/* 134 */     super(paramString, paramInt);
/*     */   }
/*     */   
/*     */   public TransferProtocolClient() {}
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\TransferProtocolClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */