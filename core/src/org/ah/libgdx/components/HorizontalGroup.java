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
public class HorizontalGroup extends Group {

    @Override
    public void doLayout() {
        int height = 0;
        int width = 0;
        for (Component component : getChildren()) {
            if (component.isVisible() && component.getHeight() > height) {
                height = component.getHeight();
            }
        }
        for (Component component : getChildren()) {
            if (component.isVisible()) {
                component.setPosition(getX() + width, getY());
                width = width + component.getWidth() + getMargin();
            }
        }
        setSize(width, height);
    }

}
