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
import gestalt.util.sunflow.Util;
import data.Resource;
import gestalt.G;
import gestalt.Gestalt;
import gestalt.context.DisplayCapabilities;
import gestalt.render.AnimatorRenderer;
import gestalt.shape.Line;

import mathematik.Vector3f;


public class SketchTestLines
        extends AnimatorRenderer {

    private Line _myLine;

    public void setup() {
        cameramover(true);
        camera().setMode(Gestalt.CAMERA_MODE_LOOK_AT);
        WORKAROUND_FORCE_QUIT = false;

        GestaltSunflowRenderer.scale_viewport = 1f;

        /* create a line with the drawablefactory */
        _myLine = drawablefactory().line();
        _myLine.material().color.set(1f, 1f);
        _myLine.points = new Vector3f[500];
        for (int i = 0; i < _myLine.points.length; i++) {
            _myLine.points[i] = new Vector3f(Math.random() * 300 - 150,
                                             Math.random() * 300 - 150,
                                             Math.random() * 300 - 150);
        }

        final int WIDTH = 25;
        final int HEIGHT = 25;
        final int LENGTH = 15;
        final float SCALE = 10;
        final float mOffsetX = WIDTH * SCALE / -2;
        final float mOffsetY = HEIGHT * SCALE / -2;
        _myLine.points = new Vector3f[WIDTH * HEIGHT * 2];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                final int i = x + y * WIDTH;
                float mOffset = (float)(Math.sin(2 * PI * (float)i / _myLine.points.length));
                _myLine.points[i * 2 + 0] = new Vector3f(x * SCALE + mOffsetX, mOffset * LENGTH + LENGTH * 2, y * SCALE + mOffsetY);
                _myLine.points[i * 2 + 1] = new Vector3f(x * SCALE + mOffsetX, 0, y * SCALE + mOffsetY);
            }
        }

        _myLine.linewidth = 0.75f;

        /* add line to renderer */
        bin(BIN_3D).add(_myLine);
    }

    public void loop(final float theDeltaTime) {
    }

    public void keyPressed(char c, int theKeyCode) {
        if (c == ' ' || c == 'p') {
            Util.render(c == 'p',
                        bin(BIN_3D),
                        this,
                        new Vector3f(),
                        Resource.getPath("") + "/test.png");
        }
    }

    public static void main(String[] args) {
        DisplayCapabilities myDisplayCapabilities = new DisplayCapabilities();
        myDisplayCapabilities.width = 640;
        myDisplayCapabilities.height = 480;
        myDisplayCapabilities.backgroundcolor.set(0.8f);
        G.init(SketchTestLines.class, myDisplayCapabilities);
    }
}

