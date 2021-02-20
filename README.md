# GraphQL Query Cache

This repo provides a web interceptor that caches the response from a dotCMS graphql query.  Currently, the cache TTL is hard coded at 120 seconds which means your graphql responses for the same query will not change unless the query changes.  

The plugin will set a header if the cache is a hit or a miss, e.g.
`x-graphql-cache: hit`


It is easy to invalidate or punch through the cache by adding a comment in your graph query, e.g. these two queries will be cached separately. 

```
{
  page(url:"/destinations/costa-rica" pageMode:"LIVE", languageId:"1") {
    path
    type
    title
    shortyWorking
   # cache
  }
}
```


and 

```
{
  page(url:"/destinations/costa-rica" pageMode:"LIVE", languageId:"1") {
    path
    type
    title
    shortyWorking
   # different Cache
  }
}
```

