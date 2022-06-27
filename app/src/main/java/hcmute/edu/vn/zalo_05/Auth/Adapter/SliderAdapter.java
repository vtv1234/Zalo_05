package hcmute.edu.vn.zalo_05.Auth.Adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import hcmute.edu.vn.zalo_05.R;


public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;

    }

//    list of images in slide
    public int[] slide_images = {
            -1,
            R.drawable.video_call_slide,
            R.drawable.group_chat_slide,
            R.drawable.feature_slide
    };

//    headings on each slide
    public String[] slide_heading = {
        "",
        "Gọi video ổn định",
        "Chat nhóm tiện ích",
        "Gửi ảnh nhanh chóng"
    };

//    slide description on each slide
    public String[] slide_descs = {
        "",
        "Trò chuyện thật đã với chất lượng video ổn định mọi lúc, mọi nơi",
        "Nơi cùng nhau trao đổi, giữ liên lạc với gia đình, bạn bè, đồng nghiệp...",
        "Trao đổi hình ảnh chất lượng cao với bạn bè và người thân thật nhanh và dễ dàng"
    };

    @Override
    public int getCount() {
        return slide_heading.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        this.layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);
        //map data to view elements
        ImageView slideImageView = view.findViewById(R.id.slide_image);
        TextView slideAppHeader = view.findViewById(R.id.slide_app_header);
        TextView slideFeatureHeader = view.findViewById(R.id.slide_featuer_header);
        TextView slideDescription = view.findViewById(R.id.slide_description);

        if(slide_images[position] != -1) {
            slideImageView.setImageResource(slide_images[position]);
        }
        else {
            slideImageView.setVisibility(View.GONE);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0, (float) 0.6);
            slideAppHeader.setLayoutParams(lp);
            slideAppHeader.setTextSize(100);
            slideAppHeader.setGravity(Gravity.CENTER);
        }
        if(slide_heading[position] != "") {
            slideFeatureHeader.setText(slide_heading[position]);
            slideDescription.setText(slide_descs[position]);
        }
        else {
            slideFeatureHeader.setVisibility(View.GONE);
            slideDescription.setVisibility(View.GONE);
        }
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
