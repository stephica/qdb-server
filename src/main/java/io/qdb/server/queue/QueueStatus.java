/*
 * Copyright 2013 David Tinker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.qdb.server.queue;

/**
 * The status of a queue.
 */
public class QueueStatus {

    public enum Type { OK, WARN, ERROR }

    public final Type type;
    public final String message;
    public final long created = System.currentTimeMillis();

    public QueueStatus(Type type, String message) {
        this.type = type;
        this.message = message;
    }

    @Override
    public String toString() {
        return type + (message == null ? "" : ": " + message);
    }
}