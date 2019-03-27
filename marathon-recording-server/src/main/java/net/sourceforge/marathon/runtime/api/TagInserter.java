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

public class TagInserter {

    public static final Logger LOGGER = Logger.getLogger(TagInserter.class.getName());

    private CompositeScriptElement rootTag = new TestRootElement(null);
    private CompositeScriptElement currentContainer = null;

    public CompositeScriptElement getRootTag() {
        return rootTag;
    }

    public void add(CompositeScriptElement container, IScriptElement recordable) {
        if (recordable != null && undo(recordable)) {
            return;
        }
        addTagContainer(currentContainer, container);
        if (recordable != null && recordable.toScriptCode() != null) {
            currentContainer.add(recordable);
        }
    }

    public void add(IScriptElement recordable) {
        if (recordable != null && undo(recordable)) {
            return;
        }
        if (currentContainer == null) {
            rootTag.add(recordable);
        } else {
            currentContainer.add(recordable);
        }
    }

    private boolean undo(IScriptElement recordable) {
        if (recordable.isUndo()) {
            IScriptElement element = last();
            if (element != null && element.equals(recordable.getUndoElement())) {
                currentContainer.getChildren().removeLast();
            }
            return true;
        }
        return false;
    }

    private void addTagContainer(CompositeScriptElement oldContainer, CompositeScriptElement newContainer) {
        if (oldContainer == null) {
            if (!newContainer.equals(rootTag.getChildren().last())) {
                rootTag.add(newContainer);
                currentContainer = newContainer;
            } else {
                currentContainer = (WindowElement) rootTag.getChildren().last();
            }
        } else if (oldContainer.owns(newContainer)) {
            if (!newContainer.equals(oldContainer.getChildren().last())) {
                oldContainer.add(newContainer);
                currentContainer = newContainer;
            } else {
                currentContainer = (WindowElement) oldContainer.getChildren().last();
            }
        } else {
            addTagContainer(getParent(oldContainer), newContainer);
        }
    }

    private CompositeScriptElement getParent(CompositeScriptElement container) {
        CompositeScriptElement parent = (CompositeScriptElement) rootTag.getChildren().last();
        if (parent.equals(container)) {
            return null;
        }
        while (true) {
            WindowElement child = (WindowElement) parent.getChildren().last();
            if (child.equals(container)) {
                return parent;
            }
            parent = child;
        }
    }

    private IScriptElement last() {
        if (currentContainer == null) {
            return null;
        }
        return currentContainer.getChildren().last();
    }
}
