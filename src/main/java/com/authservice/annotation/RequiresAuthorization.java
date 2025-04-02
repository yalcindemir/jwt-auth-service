package com.authservice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresAuthorization {
    
    /**
     * Nesne tipi
     */
    String objectType();
    
    /**
     * İlişki türü
     */
    String relation();
    
    /**
     * Nesne ID'sinin bulunduğu parametre adı
     */
    String objectIdParam();
}
