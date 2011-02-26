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


import com.sun.j3d.utils.geometry.GeometryInfo;
import java.util.Vector;


import gestalt.extension.quadline.QuadLine;
import gestalt.impl.jogl.shape.JoglTriangle;
import gestalt.render.BasicRenderer;
import gestalt.render.bin.AbstractBin;
import gestalt.shape.Triangle;
import gestalt.shape.Triangles;
import javax.media.j3d.GeometryArray;
import javax.vecmath.Point3f;

import mathematik.Random;
import mathematik.Vector3f;

import teilchen.Particle;
import teilchen.util.ParticleTrail;


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

    public static void mapLineToQuadline(QuadLine theLine, ParticleTrail theTrail) {
        final Vector<Particle> myFragments = theTrail.fragments();
        if (myFragments.size() < 2) {
            return;
        } else {
            theLine.points = new Vector3f[myFragments.size()];
            for (int i = 0; i < theLine.points.length; i++) {
                theLine.points[i] = myFragments.get(i).position();
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

    public static Vector<Vector3f[]> convertToTriangles(final Vector<Vector<Vector<Vector3f>>> theWord) {
        final Vector<Vector3f[]> myCharTriangles = new Vector<Vector3f[]>();
        for (int i = 0; i < theWord.size(); i++) {
            final Vector<Vector3f> myVertices = new Vector<Vector3f>();
            final Vector<Integer> myVertivesPerShape = new Vector<Integer>();
            final Vector<Vector<Vector3f>> myCharacter = theWord.get(i);
            for (int j = 0; j < myCharacter.size(); j++) {
                final Vector<Vector3f> myOutline = myCharacter.get(j);
                myVertivesPerShape.add(myOutline.size());
                for (Vector3f v : myOutline) {
                    myVertices.add(v);
                }
            }
            if (myCharacter.size() > 0) {
                myCharTriangles.add(triangulate(werkzeug.Util.toFloatArray(myVertices),
                                                werkzeug.Util.toArray(myVertivesPerShape),
                                                new int[] {myCharacter.size()}));
            }
        }
        return myCharTriangles;
    }

    public static Vector3f[] triangulate(float[] theData,
                                         int[] theStripCount,
                                         int[] theContourCount) {
        final GeometryInfo myGeometryInfo = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        myGeometryInfo.setCoordinates(theData);
        myGeometryInfo.setStripCounts(theStripCount);
        myGeometryInfo.setContourCounts(theContourCount);

        final GeometryArray myGeometryArray = myGeometryInfo.getGeometryArray();
        final Vector3f[] myPoints = new Vector3f[myGeometryArray.getValidVertexCount()];
        for (int i = 0; i < myGeometryArray.getValidVertexCount(); i++) {
            final Point3f p = new Point3f();
            myGeometryArray.getCoordinate(i, p);
            myPoints[i] = new Vector3f();
            myPoints[i].set(p.x, p.y, p.z);
        }

        return myPoints;
    }
}
