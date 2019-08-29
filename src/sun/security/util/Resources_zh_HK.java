/*     */ package sun.security.util;
/*     */ 
/*     */ import java.util.ListResourceBundle;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Resources_zh_HK
/*     */   extends ListResourceBundle
/*     */ {
/*  35 */   private static final Object[][] contents = { { "invalid.null.input.s.", "無效空值輸入" }, { "actions.can.only.be.read.", "動作只能被「讀取」" }, { "permission.name.name.syntax.invalid.", "權限名稱 [{0}] 是無效的語法: " }, { "Credential.Class.not.followed.by.a.Principal.Class.and.Name", "Credential 類別後面不是 Principal 類別及名稱" }, { "Principal.Class.not.followed.by.a.Principal.Name", "Principal 類別後面不是 Principal 名稱" }, { "Principal.Name.must.be.surrounded.by.quotes", "Principal 名稱必須以引號圈住" }, { "Principal.Name.missing.end.quote", "Principal 名稱缺少下引號" }, { "PrivateCredentialPermission.Principal.Class.can.not.be.a.wildcard.value.if.Principal.Name.is.not.a.wildcard.value", "如果 Principal 名稱不是一個萬用字元 (*) 值，那麼 PrivateCredentialPermission Principal 類別就不能是萬用字元 (*) 值" }, { "CredOwner.Principal.Class.class.Principal.Name.name", "CredOwner:\n\tPrincipal 類別 = {0}\n\tPrincipal 名稱 = {1}" }, { "provided.null.name", "提供空值名稱" }, { "provided.null.keyword.map", "提供空值關鍵字對映" }, { "provided.null.OID.map", "提供空值 OID 對映" }, { "NEWLINE", "\n" }, { "invalid.null.AccessControlContext.provided", "提供無效的空值 AccessControlContext" }, { "invalid.null.action.provided", "提供無效的空值動作" }, { "invalid.null.Class.provided", "提供無效的空值類別" }, { "Subject.", "主題:\n" }, { ".Principal.", "\tPrincipal: " }, { ".Public.Credential.", "\t公用證明資料: " }, { ".Private.Credentials.inaccessible.", "\t私人證明資料無法存取\n" }, { ".Private.Credential.", "\t私人證明資料: " }, { ".Private.Credential.inaccessible.", "\t私人證明資料無法存取\n" }, { "Subject.is.read.only", "主題為唯讀" }, { "attempting.to.add.an.object.which.is.not.an.instance.of.java.security.Principal.to.a.Subject.s.Principal.Set", "試圖新增一個非 java.security.Principal 執行處理的物件至主題的 Principal 群中" }, { "attempting.to.add.an.object.which.is.not.an.instance.of.class", "試圖新增一個非 {0} 執行處理的物件" }, { "LoginModuleControlFlag.", "LoginModuleControlFlag: " }, { "Invalid.null.input.name", "無效空值輸入: 名稱" }, { "No.LoginModules.configured.for.name", "無針對 {0} 設定的 LoginModules" }, { "invalid.null.Subject.provided", "提供無效空值主題" }, { "invalid.null.CallbackHandler.provided", "提供無效空值 CallbackHandler" }, { "null.subject.logout.called.before.login", "空值主題 - 在登入之前即呼叫登出" }, { "unable.to.instantiate.LoginModule.module.because.it.does.not.provide.a.no.argument.constructor", "無法創設 LoginModule，{0}，因為它並未提供非引數的建構子" }, { "unable.to.instantiate.LoginModule", "無法建立 LoginModule" }, { "unable.to.instantiate.LoginModule.", "無法建立 LoginModule: " }, { "unable.to.find.LoginModule.class.", "找不到 LoginModule 類別: " }, { "unable.to.access.LoginModule.", "無法存取 LoginModule: " }, { "Login.Failure.all.modules.ignored", "登入失敗: 忽略所有模組" }, { "java.security.policy.error.parsing.policy.message", "java.security.policy: 剖析錯誤 {0}: \n\t{1}" }, { "java.security.policy.error.adding.Permission.perm.message", "java.security.policy: 新增權限錯誤 {0}: \n\t{1}" }, { "java.security.policy.error.adding.Entry.message", "java.security.policy: 新增項目錯誤: \n\t{0}" }, { "alias.name.not.provided.pe.name.", "未提供別名名稱 ({0})" }, { "unable.to.perform.substitution.on.alias.suffix", "無法對別名執行替換，{0}" }, { "substitution.value.prefix.unsupported", "不支援的替換值，{0}" }, { "LPARAM", "(" }, { "RPARAM", ")" }, { "type.can.t.be.null", "輸入不能為空值" }, { "keystorePasswordURL.can.not.be.specified.without.also.specifying.keystore", "指定 keystorePasswordURL 需要同時指定金鑰儲存庫" }, { "expected.keystore.type", "預期的金鑰儲存庫類型" }, { "expected.keystore.provider", "預期的金鑰儲存庫提供者" }, { "multiple.Codebase.expressions", "多重 Codebase 表示式" }, { "multiple.SignedBy.expressions", "多重 SignedBy 表示式" }, { "duplicate.keystore.domain.name", "重複的金鑰儲存庫網域名稱: {0}" }, { "duplicate.keystore.name", "重複的金鑰儲存庫名稱: {0}" }, { "SignedBy.has.empty.alias", "SignedBy 有空別名" }, { "can.not.specify.Principal.with.a.wildcard.class.without.a.wildcard.name", "沒有萬用字元名稱，無法指定含有萬用字元類別的 Principal" }, { "expected.codeBase.or.SignedBy.or.Principal", "預期的 codeBase 或 SignedBy 或 Principal" }, { "expected.permission.entry", "預期的權限項目" }, { "number.", "號碼 " }, { "expected.expect.read.end.of.file.", "預期的 [{0}], 讀取 [end of file]" }, { "expected.read.end.of.file.", "預期的 [;], 讀取 [end of file]" }, { "line.number.msg", "行 {0}: {1}" }, { "line.number.expected.expect.found.actual.", "行 {0}: 預期的 [{1}]，發現 [{2}]" }, { "null.principalClass.or.principalName", "空值 principalClass 或 principalName" }, { "PKCS11.Token.providerName.Password.", "PKCS11 記號 [{0}] 密碼: " }, { "unable.to.instantiate.Subject.based.policy", "無法建立主題式的原則" } };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object[][] getContents()
/*     */   {
/* 169 */     return contents;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\util\Resources_zh_HK.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */