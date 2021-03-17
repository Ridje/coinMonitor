package com.kis.coinmonitor.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kis.coinmonitor.R;
import com.kis.coinmonitor.model.websocketAPI.Prices;
import com.kis.coinmonitor.network.APIConnector;
import com.kis.coinmonitor.model.standardAPI.Asset;
import com.kis.coinmonitor.model.standardAPI.Assets;
import com.kis.coinmonitor.network.AssetWebSocketListener;
import com.kis.coinmonitor.network.OnMessageAccepted;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssetsListFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    APIConnector connector = APIConnector.getAPIConnector();
    List<Asset> listOfAssets = new ArrayList<>();
    private Integer currentOffset = 0;
    private final Integer LIMIT_PER_DOWNLOAD = 25;
    private List<Prices> cacheList = new ArrayList<>();
    private Thread getAssetsThread;
    String assetsKeysList;
    Request request;
    OkHttpClient httpClient;
    WebSocket websocketPrices;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    volatile boolean isLoading = false;

    public AssetsListFragment() {
    }

    public static AssetsListFragment newInstance() {
        AssetsListFragment fragment = new AssetsListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            currentOffset = savedInstanceState.getInt("currentOffset");
            if (savedInstanceState.getParcelableArrayList("assets") != null)
                listOfAssets = savedInstanceState.getParcelableArrayList("assets");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!isLoading) {
            outState.putInt("currentOffset", currentOffset);
            outState.putParcelableArrayList("assets", (ArrayList<Asset>) recyclerViewAdapter.mItemList);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_assets_list, container, false);

        return inflatedView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.assets_list_recycler_view);
        initAdapter();
        initScrollListener();
        httpClient = new OkHttpClient();
        if (listOfAssets.isEmpty()) {
            getAssetsThread = new Thread(() -> loadMore());
            getAssetsThread.start();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (cacheList.isEmpty()) {
                        continue;
                    }
                    Prices processedPrices = cacheList.get(cacheList.size() - 1);
                    for (Map.Entry<String, String> entry: processedPrices.getPrices().entrySet()) {
                        for (int i = 0; i < listOfAssets.size() - 1; i++) {
                            if (listOfAssets.get(i).getId().equals(entry.getKey())) {
                                listOfAssets.get(i).setPriceUsd(new BigDecimal(entry.getValue()));
                                final int position = i;
                                requireActivity().runOnUiThread(() -> recyclerViewAdapter.notifyItemChanged(position));
                            }
                        }
                    }

                    cacheList.clear();
                }
            }
        }).start();
    }


    private void initAdapter() {
        recyclerViewAdapter = new RecyclerViewAdapter(listOfAssets);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == recyclerViewAdapter.getItemCount() - 1) {
                        Thread getAssetsThread = new Thread(() -> loadMore());
                        getAssetsThread.start();
                    }
                }
            }
        });
    }

    private synchronized void loadMore() {
        isLoading = true;
        recyclerViewAdapter.addLoadingBar();
        getActivity().runOnUiThread(() -> recyclerViewAdapter.notifyDataSetChanged());
        connector.getAssets(null, null, LIMIT_PER_DOWNLOAD, currentOffset, new Callback<Assets>() {
            @Override
            public void onResponse(Call<Assets> call, Response<Assets> response) {
                if (!isAdded())  {
                    isLoading = false;
                    return;
                }
                recyclerViewAdapter.removeLoadingBar();
                requireActivity().runOnUiThread(() -> recyclerViewAdapter.notifyDataSetChanged());
                recyclerViewAdapter.addAssets(response.body().getData());
                currentOffset += LIMIT_PER_DOWNLOAD;
                isLoading = false;
                assetsKeysList = String.join(",", listOfAssets.stream().map(asset -> asset.getId()).collect(Collectors.toList()));
                request = new Request.Builder().url("wss://ws.coincap.io/prices?assets=" + assetsKeysList).build();
                if (websocketPrices != null) {
                    websocketPrices.close(1000, "recreating");
                    websocketPrices = null;
                }
                websocketPrices = httpClient.newWebSocket(request, new AssetWebSocketListener(new OnMessageAccepted() {
                    @Override
                    public void onResponce(String textResponse) {
                        try {
                            cacheList.add(new ObjectMapper()
                                    .readerFor(Prices.class)
                                    .readValue(textResponse));

                        } catch (JsonProcessingException e) {
                            onFailure();
                        }
                    }

                    @Override
                    public void onFailure() {

                    }
                }));
            }

            @Override
            public void onFailure(Call<Assets> call, Throwable t) {
                if (!isAdded()) {
                    isLoading = false;
                    return;
                }
                recyclerViewAdapter.removeLoadingBar();
                requireActivity().runOnUiThread(() -> recyclerViewAdapter.notifyDataSetChanged());
                isLoading = false;
            }
        });
    }

}