package com.example.multiforecastforecastservice.grpc;

import com.example.multiforecastforecastservice.dto.ForecastLocation;
import com.example.multiforecastforecastservice.mapper.ForecastLocationMapper;
import com.multiforecast.userservice.LocationRequest;
import com.multiforecast.userservice.LocationResponse;
import com.multiforecast.userservice.LocationServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

@Slf4j
@GRpcService
public class LocationClient {

    @Value("${app.grpc.userService.host}")
    private String userServiceHost;

    public Optional<ForecastLocation> getLocation(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }

        ManagedChannel channel = ManagedChannelBuilder.forAddress(userServiceHost, 6566)
                .usePlaintext()
                .build();

        LocationServiceGrpc.LocationServiceBlockingStub stub = LocationServiceGrpc.newBlockingStub(channel);

        Optional<LocationResponse> locationOptional = Optional.empty();

        try {
            locationOptional = Optional.ofNullable(stub.getLocation(LocationRequest.newBuilder().setUserId(userId).build()));
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new UserNotFoundException();
            }
            log.error("No user found. {}", e.getMessage());
        }
        channel.shutdown();
        return locationOptional.map(ForecastLocationMapper::fromResponse);
    }
}
