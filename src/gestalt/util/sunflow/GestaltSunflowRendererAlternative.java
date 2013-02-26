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
import gestalt.render.bin.Bin;
import gestalt.render.controller.Camera;
import gestalt.render.controller.cameraplugins.Light;
import java.util.Vector;


public class GestaltSunflowRendererAlternative
        extends Thread {

    private Vector<SunflowTranslator> _myTranslators = new Vector<SunflowTranslator>();

    public static gestalt.material.Color BACKGROUNDCOLOR = new gestalt.material.Color(1);

    private static final float MAX_DIST = 600f;

    public static int SAMPLES = 128;

    public static int AA_MIN = 1;

    public static int AA_MAX = 2;

    private static int _myDrawableID = 0;

    private static final String SHADER_NAME = "ao_triangle";

    public Vector<SunflowTranslator> translators() {
        return _myTranslators;
    }

    public static void render(Bin theBin, Camera theCamera, Light theLight) {
        GestaltSunflowRendererAlternative myRenderer = new GestaltSunflowRendererAlternative();
        myRenderer.render(theCamera, theLight);
        myRenderer.parse(theBin);
        myRenderer.start();
    }

    private void parse(final Bin theBin) {
        Drawable[] mySortables = theBin.getDataRef();
        for (int i = 0; i < theBin.size(); i++) {
            final Drawable myDrawable = mySortables[i];
            if (myDrawable != null) {
                if (myDrawable.isActive()) {
                    parseDrawables(myDrawable);
                }
            }
        }
    }

    private void parseDrawables(final Drawable theDrawable) {
        for (final SunflowTranslator myTranslator : _myTranslators) {
            if (myTranslator.isClass(theDrawable)) {
//                myTranslator.parse(this, theDrawable);
                return;
            }
        }
        System.out.println("### WARNING / drawable type unsupported. / " + theDrawable.getClass());
    }

    public void run() {
//        sunflow.render();
    }

    public void render(Camera theCamera, Light theLight) {
        setupTranslators();
        setupCamera(theCamera);
        setupLight(theLight);

//        sunflow.render();
    }

    public void setupTranslators() {
        _myTranslators.add(new MeshTranslator());
        _myTranslators.add(new ModelTranslator());
        _myTranslators.add(new CubeTranslator());
        _myTranslators.add(new QuadLineTranslator());
        _myTranslators.add(new QuadsTranslator());
        _myTranslators.add(new TrianglesTranslator());
        _myTranslators.add(new PlaneTranslator());
    }

    public void setupCamera(Camera theCamera) {
//        /* set width and height */
//        sunflow.setWidth(theCamera.viewport().width);
//        sunflow.setHeight(theCamera.viewport().height);
//
//        sunflow.setBackground(BACKGROUNDCOLOR.r, BACKGROUNDCOLOR.g, BACKGROUNDCOLOR.b);
//
//        sunflow.setCameraTarget(theCamera.lookat().x, theCamera.lookat().y, theCamera.lookat().z);
//        sunflow.setCameraPosition(theCamera.position().x, theCamera.position().y, theCamera.position().z);
//        sunflow.setCameraUp(theCamera.upvector().x, theCamera.upvector().y, theCamera.upvector().z);
//        final float myAspect = (float)theCamera.viewport().width / (float)theCamera.viewport().height;
////        sunflow.setPinholeCamera("my_camera", theCamera.fovy, myAspect);
//        sunflow.setThinlensCamera("my_camera", theCamera.fovy, myAspect);
    }

    public void setupLight(Light theLight) {
//        sunflow.setPointLight("myPointLight",
//                              new Point3(theLight.position().x,
//                                         theLight.position().y,
//                                         theLight.position().z),
//                              new Color(255, 255, 255));
    }

    public void sendAmbientOcclusionMaterial(gestalt.material.Color theColor) {
        if (theColor == null) {
            theColor = new gestalt.material.Color(1, 1, 1, 1);
        }

//        sunflow.setAmbientOcclusionShader(SHADER_NAME + _myDrawableID,
//                                          new Color(theColor.r, theColor.g, theColor.b),
//                                          new Color(0, 0, 0),
//                                          SAMPLES,
//                                          MAX_DIST);
    }

    public void bumpDrawableID() {
        _myDrawableID++;
    }

    /* ----------------------------- */
    private void materialExamples() {
//
//        // set shader
//        sunflow.setAmbientOcclusionShader("myAmbientOcclusionShader", new Color(255, 0, 0), new Color(0, 0, 0), 16, 1);
//        // draw an object
//        sunflow.drawSphere("sphere01", -4, 0, 0, 1);
//
//        // set shader
//        sunflow.setDiffuseShader("myDiffuseShader", new Color(255, 0, 0));
//        // draw an object
//        sunflow.drawSphere("sphere02", -2, 0, 0, 1);
//
//        // set shader
//        sunflow.setGlassShader("myGlassShader", new Color(1f, 1f, 1f), 2.5f, 3f, new Color(1f, 1f, 1f));
//        // draw an object
//        sunflow.drawSphere("sphere03", 0, 0, 0, 1);
//
//        // set shader
//        sunflow.setShinyDiffuseShader("myShinyShader", new Color(255, 0, 0), 2f);
//        // draw an object
//        sunflow.drawSphere("sphere04", 2, 0, 0, 1);
//
//        // set shader
//        sunflow.setPhongShader("myPhongShader", new Color(1f, 1f, 1f), new Color(.5f, .5f, .9f), 10, 16);
//        // draw an object
//        sunflow.drawSphere("sphere05", 4, 0, 0, 1);
//
//        sunflow.setIrradianceCacheGIEngine(32, .4f, 1f, 15f, null);
    }
}
