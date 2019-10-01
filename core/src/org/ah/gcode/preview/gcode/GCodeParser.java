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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Daniel Sendula
 */
public class GCodeParser {

    public static enum DetectedSlicer {
        Unknown,
        RoboxUnknow,
        RoboxSlic3r,
        Slic3r
    }

    private List<String> lines;
    private GCodeModel resultModel;

    private Point currentPosition;
    private Layer currentLayer;
    private int currentLine = 0;

    private boolean moveRelative;
    private boolean extrudeRelative;
    private boolean dcExtrude;
    private boolean assumeNonDC;
    private boolean extrude;
    private int retract;
    private float prev_extrude_abs;
    private float lastF;
    private boolean volPerMMDefined;
    private float volPerMM;
    private float nozzleSize;
    private Map<String, FloatBox> prev_extrude;
    private Map<String, IntBox> prevRetract;

    private boolean gotCoordinates;
    private float z;
    private float y;
    private float x;

    private DetectedSlicer detectedSlicer;

    public GCodeParser() {
    }

    public GCodeModel parse(List<String> lines) {
        initParsing(lines);
        for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
            String line = lines.get(lineNumber);
            parseLine(line, lineNumber);
        }
        return resultModel;
    }

    public void initParsing(List<String> lines) {
        this.lines = lines;

        resultModel = new GCodeModel();
        currentPosition = new Point();

        detectedSlicer = DetectedSlicer.Unknown;

        moveRelative = false;
        extrudeRelative = false;
        dcExtrude = false;
        assumeNonDC = false;
        extrude = false;
        retract = 0;
        prev_extrude_abs = 0f;

        lastF = 0f;

        volPerMMDefined = false;
        volPerMM = 0f;
        nozzleSize = 0.3f;

        x = currentPosition.x;
        y = currentPosition.y;
        z = currentPosition.z;
        gotCoordinates = false;
        currentLine = 0;

        prev_extrude = new HashMap<String, GCodeParser.FloatBox>();
        prevRetract = new HashMap<String, GCodeParser.IntBox>();
        prev_extrude.put("abs", new FloatBox(0));

    }

    public boolean parseNextLine() {
        parseLine(lines.get(currentLine), currentLine + 1);
        currentLine = currentLine + 1;
        return isFinished();
    }

    public boolean isFinished() {
        return currentLine >= lines.size();
    }

    public GCodeModel getModel() {
        return resultModel;
    }

    public int getCurrentLine() {
        return currentLine;
    }

    public List<String> getLines() {
        return lines;
    }
    public void parseLine(String line, int lineNumber) {

        String[] lineParts = line.split(";");

        if (lineParts.length > 0) {

            if (lineParts.length > 1) {
                if (detectedSlicer == DetectedSlicer.Unknown) {
                    String comment = lineParts[1];
                    if (comment.contains("Roboxiser")) {
                        detectedSlicer = DetectedSlicer.RoboxUnknow;
                        resultModel.setBedSize(210f, 150f);
                    } else if (comment.contains("Slic3r")) {
                        detectedSlicer = DetectedSlicer.Slic3r;
                    }
                } else if (detectedSlicer == DetectedSlicer.RoboxUnknow) {
                    String comment = lineParts[1];
                    if (comment.contains("Slic3r")) {
                        detectedSlicer = DetectedSlicer.RoboxSlic3r;
                        // Detected Robox with Slic3r - clear all previous movements and layers.
                        resultModel.getLayers().clear();
                        currentPosition.z = 0f;
                        currentLayer = null;
                    }
                }
            }
            line = lineParts[0].trim();

            gotCoordinates = false;
            retract = 0;

            extrude = false;
            prev_extrude_abs = 0f;

            String[] args = line.toUpperCase().split(" ");

            String command = args[0];
            if ("G0".equals(command) || "G00".equals(command)
                    || "G1".equals(command) || "G01".equals(command)) {

                for (String a : args) {
                    char c = a.charAt(0);
                    if (c == 'X') {
                        x = Float.parseFloat(a.substring(1));
                        gotCoordinates = true;
                    } else if (c == 'Y') {
                        y = Float.parseFloat(a.substring(1));
                        gotCoordinates = true;
                    } else if (c == 'Z') {
                        z = Float.parseFloat(a.substring(1));
                        gotCoordinates = true;
                    } else if (c == 'E' || c == 'A' || c == 'B' || c == 'C') {
                        FloatBox prev_extrude_argChar = prev_extrude.get(Character.toString(c));
                        if (prev_extrude_argChar == null) {
                            prev_extrude_argChar = new FloatBox();
                            prev_extrude.put(Character.toString(c), prev_extrude_argChar);
                        }

                        IntBox prevRetract_extruder = prevRetract.get(Character.toString(c));
                        if (prevRetract_extruder == null) {
                            prevRetract_extruder = new IntBox();
                            prevRetract.put(Character.toString(c), prevRetract_extruder);
                        }

                        assumeNonDC = true;
                        float extrudeValue = Float.parseFloat(a.substring(1));

                        if (!extrudeRelative) {
                            // absolute extrusion positioning
                            // prev_extrude["abs"] = parseFloat(numSlice)-parseFloat(prev_extrude[argChar]);
                            prev_extrude_abs = extrudeValue - prev_extrude_argChar.f;
                        } else {
                            // prev_extrude["abs"] = parseFloat(numSlice);
                            prev_extrude_abs = extrudeValue;
                        }

                        extrude = prev_extrude_abs > 0f;
                        if (prev_extrude_abs < 0f) {
                            prevRetract_extruder.i = -1;
                            retract = -1;
                        } else if (prev_extrude_abs == 0f) {
                            retract = 0;
                        } else if (prev_extrude_abs > 0f && prevRetract_extruder.i < 0f) {
                            prevRetract_extruder.i = 0;
                            retract = 1;
                        } else {
                            retract = 0;
                        }
                        prev_extrude_argChar.f = extrudeValue;
                    } else if (c == 'F') {
                        lastF = Float.parseFloat(a.substring(1));
                    }
                }

                if (gotCoordinates) {
                    if (moveRelative) {
                        x = x + currentPosition.x;
                        y = y + currentPosition.y;
                        z = z + currentPosition.z;
                    }
                    if (x == currentPosition.x && y == currentPosition.y && z != currentPosition.z) {
                    } else {
                        Point target = new Point(x, y, z);
                        if (dcExtrude && !assumeNonDC) {
                            extrude = true;
                            prev_extrude_abs =
                                    (float)Math.sqrt((currentPosition.x - x) * (currentPosition.x - x)
                                            + (currentPosition.y - y)*(currentPosition.y - y));
                        }
                        if (extrude && retract == 0) {
                            volPerMM = prev_extrude_abs / (float)Math.sqrt((currentPosition.x - x) * (currentPosition.x - x)
                                    + (currentPosition.y - y)*(currentPosition.y - y));
                            volPerMMDefined = true;
                        }

                        GCodeMovement code = new GCodeMovement(lineNumber, currentPosition, target, extrude, nozzleSize);
                        addToLayer(code);
                        currentPosition = target;
                    }
                } else {
                }
            } else if ("M82".equals(command)) {
                extrudeRelative = false;
            } else if ("M83".equals(command)) {
                extrudeRelative = true;
            } else if ("G91".equals(command)) {
                moveRelative = true;
                // extrudeRelative = true;
            } else if ("G90".equals(command)) {
                moveRelative = false;
                // extrudeRelative = false;
            } else if ("M101".equals(command)) {
                dcExtrude = true;
            } else if ("M103".equals(command)) {
                dcExtrude = false;
            } else if ("T0".equals(command)) {
                if (detectedSlicer == DetectedSlicer.RoboxSlic3r) {
                    nozzleSize = 0.3f;
                }
            } else if ("T1".equals(command)) {
                if (detectedSlicer == DetectedSlicer.RoboxSlic3r) {
                    nozzleSize = 0.8f;
                }
            }
        }
    }

    protected Point parsePosition(String[] args) {
        float x = currentPosition.x;
        float y = currentPosition.y;
        float z = currentPosition.z;
        boolean gotCoordinate = false;
        for (String a : args) {
            char c = a.charAt(0);
            if (c == 'X') {
                x = Float.parseFloat(a.substring(1));
                gotCoordinate = true;
            }
            if (c == 'Y') {
                y = Float.parseFloat(a.substring(1));
                gotCoordinate = true;
            }
            if (c == 'Z') {
                z = Float.parseFloat(a.substring(1));
                gotCoordinate = true;
            }
        }
        if (gotCoordinate) {
            return new Point(x, y, z);
        } else {
            return null;
        }
    }

    protected void addToLayer(GCodeMovement code) {
        float z = currentPosition.z;

        GCodeMovement movement = code;
        Point target = movement.getTargetPoint();
        z = target.z;

        resultModel.getMin().updateMin(target);
        resultModel.getMax().updateMax(target);
        if (currentLayer != null) {
            currentLayer.getMin().updateMin(target);
            currentLayer.getMax().updateMax(target);
        }

        if (currentLayer == null) {
            currentLayer = new Layer(z, z);
            resultModel.getLayers().add(currentLayer);
        }
        if (currentLayer.getZOffset() != z) {
            float layerHeight = z - currentLayer.getZOffset();
            if (layerHeight < 0f) {
                layerHeight = z;
            }
            if (layerHeight > 0.5f) {
                System.out.println("Really high layer: " + z + " height " + layerHeight);
            }
            currentLayer = new Layer(z, layerHeight);
            resultModel.getLayers().add(currentLayer);
        }
        currentLayer.addMovement(code);
    }

    private static class FloatBox {
        float f;

        FloatBox() {}
        FloatBox(float f) { this.f = f; }
    }

    private static class IntBox {
        int i;

        IntBox() {}
        IntBox(int i) { this.i = i; }
    }
}
