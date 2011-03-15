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


package gestalt.util.sunflow.demo;


import data.Resource;
import gestalt.G;
import gestalt.model.ModelData;
import gestalt.model.ModelLoaderOBJ;
import gestalt.render.SketchRenderer;
import gestalt.util.sunflow.GestaltSunflowRenderer;
import gestalt.util.sunflow.Persons;
import gestalt.util.sunflow.Persons.Person;
import gestalt.util.sunflow.Util;
import java.util.Vector;
import mathematik.Vector3f;


public class SketchModelHairThing
        extends SketchRenderer {

    private Vector<Line> mLines = new Vector<Line>();

    private ModelData mMesh;

    private Vector<Vector3f> mPositions;

    private class Line {

        private Vector3f a = new Vector3f();

        private Vector3f b = new Vector3f();

        private float width = 1.0f;
    }

    public void setup() {
        cameramover(true);
        camera().setMode(CAMERA_MODE_LOOK_AT);
        camera().position().set(-271.0138, 326.966, 476.93112);
        bin(BIN_2D_FOREGROUND).add(stats_view());
        bin(BIN_3D).add(g());
        backgroundcolor().set(0.5f);

        final Persons myPersons = new Persons();
        bin(BIN_3D).add(myPersons);

        for (int i = 0; i < 30; i++) {
            final Person myPerson = myPersons.instance();
            myPerson.position.set(-50 + random(-50, 0), 0, random(-50, 50));
            myPerson.rotation.y = random(-PI / 4, PI / 4);
            myPerson.scale.scale(0.05f);
        }

        g().position.y = 20;

        prepareData();
        createLineMesh(10);
    }

    private void prepareData() {
        mMesh = ModelLoaderOBJ.getModelData(Resource.getStream("venusBody.obj"));
        mPositions = convertToVector3f(mMesh.vertices);
        scalePositions(mPositions, 30.0f);
    }

    private void createLineMesh(int pNumberOfLines) {
        final float LINEWIDTH = 0.25f;
        final float PROXIMITY = 50;
        for (int i = 0; i < pNumberOfLines; i++) {
            final Line mLine = new Line();
            mLine.a.set(mPositions.get((int)random(0, mPositions.size())));
            mLine.b.set(findProximalPoint(mPositions, mLine.a, PROXIMITY));
            mLine.width = LINEWIDTH;
            mLines.add(mLine);
        }
    }

    private Vector3f findProximalPoint(final Vector<Vector3f> mPositions,
                                       final Vector3f pPosition,
                                       float pProximity) {
        final float mMinimalDistance = 1.0f;
        Vector3f mNewPosition = mPositions.get((int)random(0, mPositions.size()));
        while (pPosition.distance(mNewPosition) > pProximity
                || pPosition.distance(mNewPosition) < mMinimalDistance
                || pPosition == mNewPosition) {
            mNewPosition = mPositions.get((int)random(0, mPositions.size()));
        }
        return mNewPosition;
    }

    private void scalePositions(Vector<Vector3f> pPositions, float pScale) {
        for (Vector3f v : pPositions) {
            v.scale(pScale);
        }
    }

    private Vector<Vector3f> convertToVector3f(final float[] pData) {
        final Vector<Vector3f> mVector3fs = new Vector<Vector3f>();
        for (int i = 0; i < pData.length; i += 3) {
            final Vector3f v = new Vector3f(pData[i + 0],
                                            pData[i + 1],
                                            pData[i + 2]);
            mVector3fs.add(v);
        }
        return mVector3fs;
    }

    public void loop(final float theDeltaTime) {
        if (mouseDown) {
            createLineMesh(10);
        }

        g().color(1);
        for (Line mLine : mLines) {
            g().linewidth(mLine.width);
            g().line(mLine.a, mLine.b);
        }
    }

    public void keyPressed(char c, int theKeyCode) {
        if (c == '1' || c == '2' || c == 'p') {
            GestaltSunflowRenderer.scale_viewport = (c == '1' ? 1f : 2f);
            Util.render(c == 'p',
                        bin(BIN_3D),
                        this,
                        new Vector3f(),
                        System.getProperty("user.home") + "/Desktop/" + getClass().getSimpleName()
                    + "-" + (c == 'p' ? "p" : "")
                    + "-" + werkzeug.Util.now() + ".png");
        }
    }

    public static void main(String[] args) {
        G.init(SketchModelHairThing.class);
    }
}


