package com.example.multiforecastforecastservice.grpc;

import com.example.multiforecastforecastservice.dto.ForecastLocation;
import com.example.multiforecastforecastservice.mapper.ForecastLocationMapper;
import com.multiforecast.userservice.LocationRequest;
import com.multiforecast.userservice.LocationServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.lognet.springboot.grpc.GRpcService;

import java.util.Optional;

@GRpcService
public class LocationClient {

    public Optional<ForecastLocation> getLocation(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6566)
                .usePlaintext()
                .build();

        LocationServiceGrpc.LocationServiceBlockingStub stub = LocationServiceGrpc.newBlockingStub(channel);

        Optional<ForecastLocation> locationOptional = Optional.ofNullable(stub.getLocation(LocationRequest.newBuilder().setUserId(1).build()))
                .map(ForecastLocationMapper::fromResponse);

        channel.shutdown();

        return locationOptional;
    }
}
