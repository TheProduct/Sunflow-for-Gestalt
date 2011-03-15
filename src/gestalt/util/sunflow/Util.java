/*
 * Sunflow Plugin for Gestalt
 *
 * Copyright (C) 2011 The Product GbR Kochlik + Paul
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * {@link http://www.gnu.org/licenses/lgpl.html}
 *
 */


package gestalt.util.sunflow;


import java.util.Vector;

import gestalt.extension.quadline.QuadLine;
import gestalt.impl.jogl.shape.JoglTriangle;
import gestalt.render.BasicRenderer;
import gestalt.render.bin.AbstractBin;
import gestalt.shape.Triangle;
import gestalt.shape.Triangles;
import mathematik.Random;

import mathematik.Vector3f;


public class Util {

    public static final void updateTriangles(Triangles theTriangles, Vector3f[] theVertices) {
        for (int i = 0; i < theVertices.length; i += 3) {
            final Triangle myTriangle = new JoglTriangle();
            myTriangle.a().position.set(theVertices[i + 0]);
            myTriangle.b().position.set(theVertices[i + 1]);
            myTriangle.c().position.set(theVertices[i + 2]);
            final Vector3f myNormal = mathematik.Util.createNormal(myTriangle.a().position,
                                                                   myTriangle.b().position,
                                                                   myTriangle.c().position);
            myTriangle.a().normal.set(myNormal);
            myTriangle.b().normal.set(myNormal);
            myTriangle.c().normal.set(myNormal);
            myTriangle.a().color = null;
            myTriangle.b().color = null;
            myTriangle.c().color = null;
            theTriangles.triangles().add(myTriangle);
        }
    }

    public static final void updateTriangles(Triangles theTriangles, Vector<Vector3f> theVertices) {
        for (int i = 0; i < theVertices.size(); i += 3) {
            final Triangle myTriangle = new JoglTriangle();
            myTriangle.a().position.set(theVertices.get(i + 0));
            myTriangle.b().position.set(theVertices.get(i + 1));
            myTriangle.c().position.set(theVertices.get(i + 2));
            final Vector3f myNormal = mathematik.Util.createNormal(myTriangle.a().position,
                                                                   myTriangle.b().position,
                                                                   myTriangle.c().position);
            myTriangle.a().normal.set(myNormal);
            myTriangle.b().normal.set(myNormal);
            myTriangle.c().normal.set(myNormal);
            myTriangle.a().color = null;
            myTriangle.b().color = null;
            myTriangle.c().color = null;
            theTriangles.triangles().add(myTriangle);
        }
    }

    public static float random(float b) {
        return random(0, b);
    }

    public static float random(float a, float b) {
        return new Random().getFloat(a, b);
    }

    public static void mapLineToQuadline(QuadLine theLine, Vector<Vector3f> pLine) {
        if (pLine.size() < 2) {
            return;
        } else {
            theLine.points = new Vector3f[pLine.size()];
            for (int i = 0; i < theLine.points.length; i++) {
                theLine.points[i] = pLine.get(i);
            }
            theLine.update();
        }
    }

    public static void render(boolean pPreview,
                              AbstractBin pBin,
                              BasicRenderer pRenderer,
                              Vector3f pFloor,
                              String pPNGFile) {
        GestaltSunflowRenderer.preview = pPreview;
        if (pFloor != null) {
            GestaltSunflowRenderer.floor = true;
            GestaltSunflowRenderer.floor().set(pFloor);
        } else {
            GestaltSunflowRenderer.floor = false;
        }
        GestaltSunflowRenderer.render(pRenderer,
                                      pBin,
                                      pPNGFile);
    }

    public static float[] convertVector3fToFloat(final Vector3f[] pVector) {
        final float[] mFloats = new float[pVector.length * 3];
        for (int i = 0; i < pVector.length; i++) {
            final Vector3f v = pVector[i];
            mFloats[i * 3 + 0] = v.x;
            mFloats[i * 3 + 1] = v.y;
            mFloats[i * 3 + 2] = v.z;
        }
        return mFloats;
    }
}
