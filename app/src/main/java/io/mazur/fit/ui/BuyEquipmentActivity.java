package io.mazur.fit.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.amazon.device.associates.AssociatesAPI;
import com.amazon.device.associates.OpenProductPageRequest;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import io.mazur.fit.R;
import io.mazur.fit.utils.Logger;

public class BuyEquipmentActivity extends AppCompatActivity {
    @InjectView(R.id.product1) ImageView product1;
    @InjectView(R.id.product2) ImageView product2;
    @InjectView(R.id.product3) ImageView product3;
    @InjectView(R.id.product4) ImageView product4;
    @InjectView(R.id.product5) ImageView product5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_buy_equipment);

        ButterKnife.inject(this);

        getSupportActionBar().setTitle("Buy Equipment");

        AssociatesAPI.initialize(
                new AssociatesAPI.Config("<KEY>", this)
        );


        Picasso.with(this)
                .load(R.drawable.product_bands)
                .into(product1);

        Picasso.with(this)
                .load(R.drawable.product_yoga)
                .into(product2);

        Picasso.with(this)
                .load(R.drawable.product_powertower)
                .into(product3);

        Picasso.with(this)
                .load(R.drawable.product_rings)
                .into(product4);

        Picasso.with(this)
                .load(R.drawable.product_irongym)
                .into(product5);
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

    @OnClick(R.id.product1card)
    @SuppressWarnings("unused")
    public void onClickButton1(View view) {
        openProductPage("B0026PMD70");
    }

    @OnClick(R.id.product2card)
    @SuppressWarnings("unused")
    public void onClickButton2(View view) {
        openProductPage("B004TN51EE");
    }

    @OnClick(R.id.product3card)
    @SuppressWarnings("unused")
    public void onClickButton3(View view) {
        openProductPage("B002Y2SUU4");
    }

    @OnClick(R.id.product4card)
    @SuppressWarnings("unused")
    public void onClickButton4(View view) {
        openProductPage("B0031QCS8C");
    }

    @OnClick(R.id.product5card)
    @SuppressWarnings("unused")
    public void onClickButton5(View view) {
        openProductPage("B001EJMS6K");
    }

    public void openProductPage(String productId) {
        try {
            AssociatesAPI.getLinkService().openRetailPage(
                    new OpenProductPageRequest(productId)
            );
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }
}
