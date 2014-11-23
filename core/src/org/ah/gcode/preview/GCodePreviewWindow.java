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

import org.ah.gcode.preview.view.Button;
import org.ah.gcode.preview.view.Component;
import org.ah.gcode.preview.view.Console;
import org.ah.gcode.preview.view.HorizontalGroup;
import org.ah.gcode.preview.view.HorizontalSlider;
import org.ah.gcode.preview.view.Panel;
import org.ah.gcode.preview.view.Slider;
import org.ah.gcode.preview.view.VerticalGroup;
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
    private Button okButton;
    private Button cancelButton;
    private HorizontalGroup okCancelGroup;
    private Panel hideSliderPanel;
    private Button hideSliderPanelButton;

    private VerticalGroup leftPanel;

    private boolean overOKCancel;
    private boolean initialised = false;

    public GCodePreviewWindow(int posX, int posY, int width, int height) {
        super(posX, posY, width, height);
    }

    public void initialise(TextureProvider textureProvider, BitmapFont font) {
        leftPanel = new VerticalGroup();
        leftPanel.setStretchLast(true);
        leftPanel.setFillWidth(true);
        addChild(leftPanel);

        fpsPanel = new Panel(font, (int)font.getBounds("FPS: 00.00 ").width);
        fpsPanel.setPosition(3, 3);
        leftPanel.addChild(fpsPanel);

        playPanel = new Panel(font, (int)font.getBounds("Instr: 00000000/00000000").width, (int)font.getLineHeight() * 3 + 4);
        leftPanel.addChild(playPanel);

        console = new Console(font, 320, 240, 5, 5);
        console.println("GCode Preview 0.1a");
        console.println("(C) Abstract Horizon");
        console.println("(http://www.abstracthorizon.org");
        console.println();
        console.setVisible(true);
        leftPanel.addChild(console);

        hideSliderPanel = new Panel(null, 16, height);
        hideSliderPanel.setBackgroundColor(hideSliderPanel.getBackgroundColor().sub(0.25f, 0.25f, 0.25f, 0f));
        hideSliderPanel.refresh();
        addChild(hideSliderPanel);
        hideSliderPanelButton = new Button(textureProvider.load("gui/three-circles-dark.png"), textureProvider.load("gui/three-circles-light.png"));
        hideSliderPanelButton.setStretchImage(false);
        hideSliderPanelButton.registerButtonClickedListener((Button button) -> leftPanel.setVisible(!leftPanel.isVisible()));
        addChild(hideSliderPanelButton);

        horizontalSlider = new HorizontalSlider(textureProvider);
        addChild(horizontalSlider);
        verticalSlider = new VerticalSlider(textureProvider);
        addChild(verticalSlider);

        okButton = new Button(textureProvider.load("gui/ok-button.png"), textureProvider.load("gui/ok-button-selected.png"));
        okButton.setPreferredSize(32, 32);
        cancelButton = new Button(textureProvider.load("gui/cancel-button.png"), textureProvider.load("gui/cancel-button-selected.png"));
        cancelButton.setVisible(false);
        cancelButton.setSize(32, 32);
        cancelButton.setPreferredSize(32, 32);

        okCancelGroup = new HorizontalGroup();
        okCancelGroup.setMargin(3);
        okCancelGroup.addChild(cancelButton);
        okCancelGroup.addChild(okButton);
        addChild(okCancelGroup);
        okCancelGroup.registerMouseOverListener(this::mouseOverListener);

        initialised = true;
        doLayout();
    }

    private void mouseOverListener(Component component, int x, int y, boolean over) {
        boolean changed = false;
        if (overOKCancel != over) {
            changed = true;
        }
        overOKCancel = over;
        if (changed) {
            doLayout();
        }
    }

    public Console getConsole() { return console; }
    public Panel getFPSPanel() { return fpsPanel; }
    public Panel getPlayPanel() { return playPanel; }
    public Slider getHorizontalSlider() { return horizontalSlider; }
    public Slider getVerticalSlider() { return verticalSlider; }
    public Button getOKButton() { return okButton; }
    public Button getCancelButton() { return cancelButton; }
    public Component getLeftPanel() { return leftPanel; }

    @Override
    protected void doLayout() {
        int width = getWidth();
        int height = getHeight();
        if (initialised) {
            leftPanel.setPosition(0, 0);
            leftPanel.setSize(320, height);
            if (leftPanel.isVisible()) {
                leftPanel.doLayout();
                hideSliderPanel.setPosition(leftPanel.getX() + leftPanel.getWidth(), 0);
            } else {
                hideSliderPanel.setPosition(0, 0);
            }
            hideSliderPanel.setSize(16, height);
            hideSliderPanelButton.setPosition(hideSliderPanel.getX(), hideSliderPanel.getY());
            hideSliderPanelButton.setSize(hideSliderPanel.getWidth(), hideSliderPanel.getHeight());
            hideSliderPanel.refresh();

//            playPanel.setPosition((width - playPanel.getWidth()) / 2, 3);

            if (overOKCancel) {
                cancelButton.setVisible(true);
                okButton.setSize(okButton.getPreferredWidth(), okButton.getPreferredHeight());
            } else {
                cancelButton.setVisible(false);
                int h = horizontalSlider.getHeight();
                okButton.setSize(okButton.getPreferredWidth() * h / okButton.getPreferredHeight(), h);
            }
            okCancelGroup.doLayout(); // Calculate width/height
            okCancelGroup.setPosition(width - okCancelGroup.getWidth() - 3, height - okCancelGroup.getHeight() - 3);
            okCancelGroup.doLayout();

//            if (console.isVisible()) {
                horizontalSlider.setPosition(hideSliderPanel.getX() + hideSliderPanel.getWidth() + 5, height - horizontalSlider.getHeight() - 3);
                horizontalSlider.setSize(okButton.getX() - hideSliderPanel.getX() - hideSliderPanel.getWidth() - 5 - 3, height);
//            } else {
//                horizontalSlider.setPosition(3, height - horizontalSlider.getHeight() - 3);
//                horizontalSlider.setSize(okCancelGroup.getX() - 6, horizontalSlider.getHeight());
//            }

            verticalSlider.setPosition(width - verticalSlider.getWidth() - 3, 3);
            verticalSlider.setSize(verticalSlider.getWidth(), okCancelGroup.getY() - 6);
        }
    }
}
