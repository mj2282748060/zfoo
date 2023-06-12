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
package com.zfoo.event.model.anno;

import com.zfoo.thread.IThreadGroup;
import com.zfoo.thread.enums.RandomThreadGroup;
import com.zfoo.thread.enums.SingleThreadGroup;

/**
 *
 */
public enum Bus {

    CURRENT_THREAD(null),

    ROLE_THREAD(SingleThreadGroup.ROLES),

    RANDOM_THREAD(RandomThreadGroup.RANDOM),

    VirtualThread(null),
    ;

    final IThreadGroup threadGroup;

    Bus(IThreadGroup threadGroup) {
        this.threadGroup = threadGroup;
    }

    public IThreadGroup getThreadGroup() {
        return threadGroup;
    }


}
