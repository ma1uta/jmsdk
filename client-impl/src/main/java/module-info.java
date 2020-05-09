module matrix.client.impl {
    uses io.github.ma1uta.matrix.impl.Deserializer;
    uses io.github.ma1uta.matrix.impl.RestClientBuilderConfigurer;

    exports io.github.ma1uta.matrix.client;
    exports io.github.ma1uta.matrix.client.methods.blocked;
    exports io.github.ma1uta.matrix.client.methods.async;
    exports io.github.ma1uta.matrix.client.sync;

    requires transitive matrix.client.api;
    requires transitive matrix.common.impl;
    requires transitive java.naming;
    requires transitive microprofile.rest.client.api;
}
