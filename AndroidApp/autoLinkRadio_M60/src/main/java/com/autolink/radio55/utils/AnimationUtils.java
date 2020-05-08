package com.autolink.radio55.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

/**
 * 
 */
public class AnimationUtils {
	public static final int HS_TIME = 100;

	public enum AnimationState {
		STATE_SHOW, STATE_HIDDEN
	}


	public static void showAndHiddenAnimation(final View view, AnimationState state, long duration) {
		float start = 0f;
		float end = 0f;
		if (state == AnimationState.STATE_SHOW) {
			end = 1f;
			view.setVisibility(View.VISIBLE);
		} else if (state == AnimationState.STATE_HIDDEN) {
			start = 0.5f;
			view.setVisibility(View.INVISIBLE);
		}
		AlphaAnimation animation = new AlphaAnimation(start, end);
		animation.setDuration(duration);
		animation.setFillAfter(true);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				view.clearAnimation();
			}
		});
		view.setAnimation(animation);
		animation.start();
	}
}