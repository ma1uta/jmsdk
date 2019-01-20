/*
 * Copyright sablintolya@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.ma1uta.matrix.bot.command;

import io.github.ma1uta.matrix.Id;
import io.github.ma1uta.matrix.bot.BotConfig;
import io.github.ma1uta.matrix.bot.BotDao;
import io.github.ma1uta.matrix.bot.Context;
import io.github.ma1uta.matrix.bot.PersistentService;
import io.github.ma1uta.matrix.event.RoomEvent;

/**
 * Set or show default command.
 *
 * @param <C> bot configuration.
 * @param <D> bot dao.
 * @param <S> bot service.
 * @param <E> extra data.
 */
public class DefaultCommand<C extends BotConfig, D extends BotDao<C>, S extends PersistentService<D>, E> extends OwnerCommand<C, D, S, E> {

    @Override
    public String name() {
        return "default";
    }

    @Override
    public boolean ownerInvoke(Context<C, D, S, E> context, Id roomId, RoomEvent event, String arguments) {
        if (arguments == null || arguments.trim().isEmpty()) {
            context.getConfig().setDefaultCommand(null);
            return true;
        } else if (context.getBot().getCommands().get(arguments) != null) {
            context.getConfig().setDefaultCommand(arguments);
            return true;
        } else {
            context.getMatrixClient().event().sendNotice(roomId, "Unknown command: " + arguments);
            return false;
        }
    }

    @Override
    public String help() {
        return "set or show default command (invoked only by owner).";
    }

    @Override
    public String usage() {
        return "default [<command>]";
    }
}
