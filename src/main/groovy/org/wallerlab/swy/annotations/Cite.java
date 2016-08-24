package org.wallerlab.swy.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A custom annotation to mark parts of the program that use
 * published theories, concepts, equations, ...<br>
 * At the end of the program these references are (hopefully)
 * automatically printed, if the annotated class (or the class
 * of the annotated method) has been used.
 * 
 * @author t_dres03
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cite {
	
	String[] authors();
	String journal();
	int year();
	String pages();
}
