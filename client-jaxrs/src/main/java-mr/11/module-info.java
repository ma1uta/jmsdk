module matrix.client.jaxrs {
    exports io.github.ma1uta.matrix.client.factory.jaxrs;

    requires transitive matrix.client.impl;
    requires transitive matrix.support.jackson;
    requires transitive com.fasterxml.jackson.jaxrs.json;
}
