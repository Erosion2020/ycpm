package org.erosion2020.payloads.annotation;

import org.erosion2020.payloads.PayloadType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PayloadSupport {
    int value() default PayloadType.Y_SO_SERIAL;
}