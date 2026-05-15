package ch.zhaw.it.pm4.javer.compiler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks compiler scaffolding that should be ignored by JaCoCo coverage reports.
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface JacocoGenerated {
    /**
     * Optional reason why the annotated element is excluded from coverage.
     *
     * @return exclusion reason
     */
    String value() default "";
}
