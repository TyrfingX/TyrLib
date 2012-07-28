package tyrfing.common.game;

import tyrfing.common.render.Ressources;
import tyrfing.common.render.TargetMetrics;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public abstract class GameActivity extends Activity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		this.setRequestedOrientation(1);
        
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        TargetMetrics.init(this);
        Ressources.init(this);
        this.go();
    }

    
    public abstract void go();    
    
    
	
}
