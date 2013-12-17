My experiments with Scala, Akka and Spray

Curl commands for CRUD operations -

* C: curl -v -X POST http://localhost:9876/blog -H "Content-Type: application/json" -d '{"title":"poster","content":"hello"}'
* R: http://localhost:9876, http://localhost:9876/blogs, http://localhost:9876/blog/1
* U: curl -v -X PUT http://localhost:9876/blog -H "Content-Type: application/json" -d '{"id":"2","title":"poster","content":"hello"}'
* D: curl -v -X DELETE http://localhost:9876/blog/2