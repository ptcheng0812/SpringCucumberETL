package etl.grpc;

import com.google.protobuf.*;
import com.google.protobuf.util.JsonFormat;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.MethodDescriptor;
import io.grpc.StatusRuntimeException;
import io.grpc.reflection.v1alpha.*;
import io.grpc.reflection.v1alpha.ServerReflectionGrpc.ServerReflectionBlockingStub;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class gRPCHandler {
    String targetUrl;
    String service;
    String method;
    String message;



    public gRPCHandler(String targetUrl, String service, String method, String message) throws InterruptedException {
        this.targetUrl = targetUrl;
        this.service = service;
        this.method = method;
        this.message = message;
    }

    // Create a channel
    ManagedChannel channel = ManagedChannelBuilder.forTarget(targetUrl)
            .useTransportSecurity()
            .build();

    // Discover services and methods
    Map<String, DynamicGrpcMethod> methods = discoverMethods(channel);


    public Map<String, DynamicGrpcMethod> discoverMethods(ManagedChannel channel) throws InterruptedException {
        Map<String, DynamicGrpcMethod> methods = new HashMap<>();
        CountDownLatch latch = new CountDownLatch(1);

        ServerReflectionGrpc.ServerReflectionStub reflectionStub = ServerReflectionGrpc.newStub(channel);

        StreamObserver<ServerReflectionRequest> requestObserver = reflectionStub.serverReflectionInfo(new StreamObserver<ServerReflectionResponse>() {
            @Override
            public void onNext(ServerReflectionResponse response) {
                if (response.hasFileDescriptorResponse()) {
                    FileDescriptorResponse fileDescriptorResponse = response.getFileDescriptorResponse();
                    for (com.google.protobuf.ByteString fdProtoBytes : fileDescriptorResponse.getFileDescriptorProtoList()) {
                        try {
                            DescriptorProtos.FileDescriptorProto fdProto = DescriptorProtos.FileDescriptorProto.parseFrom(fdProtoBytes);
                            com.google.protobuf.Descriptors.FileDescriptor fd = com.google.protobuf.Descriptors.FileDescriptor.buildFrom(fdProto, new com.google.protobuf.Descriptors.FileDescriptor[]{});
                            for (com.google.protobuf.Descriptors.ServiceDescriptor serviceDescriptor : fd.getServices()) {
                                for (com.google.protobuf.Descriptors.MethodDescriptor methodDescriptor : serviceDescriptor.getMethods()) {
                                    methods.put(serviceDescriptor.getName() + "." + methodDescriptor.getName(),
                                            new DynamicGrpcMethod(serviceDescriptor, methodDescriptor));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        requestObserver.onNext(ServerReflectionRequest.newBuilder()
                .setListServices("")
                .build());

        // Wait for reflection to complete
        latch.await(5, TimeUnit.SECONDS);

        requestObserver.onCompleted();
        return methods;
    }


    public class DynamicGrpcMethod {
        private final com.google.protobuf.Descriptors.MethodDescriptor methodDescriptor;

        public DynamicGrpcMethod(com.google.protobuf.Descriptors.ServiceDescriptor serviceDescriptor,
                                 com.google.protobuf.Descriptors.MethodDescriptor methodDescriptor) {
            this.methodDescriptor = methodDescriptor;
        }

        public com.google.protobuf.Descriptors.Descriptor getInputType() {
            return methodDescriptor.getInputType();
        }

        public DynamicMessage call(ManagedChannel channel, DynamicMessage request) throws Exception {
            MethodDescriptor<DynamicMessage, DynamicMessage> grpcMethodDescriptor = MethodDescriptor.<DynamicMessage, DynamicMessage>newBuilder()
                    .setType(MethodDescriptor.MethodType.UNARY)
                    .setFullMethodName(MethodDescriptor.generateFullMethodName(methodDescriptor.getService().getFullName(), methodDescriptor.getName()))
                    .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(DynamicMessage.getDefaultInstance(methodDescriptor.getInputType())))
                    .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(DynamicMessage.getDefaultInstance(methodDescriptor.getOutputType())))
                    .build();

            return io.grpc.stub.ClientCalls.blockingUnaryCall(channel, grpcMethodDescriptor, io.grpc.CallOptions.DEFAULT, request);
        }
    }

}

