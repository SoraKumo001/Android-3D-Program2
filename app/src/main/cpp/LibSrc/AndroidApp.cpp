#include <string.h>
#include <android/log.h>
#include "aflStd.h"
#include "AndroidApp.h"

using namespace AFL;

jobject AndroidApp::m_obj;
jmethodID AndroidApp::MID_open;
jmethodID AndroidApp::MID_getFileSize;
jmethodID AndroidApp::MID_openImage;
jmethodID AndroidApp::MID_getImageSize;
jmethodID AndroidApp::MID_getFontSize;
jmethodID AndroidApp::MID_getFontImage;
AAssetManager* AndroidApp::m_assetManager;

std::map<int,JNIEnv*> g_mapEnv;

void AndroidApp::init(JNIEnv* env,jobject obj)
{
	g_mapEnv[pthread_self()] = env;
	if(!m_obj)
	{
		m_obj = env->NewGlobalRef(obj);
	}
	if(MID_open == 0)
	{
		//クラス取得
		jclass jcls = env->GetObjectClass(  obj );

		//メソッド取得
		MID_open = env->GetMethodID(  jcls, "open", "(Ljava/lang/String;)[B");
		MID_getFileSize = env->GetMethodID(  jcls, "getFileSize", "(Ljava/lang/String;)I");
		MID_openImage = env->GetMethodID(  jcls, "openImage", "(Ljava/lang/String;Ljava/nio/ByteBuffer;IIZ)Z");
		MID_getImageSize = env->GetMethodID(  jcls, "getImageSize", "(Ljava/lang/String;)[I");
		MID_getFontSize = env->GetMethodID(  jcls, "getFontSize", "(Ljava/lang/String;IIZ)[I");
		MID_getFontImage = env->GetMethodID(  jcls, "getFontImage", "(Ljava/nio/ByteBuffer;IILjava/lang/String;IIIIZ)Z");
	}


}


char* AndroidApp::readFile(const char* fileName)
{

	//JNIEnv* env = g_mapEnv[pthread_self()];
	__android_log_print( ANDROID_LOG_DEBUG,"File","%s ",fileName);

	AAssetManager* am = AndroidApp::getAssetManager();
	AAsset* asset = AAssetManager_open(am, fileName, AASSET_MODE_STREAMING);
    if(asset == NULL)
    	return NULL;
    off_t length;
    length = AAsset_getLength(asset);
    char* data = new char[length+1];

	AAsset_read(asset,data,length);
	data[length] = 0;

    AAsset_close(asset);

	return data;
}
int AndroidApp::getFileSize(const char* fileName)
{
	JNIEnv* env = g_mapEnv[pthread_self()];

	//文字列の設定
	jstring strj = env->NewStringUTF(fileName);
	//メソッド呼び出し
	jint size = (jint)env->CallIntMethod(  m_obj, MID_getFileSize,strj );
	env->DeleteLocalRef (strj);
	return size;
}
void AndroidApp::getFontSize(int* size,const char* text,int fontSize,int limitWidth,bool mline)
{
	JNIEnv* env = g_mapEnv[pthread_self()];

	//文字列の設定
	jstring strj = env->NewStringUTF(text);
	//メソッド呼び出し
	jintArray arrj = (jintArray)env->CallObjectMethod(  m_obj, MID_getFontSize,strj,fontSize,limitWidth,mline );
	env->DeleteLocalRef (strj);

	if(arrj)
	{
		jint* arrSrc = (jint*)env->GetIntArrayElements(arrj,NULL);
		size[0] = arrSrc[0];
		size[1] = arrSrc[1];
		env->ReleaseIntArrayElements (arrj,arrSrc,0);
		env->DeleteLocalRef (arrj);
	}
}

void AndroidApp::getFontSize(int* size,const wchar_t* text,int fontSize,int limitWidth,bool mline)
{
	getFontSize(size,UTF8(text),fontSize,limitWidth,mline);
}

void AndroidApp::getFontImage(void* dest,int width,int height,const char* text,int fontSize,int color,int bcolor,int limitWidth,bool mline)
{
	JNIEnv* env = g_mapEnv[pthread_self()];

	int length = width * height * 4;
	jobject buffer = env->NewDirectByteBuffer(dest,length);

	//文字列の設定
	jstring strj = env->NewStringUTF(text);
	//メソッド呼び出し
	jboolean result = (jboolean)env->CallBooleanMethod(  m_obj, MID_getFontImage,buffer,width,height,strj,fontSize,color,bcolor,limitWidth,mline );
	env->DeleteLocalRef (strj);
	env->DeleteLocalRef (buffer);
}

void AndroidApp::getFontImage(void* dest,int width,int height,const wchar_t* text,int fontSize,int color,int bcolor,int limitWidth,bool mline)
{
	getFontImage(dest,width,height,UTF8(text),fontSize,color,bcolor,limitWidth,mline);
}

bool AndroidApp::getImageSize(const char* fileName,void* size)
{
	JNIEnv* env = g_mapEnv[pthread_self()];

	//文字列の設定
	jstring strj = env->NewStringUTF(fileName);
	//メソッド呼び出し
	jintArray arrj = (jintArray)env->CallObjectMethod(  m_obj, MID_getImageSize,strj );
	env->DeleteLocalRef (strj);
	if(arrj)
	{
		jint* arrSrc = (jint*)env->GetIntArrayElements(arrj,NULL);
		((int*)size)[0] = arrSrc[0];
		((int*)size)[1] = arrSrc[1];

		env->ReleaseIntArrayElements (arrj,arrSrc,0);
		env->DeleteLocalRef (arrj);
		return true;
	}

	return false;
}
bool AndroidApp::openImage(const char* fileName,void* dest,int width,int height,bool filter)
{
	JNIEnv* env = g_mapEnv[pthread_self()];

	int size[2];
	if(!getImageSize(fileName,size))
		return false;

	int length = width * height * 4;

	jobject buffer = env->NewDirectByteBuffer(dest,length);

	//文字列の設定
	jstring strj = env->NewStringUTF(fileName);
	//メソッド呼び出し
	jboolean ret = env->CallBooleanMethod(  m_obj, MID_openImage,strj,buffer,width,height,filter);
	env->DeleteLocalRef (strj);
	env->DeleteLocalRef (buffer);

	return ret;
}
bool AndroidApp::setAssetManager(JNIEnv* env, jobject assetManager)
{
	AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);
	if(!mgr)
		return false;
	m_assetManager = mgr;
	return true;
}
AAssetManager* AndroidApp::getAssetManager()
{
	return m_assetManager;
}
