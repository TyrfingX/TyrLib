package tyrfing.common.ui;

import tyrfing.common.render.Ressources;

public class AndroidSkin implements Skin {
	
	public void load()
	{
		Ressources.loadRes("ButtonNormal", tyrfing.games.R.drawable.button_normal);
		Ressources.loadRes("ButtonClick", tyrfing.games.R.drawable.button_click);
		Ressources.loadRes("ButtonDisabled", tyrfing.games.R.drawable.button_disabled);
		Ressources.loadRes("MessageBox", tyrfing.games.R.drawable.box);
	}
}
