mTLS CLI Client Example w/ Spring Boot
===================

This is a demo of how to implement an mTLS client via Spring Boot and Java.

I'm sure there MUST BE A BETTER WAY and I welcome PRs.  

Unlike the server-side, the client needed code to establish an SSL Context 
that loaded the keystore and trust store, instead of doing it all just from 
changes to application properties like the server-side example 
[here](https://github.com/navicore/sbjava-mtls-lotsofnames-server) was able to.

HOWTO
----------

###STEP 0

This repo is just a client.  You need a server for it to talk to.  So, run an
instance of a server from [this](https://github.com/navicore/sbjava-mtls-lotsofnames-server) other repo locally.

For the demo - use the p12 files generated in the server setup instructions.

###STEP 1

Copy from the server's `src/main/resources` the `client.p12` and 
`client-truststore.p12` into this repo's `src/main/resources/` dir.


###STEP 2

```bash
./gradlew assemble
```

###STEP 3

```bash
java -jar ./build/libs/mtls-client-0.0.1-SNAPSHOT.jar
```

If it worked, you should see something like this printed to stdout:


```json
{
  "src" : "fred",
  "value" : "Esplees Drillock"
}
```
