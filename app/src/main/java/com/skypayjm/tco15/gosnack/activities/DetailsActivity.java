package com.skypayjm.tco15.gosnack.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.transition.Transition;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.skypayjm.tco15.gosnack.R;
import com.skypayjm.tco15.gosnack.models.Snack;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class DetailsActivity extends AppCompatActivity {
    public static final String EXTRA_SNACK = "EXTRA_SNACK";
    private Snack mSnack;
    private ImageView ivProfile;
    private TextView tvName;
    private TextView tvPhone;
    private View vPalette;
    private FloatingActionButton fab;
    private View snackBar;
    private Transition.TransitionListener mEnterTransitionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        // Enable up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ivProfile = (ImageView) findViewById(R.id.ivProfile);
        tvName = (TextView) findViewById(R.id.tvName);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        vPalette = findViewById(R.id.vPalette);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        snackBar = findViewById(R.id.snackBar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel:" + mSnack.getNumber();
//                Intent intent = new Intent(Intent.ACTION_DIAL);
//                intent.setData(Uri.parse(uri));
//                startActivity(intent);
                Snackbar.make(snackBar, uri, Snackbar.LENGTH_LONG).show();
            }
        });
        fab.setVisibility(View.INVISIBLE);

        // Extract snack from bundle
        mSnack = (Snack) getIntent().getExtras().getSerializable(EXTRA_SNACK);

        // Fill views with data
//        Picasso.with(DetailsActivity.this).load(mSnack.getThumbnailDrawable()).into(ivProfile);
        tvName.setText(mSnack.getName());
        tvPhone.setText(mSnack.getNumber());
        //Define Listener for image loading
        Target target = new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                // TODO 1. Insert the bitmap into the profile image view
                ivProfile.setImageBitmap(bitmap);


                // TODO 2. Use generateAsync() method from the Palette API to get the vibrant color from the bitmap
                //      Set the result as the background color for `R.id.vPalette` view containing the snack's name.
                Palette.from(bitmap).maximumColorCount(16).generate(new Palette.PaletteAsyncListener() {

                    @Override
                    public void onGenerated(Palette palette) {
                        // Get the "vibrant" color swatch based on the bitmap
                        Palette.Swatch vibrant = palette.getVibrantSwatch();
                        if (vibrant != null) {
                            // Set the background color of a layout based on the vibrant color
                            vPalette.setBackgroundColor(vibrant.getRgb());
                            // Update the title TextView with the proper text color
                            tvName.setTextColor(vibrant.getTitleTextColor());
                            tvPhone.setTextColor(vibrant.getTitleTextColor());
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
        ivProfile.setTag(target);
        Picasso.with(this).load(mSnack.getThumbnailDrawable()).into(target);
        mEnterTransitionListener = new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                enterReveal();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        };
        getWindow().getEnterTransition().addListener(mEnterTransitionListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                exitReveal();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void enterReveal() {
        // previously invisible view
        final View myView = findViewById(R.id.fab);

        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(myView.getWidth(), myView.getHeight()) / 2;

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);

        // make the view visible and start the animation
        myView.setVisibility(View.VISIBLE);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                getWindow().getEnterTransition().removeListener(mEnterTransitionListener);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    void exitReveal() {
        // previously visible view
        final View myView = findViewById(R.id.fab);

        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the initial radius for the clipping circle
        int initialRadius = myView.getWidth() / 2;

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                myView.setVisibility(View.INVISIBLE);
                // Finish the activity after the exit transition completes.
                supportFinishAfterTransition();
            }
        });

        // start the animation
        anim.start();
    }

    @Override
    public void onBackPressed() {
        exitReveal();
    }
}
