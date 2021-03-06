/*
 * Copyright 2013 by Cloudsoft Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package brooklyn.entity.messaging.kafka;

import java.util.Map;

import brooklyn.entity.Entity;
import brooklyn.entity.basic.AbstractEntity;
import brooklyn.entity.messaging.Topic;
import brooklyn.util.collections.MutableMap;

public class KafkaTopic extends AbstractEntity implements Topic {

    public KafkaTopic() {
        super(MutableMap.of(), null);
    }
    public KafkaTopic(Map properties) {
        super(properties, null);
    }
    public KafkaTopic(Entity parent) {
        super(MutableMap.of(), parent);
    }
    public KafkaTopic(Map properties, Entity parent) {
        super(properties, parent);
    }

    // kafka:type=kafka.logs.${topicName}

    @Override
    public String getTopicName() {
        return null; // TODO
    }

    @Override
    public void create() {
        // TODO
    }

    @Override
    public void delete() {
        // TODO
    }

}
