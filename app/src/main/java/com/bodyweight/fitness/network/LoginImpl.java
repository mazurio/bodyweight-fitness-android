package com.bodyweight.fitness.network;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Regions;

import com.bodyweight.fitness.App;
import com.bodyweight.fitness.BuildConfig;

import java.util.HashMap;

import rx.Observable;
import rx.subjects.PublishSubject;

public class LoginImpl {
    private static LoginImpl sInstance;

    public static LoginImpl getInstance() {
        if(sInstance == null) {
            sInstance = new LoginImpl();
        }

        return sInstance;
    }

    private AuthenticationProvider mAuthenticationProvider;
    private CognitoCachingCredentialsProvider mCognitoProvider;
    private CognitoSyncManager mCognitoSyncManager;
    private LambdaInvokerFactory mLambdaInvoker;

    private LoginInterface mLoginInterface;

    private static final Regions REGION = Regions.EU_WEST_1;

    private final PublishSubject<String> mAuthSubject = PublishSubject.create();

    /**
     * TODO: At the moment cognito is set to "Unathorized" as well, we should disable it to
     * TODO: prevent users from creating multiple cognito-id's.
     */
    private LoginImpl() {
        mAuthenticationProvider = new AuthenticationProvider(
                null,
                BuildConfig.IDENTITY_POOL_ID,
                REGION
        );

        mCognitoProvider = new CognitoCachingCredentialsProvider(
                App.getContext(),
                mAuthenticationProvider,
                REGION
        );

        mCognitoSyncManager = new CognitoSyncManager(
                App.getContext(),
                REGION,
                mCognitoProvider
        );

        mLambdaInvoker = new LambdaInvokerFactory(
                App.getContext(),
                REGION,
                mCognitoProvider
        );

        mLoginInterface = mLambdaInvoker.build(LoginInterface.class);
    }

    public LoginResponse invokeLogin(LoginRequest loginRequest) {
        return mLoginInterface.LambdAuthLogin(loginRequest);
    }

    public void login(String email, String identityId, String token) {
        mAuthenticationProvider.login(identityId, token);

        HashMap<String, String> logins = new HashMap<>();

        logins.put("com.bodyweight.fitness", token);
        logins.put("cognito-identity.amazonaws.com", token);

        mCognitoProvider.setLogins(logins);

        /**
         * It should publish an object with email, identityId and token(?).
         * First Name, Last Name as well (or Username).
         */
        mAuthSubject.onNext(email);
    }

    public Observable<String> getAuthSubject() {
        return mAuthSubject.asObservable();
    }

    /**
     * For later
     */

//    Dataset dataset = client.openOrCreateDataset("datasetname");
//    dataset.put("myKey", "my value");
//    dataset.synchronize(new Dataset.SyncCallback() {
//        @Override
//        public void onSuccess(Dataset dataset, List< Record > updatedRecords) {
//            print("success");
//        }
//
//        @Override
//        public boolean onConflict(Dataset dataset, List< SyncConflict > conflicts) {
//            print("conflict");
//            return false;
//        }
//
//        @Override
//        public boolean onDatasetDeleted(Dataset dataset, String datasetName) {
//            print("data-set-deleted");
//            return false;
//        }
//
//        @Override
//        public boolean onDatasetsMerged(Dataset dataset, List<String> datasetNames) {
//            print("data-set-merged");
//            return false;
//        }
//
//        @Override
//        public void onFailure(DataStorageException dse) {
//            Logger.d(dse.getMessage());
//            print("data-set-failure");
//        }
//    });
}
