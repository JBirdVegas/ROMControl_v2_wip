package com.aokp.romcontrol.widgets;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.aokp.romcontrol.R;
import com.aokp.romcontrol.tasks.XkcdTask;
import com.aokp.romcontrol.tools.SwipeDismissTouchListener;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DeveloperPreference extends LinearLayout implements SwipeDismissTouchListener.DismissCallbacks {
    private static final String TAG = "DeveloperPreference";
    public static final String GRAVATAR_API = "http://www.gravatar.com/avatar/";
    public static int mDefaultAvatarSize = 400;
    private ImageView mTwitterButton;
    private ImageView mDonateButton;
    private ImageView mGithubButton;

    private ImageView mAvatar;
    private TextView mDevName;

    private String mDeveloperMoniker;
    private String mTwitterHandle;
    private String mDonateLink;
    private String mGithubLink;
    private String mEmailLink;
    public static final String TWITTER = "http://twitter.com/#!/";
    private String mMessage;
    private ViewGroup mLayout;

    public DeveloperPreference(Context context) {
        this(context, null);
    }

    public DeveloperPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    public DeveloperPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray typedArray = null;
        try {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.DeveloperPreference);
            mDeveloperMoniker = typedArray.getString(R.styleable.DeveloperPreference_nameDev);
            mTwitterHandle = typedArray.getString(R.styleable.DeveloperPreference_twitterHandle);
            mDonateLink = typedArray.getString(R.styleable.DeveloperPreference_donateLink);
            mGithubLink = typedArray.getString(R.styleable.DeveloperPreference_githubLink);
            mEmailLink = typedArray.getString(R.styleable.DeveloperPreference_emailDev);
            mMessage = typedArray.getString(R.styleable.DeveloperPreference_blurb);
        } finally {
            if (typedArray != null) {
                typedArray.recycle();
            }
        }

        /**
         * Inflate views
         */
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayout = (ViewGroup) inflater.inflate(R.layout.dev_card, this, true);

        mTwitterButton = (ImageView) mLayout.findViewById(R.id.twitter_button);
        mDonateButton = (ImageView) mLayout.findViewById(R.id.donate_button);
        mGithubButton = (ImageView) mLayout.findViewById(R.id.github_button);
        mDevName = (TextView) mLayout.findViewById(R.id.name);
        mAvatar = (ImageView) mLayout.findViewById(R.id.photo);

        /**
         * Setup UI
         */
        mDevName.setText(mDeveloperMoniker);

        if (mDonateLink != null) {
            mDonateButton.setOnClickListener(getClickListener(mDonateLink));
        } else {
            mDonateButton.setVisibility(View.GONE);
        }

        if (mGithubLink != null) {
            mGithubButton.setOnClickListener(getClickListener(mGithubLink));
        } else {
            mGithubButton.setVisibility(View.GONE);
        }

        if (mTwitterHandle != null) {
            // changed to clicking the preference to open twitter
            // it was a hit or miss to click the twitter bird
            findViewById(R.id.photo_text_bar).setOnClickListener(
                    getClickListener(TWITTER + mTwitterHandle));
        } else {
            mTwitterButton.setVisibility(View.INVISIBLE);
        }

        if (mEmailLink != null) {
            UrlImageViewHelper.setUrlDrawable(this.mAvatar,
                    getGravatarUrl(mEmailLink),
                    R.drawable.ic_null,
                    UrlImageViewHelper.CACHE_DURATION_ONE_WEEK);
        } else {
            mAvatar.setVisibility(View.GONE);
        }

        // Only Views that accept onClick are swipable
        // give the Background a click listener stub for
        // increased swipable area
//        View avatar = findViewById(R.id.image_here);
        mLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // Stub!
            }
        });
        SwipeDismissTouchListener swipeDismissTouchListener = new SwipeDismissTouchListener(
                mLayout, null, this);
        swipeDismissTouchListener.setDismissView(false);
        mLayout.setOnTouchListener(
                swipeDismissTouchListener);

//        setOnTouchListener(new SwipeDismissTouchListener(this, null, this));
    }

    private OnClickListener getClickListener(final String url) {
        return new OnClickListener() {
            @Override
            public void onClick(View view) {
                launchWebsite(Uri.parse(url));
            }
        };
    }

    private void launchWebsite(Uri uri) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getContext().startActivity(intent);
    }

    public String getGravatarUrl(String email) {
        try {
            String emailMd5 = getMd5(email.trim().toLowerCase());
            return String.format("%s%s?s=%d&d=mm",
                    GRAVATAR_API,
                    emailMd5,
                    mDefaultAvatarSize);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    private String getMd5(String devEmail) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(devEmail.getBytes());
        byte byteData[] = md.digest();
        StringBuilder sb = new StringBuilder(0);
        for (byte aByteData : byteData) {
            sb.append(Integer.toString((aByteData & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    @Override
    public boolean canDismiss(Object token) {
        Log.d(TAG, "hit canDismiss");
        return mMessage != null;
    }

    @Override
    public void onDismiss(View view, Object token) {
        if (mMessage != null) {
            FrameLayout frameLayout = new FrameLayout(getContext());
            frameLayout.setLayoutParams(new FrameLayout.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            TextView textView = new TextView(getContext());
            textView.setText(mMessage);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(20);
            frameLayout.addView(textView);
            mLayout.removeAllViews();
            mLayout.addView(frameLayout);
        } else {
            new XkcdTask(getContext()).execute();
        }
    }
}
