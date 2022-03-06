# kvstore

InMemory Key-Value Store implemented using Java, Grpc, PicoCli with Concurrency on HashMap. 

Running the Grpc Server
$ mvn -DskipTests package exec:java -Dexec.mainClass=com.interviews.kvstore.App

Running the Client
$ cd bin
$ ./kvstore set  -k test1 -v testval1 -k test2 -v testval2
Response=>
items {
  key: "test1"
  value: "testval1"
}
items {
  key: "test2"
  value: "testval2"
}

$ ./kvstore metrics
Metrics=>
Keys Count: 2
Values Size(In Bytes): 16
Get Ops Count: 0
Set Ops Count: 1
Delete Ops Count: 0
$ ./kvstore set  -k test3 -v testval3
Response=>
key: "test3"
value: "testval3"

$ ./kvstore get -k test2 -k test1
Response=>
items {
  key: "test2"
  value: "testval2"
}
items {
  key: "test1"
  value: "testval1"
}

$ ./kvstore get -k test3
Response=>
key: "test3"
value: "testval3"

$ ./kvstore metrics
Metrics=>
Keys Count: 3
Values Size(In Bytes): 24
Get Ops Count: 2
Set Ops Count: 2
Delete Ops Count: 0
$ ./kvstore delete -k test2
Response=>success: true
