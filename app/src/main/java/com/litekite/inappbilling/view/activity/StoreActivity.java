/*
 * Copyright 2018 LiteKite Startup. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.litekite.inappbilling.view.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.litekite.inappbilling.R;
import com.litekite.inappbilling.databinding.ActivityStoreBinding;
import com.litekite.inappbilling.room.entity.BillingSkuRelatedPurchases;
import com.litekite.inappbilling.view.adapter.StoreAdapter;
import com.litekite.inappbilling.viewmodel.BillingVM;
import com.litekite.inappbilling.viewmodel.ProductsAndPurchasesVM;

import java.util.ArrayList;
import java.util.List;

/**
 * StoreActivity, which displays inApp and subscription products as a list and each products has
 * its own name and price and users buy them by tapping buy button. Purchases made from Google
 * Play Billing Library.
 *
 * @author Vignesh S
 * @version 1.0, 10/03/2018
 * @since 1.0
 */
public class StoreActivity extends BaseActivity {

	private ActivityStoreBinding storeBinding;
	private List<BillingSkuRelatedPurchases> skuProductsAndPurchasesList;
	private StoreAdapter storeAdapter;

	/**
	 * Observes changes and updates of Sku Products and Purchases which is stored in local database.
	 * Updates observed changes to the products list.
	 */
	private Observer<List<BillingSkuRelatedPurchases>> skuProductsAndPurchasesObserver =
			new Observer<List<BillingSkuRelatedPurchases>>() {
				@Override
				public void onChanged(
						@Nullable List<BillingSkuRelatedPurchases> skuRelatedPurchasesList) {
					if (skuRelatedPurchasesList != null && skuRelatedPurchasesList.size() > 0) {
						StoreActivity.this.skuProductsAndPurchasesList.clear();
						StoreActivity.this.skuProductsAndPurchasesList
								.addAll(skuRelatedPurchasesList);
						StoreActivity.this.storeAdapter.notifyDataSetChanged();
					}
				}
			};

	/**
	 * Launches StoreActivity.
	 *
	 * @param context An Activity Context.
	 */
	public static void start(Context context) {
		if (context instanceof AppCompatActivity) {
			Intent intent = new Intent(context, StoreActivity.class);
			context.startActivity(intent);
			startActivityAnimation(context);
		}
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		storeBinding = DataBindingUtil.setContentView(this, R.layout.activity_store);
		init();
	}

	/**
	 * Sets Toolbar.
	 * Initializes Presenter ProductsAndPurchasesViewModel, BillingViewModel and registers
	 * LifeCycle Observers.
	 * Observes Sku Products and Purchases.
	 * Initializes RecyclerView Products List and its adapter.
	 */
	private void init() {
		setToolbar((Toolbar) storeBinding.tbWidget.findViewById(R.id.toolbar),
				true,
				getString(R.string.store),
				(TextView) storeBinding.tbWidget.findViewById(R.id.tv_toolbar_title));
		BillingVM billingVM = ViewModelProviders.of(this).get(BillingVM.class);
		ProductsAndPurchasesVM productsAndPurchasesVM =
				ViewModelProviders.of(this).get(ProductsAndPurchasesVM.class);
		this.getLifecycle().addObserver(productsAndPurchasesVM);
		this.getLifecycle().addObserver(billingVM);
		skuProductsAndPurchasesList = new ArrayList<>();
		storeAdapter = new StoreAdapter(
				this,
				skuProductsAndPurchasesList,
				billingVM.getBillingManager());
		storeBinding.rvStore.setAdapter(storeAdapter);
		productsAndPurchasesVM.getSkuProductsAndPurchasesList()
				.observe(this, skuProductsAndPurchasesObserver);
	}

}
