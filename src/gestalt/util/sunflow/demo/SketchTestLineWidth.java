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


import gestalt.util.sunflow.Util;
import data.Resource;
import gestalt.G;
import gestalt.Gestalt;
import gestalt.context.DisplayCapabilities;
import gestalt.render.AnimatorRenderer;
import gestalt.shape.Line;

import mathematik.Vector3f;


public class SketchTestLineWidth
        extends AnimatorRenderer {

    public void setup() {
        cameramover(true);
        camera().setMode(Gestalt.CAMERA_MODE_LOOK_AT);
        WORKAROUND_FORCE_QUIT = false;

        /* create a line with the drawablefactory */
        for (int i = 0; i < 10; i++) {
            final Line mLine = drawablefactory().line();
            mLine.material().color.set(1f, 1f);
            mLine.points = new Vector3f[] {
                        new Vector3f(-100, i * 10, 0),
                        new Vector3f(100, i * 10, 0)
                    };
            mLine.linewidth = i * 0.25f;
            bin(BIN_3D).add(mLine);
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
                        System.getProperty("user.home") + "/Desktop/" + getClass().getSimpleName() + werkzeug.Util.now() + ".png");
        }
    }

    public static void main(String[] args) {
        DisplayCapabilities myDisplayCapabilities = new DisplayCapabilities();
        myDisplayCapabilities.width = 640;
        myDisplayCapabilities.height = 480;
        myDisplayCapabilities.backgroundcolor.set(0.8f);
        G.init(SketchTestLineWidth.class, myDisplayCapabilities);
    }
}



