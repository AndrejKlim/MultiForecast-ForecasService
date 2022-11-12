package com.example.multiforecastforecastservice.grpc;

import com.example.multiforecastforecastservice.dto.ForecastLocation;
import com.example.multiforecastforecastservice.dto.User;
import com.example.multiforecastforecastservice.service.OpenWeatherForecastService;
import com.multiforecast.forecastservice.ForecastRequest;
import com.multiforecast.forecastservice.ForecastResponse;
import com.multiforecast.forecastservice.ForecastServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.lognet.springboot.grpc.GRpcService;

import java.util.Optional;


@GRpcService
@RequiredArgsConstructor
public class ForecastController extends ForecastServiceGrpc.ForecastServiceImplBase {

    private final OpenWeatherForecastService forecastService;
    private final LocationClient LocationClient;

    @Override
    public void getForecast(final ForecastRequest request, final StreamObserver<ForecastResponse> responseObserver) {
        Optional<ForecastLocation> location = LocationClient.getLocation(request.getUserId());
        User user = new User();
        location.ifPresent(user::setForecastLocation);

        String openWeatherForecast = forecastService.getOpenWeatherForecast(user);

        responseObserver.onNext(ForecastResponse.newBuilder()
                .setForecast(openWeatherForecast).build());
        responseObserver.onCompleted();
    }
}
