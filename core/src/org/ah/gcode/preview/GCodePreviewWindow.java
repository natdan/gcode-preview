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

import org.ah.libgdx.components.Button;
import org.ah.libgdx.components.Component;
import org.ah.libgdx.components.Console;
import org.ah.libgdx.components.HorizontalGroup;
import org.ah.libgdx.components.HorizontalSlider;
import org.ah.libgdx.components.Image;
import org.ah.libgdx.components.Label;
import org.ah.libgdx.components.Panel;
import org.ah.libgdx.components.Slider;
import org.ah.libgdx.components.VerticalGroup;
import org.ah.libgdx.components.VerticalSlider;
import org.ah.libgdx.components.Window;
import org.ah.libgdx.components.Button.ButtonClicked;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;

/**
 *
 * @author Daniel Sendula
 */
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
    private Button twoDButton;
    private Button threeDButton;

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
        addChild(fpsPanel);

        Button detailsHeaderButton = createDarkButtonHeader(font, "Details:");
        leftPanel.addChild(detailsHeaderButton);
        playPanel = new Panel(font, (int)font.getBounds("Instr: 00000000/00000000").width, (int)font.getLineHeight() * 5 + 4);
        leftPanel.addChild(playPanel);

        Button consoleHeaderButton = createDarkButtonHeader(font, "Console:");
        leftPanel.addChild(consoleHeaderButton);
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
        hideSliderPanelButton = createButton(textureProvider,"gui/three-circles", false);
        hideSliderPanelButton.registerButtonClickedListener(new ButtonClicked() {
            @Override public void buttonClicked(Button button) { leftPanel.setVisible(!leftPanel.isVisible()); }
        });

//                (Button button) -> leftPanel.setVisible(!leftPanel.isVisible())
//         );
        addChild(hideSliderPanelButton);

        horizontalSlider = new HorizontalSlider(textureProvider);
        addChild(horizontalSlider);
        verticalSlider = new VerticalSlider(textureProvider);
        addChild(verticalSlider);

        okButton = createButton(textureProvider, "gui/ok", true);
        okButton.setPreferredSize(32, 32);
        cancelButton = createButton(textureProvider, "gui/cancel", true);
        cancelButton.setVisible(false);
        cancelButton.setSize(32, 32);
        cancelButton.setPreferredSize(32, 32);

        okCancelGroup = new HorizontalGroup();
        okCancelGroup.setMargin(3);
        okCancelGroup.addChild(cancelButton);
        okCancelGroup.addChild(okButton);
        addChild(okCancelGroup);
        okCancelGroup.registerMouseOverListener(new MouseOverListener() {
            @Override public void mouseOver(Component component, int x, int y, boolean mouseOver) {
                mouseOverListener(component, x, y, mouseOver);
            }

        });
//                this::mouseOverListener);

        twoDButton = createTextButton(font, " 2D  ");
        addChild(twoDButton);

        threeDButton = createTextButton(font, " 3D  ");
        addChild(threeDButton);

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
    public Button getTwoDButton() { return twoDButton; }
    public Button getThreeDButton() { return threeDButton; }

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

            horizontalSlider.setPosition(hideSliderPanel.getX() + hideSliderPanel.getWidth() + 5, height - horizontalSlider.getHeight() - 3);
            horizontalSlider.setSize(okCancelGroup.getX() - hideSliderPanel.getX() - hideSliderPanel.getWidth() - 5 - 3, height);

            verticalSlider.setPosition(width - verticalSlider.getWidth() - 3, 3);
            verticalSlider.setSize(verticalSlider.getWidth(), okCancelGroup.getY() - 6);

            fpsPanel.setPosition(verticalSlider.getX() - 10 - fpsPanel.getWidth(), 0);

            twoDButton.setPosition(hideSliderPanel.getX() + hideSliderPanel.getWidth() + 10, 0);
            threeDButton.setPosition(twoDButton.getX() + twoDButton.getWidth() + 3, 0);
        }
    }

    public Button createButton(TextureProvider textureProvider, String buttonImageName, boolean stretched) {
        Image buttonImage = new Image(textureProvider.load(buttonImageName + ".png"));
        buttonImage.setStretchImage(stretched);

        Image mouseOverImage = new Image(textureProvider.load(buttonImageName + "-over.png"));
        mouseOverImage.setStretchImage(stretched);

        Image selectedImage = new Image(textureProvider.load(buttonImageName + "-selected.png"));
        selectedImage.setStretchImage(stretched);

        Button button = new Button(buttonImage, mouseOverImage, selectedImage);
        return button;
    }

    public Button createButton(TextureProvider textureProvider, String buttonImageName, String mouseOverImageName, String selectedImageName, boolean stretched) {
        Image buttonImage = new Image(textureProvider.load(buttonImageName));
        buttonImage.setStretchImage(stretched);
        Image mouseOverImage = null;
        if (mouseOverImageName != null) {
            mouseOverImage = new Image(textureProvider.load(mouseOverImageName));
            mouseOverImage.setStretchImage(stretched);
        }
        Image selectedImage = null;
        if (selectedImageName != null) {
            selectedImage = new Image(textureProvider.load(selectedImageName));
            selectedImage.setStretchImage(stretched);
        }
        Button button = new Button(buttonImage, mouseOverImage, selectedImage);
        return button;
    }

    public Button createDarkButtonHeader(BitmapFont font, String headerText) {
        Label textPanel = new Label(font, headerText, 3, 3);
        textPanel.setBackgroundColor(textPanel.getBackgroundColor().sub(0.2f, 0.2f, 0.2f, 0f));
        textPanel.clear();
        textPanel.text(headerText, 0);

        Label mouseOverPanel = new Label(font, headerText, 3, 3);
        mouseOverPanel.setBackgroundColor(mouseOverPanel.getBackgroundColor().add(0.2f, 0.2f, 0.2f, 0f));
        mouseOverPanel.clear();
        mouseOverPanel.text(headerText, 0);

        Label selectedPanel = new Label(font, headerText, 3, 3);
        selectedPanel.setBackgroundColor(selectedPanel.getBackgroundColor().add(0.4f, 0.4f, 0.4f, 0f));
        selectedPanel.clear();
        selectedPanel.text(headerText, 0);

        Button button = new Button(textPanel, mouseOverPanel, selectedPanel);
        return button;
    }

    public Button createTextButton(BitmapFont font, String buttonText) {
        Panel textPanel = new Panel(font, (int)font.getBounds(buttonText).width, (int)font.getLineHeight(), 3, 3);
        textPanel.setBackgroundColor(textPanel.getBackgroundColor().add(0.5f, 0.5f, 0.5f, 0f));
        textPanel.clear();
        textPanel.text(buttonText, 0);

        Panel mouseOverPanel = new Panel(font, (int)font.getBounds(buttonText).width, (int)font.getLineHeight(), 3, 3);
        mouseOverPanel.setBackgroundColor(mouseOverPanel.getBackgroundColor().add(0.2f, 0.2f, 0.2f, 0f));
        mouseOverPanel.clear();
        mouseOverPanel.text(buttonText, 0);

        Panel selectedPanel = new Panel(font, (int)font.getBounds(buttonText).width, (int)font.getLineHeight(), 3, 3);
        selectedPanel.clear();
        selectedPanel.text(buttonText, 0);

        Button button = new Button(textPanel, mouseOverPanel, selectedPanel);
        return button;
    }
}
