module matrix.common.impl {
    exports io.github.ma1uta.matrix.impl;
    exports io.github.ma1uta.matrix.impl.exception;

    requires transitive matrix.common.api;
    requires transitive org.slf4j;
    requires transitive java.ws.rs;

    provides io.github.ma1uta.matrix.Id with io.github.ma1uta.matrix.impl.MatrixId;
}
