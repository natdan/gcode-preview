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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public class Panel extends Component {

    public static enum Alignment {
        Left, Right, Center;
    }

    protected Texture texture;
    protected BitmapFont font;
    protected FrameBuffer frameBuffer;
    protected OrthographicCamera camera;

    protected SpriteBatch batch;

    protected int xPadding = 2;
    protected int yPadding = 2;

    protected ModelInstance modelInstance;

    protected boolean visible = true;

    public Panel(BitmapFont font, int width) {
        this(font, width, (int)font.getLineHeight());
    }

    public Panel(BitmapFont font, int width, int xPadding, int yPadding) {
        this(font, width, (int)font.getLineHeight(), xPadding, yPadding);
    }

    public Panel(BitmapFont font, int width, int height) {
        this(font, width, height, 2, 2);
    }

    public Panel(BitmapFont font, int width, int height, int xPadding, int yPadding) {
        super(width, height);

        this.font = font;
        this.xPadding = xPadding;
        this.yPadding = yPadding;

        camera = new OrthographicCamera();

        frameBuffer = new FrameBuffer(Format.RGBA8888, width, height, false);
        texture = frameBuffer.getColorBufferTexture();

        camera.setToOrtho(true, frameBuffer.getWidth(), frameBuffer.getHeight());

        batch = new SpriteBatch();
        batch.enableBlending();
        batch.setProjectionMatrix(camera.combined);

        refresh();
    }

    public void dispose() {
        texture.dispose();
        frameBuffer.dispose();
    }

    public void addChild(Component component) {
        super.addChild(component);
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    public void render(SpriteBatch spriteBatch) {
        spriteBatch.draw(texture, posX, posY);
    }

    public void refresh() {
        frameBuffer.begin();
        Color backgroundColor = getBackgroundColor();
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        if (hasChildren()) {
            for (Component component : getChildren()) {
                component.render(batch);
            }
        }
        batch.end();
        frameBuffer.end();
    }

    @Override
    public void setSize(int width, int height) {
        if (width != getWidth() || height != getHeight()) {
            super.setSize(width, height);
            texture.dispose();
            frameBuffer.dispose();

            frameBuffer = new FrameBuffer(Format.RGBA8888, width, height, false);
            texture = frameBuffer.getColorBufferTexture();

            camera.setToOrtho(true, frameBuffer.getWidth(), frameBuffer.getHeight());
            camera.update();
            batch.setProjectionMatrix(camera.combined);

            refresh();
        }
    }

    public void text(String text, int line) {
        text(text, line, Alignment.Left);
    }

    public void text(String text, int line, Alignment alignment) {
        int x = xPadding;
        int y = yPadding + line * (int)font.getLineHeight();
        if (alignment == Alignment.Left) {
        } else if (alignment == Alignment.Right) {
            TextBounds bounds = font.getBounds(text);
            x = width - xPadding - (int)bounds.width;
        } else {
            TextBounds bounds = font.getBounds(text);
            x = (width - (int)bounds.width) / 2;
        }
        frameBuffer.begin();
        batch.begin();
        font.draw(batch, text, x, y);
        batch.end();
        frameBuffer.end();
    }
}
