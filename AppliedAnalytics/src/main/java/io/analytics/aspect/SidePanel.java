package io.analytics.aspect;

import io.analytics.enums.PageView;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SidePanel {
	boolean animate() default false;
	PageView page() default PageView.DASHBOARD;
}
