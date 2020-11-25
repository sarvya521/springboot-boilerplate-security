package com.backend.boilerplate.security.util;

import com.backend.boilerplate.util.StringUtil;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public final class HandlerMappingUriReader {

    private HandlerMappingUriReader() {
        throw new AssertionError();
    }

    public static String[] readUris(final HandlerMethod handlerMethod,
                                    final HttpMethod httpMethod) {
        String[] controllerUris = readClassLevelRequestMappingUris(handlerMethod);
        String[] methodUris = readMethodLevelRequestMappingUris(handlerMethod, httpMethod);
        return StringUtil.crossJoinArrays(controllerUris, methodUris);
    }

    public static String[] readClassLevelRequestMappingUris(final HandlerMethod handlerMethod) {
        String[] controllerUris = null;
        if (handlerMethod.getBeanType().isAnnotationPresent(RequestMapping.class)) {
            final String[] path = handlerMethod.getBeanType().getAnnotation(RequestMapping.class).path();
            final String[] value = handlerMethod.getBeanType().getAnnotation(RequestMapping.class).value();
            controllerUris = StringUtil.union(path, value);
        }
        return controllerUris;
    }

    public static String[] readMethodLevelRequestMappingUris(final HandlerMethod handlerMethod,
                                                             final HttpMethod httpMethod) {
        String[] methodUris = readMethodLevelRequestMappingUris(handlerMethod);
        if (null != methodUris) {
            return methodUris;
        }
        switch (httpMethod) {
            case GET:
                methodUris = readMethodLevelGetMappingUris(handlerMethod);
                break;
            case POST:
                methodUris = readMethodLevelPostMappingUris(handlerMethod);
                break;
            case PUT:
                methodUris = readMethodLevelPutMappingUris(handlerMethod);
                break;
            case PATCH:
                methodUris = readMethodLevelPatchMappingUris(handlerMethod);
                break;
            case DELETE:
                methodUris = readMethodLevelDeleteMappingUris(handlerMethod);
                break;
            default:
                break;
        }
        return methodUris;
    }

    public static String[] readMethodLevelRequestMappingUris(final HandlerMethod handlerMethod) {
        String[] methodUris = null;
        if (handlerMethod.getMethod().isAnnotationPresent(RequestMapping.class)) {
            final String[] path = handlerMethod.getMethod().getAnnotation(RequestMapping.class).path();
            final String[] value = handlerMethod.getMethod().getAnnotation(RequestMapping.class).value();
            methodUris = StringUtil.union(path, value);
        }
        return methodUris;
    }

    public static String[] readMethodLevelGetMappingUris(final HandlerMethod handlerMethod) {
        String[] methodUris = null;
        if (handlerMethod.getMethod().isAnnotationPresent(GetMapping.class)) {
            final String[] path = handlerMethod.getMethod().getAnnotation(GetMapping.class).path();
            final String[] value = handlerMethod.getMethod().getAnnotation(GetMapping.class).value();
            methodUris = StringUtil.union(path, value);
        }
        return methodUris;
    }

    public static String[] readMethodLevelPostMappingUris(final HandlerMethod handlerMethod) {
        String[] methodUris = null;
        if (handlerMethod.getMethod().isAnnotationPresent(PostMapping.class)) {
            final String[] path = handlerMethod.getMethod().getAnnotation(PostMapping.class).path();
            final String[] value = handlerMethod.getMethod().getAnnotation(PostMapping.class).value();
            methodUris = StringUtil.union(path, value);
        }
        return methodUris;
    }

    public static String[] readMethodLevelPutMappingUris(final HandlerMethod handlerMethod) {
        String[] methodUris = null;
        if (handlerMethod.getMethod().isAnnotationPresent(PutMapping.class)) {
            final String[] path = handlerMethod.getMethod().getAnnotation(PutMapping.class).path();
            final String[] value = handlerMethod.getMethod().getAnnotation(PutMapping.class).value();
            methodUris = StringUtil.union(path, value);
        }
        return methodUris;
    }

    public static String[] readMethodLevelPatchMappingUris(final HandlerMethod handlerMethod) {
        String[] methodUris = null;
        if (handlerMethod.getMethod().isAnnotationPresent(PatchMapping.class)) {
            final String[] path = handlerMethod.getMethod().getAnnotation(PatchMapping.class).path();
            final String[] value = handlerMethod.getMethod().getAnnotation(PatchMapping.class).value();
            methodUris = StringUtil.union(path, value);
        }
        return methodUris;
    }

    public static String[] readMethodLevelDeleteMappingUris(final HandlerMethod handlerMethod) {
        String[] methodUris = null;
        if (handlerMethod.getMethod().isAnnotationPresent(DeleteMapping.class)) {
            final String[] path = handlerMethod.getMethod().getAnnotation(DeleteMapping.class).path();
            final String[] value = handlerMethod.getMethod().getAnnotation(DeleteMapping.class).value();
            methodUris = StringUtil.union(path, value);
        }
        return methodUris;
    }
}
