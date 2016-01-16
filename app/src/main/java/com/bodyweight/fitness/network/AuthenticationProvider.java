package com.bodyweight.fitness.network;

import com.amazonaws.auth.AWSAbstractCognitoDeveloperIdentityProvider;
import com.amazonaws.regions.Regions;
import com.bodyweight.fitness.utils.Logger;

public class AuthenticationProvider extends AWSAbstractCognitoDeveloperIdentityProvider {
    private static final String developerProvider = "com.bodyweight.fitness";

    public AuthenticationProvider(String accountId, String identityPoolId, Regions region) {
        super(accountId, identityPoolId, region);
    }

    @Override
    public String getProviderName() {
        return developerProvider;
    }

    @Override
    public String refresh() {
        Logger.e("Called refresh in AuthenticationProvider");

        return super.refresh();
    }

    public void login(String identityId, String token) {
        update(identityId, token);
    }
}