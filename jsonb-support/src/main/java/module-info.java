module matrix.support.jsonb {
    uses io.github.ma1uta.matrix.support.jsonb.JsonbProvider;
    exports io.github.ma1uta.matrix.support.jsonb;
    exports io.github.ma1uta.matrix.support.jsonb.mapper to org.mapstruct;

    requires transitive matrix.common.api;
    requires transitive matrix.common.impl;
    requires transitive java.json;
    requires org.mapstruct;
    requires java.annotation;
    requires java.sql;
}
