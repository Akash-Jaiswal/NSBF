package com.conduent.hcesdk.core;


import android.content.Context;
import android.util.Log;
import com.conduent.hcesdk.HCECardData;
import com.conduent.hcesdk.ReadCallback;
import com.conduent.hcesdk.ReadParameters;
import com.conduent.hcesdk.entities.valuesapi.ValuesApiResponse;
import com.conduent.hcesdk.network.RetrofitConfig;
import com.conduent.hcesdk.network.ServiceGenerator;
import com.conduent.hcesdk.room.DatabaseQueryAsync;
import com.conduent.hcesdk.room.OnDataBaseQueryListener;
import com.conduent.hcesdk.room.RoomRequestCodes;
import com.conduent.hcesdk.utils.HCEConstant;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HCEEngine implements IHCEEngine {

    private static volatile IHCEEngine instance;
    private Context context;

    private HCEEngine(final Context context) {
        this.context = context;
        new RetrofitConfig.Builder().setBaseUrl(HCEConstant.BASE_URL).setTimeOut(HCEConstant.TIME_OUT).build();
    }

    public static IHCEEngine getInstance(Context context) {
        if (instance == null) {
            synchronized (HCEEngine.class) {
                if (instance == null)
                    instance = new HCEEngine(context);
            }
        }
        return instance;
    }

    static IHCEEngine localInstance() {
        return instance;
    }

    private IHCECore getHCEAccess() {
        return CoreProvider.getInstance().provideHCECoreAccess();
    }

    @Override
    public void startReading(HCECardData hceCardData, ReadCallback callback) {
        getHCEAccess().startReading(hceCardData, callback);
    }

    @Override
    public void startReading(String hceCardData, ReadCallback callback) {
        getHCEAccess().startReading(hceCardData, callback);
    }

    @Override
    public void startReading(ReadParameters params, ReadCallback callback) {
        getHCEAccess().startReading(params, callback);
    }

    @Override
    public void retrieveRemoteOffer() {
        new DatabaseQueryAsync(localInstance().getContext(), RoomRequestCodes.GET_VERSION_FILE, new OnDataBaseQueryListener() {
            @Override
            public void onDataFetched(@NotNull Object singleFile) {
                Log.i("NSBF", "Success");
            }

            @Override
            public void onCountFetched(int count) {
                Log.i("NSBF", "count");
            }
        }).execute();
    }

    @Override
    public void pingMe(ReadCallback callback) {
        //callback.onReadComplete();
        IMappingRule mapRuleAccess = CoreProvider.getInstance().provideMappingRuleAccess();
        ValuesApiResponse valuesApiResponse = mapRuleAccess.provideValuesApiResponse();
        new DatabaseQueryAsync(localInstance().getContext(), RoomRequestCodes.INSERT_VALUES_API_FILE, valuesApiResponse).execute();
    }

    @Override
    public Context getContext() {
        return this.context;
    }

    private void makeRetroCall() {
        new RetrofitConfig.Builder().setBaseUrl(HCEConstant.BASE_URL).setTimeOut(HCEConstant.TIME_OUT).build();
        Call<ValuesApiResponse> call = ServiceGenerator.Instance().getService().getValuesData();
        call.enqueue(new Callback<ValuesApiResponse>() {
            @Override
            public void onResponse(Call<ValuesApiResponse> call, Response<ValuesApiResponse> response) {
                Log.i("NSBF", "Succes");
            }

            @Override
            public void onFailure(Call<ValuesApiResponse> call, Throwable t) {
                Log.i("NSBF", "fail");
            }
        });
    }
}
