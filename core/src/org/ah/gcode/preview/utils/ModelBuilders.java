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

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class ModelBuilders {

    public static final float FULL_CIRCLE = (float)(Math.PI * 2);
    
    public static Mesh createRectX(float x, float y, float z, float width, float height, int normal) {

        Mesh mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.ColorUnpacked(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0));

        mesh.setVertices(new float[] { x, y, z, 1, 1, 1, 1, normals[normal][0], normals[normal][2], normals[normal][2], 0, 0, x + width, y, z, 1, 1, 1, 1, normals[normal][0], normals[normal][2],
                normals[normal][2], 1, 0, x + width, y + height, z, 1, 1, 1, 1, normals[normal][0], normals[normal][2], normals[normal][2], 1, 1, x, y + height, z, 1, 1, 1, 1, normals[normal][0],
                normals[normal][2], normals[normal][2], 0, 1 });

        mesh.setIndices(new short[] { 0, 1, 2, 2, 3, 0 });
        return mesh;
    }

    public static Mesh createRectY(float x, float y, float z, float width, float height, int normal) {

        Mesh mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.ColorUnpacked(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0));

        mesh.setVertices(new float[] { x, y, z, 1, 1, 1, 1, normals[normal][0], normals[normal][2], normals[normal][2], 0, 0, x + width, y, z, 1, 1, 1, 1, normals[normal][0], normals[normal][2],
                normals[normal][2], 1, 0, x + width, y, z + height, 1, 1, 1, 1, normals[normal][0], normals[normal][2], normals[normal][2], 1, 1, x, y, z + height, 1, 1, 1, 1, normals[normal][0],
                normals[normal][2], normals[normal][2], 0, 1 });

        mesh.setIndices(new short[] { 0, 1, 2, 2, 3, 0 });
        return mesh;
    }

    public static Mesh createRectY2(float x, float y, float z, float width, float height, int normal) {

        Mesh mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.ColorUnpacked(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0));

        mesh.setVertices(new float[] { x, y, z, 1, 1, 1, 1, normals[normal][0], normals[normal][2], normals[normal][2], 0, 0, x + width, y, z, 1, 1, 1, 1, normals[normal][0], normals[normal][2],
                normals[normal][2], 1, 0, x + width, y, z + height, 1, 1, 1, 1, normals[normal][0], normals[normal][2], normals[normal][2], 1, 1, x, y, z + height, 1, 1, 1, 1, normals[normal][0],
                normals[normal][2], normals[normal][2], 0, 1 });

        mesh.setIndices(new short[] { 0, 2, 1, 3, 2, 0 });
        return mesh;
    }

    public static Mesh createRectZ(float x, float y, float z, float width, float height, int normal) {

        Mesh mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.ColorUnpacked(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0));

        mesh.setVertices(new float[] { x, y, z, 1, 1, 1, 1, normals[normal][0], normals[normal][2], normals[normal][2], 0, 0, x, y + width, z, 1, 1, 1, 1, normals[normal][0], normals[normal][2],
                normals[normal][2], 1, 0, x, y + width, z + height, 1, 1, 1, 1, normals[normal][0], normals[normal][2], normals[normal][2], 1, 1, x, y, z + height, 1, 1, 1, 1, normals[normal][0],
                normals[normal][2], normals[normal][2], 0, 1 });

        mesh.setIndices(new short[] { 0, 1, 2, 2, 3, 0 });
        return mesh;
    }

    static float[][] normals = new float[][] { new float[] { 0, 0, 1f }, new float[] { 0, 0, -1f }, new float[] { 0, 1f, 0 }, new float[] { 0, -1f, 0 }, new float[] { 1f, 0, 0 }, new float[] { -1f, 0, 0 } };

    public static ModelInstance createBox(Material material1, Material material2, Material material3) {

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        modelBuilder.part("1", createRectX(-2.5f, -2.5f, 2.5f, 5f, 5f, 1), GL20.GL_TRIANGLES, material1);
        modelBuilder.part("2", createRectX(-2.5f, -2.5f, -2.5f, 5f, 5f, 0), GL20.GL_TRIANGLES, material1);

        modelBuilder.part("3", createRectY(-2.5f, -2.5f, -2.5f, 5f, 5f, 3), GL20.GL_TRIANGLES, material2);
        modelBuilder.part("4", createRectY(-2.5f, 2.5f, -2.5f, 5f, 5f, 2), GL20.GL_TRIANGLES, material2);

        modelBuilder.part("5", createRectZ(-2.5f, -2.5f, -2.5f, 5f, 5f, 5), GL20.GL_TRIANGLES, material3);
        modelBuilder.part("6", createRectZ(2.5f, -2.5f, -2.5f, 5f, 5f, 4), GL20.GL_TRIANGLES, material3);

        Model model = modelBuilder.end();

        ModelInstance instance = new ModelInstance(model);

        return instance;
    }

    public static ModelInstance createCircle(Material material) {

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        float outerDiameter = 5f;
        float innerDiameter = 2f;

        int sections = 20;
        float angle = (float) (FULL_CIRCLE / sections);

        Mesh mesh = new Mesh(true, sections * 2, sections * 12, VertexAttribute.Position(), VertexAttribute.ColorUnpacked(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0));

        float[] vertices = new float[sections * 24];

        for (int i = 0; i < sections; i++) {
            int v = i * 24;
            // ======== outer =========
            vertices[v] = (float) (Math.sin(angle * i)) * outerDiameter;
            vertices[v + 1] = (float) (Math.cos(angle * i)) * outerDiameter;
            vertices[v + 2] = 0;

            // Colours
            vertices[v + 3] = 1;
            vertices[v + 4] = 1;
            vertices[v + 5] = 1;
            vertices[v + 6] = 1;

            // Normal
            vertices[v + 7] = 0;
            vertices[v + 8] = 0;
            vertices[v + 9] = 1;

            // Text coordinates
            vertices[v + 10] = (float) (Math.sin(angle * i) / 2) + 0.5f;
            vertices[v + 11] = (float) (Math.cos(angle * i) / 2) + 0.5f;

            // ======== inner =========
            vertices[v + 12] = (float) (Math.sin(angle * i)) * innerDiameter;
            vertices[v + 13] = (float) (Math.cos(angle * i)) * innerDiameter;
            vertices[v + 14] = 0;

            // Colours
            vertices[v + 15] = 1;
            vertices[v + 16] = 1;
            vertices[v + 17] = 1;
            vertices[v + 18] = 1;

            // Normal
            vertices[v + 19] = 0;
            vertices[v + 20] = 0;
            vertices[v + 21] = 1;

            // Text coordinates
            vertices[v + 22] = (float) (Math.sin(angle * i) / 2) * (innerDiameter / outerDiameter) + 0.5f;
            vertices[v + 23] = (float) (Math.cos(angle * i) / 2) * (innerDiameter / outerDiameter) + 0.5f;
        }

        mesh.setVertices(vertices);

        short[] indices = new short[sections * 12];

        for (int i = 0; i < sections - 1; i++) {
            int j = i * 12;
            indices[j + 0] = (short) (i * 2 + 0);
            indices[j + 1] = (short) (i * 2 + 1);
            indices[j + 2] = (short) (i * 2 + 2);

            indices[j + 3] = (short) (i * 2 + 0);
            indices[j + 4] = (short) (i * 2 + 2);
            indices[j + 5] = (short) (i * 2 + 1);

            indices[j + 6] = (short) (i * 2 + 1);
            indices[j + 7] = (short) (i * 2 + 2);
            indices[j + 8] = (short) (i * 2 + 3);

            indices[j + 9] = (short) (i * 2 + 1);
            indices[j + 10] = (short) (i * 2 + 3);
            indices[j + 11] = (short) (i * 2 + 2);
        }

        int i = (sections - 1) * 2;
        int j = (sections - 1) * 12;
        indices[j] = (short) (i + 0);
        indices[j + 1] = (short) (i + 1);
        indices[j + 2] = (short) (0);
        indices[j + 3] = (short) (i + 0);
        indices[j + 4] = (short) (0);
        indices[j + 5] = (short) (i + 1);
        indices[j + 6] = (short) (i + 1);
        indices[j + 7] = (short) (0);
        indices[j + 8] = (short) (1);
        indices[j + 9] = (short) (i + 1);
        indices[j + 10] = (short) (1);
        indices[j + 11] = (short) (0);

        mesh.setIndices(indices);

        modelBuilder.part("circle", mesh, GL20.GL_TRIANGLES, material);
        Model model = modelBuilder.end();

        ModelInstance instance = new ModelInstance(model);

        return instance;
    }

    public static Model createTorus(Material material, float zDiameter, float xDiameter, int zSections, int xSections) {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        float zAngle = (float) (FULL_CIRCLE / zSections);
        float xAngle = (float) (FULL_CIRCLE / xSections);

        Mesh mesh = new Mesh(true, zSections * xSections, zSections * xSections * 6, 
                VertexAttribute.Position(), VertexAttribute.ColorUnpacked(), 
                VertexAttribute.Normal(), VertexAttribute.TexCoords(0));

        float[] vertices = new float[zSections * xSections * 12];

        for (int j = 0; j < xSections; j++) {
            for (int i = 0; i < zSections; i++) {
                int v = (j * zSections * 12) + i * 12;

                createTorusVertice(zDiameter, xDiameter, zSections, xSections, zAngle, xAngle, vertices, j, i, v);
            }
        }
        mesh.setVertices(vertices);

        short[] indices = new short[zSections * xSections * 6];

        createTorusIndices(zSections, xSections, indices);

        mesh.setIndices(indices);

        modelBuilder.part("torus", mesh, GL20.GL_TRIANGLES, material);
        Model model = modelBuilder.end();
        
        return model;
    }

    public static Model createTorus2(Material material, float zDiameter, float xDiameter, int zSections, int xSections) {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        createTorus2(modelBuilder, material, zDiameter, xDiameter, zSections, xSections);
        Model model = modelBuilder.end();
        
        return model;
    }
    
    public static void createTorus2(ModelBuilder modelBuilder, Material material, float zDiameter, float xDiameter, int zSections, int xSections) {

        float zAngle = (float) (FULL_CIRCLE / zSections);
        float xAngle = (float) (FULL_CIRCLE / xSections);

        Mesh mesh = new Mesh(true, (zSections + 1) * (xSections + 1), zSections * xSections * 6, 
                VertexAttribute.Position(), VertexAttribute.ColorUnpacked(), 
                VertexAttribute.Normal(), VertexAttribute.TexCoords(0));

        float[] vertices = new float[(zSections + 1) * (xSections + 1) * 12];

        for (int j = 0; j < xSections + 1; j++) {
            for (int i = 0; i < zSections + 1; i++) {
                int v = (j * (zSections + 1) * 12) + i * 12;

                createTorusVertice2(zDiameter, xDiameter, zSections, xSections, zAngle, xAngle, vertices, j, i, v);
            }
        }
        mesh.setVertices(vertices);

        short[] indices = new short[zSections * xSections * 6];

        createTorusIndices2(zSections, xSections, indices);

        mesh.setIndices(indices);

        modelBuilder.part("torus", mesh, GL20.GL_TRIANGLES, material);
    }

    public static Model createTorus3(Material material, float zDiameter, float xDiameter, int zSections, int xSections, int bones) {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        createTorus3(modelBuilder, material, zDiameter, xDiameter, zSections, xSections, bones);
        Model model = modelBuilder.end();
        
        return model;
    }
    
    public static void createTorus3(ModelBuilder modelBuilder, Material material, 
            float zDiameter, float xDiameter, int zSections, int xSections, int bones) {

        float zAngle = (float) (FULL_CIRCLE / zSections);
        float xAngle = (float) (FULL_CIRCLE / xSections);
        float boneAngle = (float) (FULL_CIRCLE / bones);

        Mesh mesh = new Mesh(true, (zSections + 1) * (xSections + 1), zSections * xSections * 6,
                VertexAttribute.Position(), VertexAttribute.ColorUnpacked(), VertexAttribute.Normal(),
                VertexAttribute.TexCoords(0), VertexAttribute.BoneWeight(0), VertexAttribute.BoneWeight(1));

        float[] vertices = new float[(zSections + 1) * (xSections + 1) * 16];

        int bone1Index = 0;
        int bone2Index = 0;
        float bone1Weight = 0.0f;
        float bone2Weight = 0.0f;
        
        float zSectionToBoneRatio = (float)(zSections + 1) / (float)bones;
        float zSectionAngle = 0.0f;
        float zSectionWeight = 0.0f;

        for (int i = 0; i < zSections + 1; i++) {

            bone1Index = (int)(i / zSectionToBoneRatio);
            if (bone1Index >= bones) {
                throw new IllegalStateException();
            }
            bone2Index = bone1Index + 1;
            if (bone2Index >= bones) {
                bone2Index = 0;
            }
            
            zSectionAngle = i * zAngle - bone1Index * boneAngle;
            if (zSectionAngle > FULL_CIRCLE) {
                zSectionAngle = zSectionAngle - FULL_CIRCLE;
            }
            zSectionWeight = zSectionAngle / boneAngle;
            bone1Weight = 1 - zSectionWeight;
            bone2Weight = zSectionWeight;

            for (int j = 0; j < xSections + 1; j++) {
                int v = (j * (zSections + 1) * 16) + i * 16;
                createTorusVertice3(zDiameter, xDiameter, zSections, xSections,
                        zAngle, xAngle, vertices, j, i, v,
                        bone1Index, bone1Weight, bone2Index, bone2Weight);
            }
        }
        mesh.setVertices(vertices);

        short[] indices = new short[zSections * xSections * 6];

        createTorusIndices2(zSections, xSections, indices);

        mesh.setIndices(indices);

        Node node = modelBuilder.node();
        node.id = "torus";
        modelBuilder.part("torus", mesh, GL20.GL_TRIANGLES, material);
        Matrix4[] boneMatrixes = new Matrix4[bones];
        for (int i = 0; i < boneMatrixes.length; i++) {
            boneMatrixes[i] = new Matrix4().idt();
        }
        
        Array<NodePart> parts = node.parts;
        Object[] items = parts.items;
        NodePart item = (NodePart)items[0];
        item.bones = boneMatrixes;
        //node.parts.items[0].bones = boneMatrixes;
    }

    public static Model createUnevenTorus(Material material, float zDiameter, float xDiameter, int zSections, int xSections, float deviation) {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        float zAngle = (float) (FULL_CIRCLE / zSections);
        float xAngle = (float) (FULL_CIRCLE / xSections);

        Mesh mesh = new Mesh(true, (zSections + 1) * (xSections + 1), zSections * xSections * 6, VertexAttribute.Position(), VertexAttribute.ColorUnpacked(), VertexAttribute.Normal(),
                VertexAttribute.TexCoords(0));

        float[] vertices = new float[(zSections + 1) * (xSections + 1) * 12];

        for (int j = 0; j < xSections + 1; j++) {
            for (int i = 0; i < zSections + 1; i++) {
                int v = (j * (zSections + 1) * 12) + i * 12;

                createUnevenTorusVertice(zDiameter, xDiameter, zSections, xSections, zAngle, xAngle, vertices, j, i, v, deviation);
                //createTorusVertice2(zDiameter, xDiameter, zSections, xSections, zAngle, xAngle, vertices, j, i, v);
            }
        }
        mesh.setVertices(vertices);

        short[] indices = new short[zSections * xSections * 6];

        createTorusIndices2(zSections, xSections, indices);

        mesh.setIndices(indices);

        modelBuilder.part("torus", mesh, GL20.GL_TRIANGLES, material);
        Model model = modelBuilder.end();
        
        return model;
    }

    private static void createTorusIndices(int zSections, int xSections, short[] indices) {
        for (int j = 0; j < xSections - 1; j++) {
            for (int i = 0; i < zSections - 1; i++) {
                int jj = (j * zSections * 6) + i * 6;

                int v1 = zSections * (j + 0) + i;
                int v2 = zSections * (j + 1) + i;
                int v3 = zSections * (j + 0) + i + 1;
                int v4 = zSections * (j + 1) + i + 1;

                indices[jj + 0] = (short) v1;
                indices[jj + 1] = (short) v2;
                indices[jj + 2] = (short) v3;

                indices[jj + 3] = (short) v2;
                indices[jj + 4] = (short) v4;
                indices[jj + 5] = (short) v3;
            }
            int jj = (j * zSections * 6) + (zSections - 1) * 6;

            int v1 = zSections * (j + 0) + (zSections - 1);
            int v2 = zSections * (j + 1) + (zSections - 1);
            int v3 = zSections * (j + 0);
            int v4 = zSections * (j + 1);

            indices[jj + 0] = (short) v1;
            indices[jj + 1] = (short) v2;
            indices[jj + 2] = (short) v3;

            indices[jj + 3] = (short) v2;
            indices[jj + 4] = (short) v4;
            indices[jj + 5] = (short) v3;
        }

        int j = xSections - 1;
        for (int i = 0; i < zSections - 1; i++) {
            int jj = (j * zSections * 6) + i * 6;

            int v1 = zSections * (j + 0) + i;
            int v2 = i;
            int v3 = zSections * (j + 0) + i + 1;
            int v4 = i + 1;

            indices[jj + 0] = (short) v1;
            indices[jj + 1] = (short) v2;
            indices[jj + 2] = (short) v3;

            indices[jj + 3] = (short) v2;
            indices[jj + 4] = (short) v4;
            indices[jj + 5] = (short) v3;
        }
        int jj = (j * zSections * 6) + (zSections - 1) * 6;

        int v1 = zSections * (j + 0) + (zSections - 1);
        int v2 = (zSections - 1);
        int v3 = zSections * (j + 0);
        int v4 = 0;

        indices[jj + 0] = (short) v1;
        indices[jj + 1] = (short) v2;
        indices[jj + 2] = (short) v3;

        indices[jj + 3] = (short) v2;
        indices[jj + 4] = (short) v4;
        indices[jj + 5] = (short) v3;
    }

    private static void createTorusIndices2(int zSections, int xSections, short[] indices) {
        for (int j = 0; j < xSections; j++) {
            for (int i = 0; i < zSections; i++) {
                int jj = (j * zSections * 6) + i * 6;

                int v1 = (zSections + 1) * (j + 0) + i;
                int v2 = (zSections + 1) * (j + 1) + i;
                int v3 = (zSections + 1) * (j + 0) + i + 1;
                int v4 = (zSections + 1) * (j + 1) + i + 1;

                indices[jj + 0] = (short) v1;
                indices[jj + 1] = (short) v2;
                indices[jj + 2] = (short) v3;

                indices[jj + 3] = (short) v2;
                indices[jj + 4] = (short) v4;
                indices[jj + 5] = (short) v3;
            }
        }
    }

    private static void createTorusVertice(float zDiameter, float xDiameter, int zSections, int xSections, float zAngle, float xAngle, float[] vertices, int j, int i, int v) {
        float xx = (float) (Math.cos(xAngle * j + Math.PI)) * xDiameter;
        float zx = (float) (Math.sin(xAngle * j + Math.PI)) * xDiameter;

        float xz = (float) (Math.sin(zAngle * i)) * (zDiameter + xx);
        float yz = (float) (Math.cos(zAngle * i)) * (zDiameter + xx);

        float x0 = (float) (Math.sin(zAngle * i)) * (zDiameter);
        float y0 = (float) (Math.cos(zAngle * i)) * (zDiameter);

        vertices[v] = xz;
        vertices[v + 1] = yz;
        vertices[v + 2] = zx;

        // Colours
        vertices[v + 3] = 1;
        vertices[v + 4] = 1;
        vertices[v + 5] = 1;
        vertices[v + 6] = 1;

        // Normal
        vector.x = xz - x0;
        vector.y = yz - y0;
        vector.z = zx;
        vector.nor();

        vertices[v + 7] = vector.x;
        vertices[v + 8] = vector.y;
        vertices[v + 9] = vector.z;

        // Texture coordinates
        vertices[v + 10] = (float) ((float)i / (zSections - 1));
        vertices[v + 11] = (float) ((float)j / (xSections - 1));
    }

    private static Vector3 vector = new Vector3();
    
    private static void createTorusVertice2(float zDiameter, float xDiameter, int zSections, int xSections, float zAngle, float xAngle, float[] vertices, int j, int i, int v) {
        float xx = (float) (Math.cos(xAngle * j + Math.PI)) * xDiameter;
        float zx = (float) (Math.sin(xAngle * j + Math.PI)) * xDiameter;

        float xz = (float) (Math.sin(zAngle * i)) * (zDiameter + xx);
        float yz = (float) (Math.cos(zAngle * i)) * (zDiameter + xx);

        float x0 = (float) (Math.sin(zAngle * i)) * (zDiameter);
        float y0 = (float) (Math.cos(zAngle * i)) * (zDiameter);

        vertices[v] = xz;
        vertices[v + 1] = yz;
        vertices[v + 2] = zx;

        // Colours
        vertices[v + 3] = 1;
        vertices[v + 4] = 1;
        vertices[v + 5] = 1;
        vertices[v + 6] = 1;

        // Normal
        vector.x = xz - x0;
        vector.y = yz - y0;
        vector.z = zx;
        vector.nor();

        vertices[v + 7] = vector.x;
        vertices[v + 8] = vector.y;
        vertices[v + 9] = vector.z;

        // Texture coordinates
        vertices[v + 10] = (float) ((float)i / zSections);
        vertices[v + 11] = (float) ((float)j / xSections);
    }

    private static void createTorusVertice3(float zDiameter, float xDiameter, int zSections, int xSections, float zAngle, float xAngle, 
            float[] vertices, int j, int i, int v, int bone1, float bone1Weight, int bone2, float bone2Weight) {
        float xx = (float) (Math.cos(xAngle * j + Math.PI)) * xDiameter;
        float zx = (float) (Math.sin(xAngle * j + Math.PI)) * xDiameter;

        float xz = (float) (Math.sin(zAngle * i)) * (zDiameter + xx);
        float yz = (float) (Math.cos(zAngle * i)) * (zDiameter + xx);

        float x0 = (float) (Math.sin(zAngle * i)) * (zDiameter);
        float y0 = (float) (Math.cos(zAngle * i)) * (zDiameter);

        vertices[v] = xz;
        vertices[v + 1] = yz;
        vertices[v + 2] = zx;

        // Colours
        vertices[v + 3] = 1;
        vertices[v + 4] = 1;
        vertices[v + 5] = 1;
        vertices[v + 6] = 1;

        // Normal
        vector.x = xz - x0;
        vector.y = yz - y0;
        vector.z = zx;
        vector.nor();

        vertices[v + 7] = vector.x;
        vertices[v + 8] = vector.y;
        vertices[v + 9] = vector.z;

        // Texture coordinates
        vertices[v + 10] = (float) ((float)i / zSections);
        vertices[v + 11] = (float) ((float)j / xSections);
        
        vertices[v + 12] = bone1;
        vertices[v + 13] = bone1Weight;
        
        vertices[v + 14] = bone2;
        vertices[v + 15] = bone2Weight;
    }

    private static void createUnevenTorusVertice(float zDiameter, float xDiameter, int zSections, int xSections, float zAngle, float xAngle, float[] vertices, int j, int i, int v, float deviation) {
        float xx = (float) (Math.cos(xAngle * j + Math.PI)) * xDiameter;
        float zx = (float) (Math.sin(xAngle * j + Math.PI)) * xDiameter;

        float xz = (float) (Math.sin(zAngle * i)) * (zDiameter + xx + (float)Math.random() * deviation);
        float yz = (float) (Math.cos(zAngle * i)) * (zDiameter + xx + (float)Math.random() * deviation);

        float x0 = (float) (Math.sin(zAngle * i)) * (zDiameter);
        float y0 = (float) (Math.cos(zAngle * i)) * (zDiameter);

        vertices[v] = xz;
        vertices[v + 1] = yz;
        vertices[v + 2] = zx;

        // Colours
        vertices[v + 3] = 1;
        vertices[v + 4] = 1;
        vertices[v + 5] = 1;
        vertices[v + 6] = 1;

        // Normal
        vector.x = xz - x0;
        vector.y = yz - y0;
        vector.z = zx;
        vector.nor();

        vertices[v + 7] = vector.x;
        vertices[v + 8] = vector.y;
        vertices[v + 9] = vector.z;

        // Texture coordinates
        vertices[v + 10] = (float) ((float)i / zSections);
        vertices[v + 11] = (float) ((float)j / xSections);
    }
}
