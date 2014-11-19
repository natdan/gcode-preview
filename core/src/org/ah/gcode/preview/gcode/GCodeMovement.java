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

import org.ah.gcode.preview.gcode.Layer.MeshDetails;

import com.badlogic.gdx.math.Vector3;


public class GCodeMovement {
    
    public static float MOVE_WIDTH = 0.4f;
    public static float MOVE_HEIGHT = 0.4f;
    
    public static int ELEMENTS_PER_VERTICE = 12;
    public static int VERTICES_PER_CUBE = 8;
    
    public static int ELEMENTS_PER_INDICE = 6;
    public static int INDICES_PER_CUBE = 12;
    
    public static int ROUND_SECTIONS = 3;

    public static final Vector3 UP = new Vector3(0f, 1f, 0f);
    public static final Vector3 DOWN = new Vector3(0f, -1f, 0f);

    private int lineNumber;
    private Point source;
    private Point target;
    private boolean extrude;
    private float nozzleSize;
    private int ordinaryNumber;
    private Vector3 startPoint;
    private Vector3 endPoint;
    private Vector3 leftEnd;
    private Vector3 rightEnd;
    private Vector3 leftStart;
    private Vector3 rightStart;
    private Vector3 ortDirection; // orthogonal line to start - end direction - in XY plane only.
    
    public GCodeMovement(int lineNumber, Point source, Point target, boolean extrude, float nozzleSize) {
        this.lineNumber = lineNumber;
        
        this.source = source;
        this.target = target;
        this.extrude = extrude;
        this.nozzleSize = nozzleSize;
    }
    
    public int getLineNUmber() {
        return lineNumber;
    }
    
    public void dispose() { }

    public Point getSourcePoint() {
        return source;
    }

    public Point getTargetPoint() {
        return target;
    }
    
    public boolean isExtrude() {
        return extrude;
    }
    
    public float getNozzleSize() {
        return nozzleSize;
    }
    
    public int getVerticeElementsPerSegment() {
        if (isExtrude()) {
            return (VERTICES_PER_CUBE + ROUND_SECTIONS * 2 * 2) * ELEMENTS_PER_VERTICE;
        } else {
            return VERTICES_PER_CUBE * ELEMENTS_PER_VERTICE;
        }
    }
    
    public int getIndiceElementsPerSegment() {
        if (isExtrude()) {
            return (INDICES_PER_CUBE + ROUND_SECTIONS * 2 * 2) * ELEMENTS_PER_INDICE;
        } else {
            return INDICES_PER_CUBE * ELEMENTS_PER_INDICE;
        }
    }

    public void process(int ordinaryNumber, MeshDetails meshDetails, float layerHeight, GCodeMovement previousMovement) {
        
        this.ordinaryNumber = ordinaryNumber;

        startPoint = new Vector3((float)getSourcePoint().x, (float)getSourcePoint().z, (float)getSourcePoint().y);
        endPoint = new Vector3((float)getTargetPoint().x, (float)getTargetPoint().z, (float)getTargetPoint().y);
        
        if (!isExtrude()) {
            makeMoveMesh(meshDetails, MOVE_WIDTH, MOVE_HEIGHT);
        } else {
            makeExtrudeMesh(meshDetails, layerHeight);
        }
    }

    protected void makeMoveMesh(MeshDetails meshDetails, float width, float height) {
        
        Vector3 cornerPoint = new Vector3();
        Vector3 normal = new Vector3();
        
        float halfHeight = height / 2f;
        float flatWidth = width / 2f;

        ortDirection = new Vector3(endPoint).sub(startPoint).nor().crs(UP).nor();

        // Misused left/rightEnd variables for actually left/rightStart
        rightEnd = new Vector3(ortDirection).scl(flatWidth).add(startPoint);
        leftEnd = new Vector3(ortDirection).scl(-flatWidth).add(startPoint);
        
        cornerPoint.set(rightEnd).add(0f, halfHeight, 0f);
        normal.set(startPoint).sub(cornerPoint).nor();
        int v1 = createVertice(meshDetails, cornerPoint, normal);

        cornerPoint.set(rightEnd).sub(0f, halfHeight, 0f);
        normal.set(startPoint).sub(cornerPoint).nor();
        int v2 = createVertice(meshDetails, cornerPoint, normal);

        cornerPoint.set(leftEnd).add(0f, halfHeight, 0f);
        normal.set(startPoint).sub(cornerPoint).nor();
        int v3 = createVertice(meshDetails, cornerPoint, normal);
        
        cornerPoint.set(leftEnd).sub(0f, halfHeight, 0f);
        normal.set(startPoint).sub(cornerPoint).nor();
        int v4 = createVertice(meshDetails, cornerPoint, normal);
        
        rightEnd = new Vector3(ortDirection).scl(flatWidth).add(endPoint);
        leftEnd = new Vector3(ortDirection).scl(-flatWidth).add(endPoint);
        
        cornerPoint.set(rightEnd).add(0f, halfHeight, 0f);
        normal.set(endPoint).sub(cornerPoint).nor();
        int v5 = createVertice(meshDetails, cornerPoint, normal);

        cornerPoint.set(rightEnd).sub(0f, halfHeight, 0f);
        normal.set(endPoint).sub(cornerPoint).nor();
        int v6 = createVertice(meshDetails, cornerPoint, normal);

        cornerPoint.set(leftEnd).add(0f, halfHeight, 0f);
        normal.set(endPoint).sub(cornerPoint).nor();
        int v7 = createVertice(meshDetails, cornerPoint, normal);
        
        cornerPoint.set(leftEnd).sub(0f, halfHeight, 0f);
        normal.set(endPoint).sub(cornerPoint).nor();
        int v8 = createVertice(meshDetails, cornerPoint, normal);
        
        createPlaneIndices(meshDetails, v1, v2, v3, v4); // Start

        createPlaneIndices(meshDetails, v1, v3, v5, v7); // Top
        createPlaneIndices(meshDetails, v2, v1, v6, v5); // Right side
        createPlaneIndices(meshDetails, v3, v4, v7, v8); // Left side
        createPlaneIndices(meshDetails, v4, v2, v8, v6); // Bottom

        createPlaneIndices(meshDetails, v6, v5, v8, v7); // End

    }

    protected void makeExtrudeMesh(MeshDetails meshDetails, float layerHeight) {
        // 'ideal' extruded plastic cross section shape
        //
        //     oooooooooo      ----
        //   oo          oo
        //  o              o     l
        //  o              o     h
        //   oo          oo
        //     oooooooooo      ----
        //
        //  |xr|  flat   |xr|
        //  |               |
        //  |   nozzleSize  |
        //
        //  lh = layerHeight
        //  xr = xRadius = layarHeight / 2
        // 
        
        Vector3 cornerPoint = new Vector3();
        Vector3 normal = new Vector3();
        
        float xRadius = layerHeight / 2f;
        float flatHalf = (nozzleSize - layerHeight) / 2; // half the length of flat region

        ortDirection = new Vector3(endPoint).sub(startPoint).nor().crs(UP).nor();

        // Misused left/rightEnd variables for actually left/rightStart
        rightStart = new Vector3(ortDirection).scl(flatHalf).add(startPoint);
        leftStart = new Vector3(ortDirection).scl(-flatHalf).add(startPoint);
        
        rightEnd = new Vector3(ortDirection).scl(flatHalf).add(endPoint);
        leftEnd = new Vector3(ortDirection).scl(-flatHalf).add(endPoint);

        int pv1;
        int pv2;
        int v1;
        int v2;
        
        cornerPoint.set(rightStart).add(0f, xRadius, 0f);
        int sv1 = createVertice(meshDetails, cornerPoint, UP);
        pv1 = sv1;
        
        cornerPoint.set(rightEnd).add(0f, xRadius, 0f);
        int sv2 = createVertice(meshDetails, cornerPoint, UP);
        pv2 = sv2;

        for (int i = 0; i < ROUND_SECTIONS; i++) {
            float a = (float)((i + 1) * Math.PI / (ROUND_SECTIONS + 1));
            float x = (float)(xRadius * Math.cos(Math.PI / 2 - a));
            float y = (float)(xRadius * Math.sin(Math.PI / 2 - a));
            cornerPoint.set(ortDirection).scl(x).add(rightStart).add(0f, y, 0f);
            normal.set(cornerPoint).sub(rightStart).nor();
            v1 = createVertice(meshDetails, cornerPoint, normal);

            cornerPoint.set(ortDirection).scl(x).add(rightEnd).add(0f, y, 0f);
            normal.set(cornerPoint).sub(rightEnd).nor();
            v2 = createVertice(meshDetails, cornerPoint, normal);

            createPlaneIndices(meshDetails, pv1, pv2, v1, v2);

            pv1 = v1;
            pv2 = v2;
        }

        cornerPoint.set(rightStart).sub(0f, xRadius, 0f);
        v1 = createVertice(meshDetails, cornerPoint, DOWN);

        cornerPoint.set(rightEnd).sub(0f, xRadius, 0f);
        v2 = createVertice(meshDetails, cornerPoint, DOWN);

        createPlaneIndices(meshDetails, pv1, pv2, v1, v2);
        pv1 = v1;
        pv2 = v2;

        cornerPoint.set(leftStart).sub(0f, xRadius, 0f);
        v1 = createVertice(meshDetails, cornerPoint, DOWN);

        cornerPoint.set(leftEnd).sub(0f, xRadius, 0f);
        v2 = createVertice(meshDetails, cornerPoint, DOWN);

        createPlaneIndices(meshDetails, pv1, pv2, v1, v2);
        pv1 = v1;
        pv2 = v2;

        for (int i = 0; i < ROUND_SECTIONS; i++) {
            float a = (float)(Math.PI + (i + 1) * Math.PI / (ROUND_SECTIONS + 1));
            float x = (float)(xRadius * Math.cos(Math.PI + Math.PI / 2 + a));
            float y = (float)(xRadius * Math.sin(Math.PI + Math.PI / 2 + a));
            cornerPoint.set(ortDirection).scl(x).add(leftStart).sub(0f, y, 0f);
            normal.set(cornerPoint).sub(leftStart).nor();
            v1 = createVertice(meshDetails, cornerPoint, normal);

            cornerPoint.set(ortDirection).scl(x).add(leftEnd).sub(0f, y, 0f);
            normal.set(cornerPoint).sub(leftEnd).nor();
            v2 = createVertice(meshDetails, cornerPoint, normal);

            createPlaneIndices(meshDetails, pv1, pv2, v1, v2);
            pv1 = v1;
            pv2 = v2;
        }

        cornerPoint.set(leftStart).add(0f, xRadius, 0f);
        v1 = createVertice(meshDetails, cornerPoint, UP);

        cornerPoint.set(leftEnd).add(0f, xRadius, 0f);
        v2 = createVertice(meshDetails, cornerPoint, UP);

        createPlaneIndices(meshDetails, pv1, pv2, v1, v2);

        createPlaneIndices(meshDetails, v1, v2, sv1, sv2);
    }
    
    private int createVertice(MeshDetails meshDetails,
            Vector3 point,
            Vector3 normal) {

        int number = ordinaryNumber;
        float[] vertices = meshDetails.vertices;
        int v = meshDetails.vi;

        vertices[v] = point.x;
        vertices[v + 1] = point.y;
        vertices[v + 2] = point.z;

        // Colours
        if (isExtrude()) {
            float r = (number % 100) / 100f;
            float g = ((number / 100) % 100) / 100f;
            float b = ((number / 10000) % 100) / 100f;
            vertices[v + 3] = r;
            vertices[v + 4] = g;
            vertices[v + 5] = b;
            vertices[v + 6] = 1f / 100f;
        } else { // Move
            float r = (number % 100) / 100f;
            float g = ((number / 100) % 100) / 100f;
            float b = ((number / 10000) % 100) / 100f;
            vertices[v + 3] = r;
            vertices[v + 4] = g;
            vertices[v + 5] = b;
            vertices[v + 6] = 0f;
        }

        vertices[v + 7] = normal.x;
        vertices[v + 8] = normal.y;
        vertices[v + 9] = normal.z;

        // Texture coordinates
        vertices[v + 10] = 0f;
        vertices[v + 11] = 0f;
        
        meshDetails.vi = v + 12;
        int vn = meshDetails.nextVertice;
        meshDetails.nextVertice = meshDetails.nextVertice + 1;
        return vn;
    }

    private static void createPlaneIndices(MeshDetails meshDetails, int v1, int v2, int v3, int v4) {
        short[] indices = meshDetails.indices;
        int ii = meshDetails.ii;
        
        indices[ii + 0] = (short) v1;
        indices[ii + 1] = (short) v3;
        indices[ii + 2] = (short) v2;

        indices[ii + 3] = (short) v2;
        indices[ii + 4] = (short) v3;
        indices[ii + 5] = (short) v4;
        
        meshDetails.ii = meshDetails.ii + 6;
    }


}
