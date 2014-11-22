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
package org.ah.gcode.preview.player;

import java.util.List;

import org.ah.gcode.preview.ExitCallback;
import org.ah.gcode.preview.GCodePreviewWindow;
import org.ah.gcode.preview.gcode.GCodeModel;
import org.ah.gcode.preview.gcode.Layer;
import org.ah.gcode.preview.utils.SceneCameraInputController;
import org.ah.gcode.preview.view.Button;
import org.ah.gcode.preview.view.Console;
import org.ah.gcode.preview.view.Panel;
import org.ah.gcode.preview.view.Slider;
import org.ah.gcode.preview.view.Slider.Knob;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;

public class Controller implements InputProcessor {

    public static enum Mode {
        Loading, DisplayAll, CycleThroughLayers, Play, TwoD;
    }

    private Mode mode = Mode.Loading;

    private ExitCallback exitCallback;

    private GCodeModel gCodeModel;
    private GCodePreviewWindow window;
    private Console console;
    private SceneCameraInputController sceneCameraInputController;

    private int pressedButton;

    private float middleX = 0;
    private float middleY = 0;

    private int mouseX;
    private int mouseY;

    private int startLayerNo = 0;
    private int currentLayerNo = 0;
    private int visibleLayerNo = 1;
    private long lastLayersChange = 0;
    private long layersChangeSpeed = 100;

    private int instructionsSpeed = 1;
    private int timeoutBeforeResun = 3000;

    private long lastPlayed = 0;
    private boolean paused = false;

    private boolean displayTopLayers = false;

    private Renderer playerRenderer;

    private Slider horizontalSlider;
    private Knob horizontalKnob;

    private Slider verticalSlider;
    private Knob verticalKnob;

    public Controller(GCodeModel gCodeModel,
            GCodePreviewWindow window,
            SceneCameraInputController sceneCameraInputController,
            ExitCallback exitCallback) {
        this.gCodeModel = gCodeModel;
        this.window = window;
        this.sceneCameraInputController = sceneCameraInputController;
        this.exitCallback = exitCallback;

        horizontalSlider = window.getHorizontalSlider();
        horizontalKnob = horizontalSlider.getKnobs().get(0);
        horizontalKnob.setPositionChangedListener(
                (Knob knob, int oldPosition, int newPosition) ->
                    playerRenderer.setCurrentInstructionNo(newPosition)
            );

        verticalSlider = window.getVerticalSlider();
        verticalKnob = verticalSlider.getKnobs().get(0);
        verticalKnob.setPositionChangedListener(
                (Knob knob, int oldPosition, int newPosition) ->
                    {
                        int layerNo = newPosition;
                        if (layerNo > gCodeModel.getLayers().size()) {
                            layerNo = gCodeModel.getLayers().size();
                        }
                        if (layerNo < 0) { layerNo = 0; }
                        setCurrentLayer(layerNo);
                    }
            );

        window.getOKButton().registerButtonClickedListener(
                (Button button) -> exitCallback.exit()
            );

        window.getCancelButton().registerButtonClickedListener(
                (Button button) -> exitCallback.exit()
            );

        console = window.getConsole();
        printHelp();
        playerRenderer = new Renderer();
        middleX = gCodeModel.getBedWidth() / 2;
        middleY = gCodeModel.getBedHeight() / 2;
        sceneCameraInputController.resetToCentre(middleX, middleY);
    }

    public void resetView() {
        middleX = (gCodeModel.getMax().x + gCodeModel.getMin().x) / 2;
        middleY = (gCodeModel.getMax().y + gCodeModel.getMin().y) / 2;
        sceneCameraInputController.resetToCentre(middleX, middleY);
    }

    protected void printHelp() {
        console.println("2 - 2d view");
        console.println("3 - 3d view");
        console.println("p - play printing");
        console.println("l - cycle through layers");
        console.println("c - toggle console on/off");
        console.println();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.ESCAPE) {
            if (exitCallback != null) {
                exitCallback.exit();
            }
        }
        if (keycode == Keys.LEFT) {
            if (getMode() == Mode.Play) {
                int speed = getInsrtuctionsSpeed();
                if (speed > 0) {
                    setInsrtuctionsSpeed(speed - 1);
                }
                return true;
            }
        } else if (keycode == Keys.RIGHT) {
            if (getMode() == Mode.Play) {
                int speed = getInsrtuctionsSpeed();
                setInsrtuctionsSpeed(speed + 1);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if (character == 'c') {
            console.setVisible(!console.isVisible());
            return true;
        } else if (character == 'l') {
            sceneCameraInputController.setThreeDimensions();
            startCyclingThroughLayers();
            return true;
        } else if (character == 'p') {
            sceneCameraInputController.setThreeDimensions();
            startPlaying();
            return true;
        } else if (character == '3' || character == 'a') {
            sceneCameraInputController.setThreeDimensions();
            displayAll();
            return true;
        } else if (character == '2') {
            setTwoDView();
            return true;
        } else if (character == 'h') {
            if (!console.isVisible()) {
                console.setVisible(true);
            }
            printHelp();
            return true;
        } else if (character == ' ') {
            setPaused(!isPaused());
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        mouseX = screenX;
        mouseY = screenY;
        boolean consumed = window.receiveTouchDown(screenX, screenY, pointer, button);
        if (!consumed && sceneCameraInputController != null) {
            sceneCameraInputController.touchDown(screenX, screenY);
            pressedButton = button;
            consumed = true;
        }
        return consumed;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        mouseX = screenX;
        mouseY = screenY;
        boolean consumed = window.receiveTouchUp(screenX, screenY, pointer, button);
        if (!consumed  && sceneCameraInputController != null) {
            sceneCameraInputController.touchUp(screenX, screenY);
            consumed = true;
        }
        return consumed;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        mouseX = screenX;
        mouseY = screenY;
        boolean consumed = window.receiveTouchDragged(screenX, screenY, pointer);
        if (!consumed  && sceneCameraInputController != null) {
            if (pressedButton == Buttons.RIGHT) {
                sceneCameraInputController.rotate(screenX, screenY);
            } else if (pressedButton == Buttons.LEFT) {
                sceneCameraInputController.translate(screenX, screenY);
            }
            consumed = true;
        }
        return consumed;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        mouseX = screenX;
        mouseY = screenY;
        boolean res = window.receiveMouseMoved(screenX, screenY);
        return res;
    }

    @Override
    public boolean scrolled(int amount) {
        boolean consumed = window.receiveScroll(mouseX, mouseY, amount);
        if (!consumed  && sceneCameraInputController != null) {
            consumed = sceneCameraInputController.zoom(-amount);
        }
        return consumed;
    }

    public void render(Camera camera, Environment environment) {
        Panel playPanel = window.getPlayPanel();
        List<Layer> layers = gCodeModel.getLayers();
        if (mode == Mode.CycleThroughLayers)  {
            if (!paused) {
                long now = System.currentTimeMillis();
                if (now - lastLayersChange > layersChangeSpeed) {
                    lastLayersChange = now;
                    if (currentLayerNo < layers.size() - 1) {
                        currentLayerNo = currentLayerNo + 1;
                        if (currentLayerNo - startLayerNo > visibleLayerNo) {
                            startLayerNo = startLayerNo + 1;
                        }
                    } else {
                        if (startLayerNo == currentLayerNo) {
                            startLayerNo = 0;
                            currentLayerNo = 0;
                        } else {
                            startLayerNo = startLayerNo + 1;
                        }
                    }
                }
            }
            playerRenderer.renderProgress(camera, environment, layers, startLayerNo, currentLayerNo, displayTopLayers);

        } else if (mode == Mode.Play) {
            if (!paused) {
                if (lastPlayed > 0) {
                    playerRenderer.setCurrentLayer(null);
                    long now = System.currentTimeMillis();
                    if (now - lastPlayed > timeoutBeforeResun) {
                        currentLayerNo = 0;
                        lastPlayed = 0;
                        if (!layers.isEmpty()) {
                            playerRenderer.setCurrentLayer(layers.get(0));
                        }
                        playerRenderer.setCurrentInstructionNo(0);
                    }
                } else {
                    if (playerRenderer.getCurrentLayer() != null) {
                        if (playerRenderer.getCurrentInstructionNo() >= playerRenderer.getCurrentLayer().getNumberOfInstructions()) {
                            currentLayerNo = currentLayerNo + 1;
                            if (currentLayerNo >= layers.size()) {
                                lastPlayed = System.currentTimeMillis();
                                playerRenderer.setCurrentLayer(null);
                            } else {
                                setCurrentLayer(currentLayerNo);
                                playerRenderer.setCurrentInstructionNo(0);
                            }
                        }
                        playerRenderer.setCurrentInstructionNo(playerRenderer.getCurrentInstructionNo() + instructionsSpeed);
                    }
                }
            }

            playerRenderer.renderProgress(camera, environment, layers, startLayerNo, currentLayerNo, displayTopLayers);
        } else if (mode == Mode.TwoD) {
            playerRenderer.renderProgress(camera, environment, layers, currentLayerNo, currentLayerNo, false);
        } else {
            setCurrentLayer(currentLayerNo);
            playerRenderer.renderProgress(camera, environment, layers, 0, currentLayerNo, displayTopLayers);
            // playerRenderer.renderSolid(camera, environment, layers);
        }

        if (mode != Mode.Loading) {
            playPanel.clear();

            if (getCurrentLayerNo() >= gCodeModel.getLayers().size()) {
                playPanel.text("Layer: ALL /" + gCodeModel.getLayers().size(), 0);
                playPanel.text("Instr: ", 1);
            } else {
                playPanel.text("Layer: " + Integer.toString(getCurrentLayerNo() + 1) + "/" + gCodeModel.getLayers().size(), 0);
                playPanel.text("Instr: " + Integer.toString(getCurrentInstructionNo()) + "/" + getCurrentInstructionMax(), 1);
            }
            if (mode == Mode.Play) {
                playPanel.text("Speed: " + Integer.toString(getInsrtuctionsSpeed()), 2);
            }
        }
    }

    public void startCyclingThroughLayers() {
        startLayerNo = 0;
        currentLayerNo = 0;
        lastLayersChange = System.currentTimeMillis();
        setMode(Mode.CycleThroughLayers);
    }

    public void startPlaying() {
        currentLayerNo = 0;
        paused = false;
        if (!gCodeModel.getLayers().isEmpty()) {
            setCurrentLayer(0);
        }
        playerRenderer.setCurrentInstructionNo(0);
        setMode(Mode.Play);
    }

    public void setTwoDView() {
        sceneCameraInputController.resetToCentre(middleX, middleY);
        sceneCameraInputController.setTwoDimensions();

        currentLayerNo = 0;
        paused = false;
        if (!gCodeModel.getLayers().isEmpty()) {
            setCurrentLayer(0);
        }
        setMode(Mode.TwoD);
    }

    public void displayAll() {
        setMode(Mode.DisplayAll);
    }

    protected Mode getMode() {
        return mode;
    }

    protected void setMode(Mode mode) {
        this.mode = mode;
//        Panel playPanel = window.getPlayPanel();
//        if (mode == Mode.Play) {
//            playPanel.setVisible(true);
//        } else {
//            playPanel.setVisible(false);
//        }
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public int getInsrtuctionsSpeed() {
        return instructionsSpeed;
    }

    public void setInsrtuctionsSpeed(int instructionsSpeed) {
        this.instructionsSpeed = instructionsSpeed;
    }

    public int getCurrentInstructionNo() {
        return playerRenderer.getCurrentInstructionNo();
    }

    public int getCurrentInstructionMax() {
        if (playerRenderer.getCurrentLayer() != null) {
            return playerRenderer.getCurrentLayer().getNumberOfInstructions();
        }
        return 0;
    }

    public int getCurrentLayerNo() {
        return currentLayerNo;
    }

    public void setCurrentLayer(int layerNo) {
        Layer layer = null;
        int layersNumber = gCodeModel.getLayers().size();
        if (getMode() == Mode.TwoD) {
            // In Two-D mode we don't want to display layer above last (no layers selected as green)
            layersNumber = gCodeModel.getLayers().size() - 1;
        }
        if (layerNo >= gCodeModel.getLayers().size()) {
            if (getMode() == Mode.TwoD) {
                layerNo = gCodeModel.getLayers().size() - 1;
            } else {
                layerNo = gCodeModel.getLayers().size();
            }
        } else {
            if (layerNo < 0) {
                layerNo = 0;
            }
            layer = gCodeModel.getLayers().get(layerNo);
        }
        currentLayerNo = layerNo;
        playerRenderer.setCurrentLayer(layer);
        if (layer != null) {
            horizontalSlider.setMax(layer.getNumberOfInstructions());
            horizontalKnob.setPosition(horizontalSlider.getMax());
            playerRenderer.setCurrentInstructionNo(horizontalSlider.getMax());
        }
        verticalSlider.setMax(layersNumber);
        verticalKnob.setPosition(layerNo);
    }
}
