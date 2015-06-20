package com.skypayjm.tco15.gosnack.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.melnykov.fab.FloatingActionButton;
import com.skypayjm.tco15.gosnack.R;
import com.skypayjm.tco15.gosnack.models.Snack;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;
import java.util.Map;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class DetailsActivity extends AppCompatActivity {
    public static final String EXTRA_SNACK = "EXTRA_SNACK";
    private Snack mSnack;
    private ImageView ivProfile;
    private Toolbar mToolbar;
    private TextView tvName;
    private TextView tvPhone;
    private View vPalette;
    private FloatingActionButton fab;
    private View snackBar;
    private Transition.TransitionListener mEnterTransitionListener;
    private Firebase firebaseRef;
    /* A dialog that is presented until the Firebase authentication finished. */
    private ProgressDialog mAuthProgressDialog;
    /* *************************************
    *              TWITTER                *
    ***************************************/
    public static final int RC_TWITTER_LOGIN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        if (null != mToolbar) {
            mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

            mToolbar.setTitle(R.string.title_activity_settings);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    exitReveal();
//                    NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                }
            });
        }
        ivProfile = (ImageView) findViewById(R.id.ivProfile);
        tvName = (TextView) findViewById(R.id.tvName);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        vPalette = findViewById(R.id.vPalette);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        snackBar = findViewById(R.id.snackBar);

        firebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
        fab.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       String uri = mSnack.getNumber();
                                       Snackbar.make(snackBar, uri, Snackbar.LENGTH_LONG).show();
                                       if (loggedIntoTwitter()) {
                                           new AsyncTask<Void, Void, Void>() {

                                               @Override
                                               protected Void doInBackground(Void... params) {
                                                   publishTweet();
                                                   return null;
                                               }

                                           }.execute();
                                       } else {
                                           // Do processing after authentication failure
                                           loginWithTwitter();
                                       }
                                       // Check if already logged into Twitter.
//                if (loggedIntoTwitter()) {
//                    // If yes, proceed to show user the window to publish the tweet
//                    publishTweet();
//
//                } else {
//                    loginWithTwitter();
//                }
                                       // If no, proceed to get user to login.
                                   }
                               }

        );
        fab.setVisibility(View.INVISIBLE);

        // Extract snack from bundle
        mSnack = (Snack)

                getIntent()

                        .

                                getExtras()

                        .

                                getSerializable(EXTRA_SNACK);

        // Fill views with data
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
        Picasso.with(this).

                load(mSnack.getThumbnailDrawable()

                ).

                into(target);

        mEnterTransitionListener = new Transition.TransitionListener()

        {
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
        }

        ;

        getWindow()

                .

                        getEnterTransition()

                .

                        addListener(mEnterTransitionListener);
        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAuthProgressDialog = new

                ProgressDialog(this);

        mAuthProgressDialog.setTitle("Loading");
        mAuthProgressDialog.setMessage("Authenticating with Firebase...");
        mAuthProgressDialog.setCancelable(false);
    }

    private void publishTweet() {
        Snackbar.make(findViewById(R.id.snackBar), "Publishing tweet", Snackbar.LENGTH_LONG).show();
        ConfigurationBuilder cb = new ConfigurationBuilder();

        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(getResources().getString(R.string.twitter_consumer_key))
                .setOAuthConsumerSecret(getResources().getString(R.string.twitter_consumer_secret))
                .setOAuthAccessToken(getResources().getString(R.string.twitter_access_token))
                .setOAuthAccessTokenSecret(getResources().getString(R.string.twitter_access_token_secret));
        try {
            TwitterFactory factory = new TwitterFactory(cb.build());
            Twitter twitter = factory.getInstance();

            String latestStatus = "@Stadium, snacking on awesome food #vendor. All the way #Team !!";
            Status status = twitter.updateStatus(latestStatus);
            Snackbar.make(findViewById(R.id.snackBar), "Tweet successfully", Snackbar.LENGTH_LONG);
        } catch (TwitterException te) {
            te.printStackTrace();
        }
    }

    private boolean loggedIntoTwitter() {
        AuthData authData = firebaseRef.getAuth();
        return authData.getProvider().equals("twitter");
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
        Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);

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

    /* ************************************
     *               TWITTER              *
     **************************************
     */
    private void loginWithTwitter() {
        startActivityForResult(new Intent(this, TwitterOAuthActivity.class), RC_TWITTER_LOGIN);
    }

    /**
     * This method will attempt to authenticate a user to firebase given an oauth_token (and other
     * necessary parameters depending on the provider)
     */
    private void authWithFirebase(final String provider, Map<String, String> options) {
        if (options.containsKey("error")) {
            showErrorDialog(options.get("error"));
        } else {
            mAuthProgressDialog.show();
            if (provider.equals("twitter")) {
                // if the provider is twitter, we pust pass in additional options, so use the options endpoint
                firebaseRef.authWithOAuthToken(provider, options, new Firebase.AuthResultHandler() {

                    @Override
                    public void onAuthenticated(AuthData authData) {
                        mAuthProgressDialog.dismiss();
                        new AsyncTask<Void, Void, Void>() {

                            protected Void doInBackground(Void... args) {
                                publishTweet();
                                return null;
                            }

                        }.execute();
//                        publishTweet();
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        mAuthProgressDialog.dismiss();
                        showErrorDialog(firebaseError.toString());
                    }
                });
            }
        }
    }

    /**
     * Show errors to users
     */
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Map<String, String> options = new HashMap<String, String>();
        if (requestCode == RC_TWITTER_LOGIN) {
            options.put("oauth_token", data.getStringExtra("oauth_token"));
            options.put("oauth_token_secret", data.getStringExtra("oauth_token_secret"));
            options.put("user_id", data.getStringExtra("user_id"));
            authWithFirebase("twitter", options);
        }
    }
}
