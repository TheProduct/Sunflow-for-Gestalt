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


import gestalt.G;
import gestalt.Gestalt;
import gestalt.context.DisplayCapabilities;
import gestalt.impl.jogl.shape.JoglCube;
import gestalt.render.AnimatorRenderer;
import gestalt.shape.Cube;
import gestalt.util.sunflow.GestaltSunflowRenderer;

import mathematik.Random;
import gestalt.shape.Color;
import gestalt.util.sunflow.SunflowMaterial;
import gestalt.util.sunflow.Util;
import mathematik.Vector3f;


public class SketchUsingSunflowMaterials
        extends AnimatorRenderer {

    public void setup() {
        cameramover(true);
        camera().setMode(Gestalt.CAMERA_MODE_LOOK_AT);
        WORKAROUND_FORCE_QUIT = false;

        final int CUBES = 10;
        for (int x = 0; x < CUBES; x++) {
            for (int y = 0; y < CUBES; y++) {
                for (int z = 0; z < CUBES; z++) {
                    Cube p = new CubeWithSunflowMaterial();
                    bin(BIN_3D).add(p);
                    p.scale().set(5, 5, 5);
                    p.position().set(x * 8, y * 8, z * 8);
                    p.position().add(CUBES * 8 / -2.0f,
                                     8,
                                     CUBES * 8 / -2.0f);
                    p.material().color.set((float)x / (float)CUBES, (float)y / (float)CUBES, (float)z / (float)CUBES);
                }
            }
        }
    }

    class CubeWithSunflowMaterial
            extends JoglCube
            implements SunflowMaterial {

        public void sendMaterial(GestaltSunflowRenderer theParent) {
            theParent.sendGlassMaterial(material().color,
                                        1.33f,
                                        50,
                                        new Color(1.0f));
        }
    }

    private Color getRandomColor() {
        Color c = new Color(0, 0, 0);
        while ((c.r == 0.0f && c.g == 0.0f && c.b == 0.0f)) {
            c.set(getRandomValue(),
                  getRandomValue(),
                  getRandomValue());
        }
        return c;
    }

    private float getRandomValue() {
        final int myGrid = 1;
        final Random r = new Random();
        final float myValue = r.getInt(0, myGrid);
        return (myValue / myGrid);
    }

    public void loop(final float theDeltaTime) {
    }

    public void keyPressed(char c, int theKeyCode) {
        if (c == ' ' || c == 'p') {
            Util.render(c == 'p',
                        bin(BIN_3D),
                        this,
                        new Vector3f(),
                        Util.good_render_result_filename(getClass()));
        }
    }

    public static void main(String[] args) {
        DisplayCapabilities myDisplayCapabilities = new DisplayCapabilities();
        myDisplayCapabilities.width = 640;
        myDisplayCapabilities.height = 480;
        myDisplayCapabilities.backgroundcolor.set(0.8f);

        G.init(SketchUsingSunflowMaterials.class, myDisplayCapabilities);
    }
}
