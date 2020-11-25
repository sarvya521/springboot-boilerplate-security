package com.backend.boilerplate.security;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Log4j2
public class CompositeHandlerMapping implements HandlerMapping {

    private ListableBeanFactory beanFactory;

    private List<HandlerMapping> mappings;

    public CompositeHandlerMapping(ListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest request)
            throws Exception {
        if (this.mappings == null) {
            this.mappings = extractMappings();
        }
        for (HandlerMapping mapping : this.mappings) {
            HandlerExecutionChain handler = mapping.getHandler(request);
            if (handler != null) {
                return handler;
            }
        }
        return null;
    }

    private List<HandlerMapping> extractMappings() {
        return this.beanFactory
                .getBeansOfType(HandlerMapping.class)
                .values()
                .stream()
                .filter(handlerMapping -> !handlerMapping.equals(this))
                .sorted(AnnotationAwareOrderComparator.INSTANCE)
                .collect(Collectors.toList());
    }

}