package com.semicolon.tadlaly.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.semicolon.tadlaly.Adapters.AdsDetailsPagerAdapter;
import com.semicolon.tadlaly.Models.MyAdsModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AdsDetailsActivity extends AppCompatActivity {
    private ViewPager pager;
    private TabLayout tab;
    private TextView ad_number, ad_cost, ad_date, ad_viewers, ad_shares, ad_distance, ad_title, ad_details, ad_state_new, ad_state_old, city, no_ads, ad_name,city2;
    private AdsDetailsPagerAdapter adapter;
    private List<MyAdsModel.Images> images;
    private LinearLayout distContainer;
    private MyAdsModel myAdsModel;
    private String whoVisit = "";
    private Timer timer;
    private ImageView back, shareBtn, viewerBtn;
    private LinearLayout contactContainer;
    private FrameLayout call_btn, email_btn, whats_btn;
    private final int share_req=968;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_details);
        timer = new Timer();
        initView();
        getDataFromIntent();
    }



    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            myAdsModel = (MyAdsModel) intent.getSerializableExtra("ad_details");
            whoVisit = intent.getStringExtra("whoVisit");

            UpdateUi(myAdsModel, whoVisit);
            Log.e("ads_id",myAdsModel.getId_advertisement());
            if (whoVisit.equals(Tags.visitor))
            {
                UpdateViewers(myAdsModel.getId_advertisement());

            }


        }
    }
    private void UpdateViewers(String id_advertisement) {
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<MyAdsModel> call = retrofit.create(Services.class).IncreaseShare_Viewers(id_advertisement, Tags.view);
        call.enqueue(new Callback<MyAdsModel>() {
            @Override
            public void onResponse(Call<MyAdsModel> call, Response<MyAdsModel> response) {
                if (response.isSuccessful())
                {
                    if (response.body().getSuccess()==1)
                    {
                        ad_viewers.setText(response.body().getView_count());
                    }else if (response.body().getSuccess()==0)
                    {
                        Log.e("Error","Error");
                    }
                }
            }

            @Override
            public void onFailure(Call<MyAdsModel> call, Throwable t) {
                Log.e("Error",t.getMessage());

            }
        });
    }
    private void initView() {
        images = new ArrayList<>();
        pager = findViewById(R.id.pager);
        tab = findViewById(R.id.tab);
        back = findViewById(R.id.back);
        shareBtn = findViewById(R.id.shareBtn);
        viewerBtn = findViewById(R.id.viewerBtn);
        no_ads = findViewById(R.id.no_ads);
        distContainer = findViewById(R.id.distContainer);
        ad_name = findViewById(R.id.ad_name);
        ad_number = findViewById(R.id.ad_number);
        ad_cost = findViewById(R.id.ad_cost);
        ad_date = findViewById(R.id.ad_date);
        ad_viewers = findViewById(R.id.viewers);
        ad_shares = findViewById(R.id.shares);
        ad_distance = findViewById(R.id.distance);
        ad_title = findViewById(R.id.ad_title);
        ad_details = findViewById(R.id.ad_details);
        ad_state_new = findViewById(R.id.state_new);
        ad_state_old = findViewById(R.id.state_old);
        contactContainer = findViewById(R.id.contactContainer);
        call_btn = findViewById(R.id.call_btn);
        email_btn = findViewById(R.id.email_btn);
        whats_btn = findViewById(R.id.whatsapp_btn);
        city = findViewById(R.id.city);
        city2 = findViewById(R.id.city2);

        tab.setupWithViewPager(pager);
        adapter = new AdsDetailsPagerAdapter(images, this);
        pager.setAdapter(adapter);
        back.setOnClickListener(view -> finish());

        if (checkWhatsAppFounded())
        {
            whats_btn.setVisibility(View.VISIBLE);
        }else
            {
                whats_btn.setVisibility(View.INVISIBLE);

            }

        call_btn.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + myAdsModel.getPhone()));
            if (ActivityCompat.checkSelfPermission(AdsDetailsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivity(intent);
        });
        email_btn.setOnClickListener(view -> {
            //send Message
        });

        whats_btn.setOnClickListener(view -> {
            Uri uri = Uri.parse("smsto:"+myAdsModel.getPhone());
            Intent intent = new Intent(Intent.ACTION_SENDTO,uri);
            intent.setPackage("com.whatsapp");
            startActivity(intent);
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Share();
            }
        });
    }

    private void Share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,"تطبيق تدللي");
        startActivityForResult(intent,share_req);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==share_req && resultCode==RESULT_OK)
        {
            if (whoVisit.equals(Tags.visitor))
            {
                UpdateShare();

            }
        }
    }

    private void UpdateShare() {
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<MyAdsModel> call = retrofit.create(Services.class).IncreaseShare_Viewers(myAdsModel.getId_advertisement(), Tags.share);
        call.enqueue(new Callback<MyAdsModel>() {
            @Override
            public void onResponse(Call<MyAdsModel> call, Response<MyAdsModel> response) {
                if (response.isSuccessful())
                {
                    if (response.body().getSuccess()==1)
                    {
                        ad_shares.setText(response.body().getShare_count());
                    }else if (response.body().getSuccess()==0)
                    {
                        Log.e("Error","Error");
                    }
                }
            }

            @Override
            public void onFailure(Call<MyAdsModel> call, Throwable t) {
                Log.e("Error",t.getMessage());

            }
        });
    }

    private boolean checkWhatsAppFounded()
    {
        PackageManager packageManager = getPackageManager();
        try {
            packageManager.getPackageInfo("com.whatsapp",PackageManager.GET_ACTIVITIES);
            return true;
        }catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
            return false;
        }
    }
    private void  UpdateUi(MyAdsModel myAdsModel ,String whoVisit)
    {
        Typeface typeface = Typeface.createFromAsset(getAssets(),"OYA-Regular.ttf");
        if (whoVisit.equals(Tags.me_visit))
        {
            distContainer.setVisibility(View.GONE);
            city.setVisibility(View.VISIBLE);
            city.setText(myAdsModel.getCity());
            shareBtn.setEnabled(false);
            viewerBtn.setEnabled(false);
            contactContainer.setVisibility(View.GONE);
            city2.setVisibility(View.INVISIBLE);

        }else
            {
                shareBtn.setEnabled(true);
                viewerBtn.setEnabled(true);
                distContainer.setVisibility(View.VISIBLE);
                city.setVisibility(View.GONE);
                city2.setVisibility(View.VISIBLE);
                city2.setText(myAdsModel.getCity());
                city2.setTypeface(typeface);
                ad_distance.setText(myAdsModel.getDistance()+" "+getString(R.string.km));
                contactContainer.setVisibility(View.VISIBLE);
                city.setVisibility(View.VISIBLE);



            }
            if (myAdsModel.getAdvertisement_image().size()>0)
            {
                no_ads.setVisibility(View.GONE);
            }else
                {
                    no_ads.setVisibility(View.VISIBLE);

                }
        ad_shares.setTypeface(typeface);
        ad_viewers.setTypeface(typeface);
        city.setTypeface(typeface);
        ad_number.setTypeface(typeface);
        ad_cost.setTypeface(typeface);
        ad_date.setTypeface(typeface);
        ad_title.setTypeface(typeface);
        ad_details.setTypeface(typeface);
        ad_name.setTypeface(typeface);
        images.addAll(myAdsModel.getAdvertisement_image());
        adapter.notifyDataSetChanged();
        ad_name.setText(myAdsModel.getAdvertisement_title());
        timer.scheduleAtFixedRate(new TimerClass(),4000,5000);
        ad_number.setText("#"+myAdsModel.getAdvertisement_code());
        ad_cost.setText(myAdsModel.getAdvertisement_price()+" ريال");
        ad_date.setText(myAdsModel.getAdvertisement_date());
        ad_viewers.setText(myAdsModel.getView_count());
        ad_shares.setText(myAdsModel.getShare_count());
        ad_title.setText(myAdsModel.getAdvertisement_title());
        ad_details.setText(myAdsModel.getAdvertisement_content());
        if (myAdsModel.getAdvertisement_type().equals(Tags.ad_new))
        {
            ad_state_new.setVisibility(View.VISIBLE);
            ad_state_old.setVisibility(View.GONE);
        }else
            {
                ad_state_new.setVisibility(View.GONE);
                ad_state_old.setVisibility(View.VISIBLE);
            }

    }
    public class TimerClass extends TimerTask{
        @Override
        public void run() {
            try {
                runOnUiThread(() -> {
                    if (pager.getCurrentItem()<images.size()-1)
                    {
                        pager.setCurrentItem(pager.getCurrentItem()+1);
                    }else
                    {
                        try {
                            pager.setAdapter(adapter);
                            pager.setCurrentItem(0);

                        }catch (NullPointerException e)
                        {

                        }catch (Exception e)
                        { }
                    }
                });
            }catch (NullPointerException e){}
            catch (Exception e){}

        }
    }

    @Override
    protected void onDestroy() {
        if (timer!=null)
        {
            timer.cancel();
        }
        super.onDestroy();

    }
}