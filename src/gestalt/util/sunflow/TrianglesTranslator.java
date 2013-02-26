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


import gestalt.render.Drawable;
import gestalt.shape.Triangle;
import gestalt.shape.Triangles;


public class TrianglesTranslator
        implements SunflowTranslator {

    public boolean isClass(Drawable theDrawable) {
        return theDrawable instanceof Triangles;
    }

    public void parse(GestaltSunflowRenderer theParent,
                      Drawable theDrawable) {
        final Triangles myDrawable = (Triangles)theDrawable;

        if (myDrawable instanceof SunflowMaterial) {
            ((SunflowMaterial)myDrawable).sendMaterial(theParent);
        } else {
            theParent.sendAmbientOcclusionMaterial(myDrawable.material().color4f());
        }

        final float[] myVertices = new float[myDrawable.triangles().size() * 3 * 3];
        for (int i = 0; i < myDrawable.triangles().size(); i++) {
            final Triangle q = myDrawable.triangles().get(i);
            final int myOffset = i * 3 * 3;
            int c = 0;

            myVertices[myOffset + c++] = q.a().position.x;
            myVertices[myOffset + c++] = q.a().position.y;
            myVertices[myOffset + c++] = q.a().position.z;

            myVertices[myOffset + c++] = q.b().position.x;
            myVertices[myOffset + c++] = q.b().position.y;
            myVertices[myOffset + c++] = q.b().position.z;

            myVertices[myOffset + c++] = q.c().position.x;
            myVertices[myOffset + c++] = q.c().position.y;
            myVertices[myOffset + c++] = q.c().position.z;
        }

        /* geometry */
        /** @todo add texture coordinates and normals */
        theParent.sendTriangles(myVertices,
                                null,
                                null,
                                myDrawable.transform(),
                                myDrawable.rotation(),
                                myDrawable.scale());

        theParent.bumpDrawableID();
    }
}
