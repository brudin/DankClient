package eu.bibl.api.event.info;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ java.lang.annotation.ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public abstract @interface EventTarget {
	
	public abstract byte priority() default EventPriority.NORMAL;
}