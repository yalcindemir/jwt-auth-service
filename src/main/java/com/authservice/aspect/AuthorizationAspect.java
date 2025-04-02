package com.authservice.aspect;

import com.authservice.annotation.RequiresAuthorization;
import com.authservice.service.OpenFgaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationAspect {

    private final OpenFgaService openFgaService;

    @Around("@annotation(com.authservice.annotation.RequiresAuthorization)")
    public Object checkAuthorization(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        RequiresAuthorization annotation = method.getAnnotation(RequiresAuthorization.class);
        String objectType = annotation.objectType();
        String relation = annotation.relation();
        
        // Nesne ID'sini parametrelerden al
        String objectId = resolveObjectId(joinPoint, annotation.objectIdParam());
        
        // Kullanıcı ID'sini güvenlik bağlamından al
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        
        // Yetkilendirme kontrolü yap
        boolean isAuthorized = openFgaService.checkAuthorization(objectType, objectId, relation, userId);
        
        if (!isAuthorized) {
            throw new SecurityException("Bu işlem için yetkiniz bulunmamaktadır");
        }
        
        return joinPoint.proceed();
    }
    
    private String resolveObjectId(ProceedingJoinPoint joinPoint, String paramName) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        
        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterNames[i].equals(paramName)) {
                Object arg = args[i];
                if (arg instanceof String) {
                    return (String) arg;
                } else if (arg instanceof UUID) {
                    return arg.toString();
                }
                break;
            }
        }
        
        throw new IllegalArgumentException("Nesne ID parametresi bulunamadı: " + paramName);
    }
}
