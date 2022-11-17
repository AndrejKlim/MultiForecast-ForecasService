package com.example.multiforecastforecastservice.grpc;

import com.example.multiforecastforecastservice.dto.ForecastLocation;
import com.example.multiforecastforecastservice.dto.User;
import com.example.multiforecastforecastservice.service.OpenWeatherForecastService;
import com.google.protobuf.Any;
import com.google.rpc.Status;
import com.multiforecast.forecastservice.ForecastRequest;
import com.multiforecast.forecastservice.ForecastResponse;
import com.multiforecast.forecastservice.ForecastServiceGrpc;
import com.multiforecast.forecastservice.UserNotFoundError;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.lognet.springboot.grpc.GRpcService;

import java.util.Optional;


@GRpcService
@RequiredArgsConstructor
public class ForecastController extends ForecastServiceGrpc.ForecastServiceImplBase {

    private final OpenWeatherForecastService forecastService;
    private final LocationClient locationClient;

    @Override
    public void getForecast(final ForecastRequest request, final StreamObserver<ForecastResponse> responseObserver) {
        Optional<ForecastLocation> location;
        try {
            location = locationClient.getLocation(request.getUserId());
        } catch (UserNotFoundException e) {
            Status status = Status.newBuilder()
                    .setCode(com.google.rpc.Code.NOT_FOUND.getNumber())
                    .setMessage("User not found")
                    .addDetails(Any.pack(UserNotFoundError.newBuilder()
                            .setUserId(request.getUserId())
                            .build()))
                    .build();
            responseObserver.onError(StatusProto.toStatusRuntimeException(status));
            return;
        }

        User user = new User();
        location.ifPresent(user::setForecastLocation);

        String openWeatherForecast = forecastService.getOpenWeatherForecast(user);

        responseObserver.onNext(ForecastResponse.newBuilder()
                .setForecast(openWeatherForecast).build());
        responseObserver.onCompleted();
    }
}
