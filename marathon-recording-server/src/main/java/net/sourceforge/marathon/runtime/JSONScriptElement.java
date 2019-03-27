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
package net.sourceforge.marathon.runtime;

import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import org.json.JSONObject;

import net.sourceforge.marathon.runtime.api.IScriptElement;
import net.sourceforge.marathon.runtime.api.Indent;
import net.sourceforge.marathon.runtime.api.RecordingScriptModel;
import net.sourceforge.marathon.runtime.api.WindowId;

public class JSONScriptElement implements IScriptElement {

    public static final Logger LOGGER = Logger.getLogger(JSONScriptElement.class.getName());

    private final WindowId windowId;
    private final JSONObject event;
    private final String name;
    private static final long serialVersionUID = 1L;

    public JSONScriptElement(WindowId windowId, String name, JSONObject event) {
        this.windowId = windowId;
        this.event = event;
        this.name = name;
    }

    @Override
    public String toScriptCode() {
        if (event.getString("type").equals("key_raw")) {
            return enscriptKeystroke();
        } else if (event.getString("type").equals("click_raw")) {
            return enscriptRawMouseClick();
        } else if (event.getString("type").equals("click")) {
            return enscriptMouseClick();
        } else if (event.getString("type").equals("select")) {
            return enscriptSelect();
        } else if (event.getString("type").equals("select_menu")) {
            return enscriptSelectMenu();
        } else if (event.getString("type").equals("menu_item")) {
            return enscriptMenuItem();
        } else if (event.getString("type").equals("assert") || event.getString("type").equals("wait")) {
            return enscriptAssert(event.getString("type"));
        } else if (event.getString("type").equals("window_closed")) {
            return enscriptWindowClosed();
        } else if (event.getString("type").equals("window_state")) {
            return enscriptWindowState();
        } else if (event.getString("type").equals("hover")) {
            return enscriptHover();
        }
        return "on '" + name + "' " + event.toString(4);
    }

    private String enscriptWindowClosed() {
        return Indent.getIndent()
                + RecordingScriptModel.getModel().getScriptCodeForGenericAction("window_closed", "", windowId.getTitle());
    }

    private String enscriptWindowState() {
        return Indent.getIndent()
                + RecordingScriptModel.getModel().getScriptCodeForGenericAction("window_changed", "", event.getString("bounds"));
    }

    private String enscriptAssert(String type) {
        String property = event.getString("property");
        String cellinfo = null;
        if (event.has("cellinfo")) {
            cellinfo = event.getString("cellinfo");
        }
        String method = "assert_p";
        if (event.has("method")) {
            method = event.getString("method");
        } else {
            if (type.equals("wait")) {
                method = "wait_p";
            } else if (property.equalsIgnoreCase("content")) {
                method = "assert_content";
            }
        }
        Object value = event.get("value");
        if (value == null || value == JSONObject.NULL)
            value = "";
        if (method.equals("assert_content")) {
            return Indent.getIndent() + RecordingScriptModel.getModel().getScriptCodeForGenericAction(method, "", name, value);
        }
        if (cellinfo == null || "".equals(cellinfo)) {
            return Indent.getIndent()
                    + RecordingScriptModel.getModel().getScriptCodeForGenericAction(method, "", name, property, value);
        } else {
            return Indent.getIndent()
                    + RecordingScriptModel.getModel().getScriptCodeForGenericAction(method, "", name, property, value, cellinfo);
        }
    }

    private String enscriptSelect() {
        String suffix = "";
        if (event.has("suffix")) {
            suffix = "_" + event.getString("suffix");
        }
        String value = event.getString("value");
        String cellinfo = null;
        if (event.has("cellinfo")) {
            cellinfo = event.getString("cellinfo");
        }
        if (cellinfo == null) {
            return Indent.getIndent()
                    + RecordingScriptModel.getModel().getScriptCodeForGenericAction("select", suffix, name, value);
        } else {
            return Indent.getIndent()
                    + RecordingScriptModel.getModel().getScriptCodeForGenericAction("select", suffix, name, value, cellinfo);
        }
    }

    private String enscriptSelectMenu() {
        String value = event.getString("value");
        return Indent.getIndent() + RecordingScriptModel.getModel().getScriptCodeForGenericAction("select_menu", "", value);
    }

    private String enscriptMenuItem() {
        return "";
    }

    private String enscriptHover() {
        String suffix = "";
        if (event.has("suffix")) {
            suffix = "_" + event.getString("suffix");
        }
        String cellinfo = null;
        if (event.has("cellinfo")) {
            cellinfo = event.getString("cellinfo");
        }
        if (cellinfo == null) {
            return Indent.getIndent() + RecordingScriptModel.getModel().getScriptCodeForGenericAction("hover", suffix, name);
        } else {
            return Indent.getIndent()
                    + RecordingScriptModel.getModel().getScriptCodeForGenericAction("hover", suffix, name, cellinfo);
        }
    }

    private String enscriptRawMouseClick() {
        boolean popupTrigger = event.getInt("button") == MouseEvent.BUTTON3;
        int clickCount = event.getInt("clickCount");
        int x = event.getInt("x");
        int y = event.getInt("y");
        String mtext = event.getString("modifiersEx");
        String method = "click";
        if (popupTrigger) {
            method = "rightclick";
        }
        if ("".equals(mtext)) {
            return Indent.getIndent()
                    + RecordingScriptModel.getModel().getScriptCodeForGenericAction(method, "", name, clickCount, x, y);
        }
        return Indent.getIndent()
                + RecordingScriptModel.getModel().getScriptCodeForGenericAction(method, "", name, clickCount, x, y, mtext);
    }

    private String enscriptMouseClick() {
        String suffix = "";
        if (event.has("suffix")) {
            suffix = "_" + event.getString("suffix");
        }
        boolean popupTrigger = event.getInt("button") == MouseEvent.BUTTON3;
        int clickCount = event.has("clickCount") ? event.getInt("clickCount") : 1;
        String mtext = event.getString("modifiersEx");
        String cellinfo = null;
        if (event.has("cellinfo")) {
            cellinfo = event.getString("cellinfo");
        }
        if ("".equals(cellinfo)) {
            cellinfo = null;
        }
        if (popupTrigger) {
            if (clickCount == 1) {
                if ("".equals(mtext)) {
                    if (cellinfo == null) {
                        return Indent.getIndent()
                                + RecordingScriptModel.getModel().getScriptCodeForGenericAction("rightclick", suffix, name);
                    }
                    return Indent.getIndent()
                            + RecordingScriptModel.getModel().getScriptCodeForGenericAction("rightclick", suffix, name, cellinfo);
                } else {
                    if (cellinfo == null) {
                        return Indent.getIndent()
                                + RecordingScriptModel.getModel().getScriptCodeForGenericAction("rightclick", suffix, name, mtext);
                    }
                    return Indent.getIndent() + RecordingScriptModel.getModel().getScriptCodeForGenericAction("rightclick", suffix,
                            name, mtext, cellinfo);
                }
            } else {
                if ("".equals(mtext)) {
                    if (cellinfo == null) {
                        return Indent.getIndent() + RecordingScriptModel.getModel().getScriptCodeForGenericAction("rightclick",
                                suffix, name, clickCount);
                    }
                    return Indent.getIndent() + RecordingScriptModel.getModel().getScriptCodeForGenericAction("rightclick", suffix,
                            name, clickCount, cellinfo);
                } else {
                    if (cellinfo == null) {
                        return Indent.getIndent() + RecordingScriptModel.getModel().getScriptCodeForGenericAction("rightclick",
                                suffix, name, clickCount, mtext);
                    }
                    return Indent.getIndent() + RecordingScriptModel.getModel().getScriptCodeForGenericAction("rightclick", suffix,
                            name, clickCount, mtext, cellinfo);
                }
            }
        } else {
            if (clickCount == 1) {
                if ("".equals(mtext)) {
                    if (cellinfo == null) {
                        return Indent.getIndent()
                                + RecordingScriptModel.getModel().getScriptCodeForGenericAction("click", suffix, name);
                    }
                    return Indent.getIndent()
                            + RecordingScriptModel.getModel().getScriptCodeForGenericAction("click", suffix, name, cellinfo);
                } else {
                    if (cellinfo == null) {
                        return Indent.getIndent()
                                + RecordingScriptModel.getModel().getScriptCodeForGenericAction("click", suffix, name, mtext);
                    }
                    return Indent.getIndent()
                            + RecordingScriptModel.getModel().getScriptCodeForGenericAction("click", suffix, name, mtext, cellinfo);
                }
            } else {
                if ("".equals(mtext)) {
                    if (cellinfo == null) {
                        return Indent.getIndent()
                                + RecordingScriptModel.getModel().getScriptCodeForGenericAction("doubleclick", suffix, name);
                    }
                    return Indent.getIndent()
                            + RecordingScriptModel.getModel().getScriptCodeForGenericAction("doubleclick", suffix, name, cellinfo);
                } else {
                    if (cellinfo == null) {
                        return Indent.getIndent()
                                + RecordingScriptModel.getModel().getScriptCodeForGenericAction("doubleclick", suffix, name, mtext);
                    }
                    return Indent.getIndent() + RecordingScriptModel.getModel().getScriptCodeForGenericAction("doubleclick", suffix,
                            name, mtext, cellinfo);
                }
            }
        }
    }

    private String enscriptKeystroke() {
        String keytext = null;
        if (event.has("keyChar")) {
            keytext = event.getString("keyChar");
        } else {
            String mtext = event.getString("modifiersEx");
            if (mtext.length() > 0) {
                mtext = mtext + "+";
            }
            keytext = mtext + event.getString("keyCode");
        }
        return Indent.getIndent() + RecordingScriptModel.getModel().getScriptCodeForGenericAction("keystroke", "", name, keytext);
    }

    @Override
    public WindowId getWindowId() {
        return windowId;
    }

    @Override
    public IScriptElement getUndoElement() {
        return null;
    }

    @Override
    public boolean isUndo() {
        return false;
    }

    @Override
    public String toString() {
        return "JSONScriptElement [windowId=" + windowId + ", event=" + event + ", name=" + name + "]";
    }

}
