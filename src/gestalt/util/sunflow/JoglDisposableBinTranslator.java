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


import gestalt.Gestalt;
import gestalt.render.bin.DisposableBin;
import gestalt.shape.Line;
import gestalt.shape.Mesh;
import gestalt.render.Drawable;
import gestalt.material.Color;
import gestalt.shape.Line;
import java.util.Vector;
import mathematik.Vector3f;


public class JoglDisposableBinTranslator
        implements SunflowTranslator {

    public boolean mParseLineAsIndividualShape = true;

    public boolean isClass(Drawable theDrawable) {
        return theDrawable instanceof DisposableBin;
    }

    public void parse(GestaltSunflowRenderer theParent,
                      Drawable theDrawable) {
        final DisposableBin g = (DisposableBin)theDrawable;

        /* delegate triangles */
        {
            final MeshTranslator mTranslator = new MeshTranslator();
            final Vector<Vector3f> mVertexData = new Vector<Vector3f>();
            final Vector<Color> mColorData = new Vector<Color>();
            g.collectTriangleData(mVertexData, mColorData);
            final Mesh mMesh = new Mesh(werkzeug.Util.toArray3f(mVertexData), 3,
                                                werkzeug.Util.toArray4f(mColorData), 4,
                                                null, 2,
                                                null,
                                                Gestalt.MESH_TRIANGLES);
            mMesh.position().set(g.position);
            mMesh.scale().set(g.scale);
            mTranslator.parse(theParent, mMesh);
        }

        /* delegate lines */
        if (mParseLineAsIndividualShape) {
            final LineTranslator mTranslator = new LineTranslator();
            final Vector<DisposableBin.Line> mLines = g.collectLines();
            for (DisposableBin.Line mGLine : mLines) {
                final Line mLine = new Line();
                mLine.position().set(g.position);
                mLine.scale().set(g.scale);
                mLine.points = new Vector3f[] {new Vector3f(mGLine.startX,
                                                            mGLine.startY,
                                                            mGLine.startZ),
                                               new Vector3f(mGLine.endX,
                                                            mGLine.endY,
                                                            mGLine.endZ)};
                mLine.linewidth = mGLine.width;
                mLine.material().color4f().set(mGLine.color.r, mGLine.color.g, mGLine.color.b, mGLine.color.a);
                mLine.setPrimitive(Gestalt.LINE_PRIMITIVE_TYPE_LINES);
                mTranslator.parse(theParent, mLine);
            }
        } else {
            final LineTranslator mTranslator = new LineTranslator();
            final Vector<Vector3f> mVertexData = new Vector<Vector3f>();
            final Vector<Color> mColorData = new Vector<Color>(); /* sunflow can t interpret color4f data */
            g.collectLineData(mVertexData, mColorData);
            final Line mLine = new Line();
            mLine.position().set(g.position);
            mLine.scale().set(g.scale);
            mLine.points = werkzeug.Util.toArrayVector3f(mVertexData);
            mLine.colors = werkzeug.Util.toArrayColor(mColorData);
            mLine.setPrimitive(Gestalt.LINE_PRIMITIVE_TYPE_LINES);
            /* we are sending lines not line-strips */
            mTranslator.parse(theParent, mLine);
        }
    }
}


