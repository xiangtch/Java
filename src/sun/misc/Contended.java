package sun.misc;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.TYPE})
public @interface Contended
{
  String value() default "";
}


/* Location:              E:\java_source\rt.jar!\sun\misc\Contended.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */