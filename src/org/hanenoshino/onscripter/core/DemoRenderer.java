package org.hanenoshino.onscripter.core;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

import org.hanenoshino.onscripter.misc.GLSurfaceView_SDL;
import org.hanenoshino.onscripter.misc.NativeReferenced;

import android.content.Context;

@NativeReferenced
class DemoRenderer extends GLSurfaceView_SDL.Renderer {

	private final boolean isRenderFontOutline;
	private final String currentDirectoryPath;

	public DemoRenderer(Context _context, String currentDirectoryPath,
			boolean isRenderFontOutline) {
		// context = _context;
		this.isRenderFontOutline = isRenderFontOutline;
		this.currentDirectoryPath = currentDirectoryPath;
		int n = 1;
		if (isRenderFontOutline)
			n++;
		String[] arg = new String[n];
		n = 0;
		arg[n++] = "--open-only";
		if (isRenderFontOutline)
			arg[n++] = "--render-font-outline";
		nativeInit(currentDirectoryPath, arg);

	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// nativeInit();
	}

	public void onSurfaceChanged(GL10 gl, int w, int h) {
		// gl.glViewport(0, 0, w, h);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glViewport(0, 0, w, h);
		gl.glOrthof(0.0f, (float) w, (float) h, 0.0f, 0.0f, 1.0f);
		nativeResize(w, h);
	}

	public void onDrawFrame(GL10 gl) {

		nativeInitJavaCallbacks();

		// Calls main() and never returns,
		// hehe - we'll call eglSwapBuffers() from native code

		int n = 0;
		if (isRenderFontOutline)
			n++;
		String[] arg = new String[n];
		n = 0;
		if (isRenderFontOutline)
			arg[n++] = "--render-font-outline";
		nativeInit(currentDirectoryPath, arg);

	}

	@NativeReferenced
	public int swapBuffers() // Called from native code, returns 1 on success,
								// 0 when GL context lost (user put app to
								// background)
	{
		return super.SwapBuffers() ? 1 : 0;
	}

	public void exitApp() {
		nativeDone();
	};

	private native void nativeInitJavaCallbacks();

	private native void nativeInit(String currentDirectoryPath, String[] arg);

	private native void nativeResize(int w, int h);

	private native void nativeDone();

	// private Context context = null;

	private EGL10 mEgl = null;
	private EGLDisplay mEglDisplay = null;
	private EGLSurface mEglSurface = null;
	private EGLContext mEglContext = null;
	private int skipFrames = 0;
}