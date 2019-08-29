/*     */ package sun.rmi.transport.proxy;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.Socket;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class CGIForwardCommand
/*     */   implements CGICommandHandler
/*     */ {
/*     */   public String getName()
/*     */   {
/* 222 */     return "forward";
/*     */   }
/*     */   
/*     */   private String getLine(DataInputStream paramDataInputStream) throws IOException
/*     */   {
/* 227 */     return paramDataInputStream.readLine();
/*     */   }
/*     */   
/*     */   public void execute(String paramString) throws CGIClientException, CGIServerException
/*     */   {
/* 232 */     if (!CGIHandler.RequestMethod.equals("POST")) {
/* 233 */       throw new CGIClientException("can only forward POST requests");
/*     */     }
/*     */     int i;
/*     */     try {
/* 237 */       i = Integer.parseInt(paramString);
/*     */     } catch (NumberFormatException localNumberFormatException) {
/* 239 */       throw new CGIClientException("invalid port number.", localNumberFormatException);
/*     */     }
/* 241 */     if ((i <= 0) || (i > 65535))
/* 242 */       throw new CGIClientException("invalid port: " + i);
/* 243 */     if (i < 1024) {
/* 244 */       throw new CGIClientException("permission denied for port: " + i);
/*     */     }
/*     */     
/*     */     Socket localSocket;
/*     */     try
/*     */     {
/* 250 */       localSocket = new Socket(InetAddress.getLocalHost(), i);
/*     */     } catch (IOException localIOException1) {
/* 252 */       throw new CGIServerException("could not connect to local port", localIOException1);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 258 */     DataInputStream localDataInputStream1 = new DataInputStream(System.in);
/* 259 */     byte[] arrayOfByte = new byte[CGIHandler.ContentLength];
/*     */     try {
/* 261 */       localDataInputStream1.readFully(arrayOfByte);
/*     */     } catch (EOFException localEOFException1) {
/* 263 */       throw new CGIClientException("unexpected EOF reading request body", localEOFException1);
/*     */     } catch (IOException localIOException2) {
/* 265 */       throw new CGIClientException("error reading request body", localIOException2);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 273 */       DataOutputStream localDataOutputStream = new DataOutputStream(localSocket.getOutputStream());
/* 274 */       localDataOutputStream.writeBytes("POST / HTTP/1.0\r\n");
/* 275 */       localDataOutputStream.writeBytes("Content-length: " + CGIHandler.ContentLength + "\r\n\r\n");
/*     */       
/* 277 */       localDataOutputStream.write(arrayOfByte);
/* 278 */       localDataOutputStream.flush();
/*     */     } catch (IOException localIOException3) {
/* 280 */       throw new CGIServerException("error writing to server", localIOException3);
/*     */     }
/*     */     
/*     */ 
/*     */     DataInputStream localDataInputStream2;
/*     */     
/*     */     try
/*     */     {
/* 288 */       localDataInputStream2 = new DataInputStream(localSocket.getInputStream());
/*     */     } catch (IOException localIOException4) {
/* 290 */       throw new CGIServerException("error reading from server", localIOException4);
/*     */     }
/* 292 */     String str1 = "Content-length:".toLowerCase();
/* 293 */     int j = 0;
/*     */     
/* 295 */     int k = -1;
/*     */     String str2;
/*     */     do {
/* 298 */       try { str2 = getLine(localDataInputStream2);
/*     */       } catch (IOException localIOException5) {
/* 300 */         throw new CGIServerException("error reading from server", localIOException5);
/*     */       }
/* 302 */       if (str2 == null) {
/* 303 */         throw new CGIServerException("unexpected EOF reading server response");
/*     */       }
/*     */       
/* 306 */       if (str2.toLowerCase().startsWith(str1)) {
/* 307 */         if (j != 0) {
/* 308 */           throw new CGIServerException("Multiple Content-length entries found.");
/*     */         }
/*     */         
/*     */ 
/* 312 */         k = Integer.parseInt(str2.substring(str1.length()).trim());
/* 313 */         j = 1;
/*     */       }
/*     */       
/* 316 */     } while ((str2.length() != 0) && 
/* 317 */       (str2.charAt(0) != '\r') && (str2.charAt(0) != '\n'));
/*     */     
/* 319 */     if ((j == 0) || (k < 0)) {
/* 320 */       throw new CGIServerException("missing or invalid content length in server response");
/*     */     }
/* 322 */     arrayOfByte = new byte[k];
/*     */     try {
/* 324 */       localDataInputStream2.readFully(arrayOfByte);
/*     */     } catch (EOFException localEOFException2) {
/* 326 */       throw new CGIServerException("unexpected EOF reading server response", localEOFException2);
/*     */     }
/*     */     catch (IOException localIOException6) {
/* 329 */       throw new CGIServerException("error reading from server", localIOException6);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 335 */     System.out.println("Status: 200 OK");
/* 336 */     System.out.println("Content-type: application/octet-stream");
/* 337 */     System.out.println("");
/*     */     try {
/* 339 */       System.out.write(arrayOfByte);
/*     */     } catch (IOException localIOException7) {
/* 341 */       throw new CGIServerException("error writing response", localIOException7);
/*     */     }
/* 343 */     System.out.flush();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\rmi\transport\proxy\CGIForwardCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */