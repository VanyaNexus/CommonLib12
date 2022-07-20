/*
 * Decompiled with CFR 0.150.
 */
package ru.den_abr.commonlib.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
public @interface Command {
    public String name();

    public String[] aliases() default {};

    public String permission() default "";
}

