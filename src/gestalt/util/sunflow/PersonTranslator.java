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


import gestalt.util.sunflow.Persons.Person;
import gestalt.render.Drawable;
import gestalt.shape.Mesh;


public class PersonTranslator
        implements SunflowTranslator {

    public boolean isClass(Drawable theDrawable) {
        return theDrawable instanceof Persons;
    }

    public void parse(GestaltSunflowRenderer theParent,
                      Drawable theDrawable) {
        final Persons myPerson = (Persons)theDrawable;
        final Mesh myMesh = myPerson.mesh();

        for (Person myPersonInstance : myPerson.persons()) {
            theParent.sendTexturedAmbientOcclusionMaterial(myMesh.material().color4f(),
                                                           myPerson.texturefile());
            theParent.sendTriangles(myMesh.vertices(),
                                    myMesh.normals(),
                                    myMesh.texcoords(),
                                    myPersonInstance.transform,
                                    myPersonInstance.rotation,
                                    myPersonInstance.scale);
            theParent.bumpDrawableID();
        }
    }
}
