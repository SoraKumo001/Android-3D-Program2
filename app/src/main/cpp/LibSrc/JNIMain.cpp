#include <android/log.h>

#define FUNC(name) Java_jp_croud_a3dapp_Grp_##name


#include "AndroidApp.h"
#include "aflOpenGL.h"
#include "aflInput.h"
#include "../Main.h"

//Main _g_main;
Main* g_main;
AFL::Input m_input;

extern "C" void FUNC(onNdkInit)(JNIEnv* env, jobject obj,jobject context)
{
	//クラス取得
	jclass jcls = env->GetObjectClass(  context );
	//メソッド取得
	jmethodID MID_getAssets = env->GetMethodID(  jcls, "getAssets", "()Landroid/content/res/AssetManager;");
	if(MID_getAssets)
	{
		jobject manager = env->CallObjectMethod(  context, MID_getAssets);
		manager = env->NewGlobalRef(manager);
		AndroidApp::setAssetManager(env,manager);
	}
	AndroidApp::init(env,obj);
	GLDevice::loadShaders("glsl");
	GLDevice::setTexturePath("Images");

}


extern "C" void FUNC(onNdkSurfaceCreated)(JNIEnv* env, jobject obj)
{
	__android_log_print( ANDROID_LOG_DEBUG,"Surace","Create:%d\n",(int)pthread_self());
	AndroidApp::init(env,obj);

	if(!g_main)
	{

		g_main = new Main();
		g_main->setInput(&m_input);
		__android_log_print( ANDROID_LOG_DEBUG,"Surace","1");
		g_main->init(NULL);
		__android_log_print( ANDROID_LOG_DEBUG,"Surace","4");
	}
	else
	{
		GLDevice::restore();
	}
}
extern "C" void FUNC(onNdkSurfaceRelease)(JNIEnv* env, jobject obj)
{
	AndroidApp::init(env,obj);
	if(g_main)
	{
		GLDevice::lost();
		//g_main->release();
		//delete g_main;
		//g_main = NULL;
	}
	__android_log_print( ANDROID_LOG_DEBUG,"Surace","Release:%d\n",(int)pthread_self());
}

extern "C" void FUNC(onNdkSurfaceChanged)(JNIEnv* env, jobject obj, jint width, jint height)
{
	__android_log_print( ANDROID_LOG_DEBUG,"Surace","Change:%d\n",(int)pthread_self());
	AndroidApp::init(env,obj);
	if(g_main)
		g_main->size(NULL,width,height);
}
extern "C" void FUNC(onNdkTouchEvent)(JNIEnv* env, jobject obj,jfloatArray point)
{
//	AndroidApp::init(env,obj);
	int size = env->GetArrayLength(point);
	float* data = (jfloat*)env->GetFloatArrayElements(point,NULL);
	if(size >= 5)
		__android_log_print( ANDROID_LOG_DEBUG,"Touch","%f,%f\n",data[1],data[2]);
	m_input.sendEvent(data,size);
	env->ReleaseFloatArrayElements (point,data,0);
	env->DeleteLocalRef (point);
}
AFL::Critical g_critical;
extern "C" void FUNC(onNdkDrawFrame)(JNIEnv* env, jobject obj)
{
	AndroidApp::init(env,obj);
	if(g_main)
	{
		g_critical.lock();
		g_main->render();
		g_critical.unlock();
	}
}
extern "C" void FUNC(onNdkAction)(JNIEnv* env, jobject obj)
{
	AndroidApp::init(env,obj);
	if(g_main)
	{
		g_critical.lock();
		g_main->action();
		g_critical.unlock();
	}
}
