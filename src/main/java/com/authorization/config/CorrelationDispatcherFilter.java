package com.authorization.config;

import com.authorization.constants.CorrelationConstants;
import com.authorization.loggers.Loggers;

import com.authorization.utils.CorrelationUtils;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CorrelationDispatcherFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        ServletRequest httpServletRequest = (ServletRequest) servletRequest;
        ServletResponse httpServletResponse = (ServletResponse) servletResponse;

        String defaultThreadName = Thread.currentThread().getName();
        String correlationID = getCorrelationIDFromHeaders(httpServletRequest);
        Loggers.AUTHORIZATION_LOGGER.info("Correlation Id from header correlationID : " + correlationID);

        if (correlationID == null || !correlationID.contains(CorrelationConstants.CIDPrefix)
                || correlationID.length() < CorrelationConstants.CIDLength)
            correlationID = CorrelationUtils.getCorrelationID();

        Thread.currentThread().setName(correlationID);
        MDC.put("CorrelationId", correlationID);
        servletRequest.setAttribute(CorrelationConstants.CIDHeader, correlationID);

        Loggers.AUTHORIZATION_LOGGER.info("Correlation ID of the current request correlationID " + correlationID);

        try {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } catch (Exception e) {
            Loggers.AUTHORIZATION_LOGGER.info("Exception while handling request " , e);
        } finally {
            MDC.remove("CorrelationId");
            Thread.currentThread().setName(defaultThreadName);
        }
    }

    public String getCorrelationIDFromHeaders(ServletRequest httpServletRequest) {
        if (httpServletRequest instanceof ServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) httpServletRequest;
            return httpRequest.getHeader(CorrelationConstants.CIDHeader);
        }
        return null;
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
