package com.spay.wallet.customerservice.common.security;

import com.spay.wallet.customerservice.common.security.UserPermission;
import com.spay.wallet.customerservice.common.security.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//@Target({ElementType.METHOD})
//@Retention(RetentionPolicy.RUNTIME)
//public @interface UserAuthorize {
//    UserRole[] roles();
//    UserPermission[] permissions() default {};
//
//}