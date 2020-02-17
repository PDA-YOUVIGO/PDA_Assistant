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

package com.youvigo.wms.data.backend.api;

import com.youvigo.wms.data.dto.request.FinishedProductsQueryRequest;
import com.youvigo.wms.data.dto.request.MaterialQueryRequest;
import com.youvigo.wms.data.dto.request.OnShevingRequest;
import com.youvigo.wms.data.dto.request.ShelvingQueryRequest;
import com.youvigo.wms.data.dto.response.FinishedProductsQueryResponse;
import com.youvigo.wms.data.dto.response.MaterialQueryResult;
import com.youvigo.wms.data.dto.response.OnShevlingResponse;
import com.youvigo.wms.data.dto.response.ShelvingResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * SAP接口
 */
public interface SapService {

	/**
	 * 获取SAP入库上架单据
	 */
	@POST("RESTAdapter/PDA/On_The_Shelf_Task_Query_Sender")
	Call<ShelvingResult> queryOnShelvings(@Body ShelvingQueryRequest request);

	/**
	 * 入库上架
	 */
	@POST("RESTAdapter/PDA/On_The_Shelf")
	Call<OnShevlingResponse> submitOnSheving(@Body OnShevingRequest request);

	/**
	 * 获取SAP成品上架单据信息
	 */
	@POST("RESTAdapter/PDA/On_The_Shelf_Task_Query_Sender2")
	Call<FinishedProductsQueryResponse> queryFinishedProducts(@Body FinishedProductsQueryRequest request);

	/**
	 * 获取物料信息
	 */
	@POST("RESTAdapter/PDA/Stock_Query")
	Call<MaterialQueryResult> materialQuery(@Body MaterialQueryRequest request);

}
