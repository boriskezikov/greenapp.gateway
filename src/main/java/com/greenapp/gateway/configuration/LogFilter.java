package com.greenapp.gateway.configuration;

import com.greenapp.gateway.services.AuthUtilsService;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class LogFilter extends ZuulFilter {

    private final AuthUtilsService service;

    @Value("${zuul.routes.auth.url}")
    private String AUTH_URL;

    @Value("${zuul.routes.task.url}")
    private String TASK_PROVIDER_URL;

    private static final Logger LOG = LoggerFactory.getLogger(LogFilter.class);

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        ctx.addZuulRequestHeader("X-GREEN-APP-ID", "GREEN");
        LOG.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));

        LOG.info(">> Authenticated");
        LOG.info("-------------------------------------------------------------------------------------------");
        LOG.info(String.format(">> Begin request: %s: %s", request.getMethod(), request.getRequestURI()));
        LOG.info(String.format(">> Headers: %s", service.parseHeaders(request.getHeaderNames(), request)));

        if (request.getRequestURI().contains("auth")) {
            LOG.warn(String.format(">> Redirecting to : %s", AUTH_URL));
        } else if (request.getRequestURI().contains("task-provider")) {
            LOG.warn(String.format(">> Redirecting to : %s", TASK_PROVIDER_URL));
        }
        LOG.info(String.format(">> End request: %s", request.getMethod()));
        LOG.info("-------------------------------------------------------------------------------------------");
        return null;

    }
}

