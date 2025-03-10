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

package com.zfoo.event.model.vo;

import com.zfoo.event.model.anno.Bus;
import com.zfoo.event.model.event.IEvent;

/**
 * @author godotg
 * @version 3.0
 */
public interface IEventReceiver {
    default Bus bus(){return Bus.CURRENT_THREAD;}

    default int order(){return Integer.MAX_VALUE;}

    void invoke(IEvent event);
}
