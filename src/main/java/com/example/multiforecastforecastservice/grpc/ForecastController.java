package com.example.multiforecastforecastservice.grpc;

import com.example.multiforecastforecastservice.dto.ForecastLocation;
import com.example.multiforecastforecastservice.dto.User;
import com.example.multiforecastforecastservice.service.OpenWeatherForecastService;
import com.example.multiforecastforecastservice.service.translator.TranslatorService;
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
    private final UserServiceClient userServiceClient;
    private final TranslatorService translatorService;

    @Override
    public void getForecast(final ForecastRequest request, final StreamObserver<ForecastResponse> responseObserver) {
        Optional<ForecastLocation> location;
        try {
            location = userServiceClient.getLocation(request.getUserId());
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
        user.setId(request.getUserId());
        location.ifPresent(user::setForecastLocation);

        String openWeatherForecast = forecastService.getOpenWeatherForecast(user);
        String translated = translatorService.translate(openWeatherForecast);


        responseObserver.onNext(ForecastResponse.newBuilder()
                .setForecast(translated).build());
        responseObserver.onCompleted();
    }
}
