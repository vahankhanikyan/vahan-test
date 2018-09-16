# vahan-test-revolut

Used technologies:
+ Spark
+ Hibernate
+ H2
+ Jackson
+ Slf4j

To run the web service from the command line you need to execute. By defauld it runs on 4567 port
```sh
$ java -jar service/target/service-1.0-SNAPSHOT.jar
```
To have the jar file you need to install <service> module via maven.

## Smoke test
As I think it will run on Team CIty, Jenkins like systems, to have the Junit test structures I used maven test in place of console run. Could be also ant or any other tool that is suported by integration systems. 

## API

<table>
<tr><td>Request</td><td>URL</td><td>Description</td><td>Example</td></tr>
<tr><td>GET</td><td>/user/:id</td><td>get User by Id</td><td></td></tr>
<tr><td>GET</td><td>/user/byname/:name</td><td>get User by name</td><td></td></tr>
<tr><td>POST</td><td>/user</td><td>create a new user</td><td><p>Content-Type: application/json</p>
{
  "name": "Vahan",
  "amount": 551.1743742839909
}</td></tr>
<tr><td>GET</td><td>/transfer/:senderId/:receiverId/:amount</td><td>transfer money from one user to another</td><td><p></td></tr>
</table>

Because of no Spring requirement I learned new technologie Spark. Thank You for that :)