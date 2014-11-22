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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;

public class HorizontalSlider extends Slider {

    private Texture leftTexture;
    private Texture rightTexture;
    private Texture horizontal;

    public HorizontalSlider(TextureProvider textureProvider) {
        super(textureProvider);
        leftTexture = textureProvider.load("gui/slider-end-left.png");
        rightTexture = textureProvider.load("gui/slider-end-right.png");
        horizontal = textureProvider.load("gui/slider-horizontal.png");
    }

    public void render(SpriteBatch spriteBatch) {
        int kw = knobTexture.getWidth();
        int kh = knobTexture.getHeight();
        int lw = leftTexture.getWidth();
        int lh = leftTexture.getHeight();
        int hw = horizontal.getWidth();
        int hh = horizontal.getHeight();
        int rw = rightTexture.getWidth();

        int posX = getX();
        int posY = getY();
        int width = getWidth();

        int path = width - kw;

        spriteBatch.draw(leftTexture, posX, posY);
        spriteBatch.draw(rightTexture, posX + width - rw, posY);

        spriteBatch.draw(horizontal, posX + lw, posY, width - lw - rw, hh,
                0, 0, hw, hh, false, false);

        for (Knob knob : getKnobs()) {
            int x = 0;
            if (max != 0) {
                x = posX + knob.getPosition() * path / max;
            }
            int y = posY + (lh - kh) / 2;
            spriteBatch.draw(knobTexture, x, y);
        }
    }

    @Override
    public int getHeight() {
        return leftTexture.getHeight();
    }

    @Override
    public boolean receiveTouchDragged(int screenX, int screenY, int pointer) {
        int w = knobTexture.getWidth();
        int path = getWidth() - w;

        int pos = screenX - getX() - w / 2;

        if (pos < 0) {
            pos = 0;
        } else if (pos > path) {
            pos = path;
        }

        if (pos == path) {
            getKnobs().get(0).setPosition(getMax());
        } else {
            getKnobs().get(0).setPosition(pos * getMax() / path);
        }
        return true;
    }

}
