package ru.den_abr.commonlib.placeholders.custom;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface CPlaceholder {
    String id();
    
    String version();
    
    String description();
    
    boolean requiresPlayer() default true;
    
    boolean canRunAsync() default true;
    
    String author() default "Den_Abr";
    
    String[] depend();
}
