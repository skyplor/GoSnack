package com.skypayjm.tco15.gosnack.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.skypayjm.tco15.gosnack.R;
import com.skypayjm.tco15.gosnack.activities.DetailsActivity;
import com.skypayjm.tco15.gosnack.models.Snack;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

// Provide the underlying view for an individual list item.
public class SnacksAdapter extends RecyclerView.Adapter<SnacksAdapter.VH> {
    private Activity mContext;
    private List<Snack> mSnacks;

    public SnacksAdapter(Activity context, List<Snack> snacks) {
        mContext = context;
        if (snacks == null) {
            throw new IllegalArgumentException("snacks must not be null");
        }
        mSnacks = snacks;
    }

    // Inflate the view based on the viewType provided.
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_snack, parent, false);
        return new VH(itemView, mContext);
    }

    // Display data at the specified position
    @Override
    public void onBindViewHolder(final VH holder, int position) {
        Snack snack = mSnacks.get(position);
        holder.rootView.setTag(snack);
        holder.tvName.setText(snack.getName());
//        Picasso.with(mContext).load(snack.getThumbnailDrawable()).into(holder.ivProfile);
        //Define Listener for image loading
        Target target = new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                // TODO 1. Insert the bitmap into the profile image view
                holder.ivProfile.setImageBitmap(bitmap);


                // TODO 2. Use generateAsync() method from the Palette API to get the vibrant color from the bitmap
                //      Set the result as the background color for `R.id.vPalette` view containing the snack's name.
                Palette.from(bitmap).maximumColorCount(16).generate(new Palette.PaletteAsyncListener() {

                    @Override
                    public void onGenerated(Palette palette) {
                        // Get the "vibrant" color swatch based on the bitmap
                        Palette.Swatch vibrant = palette.getVibrantSwatch();
                        if (vibrant != null) {
                            // Set the background color of a layout based on the vibrant color
                            holder.vPalette.setBackgroundColor(vibrant.getRgb());
                            // Update the title TextView with the proper text color
                            holder.tvName.setTextColor(vibrant.getTitleTextColor());
                        }
                    }
                });
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        holder.ivProfile.setTag(target);
        Picasso.with(mContext).load(snack.getThumbnailDrawable()).into(target);
    }

    @Override
    public int getItemCount() {
        return mSnacks.size();
    }

    // Provide a reference to the views for each snack item
    public final static class VH extends RecyclerView.ViewHolder {
        final View rootView;
        final ImageView ivProfile;
        final TextView tvName;
        final View vPalette;

        public VH(View itemView, final Context context) {
            super(itemView);
            rootView = itemView;
            ivProfile = (ImageView) itemView.findViewById(R.id.ivProfile);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            vPalette = itemView.findViewById(R.id.vPalette);

            // Navigate to snack details activity on click of card view.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Snack snack = (Snack) v.getTag();
                    if (snack != null) {
                        // Fire an intent when a snack is selected
                        // Pass snack object in the `bundle and populate details activity.
                        Intent intent = new Intent(context, DetailsActivity.class);
                        intent.putExtra(DetailsActivity.EXTRA_SNACK, snack);
                        Pair<View, String> p1 = Pair.create((View) ivProfile, "profile");
                        Pair<View, String> p2 = Pair.create((View) tvName, "name");
                        Pair<View, String> p3 = Pair.create(vPalette, "palette");
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, p1, p2, p3);
                        context.startActivity(intent, options.toBundle());
                    }
                }
            });
        }
    }
}
