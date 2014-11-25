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
 *******************************************************************************/
package org.ah.libgdx.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 *
 * @author Daniel Sendula
 */
public class Button extends Component {

    private Component unselected;
    private Component mouseOverComponent;
    private Component selected;

    private int preferredWidth;
    private int preferredHeight;

    private ButtonClicked buttonClickedListener;

    private boolean mousePressed;
    private boolean toggled;

    public Button(Component unselected, Component mouseOverComponent, Component selected) {
        this.unselected = unselected;
        this.mouseOverComponent = mouseOverComponent;
        this.selected = selected;

        if (unselected != null) {
            preferredWidth = Math.max(preferredWidth, unselected.getWidth());
            preferredHeight = Math.max(preferredHeight, unselected.getHeight());
        }
        if (mouseOverComponent != null) {
            preferredWidth = Math.max(preferredWidth, mouseOverComponent.getWidth());
            preferredHeight = Math.max(preferredHeight, mouseOverComponent.getHeight());
        }
        if (selected != null) {
            preferredWidth = Math.max(preferredWidth, selected.getWidth());
            preferredHeight = Math.max(preferredHeight, selected.getHeight());
        }

        if (preferredWidth > 0 && preferredHeight > 0) {
            setSize(getPreferredWidth(), getPreferredHeight());
        }
    }

    public void setPreferredSize(int preferredWidth, int preferredHeight) {
        this.preferredWidth = preferredWidth;
        this.preferredHeight = preferredHeight;
    }

    public int getPreferredWidth() {
        return preferredWidth;
    }

    public int getPreferredHeight() {
        return preferredHeight;
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        if (unselected != null) { unselected.setPosition(x, y); }
        if (mouseOverComponent != null) { mouseOverComponent.setPosition(x, y); }
        if (selected != null) { selected.setPosition(x, y); }
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        if (unselected != null) { unselected.setSize(width, height); }
        if (mouseOverComponent != null) { mouseOverComponent.setSize(width, height); }
        if (selected != null) { selected.setSize(width, height); }
    }

    public void render(SpriteBatch spriteBatch) {
        Component displayComponent;
        if (toggled) {
            displayComponent = selected;
        } else {
            displayComponent = unselected;
        }

        if (mousePressed && selected != null) {
            if (toggled) {
                displayComponent = unselected;
            } else {
                displayComponent = selected;
            }
        } else if (mouseOver && mouseOverComponent != null) {
            displayComponent = mouseOverComponent;
        }

        displayComponent.render(spriteBatch);
    }

    public void registerButtonClickedListener(ButtonClicked buttonClickedListener) {
        this.buttonClickedListener = buttonClickedListener;
    }

    public static interface ButtonClicked {
        void buttonClicked(Button button);
    }

    public boolean receiveTouchDown(int screenX, int screenY, int pointer, int button) {
        mousePressed = true;
        return true;
    }

    public boolean receiveTouchUp(int screenX, int screenY, int pointer, int button) {
        mousePressed = false;
        if (getX() <= screenX && getX() + getWidth() >= screenX
                && getY() <= screenY && getY() + getHeight() >= screenY) {
            if (buttonClickedListener != null) {
                buttonClickedListener.buttonClicked(this);
            }
        }
        return true;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }

    public boolean getToggled() {
        return toggled;
    }
}
