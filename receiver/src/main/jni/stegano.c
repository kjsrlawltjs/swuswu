#include <jni.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <stdlib.h>
#include <errno.h>

jint Java_com_steganomobile_receiver_controller_cc_UnixSocketDiscoveryReceiver_getOpenPort(JNIEnv* env, jobject thiz, jint start, jint end) {

    int sd;
    int port;
    int rval;
    struct hostent *hostaddr;   //To be used for IPaddress
    struct sockaddr_in servaddr;   //socket structure

    sd = socket(PF_INET, SOCK_STREAM, IPPROTO_TCP); //created the tcp socket
    if (sd == -1) {
        return 0;
    }
    memset(&servaddr, 0, sizeof(servaddr));
    hostaddr = gethostbyname("127.0.0.1"); //get the ip 1st argument
    servaddr.sin_family = AF_INET;
    memcpy(&servaddr.sin_addr, hostaddr->h_addr, hostaddr->h_length);

    for (port = start; port <= end; port++)
    {
        servaddr.sin_port = htons(port); //set the portno

        rval = connect(sd, (struct sockaddr *) &servaddr, sizeof(servaddr));
        if (rval != -1) {
            close(sd);
            return port - start - 128;
        }
    }
    close(sd);
    return 0;
}
