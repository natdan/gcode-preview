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
package org.ah.gcode.preview.desktop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.swing.JFileChooser;

import org.ah.gcode.preview.GCodePreview;
import org.ah.gcode.preview.utils.Files;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class GCodePreviewLauncher {

    public static LwjglApplication app;

    public static void main(String[] args) {

        File gcodeFile = null;
        if (args.length > 1) {
            System.err.println("At the moment only one argument is allowed.");
            System.exit(1);
        } else if (args.length == 1) {
            String fileName = args[0];
            gcodeFile = new File(fileName);
        } else {
            gcodeFile = chooseFile();
        }

        GCodePreview gCodePreview = new GCodePreview();

        // String fileName = "even-smaller-test.gcode";
        // String fileName = "small-test.gcode";
        // String fileName = "../android/assets/test.gcode";
        // String fileName = "bad_cube_robox.gcode";
        // String fileName = "two_nozzles_robox.gcode";
        // String fileName = "reel_bottom_robox.gcode";

        if (!gcodeFile.exists()) {
            System.err.println("File does not exist: " + gcodeFile.getAbsolutePath());
            System.exit(1);
        }

//        FileHandle gcodeFile = Gdx.files.internal(fileName);
        try {
            InputStream gcodeStream = new FileInputStream(gcodeFile);
            try {
                List<String> lines = Files.readLines(gcodeStream);
                gCodePreview.setLines(lines);
            } finally {
                gcodeStream.close();
            }
        } catch (IOException e) {
            System.err.println("There is a problem loading file " + gcodeFile.getAbsolutePath());
            e.printStackTrace();
            System.exit(1);
        }

        gCodePreview.setExitCallback(() -> app.exit());

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1024;
        config.height = 768;
        app = new LwjglApplication(gCodePreview, config);
    }

    public static File chooseFile() {
        File selectedFile = null;
        JFileChooser fc = new JFileChooser();

        int res = fc.showOpenDialog(null);
        if (res == JFileChooser.APPROVE_OPTION) {
            selectedFile = fc.getSelectedFile();
        } else {
            System.err.println("No files selected");
            System.exit(1);
        }
        return selectedFile;
    }
}
