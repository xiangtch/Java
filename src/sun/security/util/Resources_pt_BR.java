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
/*     */ public class Resources_pt_BR
/*     */   extends ListResourceBundle
/*     */ {
/*  35 */   private static final Object[][] contents = { { "invalid.null.input.s.", "entrada(s) nula(s) inválida(s)" }, { "actions.can.only.be.read.", "as ações só podem ser 'lidas'" }, { "permission.name.name.syntax.invalid.", "sintaxe inválida do nome da permissão [{0}]: " }, { "Credential.Class.not.followed.by.a.Principal.Class.and.Name", "Classe da Credencial não seguida por um Nome e uma Classe do Principal" }, { "Principal.Class.not.followed.by.a.Principal.Name", "Classe do Principal não seguida por um Nome do Principal" }, { "Principal.Name.must.be.surrounded.by.quotes", "O Nome do Principal deve estar entre aspas" }, { "Principal.Name.missing.end.quote", "Faltam as aspas finais no Nome do Principal" }, { "PrivateCredentialPermission.Principal.Class.can.not.be.a.wildcard.value.if.Principal.Name.is.not.a.wildcard.value", "A Classe do Principal PrivateCredentialPermission não pode ser um valor curinga (*) se o Nome do Principal não for um valor curinga (*)" }, { "CredOwner.Principal.Class.class.Principal.Name.name", "CredOwner:\n\tClasse do Principal = {0}\n\tNome do Principal = {1}" }, { "provided.null.name", "nome nulo fornecido" }, { "provided.null.keyword.map", "mapa de palavra-chave nulo fornecido" }, { "provided.null.OID.map", "mapa OID nulo fornecido" }, { "NEWLINE", "\n" }, { "invalid.null.AccessControlContext.provided", "AccessControlContext nulo inválido fornecido" }, { "invalid.null.action.provided", "ação nula inválida fornecida" }, { "invalid.null.Class.provided", "Classe nula inválida fornecida" }, { "Subject.", "Assunto:\n" }, { ".Principal.", "\tPrincipal: " }, { ".Public.Credential.", "\tCredencial Pública: " }, { ".Private.Credentials.inaccessible.", "\tCredenciais Privadas inacessíveis\n" }, { ".Private.Credential.", "\tCredencial Privada: " }, { ".Private.Credential.inaccessible.", "\tCredencial Privada inacessível\n" }, { "Subject.is.read.only", "O Assunto é somente para leitura" }, { "attempting.to.add.an.object.which.is.not.an.instance.of.java.security.Principal.to.a.Subject.s.Principal.Set", "tentativa de adicionar um objeto que não é uma instância de java.security.Principal a um conjunto de principais do Subject" }, { "attempting.to.add.an.object.which.is.not.an.instance.of.class", "tentativa de adicionar um objeto que não é uma instância de {0}" }, { "LoginModuleControlFlag.", "LoginModuleControlFlag: " }, { "Invalid.null.input.name", "Entrada nula inválida: nome" }, { "No.LoginModules.configured.for.name", "Nenhum LoginModule configurado para {0}" }, { "invalid.null.Subject.provided", "Subject nulo inválido fornecido" }, { "invalid.null.CallbackHandler.provided", "CallbackHandler nulo inválido fornecido" }, { "null.subject.logout.called.before.login", "Subject nulo - log-out chamado antes do log-in" }, { "unable.to.instantiate.LoginModule.module.because.it.does.not.provide.a.no.argument.constructor", "não é possível instanciar LoginModule, {0}, porque ele não fornece um construtor sem argumento" }, { "unable.to.instantiate.LoginModule", "não é possível instanciar LoginModule" }, { "unable.to.instantiate.LoginModule.", "não é possível instanciar LoginModule: " }, { "unable.to.find.LoginModule.class.", "não é possível localizar a classe LoginModule: " }, { "unable.to.access.LoginModule.", "não é possível acessar LoginModule: " }, { "Login.Failure.all.modules.ignored", "Falha de Log-in: todos os módulos ignorados" }, { "java.security.policy.error.parsing.policy.message", "java.security.policy: erro durante o parsing de {0}:\n\t{1}" }, { "java.security.policy.error.adding.Permission.perm.message", "java.security.policy: erro ao adicionar a permissão, {0}:\n\t{1}" }, { "java.security.policy.error.adding.Entry.message", "java.security.policy: erro ao adicionar a Entrada:\n\t{0}" }, { "alias.name.not.provided.pe.name.", "nome de alias não fornecido ({0})" }, { "unable.to.perform.substitution.on.alias.suffix", "não é possível realizar a substituição no alias, {0}" }, { "substitution.value.prefix.unsupported", "valor da substituição, {0}, não suportado" }, { "LPARAM", "(" }, { "RPARAM", ")" }, { "type.can.t.be.null", "o tipo não pode ser nulo" }, { "keystorePasswordURL.can.not.be.specified.without.also.specifying.keystore", "keystorePasswordURL não pode ser especificado sem que a área de armazenamento de chaves também seja especificada" }, { "expected.keystore.type", "tipo de armazenamento de chaves esperado" }, { "expected.keystore.provider", "fornecedor da área de armazenamento de chaves esperado" }, { "multiple.Codebase.expressions", "várias expressões CodeBase" }, { "multiple.SignedBy.expressions", "várias expressões SignedBy" }, { "duplicate.keystore.domain.name", "nome do domínio da área de armazenamento de teclas duplicado: {0}" }, { "duplicate.keystore.name", "nome da área de armazenamento de chaves duplicado: {0}" }, { "SignedBy.has.empty.alias", "SignedBy tem alias vazio" }, { "can.not.specify.Principal.with.a.wildcard.class.without.a.wildcard.name", "não é possível especificar um principal com uma classe curinga sem um nome curinga" }, { "expected.codeBase.or.SignedBy.or.Principal", "CodeBase ou SignedBy ou Principal esperado" }, { "expected.permission.entry", "entrada de permissão esperada" }, { "number.", "número " }, { "expected.expect.read.end.of.file.", "esperado [{0}], lido [fim do arquivo]" }, { "expected.read.end.of.file.", "esperado [;], lido [fim do arquivo]" }, { "line.number.msg", "linha {0}: {1}" }, { "line.number.expected.expect.found.actual.", "linha {0}: esperada [{1}], encontrada [{2}]" }, { "null.principalClass.or.principalName", "principalClass ou principalName nulo" }, { "PKCS11.Token.providerName.Password.", "Senha PKCS11 de Token [{0}]: " }, { "unable.to.instantiate.Subject.based.policy", "não é possível instanciar a política com base em Subject" } };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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


/* Location:              E:\java_source\rt.jar!\sun\security\util\Resources_pt_BR.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */