/*
 * Copyright 2000-2012 JetBrains s.r.o.
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
package com.intellij.designer.designSurface.tools;

import com.intellij.designer.designSurface.EditableArea;
import com.intellij.designer.model.RadComponent;
import com.intellij.designer.propertyTable.InplaceContext;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import org.jetbrains.annotations.Nullable;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * @author Alexander Lobas
 */
public class SelectionTool extends InputTool {
  private InputTool myTracker;

  @Override
  public void deactivate() {
    deactivateTracker();
    super.deactivate();
  }

  @Override
  public void refreshCursor() {
    if (myTracker == null) {
      super.refreshCursor();
    }
  }

  @Override
  protected void handleButtonDown(int button) {
    if (myState == STATE_INIT) {
      myState = STATE_DRAG;
      deactivateTracker();

      if (!myArea.isTree()) {
        if (myInputEvent.isAltDown()) {
          setTracker(new MarqueeTracker());
          return;
        }

        InputTool tracker = myArea.findTargetTool(myCurrentScreenX, myCurrentScreenY);
        if (tracker != null) {
          setTracker(tracker);
          if (tracker instanceof ResizeTracker) {
            myArea.showSelection(false);
          }
          return;
        }
      }

      RadComponent component = myArea.findTarget(myCurrentScreenX, myCurrentScreenY, null);
      if (component == null) {
        if (!myArea.isTree()) {
          setTracker(new MarqueeTracker());
        }
      }
      else {
        setTracker(component.getDragTracker());
      }
    }
  }

  @Override
  protected void handleButtonUp(int button) {
    myState = STATE_INIT;
    setTracker(null);
    handleMove(); // hack: update cursor
  }

  @Override
  protected void handlePopup() {
    ActionManager actionManager = ActionManager.getInstance();
    ActionPopupMenu popupMenu = actionManager.createActionPopupMenu(myArea.getPopupPlace(), myArea.getPopupActions());
    MouseEvent event = (MouseEvent)myInputEvent;
    popupMenu.getComponent().show(myArea.getNativeComponent(), event.getX(), event.getY());
  }

  @Override
  protected void handleDoubleClick(int button) {
    if (button == MouseEvent.BUTTON1 && myToolProvider != null && !myArea.isTree()) {
      myToolProvider.startInplaceEditing(null);
    }
  }

  @Override
  protected void handleMove() {
    if (myState == STATE_INIT) {
      InputTool tracker = myArea.findTargetTool(myCurrentScreenX, myCurrentScreenY);
      if (tracker == null) {
        refreshCursor();
        myArea.setDescription(null);
      }
      else {
        myArea.setCursor(tracker.getDefaultCursor());
        myArea.setDescription(tracker.getDescription());
      }
    }
  }

  private void setTracker(@Nullable InputTool tracker) {
    if (myTracker != tracker) {
      deactivateTracker();
      myTracker = tracker;
      refreshCursor();

      if (myTracker != null) {
        myToolProvider.hideInspections();
        myTracker.setToolProvider(myToolProvider);
        myTracker.setArea(myArea);
        myTracker.activate();
      }
    }
  }

  private void deactivateTracker() {
    if (myTracker != null) {
      myTracker.deactivate();
      myTracker = null;
      if (myArea != null) {
        myArea.showSelection(true);
      }
    }
  }

  @Override
  public void mouseDown(MouseEvent event, EditableArea area) throws Exception {
    super.mouseDown(event, area);
    if (myTracker != null) {
      myTracker.mouseDown(event, area);
    }
  }

  @Override
  public void mouseUp(MouseEvent event, EditableArea area) throws Exception {
    if (myTracker != null) {
      myTracker.mouseUp(event, area);
    }
    super.mouseUp(event, area);
  }

  @Override
  public void mouseMove(MouseEvent event, EditableArea area) throws Exception {
    if (myTracker != null) {
      myTracker.mouseMove(event, area);
    }
    super.mouseMove(event, area);
  }

  @Override
  public void mouseDrag(MouseEvent event, EditableArea area) throws Exception {
    if (myTracker != null) {
      myTracker.mouseDrag(event, area);
    }
    super.mouseDrag(event, area);
  }


  @Override
  public void mousePopup(MouseEvent event, EditableArea area) throws Exception {
    if (myTracker != null) {
      myTracker.mousePopup(event, area);
    }
    super.mousePopup(event, area);
  }

  @Override
  public void mouseDoubleClick(MouseEvent event, EditableArea area) throws Exception {
    super.mouseDoubleClick(event, area);
    if (myTracker != null) {
      myTracker.mouseDoubleClick(event, area);
    }
  }

  @Override
  public void keyPressed(KeyEvent event, EditableArea area) throws Exception {
    if (myTracker != null) {
      myTracker.keyPressed(event, area);
    }
    else if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
      List<RadComponent> selection = area.getSelection();
      if (!selection.isEmpty()) {
        RadComponent component = selection.get(0).getParent();
        if (component != null) {
          area.select(component);
        }
      }
    }
  }

  @Override
  public void keyTyped(KeyEvent event, EditableArea area) throws Exception {
    if (myTracker != null) {
      myTracker.keyTyped(event, area);
    }
    else if (myToolProvider != null && !area.isTree() &&
             Character.isLetterOrDigit(event.getKeyChar()) &&
             (event.getModifiers() & (InputEvent.ALT_MASK | InputEvent.CTRL_MASK | InputEvent.META_MASK)) == 0) {
      myToolProvider.startInplaceEditing(new InplaceContext(event.getKeyChar()));
    }
  }

  @Override
  public void keyReleased(KeyEvent event, EditableArea area) throws Exception {
    if (myTracker != null) {
      myTracker.keyReleased(event, area);
    }
  }
}