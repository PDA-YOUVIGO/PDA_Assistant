/*
 * Copyright (c) 2020. komamj
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.youvigo.wms.outstock;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.youvigo.wms.R;
import com.youvigo.wms.base.BaseActivity;
import com.youvigo.wms.data.backend.RetrofitClient;
import com.youvigo.wms.data.backend.api.BackendApi;
import com.youvigo.wms.data.dto.base.ApiResponse;
import com.youvigo.wms.data.dto.response.MoveType;
import com.youvigo.wms.search.SearchActivity;
import com.youvigo.wms.util.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * 有预留出库页面
 */
public class ReservedOutBoundActivity extends BaseActivity {
    public static final String ORDER_NUMBER = "order_number";

    private EditText orderNumber, remark;

    private ProgressBar progressBar;

    private Spinner moveType;
    private TextView internalOrder;
    private TextView employer;
    private TextView department;
    private TextView placeOfReceipt;

    private ReservedOutBoundAdapter adapter;

    private List<MoveType> moveTypes = new ArrayList<>();
    private ArrayAdapter<MoveType> moveTypeAdapter;

    private ReservedOutBoundViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();

        loadMoveType();

        observeData();
    }

    private void initViews() {
        orderNumber = findViewById(R.id.tv_document_number_description);
        moveType = findViewById(R.id.sp_move_type_description);
        internalOrder = findViewById(R.id.tv_internal_order_description);
        employer = findViewById(R.id.tv_employer_description);
        department = findViewById(R.id.tv_receiving_department_description);
        placeOfReceipt = findViewById(R.id.sp_place_of_receipt_description);
        remark = findViewById(R.id.et_remark);

        progressBar = findViewById(R.id.progress_bar);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReservedOutBoundAdapter();
        recyclerView.setAdapter(adapter);

        moveType.setEnabled(false);

        moveTypeAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.item_spinner, moveTypes);
        moveType.setAdapter(moveTypeAdapter);
        moveType.setSelection(-1, true);
    }

    /**
     * 观察数据变化
     */
    private void observeData() {
        viewModel = new ViewModelProvider.NewInstanceFactory().create(ReservedOutBoundViewModel.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.d("onActivityResult");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.out_of_stock_activity;
    }

    @Override
    protected void onMenuSearchClicked() {

        String number = orderNumber.getText().toString();

        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(Constants.CATEGORY, Constants.TYPE_RESERVED_OUT_BOUND);
        intent.putExtra(ORDER_NUMBER, number);
        startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * 加载库存地
     */
    private void loadMoveType() {
        RetrofitClient retrofitClient = RetrofitClient.getInstance();
        BackendApi backendApi = retrofitClient.getBackendApi();

        Call<ApiResponse<List<MoveType>>> call = backendApi.getMoveTypes(retrofitClient.getFactoryCode());
        call.enqueue(new Callback<ApiResponse<List<MoveType>>>() {
            @Override
            public void onResponse(@NotNull Call<ApiResponse<List<MoveType>>> call, @NotNull Response<ApiResponse<List<MoveType>>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<List<MoveType>> apiResponse = response.body();
                    moveTypes.addAll(apiResponse.getData());
                    moveTypeAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ApiResponse<List<MoveType>>> call, @NotNull Throwable t) {

            }
        });
    }
}
