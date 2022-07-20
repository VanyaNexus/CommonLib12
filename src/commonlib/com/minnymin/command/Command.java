/*
 * Decompiled with CFR 0.150.
 */
package commonlib.com.minnymin.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Command {
    public String name();

    public String permission() default "";

    public String noPerm() default "";

    public String[] aliases() default {};

    @Deprecated
    public String description() default "";

    @Deprecated
    public String usage() default "";

    public boolean inGameOnly() default false;

    public boolean isOpOnly() default false;
}

