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

/**
 *
 * @author Daniel Sendula
 */
public class Point {

    public float x;
    public float y;
    public float z;

    public Point() {
        x = 0f;
        y = 0f;
        z = 0f;
    }

    public Point(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void updateMin(Point target) {
        if ( target.x < x) { x = target.x; }
        if ( target.y < y) { y = target.y; }
        if ( target.z < z) { z = target.z; }
    }

    public void updateMax(Point target) {
        if ( target.x > x) { x = target.x; }
        if ( target.y > y) { y = target.y; }
        if ( target.z > z) { z = target.z; }
    }

    public void reset() {
        x = 0f;
        y = 0f;
        z = 0f;
    }
}
