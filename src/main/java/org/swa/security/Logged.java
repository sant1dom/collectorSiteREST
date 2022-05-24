package org.swa.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import jakarta.ws.rs.NameBinding;


@NameBinding
@Retention(RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Logged {

}
