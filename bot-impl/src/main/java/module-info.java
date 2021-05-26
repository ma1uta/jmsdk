module matrix.bot {
    exports io.github.ma1uta.matrix.appservice;
    exports io.github.ma1uta.matrix.bot;
    exports io.github.ma1uta.matrix.bot.command;

    requires transitive jakarta.persistence;
    requires matrix.client.impl;
}
