package com.bodyweight.fitness.network;

import com.amazonaws.auth.AWSAbstractCognitoDeveloperIdentityProvider;
import com.amazonaws.regions.Regions;

public class AuthenticationProvider extends AWSAbstractCognitoDeveloperIdentityProvider {
    private static final String developerProvider = "com.bodyweight.fitness";

    public AuthenticationProvider(String accountId, String identityPoolId, Regions region) {
        super(accountId, identityPoolId, region);
    }

    @Override
    public String getProviderName() {
        return developerProvider;
    }

    public void login(String identityId, String token) {
        update(identityId, token);
    }
}