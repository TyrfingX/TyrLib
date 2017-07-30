package com.tyrfing.games.id3;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.widget.RelativeLayout;
import tyrfing.games.id3.lib.AppRater;

public class BlockQuestActivity extends tyrfing.games.id3.lib.BlockQuestActivity {
	private AdView adView;
	private String MY_AD_UNIT_ID = "ca-app-pub-1009404857840985/3978060158";	 
	public static AdView ADS;
	
	@Override
	public void go() {
		super.go();
		
	    RelativeLayout outer = new RelativeLayout(this);
	    RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
	            RelativeLayout.LayoutParams.WRAP_CONTENT);
	    @SuppressWarnings("deprecation")
		RelativeLayout.LayoutParams gameViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, 
	            RelativeLayout.LayoutParams.WRAP_CONTENT); 
	    adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	    gameViewParams.addRule(RelativeLayout.ABOVE);
	    adParams.addRule(RelativeLayout.BELOW);
	    
        // Create a banner ad. The ad size and ad unit ID must be set before calling loadAd.
	    adView = new AdView(this);
	    adView.setAdSize(AdSize.SMART_BANNER);
	    adView.setAdUnitId(MY_AD_UNIT_ID);

        // Create an ad request.
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

        // Add the AdView to the view hierarchy.
        outer.addView(game, gameViewParams);
        //outer.addView(adView, adParams);

        setContentView(outer);
        
        // Start loading the ad.
        adView.loadAd(adRequestBuilder.build());
	    
	    try
	    {
	    	game.go();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    
	    AppRater.APP_TITLE = "Dungeon God";
	    AppRater.APP_PNAME = "com.tyrfing.games.id3";
	    AppRater.app_launched(this);
	}
	
	  @Override
	  public void onDestroy() {
		try
		{
			super.onDestroy();
			adView.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	  }
	  
	  @Override
	protected void onPause() {
		adView.pause();
		super.onPause();
	}
	  
	  @Override
	protected void onResume() {
		adView.resume();
		super.onResume();
	}
	  	  
}