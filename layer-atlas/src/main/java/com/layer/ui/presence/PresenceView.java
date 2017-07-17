package com.layer.ui.presence;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.AttributeSet;
import android.view.View;

import com.layer.sdk.messaging.Identity;
import com.layer.sdk.messaging.Presence;
import com.layer.ui.R;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public class PresenceView extends View {

    private final Paint mBackgroundPaint = new Paint();
    private final Paint mPresencePaint = new Paint();

    private Identity mIdentity;
    private int mAvailableColor = Color.rgb(0x4F, 0xBF, 0x62);
    private int mBusyColor = Color.rgb(0xE6, 0x44, 0x3F);
    private int mAwayColor = Color.rgb(0xF7, 0xCA, 0x40);
    private int mInvisibleColor = Color.rgb(0x50, 0xC0, 0x62);
    private int mOfflineColor = Color.rgb(0x99, 0x99, 0x9c);

    public PresenceView(Context context) {
        super(context);
    }

    public PresenceView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PresenceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseStyle(context, attrs, defStyleAttr);
    }

    public void setParticipants(Set<Identity> participants) {
        showPresenceIfIdentityIsOne(participants);
    }

    public void setParticipants(Identity... participants) {
        showPresenceIfIdentityIsOne(Arrays.asList(participants));
    }

    private void showPresenceIfIdentityIsOne(Collection<Identity> participants) {
        if (participants.size() == 1) {
            mIdentity = participants.iterator().next();
            setVisibility(VISIBLE);
            invalidate();
        } else {
            setVisibility(INVISIBLE);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPresence(canvas);
    }

    private void drawPresence(Canvas canvas) {

        Presence.PresenceStatus currentStatus = mIdentity != null ? mIdentity.getPresenceStatus() : null;
        if (currentStatus == null) {
            return;
        }

        switch (currentStatus) {
            case AVAILABLE:
                drawAvailable(canvas);
                break;
            case AWAY:
                drawAway(canvas);
                break;
            case OFFLINE:
                drawOffline(canvas);
                break;
            case INVISIBLE:
                drawInvisible(canvas);
                break;
            case BUSY:
                drawBusy(canvas);
                break;
            default:
                drawDefault(canvas);
                break;
        }
    }

    public void drawAvailable(Canvas canvas) {
        mPresencePaint.setColor(mAvailableColor);
        drawPresence(canvas, false);
    }

    public void drawAway(Canvas canvas) {
        mPresencePaint.setColor(mAwayColor);
        drawPresence(canvas, false);
    }

    public void drawOffline(Canvas canvas) {
        mPresencePaint.setColor(mOfflineColor);
        drawPresence(canvas, true);
    }

    public void drawInvisible(Canvas canvas) {
        mPresencePaint.setColor(mInvisibleColor);
        drawPresence(canvas, true);
    }

    public void drawBusy(Canvas canvas) {
        mPresencePaint.setColor(mBusyColor);
        drawPresence(canvas, false);
    }

    public void drawDefault(Canvas canvas) {
        drawPresence(canvas, false);
    }

    private void drawPresence(Canvas canvas,boolean makeCircleHollow) {

        int drawableWidth = getWidth() - (getPaddingLeft() + getPaddingRight());
        int drawableHeight = getHeight() - (getPaddingTop() + getPaddingBottom());
        float dimension = Math.min(drawableWidth, drawableHeight);
        float density = getContext().getResources().getDisplayMetrics().density;
        float fraction = 1f;
        float BORDER_SIZE_DP = 1F;

        float outerRadius = fraction * dimension / 2f;
        float innerRadius = outerRadius - (density * BORDER_SIZE_DP);
        float centerX = getPaddingLeft() + outerRadius;
        float centerY = getPaddingTop() + outerRadius;

        // Presence
        float presenceOuterRadius = outerRadius / 3f;
        float presenceInnerRadius = innerRadius / 3f;
        float presenceCenterX = centerX + outerRadius - presenceOuterRadius;
        float presenceCenterY = centerY + outerRadius - presenceOuterRadius;

        // Clear background + create border
        mBackgroundPaint.setColor(Color.WHITE);
        mBackgroundPaint.setAntiAlias(true);
        canvas.drawCircle(presenceCenterX, presenceCenterY, presenceOuterRadius, mBackgroundPaint);

        // Draw Presence status
        mPresencePaint.setAntiAlias(true);
        canvas.drawCircle(presenceCenterX, presenceCenterY, presenceInnerRadius, mPresencePaint);

        // Draw hollow if needed
        if (makeCircleHollow) {
            canvas.drawCircle(presenceCenterX, presenceCenterY, (presenceInnerRadius / 2f), mBackgroundPaint);
        }
    }

    @VisibleForTesting
    public int getPresenceColor() {
        return mPresencePaint.getColor();
    }

    private void parseStyle(Context context, AttributeSet attrs, int defStyle) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PresenceView, R.attr.PresenceView, defStyle);
        this.mAvailableColor = ta.getColor(R.styleable.PresenceView_presenceAvailableColor, Color.rgb(0x4F, 0xBF, 0x62));
        this.mBusyColor = ta.getColor(R.styleable.PresenceView_presenceBusyColor, Color.rgb(0xE6, 0x44, 0x3F));
        this.mAwayColor = ta.getColor(R.styleable.PresenceView_presenceAwayColor, Color.rgb(0xF7, 0xCA, 0x40));
        this.mInvisibleColor = ta.getColor(R.styleable.PresenceView_presenceInvisibleColor, Color.rgb(0x50, 0xC0, 0x62));
        this.mOfflineColor = ta.getColor(R.styleable.PresenceView_presenceOfflineColor, Color.rgb(0x99, 0x99, 0x9c));
        ta.recycle();
    }
}
