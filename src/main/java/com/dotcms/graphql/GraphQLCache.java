package com.dotcms.graphql;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import com.dotmarketing.util.UtilMethods;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

public class GraphQLCache  {


  private final Cache<Long, String> graphCache;

  private final long CACHE_SIZE=5000;
  private final long CACHE_TTL_SECONDS=120;
  

  public GraphQLCache() {
      this.graphCache = Caffeine.newBuilder()
                      .maximumSize(CACHE_SIZE)
                      .expireAfterWrite(CACHE_TTL_SECONDS, TimeUnit.SECONDS).build();
  }



  public Optional<String> get(String query) {

    final long cacheKey = hashQuery(query);

    return Optional.ofNullable(graphCache.getIfPresent(cacheKey));
  }
  

  public void put(String query, String result) {
    final long cacheKey = hashQuery(query);
    if(UtilMethods.isSet(result)) {
        graphCache.put(cacheKey, result);
    }
  }

  private long hashQuery(String query) {
    long hashCode = 1125899906842597L;
    for (int i = 0; i < query.length(); i++) {
      hashCode = 31 * hashCode + query.charAt(i);
    }

    return hashCode;
  }


  public enum INSTANCE {
    INSTANCE;
    private final GraphQLCache cache = new GraphQLCache();

    public static GraphQLCache get() {
      return INSTANCE.cache;
    }
  }

  
}
