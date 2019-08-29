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
/*     */ public class Resources_it
/*     */   extends ListResourceBundle
/*     */ {
/*  35 */   private static final Object[][] contents = { { "invalid.null.input.s.", "input nullo/i non valido/i" }, { "actions.can.only.be.read.", "le azioni possono essere solamente 'lette'" }, { "permission.name.name.syntax.invalid.", "sintassi [{0}] non valida per il nome autorizzazione: " }, { "Credential.Class.not.followed.by.a.Principal.Class.and.Name", "la classe di credenziali non è seguita da un nome e una classe di principal" }, { "Principal.Class.not.followed.by.a.Principal.Name", "la classe di principal non è seguita da un nome principal" }, { "Principal.Name.must.be.surrounded.by.quotes", "il nome principal deve essere compreso tra apici" }, { "Principal.Name.missing.end.quote", "apice di chiusura del nome principal mancante" }, { "PrivateCredentialPermission.Principal.Class.can.not.be.a.wildcard.value.if.Principal.Name.is.not.a.wildcard.value", "la classe principal PrivateCredentialPermission non può essere un valore carattere jolly (*) se il nome principal a sua volta non è un valore carattere jolly (*)" }, { "CredOwner.Principal.Class.class.Principal.Name.name", "CredOwner:\n\tclasse Principal = {0}\n\tNome Principal = {1}" }, { "provided.null.name", "il nome fornito è nullo" }, { "provided.null.keyword.map", "specificata mappa parole chiave null" }, { "provided.null.OID.map", "specificata mappa OID null" }, { "NEWLINE", "\n" }, { "invalid.null.AccessControlContext.provided", "fornito un valore nullo non valido per AccessControlContext" }, { "invalid.null.action.provided", "fornita un'azione nulla non valida" }, { "invalid.null.Class.provided", "fornita una classe nulla non valida" }, { "Subject.", "Oggetto:\n" }, { ".Principal.", "\tPrincipal: " }, { ".Public.Credential.", "\tCredenziale pubblica: " }, { ".Private.Credentials.inaccessible.", "\tImpossibile accedere alle credenziali private\n" }, { ".Private.Credential.", "\tCredenziale privata: " }, { ".Private.Credential.inaccessible.", "\tImpossibile accedere alla credenziale privata\n" }, { "Subject.is.read.only", "L'oggetto è di sola lettura" }, { "attempting.to.add.an.object.which.is.not.an.instance.of.java.security.Principal.to.a.Subject.s.Principal.Set", "si è tentato di aggiungere un oggetto che non è un'istanza di java.security.Principal a un set principal dell'oggetto" }, { "attempting.to.add.an.object.which.is.not.an.instance.of.class", "si è tentato di aggiungere un oggetto che non è un''istanza di {0}" }, { "LoginModuleControlFlag.", "LoginModuleControlFlag: " }, { "Invalid.null.input.name", "Input nullo non valido: nome" }, { "No.LoginModules.configured.for.name", "Nessun LoginModules configurato per {0}" }, { "invalid.null.Subject.provided", "fornito un valore nullo non valido per l'oggetto" }, { "invalid.null.CallbackHandler.provided", "fornito un valore nullo non valido per CallbackHandler" }, { "null.subject.logout.called.before.login", "oggetto nullo - il logout è stato richiamato prima del login" }, { "unable.to.instantiate.LoginModule.module.because.it.does.not.provide.a.no.argument.constructor", "impossibile creare un''istanza di LoginModule {0} in quanto non restituisce un argomento vuoto per il costruttore" }, { "unable.to.instantiate.LoginModule", "impossibile creare un'istanza di LoginModule" }, { "unable.to.instantiate.LoginModule.", "impossibile creare un'istanza di LoginModule: " }, { "unable.to.find.LoginModule.class.", "impossibile trovare la classe LoginModule: " }, { "unable.to.access.LoginModule.", "impossibile accedere a LoginModule " }, { "Login.Failure.all.modules.ignored", "Errore di login: tutti i moduli sono stati ignorati" }, { "java.security.policy.error.parsing.policy.message", "java.security.policy: errore durante l''analisi di {0}:\n\t{1}" }, { "java.security.policy.error.adding.Permission.perm.message", "java.security.policy: errore durante l''aggiunta dell''autorizzazione {0}:\n\t{1}" }, { "java.security.policy.error.adding.Entry.message", "java.security.policy: errore durante l''aggiunta della voce:\n\t{0}" }, { "alias.name.not.provided.pe.name.", "impossibile fornire nome alias ({0})" }, { "unable.to.perform.substitution.on.alias.suffix", "impossibile eseguire una sostituzione sull''alias, {0}" }, { "substitution.value.prefix.unsupported", "valore sostituzione, {0}, non supportato" }, { "LPARAM", "(" }, { "RPARAM", ")" }, { "type.can.t.be.null", "il tipo non può essere nullo" }, { "keystorePasswordURL.can.not.be.specified.without.also.specifying.keystore", "Impossibile specificare keystorePasswordURL senza specificare anche il keystore" }, { "expected.keystore.type", "tipo keystore previsto" }, { "expected.keystore.provider", "provider di keystore previsto" }, { "multiple.Codebase.expressions", "espressioni Codebase multiple" }, { "multiple.SignedBy.expressions", "espressioni SignedBy multiple" }, { "duplicate.keystore.domain.name", "nome dominio keystore duplicato: {0}" }, { "duplicate.keystore.name", "nome keystore duplicato: {0}" }, { "SignedBy.has.empty.alias", "SignedBy presenta un alias vuoto" }, { "can.not.specify.Principal.with.a.wildcard.class.without.a.wildcard.name", "impossibile specificare un principal con una classe carattere jolly senza un nome carattere jolly" }, { "expected.codeBase.or.SignedBy.or.Principal", "previsto codeBase o SignedBy o principal" }, { "expected.permission.entry", "prevista voce di autorizzazione" }, { "number.", "numero " }, { "expected.expect.read.end.of.file.", "previsto [{0}], letto [end of file]" }, { "expected.read.end.of.file.", "previsto [;], letto [end of file]" }, { "line.number.msg", "riga {0}: {1}" }, { "line.number.expected.expect.found.actual.", "riga {0}: previsto [{1}], trovato [{2}]" }, { "null.principalClass.or.principalName", "principalClass o principalName nullo" }, { "PKCS11.Token.providerName.Password.", "Password per token PKCS11 [{0}]: " }, { "unable.to.instantiate.Subject.based.policy", "impossibile creare un'istanza dei criteri basati sull'oggetto" } };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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


/* Location:              E:\java_source\rt.jar!\sun\security\util\Resources_it.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */