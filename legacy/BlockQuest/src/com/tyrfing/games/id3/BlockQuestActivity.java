package com.tyrfing.games.id3;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

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
	 
	    
	    // Create the adView
	    adView = new AdView(this, AdSize.BANNER, MY_AD_UNIT_ID);
	    // Add the adView to it
	    outer.addView(game, gameViewParams);
	    outer.addView(adView, adParams);
	   
	    setContentView(outer);

	    // Initiate a generic request to load it with an ad
	    AdRequest adRequest = new AdRequest();
	    adRequest.addTestDevice("761E61422E05577CD14CB753A058E8FF");

	    adView.loadAd(adRequest);
	    
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
	  	  
}