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
    
    private int currentLayerNo = 0;

    private int totalInstructions;
    private int maxInstructionsPerLayer;
    private Context context;
    private float bedWidth = -1f;
    private float bedHeight = -1f;

    public GCodeModel() {
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
    
    public int getCurrentLayerNo() {
        return currentLayerNo;
    }    
}
