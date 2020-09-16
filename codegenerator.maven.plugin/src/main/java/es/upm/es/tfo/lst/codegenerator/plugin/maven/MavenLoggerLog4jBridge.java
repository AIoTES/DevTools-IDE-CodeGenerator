/*******************************************************************************
 * Copyright 2018 Universidad Politécnica de Madrid UPM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package es.upm.es.tfo.lst.codegenerator.plugin.maven;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.maven.plugin.logging.Log;

public class MavenLoggerLog4jBridge extends AppenderSkeleton {
    private Log logger;

    public MavenLoggerLog4jBridge(Log logger) {
        this.logger = logger;
    }

    protected void append(LoggingEvent event) {
        int level = event.getLevel().toInt();
        String msg = event.getMessage().toString();
        if (level == Level.DEBUG_INT || level == Level.TRACE_INT) {
            this.logger.debug(msg);
        } else if (level == Level.INFO_INT) {
            this.logger.info(msg);
        } else if (level == Level.WARN_INT) {
            this.logger.warn(msg);
        } else if (level == Level.ERROR_INT || level == Level.FATAL_INT) {
            this.logger.error(msg);
        }
    }

    public void close() {
    }

    public boolean requiresLayout() {
        return false;
    }
}
