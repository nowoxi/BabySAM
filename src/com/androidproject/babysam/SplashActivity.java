package com.androidproject.babysam;


import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends babysamActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        
        ImageView logo1 = (ImageView) findViewById(R.id.logo);
        Animation fade1 = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        logo1.startAnimation(fade1);
        
        //Animation fade2 = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fade1.setAnimationListener(new AnimationListener() {
        	@Override
	        public void onAnimationEnd(Animation animation) {
		        startActivity(new Intent(SplashActivity.this,
		        LoginActivity.class));
		        SplashActivity.this.finish();
	        }

    		@Override
    		public void onAnimationRepeat(Animation arg0) {
    			// TODO Auto-generated method stub
			
    		}

    		@Override
    		public void onAnimationStart(Animation animation) {
    			// TODO Auto-generated method stub
			
    		}
        });
        
    }
    
    
    
    @Override
    protected void onPause() {
	    super.onPause();
	    // Stop the animation
	    ImageView logo1 = (ImageView) findViewById(R.id.logo);
	    logo1.clearAnimation();
	 
	    // ... stop other animations
    }
    
    
}