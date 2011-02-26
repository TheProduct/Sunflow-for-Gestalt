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


import gestalt.extension.quadline.QuadFragment;
import gestalt.extension.quadline.QuadLine;
import gestalt.render.Drawable;


public class QuadLineTranslator
        implements SunflowTranslator {

    public boolean isClass(Drawable theDrawable) {
        return theDrawable instanceof QuadLine;
    }

    public void parse(GestaltSunflowRenderer theParent,
                      Drawable theDrawable) {
        final QuadLine myDrawable = (QuadLine)theDrawable;

        if (myDrawable instanceof SunflowMaterial) {
            ((SunflowMaterial)myDrawable).sendMaterial(theParent);
        } else {
            theParent.sendAmbientOcclusionMaterial(myDrawable.material().color);
        }

        final QuadFragment[] myFragments = myDrawable.getLineFragments();
        float[] myVertices = new float[(myFragments.length - 1) * 2 * 3 * 3];
        for (int i = 0; i < myFragments.length - 1; i++) {
            final QuadFragment a = myFragments[i];
            final QuadFragment b = myFragments[i + 1];
            final int myOffset = i * 2 * 3 * 3;
            int c = 0;
            myVertices[myOffset + c++] = a.pointA.x;
            myVertices[myOffset + c++] = a.pointA.y;
            myVertices[myOffset + c++] = a.pointA.z;

            myVertices[myOffset + c++] = b.pointA.x;
            myVertices[myOffset + c++] = b.pointA.y;
            myVertices[myOffset + c++] = b.pointA.z;

            myVertices[myOffset + c++] = b.pointB.x;
            myVertices[myOffset + c++] = b.pointB.y;
            myVertices[myOffset + c++] = b.pointB.z;

            myVertices[myOffset + c++] = a.pointA.x;
            myVertices[myOffset + c++] = a.pointA.y;
            myVertices[myOffset + c++] = a.pointA.z;

            myVertices[myOffset + c++] = b.pointB.x;
            myVertices[myOffset + c++] = b.pointB.y;
            myVertices[myOffset + c++] = b.pointB.z;

            myVertices[myOffset + c++] = a.pointB.x;
            myVertices[myOffset + c++] = a.pointB.y;
            myVertices[myOffset + c++] = a.pointB.z;
        }

        /* geometry */
        theParent.sendTriangles(myVertices,
                                myDrawable.transform(),
                                myDrawable.rotation(),
                                myDrawable.scale());

        theParent.bumpDrawableID();
    }
}
