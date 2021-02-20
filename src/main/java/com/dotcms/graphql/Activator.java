package com.dotcms.graphql;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.osgi.framework.BundleContext;
import com.dotcms.filters.interceptor.FilterWebInterceptorProvider;
import com.dotcms.filters.interceptor.WebInterceptorDelegate;
import com.dotmarketing.filters.AutoLoginFilter;
import com.dotmarketing.loggers.Log4jUtil;
import com.dotmarketing.osgi.GenericBundleActivator;
import com.dotmarketing.util.Config;

public class Activator extends GenericBundleActivator {
    private LoggerContext pluginLoggerContext;
    
    private final GraphqlCacheWebInterceptor graphqlCorsWebInterceptor= new GraphqlCacheWebInterceptor();
    
    public void start(BundleContext context) throws Exception {


        initializeServices(context);
        // Initializing log4j...
        LoggerContext dotcmsLoggerContext = Log4jUtil.getLoggerContext();
        // Initialing the log4j context of this plugin based on the dotCMS logger context
        pluginLoggerContext = (LoggerContext) LogManager.getContext(this.getClass().getClassLoader(), false, dotcmsLoggerContext,
                dotcmsLoggerContext.getConfigLocation());
        
        final FilterWebInterceptorProvider filterWebInterceptorProvider = FilterWebInterceptorProvider.getInstance(Config.CONTEXT);

        final WebInterceptorDelegate delegate = filterWebInterceptorProvider.getDelegate(AutoLoginFilter.class);

        delegate.addFirst(graphqlCorsWebInterceptor);
        

        
        System.out.println("Installing the GraphqlCacheWebInterceptor");
        

    }

    public void stop(BundleContext context) throws Exception {
        
        final FilterWebInterceptorProvider filterWebInterceptorProvider = FilterWebInterceptorProvider.getInstance(Config.CONTEXT);

        final WebInterceptorDelegate delegate = filterWebInterceptorProvider.getDelegate(AutoLoginFilter.class);

        delegate.remove(graphqlCorsWebInterceptor.getName(), true);
        Log4jUtil.shutdown(pluginLoggerContext);

    }

}
