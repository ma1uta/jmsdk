module matrix.support.jsonb {
    exports io.github.ma1uta.matrix.support.jsonb;
    exports io.github.ma1uta.matrix.support.jsonb.mapper to org.mapstruct;

    requires transitive matrix.common.api;
    requires transitive java.json;
    requires org.mapstruct;
    requires java.sql;
}
