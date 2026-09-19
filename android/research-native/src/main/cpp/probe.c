#include <jni.h>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <stdio.h>
#include <sys/syscall.h>
#include <sys/system_properties.h>

// 0 accessible, 1 permission denied, 2 absent, 3 other error. No contents returned.
static int opened(const char *path, int flags) {
    int fd = (int)syscall(SYS_openat, AT_FDCWD, path, flags | O_CLOEXEC, 0);
    if (fd >= 0) { close(fd); return 0; }
    return errno == EACCES || errno == EPERM ? 1 : errno == ENOENT ? 2 : 3;
}
JNIEXPORT jintArray JNICALL Java_com_privacydecoy_research_nativeprobe_NativeProbe_observe(
        JNIEnv *env, jclass cls, jstring sentinel, jint manager_pid) {
    (void)cls;
    const char *path = (*env)->GetStringUTFChars(env, sentinel, NULL);
    if (!path) return NULL;
    char manager_proc[64];
    snprintf(manager_proc, sizeof(manager_proc), "/proc/%d/maps", manager_pid);
    char property[PROP_VALUE_MAX];
    jint values[] = {(jint)getuid(), (jint)getgid(), (jint)getpid(),
        opened(path, O_RDONLY), opened(path, O_WRONLY),
        opened("/proc/self/maps", O_RDONLY), opened(manager_proc, O_RDONLY),
        opened("/sys/devices/system/cpu/online", O_RDONLY),
        __system_property_get("ro.build.version.sdk", property) > 0};
    (*env)->ReleaseStringUTFChars(env, sentinel, path);
    jintArray result = (*env)->NewIntArray(env, 9);
    if (result) (*env)->SetIntArrayRegion(env, result, 0, 9, values);
    return result;
}
