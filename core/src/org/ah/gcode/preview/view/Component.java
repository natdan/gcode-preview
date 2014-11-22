/*******************************************************************************
 * Copyright (c) 2014 Creative Sphere Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Creative Sphere - initial API and implementation
 *
 *
 *
 *******************************************************************************/
package org.ah.gcode.preview.view;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Component {

    protected int posX = 10;
    protected int posY = 10;
    protected int width;
    protected int height;
    protected boolean mouseOver;
    private MouseOverListener mouseOverListener;
    private List<Component> children;
    private Component parent;

    protected Color backgroundColor = new Color(0.6f, 0.6f, 0.7f, 0.8f);

    protected boolean visible = true;

    public Component() {
    }

    public Component(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Component(int posX, int posY, int width, int height) {
        this.width = width;
        this.height = height;
        this.posX = posX;
        this.posY = posY;
    }

    protected Component getParent() { return parent; }
    protected void setParent(Component parent) { this.parent = parent; }

    protected List<Component> getChildren() {
        if (children == null) { children = new ArrayList<Component>(); }
        return children;
    }

    protected void addChild(Component component) {
        getChildren().add(component);
        component.setParent(this);
    }

    protected boolean hasChildren() {
        return children != null && children.size() > 0;
    }

    public void dispose() {
        if (hasChildren()) {
            for (Component component : getChildren()) {
                component.dispose();
            }
        }
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Color getBackgroundColor() { return backgroundColor; }

    public void setPosition(int x, int y) {
        if (x != posX || y != posY) {
            setX(x);
            setY(y);
        }
    }

    public void setX(int posX) { this.posX = posX; }
    public void setY(int posY) { this.posY = posY; }
    public int getX() { return posX; }
    public int getY() { return posY; }

    public void setSize(int width, int height) {
        if (width != getWidth() || height != getHeight()) {
            setWidth(width);
            setHeight(height);
        }
    }

    public void layout() {
        if (parent != null) {
            parent.layout();
        } else {
            doLayout();
        }
    }

    protected void doLayout() {
    }

    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void render(SpriteBatch spriteBatch) {
        if (hasChildren()) {
            for (Component component : getChildren()) {
                if (component.isVisible()) {
                    component.render(spriteBatch);
                }
            }
        }
    }

    public boolean isVisible() {
        return visible ;
    }

    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;
            layout();
        }
    }

    public void registerMouseOverListener(MouseOverListener mouseOverListener) {
        this.mouseOverListener = mouseOverListener;
    }

    private Component selectedComponent;
    private Component mouseMoveComponent;

    public boolean receiveTouchDown(int screenX, int screenY, int pointer, int button) {
        if (hasChildren()) {
            for (Component component : getChildren()) {
                if (component.getX() <= screenX && component.getX() + component.getWidth() >= screenX
                        && component.getY() <= screenY && component.getY() + component.getHeight() >= screenY) {
                    selectedComponent = component;
                    boolean res = component.receiveTouchDown(screenX, screenY, pointer, button);
                    if (res) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean receiveTouchUp(int screenX, int screenY, int pointer, int button) {
        boolean res = false;
        if (selectedComponent != null) {
            res = selectedComponent.receiveTouchUp(screenX, screenY, pointer, button);
            selectedComponent = null;
        }
        if (!res && hasChildren()) {
            for (Component component : getChildren()) {
                if (component.getX() <= screenX && component.getX() + component.getWidth() >= screenX
                        && component.getY() <= screenY && component.getY() + component.getHeight() >= screenY) {
                    component.receiveTouchUp(screenX, screenY, pointer, button);
                    if (res) {
                        return true;
                    }
                }
            }
        }
        return res;
    }

    public boolean receiveTouchDragged(int screenX, int screenY, int pointer) {
        boolean res = false;
        if (selectedComponent != null) {
            res = selectedComponent.receiveTouchDragged(screenX, screenY, pointer);
        }
        if (!res && hasChildren()) {
            for (Component component : getChildren()) {
                if (component.getX() <= screenX && component.getX() + component.getWidth() >= screenX
                        && component.getY() <= screenY && component.getY() + component.getHeight() >= screenY) {
                    res = component.receiveTouchDragged(screenX, screenY, pointer);
                    if (res) {
                        return true;
                    }
                }
            }
        }
        return res;
    }

    public boolean receiveMouseMoved(int screenX, int screenY) {
        boolean res = false;

        if (getX() <= screenX && getX() + getWidth() >= screenX
                && getY() <= screenY && getY() + getHeight() >= screenY) {
            if (!mouseOver) {
                mouseOver = true;
                if (mouseOverListener != null) {
                    mouseOverListener.mouseOver(this, screenX, screenY, true);
                }
            }
        } else {
            if (mouseOver) {
                mouseOver = false;
                if (mouseOverListener != null) {
                    mouseOverListener.mouseOver(this, screenX, screenY, false);
                }
            }
        }
        if (selectedComponent != null) {
            res = selectedComponent.receiveMouseMoved(screenX, screenY);
        }
        if (mouseMoveComponent != null) {
            res = mouseMoveComponent.receiveMouseMoved(screenX, screenY);
        }
        if (!res && hasChildren()) {
            for (Component component : getChildren()) {
                if (component.getX() <= screenX && component.getX() + component.getWidth() >= screenX
                        && component.getY() <= screenY && component.getY() + component.getHeight() >= screenY) {
                    if (mouseMoveComponent != null) {
                        mouseMoveComponent = null;
                    }
                    res = component.receiveMouseMoved(screenX, screenY);
                    mouseMoveComponent = component;
                    if (res) {
                        return true;
                    }
                }
            }
        }
        return res;
    }

    public boolean receiveScroll(int screenX, int screenY, int amount) {
        boolean res = false;
        if (selectedComponent != null) {
            res = selectedComponent.receiveScroll(screenX, screenY, amount);
        }
        if (!res && hasChildren()) {
            for (Component component : getChildren()) {
                if (component.getX() <= screenX && component.getX() + component.getWidth() >= screenX
                        && component.getY() <= screenY && component.getY() + component.getHeight() >= screenY) {
                    res = component.receiveScroll(screenX, screenY, amount);
                    if (res) {
                        return true;
                    }
                }
            }
        }
        return res;
    }

    public static interface MouseOverListener {
        void mouseOver(Component component, int x, int y, boolean mouseOver);
    }
}
