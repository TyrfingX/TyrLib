package tyrfing.games.BlockQuest.lib;

import android.graphics.Color;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.Ressources;
import tyrfing.common.render.TargetMetrics;
import tyrfing.common.ui.Skin;
import tyrfing.common.ui.widgets.Button;
import tyrfing.common.ui.widgets.Label;
import tyrfingx.games.BlockQuest.lib.R;

public class PaperSkin implements Skin{
	public void load()
	{
		Ressources.loadRes("ButtonNormal", R.drawable.menubg, new Vector2(MenuConfig.WIDTH, MenuConfig.HEIGHT));
		Ressources.loadRes("ButtonClick",  R.drawable.buttonclick, new Vector2(MenuConfig.WIDTH, MenuConfig.HEIGHT));
		Ressources.loadRes("ButtonDisabled",  R.drawable.buttondisabled, new Vector2(MenuConfig.WIDTH, MenuConfig.HEIGHT));
		Ressources.loadRes("MessageBox",  R.drawable.menubg, new Vector2(TargetMetrics.width * 0.8f, TargetMetrics.height * 0.35f));
		Label.DEFAULT_TEXT_COLOR = Color.BLACK;
		Button.TEXT_OFFSET = 0;
	}
}

