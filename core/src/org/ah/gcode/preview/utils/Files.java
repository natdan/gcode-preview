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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.files.FileHandle;

/**
 *
 * @author Daniel Sendula
 */
public class Files {

    private Files() { }

    public static List<String> readLines(FileHandle handle) throws IOException {
        return Files.readLines(handle.read());
    }

    public static List<String> readLines(InputStream is) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        try {
            List<String> lines = new ArrayList<String>();
            String line = in.readLine();
            while (line != null) {
                lines.add(line);
                line = in.readLine();
            }
            return lines;
        } finally {
            in.close();
        }
    }
}
