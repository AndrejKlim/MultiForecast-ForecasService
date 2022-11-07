package com.example.multiforecastforecastservice.grpc;

import com.multiforecast.userservice.LocationRequest;
import com.multiforecast.userservice.LocationResponse;
import com.multiforecast.userservice.LocationServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.lognet.springboot.grpc.GRpcService;

import java.util.Optional;

@GRpcService
public class LocationClient {

    public Optional<String> getLocation(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6566)
                .usePlaintext()
                .build();

        LocationServiceGrpc.LocationServiceBlockingStub stub = LocationServiceGrpc.newBlockingStub(channel);

        LocationResponse response = stub.getLocation(LocationRequest.newBuilder().setUserId(1).build());

        channel.shutdown();

        return Optional.of("response: lon " + response.getLon() + " lat " + response.getLat());
    }
}
