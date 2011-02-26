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


import gestalt.util.font.TextOutlineCreator;
import gestalt.util.sunflow.GestaltSunflowRenderer;
import gestalt.util.sunflow.Util;
import data.Resource;
import gestalt.G;
import gestalt.impl.jogl.shape.JoglMesh;
import gestalt.render.BasicRenderer;
import gestalt.render.SketchRenderer;
import gestalt.render.bin.AbstractBin;
import gestalt.render.bin.RenderBin;
import gestalt.shape.Color;
import java.util.Vector;
import mathematik.Vector3f;


public class SketchTestOutlineExtractor
        extends SketchRenderer {

    private Vector<Vector<Vector<Vector3f>>> mWordOutlines;

    private final String mString = "When I was a young boy ?";

    private Color[] mColors = {new Color(0, 0, 1),
                               new Color(1, 0.5f, 0),
                               new Color(1, 0, 1),
                               new Color(1, 0, 0.5f),
                               new Color(1, 1, 0),
                               new Color(0, 0.5f, 1)};

    public void setup() {
        cameramover(true);
        camera().setMode(CAMERA_MODE_LOOK_AT);
        camera().position().set(-271.0138, 326.966, 476.93112);
        bin(BIN_2D_FOREGROUND).add(stats_view());
        bin(BIN_3D).add(g());
        backgroundcolor().set(0.2f);

        final TextOutlineCreator myOutlineExtractor = new TextOutlineCreator("Helvetica", 64);
        mWordOutlines = myOutlineExtractor.getOutlineFromText(mString);

        for (final Vector<Vector<Vector3f>> myCharacterOutlines : mWordOutlines) {
            for (final Vector<Vector3f> myShapeOutlines : myCharacterOutlines) {
                for (final Vector3f p : myShapeOutlines) {
                    p.y *= -1;
                    p.x += -200;
                }
            }
        }
    }

    public void loop(final float theDeltaTime) {
        /* outlines */
        int myColor = 0;
        int myCharID = 0;
        for (final Vector<Vector<Vector3f>> myCharacterOutlines : mWordOutlines) {
            for (final Vector<Vector3f> myShapeOutlines : myCharacterOutlines) {
                myColor++;
                myColor %= mColors.length;
                g().color(mColors[myColor]);
                Vector3f myPreviousPoint = null;
                for (final Vector3f p : myShapeOutlines) {
                    if (myPreviousPoint != null) {
                        g().line(p.x, p.y, 0, myPreviousPoint.x, myPreviousPoint.y, 0);
                    }
                    myPreviousPoint = p;
                }
            }
            myCharID++;
        }

        /* triangles  */
        g().color(1);
        g().wireframe = true;
        g().wireframe_color.set(0.5f);
        final Vector<Vector3f[]> myTriangles = Util.convertToTriangles(mWordOutlines);
        for (Vector3f[] myCharacters : myTriangles) {
            for (int i = 0; i < myCharacters.length; i += 3) {
                g().triangle(myCharacters[i + 0],
                             myCharacters[i + 1],
                             myCharacters[i + 2]);
            }
        }
    }

    public void keyPressed() {
        final Vector<Vector3f[]> myTriangles = Util.convertToTriangles(mWordOutlines);
        final RenderBin myBin = new RenderBin();
        for (int i = 0; i < myTriangles.size(); i++) {
            final JoglMesh myMesh = new JoglMesh(werkzeug.Util.toArray(myTriangles.get(i)), 3,
                                                 null, 4,
                                                 null, 2,
                                                 null,
                                                 MESH_TRIANGLES);
            myMesh.material().color.set(1, 0, 0, 1);
            myBin.add(myMesh);
        }

        if (key == ' ') {
            render(myBin, this, false);
        }
        if (key == 'p') {
            render(myBin, this, true);
        }
    }

    public static void render(AbstractBin theBin, BasicRenderer theRenderer, boolean thePreviewFlag) {
        GestaltSunflowRenderer.preview = thePreviewFlag;
        GestaltSunflowRenderer.floor = true;
        GestaltSunflowRenderer.floor().y = 0;
        GestaltSunflowRenderer.SAMPLES = 16;
        GestaltSunflowRenderer.render(theRenderer,
                                      theBin,
                                      Resource.getPath("") + "/-" + SketchTestOutlineExtractor.class.getSimpleName() + System.currentTimeMillis() + ".png");
    }

    public static void main(String[] args) {
        G.init(SketchTestOutlineExtractor.class);
    }
}
