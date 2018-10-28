module matrix.client.impl {
    exports io.github.ma1uta.matrix.client;
    exports io.github.ma1uta.matrix.client.factory;
    exports io.github.ma1uta.matrix.client.methods;
    exports io.github.ma1uta.matrix.client.sync;

    requires transitive matrix.client.api;
    requires transitive matrix.common.impl;
}
