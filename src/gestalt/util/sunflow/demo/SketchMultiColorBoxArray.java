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


import data.Resource;
import gestalt.G;
import gestalt.Gestalt;
import gestalt.context.DisplayCapabilities;
import gestalt.render.AnimatorRenderer;
import gestalt.shape.Cube;
import gestalt.shape.Plane;

import mathematik.Random;
import gestalt.shape.Color;
import gestalt.util.sunflow.Util;


public class SketchMultiColorBoxArray
        extends AnimatorRenderer {

    private Plane mFloor;

    private static final Class SKETCH_CLASS = SketchMultiColorBoxArray.class;

    public void setup() {
        cameramover(true);
        camera().setMode(Gestalt.CAMERA_MODE_LOOK_AT);
        WORKAROUND_FORCE_QUIT = false;

        mFloor = G.plane();
        mFloor.scale().set(1000, 1000);
        mFloor.position().set(0, 0, 0);
        mFloor.rotation().x = PI_HALF;
        mFloor.material().transparent = true;
        mFloor.material().color.set(1, 0.2f);

        final int CUBES = 20;
        for (int x = 0; x < CUBES; x++) {
            for (int y = 0; y < CUBES; y++) {
                for (int z = 0; z < CUBES; z++) {
                    Cube p = G.cube();
                    p.scale().set(5, 5, 5);
                    p.position().set(x * 8, y * 8, z * 8);
                    p.position().add(CUBES * 8 / -2.0f,
                                     8,
                                     CUBES * 8 / -2.0f);
                    p.material().color.set(getRandomColor());
                }
            }
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
                        mFloor.position(),
                        Resource.getPath("") + SKETCH_CLASS.getName() + ".png");
        }
    }

    public static void main(String[] args) {
        DisplayCapabilities myDisplayCapabilities = new DisplayCapabilities();
        myDisplayCapabilities.width = 640;
        myDisplayCapabilities.height = 480;
        myDisplayCapabilities.backgroundcolor.set(0.8f);

        G.init(SKETCH_CLASS, myDisplayCapabilities);
    }
}
