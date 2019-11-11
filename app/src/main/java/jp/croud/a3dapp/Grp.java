package jp.croud.a3dapp;


import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

class ContextFactory implements GLSurfaceView.EGLContextFactory
{
	private static int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

	public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig)
	{
		int[] attrib_list = {EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE };
		EGLContext context = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, attrib_list);
		Log.d("ContextFactory", "Create");
		return context;
	}

	public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context)
	{
		egl.eglDestroyContext(display, context);
		Log.d("ContextFactory", "Destory");
	}
}

public class Grp extends GLSurfaceView implements Renderer, ThreadAction
{

	static
	{
//		System.loadLibrary("c++_shared");
		System.loadLibrary("Grp");
	}

	native public void onNdkInit(Context con);

	native private void onNdkSurfaceCreated();

	native private void onNdkSurfaceChanged(int width, int height);

	native private void onNdkDrawFrame();
	native private void onNdkAction();
	native private void onNdkSurfaceRelease();
	native private void onNdkTouchEvent(float[] point);

	ThreadTimer threadTimer;


	public int getFileSize(String name)
	{
		AssetManager as = activity.getResources().getAssets();
		try
		{
			InputStream is = as.open(name);
			int size = is.available();
			return size;
		} catch (IOException e)
		{
			return 0;
		}
	}
	public byte[] open(String name)
	{
		AssetManager as = activity.getResources().getAssets();
		try
		{
			InputStream is = as.open(name);
			byte[] b = new byte[is.available()];
			is.read(b);
			Log.d("File", name);
			return b;
		} catch (IOException e)
		{
			Log.e("File", name);
			return null;
		}
	}

	public int[] getImageSize(String fileName)
	{
		AssetManager as = activity.getResources().getAssets();

		try
		{
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;

			InputStream is = as.open(fileName);
			if (is == null)
				return null;
			BitmapFactory.decodeStream(is, null, opts);

			int[] size = new int[2];
			size[0] = opts.outWidth;
			size[1] = opts.outHeight;
			return size;
		} catch (IOException e)
		{
			return null;
		}
	}
	public int[] getFontSize(String text,int fontSize,int limitWidth,boolean mline)
	{
		Paint paint = new Paint();
        paint.setTextSize(fontSize);
        paint.setAntiAlias(true);

        int[] size = new int[2];

        size[0] =  (int)paint.measureText(text);
        size[1] = fontSize;

		return size;
	}
	public boolean getFontImage(ByteBuffer buffer,int width,int height,String text,int fontSize,int color,int bcolor,int limitWidth,boolean mline)
	{
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);

		Paint paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(fontSize);
        paint.setAntiAlias(true);
		canvas.drawText(text,0,fontSize,paint);

		bitmap.copyPixelsToBuffer(buffer);
		return true;
	}
	public boolean openImage(String fileName,ByteBuffer buffer,int width,int height,boolean fillter)
	{
		try
		{
			AssetManager as = activity.getResources().getAssets();
			InputStream is = as.open(fileName);
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inPreferredConfig = Bitmap.Config.ARGB_8888;

			Bitmap bitmap = BitmapFactory.decodeStream(is, null, opts);
			if (bitmap == null)
				return false;

			try
			{
				if(opts.outWidth!=width || opts.outHeight!=height)
				{
					//サイズの変更
					Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

					Canvas c = new Canvas(b);
					if(fillter)
					{
						//スケーリングで最大まで広げる
						Rect rectSrc = new Rect(0, 0,opts.outWidth,opts.outHeight);
						Rect rectDest = new Rect(0, 0,width,height);
						c.drawBitmap(bitmap,rectSrc,rectDest,null);
					}
					else
					{
						//ソース画像維持
						c.drawBitmap(bitmap,0,0,null);
					}
					b.copyPixelsToBuffer(buffer);
				}
				else
				{
					bitmap.copyPixelsToBuffer(buffer);
				}
				Log.d("Load-Image", fileName);
				return true;
			} catch (IllegalArgumentException e)
			{
			}
		} catch (IOException e)
		{

		}
		Log.e("Load-Image", fileName);
		return false;
	}

	boolean m_destory;
	Activity activity;

	public Grp(Activity activity)
	{
		super(activity);
		setEGLContextClientVersion(2);
		setEGLConfigChooser(8, 8, 8, 0, 0, 0);
		//setEGLConfigChooser(5, 6, 5, 0, 16, 8);
		//setEGLContextFactory(new ContextFactory());
		setDebugFlags(DEBUG_CHECK_GL_ERROR | DEBUG_LOG_GL_CALLS);
		setRenderer(this);
		this.activity = activity;
		onNdkInit(activity);
	}

	public void setActivity(Activity activity)
	{
		this.activity = activity;
	}
	public boolean onTouchEvent(MotionEvent event)
	{
		int pointerCount = event.getPointerCount();
		int i;

		float[] point = new float[pointerCount*5];
		for(i=0;i<pointerCount;i++)
		{
			int index = event.getHistorySize()-1;
			point[i*5+0] = event.getPointerId(i);
			point[i*5+1] = (event.getX(i) - getWidth()*0.5f) / getWidth() * 2.0f;
			point[i*5+2] = (event.getY(i) - getHeight()*0.5f) / getHeight() * 2.0f;
			if(index > 0)
			{
				point[i*5+3] = (event.getHistoricalY(i,index) - getWidth()*0.5f) / getWidth() * 2.0f;
				point[i*5+4] = (event.getHistoricalY(i,index) - getHeight()*0.5f) / getHeight() * 2.0f;
			}
			else
			{
				point[i*5+3] = 0.0f;
				point[i*5+4] = 0.0f;
			}
		}
		onNdkTouchEvent(point);

		return true;
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		// TODO 自動生成されたメソッド・スタブ
		super.surfaceCreated(holder);
		m_destory = true;
		Log.d("MSG", "surfaceCreated");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		stop();
		Log.d("MSG", "surfaceDestroyed");
		queueEvent(new Runnable()
		{
			public void run()
			{
				onNdkSurfaceRelease();
			}
		});
		m_destory = true;

		super.surfaceDestroyed(holder);
	}

	@Override
	public void onDrawFrame(GL10 gl)
	{
		onNdkDrawFrame();

	}
	public void start()
	{
		threadTimer = new ThreadTimer();
		threadTimer.setWait(false);
		threadTimer.setInterval(1000/30);
		threadTimer.setAction(this);
		threadTimer.startThread(null);
	}
	public void stop()
	{
		threadTimer.stopThread();
		threadTimer = null;
	}
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		setRenderMode(RENDERMODE_WHEN_DIRTY);
		Log.d("MSG", "onSurfaceCreated");
		if(m_destory)
		{
			onNdkSurfaceCreated();
			m_destory = false;
			Log.d("MSG", "onSurfaceCreated2");
			start();
		}

	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		Log.d("MSG", "onSurfaceChanged");

		if(m_destory)
		{
			onNdkSurfaceCreated();
			m_destory = false;
			start();
		}
		onNdkSurfaceChanged(width, height);



	}

	@Override
	public void onStart(Object object)
	{

	}

	@Override
	public void onEnd(Object object)
	{

	}

	@Override
	public void onRender(Object object)
	{
		requestRender();
	}

	@Override
	public void onAction(Object object)
	{
		onNdkAction();
	}

	@Override
	public void onIdel(Object object)
	{
	}

}