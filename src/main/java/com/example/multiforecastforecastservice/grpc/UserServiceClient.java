package com.example.multiforecastforecastservice.grpc;

import com.example.multiforecastforecastservice.dto.ForecastLocation;
import com.example.multiforecastforecastservice.mapper.ForecastLocationMapper;
import com.multiforecast.userservice.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

@Slf4j
@GRpcService
public class UserServiceClient {

    @Value("${app.grpc.userService.host}")
    private String userServiceHost;
    @Value("${app.grpc.userService.port}")
    private Integer userServicePort;

    public Optional<ForecastLocation> getLocation(Long userId) {
        if (userId == null) {
            log.warn("Empty user Id");
            return Optional.empty();
        }

        ManagedChannel channel = ManagedChannelBuilder.forAddress(userServiceHost, userServicePort)
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

    public String getUserSearchPath(Long userId) {
        if (userId == null) {
            log.warn("Empty user Id");
            return Strings.EMPTY;
        }

        ManagedChannel channel = ManagedChannelBuilder.forAddress(userServiceHost, userServicePort)
                .usePlaintext()
                .build();

        UserServiceGrpc.UserServiceBlockingStub stub = UserServiceGrpc.newBlockingStub(channel);

        UserSearchPathResponse userSearchPath = stub.getUserSearchPath(UserSearchPathRequest.newBuilder().setUserId(userId).build());
        String result = userSearchPath.getSearchPath();


        channel.shutdown();
        return result;
    }
}
