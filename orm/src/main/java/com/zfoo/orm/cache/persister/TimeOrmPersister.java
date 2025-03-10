/*
 * Copyright (C) 2020 The zfoo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.zfoo.orm.cache.persister;

import com.zfoo.event.manager.EventBus;
import com.zfoo.event.model.anno.Bus;
import com.zfoo.orm.OrmContext;
import com.zfoo.orm.cache.EntityCaches;
import com.zfoo.orm.model.vo.EntityDef;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.manager.SchedulerBus;

import java.util.concurrent.TimeUnit;

/**
 * @author godotg
 * @version 3.0
 */
public class TimeOrmPersister extends AbstractOrmPersister {

    /**
     * 执行的频率
     */
    private final long rate;

    public TimeOrmPersister(EntityDef entityDef, EntityCaches<?, ?> entityCaches) {
        super(entityDef, entityCaches);
        this.rate = Long.parseLong(entityDef.getPersisterStrategy().getConfig());
        if (this.rate <= 0) {
            throw new RuntimeException(StringUtils.format("刷新频率[{}]不能小于0", rate));
        }
    }

    @Override
    public void start() {
        SchedulerBus.scheduleAtFixedRate(() -> {
            if (!OrmContext.isStop()) {
                EventBus.execute(Bus.RANDOM_THREAD, entityDef.getClazz().hashCode(), () -> entityCaches.persistAll());
            }
        }, rate, TimeUnit.MILLISECONDS);
    }

}
