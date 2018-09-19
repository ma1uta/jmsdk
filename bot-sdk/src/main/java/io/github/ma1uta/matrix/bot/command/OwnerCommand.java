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

import io.github.ma1uta.matrix.Event;
import io.github.ma1uta.matrix.bot.BotConfig;
import io.github.ma1uta.matrix.bot.BotDao;
import io.github.ma1uta.matrix.bot.Command;
import io.github.ma1uta.matrix.bot.Context;
import io.github.ma1uta.matrix.bot.PersistentService;

/**
 * Provide checking that current command was invoked by owner.
 *
 * @param <C> bot configuration.
 * @param <D> bot dao.
 * @param <S> bot service.
 * @param <E> extra data.
 */
public abstract class OwnerCommand<C extends BotConfig, D extends BotDao<C>, S extends PersistentService<D>, E> implements
    Command<C, D, S, E> {

    @Override
    public boolean invoke(Context<C, D, S, E> context, String roomId, Event event, String arguments) {
        C config = context.getConfig();
        if (config.getOwner() != null && !config.getOwner().equals(event.getSender())) {
            return false;
        }

        return ownerInvoke(context, roomId, event, arguments);
    }

    protected abstract boolean ownerInvoke(Context<C, D, S, E> context, String roomId, Event event, String arguments);
}
