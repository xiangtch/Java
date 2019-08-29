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
/*     */ public class Resources_ja
/*     */   extends ListResourceBundle
/*     */ {
/*  35 */   private static final Object[][] contents = { { "invalid.null.input.s.", "nullの入力は無効です" }, { "actions.can.only.be.read.", "アクションは'読込み'のみ可能です" }, { "permission.name.name.syntax.invalid.", "アクセス権名[{0}]の構文が無効です: " }, { "Credential.Class.not.followed.by.a.Principal.Class.and.Name", "Credentialクラスの次にPrincipalクラスおよび名前がありません" }, { "Principal.Class.not.followed.by.a.Principal.Name", "Principalクラスの次にプリンシパル名がありません" }, { "Principal.Name.must.be.surrounded.by.quotes", "プリンシパル名は引用符で囲む必要があります" }, { "Principal.Name.missing.end.quote", "プリンシパル名の最後に引用符がありません" }, { "PrivateCredentialPermission.Principal.Class.can.not.be.a.wildcard.value.if.Principal.Name.is.not.a.wildcard.value", "プリンシパル名がワイルドカード(*)値でない場合、PrivateCredentialPermissionのPrincipalクラスをワイルドカード(*)値にすることはできません" }, { "CredOwner.Principal.Class.class.Principal.Name.name", "CredOwner:\n\tPrincipalクラス={0}\n\tプリンシパル名={1}" }, { "provided.null.name", "nullの名前が指定されました" }, { "provided.null.keyword.map", "nullのキーワード・マップが指定されました" }, { "provided.null.OID.map", "nullのOIDマップが指定されました" }, { "NEWLINE", "\n" }, { "invalid.null.AccessControlContext.provided", "無効なnull AccessControlContextが指定されました" }, { "invalid.null.action.provided", "無効なnullアクションが指定されました" }, { "invalid.null.Class.provided", "無効なnullクラスが指定されました" }, { "Subject.", "サブジェクト:\n" }, { ".Principal.", "\tプリンシパル: " }, { ".Public.Credential.", "\t公開資格: " }, { ".Private.Credentials.inaccessible.", "\t非公開資格にはアクセスできません\n" }, { ".Private.Credential.", "\t非公開資格: " }, { ".Private.Credential.inaccessible.", "\t非公開資格にはアクセスできません\n" }, { "Subject.is.read.only", "サブジェクトは読取り専用です" }, { "attempting.to.add.an.object.which.is.not.an.instance.of.java.security.Principal.to.a.Subject.s.Principal.Set", "java.security.Principalのインスタンスではないオブジェクトを、サブジェクトのプリンシパル・セットに追加しようとしました" }, { "attempting.to.add.an.object.which.is.not.an.instance.of.class", "{0}のインスタンスではないオブジェクトを追加しようとしました" }, { "LoginModuleControlFlag.", "LoginModuleControlFlag: " }, { "Invalid.null.input.name", "無効なnull入力: 名前" }, { "No.LoginModules.configured.for.name", "{0}用に構成されたLoginModulesはありません" }, { "invalid.null.Subject.provided", "無効なnullサブジェクトが指定されました" }, { "invalid.null.CallbackHandler.provided", "無効なnull CallbackHandlerが指定されました" }, { "null.subject.logout.called.before.login", "nullサブジェクト - ログインする前にログアウトが呼び出されました" }, { "unable.to.instantiate.LoginModule.module.because.it.does.not.provide.a.no.argument.constructor", "LoginModule {0}は引数を取らないコンストラクタを指定できないため、インスタンスを生成できません" }, { "unable.to.instantiate.LoginModule", "LoginModuleのインスタンスを生成できません" }, { "unable.to.instantiate.LoginModule.", "LoginModuleのインスタンスを生成できません: " }, { "unable.to.find.LoginModule.class.", "LoginModuleクラスを検出できません: " }, { "unable.to.access.LoginModule.", "LoginModuleにアクセスできません: " }, { "Login.Failure.all.modules.ignored", "ログイン失敗: すべてのモジュールは無視されます" }, { "java.security.policy.error.parsing.policy.message", "java.security.policy: {0}の構文解析エラー:\n\t{1}" }, { "java.security.policy.error.adding.Permission.perm.message", "java.security.policy: アクセス権{0}の追加エラー:\n\t{1}" }, { "java.security.policy.error.adding.Entry.message", "java.security.policy: エントリの追加エラー:\n\t{0}" }, { "alias.name.not.provided.pe.name.", "別名の指定がありません({0})" }, { "unable.to.perform.substitution.on.alias.suffix", "別名{0}に対して置換操作ができません" }, { "substitution.value.prefix.unsupported", "置換値{0}はサポートされていません" }, { "LPARAM", "(" }, { "RPARAM", ")" }, { "type.can.t.be.null", "入力をnullにすることはできません" }, { "keystorePasswordURL.can.not.be.specified.without.also.specifying.keystore", "キーストアを指定しない場合、keystorePasswordURLは指定できません" }, { "expected.keystore.type", "予想されたキーストア・タイプ" }, { "expected.keystore.provider", "予想されたキーストア・プロバイダ" }, { "multiple.Codebase.expressions", "複数のCodebase式" }, { "multiple.SignedBy.expressions", "複数のSignedBy式" }, { "duplicate.keystore.domain.name", "重複するキーストア・ドメイン名: {0}" }, { "duplicate.keystore.name", "重複するキーストア名: {0}" }, { "SignedBy.has.empty.alias", "SignedByは空の別名を保持します" }, { "can.not.specify.Principal.with.a.wildcard.class.without.a.wildcard.name", "ワイルドカード名のないワイルドカード・クラスを使用して、プリンシパルを指定することはできません" }, { "expected.codeBase.or.SignedBy.or.Principal", "予想されたcodeBase、SignedByまたはPrincipal" }, { "expected.permission.entry", "予想されたアクセス権エントリ" }, { "number.", "数 " }, { "expected.expect.read.end.of.file.", "[{0}]ではなく[ファイルの終わり]が読み込まれました" }, { "expected.read.end.of.file.", "[;]ではなく[ファイルの終わり]が読み込まれました" }, { "line.number.msg", "行{0}: {1}" }, { "line.number.expected.expect.found.actual.", "行{0}: [{1}]ではなく[{2}]が検出されました" }, { "null.principalClass.or.principalName", "nullのprincipalClassまたはprincipalName" }, { "PKCS11.Token.providerName.Password.", "PKCS11トークン[{0}]パスワード: " }, { "unable.to.instantiate.Subject.based.policy", "サブジェクト・ベースのポリシーのインスタンスを生成できません" } };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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


/* Location:              E:\java_source\rt.jar!\sun\security\util\Resources_ja.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */