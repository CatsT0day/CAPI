package me.catst0day.capi.Commands.commandAPI;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.catst0day.capi.Managers.CAPIPermissionManager.CAPIPerm;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CAPICommandAnnotation {
    String name();
    String[] aliases() default {};
    CAPIPerm permission();
    boolean requirePlayer() default false;
    long cooldownSeconds() default 0;
    String description() default "";
}