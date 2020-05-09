# Client sdk

There are two classes: `MatrixClient` and `SyncLoop`.

`MatrixClient` is a core classe with full support of the [Client-Server API](https://matrix.org/docs/spec/client_server/r0.4.0.html).

Access to the methods organized via api methods (event() for Event Api, room() for Room Api, ...).
You can find implemented API [here](https://github.com/ma1uta/jeon/tree/master/client-api/src/main/java/io/github/ma1uta/matrix/client/api)

### Usage

Complete example with minimal configuration: https://github.com/ma1uta/matrix-client-example

```$java
StandaloneClient mxClient = new StandaloneClient.Builder().domain("matrix.homeserver.tld").build();

// login
mxClient.auth().login("username", "password");

// set display name via profile api
mxCLient.profile().setDisplayName("my new display name");

// retrieve all joined rooms
List<String> rooms = mxClient.room().joinedRooms().getJoinedRooms();
String roomId = rooms.get(0);
// or join to the room
String roomId = mxClient.room().joinByIdOrAlias("#test:matrix.org", null, null).getRoomId();

// send message to the room
mxClient.event().sendMessage(roomId, "Hello, World!");

// logout
mxClient.auth().logout();
```

There are two ways to receive events from the server:
1. invoke api
    ```$java
    SyncResponse response = mxClient.sync().sync(filterId, nextBatch, fullState, presence, delay);
    ```
2. run `SyncLoop` in the separate thread.
    ```$java
    SyncLoop syncLoop = new SyncLoop(mxClient.sync(), (syncResponse, syncParams) -> {
       // inbound listener to parse incoming events.
       ...

       syncParams.setFullState(false);
       
       // syncParams.setTerminate(true); // to stop SyncLoop.
    });
    
    // Set initial parameters (Optional).
    SyncParams params = SyncParams.builder()
        .nextBatch("s123")   // set initial batch_token (optional)
        .fullState(true)     // retrieve full state or not
        .filter("myFilter")  // filter the received events (optional)
        .presence(null)      // set presence "offline" or null (optional)
        .timeout(10 * 1000)  // set long-polling delay in milliseconds (recommended to set bigger than 0 to avoid spam server)
        .build();
    
    syncLoop.setInit(params);
    
    // run the syncLoop
    ExecutorService service = Executors.newFixedThreadPool(1);
    service.submit(syncLoop);
    
    // stop loop
    service.shutdown();
    
    ```
