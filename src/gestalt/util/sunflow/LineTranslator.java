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
import gestalt.shape.Line;
import mathematik.Matrix3f;

import mathematik.TransformMatrix4f;
import mathematik.Vector3f;


public class LineTranslator
        implements SunflowTranslator {

    public boolean isClass(Drawable theDrawable) {
        return theDrawable instanceof Line;
    }

    public void parse(GestaltSunflowRenderer theParent, Drawable pDrawable) {
        final Line myDrawable = (Line)pDrawable;

        if (myDrawable instanceof SunflowMaterial) {
            ((SunflowMaterial)myDrawable).sendMaterial(theParent);
        } else {
            theParent.sendAmbientOcclusionMaterial(myDrawable.material().color);
        }

        /* geometry */
        theParent.sendLines(myDrawable,
                            Util.convertVector3fToFloat(myDrawable.points),
                            myDrawable.transform(),
                            myDrawable.rotation(),
                            myDrawable.scale());

        theParent.bumpDrawableID();
    }
//
//    public void sendLines(final GestaltSunflowRenderer theParent,
//                          final Line myDrawable,
//                          final float[] theVertices,
//                          final TransformMatrix4f theTransform,
//                          final Vector3f theRotation,
//                          final Vector3f theScale) {
//        final float[] myVertices = new float[theVertices.length];
//
//        Matrix3f myRotationMatrix = null;
//        if (theTransform != null) {
//            myRotationMatrix = new Matrix3f(theTransform.rotation);
//            myRotationMatrix.invert();
//        }
//
//        for (int i = 0; i < myVertices.length; i += 3) {
//            final Vector3f myVertex = new Vector3f(theVertices[i + 0],
//                                                   theVertices[i + 1],
//                                                   theVertices[i + 2]);
//            if (theScale != null) {
//                myVertex.scale(theScale);
//            }
//            if (theRotation != null
//                    && (theRotation.x != 0 || theRotation.y != 0 || theRotation.z != 0)) {
//                final TransformMatrix4f myTempRotationMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
//                myTempRotationMatrix.rotation.setXYZRotation(theRotation);
//                myTempRotationMatrix.transform(myVertex);
//            }
//            if (theTransform != null) {
//                myRotationMatrix.transform(myVertex);
//                myVertex.add(theTransform.translation);
//            }
//
//            /* --- */
//            myVertices[i + 0] = myVertex.x;
//            myVertices[i + 1] = myVertex.y;
//            myVertices[i + 2] = myVertex.z;
//        }
//
//        /* we ll do with a line-strip for now */
//        theParent.sunflow().parameter("segments", myDrawable.points.length - 1);
//        theParent.sunflow().parameter("widths", myDrawable.linewidth);
//        theParent.sunflow().parameter("points", "point", "vertex", myVertices);
//        theParent.sunflow().geometry("myPrimitive" + GestaltSunflowRenderer._myDrawableID, "hair");
//
//        theParent.sunflow().parameter("shaders",
//                                      GestaltSunflowRenderer.SHADER_NAME + GestaltSunflowRenderer._myDrawableID);
//        theParent.sunflow().instance("myPrimitive" + GestaltSunflowRenderer._myDrawableID + ".instance",
//                                     "myPrimitive" + GestaltSunflowRenderer._myDrawableID);
//    }
}
