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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;

public class Console extends Panel {

    private List<String> lines = new ArrayList<String>();
    private String currentLine = "";

    protected int x = 0;
    protected int y = 0;

    public Console(BitmapFont font, int width, int height, int paddingX, int paddingY) {
        super(font, width, height, paddingX, paddingY);

        lines.add(currentLine);
    }

    @Override
    public void refresh() {
        super.refresh();
        frameBuffer.begin();
        batch.begin();
        scrollupImpl();
        batch.end();
        frameBuffer.end();
    }

    protected void scrollupImpl() {
        Color backgroundColor = getBackgroundColor();
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        int y1 = yPadding;
        if (lines != null) {
            for (String line : lines) {
                font.draw(batch, line, xPadding, y1);
                y1 = y1 + (int)font.getLineHeight();
            }
        }
    }

    protected void printlnImpl() {
        int y1 = y + (int)font.getLineHeight();
        if (y1 + (int)font.getLineHeight() > height) {
            y1 = y;
            lines.remove(0);
            scrollupImpl();
        } else {
            y = y1;
        }
        x = xPadding;
        currentLine = "";
        lines.add(currentLine);
    }

    protected void printImpl(char c) {
        if (c  < ' ') {
            if (c == '\n') {
                printlnImpl();
            }
        } else {
            String s = Character.toString(c);
            TextBounds bounds = font.getBounds(s);
            int w = (int)bounds.width;
            if (x + w > width) {
                printlnImpl();
                x = xPadding;
            }
            font.draw(batch, s, x, y);

            x = x + w;
            currentLine = currentLine + s;
            lines.set(lines.size() - 1, currentLine);
        }
    }

    public void print(String s) {
        frameBuffer.begin();
        batch.begin();
        for (int i = 0; i < s.length(); i++) {
            printImpl(s.charAt(i));
        }
        batch.end();
        frameBuffer.end();
    }

    public void println() {
        print("\n");
    }

    public void println(String s) {
        print(s + "\n");
    }
}
