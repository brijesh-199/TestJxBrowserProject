/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
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
package net.sourceforge.marathon.runtime.api;

import java.util.logging.Logger;

public class ObjectComparator {

    public static final Logger LOGGER = Logger.getLogger(ObjectComparator.class.getName());

    public static int compare(Object o1, Object o2) {
        if (o1 == null ^ o2 == null) {
            return o1 == null ? -1 : 1;
        }
        if (o1 == null) {
            return 0;
        }
        return o1.equals(o2) ? 0 : -1;
    }
}
