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

import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 *
 * @author Daniel Sendula
 */
public class Label extends Panel {

    private String text;

    public Label(BitmapFont font, String text) {
        this(font, text, (int)font.getBounds(text).width, 2, 2);
    }

    public Label(BitmapFont font, String text, int width) {
        this(font, text, width, 2, 2);
    }

    public Label(BitmapFont font, String text, int xPadding, int yPadding) {
        this(font, text, (int)font.getBounds(text).width, xPadding, yPadding);
    }

    public Label(BitmapFont font, String text, int width, int xPadding, int yPadding) {
        super(font, width, (int)font.getLineHeight(), xPadding, yPadding);
        this.text = text;
    }

    @Override
    public void refresh() {
        super.refresh();
        if (text != null) {
            text(text, 0);
        }
    }
}
