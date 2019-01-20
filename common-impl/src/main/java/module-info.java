import io.github.ma1uta.matrix.impl.MatrixIdParser;

module matrix.common.impl {
    exports io.github.ma1uta.matrix.impl;
    exports io.github.ma1uta.matrix.impl.exception;

    requires transitive matrix.common.api;
    requires transitive org.slf4j;
    requires transitive java.ws.rs;

    provides io.github.ma1uta.matrix.IdParser with MatrixIdParser;
}
