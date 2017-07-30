package tyrfing.games.BlockQuest.prayers;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.SceneManager;
import tyrfing.common.render.TargetMetrics;
import tyrfing.common.renderables.Text;
import tyrfing.common.struct.Node;
import tyrfing.games.BlockQuest.GodPowers.Power;
import tyrfing.games.BlockQuest.GodPowers.PowerType;
import tyrfing.games.BlockQuest.lib.MenuConfig;
import tyrfing.games.BlockQuest.mechanics.State;

public class PrayerPower extends Power {

	
	private Text prayerText;
	public static String header = "Here you can take on requests from\nyour followers and save them from peril.";
	private State state;
	
	private List<Prayer> prayers;
	
	protected static int receivedPrayers = 0;
	
	public PrayerPower() {
		super(PowerType.PRAYER, "Prayer", true);
		prayerText = SceneManager.createText(header, Color.BLACK, new Node(TargetMetrics.width*0.13f,TargetMetrics.height*0.075f));
		prayerText.setVisible(false);
		prayers = new ArrayList<Prayer>();
	}

	public void setState(State state)
	{
		this.state = state;
	}
	
	public void display()
	{
		prayerText.blendIn(new Vector2(0,0), MenuConfig.FADE_TIME);
		for (Prayer prayer : prayers)
		{
			prayer.displayButton();
		}
	}
	
	public void hide()
	{
		prayerText.fadeOut(new Vector2(0,0), MenuConfig.FADE_TIME);
		for (Prayer prayer : prayers)
		{
			prayer.hideButton();
		}
	}
	
	public void addPrayer(PrayerType type, int level)
	{
		
		receivedPrayers++;
		
		if (prayers.size() == 3) {
			removePrayer(prayers.get(0));
		}
		
		if (type == PrayerType.LOST)
		{
			prayers.add(new Lost(level,prayers.size()+2,state,this));
		} else if (type == PrayerType.RUN)
		{
			prayers.add(new Run(level,prayers.size()+2,state,this));
		}
		else if (type == PrayerType.QUAKE)
		{
			prayers.add(new Quake(level,prayers.size()+2,state,this));
		}
	}
	
	public int countPrayers()
	{
		return prayers.size();
	}
	
	public void removePrayer(Prayer prayer){
		prayer.prayerButton.destroy();
		prayers.remove(prayer);
		for (Prayer p : prayers)
		{
			if (p.pos > prayer.pos)
			{
				p.moveUp();
			}
		}
	}
	
	public String toString()
	{
		String res = "";
		
		for (Prayer prayer : prayers) {
			res += prayer.getType().toString() + "," + prayer.getLevel() + ";";
		}
		
		return res;
	}
	
	public void init(String data)
	{
		String[] prayersString = data.split(";");
		for (String prayerString : prayersString)
		{
			if (!prayerString.equals("")) {
				String[] prayerData = prayerString.split(",");
				String strType = prayerData[0];
				String strLevel = prayerData[1];
				PrayerType type = PrayerType.valueOf(strType);
				this.addPrayer(type, Integer.valueOf(strLevel));
			}
		}
	}
	
	public void onDisplayMenu()
	{
		if (this.countPrayers() != 0)
		{
			openMenu.setCaption("Prayers(Available)");
		}
		else
		{
			openMenu.setCaption("Prayers");
		}
	}
	
}
