module matrix.support.jackson {
    exports io.github.ma1uta.matrix.support.jackson;

    requires transitive matrix.common.api;
    requires transitive com.fasterxml.jackson.core;
    requires transitive com.fasterxml.jackson.databind;
}
