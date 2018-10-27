# Client sdk

There are two classes: `MatrixClient` and `SyncLoop`.

`MatrixClient` is a core classes with full support of the [Client-Server API](https://matrix.org/docs/spec/client_server/r0.4.0.html).

Access to the methods organized via api methods (event() for Event Api, room() for Room Api, ...).
You can find implemented API [here](https://github.com/ma1uta/jeon/tree/master/client-api/src/main/java/io/github/ma1uta/matrix/client/api)

### Usage

```$java
MatrixClient mxClient = new MatrixClient("matrix.homeserver.tld");

// login
mxClient.auth().login("username", "password");

// set display name via profile api
mxCLient.profile().setDisplayName("my new display name");

// retrieve all joined rooms
List<String> rooms = mxClient.room().joinedRooms().join();
String roomId = rooms.get(0);
// or join to the room
String roomId = mxClient.room().joinByIdOrAlias("#test:matrix.org").join();

// send message to the room
mxCLient.event().sendMessage(roomId, "Hello, World!");

// logout
mxClient.auth().logout();
```

There are two ways to receive events from the server:
1. invoke api
    ```$java
    CompletableFuture<SyncResponse> response = mxClient.sync().sync(filterId, nextBatch, fullState, presence, timeout);
    ```
    Also it should organize loop to cycle `sync`-method.
2. run `SyncLoop` in the separate thread.
    ```$java
    SyncLoop syncLoop = new SyncLoop(mxClient.sync());
    syncLoop.setInboundListener((SyncResponse) -> {
       // parse events from the server.
       return null; // or a new sync params.
    });
    
    // Set initial parameters.
    SyncParams params = new SyncParams();
    // set initial batch_token (optional)
    params.setNextBatch("s123");
    // retrieve full state or not
    params.setFullState(true);
    // filter the received events (optional)
    params.setFilter("myFilter");
    // set presence "offline" or null (optional)
    params.setPresence(null);
    // set long-polling timeout in milliseconds (recommended to set bigger than 0 to avoid spam server)
    params.setTimeout(10 * 1000);
    
    syncLoop.setInit(params);
    
    // run the syncLoop
    ExecutorService service = Executors.newFixedThreadPool(1);
    service.submit(syncLoop);
    
    // stop loop
    service.shutdown();
    
    ```
