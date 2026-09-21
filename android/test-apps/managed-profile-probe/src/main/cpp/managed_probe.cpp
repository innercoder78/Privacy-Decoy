#include <jni.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/syscall.h>
#include <thread>
#include <string>
static const char* access_category(const char* p){int fd=(int)syscall(SYS_openat,AT_FDCWD,p,O_RDWR|O_CLOEXEC);if(fd>=0){close(fd);return "OPENED";}return "BLOCKED";}
extern "C" JNIEXPORT jstring JNICALL Java_com_privacydecoy_research_managedprobe_Evidence_nativeCategories(JNIEnv* e,jclass,jstring m,jstring p){
 const char* a=e->GetStringUTFChars(m,nullptr);const char*b=e->GetStringUTFChars(p,nullptr);std::thread t([]{});t.join();int proc=open("/proc/self/status",O_RDONLY|O_CLOEXEC);int sys=open("/sys/devices",O_RDONLY|O_CLOEXEC);
 std::string out=std::string("native_uid=")+std::to_string(getuid())+"\nnative_gid="+std::to_string(getgid())+"\nnative_pid_present="+(getpid()>0?std::string("true"):"false")+"\nnative_thread=true\nproc_self="+(proc>=0?std::string("AVAILABLE"):"BLOCKED")+"\nsys="+(sys>=0?std::string("AVAILABLE"):"BLOCKED")+"\nmanagement_open="+access_category(a)+"\npeer_open="+access_category(b)+"\n";if(proc>=0)close(proc);if(sys>=0)close(sys);e->ReleaseStringUTFChars(m,a);e->ReleaseStringUTFChars(p,b);return e->NewStringUTF(out.c_str());}
