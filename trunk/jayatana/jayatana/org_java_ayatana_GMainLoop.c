#include "org_java_ayatana_GMainLoop.h"

#include <pthread.h>
#include <glib.h>
#include <gio/gio.h>
#include <X11/Xlib.h>

GMainLoop *loop;

void *g_main_loop_thread( void *ptr ) {
	loop = g_main_loop_new(NULL, FALSE);
	g_main_loop_run(loop);
	return NULL;
}

JNIEXPORT void JNICALL Java_org_java_ayatana_GMainLoop_runGMainLoop
  (JNIEnv *env, jclass thatclass) {
	g_type_init();
	g_thread_init(NULL);
	XInitThreads();
	g_thread_create(g_main_loop_thread, NULL, TRUE, NULL);
}

JNIEXPORT void JNICALL Java_org_java_ayatana_GMainLoop_quitGMainLoop
  (JNIEnv *env, jclass thatclass) {
	g_main_loop_quit(loop);
}