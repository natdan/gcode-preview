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
package org.ah.gcode.preview.gcode;

import java.util.ArrayList;
import java.util.List;

public class GCodeModel {
    
    private List<Layer> layers = new ArrayList<Layer>();
    private Point min = new Point(0f, 0f, 0f);
    private Point max = new Point(0f, 0f, 0f);
    
//    private int startLayerNo = 0;
    private int currentLayerNo = 0;
//    private int visibleLayerNo = 1;
//    private long lastLayersChange = 0;
//    private long layersChangeSpeed = 100;
//    
//    private int instructionsSpeed = 1;
//    private int timeoutBeforeResun = 3000;
//    
//    private long lastPlayed = 0;
//    private boolean paused = false;
//    
//    private boolean displayTopLayers = false;
//
    private int totalInstructions;
    private int maxInstructionsPerLayer;
    private Context context;
    private float bedWidth = -1f;
    private float bedHeight = -1f;
//    
//    private Renderer playerRenderer;

    public GCodeModel() {
//        playerRenderer = new Renderer();
    }
    
    public void setBedSize(float width, float height) {
        this.bedWidth = width;
        this.bedHeight = height;
    }
    
    public float getBedWidth() {
        return bedWidth;
    }
    
    public float getBedHeight() {
        return bedHeight;
    }
    
    public List<Layer> getLayers() {
        return layers;
    }
    
    public Point getMax() {
        return max;
    }

    public Point getMin() {
        return min;
    }
    
//    public Mode getMode() {
//        return mode;
//    }
    
    public int getTotalInstrctions() {
        return totalInstructions;
    }
    
    public int getMaxInstructionsPerLayer() {
        return maxInstructionsPerLayer;
    }

    public void prepareForProcessing(Context context) {
        this.context = context;
        totalInstructions = 0;
        maxInstructionsPerLayer = 0;
        currentLayerNo = 0;
    }
    
    public boolean isProcessingFinished() {
        return currentLayerNo >= layers.size();
    }
    
    public void processNextLayer() {
        Layer layer = layers.get(currentLayerNo);
        layer.process(context);
        if (layer.getNumberOfInstructions() > maxInstructionsPerLayer) {
            maxInstructionsPerLayer = layer.getNumberOfInstructions();
        }
        currentLayerNo = currentLayerNo + 1;
        totalInstructions = totalInstructions + layer.getNumberOfInstructions();
    }

//    public void render(Camera camera, Environment environment) {
//        if (mode == Mode.CycleThroughLayers)  {
//            if (!paused) {
//                List<Layer> layers = getLayers();
//                long now = System.currentTimeMillis();
//                if (now - lastLayersChange > layersChangeSpeed) {
//                    lastLayersChange = now;
//                    if (currentLayerNo < layers.size() - 1) {
//                        currentLayerNo = currentLayerNo + 1;
//                        if (currentLayerNo - startLayerNo > visibleLayerNo) {
//                            startLayerNo = startLayerNo + 1;
//                        }
//                    } else {
//                        if (startLayerNo == currentLayerNo) {
//                            startLayerNo = 0;
//                            currentLayerNo = 0;
//                        } else {
//                            startLayerNo = startLayerNo + 1;
//                        }
//                    }
//                }
//            }
//            playerRenderer.renderProgress(camera, environment, layers, startLayerNo, currentLayerNo, displayTopLayers);
//            
//        } else if (mode == Mode.Play) {
//            if (!paused) {
//                
//                if (lastPlayed > 0) {
//                    playerRenderer.setCurrentLayer(null);
//                    long now = System.currentTimeMillis();
//                    if (now - lastPlayed > timeoutBeforeResun) {
//                        currentLayerNo = 0;
//                        playerRenderer.setCurrentInstructionNo(0);
//                        lastPlayed = 0;
//                        if (!layers.isEmpty()) {
//                            playerRenderer.setCurrentLayer(layers.get(0));
//                        }
//                    }
//                } else {
//                    if (playerRenderer.getCurrentLayer() != null) {
//                        playerRenderer.setCurrentInstructionNo(playerRenderer.getCurrentInstructionNo() + instructionsSpeed);
//                        if (playerRenderer.getCurrentInstructionNo() >= playerRenderer.getCurrentLayer().getNumberOfInstructions()) {
//                            currentLayerNo = currentLayerNo + 1;
//                            if (currentLayerNo >= layers.size()) {
//                                lastPlayed = System.currentTimeMillis();
//                                playerRenderer.setCurrentLayer(null);
//                            } else {
//                                playerRenderer.setCurrentInstructionNo(0);
//                                playerRenderer.setCurrentLayer(layers.get(currentLayerNo));
//                            }
//                        }
//                    }
//                }
//            }
//            
//            playerRenderer.renderProgress(camera, environment, layers, startLayerNo, currentLayerNo, displayTopLayers);
//        } else if (mode == Mode.TwoD) {
//            playerRenderer.renderProgress(camera, environment, layers, startLayerNo, currentLayerNo, false);
//        } else {
//            playerRenderer.renderSolid(camera, environment, layers);
//        }
//    }

//    public void startCyclingThroughLayers() {
//        startLayerNo = 0;
//        currentLayerNo = 0;
//        lastLayersChange = System.currentTimeMillis();
//        mode = Mode.CycleThroughLayers;
//    }
//
//    public void startPlaying() {
//        playerRenderer.setCurrentInstructionNo(0);
//        currentLayerNo = 0;
//        paused = false;
//        if (!layers.isEmpty()) {
//            playerRenderer.setCurrentLayer(layers.get(0));
//        }
//        mode = Mode.Play;
//    }
//    
//    public void setTwoDView() {
//        playerRenderer.setCurrentInstructionNo(0);
//        currentLayerNo = 0;
//        paused = false;
//        if (!layers.isEmpty()) {
//            playerRenderer.setCurrentLayer(layers.get(0));
//        }
//        mode = Mode.TwoD;
//    }
//    
//    public void displayAll() {
//        mode = Mode.DisplayAll;
//    }
//    
//    public boolean isPaused() {
//        return paused;
//    }
//    
//    public void setPaused(boolean paused) {
//        this.paused = paused;
//    }
//    
//    public int getInsrtuctionsSpeed() {
//        return instructionsSpeed;
//    }
//    
//    public void setInsrtuctionsSpeed(int instructionsSpeed) {
//        this.instructionsSpeed = instructionsSpeed;
//    }
//
//    public int getCurrentInstructionNo() {
//        return playerRenderer.getCurrentInstructionNo();
//    }
//
//    public int getCurrentInstructionMax() {
//        if (playerRenderer.getCurrentLayer() != null) {
//            return playerRenderer.getCurrentLayer().getNumberOfInstructions();
//        }
//        return 0;
//    }
//
//    public int getCurrentLayerNo() {
//        return currentLayerNo;
//    }
    
}
