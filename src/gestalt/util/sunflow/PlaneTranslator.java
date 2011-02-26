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
import gestalt.render.Drawable;
import gestalt.shape.Plane;

import mathematik.Vector3f;


public class PlaneTranslator
        implements SunflowTranslator {

    private final static float[] myNormals = {
        0, 0, 1,
        0, 0, 1,
        0, 0, 1,
        0, 0, 1,
        0, 0, 1,
        0, 0, 1
    };

    private final static float[] myTexCoords = {
        0, 0,
        1, 0,
        1, 1,
        1, 1,
        0, 1,
        0, 0
    };

    public boolean isClass(Drawable theDrawable) {
        return theDrawable instanceof Plane;
    }

    public void parse(GestaltSunflowRenderer theParent,
                      Drawable theDrawable) {
        final Plane myDrawable = (Plane)theDrawable;

        if (myDrawable instanceof SunflowMaterial) {
            ((SunflowMaterial)myDrawable).sendMaterial(theParent);
        } else {
            theParent.sendAmbientOcclusionMaterial(myDrawable.material().color);
        }

        /* geometry */
        final Vector3f myOrigin = getOriginOffset(myDrawable.origin());
        float[] myVertices = {
            0 + myOrigin.x, 0 + myOrigin.y, 0 + myOrigin.z,
            1 + myOrigin.x, 0 + myOrigin.y, 0 + myOrigin.z,
            1 + myOrigin.x, 1 + myOrigin.y, 0 + myOrigin.z,
            1 + myOrigin.x, 1 + myOrigin.y, 0 + myOrigin.z,
            0 + myOrigin.x, 1 + myOrigin.y, 0 + myOrigin.z,
            0 + myOrigin.x, 0 + myOrigin.y, 0 + myOrigin.z
        };

        theParent.sendTriangles(myVertices,
                                myNormals,
                                myTexCoords,
                                myDrawable.transform(),
                                myDrawable.rotation(),
                                myDrawable.scale());

        theParent.bumpDrawableID();
    }

    private Vector3f getOriginOffset(int theOrigin) {
        final Vector3f myOrigin = new Vector3f();
        switch (theOrigin) {
            case Gestalt.SHAPE_ORIGIN_BOTTOM_LEFT:
                break;
            case Gestalt.SHAPE_ORIGIN_BOTTOM_RIGHT:
                myOrigin.set(-1, 0, 0);
                break;
            case Gestalt.SHAPE_ORIGIN_TOP_LEFT:
                myOrigin.set(0, -1, 0);
                break;
            case Gestalt.SHAPE_ORIGIN_TOP_RIGHT:
                myOrigin.set(-1, -1, 0);
                break;
            case Gestalt.SHAPE_ORIGIN_CENTERED:
                myOrigin.set(-0.5f, -0.5f, 0);
                break;
            case Gestalt.SHAPE_ORIGIN_CENTERED_LEFT:
                myOrigin.set(0, -0.5f, 0);
                break;
            case Gestalt.SHAPE_ORIGIN_CENTERED_RIGHT:
                myOrigin.set(-1, -0.5f, 0);
                break;
            case Gestalt.SHAPE_ORIGIN_BOTTOM_CENTERED:
                myOrigin.set(-0.5f, 0, 0);
                break;
            case Gestalt.SHAPE_ORIGIN_TOP_CENTERED:
                myOrigin.set(-0.5f, -1, 0);
                break;
        }
        return myOrigin;
    }
}
