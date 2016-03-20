package com.bodyweight.fitness.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.amazon.device.associates.AssociatesAPI;
import com.amazon.device.associates.OpenProductPageRequest;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectViews;
import butterknife.OnClick;

import com.bodyweight.fitness.BuildConfig;
import com.bodyweight.fitness.R;

public class BuyEquipmentActivity extends AppCompatActivity {
    @InjectViews({
            R.id.product1,
            R.id.product2,
            R.id.product3,
            R.id.product4,
            R.id.product5})
    List<ImageView> mProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_buy_equipment);

        ButterKnife.inject(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Buy Equipment");
        }

        AssociatesAPI.initialize(
                new AssociatesAPI.Config(BuildConfig.AMAZON_API_KEY, this)
        );

        loadDrawable(R.drawable.product_bands, mProducts.get(0));
        loadDrawable(R.drawable.product_yoga, mProducts.get(1));
        loadDrawable(R.drawable.product_powertower, mProducts.get(2));
        loadDrawable(R.drawable.product_rings, mProducts.get(3));
        loadDrawable(R.drawable.product_irongym, mProducts.get(4));

        if (!BuildConfig.DEBUG) {
            Answers.getInstance().logCustom(
                    new CustomEvent("Buy Equipment")
            );
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home: {
                this.onBackPressed();

                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("unused")
    @OnClick({
            R.id.product1card,
            R.id.product2card,
            R.id.product3card,
            R.id.product4card,
            R.id.product5card
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.product1card:
                openProductPage("B0026PMD70");

                break;

            case R.id.product2card:
                openProductPage("B004TN51EE");

                break;

            case R.id.product3card:
                openProductPage("B002Y2SUU4");

                break;

            case R.id.product4card:
                openProductPage("B0031QCS8C");

                break;

            case R.id.product5card:
                openProductPage("B001EJMS6K");

                break;
        }
    }

    public void loadDrawable(int drawable, ImageView imageView) {
        Picasso.with(this).load(drawable).into(imageView);
    }

    public void openProductPage(String productId) {
        try {
            AssociatesAPI.getLinkService().openRetailPage(
                    new OpenProductPageRequest(productId)
            );
        } catch (Exception e) {}

        if (!BuildConfig.DEBUG) {
            Answers.getInstance().logCustom(
                    new CustomEvent("Open Product Page")
                            .putCustomAttribute("Product Id", productId)
            );
        }
    }
}
