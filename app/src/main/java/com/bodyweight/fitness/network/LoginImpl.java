package com.bodyweight.fitness.network;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.auth.IdentityChangedListener;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.Record;
import com.amazonaws.mobileconnectors.cognito.SyncConflict;
import com.amazonaws.mobileconnectors.cognito.exceptions.DataStorageException;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Regions;

import com.bodyweight.fitness.App;
import com.bodyweight.fitness.BuildConfig;
import com.bodyweight.fitness.utils.Logger;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * TODO: Logout.
 * TODO: Get new token while logged in.
 * TODO: Restart the app and keep user logged in.
 *
 * TODO: Sync data.
 */
public class LoginImpl implements IdentityChangedListener {
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

        DateTime dateTime = new DateTime(
                mCognitoProvider.getSessionCredentitalsExpiration()
        ).toDateTime(DateTimeZone.UTC);

        Logger.d("identityId: " + mCognitoProvider.getIdentityId());
        Logger.d("cachedIdentityId: " + mCognitoProvider.getCachedIdentityId());
        Logger.d("sessionCredentialsExpiration: " + dateTime.toString());
        Logger.d("refreshThreshold: " + mCognitoProvider.getRefreshThreshold());

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
        mCognitoProvider.registerIdentityChangedListener(this);
    }

    public LoginResponse invokeLogin(LoginRequest loginRequest) {
        return mLoginInterface.LambdAuthLogin(loginRequest);
    }

    public void isLoggedIn() {

    }

    public void login(String email, String password, String identityId, String token) {
        Logger.d("Email: " + email + "\nidentityId:" + identityId + "\ntoken:" + token);

        mAuthenticationProvider.login(identityId, token);

        HashMap<String, String> logins = new HashMap<>();

        logins.put("com.bodyweight.fitness", token);
        logins.put("cognito-identity.amazonaws.com", token);

        mCognitoProvider.setLogins(logins);

        /**
         * Session credentials expiration to 1 year.
         *
         * TODO: Is this right to do? Better than storing passwords and authenticating.
         */
        mCognitoProvider.setSessionCredentialsExpiration(
                new DateTime().plusYears(1).toDate()
        );

        /**
         * It should publish an object with email, identityId and token(?).
         * First Name, Last Name as well (or Username).
         */
        mAuthSubject.onNext(email);
    }

    public void logout() {
        mCognitoProvider.clear();
        mCognitoProvider.clearCredentials();

        mAuthSubject.onNext("null");
    }

    public Observable<String> getAuthSubject() {
        return mAuthSubject.asObservable();
    }

    @Override
    public void identityChanged(String oldIdentityId, String newIdentityId) {
        Logger.e("identityChanged: " + newIdentityId);
    }

    public void syncData() {
        mCognitoProvider.setSessionCredentialsExpiration(
                new DateTime().plusYears(1).toDate()
        );

        DateTime dateTime = new DateTime(
                mCognitoProvider.getSessionCredentitalsExpiration()
        ).toDateTime(DateTimeZone.UTC);

        Logger.d("identityId: " + mCognitoProvider.getIdentityId());
        Logger.d("cachedIdentityId: " + mCognitoProvider.getCachedIdentityId());
        Logger.d("sessionCredentialsExpiration: " + dateTime.toString());
        Logger.d("refreshThreshold: " + mCognitoProvider.getRefreshThreshold());






        Dataset dataset = mCognitoSyncManager.openOrCreateDataset("datasetname");
        dataset.put("myKey", "my test");
        dataset.put("date", new DateTime().toString());
        dataset.synchronize(new Dataset.SyncCallback() {
            @Override
            public void onSuccess(Dataset dataset, List<Record> updatedRecords) {
                Logger.d("Sync success.");
            }

            @Override
            public boolean onConflict(Dataset dataset, List<SyncConflict> conflicts) {
                Logger.d("Sync conflict.");
                return false;
            }

            @Override
            public boolean onDatasetDeleted(Dataset dataset, String datasetName) {
                Logger.d("Sync deleted.");
                return false;
            }

            @Override
            public boolean onDatasetsMerged(Dataset dataset, List<String> datasetNames) {
                Logger.d("Sync merged.");
                return false;
            }

            @Override
            public void onFailure(DataStorageException dse) {
                Logger.d(dse.getMessage());
                Logger.d("Sync failure.");
            }
        });
    }
}
