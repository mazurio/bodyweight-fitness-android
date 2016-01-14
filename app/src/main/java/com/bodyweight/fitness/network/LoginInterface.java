package com.bodyweight.fitness.network;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface LoginInterface {
    @LambdaFunction
    LoginResponse LambdAuthLogin(LoginRequest request);
}