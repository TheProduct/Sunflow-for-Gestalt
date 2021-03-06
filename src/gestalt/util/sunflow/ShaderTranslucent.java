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


import org.sunflow.SunflowAPI;
import org.sunflow.core.ParameterList;
import org.sunflow.core.Ray;
import org.sunflow.core.ShadingState;
import org.sunflow.math.Point3;
import org.sunflow.math.Vector3;


public class ShaderTranslucent
        implements org.sunflow.core.Shader {

    /* http://sfwiki.geneome.net/index.php5?title=Shaders/Janino_Translucent */
    public static final String name = "translucent";

    // object color
    public org.sunflow.image.Color color = org.sunflow.image.Color.WHITE;
    // object absorption color
    //public Color absorptionColor = Color.RED;

    public org.sunflow.image.Color absorptionColor = org.sunflow.image.Color.BLUE;
    // inverse of absorption color

    public org.sunflow.image.Color transmittanceColor = absorptionColor.copy().opposite();
    // global color-saving variable
        /* FIXME!?? - globals are not good */

    public org.sunflow.image.Color glob = org.sunflow.image.Color.black();
    // phong specular color

    public org.sunflow.image.Color pcolor = org.sunflow.image.Color.BLACK;
    // object absorption distance

    public float absorptionDistance = 0.25f;
    // depth correction parameter

    public float thickness = 0.002f;
    // phong specular power

    public float ppower = 85f;
    // phong specular samples

    public int psamples = 1;
    // phong flag

    public boolean phong = true;

    public boolean update(ParameterList pl, SunflowAPI api) {
        color = pl.getColor("color", color);
        if (absorptionDistance == 0f) {
            absorptionDistance += 0.0000001f;
        }
        if (!pcolor.isBlack()) {
            phong = true;
        }
        return true;
    }

    public org.sunflow.image.Color getRadiance(ShadingState state) {
        org.sunflow.image.Color ret = org.sunflow.image.Color.black();
        org.sunflow.image.Color absorbtion = org.sunflow.image.Color.white();
        glob.set(org.sunflow.image.Color.black());
        state.faceforward();
        state.initLightSamples();
        state.initCausticSamples();
        if (state.getRefractionDepth() == 0) {
            ret.set(state.diffuse(color).mul(0.5f));
            bury(state, thickness);
        } else {
            absorbtion = org.sunflow.image.Color.mul(-state.getRay().getMax() / absorptionDistance, transmittanceColor).exp();
        }
        state.traceRefraction(new Ray(state.getPoint(), randomVector()), 0);
        glob.add(state.diffuse(color));
        glob.mul(absorbtion);
        if (state.getRefractionDepth() == 0 && phong) {
            bury(state, -thickness);
            glob.add(state.specularPhong(pcolor, ppower, psamples));
        }
        return glob;
    }

    public void bury(ShadingState state, float th) {
        Point3 pt = state.getPoint();
        Vector3 norm = state.getNormal();
        pt.x = pt.x - norm.x * th;
        pt.y = pt.y - norm.y * th;
        pt.z = pt.z - norm.z * th;
    }

    public Vector3 randomVector() {
        return new Vector3(
                (float)(2f * Math.random() - 1f),
                (float)(2f * Math.random() - 1f),
                (float)(2f * Math.random() - 1f)).normalize();
    }

    public org.sunflow.image.Color getDiffuse(ShadingState state) {
        return color;
    }

    public void scatterPhoton(ShadingState state, org.sunflow.image.Color power) {
        org.sunflow.image.Color diffuse = getDiffuse(state);
        state.storePhoton(state.getRay().getDirection(), power, diffuse);
        state.traceReflectionPhoton(new Ray(state.getPoint(), randomVector()), power.mul(diffuse));

    }
}
