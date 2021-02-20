# GraphQL Query Cache

This repo provides a web interceptor that caches the response from a dotCMS graphql query.  Currently, the cache TTL is hard coded at 120 seconds which means your graphql responses for the same query will not change unless the query changes.  In practice, this is easy to do by adding a comment in your graph query, e.g. these two queries will be cached separately. 

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
