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

import java.awt.Canvas;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.ah.gcode.preview.ExitCallback;
import org.ah.gcode.preview.GCodePreview;
import org.ah.gcode.preview.utils.Files;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**
 *
 * @author Daniel Sendula
 */
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

        startApp(gcodeFile, true);
    }

    public static boolean startApp(File gcodeFile, boolean forceExit) {
        GCodePreview gCodePreview = new GCodePreview();

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

        final BooleanBox res = new BooleanBox();

        // TODO 1.8
        gCodePreview.setExitCallback(new ExitCallback() {
            @Override public void exit(boolean success) {
                res.set(success);
                app.exit();
            }
        });
//            () -> app.exit()
//        );


        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.forceExit = forceExit;
        config.width = 1024;
        config.height = 768;

        Canvas canvas = new Canvas();

        final JFrame frame = new JFrame();
        frame.setSize(1024, 768);
        if (forceExit) {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        frame.setLocationRelativeTo(null);
        frame.add(canvas);
        frame.setVisible(true);
        frame.addWindowListener(new WindowListener() {
            @Override public void windowOpened(WindowEvent e) { }
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) { }
            @Override public void windowDeactivated(WindowEvent e) { }
            @Override public void windowClosing(WindowEvent e) { }

            @Override
            public void windowClosed(WindowEvent e) {
                res.set(true);
                app.exit();
            }

            @Override public void windowActivated(WindowEvent e) { }
        });

        app = new LwjglApplication(gCodePreview, config, canvas);

        Thread threadKeepToFront = new Thread(new Runnable() {
            @Override public void run() {
                long startedAt = System.currentTimeMillis();
                sleep(3000);
                while (!res.received && System.currentTimeMillis() - startedAt < 5100) {
                    sleep(500);
                    if (!res.received) {
                        frame.setAlwaysOnTop(true);
                        frame.toFront();
                        frame.requestFocus();
                        frame.setAlwaysOnTop(false);
                    }
                }
            }
        });

        threadKeepToFront.start();

        boolean r = res.get();

        frame.setVisible(false);
        frame.dispose();

        return r;
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignore) { }
    }

    public static File chooseFile() {
        File lastOpenDir = null;
        String lastOpenDirStr = null;
        File gcodePreviewConfDir = new File(new File(System.getProperty("user.home")), ".gcode-preview");
        File lastOpenDirConf = new File(gcodePreviewConfDir, "last-open-dir");
        if (lastOpenDirConf.exists()) {
            lastOpenDirStr = readFile(lastOpenDirConf);
            if (lastOpenDirStr != null) {
                lastOpenDir = new File(lastOpenDirStr);
                if (!lastOpenDir.exists()) {
                    lastOpenDir = null;
                }
            }
        }

        File selectedFile = null;
        JFileChooser fc = new JFileChooser();

        if (lastOpenDir != null) {
            fc.setSelectedFile(lastOpenDir);
        }
        int res = fc.showOpenDialog(null);
        if (res == JFileChooser.APPROVE_OPTION) {
            selectedFile = fc.getSelectedFile();
        } else {
            System.err.println("No files selected");
            System.exit(1);
        }
        if (selectedFile != null) {
            writeConfFile(lastOpenDirConf, selectedFile.getAbsolutePath());
        }
        return selectedFile;
    }

    private static void writeConfFile(File lastOpenDirConf, String absolutePath) {
        try {
            File dir = lastOpenDirConf.getParentFile();
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    return;
                }
            }
            OutputStream os = new FileOutputStream(lastOpenDirConf);
            try {
                os.write(absolutePath.getBytes());
            } finally {
                os.close();
            }
        } catch (Throwable ignore) {}
    }

    private static String readFile(File lastOpenDirConf) {
        try {
            int l = (int)lastOpenDirConf.length();
            if (l < 1024*10) {
                byte[] content = new byte[l];
                InputStream is = new FileInputStream(lastOpenDirConf);
                try {
                    if (is.read(content) != l) {
                        return null;
                    }
                } finally {
                    is.close();
                }
                return new String(content).trim();
            }
        } catch (Throwable ignore) {}

        return null;
    }

    private static class BooleanBox {
        private boolean b;
        private boolean received = false;

        public void set(boolean b) {
            synchronized (this) {
                this.b = b;
                received = true;
                notifyAll();
            }
        }
        public boolean get() {
            synchronized (this) {
                while (!received) {
                    try {
                        wait(10000);
                    } catch (InterruptedException ignore) { }
                }
            }
            return b;
        }

    }
}
