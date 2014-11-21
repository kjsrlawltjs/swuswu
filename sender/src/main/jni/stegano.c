#include <jni.h>
#include <stdio.h>
#include <stdlib.h>

char* stegano_memory;

void Java_com_steganomobile_sender_controller_cc_MemoryLoad_freeStegano(JNIEnv* env, jobject thiz) {
    free(stegano_memory);
}

void Java_com_steganomobile_sender_controller_cc_MemoryLoad_allocateStegano(JNIEnv* env, jobject thiz, jint stegano_i) {
    stegano_memory = malloc((long) 10240 * 4 * stegano_i);
}