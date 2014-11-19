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

public class VerticalSlider extends Slider {

    private Texture upTexture;
    private Texture downTexture;
    private Texture verticalTexture;

    public VerticalSlider(TextureProvider textureProvider) {
        super(textureProvider);
        upTexture = textureProvider.load("gui/slider-end-up.png");
        downTexture = textureProvider.load("gui/slider-end-down.png");
        verticalTexture = textureProvider.load("gui/slider-vertical.png");
    }

    public void render(SpriteBatch spriteBatch) {
        int kw = knobTexture.getWidth();
        int kh = knobTexture.getHeight();
        int uw = upTexture.getWidth();
        int uh = upTexture.getHeight();
        int dh = downTexture.getHeight();
        int vw = verticalTexture.getWidth();
        int vh = verticalTexture.getHeight();
        
        int posX = getX();
        int posY = getY();
        int height = getHeight();

        int path = height - kh;
        
        spriteBatch.draw(downTexture, posX, posY);
        spriteBatch.draw(upTexture, posX, posY + height - dh);
        spriteBatch.draw(verticalTexture,
                posX,
                posY + uh, 
                vw, height - uh - dh, 
                0, 0, vw, vh, false, false);
        
        for (Knob knob : getKnobs()) {
            spriteBatch.draw(knobTexture, posX + (uw - kw) / 2, posY + path - knob.getPosition() * path / max);
        }
    }

    @Override
    public int getWidth() {
        return upTexture.getWidth();
    }

    @Override
    public boolean receiveTouchDragged(int screenX, int screenY, int pointer) {
        int h = knobTexture.getHeight();
        int path = getHeight() - h;
        int pos = screenY - getY() - h / 2;
        if (pos < 0) {
            pos = 0;
        } else if (pos > path) {
            pos = path;
        }
        pos = path - pos;
        
        if (pos == path) {
            getKnobs().get(0).setPosition(getMax());
        } else {
            getKnobs().get(0).setPosition(pos * getMax() / path);
        }
        return true;
    }

}
