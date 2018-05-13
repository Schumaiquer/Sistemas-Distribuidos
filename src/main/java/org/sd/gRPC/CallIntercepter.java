package org.sd.gRPC;

import io.grpc.*;

import java.net.SocketAddress;
import java.security.Key;
import java.util.Set;

public class CallIntercepter implements ServerInterceptor {
    public static final Context.Key<SocketAddress> CLIENT_ADDRESS = Context.key("addr");

    public <ReqT, RespT> ServerCall.Listener<ReqT>
    interceptCall(ServerCall<ReqT, RespT> serverCall,
                  Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
      try {
          Attributes.Key<SocketAddress> clientIpKEY = null;
          Attributes attr = serverCall.getAttributes();
          Set<Attributes.Key<?>> attrKeys = attr.keys();

          for(Attributes.Key k : attrKeys) {
              if(k.toString().equals("remote-addr")) {
                  clientIpKEY = k;
                  break;
              }
          }
          SocketAddress address = attr.get(clientIpKEY);

          Context context = Context.current().withValue(CLIENT_ADDRESS, address);
          return Contexts.interceptCall(context, serverCall, metadata, serverCallHandler);
      } catch (Exception e) {
          System.out.println(e.getMessage());
          return null;
      }
    }
}
