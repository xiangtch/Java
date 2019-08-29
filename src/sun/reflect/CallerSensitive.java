package sun.reflect;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD})
public @interface CallerSensitive {}


/* Location:              E:\java_source\rt.jar!\sun\reflect\CallerSensitive.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */