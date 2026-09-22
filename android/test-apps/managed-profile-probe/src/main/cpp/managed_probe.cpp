#include <jni.h>
#include <cerrno>
#include <cstring>
#include <fcntl.h>
#include <unistd.h>
#include <sys/syscall.h>
#include <sys/system_properties.h>
#include <thread>
#include <string>

static const char* error_category(int error) {
    if (error == EACCES || error == EPERM) return "PERMISSION_DENIED";
    if (error == ENOENT) return "ABSENT";
    return "OTHER_ERROR";
}

static const char* access_category(const char* path, bool write, bool observe = false) {
    int flags = (write ? O_WRONLY | O_APPEND : O_RDONLY) | O_CLOEXEC;
    int fd = static_cast<int>(syscall(SYS_openat, AT_FDCWD, path, flags));
    if (fd < 0) return error_category(errno);
    char synthetic = 'S';
    ssize_t count = observe ? 0 : (write ? syscall(SYS_write, fd, &synthetic, 1)
                                       : syscall(SYS_read, fd, &synthetic, 1));
    int error = errno;
    int closed = close(fd);
    if (count < 0) return error_category(error);
    if (closed < 0 || (write && count != 1)) return "OTHER_ERROR";
    return "ACCESSIBLE";
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_privacydecoy_research_managedprobe_Evidence_nativeCategories(
        JNIEnv* env, jclass, jstring management, jstring peer, jstring fingerprint) {
    const char* m = env->GetStringUTFChars(management, nullptr);
    if (!m) return nullptr;
    const char* p = env->GetStringUTFChars(peer, nullptr);
    if (!p) { env->ReleaseStringUTFChars(management, m); return nullptr; }
    const char* build = env->GetStringUTFChars(fingerprint, nullptr);
    if (!build) { env->ReleaseStringUTFChars(management, m); env->ReleaseStringUTFChars(peer, p); return nullptr; }
    std::thread worker([] {});
    worker.join();
    char property[PROP_VALUE_MAX] = {};
    bool available = __system_property_get("ro.build.fingerprint", property) > 0;
    std::string out = "native_uid=" + std::to_string(getuid())
            + "\nnative_gid=" + std::to_string(getgid())
            + "\nnative_pid_present=" + (getpid() > 0 ? "true" : "false")
            + "\nnative_thread=true\nproperty_available=" + (available ? "true" : "false")
            + "\nproperty_matches_java_build=" + (available && strcmp(property, build) == 0 ? "true" : "false");
    out += std::string("\nproc_self=") + access_category("/proc/self/status", false, true);
    // Availability only. boot_id is never read or used as nonce infrastructure.
    out += std::string("\nproc_host=") + access_category("/proc/sys/kernel/random/boot_id", false, true);
    out += std::string("\nsys=") + access_category("/sys/devices", false, true);
    out += std::string("\nnative_management_read=") + access_category(m, false);
    out += std::string("\nnative_management_write=") + access_category(m, true);
    out += std::string("\nnative_peer_read=") + access_category(p, false);
    out += std::string("\nnative_peer_write=") + access_category(p, true) + "\n";
    env->ReleaseStringUTFChars(management, m);
    env->ReleaseStringUTFChars(peer, p);
    env->ReleaseStringUTFChars(fingerprint, build);
    return env->NewStringUTF(out.c_str());
}
