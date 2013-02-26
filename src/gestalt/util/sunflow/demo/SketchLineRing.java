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
import gestalt.render.SketchRenderer;
import gestalt.util.sunflow.Util;
import mathematik.Vector3f;


public class SketchLineRing
        extends SketchRenderer {

    public void setup() {
        cameramover(true);
        camera().setMode(CAMERA_MODE_LOOK_AT);
        camera().position().set(-271.0138, 326.966, 476.93112);
        bin(BIN_2D_FOREGROUND).add(stats_view());
        bin(BIN_3D).add(g());
        backgroundcolor().set(0.2f);
    }

    public void loop(final float theDeltaTime) {
        for (float r = 0; r < PI * 2; r += PI * 2 / 36) {
            final float x = sin(r) * 100 + 100;
            final float y = cos(r) * 100 + 100;
            final float pX = sin(r - PI * 2 / 36) * 100 + 100;
            final float pY = cos(r - PI * 2 / 36) * 100 + 100;
            g().color(abs(sin(r * 0.33f)), abs(cos(r)), abs(cos(r * 0.85f)), 1);
            g().linewidth(abs(sin(r)) * 5 + 1);
            g().line(x, y, pX, pY);
        }
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
        G.init(SketchLineRing.class);
    }
}


