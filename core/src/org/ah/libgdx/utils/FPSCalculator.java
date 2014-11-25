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
package org.ah.libgdx.utils;


/**
 *
 * @author Daniel Sendula
 */
public class FPSCalculator {

    private long[] fpsArray = new long[200];
    private int fpsArrayStart = 0;
    private int fpsArrayEnd = 0;
    private String fps = "0.00";
    private long fpsTimeToPrint;
    private int FPS_REFRESH = 250;
    private int fpsi;

    public FPSCalculator() {
        fpsArray[0] = System.currentTimeMillis();
        fpsArrayEnd = 1;
    }

    /**
     * Returns true if it is time to print FPS
     *
     * @return
     */
    public boolean calcFps() {
        long now = System.currentTimeMillis();
        fpsArray[fpsArrayEnd] = now;
        long last = now;

        int i = fpsArrayEnd;

        fpsArrayEnd = fpsArrayEnd + 1;
        if (fpsArrayEnd == fpsArray.length) { fpsArrayEnd = 0; }

        int total = 0;
        int n = 0;

        while (i != fpsArrayStart && now - fpsArray[i] <= 1000) {
            i = i - 1;
            if (i < 0) { i = fpsArray.length - 1; }
            total = total + (int)(last - fpsArray[i]);
            last = fpsArray[i];
            n = n + 1;
        }

        if (fpsArrayEnd == fpsArrayStart) {
            fpsArrayStart = fpsArrayStart + 1;
            if (fpsArrayStart == fpsArray.length) {
                fpsArrayStart = 0;
            }
        }

        fpsi = 0;
        if (total != 0) {
            fpsi = 100000 * n / total;
        }
        fps = Integer.toString(fpsi); while (fps.length() < 2) { fps = "0" + fps; }
        fps = fps.substring(0, fps.length() - 2) + "." + fps.substring(fps.length() - 2);

        if (now - fpsTimeToPrint > FPS_REFRESH) {
            fpsTimeToPrint = now;
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method returns frames per second times 100
     * @return frames per second times 100
     */
    public int getFPSasInteger() {
        return fpsi;
    }

    public String getFPSText() {
        return fps;
    }
}
