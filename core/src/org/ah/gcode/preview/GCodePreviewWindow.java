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
package org.ah.gcode.preview;

import org.ah.gcode.preview.view.Console;
import org.ah.gcode.preview.view.HorizontalSlider;
import org.ah.gcode.preview.view.Panel;
import org.ah.gcode.preview.view.Slider;
import org.ah.gcode.preview.view.VerticalSlider;
import org.ah.gcode.preview.view.Window;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;

public class GCodePreviewWindow extends Window {

    private Console console;
    private Panel fpsPanel;
    private Panel playPanel;
    private HorizontalSlider horizontalSlider;
    private VerticalSlider verticalSlider;

    public GCodePreviewWindow(int posX, int posY, int width, int height) {
        super(posX, posY, width, height);
    }
    
    public void initialise(TextureProvider textureProvider, BitmapFont font) {
        console = new Console(font, 320, 240);
        console.println("GCode Preview 0.1a");
        console.println("(C) Abstract Horizon");
        console.println("(http://www.abstracthorizon.org");
        console.println();
        addChild(console);

        fpsPanel = new Panel(font, (int)font.getBounds("FPS: 00.00 ").width);
        fpsPanel.setPosition(3, 3);
        addChild(fpsPanel);
        
        playPanel = new Panel(font, (int)font.getBounds("Instr: 00000000/00000000").width, (int)font.getLineHeight() * 3 + 4);
        addChild(playPanel);

        horizontalSlider = new HorizontalSlider(textureProvider);
        addChild(horizontalSlider);
        verticalSlider = new VerticalSlider(textureProvider);
        addChild(verticalSlider);
        doLayout();
    }
    
    public Console getConsole() { return console; }
    public Panel getFPSPanel() { return fpsPanel; }
    public Panel getPlayPanel() { return playPanel; }
    public Slider getHorizontalSlider() { return horizontalSlider; }
    public Slider getVerticalSlider() { return verticalSlider; }

    @Override
    protected void doLayout() {
        int width = getWidth();
        int height = getHeight();
        if (console != null) {
            console.setPosition(3, height - console.getHeight() - 3);
            //console.setSize(console.getWidth(), height - 6);
        }
        if (playPanel != null) {
            playPanel.setPosition((width - playPanel.getWidth()) / 2, 3);
        }
        if (horizontalSlider != null) {
            if (console.isVisible()) {
                horizontalSlider.setPosition(console.getX() + console.getWidth() + 5, height - horizontalSlider.getHeight() - 3);
                horizontalSlider.setSize(width - console.getX() - console.getWidth() -5 - 3, height);
            } else {
                horizontalSlider.setPosition(3, height - horizontalSlider.getHeight() - 3);
                horizontalSlider.setSize(width - 6, horizontalSlider.getHeight());
            }
        }
        if (verticalSlider != null) {
            verticalSlider.setPosition(width - verticalSlider.getWidth() - 3, 3);
            verticalSlider.setSize(verticalSlider.getWidth(), horizontalSlider.getY() - 6);
        }
    }
}
