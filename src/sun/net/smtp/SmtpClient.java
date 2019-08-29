/*     */ package sun.net.smtp;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.InetAddress;
/*     */ import java.security.AccessController;
/*     */ import sun.net.TransferProtocolClient;
/*     */ import sun.security.action.GetPropertyAction;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SmtpClient
/*     */   extends TransferProtocolClient
/*     */ {
/*  46 */   private static int DEFAULT_SMTP_PORT = 25;
/*     */   
/*     */   String mailhost;
/*     */   SmtpPrintStream message;
/*     */   
/*     */   public void closeServer()
/*     */     throws IOException
/*     */   {
/*  54 */     if (serverIsOpen()) {
/*  55 */       closeMessage();
/*  56 */       issueCommand("QUIT\r\n", 221);
/*  57 */       super.closeServer();
/*     */     }
/*     */   }
/*     */   
/*     */   void issueCommand(String paramString, int paramInt) throws IOException {
/*  62 */     sendServer(paramString);
/*     */     int i;
/*  64 */     while ((i = readServerResponse()) != paramInt) {
/*  65 */       if (i != 220)
/*  66 */         throw new SmtpProtocolException(getResponseString());
/*     */     }
/*     */   }
/*     */   
/*     */   private void toCanonical(String paramString) throws IOException {
/*  71 */     if (paramString.startsWith("<")) {
/*  72 */       issueCommand("rcpt to: " + paramString + "\r\n", 250);
/*     */     } else
/*  74 */       issueCommand("rcpt to: <" + paramString + ">\r\n", 250);
/*     */   }
/*     */   
/*     */   public void to(String paramString) throws IOException {
/*  78 */     if (paramString.indexOf('\n') != -1) {
/*  79 */       throw new IOException("Illegal SMTP command", new IllegalArgumentException("Illegal carriage return"));
/*     */     }
/*     */     
/*  82 */     int i = 0;
/*  83 */     int j = paramString.length();
/*  84 */     int k = 0;
/*  85 */     int m = 0;
/*  86 */     int n = 0;
/*  87 */     int i1 = 0;
/*  88 */     while (k < j) {
/*  89 */       int i2 = paramString.charAt(k);
/*  90 */       if (n > 0) {
/*  91 */         if (i2 == 40) {
/*  92 */           n++;
/*  93 */         } else if (i2 == 41)
/*  94 */           n--;
/*  95 */         if (n == 0)
/*  96 */           if (m > i) {
/*  97 */             i1 = 1;
/*     */           } else
/*  99 */             i = k + 1;
/* 100 */       } else if (i2 == 40) {
/* 101 */         n++;
/* 102 */       } else if (i2 == 60) {
/* 103 */         i = m = k + 1;
/* 104 */       } else if (i2 == 62) {
/* 105 */         i1 = 1;
/* 106 */       } else if (i2 == 44) {
/* 107 */         if (m > i)
/* 108 */           toCanonical(paramString.substring(i, m));
/* 109 */         i = k + 1;
/* 110 */         i1 = 0;
/*     */       }
/* 112 */       else if ((i2 > 32) && (i1 == 0)) {
/* 113 */         m = k + 1;
/* 114 */       } else if (i == k) {
/* 115 */         i++;
/*     */       }
/* 117 */       k++;
/*     */     }
/* 119 */     if (m > i)
/* 120 */       toCanonical(paramString.substring(i, m));
/*     */   }
/*     */   
/*     */   public void from(String paramString) throws IOException {
/* 124 */     if (paramString.indexOf('\n') != -1) {
/* 125 */       throw new IOException("Illegal SMTP command", new IllegalArgumentException("Illegal carriage return"));
/*     */     }
/*     */     
/* 128 */     if (paramString.startsWith("<")) {
/* 129 */       issueCommand("mail from: " + paramString + "\r\n", 250);
/*     */     } else {
/* 131 */       issueCommand("mail from: <" + paramString + ">\r\n", 250);
/*     */     }
/*     */   }
/*     */   
/*     */   private void openServer(String paramString) throws IOException
/*     */   {
/* 137 */     this.mailhost = paramString;
/* 138 */     openServer(this.mailhost, DEFAULT_SMTP_PORT);
/* 139 */     issueCommand("helo " + InetAddress.getLocalHost().getHostName() + "\r\n", 250);
/*     */   }
/*     */   
/*     */   public PrintStream startMessage() throws IOException {
/* 143 */     issueCommand("data\r\n", 354);
/*     */     try {
/* 145 */       this.message = new SmtpPrintStream(this.serverOutput, this);
/*     */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/* 147 */       throw new InternalError(encoding + " encoding not found", localUnsupportedEncodingException);
/*     */     }
/* 149 */     return this.message;
/*     */   }
/*     */   
/*     */   void closeMessage() throws IOException {
/* 153 */     if (this.message != null) {
/* 154 */       this.message.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public SmtpClient(String paramString) throws IOException
/*     */   {
/* 160 */     if (paramString != null) {
/*     */       try {
/* 162 */         openServer(paramString);
/* 163 */         this.mailhost = paramString;
/* 164 */         return;
/*     */       }
/*     */       catch (Exception localException1) {}
/*     */     }
/*     */     try
/*     */     {
/* 170 */       this.mailhost = ((String)AccessController.doPrivileged(new GetPropertyAction("mail.host")));
/*     */       
/* 172 */       if (this.mailhost != null) {
/* 173 */         openServer(this.mailhost);
/* 174 */         return;
/*     */       }
/*     */     }
/*     */     catch (Exception localException2) {}
/*     */     try {
/* 179 */       this.mailhost = "localhost";
/* 180 */       openServer(this.mailhost);
/*     */     } catch (Exception localException3) {
/* 182 */       this.mailhost = "mailhost";
/* 183 */       openServer(this.mailhost);
/*     */     }
/*     */   }
/*     */   
/*     */   public SmtpClient() throws IOException
/*     */   {
/* 189 */     this(null);
/*     */   }
/*     */   
/*     */   public SmtpClient(int paramInt) throws IOException
/*     */   {
/* 194 */     setConnectTimeout(paramInt);
/*     */     try
/*     */     {
/* 197 */       this.mailhost = ((String)AccessController.doPrivileged(new GetPropertyAction("mail.host")));
/*     */       
/* 199 */       if (this.mailhost != null) {
/* 200 */         openServer(this.mailhost);
/* 201 */         return;
/*     */       }
/*     */     }
/*     */     catch (Exception localException1) {}
/*     */     try {
/* 206 */       this.mailhost = "localhost";
/* 207 */       openServer(this.mailhost);
/*     */     } catch (Exception localException2) {
/* 209 */       this.mailhost = "mailhost";
/* 210 */       openServer(this.mailhost);
/*     */     }
/*     */   }
/*     */   
/*     */   public String getMailHost() {
/* 215 */     return this.mailhost;
/*     */   }
/*     */   
/*     */   String getEncoding() {
/* 219 */     return encoding;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\smtp\SmtpClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */