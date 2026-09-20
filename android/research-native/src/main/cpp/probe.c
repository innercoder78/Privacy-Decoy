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


#include <sys/socket.h>
#include <arpa/inet.h>
#include <poll.h>

static int network_category(int err) {
    if (err == EACCES || err == EPERM) return 1;
    if (err == ENETUNREACH || err == EAFNOSUPPORT || err == EADDRNOTAVAIL) return 2;
    if (err == ETIMEDOUT || err == EAGAIN) return 3;
    return 4;
}
JNIEXPORT jint JNICALL Java_com_privacydecoy_research_nativeprobe_NativeProbe_network(
        JNIEnv *env, jclass cls, jint operation) {
    (void)env; (void)cls;
    if (operation < 0 || operation > 2) return 1;
    int fd = socket(operation == 2 ? AF_INET6 : AF_INET,
        (operation == 0 ? SOCK_STREAM : SOCK_DGRAM) | SOCK_CLOEXEC | SOCK_NONBLOCK, 0);
    if (fd < 0) return network_category(errno);
    struct sockaddr_in v4 = {.sin_family = AF_INET, .sin_port = htons(operation == 0 ? 46151 : 46152)};
    struct sockaddr_in6 v6 = {.sin6_family = AF_INET6, .sin6_port = htons(46152)};
    inet_pton(AF_INET, operation == 0 ? "10.0.2.2" : "198.51.100.7", &v4.sin_addr);
    inet_pton(AF_INET6, "2001:db8::7", &v6.sin6_addr);
    struct sockaddr *address = operation == 2 ? (struct sockaddr*)&v6 : (struct sockaddr*)&v4;
    socklen_t length = operation == 2 ? sizeof(v6) : sizeof(v4);
    int result;
    if (operation == 0) {
        result = connect(fd,address,length);
        if (result < 0 && errno == EINPROGRESS) {
            struct pollfd item = {.fd=fd,.events=POLLOUT};
            int ready = poll(&item,1,1200);
            if (ready == 0) { close(fd); return 3; }
            if (ready < 0) { int err=errno; close(fd); return network_category(err); }
            int err=0; socklen_t size=sizeof(err);
            if (getsockopt(fd,SOL_SOCKET,SO_ERROR,&err,&size)<0) err=errno;
            close(fd); return err ? network_category(err) : 0;
        }
    } else {
        const unsigned char value=53;
        result=(int)sendto(fd,&value,1,0,address,length);
    }
    int err=errno;
    close(fd);
    return result >= 0 ? 0 : network_category(err);
}
