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


import gestalt.util.sunflow.GestaltSunflowRenderer;
import gestalt.util.sunflow.Persons;
import gestalt.util.sunflow.Persons.Person;
import gestalt.util.sunflow.Util;
import data.Resource;
import gestalt.G;
import gestalt.Gestalt;
import gestalt.context.DisplayCapabilities;
import gestalt.render.AnimatorRenderer;

import mathematik.Vector3f;


public class SketchTestPersons
        extends AnimatorRenderer {

    public void setup() {
        cameramover(true);
        camera().setMode(Gestalt.CAMERA_MODE_LOOK_AT);
        WORKAROUND_FORCE_QUIT = false;

        GestaltSunflowRenderer.scale_viewport = 1f;

        final Persons myPersons = new Persons();
        bin(BIN_3D).add(myPersons);

        for (int i = 0; i < 1000; i++) {
            final Person myPerson = myPersons.instance();
            myPerson.position.set(gestalt.util.sunflow.Util.random(-100, 100), 0, gestalt.util.sunflow.Util.random(-100, 100));
            myPerson.rotation.y = gestalt.util.sunflow.Util.random(-PI / 4, PI / 4);
            myPerson.scale.scale(0.05f);
        }
    }

    public void loop(final float theDeltaTime) {
    }

    public void keyPressed(char c, int theKeyCode) {
        if (c == ' ' || c == 'p') {
            Util.render(c == 'p',
                        bin(BIN_3D),
                        this,
                        new Vector3f(),
                        Resource.getPath("") + "test.png");
        }
    }

    public static void main(String[] args) {
        DisplayCapabilities myDisplayCapabilities = new DisplayCapabilities();
        myDisplayCapabilities.width = 640;
        myDisplayCapabilities.height = 480;
        myDisplayCapabilities.backgroundcolor.set(0.8f);
        G.init(SketchTestPersons.class, myDisplayCapabilities);
    }
}
