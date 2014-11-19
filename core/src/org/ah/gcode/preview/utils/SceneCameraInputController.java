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
package org.ah.gcode.preview.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

public class SceneCameraInputController {

    private Camera camera;
    private final Vector3 vector = new Vector3();
    private final Vector3 vector2 = new Vector3();
    @SuppressWarnings("unused")
    private ModelInstance backgroundPlane;
    private Vector3 rotateAroundVector;
    private BoundingBox backgroundPlaneBoundingBox;
    private float startX;
    private float startY;
    private float rotateAngle = 90f;
    private float translateUnits = 100f;
    private float zoomAmount = 5f;
    private boolean twoD;


    public SceneCameraInputController(Camera camera, ModelInstance backgroundPlane) {
        this.camera = camera;
        this.backgroundPlane = backgroundPlane;
        this.backgroundPlaneBoundingBox = new BoundingBox();
        backgroundPlane.calculateBoundingBox(backgroundPlaneBoundingBox);
        rotateAroundVector = new Vector3();
    }

    public void resetToCentre(float middleX, float middleY) {
        camera.up.set(0f,  0f, -1f);
        camera.position.set(middleX, 135f, middleY);
        camera.lookAt(middleX, 0, middleY);
    }
    
    public void setTwoDimensions() {
        twoD = true;
    }
    
    public void setThreeDimensions() {
        twoD = false;
    }
    
    public void touchDown(float screenX, float screenY) {
        Ray ray = camera.getPickRay(screenX, screenY);
        Intersector.intersectRayBounds(ray, backgroundPlaneBoundingBox, rotateAroundVector);
        startX = screenX;
        startY = screenY;
    }
    
    public void touchUp(float x, float y) {
    }
    
    public void rotate(float screenX, float screenY) {
        if (!twoD) {
            final float deltaX = (screenX - startX) / Gdx.graphics.getWidth();
            final float deltaY = (startY - screenY) / Gdx.graphics.getHeight();
            
            startX = screenX;
            startY = screenY;
    
            vector.set(camera.direction).crs(camera.up).y = 0f;
            camera.rotateAround(rotateAroundVector, vector.nor(), deltaY * rotateAngle);
            camera.rotateAround(rotateAroundVector, Vector3.Y, deltaX * -rotateAngle);
        }
    }
    
    public void translate(float screenX, float screenY) {
        final float deltaX = (screenX - startX) / Gdx.graphics.getWidth();
        final float deltaY = (startY - screenY) / Gdx.graphics.getHeight();
        
        startX = screenX;
        startY = screenY;

        camera.translate(vector.set(camera.direction).crs(camera.up).nor().scl(-deltaX * translateUnits));
        camera.translate(vector2.set(camera.up).scl(-deltaY * translateUnits));
    }
    
    public boolean zoom (float amount) {
        camera.translate(vector.set(camera.direction).scl(amount * zoomAmount));

        return true;
    }

    
}
