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
package org.ah.libgdx.components;

import java.util.List;

public class Window extends Component {

    public Window(int posX, int posY, int width, int height) {
        super(posX, posY, width, height);
    }

    public List<Component> getChildren() {
        return super.getChildren();
    }

    public void setSize(int width, int height) {
        if (width != getWidth() || height != getHeight()) {
            super.setSize(width, height);
            layout();
        }
    }

    public void layout() {
        doLayout();
    }

}
