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


import java.io.InputStream;
import java.util.Vector;

import gestalt.context.GLContext;
import gestalt.impl.jogl.shape.JoglMesh;
import gestalt.impl.jogl.shape.material.JoglTexturePlugin;
import gestalt.model.Model;
import gestalt.model.ModelData;
import gestalt.model.ModelLoaderOBJ;
import gestalt.shape.AbstractDrawable;
import gestalt.texture.Bitmap;
import gestalt.texture.Bitmaps;

import mathematik.TransformMatrix4f;
import mathematik.Vector3f;

import data.Resource;


public class Persons
        extends AbstractDrawable {

    private final JoglMesh _myPersonModel;

    private final String _myImageFile;

    private final Vector<Person> _myPersons = new Vector<Person>();

    public Persons() {
        _myImageFile = Resource.getPath("person/person0001.jpg");
        _myPersonModel = model(Resource.getStream("person/person0001.obj"), _myImageFile);
    }

    public Person instance() {
        final Person myPersonInstance = new Person();
        _myPersons.add(myPersonInstance);
        return myPersonInstance;
    }

    private JoglMesh model(InputStream theModelFile,
                           String theImageFile) {
        final ModelData myModelData = ModelLoaderOBJ.getModelData(theModelFile);
        final JoglMesh myModelMesh = new JoglMesh(myModelData.vertices, 3,
                                                  myModelData.vertexColors, 4,
                                                  myModelData.texCoordinates, 2,
                                                  myModelData.normals,
                                                  myModelData.primitive);
        final Model myModel = new Model(myModelData, myModelMesh);
        final Bitmap myBitmap = Bitmaps.getBitmap(theImageFile);
        final JoglTexturePlugin myTexture = new JoglTexturePlugin();
        myTexture.scale().y = -1;
        myTexture.load(myBitmap);
        myModel.mesh().material().addTexture(myTexture);
        return myModelMesh;
    }

    public void draw(GLContext theRenderContext) {
        for (Person myPersonInstance : persons()) {
            _myPersonModel.transform().set(myPersonInstance.transform);
            _myPersonModel.rotation().set(myPersonInstance.rotation);
            _myPersonModel.scale().set(myPersonInstance.scale);
            _myPersonModel.draw(theRenderContext);
        }
    }

    public String texturefile() {
        return _myImageFile;
    }

    public JoglMesh mesh() {
        return _myPersonModel;
    }

    public Vector<Person> persons() {
        return _myPersons;
    }

    public class Person {

        public TransformMatrix4f transform = new TransformMatrix4f(TransformMatrix4f.IDENTITY);

        public Vector3f rotation = new Vector3f();

        public Vector3f scale = new Vector3f(1, 1, 1);

        public Vector3f position = transform.translation;
    }
}
