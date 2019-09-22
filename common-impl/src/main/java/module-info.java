module matrix.common.impl {
    exports io.github.ma1uta.matrix.impl;
    exports io.github.ma1uta.matrix.impl.exception;

    requires transitive matrix.common.api;
    requires transitive slf4j.api;
    requires transitive java.ws.rs;
    requires transitive microprofile.rest.client.api;
}
