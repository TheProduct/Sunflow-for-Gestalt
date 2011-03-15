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


import gestalt.Gestalt;
import java.util.Vector;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;

import org.sunflow.core.Display;
import org.sunflow.math.Matrix4;
import org.sunflow.system.ImagePanel;

import gestalt.render.BasicRenderer;
import gestalt.render.Drawable;
import gestalt.render.bin.AbstractBin;
import gestalt.render.plugin.Camera;
import gestalt.render.plugin.Light;
import gestalt.shape.Color;
import gestalt.shape.Line;

import mathematik.Matrix3f;
import mathematik.TransformMatrix4f;
import mathematik.Vector3f;
import org.sunflow.PluginRegistry;

import org.sunflow.SunflowAPI;
import org.sunflow.core.ParameterList;
import org.sunflow.core.ShadingState;
import org.sunflow.core.display.FileDisplay;
import org.sunflow.math.Vector3;
import org.sunflow.math.Point3;


public class GestaltSunflowRenderer
        extends Thread {

    public static boolean preview = false;

    public static boolean floor = true;

    public static boolean auto_dispose_frame = false;

    public static boolean start_in_extra_thread = true;

    public static boolean headless = false;

    public static Vector3f floor_up = null;

    public static float scale_viewport = 1;

    private final SunflowAPI mSunflow;

    private Display _myDisplay;

    private int _myWidth = 1024;

    private int _myHeight = 512;

    public static Color BACKGROUNDCOLOR = new Color(1);

    private static final Vector3f _myFloor = new Vector3f();

    private static final float MAX_DIST = 600f;

    public static int SAMPLES = 128;

    public static int AA_MIN = 1;

    public static int AA_MAX = 2;

    static final String SHADER_NAME = "my_shader";

    private static Vector<SunflowTranslator> mTranslators = new Vector<SunflowTranslator>();

    private final String COLORSPACE_SRGB_NONLINEAR = "sRGB nonlinear";

    private final String GI_INSTANT_GI = "igi";

    private final String GI_IRRADIANCE_CACHE = "irr-cache";

    private final String LIGHT_SUNSKY = "sunsky";

    public final String GI_FAKE = "fake";

    private final String FILTER_BLACKMAN_HARRIS = "blackman-harris";

    private final String FILTER_BOX = "box";

    private final String FILTER_CATMULL_ROM = "catmull-rom";

    private final String FILTER_GAUSSIAN = "gaussian";

    private final String FILTER_LANCZOS = "lanczos";

    private final String FILTER_MITCHELL = "mitchell";

    private final String FILTER_SINC = "sinc";

    private final String FILTER_TRIANGLE = "triangle";

    private final String FILTER_BSPLINE = "bspline";

    static {
        mTranslators.add(new MeshTranslator());
        mTranslators.add(new ModelTranslator());
        mTranslators.add(new CubeTranslator());
        mTranslators.add(new QuadLineTranslator());
        mTranslators.add(new QuadsTranslator());
        mTranslators.add(new TrianglesTranslator());
        mTranslators.add(new PlaneTranslator());
        mTranslators.add(new LineTranslator());

        mTranslators.add(new JoglDisposableBinTranslator());
        mTranslators.add(new PersonTranslator());

        PluginRegistry.shaderPlugins.registerPlugin("my_test_shader", MyCustomShader.class);
        PluginRegistry.shaderPlugins.registerPlugin(ShaderTranslucent.name, ShaderTranslucent.class);
        PluginRegistry.shaderPlugins.registerPlugin(ShaderTranslucentSR.name, ShaderTranslucentSR.class);
        PluginRegistry.shaderPlugins.registerPlugin(ShaderStainedGlass.name, ShaderStainedGlass.class);
    }

    public static Vector<SunflowTranslator> translators() {
        return mTranslators;
    }

    public SunflowAPI sunflow() {
        return mSunflow;
    }

    public GestaltSunflowRenderer() {
        mSunflow = new SunflowAPI();
    }

    public void setInstantGIEngine(int samples, int sets, float c, float bias_samples) {
        mSunflow.parameter("gi.engine", GI_INSTANT_GI);
        mSunflow.parameter("gi.igi.samples", samples);
        mSunflow.parameter("gi.igi.sets", sets);
        mSunflow.parameter("gi.igi.c", c);
        mSunflow.parameter("gi.igi.bias_samples", bias_samples);
    }

    public void setIrradianceCacheGIEngine(int samples, float tolerance, float minSpacing, float maxSpacing, String globalphotonmap) {
        mSunflow.parameter("gi.engine", GI_IRRADIANCE_CACHE);
        mSunflow.parameter("gi.irr-cache.samples", samples);
        mSunflow.parameter("gi.irr-cache.tolerance", tolerance);
        mSunflow.parameter("gi.irr-cache.min_spacing", minSpacing);
        mSunflow.parameter("gi.irr-cache.max_spacing", maxSpacing);
        mSunflow.parameter("gi.irr-cache.gmap", globalphotonmap);
    }

    public void setFakeGIEngine(Vector3f theUp) {
        mSunflow.parameter("gi.engine", GI_FAKE);
        mSunflow.parameter("gi.fake.up", new Vector3(theUp.x, theUp.y, theUp.z));
        mSunflow.parameter("gi.fake.sky",
                           COLORSPACE_SRGB_NONLINEAR, 1, 1, 1);
        mSunflow.parameter("gi.fake.ground",
                           COLORSPACE_SRGB_NONLINEAR, 1, 1, 1);
    }

    private void setSunSkyLight(String theName) {
        mSunflow.parameter("up", new Vector3(0, 0, 1));
        mSunflow.parameter("east", new Vector3(0, 1, 0));
        mSunflow.parameter("sundir", new Vector3(1, -1, 0.31f));
        mSunflow.parameter("turbidity", 0f);
        mSunflow.parameter("samples", 16);
        mSunflow.light(theName, LIGHT_SUNSKY);
    }

    public void setupScene() {
        /* background */
        mSunflow.parameter("color", null, BACKGROUNDCOLOR.r, BACKGROUNDCOLOR.g, BACKGROUNDCOLOR.b);
        mSunflow.shader("background.shader", "constant");

        mSunflow.geometry("background", "background");
        mSunflow.parameter("shaders", "background.shader");
        mSunflow.instance("background.instance", "background");

        /* ground */
        if (floor) {
            mSunflow.parameter("maxdist", MAX_DIST);
            mSunflow.parameter("samples", SAMPLES);
            mSunflow.shader("ao_ground", "ambient_occlusion");

            mSunflow.parameter("center", new Point3(_myFloor.x, _myFloor.y, _myFloor.z));
            if (floor_up == null) {
                mSunflow.parameter("normal", new Vector3(0.0f, 1.0f, 0.0f));
            } else {
                mSunflow.parameter("normal", new Vector3(floor_up.x, floor_up.y, floor_up.z));
            }
            mSunflow.geometry("ground", "plane");
            mSunflow.parameter("shaders", "ao_ground");
            mSunflow.instance("ground.instance", "ground");
        }
    }

    public static Vector3f floor() {
        return _myFloor;
    }

    public void setupCamera(final Camera theCamera) {

        if (theCamera.getMode() != Gestalt.CAMERA_MODE_LOOK_AT) {
            System.out.println("### camera currently only works in LOOK_AT mode.");
        }

        /* only works for LOOK_AT mode */

        Point3 eye = new Point3(theCamera.position().x, theCamera.position().y, theCamera.position().z);
        Point3 target = new Point3(theCamera.lookat().x, theCamera.lookat().y, theCamera.lookat().z);
        Vector3 up = new Vector3(theCamera.upvector().x, theCamera.upvector().y, theCamera.upvector().z);
        mSunflow.parameter("transform", Matrix4.lookAt(eye, target, up));

//        TransformMatrix4f myCameraTransform = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
//        myCameraTransform.rotation.set(theCamera.getRotationMatrix());
//        myCameraTransform.translation.set(theCamera.position());
//        Matrix4 myMatrix4 = new Matrix4(myCameraTransform.toArray(), false);
//        _mySunflow.parameter("transform", myMatrix4);


        mSunflow.parameter("fov", theCamera.fovy);
        final float myAspect = (float)theCamera.viewport().width / (float)theCamera.viewport().height;
        mSunflow.parameter("aspect", myAspect);
        mSunflow.parameter("shift.x", 0.0f);
        mSunflow.parameter("shift.y", 0.0f);
        mSunflow.camera("my_camera", "pinhole");

//        System.out.println("theCamera.position() " + theCamera.position());
//        System.out.println("theCamera.lookat() " + theCamera.lookat());
//        System.out.println("theCamera.upvector() " + theCamera.upvector());
//        System.out.println("theCamera.fovy() " + theCamera.fovy);
//        System.out.println("myAspect " + myAspect);
    }

    public void setupLight(Light theLight) {
        /* light */
        mSunflow.parameter("power", COLORSPACE_SRGB_NONLINEAR, 1.0f, 1.0f, 1.0f);
        mSunflow.parameter("center", new Point3(theLight.position().x,
                                                theLight.position().y,
                                                theLight.position().z));
        mSunflow.light("myLight", "point");
    }

    static int _myDrawableID = 0;

    public void sendTexturedAmbientOcclusionMaterial(Color theColor, String theAbsoluteFilePath) {
        /* material */
        mSunflow.parameter("maxdist", MAX_DIST);
        mSunflow.parameter("samples", SAMPLES);
        if (theColor != null) {
            mSunflow.parameter("bright", COLORSPACE_SRGB_NONLINEAR, theColor.r, theColor.g, theColor.b);
        } else {
            mSunflow.parameter("bright", COLORSPACE_SRGB_NONLINEAR, 1, 1, 1, 1);
        }

        /* how do we get this into the pipeline? */
        mSunflow.parameter("dark", COLORSPACE_SRGB_NONLINEAR, 0, 0, 0);
        mSunflow.parameter("texture", theAbsoluteFilePath);
//        _mySunflow.parameter("texture", Resource.getPath("person/person0001.jpg"));
        mSunflow.shader(SHADER_NAME + _myDrawableID, "textured_ambient_occlusion");
    }

    public void sendAmbientOcclusionMaterial(Color theColor) {
        /* material */
        mSunflow.parameter("maxdist", MAX_DIST);
        mSunflow.parameter("samples", SAMPLES);
        if (theColor != null) {
            mSunflow.parameter("bright", COLORSPACE_SRGB_NONLINEAR, theColor.r, theColor.g, theColor.b);
        } else {
            mSunflow.parameter("bright", COLORSPACE_SRGB_NONLINEAR, 1, 1, 1);
        }

        /* how do we get this into the pipeline? */
        mSunflow.parameter("dark", COLORSPACE_SRGB_NONLINEAR, 0, 0, 0);
        mSunflow.shader(SHADER_NAME + _myDrawableID, "ambient_occlusion");
    }

    public void sendUberMaterial(final Color theColor,
                                 final String theAbsoluteDiffuseTexturePath,
                                 final String theAbsoluteSpecularTexturePath) {
        /* material */
        mSunflow.parameter("diffuse", COLORSPACE_SRGB_NONLINEAR, theColor.r, theColor.g, theColor.b);
        mSunflow.parameter("specular", COLORSPACE_SRGB_NONLINEAR, theColor.r, theColor.g, theColor.b);
//        _mySunflow.parameter("diffuse.texture", theAbsoluteDiffuseTexturePath);
//        _mySunflow.parameter("specular.texture", theAbsoluteSpecularTexturePath);
        mSunflow.parameter("diffuse.blend", 1 - theColor.a);
        mSunflow.parameter("specular.blend", 1 - theColor.a);
        mSunflow.parameter("glossyness", 1.0f);
        mSunflow.parameter("samples", SAMPLES);

        mSunflow.shader(SHADER_NAME + _myDrawableID, "uber");
    }

    public void sendGlassMaterial(final Color theColor,
                                  final float theETA,
                                  final float theAbsorptionDistance,
                                  final Color theAbsorptionColor) {
        /* material */
        mSunflow.parameter("color", COLORSPACE_SRGB_NONLINEAR,
                           theColor.r,
                           theColor.g,
                           theColor.b);
        mSunflow.parameter("eta", theETA);
        mSunflow.parameter("absorption.distance", theAbsorptionDistance);
        mSunflow.parameter("absorption.color", COLORSPACE_SRGB_NONLINEAR,
                           theAbsorptionColor.r,
                           theAbsorptionColor.g,
                           theAbsorptionColor.b);

        /* shader */
        mSunflow.shader(SHADER_NAME + _myDrawableID, "glass");
    }

    public void sendTranslucentMaterial(final Color theColor) {
        /* material */
        mSunflow.parameter("color", COLORSPACE_SRGB_NONLINEAR,
                           theColor.r,
                           theColor.g,
                           theColor.b);
        /* shader */
        mSunflow.shader(SHADER_NAME + _myDrawableID, ShaderTranslucent.name);
    }

    public void sendStainedGlasMaterial(final Color theColor) {
        /* material */
        mSunflow.parameter("color", COLORSPACE_SRGB_NONLINEAR,
                           theColor.r,
                           theColor.g,
                           theColor.b);
        /* shader */
        mSunflow.shader(SHADER_NAME + _myDrawableID, ShaderStainedGlass.name);
    }

    public void sendTriangles(final float[] theVertices,
                              final TransformMatrix4f theTransform,
                              final Vector3f theRotation,
                              final Vector3f theScale) {
        sendTriangles(theVertices,
                      null,
                      null,
                      theTransform,
                      theRotation,
                      theScale);
    }

    public void sendTriangles(final float[] pVertices,
                              final float[] pNormals,
                              final float[] pTexCoords,
                              final TransformMatrix4f pTransform,
                              final Vector3f pRotation,
                              final Vector3f pScale) {

        float[] mTransformedVertices = transformCoords(pVertices, pTransform, pScale, pRotation);


//        float[] mTransformedVertices = new float[pVertices.length];
//
//        Matrix3f myRotationMatrix = null;
//        if (pTransform != null) {
//            myRotationMatrix = new Matrix3f(pTransform.rotation);
//            myRotationMatrix.invert();
//        }
//
//        for (int i = 0; i < mTransformedVertices.length; i += 3) {
//            final Vector3f myVertex = new Vector3f(pVertices[i + 0],
//                                                   pVertices[i + 1],
//                                                   pVertices[i + 2]);
//            if (pScale != null) {
//                myVertex.scale(pScale);
//            }
//            if (pRotation != null
//                    && (pRotation.x != 0 || pRotation.y != 0 || pRotation.z != 0)) {
//                final TransformMatrix4f myTempRotationMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
//                myTempRotationMatrix.rotation.setXYZRotation(pRotation);
//                myTempRotationMatrix.transform(myVertex);
//            }
//            if (pTransform != null) {
//                myRotationMatrix.transform(myVertex);
//                myVertex.add(pTransform.translation);
//            }
//
//            /* --- */
//            mTransformedVertices[i + 0] = myVertex.x;
//            mTransformedVertices[i + 1] = myVertex.y;
//            mTransformedVertices[i + 2] = myVertex.z;
//        }

        /*
        // create geometry @ SCParser
        api.parameter("triangles", triangles);
        api.parameter("points", "point", "vertex", points);
        api.parameter("normals", "vector", "vertex", normals);
        api.parameter("uvs", "texcoord", "vertex", uvs);
        api.geometry(name, "triangle_mesh");

         */

        int[] mFaces = new int[pVertices.length / 3];
        for (int i = 0; i < mFaces.length; i++) {
            mFaces[i] = i;
        }

        mSunflow.parameter("triangles", mFaces);
        mSunflow.parameter("points", "point", "vertex", mTransformedVertices);

        if (pNormals != null) {
            mSunflow.parameter("normals", "vector", "vertex", pNormals); // Vector3f
        }

        /** @todo texture coordinates are never transformed by texture matrix transform */
        if (pTexCoords != null) {
            mSunflow.parameter("uvs", "texcoord", "vertex", pTexCoords); // Vector2f
        }

        mSunflow.geometry("myPrimitive" + _myDrawableID, "triangle_mesh");

        mSunflow.parameter("shaders", SHADER_NAME + _myDrawableID);
        mSunflow.instance("myPrimitive" + _myDrawableID + ".instance", "myPrimitive" + _myDrawableID);
    }

    public void sendLines(final Line pDrawable) {
        float[] mTransformedVertices = transformCoords(Util.convertVector3fToFloat(pDrawable.points),
                                                       pDrawable.transform(),
                                                       pDrawable.scale(),
                                                       pDrawable.rotation());

        /* we ll do with a line-strip for now */

        mSunflow.parameter("segments", pDrawable.points.length - 1);
        mSunflow.parameter("widths", pDrawable.linewidth);
        mSunflow.parameter("points", "point", "vertex", mTransformedVertices);

//        mSunflow.parameter("colors", "???", "???", werkzeug.Util.toArrayFromColor(pDrawable.colors));

        mSunflow.geometry("myPrimitive" + _myDrawableID, "hair");

        mSunflow.parameter("shaders", SHADER_NAME + _myDrawableID);
        mSunflow.instance("myPrimitive" + _myDrawableID + ".instance", "myPrimitive" + _myDrawableID);
    }

    private float[] transformCoords(final float[] pVertices, final TransformMatrix4f pTransform, final Vector3f pScale, final Vector3f pRotation) {
        final float[] mTrasnformedVertices = new float[pVertices.length];
        Matrix3f myRotationMatrix = null;
        if (pTransform != null) {
            myRotationMatrix = new Matrix3f(pTransform.rotation);
            myRotationMatrix.invert();
        }
        for (int i = 0; i < mTrasnformedVertices.length; i += 3) {
            final Vector3f myVertex = new Vector3f(pVertices[i + 0], pVertices[i + 1], pVertices[i + 2]);
            if (pScale != null) {
                myVertex.scale(pScale);
            }
            if (pRotation != null && (pRotation.x != 0 || pRotation.y != 0 || pRotation.z != 0)) {
                final TransformMatrix4f myTempRotationMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
                myTempRotationMatrix.rotation.setXYZRotation(pRotation);
                myTempRotationMatrix.transform(myVertex);
            }
            if (pTransform != null) {
                myRotationMatrix.transform(myVertex);
                myVertex.add(pTransform.translation);
            }
            mTrasnformedVertices[i + 0] = myVertex.x;
            mTrasnformedVertices[i + 1] = myVertex.y;
            mTrasnformedVertices[i + 2] = myVertex.z;
        }
        return mTrasnformedVertices;
    }

    public void run() {
        _render();
    }

    public void render() {
        start();
    }

    private void _render() {
        mSunflow.parameter("camera", "my_camera");
        mSunflow.parameter("resolutionX", (int)(_myWidth * scale_viewport));
        mSunflow.parameter("resolutionY", (int)(_myHeight * scale_viewport));

        if (preview) {
            mSunflow.parameter("aa.min", -3);
            mSunflow.parameter("aa.max", 0);
            mSunflow.parameter("bucket.order", "spiral");
        } else {
            mSunflow.parameter("aa.min", AA_MIN);
            mSunflow.parameter("aa.max", AA_MAX);
            mSunflow.parameter("bucket.order", "column");
            mSunflow.parameter("jitter", "true");
//            _mySunflow.parameter("filter", FILTER_BLACKMAN_HARRIS);
            mSunflow.parameter("filter", FILTER_MITCHELL);
        }
        mSunflow.options(SunflowAPI.DEFAULT_OPTIONS);
        mSunflow.render(SunflowAPI.DEFAULT_OPTIONS, _myDisplay);
    }

    private void width(int theWidth) {
        _myWidth = theWidth;
    }

    private void height(int theHeight) {
        _myHeight = theHeight;
    }

    public static class MyCustomShader
            implements org.sunflow.core.Shader {

        /**
         * Gets the radiance for a specified rendering state. When this method is
         * called, you can assume that a hit has been registered in the state and
         * that the hit surface information has been computed.
         *
         * @param state current render state
         * @return color emitted or reflected by the shader
         */
        public org.sunflow.image.Color getRadiance(ShadingState state) {
            return org.sunflow.image.Color.GREEN;
        }

        /**
         * Scatter a photon with the specied power. Incoming photon direction is
         * specified by the ray attached to the current render state. This method
         * can safely do nothing if photon scattering is not supported or relevant
         * for the shader type.
         *
         * @param state current state
         * @param power power of the incoming photon.
         */
        public void scatterPhoton(ShadingState state, org.sunflow.image.Color power) {
        }

        /**
         * Update this object given a list of parameters. This method is guarenteed
         * to be called at least once on every object, but it should correctly
         * handle empty parameter lists. This means that the object should be in a
         * valid state from the time it is constructed. This method should also
         * return true or false depending on whether the update was succesfull or
         * not.
         *
         * @param pl list of parameters to read from
         * @param api reference to the current scene
         * @return <code>true</code> if the update is succesfull,
         *         <code>false</code> otherwise
         */
        public boolean update(ParameterList pl, SunflowAPI api) {
            return true;
        }
    }

    public static void render(BasicRenderer theGestalt, AbstractBin theBin, String theFilePath) {
        GestaltSunflowRenderer myRenderer = new GestaltSunflowRenderer();
        myRenderer.width(theGestalt.displaycapabilities().width);
        myRenderer.height(theGestalt.displaycapabilities().height);
        myRenderer.setupScene();
//        myRenderer.setSunSkyLight("my_sun");
        myRenderer.setupCamera(theGestalt.camera());
        myRenderer.setupLight(theGestalt.light());

        if (headless) {
            myRenderer.setDisplay(new FileDisplay(theFilePath));
        } else {
            myRenderer.setDisplay(new MyFrameDisplay(theFilePath));
        }

        /* --- */
        myRenderer.parse(theBin);

//        myRenderer.setInstantGIEngine(64, 1, 0.01f, 0); System.out.println("### using 'InstantGIEngine'");
        myRenderer.setIrradianceCacheGIEngine(32, .4f, 1f, 15f, null);
        System.out.println("### using 'IrradianceCacheGIEngine'");
//        myRenderer.setFakeGIEngine(new Vector3f(0, 1, 0));

        if (start_in_extra_thread) {
            myRenderer.render();
        } else {
            myRenderer._render();
        }
    }

    public void setDisplay(final Display theDisplay) {
        _myDisplay = theDisplay;
    }

    private void parse(final AbstractBin theBin) {
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
        for (final SunflowTranslator myTranslator : mTranslators) {
            if (myTranslator.isClass(theDrawable)) {
                myTranslator.parse(this, theDrawable);
                return;
            }
        }

        System.out.println("### WARNING / drawable type unsupported. / " + theDrawable.getClass());
    }

    public void bumpDrawableID() {
        _myDrawableID++;
    }

    private static class MyFrameDisplay
            implements Display {

        private String filename;

        private RenderFrame frame;

        public MyFrameDisplay() {
            this(null);
        }

        public MyFrameDisplay(String filename) {
            this.filename = filename;
            frame = null;
        }

        public void imageBegin(int w, int h, int bucketSize) {
            if (frame == null) {
                frame = new RenderFrame();
                frame.imagePanel.imageBegin(w, h, bucketSize);
                Dimension screenRes = Toolkit.getDefaultToolkit().getScreenSize();
                boolean needFit = false;
                if (w >= (screenRes.getWidth() - 200) || h >= (screenRes.getHeight() - 200)) {
                    frame.imagePanel.setPreferredSize(new Dimension((int)screenRes.getWidth() - 200,
                                                                    (int)screenRes.getHeight() - 200));
                    needFit = true;
                } else {
                    frame.imagePanel.setPreferredSize(new Dimension(w, h));
                }
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                if (needFit) {
                    frame.imagePanel.fit();
                }
            } else {
                frame.imagePanel.imageBegin(w, h, bucketSize);
            }
        }

        public void imagePrepare(int x, int y, int w, int h, int id) {
            frame.imagePanel.imagePrepare(x, y, w, h, id);
        }

        public void imageUpdate(int x, int y, int w, int h, org.sunflow.image.Color[] data, float[] alpha) {
            frame.imagePanel.imageUpdate(x, y, w, h, data, alpha);
        }

        public void imageFill(int x, int y, int w, int h, org.sunflow.image.Color c, float alpha) {
            frame.imagePanel.imageFill(x, y, w, h, c, alpha);
        }

        public void imageEnd() {
            frame.imagePanel.imageEnd();
            if (filename != null) {
                frame.imagePanel.save(filename);
            }
            if (auto_dispose_frame) {
                frame.dispose();
                frame = null;
            }
        }

        @SuppressWarnings("serial")
        private static class RenderFrame
                extends JFrame {

            ImagePanel imagePanel;

            RenderFrame() {
                super("Sunflow v" + SunflowAPI.VERSION);
                setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                addKeyListener(new KeyAdapter() {

                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                            setVisible(false);
                            dispose();
                        }
                    }
                });
                imagePanel = new ImagePanel();
                setContentPane(imagePanel);
                pack();
            }
        }
    }
}
