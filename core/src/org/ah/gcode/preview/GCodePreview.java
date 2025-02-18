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
package org.ah.gcode.preview;

import java.util.ArrayList;
import java.util.List;

import org.ah.gcode.preview.gcode.Context;
import org.ah.gcode.preview.gcode.GCodeModel;
import org.ah.gcode.preview.gcode.GCodeParser;
import org.ah.gcode.preview.player.Controller;
import org.ah.gcode.preview.utils.ModelBuilders;
import org.ah.gcode.preview.utils.SceneCameraInputController;
import org.ah.libgdx.components.Console;
import org.ah.libgdx.components.Panel;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader.BitmapFontParameter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider.AssetTextureProvider;
import com.badlogic.gdx.math.Vector3;

/**
 *
 * @author Daniel Sendula
 */
public class GCodePreview extends ApplicationAdapter {

    public static final int SIXTY_FPS_FRAME = 1000 / 60;

    private AssetManager assetManager;
    private AssetTextureProvider textureProvider;

    private PerspectiveCamera camera;

    private BitmapFont font;

    private SpriteBatch spriteBatch;
    private ModelBatch modelBatch;
    private Environment environment;

    private List<ModelInstance> instances = new ArrayList<ModelInstance>();

    private GCodeModel gCodeModel;

    private DirectionalLight directionalLight;

    private OrthographicCamera spriteFontCamera;

    private boolean loadingAssets;
    private boolean parsingGCode;
    private boolean bedCreated;
    boolean preparingMeshes;
    private GCodeParser parser;

    private Console console;

    private Color lightColor;

    private GCodePreviewWindow window;

    private Controller controller;

    private ExitCallback exitCallback;

    private List<String> lines;

    private ModelInstance planeInstance;

    @Override
    public void create() {
        assetManager = new AssetManager();

        BitmapFontParameter bitmapFontParameter = new BitmapFontParameter();
        bitmapFontParameter.flip = true;
        assetManager.load("arial-15.fnt", BitmapFont.class, bitmapFontParameter);
        assetManager.load("bed.png", Texture.class);
        assetManager.load("checked.png", Texture.class);


        assetManager.load("gui/slider-end-left.png", Texture.class);
        assetManager.load("gui/slider-end-right.png", Texture.class);
        assetManager.load("gui/slider-horizontal.png", Texture.class);
        assetManager.load("gui/slider-end-up.png", Texture.class);
        assetManager.load("gui/slider-end-down.png", Texture.class);
        assetManager.load("gui/slider-vertical.png", Texture.class);
        assetManager.load("gui/knob.png", Texture.class);
        prepareButtonImage(assetManager, "gui/ok");
        prepareButtonImage(assetManager, "gui/cancel");
        prepareButtonImage(assetManager, "gui/three-circles");
        prepareButtonImage(assetManager, "gui/media/begin");
        prepareButtonImage(assetManager, "gui/media/end");
        prepareButtonImage(assetManager, "gui/media/play");
        prepareButtonImage(assetManager, "gui/media/pause");
        prepareButtonImage(assetManager, "gui/media/fast-forward");
        prepareButtonImage(assetManager, "gui/media/fast-backward");


        textureProvider = new AssetTextureProvider(assetManager);

        loadingAssets = true;

        Vector3 lightDirection = new Vector3(-0.5f, -0.5f, -1f).nor();
        lightColor = Color.WHITE;
        directionalLight = new DirectionalLight().set(lightColor, lightDirection);

        spriteBatch = new SpriteBatch();
        modelBatch = new ModelBatch();
        spriteFontCamera = new OrthographicCamera();

        environment = new Environment();
        environment.set(new NumberAttribute(NumberAttribute.Number, 1000));

        modelBatch = new ModelBatch();

        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1.0f, 1.0f, 0.6f, 1f));
        // environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        environment.add(directionalLight);

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 0.001f;
        camera.far = 600f;

        camera.position.set(0f, 8f, 25f);
        camera.lookAt(0, 0, 0);

        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        window = new GCodePreviewWindow(0, 0, width, height);
        resize(width, height);

        parser = new GCodeParser();
    }

    private void prepareButtonImage(AssetManager assetManager, String image) {
        assetManager.load(image + ".png", Texture.class);
        assetManager.load(image + "-over.png", Texture.class);
        assetManager.load(image + "-selected.png", Texture.class);
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        window.dispose();
    }


    @Override
    public void resize (int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        spriteFontCamera.setToOrtho(true, width, height);
        window.setSize(width, height);
    }

    public void loadModel() {
        if (lines != null) {
            parser.initParsing(lines);
            lines = null;
            if (gCodeModel != null) {
                gCodeModel.dispose();
            }
            gCodeModel = parser.getModel();
            parsingGCode = true;

        }
    }

    protected void finishedLoading() {
        loadingAssets = false;

        font = assetManager.get("arial-15.fnt");
        window.initialise(textureProvider, font);
        window.layout();
        console = window.getConsole();

        loadModel();
    }

    protected void createBed(float bedWidth, float bedHeight) {
        Texture checkedTexture = textureProvider.load("bed.png");

        Material checkedMaterial = new Material(TextureAttribute.createDiffuse(checkedTexture));
        Material emptyMaterial = new Material();

        ModelBuilder modelBuilder = new ModelBuilder();

        Mesh mesh = ModelBuilders.createRectY2(0f, 0f, 0f, bedWidth, bedHeight, 0);
        modelBuilder.begin();
        modelBuilder.part("1", mesh, GL20.GL_TRIANGLES, checkedMaterial);
        Model planeModel = modelBuilder.end();

        planeInstance = new ModelInstance(planeModel);
        instances.add(planeInstance);

        SceneCameraInputController sceneCameraInputController = new SceneCameraInputController(camera, planeInstance);
        controller = new Controller(gCodeModel, window, sceneCameraInputController, exitCallback);
        Gdx.input.setInputProcessor(controller);

        controller.getLeftPanel().setVisible(true);

        Model axisModel = modelBuilder.createXYZCoordinates(10, emptyMaterial, Usage.Position | Usage.ColorUnpacked | Usage.Normal);
        ModelInstance axisInstance = new ModelInstance(axisModel);
        instances.add(axisInstance);
    }

    protected void parseGCode() {
        long now = System.currentTimeMillis();
        while (System.currentTimeMillis() - now < SIXTY_FPS_FRAME) {
            if (parser.isFinished()) {

                Material whiteMaterial = new Material(ColorAttribute.createAmbient(1f, 1f, 1f, 1f), new BlendingAttribute(true, 1f));
                Context context = new Context();
                context.material = whiteMaterial;

                gCodeModel.prepareForProcessing(context);
                parsingGCode = false;
                preparingMeshes = true;

                if (!bedCreated) {
                    float bedWidth = 210f;
                    float bedHeight = 150f;
                    if (gCodeModel.getBedWidth() > 0) { bedWidth = gCodeModel.getBedWidth(); }
                    if (gCodeModel.getBedHeight() > 0) { bedHeight = gCodeModel.getBedHeight(); }
                    createBed(bedWidth, bedHeight);
                    bedCreated = true;
                }
            } else {
                parser.parseNextLine();
                if (!bedCreated && parser.getModel().getBedWidth() > 0) {
                    createBed(parser.getModel().getBedWidth(), parser.getModel().getBedHeight());
                    bedCreated = true;
                }
            }
        }
        Panel playPanel = window.getPlayPanel();
        playPanel.setVisible(true);
        playPanel.refresh();
        playPanel.text("Parsed lines: ", 0);
        playPanel.text(parser.getCurrentLine() + "/" + parser.getLines().size(), 1);
    }

    protected void prepareMesh() {
        Panel playPanel = window.getPlayPanel();

        long now = System.currentTimeMillis();
        while (preparingMeshes && System.currentTimeMillis() - now < SIXTY_FPS_FRAME) {
            if (gCodeModel.isProcessingFinished()) {
                console.println("Total layers: " + gCodeModel.getLayers().size());
                console.println("Total instructions: " + gCodeModel.getTotalInstrctions());
                console.println("Max instrs per layer: " + gCodeModel.getMaxInstructionsPerLayer());
                console.println();

//                playPanel.setVisible(false);
                playPanel.refresh();
                playPanel.text("Created meshes for all layers", 0);

                if (controller.getLeftPanel().isVisible()) {
                    controller.consoleAtStartup();
                }
                preparingMeshes = false;

                controller.resetView();
                controller.setCurrentLayer(0);
                controller.setTwoDView();
            } else {
                gCodeModel.processNextLayer();
            }
//            console.println("Processed " + gCodeModel.getCurrentLayerNo() + " meshes");
        }
        if (preparingMeshes) {
            // While still preparing meshes
            controller.setCurrentLayer(gCodeModel.getLayers().size());
            playPanel.setVisible(true);
            playPanel.refresh();
            playPanel.text("Creating meshes for layers", 0);
            playPanel.text(gCodeModel.getCurrentLayerNo() + "/" + gCodeModel.getLayers().size(), 1);
        }
    }

    @Override
    public void render() {
        if (loadingAssets && assetManager.update()) {
            finishedLoading();
        }

        if (loadingAssets) {
        } else {
            if (parsingGCode) {
                parseGCode();
            } else if (preparingMeshes) {
                prepareMesh();
            } else {
            }
        }
        camera.update();
        Vector3 lightDirection = new Vector3(camera.direction).nor();
        directionalLight.set(lightColor, lightDirection);


        // Magic rendering initialisation
        Gdx.gl.glDisable(GL20.GL_CULL_FACE);
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
//        Gdx.gl.glEnable(GL20.GL_BLEND);
//        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT
                | GL20.GL_DEPTH_BUFFER_BIT
                | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (instances.size() > 0) {
            modelBatch.begin(camera);
            for (ModelInstance instance : instances) {
                modelBatch.render(instance, environment);
            }

            // gCodeModel.render(modelBatch, environment);
            modelBatch.end();
        }
        if (controller != null) {
            controller.render(camera, environment);
        }

        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(spriteFontCamera.combined);
        spriteBatch.enableBlending();
        window.render(spriteBatch);
        spriteBatch.end();
    }


    public static class NumberAttribute extends IntAttribute {
        public static final String NumberAlias = "number";
        public static final long Number = register(NumberAlias);

        public NumberAttribute(long type, int value) {
            super(type, value);
        }
    }

    public ExitCallback getExitCallback() {
        return exitCallback;
    }

    public void setExitCallback(ExitCallback exitCallback) {
        this.exitCallback = exitCallback;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

}
