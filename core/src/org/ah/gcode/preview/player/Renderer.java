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
package org.ah.gcode.preview.player;

import java.util.List;

import org.ah.gcode.preview.gcode.Layer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;

/**
 *
 * @author Daniel Sendula
 */
public class Renderer {

    private DefaultShaderProvider bottomShaderProvider;
    private DefaultShaderProvider topShaderProvider;
    private ModelBatch solidModelBatch;
    private ModelBatch topModelBatch;

    private int currentInstructionNo = 0;
    private Layer currentLayer;
    private boolean readjustZOffset = false;

    public Renderer() {
        bottomShaderProvider = new DefaultShaderProvider() {

            DefaultShader currentLayerShader = null;

            @Override protected Shader createShader (final Renderable renderable) {
                if (currentLayerHasMesh(renderable.mesh)) { // Current layer shader
                    if (currentLayerShader == null) {
                        DefaultShader.Config config = new DefaultShader.Config(
                                Gdx.files.internal("shaders/lighting.current.vertex.glsl").readString(),
                                Gdx.files.internal("shaders/lighting.fragment.glsl").readString());
                        currentLayerShader = new DefaultShader(renderable, config);

                        currentLayerShader.register(new BaseShader.Uniform("u_number"), new BaseShader.GlobalSetter() {
                            @Override public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
                                shader.set(inputID, getCurrentInstructionNo());
                            }
                        });
                    }
                    return currentLayerShader;
                } else { // Solid - below current layer shader
                    DefaultShader.Config config = new DefaultShader.Config(
                            Gdx.files.internal("shaders/lighting.solid.vertex.glsl").readString(),
                            Gdx.files.internal("shaders/lighting.fragment.glsl").readString());
                    DefaultShader defaultShader = new DefaultShader(renderable, config) {
                        @Override public boolean canRender(final Renderable renderable) {
                            if (currentLayerHasMesh(renderable.mesh)) {
                                return false;
                            }
                            return true;
                        }
                    };
                    return defaultShader;
                }
            }
        };
        solidModelBatch = new ModelBatch(null, bottomShaderProvider, null);

        topShaderProvider = new DefaultShaderProvider(
                Gdx.files.internal("shaders/lighting.top.vertex.glsl"),
                Gdx.files.internal("shaders/lighting.fragment.glsl"));
        topModelBatch = new ModelBatch(null, topShaderProvider, null);
    }

    public boolean currentLayerHasMesh(Mesh mesh) {
        if (currentLayer != null) {
            return currentLayer.hasMesh(mesh);
        }
        return false;
    }

    public DefaultShaderProvider getBottomShaderProvider() {
        return bottomShaderProvider;
    }

    public DefaultShaderProvider getTopShaderProvider() {
        return topShaderProvider;
    }

    public ModelBatch getSolidModelBatch() {
        return solidModelBatch;
    }

    public ModelBatch getTopModelBatch() {
        return topModelBatch;
    }

    public int getCurrentInstructionNo() {
        return currentInstructionNo;
    }

    public void setCurrentInstructionNo(int currentInstructionNo) {
        this.currentInstructionNo = currentInstructionNo;
    }

    public Layer getCurrentLayer() {
        return currentLayer;
    }

    public void setCurrentLayer(Layer currentLayer) {
        this.currentLayer = currentLayer;
    }


    public void renderSolid(Camera camera, Environment environment, List<Layer> layers) {
        Layer savedCurrentLayer = currentLayer;
        currentLayer = null;
        solidModelBatch.begin(camera);
        for (Layer layer : layers) {
            layer.render(solidModelBatch, environment, 0);
        }
        solidModelBatch.end();
        currentLayer = savedCurrentLayer;
    }

    public void renderProgress(Camera camera, Environment environment, List<Layer> layers, int startLayerNo, int currentLayerNo, boolean displayTopLayers) {
        solidModelBatch.begin(camera);
        float zOffset = 0;
        if (readjustZOffset && layers.size() > 0) {
            Layer layer = layers.get(startLayerNo);
            zOffset = layer.getZOffset() - layer.getLayerHeight();
            zOffset = -zOffset;
        }
        for (int i = startLayerNo; i < currentLayerNo; i++) {
            Layer layer = layers.get(i);
            layer.render(solidModelBatch, environment, zOffset);
        }
        if (currentLayer != null) {
            currentLayer.render(solidModelBatch, environment, zOffset);
        }

        solidModelBatch.end();
        if (displayTopLayers && currentLayerNo < layers.size() - 1) {
            topModelBatch.begin(camera);
            for (int i = currentLayerNo + 1; i < layers.size(); i++) {
                if (i >= startLayerNo && i < currentLayerNo) {
                    layers.get(i).render(topModelBatch, environment, zOffset);
                }
            }
            topModelBatch.end();
        }
    }

    public boolean isReadjustZOffset() {
        return readjustZOffset;
    }

    public void setReadjustZOffset(boolean readjustZOffset) {
        this.readjustZOffset = readjustZOffset;
    }
}
