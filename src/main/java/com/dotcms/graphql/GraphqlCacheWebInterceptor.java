package com.dotcms.graphql;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.dotcms.filters.interceptor.Result;
import com.dotcms.filters.interceptor.WebInterceptor;
import com.dotmarketing.util.UtilMethods;
import com.google.common.collect.ImmutableMap;
import io.vavr.control.Try;



public class GraphqlCacheWebInterceptor implements WebInterceptor {

    final static String GRAPHQL_QUERY = "GRAPHQL_QUERY";
    private static final String API_CALL = "/api/v1/graphql";

    private final GraphQLCache graphCache = GraphQLCache.INSTANCE.get();

    @Override
    public String[] getFilters() {
        return new String[] {API_CALL + "*"};
    }

    @Override
    public Result intercept(final HttpServletRequest requestIn, final HttpServletResponse response) throws IOException {

        if (!"POST".equals(requestIn.getMethod())) {
            return Result.NEXT;
        }
        final  HttpRequestReaderWrapper wrapper = new HttpRequestReaderWrapper(requestIn);

        final Optional<String> query = wrapper.getGraphQLQuery();
        if (!query.isPresent()) {
            return Result.NEXT;
        }

        final Optional<String> graphResponse = graphCache.get(query.get());
        if (graphResponse.isPresent()) {
            corsHeaders.entrySet().stream().forEach(e -> response.setHeader(e.getKey(), e.getValue()));
            response.setContentType("application/json;charset=UTF-8");
            response.setContentLength(graphResponse.get().getBytes().length);
            response.getWriter().write(graphResponse.get());
            return Result.SKIP_NO_CHAIN;
        }


        wrapper.setAttribute(GRAPHQL_QUERY, query.get());


        return new Result.Builder().wrap(new MockHttpCaptureResponse(response)).wrap(wrapper).next().build();

    }

    @Override
    public boolean afterIntercept(final HttpServletRequest request, final HttpServletResponse response) {
        final String query = (String) request.getAttribute(GRAPHQL_QUERY);

        if (response instanceof MockHttpCaptureResponse && UtilMethods.isSet(query)) {
            final MockHttpCaptureResponse mockResponse = (MockHttpCaptureResponse) response;
            final String graphqlResonse = mockResponse.writer.toString();
            Try.run(() -> mockResponse.originalResponse.getWriter().write(graphqlResonse));
            graphCache.put(query, graphqlResonse);

        }
        return true;
    }
    
    
    private final Map<String,String> corsHeaders = ImmutableMap.<String, String>builder()
                        .put("access-control-allow-origin", "*")
                        .put("access-control-allow-credentials", "true")
                        .put("access-control-allow-headers", "*")
                        .put("access-control-allow-methods", "GET,PUT,POST,DELETE,HEAD,OPTIONS,PATCH")
                        .put("access-control-expose-headers", "*")
                        .build();


    
}
