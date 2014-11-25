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

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

/**
 *
 * @author Daniel Sendula
 */
public class Layer {

    private float layerZOffset;
    private float layerHeight;
    private List<GCodeMovement> instructions = new ArrayList<GCodeMovement>();
    private List<Mesh> meshes = new ArrayList<Mesh>();
    private Model model;
    private ModelInstance modelInstance;
    private int nextOrdinaryNumber = 0;

    private Point min = new Point();
    private Point max = new Point();

    public Layer(float layerZOffset, float layerHeight) {
        this.layerZOffset = layerZOffset;
        this.layerHeight = layerHeight;
    }

    public void dispose() {
        model.dispose();
        for (GCodeMovement code : instructions) {
            code.dispose();
        }
    }

    public float getZOffset() {
        return layerZOffset;
    }

    public float getLayerHeight() {
        return layerHeight;
    }

    public Point getMax() {
        return max;
    }

    public Point getMin() {
        return min;
    }

    public int getNumberOfInstructions() {
        return nextOrdinaryNumber - 1;
    }

    public void addMovement(GCodeMovement code) {
        instructions.add(code);
    }


    public void process(Context context) {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        int verticeElements = 0;
        int indiceElements = 0;
        int meshNo = 1;
        ArrayList<GCodeMovement> movements = new ArrayList<GCodeMovement>();
        for (GCodeMovement movement : instructions) {
            movements.add(movement);
            verticeElements = verticeElements + movement.getVerticeElementsPerSegment();
            indiceElements = indiceElements + movement.getIndiceElementsPerSegment();
            if ((verticeElements / GCodeMovement.ELEMENTS_PER_VERTICE) > 30000) {
                Mesh mesh = process(movements, verticeElements, indiceElements, meshNo, modelBuilder, context.material, layerHeight);
                meshes.add(mesh);
                movements.clear();
                verticeElements = 0;
                indiceElements = 0;
                meshNo = meshNo + 1;
            }
        }
        if (!movements.isEmpty()) {
            Mesh mesh = process(movements, verticeElements, indiceElements, meshNo, modelBuilder, context.material, layerHeight);
            meshes.add(mesh);
            movements.clear();
        }

        model = modelBuilder.end();
        modelInstance = new ModelInstance(model);
    }

    protected Mesh  process(List<GCodeMovement> movements, int verticeElements, int indiceElements, int meshNo, ModelBuilder modelBuilder, Material material, float layerHeight) {

        MeshDetails meshDetails = new MeshDetails(verticeElements, indiceElements, meshNo);

        Mesh mesh = new Mesh(true, verticeElements, indiceElements,
                VertexAttribute.Position(), VertexAttribute.ColorUnpacked(), VertexAttribute.Normal(),
                VertexAttribute.TexCoords(0));


        int vi = 0;
        int ii = 0;
        GCodeMovement previousMovement = null;
        for (GCodeMovement movement : movements) {

            movement.process(nextOrdinaryNumber, meshDetails, layerHeight, previousMovement);
            if (movement.isExtrude()) {
                previousMovement = movement;
            } else {
                previousMovement = null;
            }
            nextOrdinaryNumber = nextOrdinaryNumber + 1;

            vi = vi + movement.getVerticeElementsPerSegment();
            ii = ii + movement.getIndiceElementsPerSegment();
        }

        mesh.setVertices(meshDetails.vertices);
        mesh.setIndices(meshDetails.indices);

        Node node = modelBuilder.node();
        node.id = Integer.toString(meshNo);
        modelBuilder.part(Integer.toString(meshNo), mesh, GL20.GL_TRIANGLES, material);
        return mesh;
    }

    public void render(ModelBatch modelBatch, Environment environment, float zOffset) {
        if (modelInstance != null) {
            modelInstance.transform.idt().translate(0, zOffset, 0);
            modelBatch.render(modelInstance, environment);
        }
    }

    public boolean hasMesh(Mesh mesh) {
        return meshes.contains(mesh);
    }

    public static class MeshDetails {
        int meshNo;
        float[] vertices;
        short[] indices;
        int vi;
        int ii;
        int nextVertice;

        public MeshDetails(int verticeElements, int indiceElements, int meshNo) {
            vertices = new float[verticeElements];
            indices = new short[indiceElements];
            this.meshNo = meshNo;
        }

    }
}
