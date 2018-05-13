package org.sd.gRPC;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.4.0)",
    comments = "Source: database.proto")
public final class ExecuteCommandServiceGrpc {

  private ExecuteCommandServiceGrpc() {}

  public static final String SERVICE_NAME = "org.sd.gRPC.ExecuteCommandService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<org.sd.gRPC.CommandRequest,
      org.sd.gRPC.CommandResponse> METHOD_EXECUTE_COMMAND =
      io.grpc.MethodDescriptor.<org.sd.gRPC.CommandRequest, org.sd.gRPC.CommandResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "org.sd.gRPC.ExecuteCommandService", "ExecuteCommand"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              org.sd.gRPC.CommandRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              org.sd.gRPC.CommandResponse.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ExecuteCommandServiceStub newStub(io.grpc.Channel channel) {
    return new ExecuteCommandServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ExecuteCommandServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ExecuteCommandServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ExecuteCommandServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ExecuteCommandServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class ExecuteCommandServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void executeCommand(org.sd.gRPC.CommandRequest request,
        io.grpc.stub.StreamObserver<org.sd.gRPC.CommandResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_EXECUTE_COMMAND, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_EXECUTE_COMMAND,
            asyncUnaryCall(
              new MethodHandlers<
                org.sd.gRPC.CommandRequest,
                org.sd.gRPC.CommandResponse>(
                  this, METHODID_EXECUTE_COMMAND)))
          .build();
    }
  }

  /**
   */
  public static final class ExecuteCommandServiceStub extends io.grpc.stub.AbstractStub<ExecuteCommandServiceStub> {
    private ExecuteCommandServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ExecuteCommandServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ExecuteCommandServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ExecuteCommandServiceStub(channel, callOptions);
    }

    /**
     */
    public void executeCommand(org.sd.gRPC.CommandRequest request,
        io.grpc.stub.StreamObserver<org.sd.gRPC.CommandResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_EXECUTE_COMMAND, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ExecuteCommandServiceBlockingStub extends io.grpc.stub.AbstractStub<ExecuteCommandServiceBlockingStub> {
    private ExecuteCommandServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ExecuteCommandServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ExecuteCommandServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ExecuteCommandServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.sd.gRPC.CommandResponse executeCommand(org.sd.gRPC.CommandRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_EXECUTE_COMMAND, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ExecuteCommandServiceFutureStub extends io.grpc.stub.AbstractStub<ExecuteCommandServiceFutureStub> {
    private ExecuteCommandServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ExecuteCommandServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ExecuteCommandServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ExecuteCommandServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.sd.gRPC.CommandResponse> executeCommand(
        org.sd.gRPC.CommandRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_EXECUTE_COMMAND, getCallOptions()), request);
    }
  }

  private static final int METHODID_EXECUTE_COMMAND = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ExecuteCommandServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ExecuteCommandServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_EXECUTE_COMMAND:
          serviceImpl.executeCommand((org.sd.gRPC.CommandRequest) request,
              (io.grpc.stub.StreamObserver<org.sd.gRPC.CommandResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static final class ExecuteCommandServiceDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.sd.gRPC.Database.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (ExecuteCommandServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ExecuteCommandServiceDescriptorSupplier())
              .addMethod(METHOD_EXECUTE_COMMAND)
              .build();
        }
      }
    }
    return result;
  }
}
