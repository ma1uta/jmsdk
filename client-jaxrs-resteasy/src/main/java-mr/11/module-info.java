module matrix.client.jaxrs.resteasy {
    exports io.github.ma1uta.matrix.client.factory.jaxrs;

    requires transitive matrix.client.impl;
    requires transitive matrix.support.jackson;

}
