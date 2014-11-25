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

/**
 *
 * @author Daniel Sendula
 */
public class Group extends Component {

    private boolean fillWidth = false;
    private boolean fillHeight = false;
    private boolean stretchLast = false;
    private int margin;

    public boolean isFillWidth() {
        return fillWidth;
    }

    public void setFillWidth(boolean fillWidth) {
        this.fillWidth = fillWidth;
    }

    public boolean isFillHeight() {
        return fillHeight;
    }

    public void setFillHeight(boolean fillHeight) {
        this.fillHeight = fillHeight;
    }

    public boolean isStretchLast() {
        return stretchLast;
    }

    public void setStretchLast(boolean stretchLast) {
        this.stretchLast = stretchLast;
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    @Override
    public void addChild(Component component) {
        getChildren().add(component);
        component.setParent(this);
    }
}
