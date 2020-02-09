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

package com.youvigo.wms.shelving;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.youvigo.wms.R;
import com.youvigo.wms.data.entities.Shelving;

import org.jetbrains.annotations.NotNull;

public class ShelvingDetailDialogFragment extends DialogFragment {
    private static final String TAG = "ShelvingDetailDialogFragment";
    private static final String KEY_SHELVING = "key_shelving";

    // 物料编码
    private TextView materialCoding;
    // 物料名称
    private TextView materialName;
    // 批次号
    private TextView batchNumber;
    // 规格
    private TextView specification;
    // 供应商批次
    private TextView supplierBatch;
    // 货位码
    private TextView cargoCode;
    // 未上架
    private TextView notOnShelvesQuantity, notOnShelvesUnit;
    // 已上架
    private TextView onShelveQuantity, OnShelvesUnit;
    // 当前上架
    private EditText onShelvesMainQuantity, onShelvesAuxiliayQuantity;

    // 单位
    private Spinner mainUnit, auxiliaryUnit;

    private Context context;

    private Shelving shelving;

    /**
     * 展示详情页面
     *
     * @param shelving 详情数据
     */
    public static void show(FragmentManager fragmentManager, Shelving shelving) {
        ShelvingDetailDialogFragment dialogFragment = new ShelvingDetailDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_SHELVING, shelving);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(fragmentManager, TAG);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, R.style.DetailDialogTheme);

        if (getArguments() != null) {
            shelving = getArguments().getParcelable(KEY_SHELVING);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(context).inflate(R.layout.sheling_detail_dialog_fragment, null);

        initViews(view);

        return buildDialog(view);
    }

    @NotNull
    private AlertDialog buildDialog(View view) {
        return new AlertDialog.Builder(context)
                .setTitle(R.string.detail)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog1, int which) {
                        // 点击取消时回调
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog1, int which) {
                        // 点击确定时回调

                    }
                })
                .setView(view)
                .create();
    }

    private void initViews(View view) {
        materialCoding = view.findViewById(R.id.tv_material_coding_description);
        materialName = view.findViewById(R.id.tv_material_name_description);
        batchNumber = view.findViewById(R.id.tv_batch_number_description);
        specification = view.findViewById(R.id.tv_specification_description);
        supplierBatch = view.findViewById(R.id.tv_supplier_batch_description);
        cargoCode = view.findViewById(R.id.tv_cargo_code_description);
        notOnShelvesQuantity = view.findViewById(R.id.tv_not_on_shelves_quantity);
        notOnShelvesUnit = view.findViewById(R.id.tv_not_on_shelves_unit);
        onShelveQuantity = view.findViewById(R.id.tv_on_shelve_quantity);
        OnShelvesUnit = view.findViewById(R.id.tv_on_shelves_unit);
        onShelvesMainQuantity = view.findViewById(R.id.tv_on_shelves_main_quantity);
        mainUnit = view.findViewById(R.id.sp_main_unit);
        onShelvesAuxiliayQuantity = view.findViewById(R.id.tv_on_shelves_auxiliay_quantity);
        auxiliaryUnit = view.findViewById(R.id.sp_auxiliary_unit);

        materialCoding.setText(shelving.getMaterialNumber());
        materialName.setText(shelving.getMaterialDescription());
        batchNumber.setText(shelving.getBatchNumber());
        specification.setText(shelving.getSpecifications());
        supplierBatch.setText(shelving.getSupplierBatchNumber());
        notOnShelvesUnit.setText(shelving.getBaseUnitTxt());
        OnShelvesUnit.setText(shelving.getBaseUnitTxt());
        notOnShelvesQuantity.setText(String.valueOf(shelving.getBasicQuantity()));

    }
}
