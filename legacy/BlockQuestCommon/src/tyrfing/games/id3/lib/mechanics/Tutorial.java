package tyrfing.games.id3.lib.mechanics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import android.graphics.Color;
import tyrfing.common.files.FileReader;
import tyrfing.common.files.FileWriter;
import tyrfing.common.game.BaseGame;
import tyrfing.common.input.InputManager;
import tyrfing.common.input.TouchListener;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.IFrameListener;
import tyrfing.common.render.SceneManager;
import tyrfing.common.render.TargetMetrics;
import tyrfing.common.renderables.Text;
import tyrfing.common.struct.Node;
import tyrfing.common.ui.Window;
import tyrfing.common.ui.WindowManager;
import tyrfing.games.id3.lib.MainGame;
import tyrfing.games.id3.lib.MenuConfig;
import tyrfing.games.id3.lib.R;

public class Tutorial extends Observable implements IFrameListener,
		TouchListener {
	private Map<String, Boolean> items;
	private int introCount = 0;
	private ArrayList<Text> introTexts;

	private static final String[] intro = {
			BaseGame.getString(R.string.intro1),
			BaseGame.getString(R.string.intro2),
			BaseGame.getString(R.string.intro3),
			BaseGame.getString(R.string.intro4),
			BaseGame.getString(R.string.intro5),
			BaseGame.getString(R.string.intro6),
			BaseGame.getString(R.string.intro7),
			BaseGame.getString(R.string.intro8),
			BaseGame.getString(R.string.intro9),
			BaseGame.getString(R.string.intro10),
			BaseGame.getString(R.string.intro11),
			BaseGame.getString(R.string.intro12),
			BaseGame.getString(R.string.intro13),
			BaseGame.getString(R.string.intro14)};

	private static final float TIME_BETWEEN_LINES = 1.5f;
	private static final float TIME_BLEND = 0.5f;
	private float passedTime;

	public Tutorial(String source) {
		String saveData = FileReader.readFile(MainGame.CONTEXT, source);
		items = new HashMap<String, Boolean>();
		String[] strItems = saveData.split(";");
		for (String strItem : strItems) {
			String[] strEntry = strItem.split(":");
			try
			{
				items.put(strEntry[0], Boolean.valueOf(strEntry[1]));
			}
			catch (Exception e)
			{
				try {
					items.put(strEntry[0],true);
				}
				catch (Exception e2)
				{
					
				}
			}
		}

	}

	public Tutorial() {
		items = new HashMap<String, Boolean>();
		FileWriter.writeFile(MainGame.CONTEXT, "tutorial.bs", "");
	}

	public void doItem(String name) {
		items.put(name, true);
		FileWriter.writeFile(MainGame.CONTEXT, "tutorial.bs", mapToString());
	}

	public boolean isItemDone(String name) {
		if (!items.containsKey(name))
			return false;
		return items.get(name);
	}

	private String mapToString() {
		String res = "";
		for (String key : items.keySet()) {
			Boolean value = items.get(key);
			res += key + ":" + value + ";";
		}
		return res;
	}

	public void doIntro() {
		SceneManager.RENDER_THREAD.addFrameListener(this);
		this.doItem("Intro");
		introTexts = new ArrayList<Text>();
		InputManager.addTouchListener(this);
		passedTime = Tutorial.TIME_BETWEEN_LINES;
	}

	@Override
	public void onUpdate(float time) {
		passedTime += time;

		if (passedTime >= Tutorial.TIME_BETWEEN_LINES) {
			this.displayNextTutMessage();
			passedTime -= Tutorial.TIME_BETWEEN_LINES;
		}

	}

	private void displayNextTutMessage() {
		introTexts.add(SceneManager.createText(intro[introCount], Color.BLACK,
				new Node(MenuConfig.LEFT / 3, MenuConfig.OFFSET
						* (introCount + 1) / 3)));
		introTexts.get(introCount).blendIn(new Vector2(-1, 0),
				Tutorial.TIME_BLEND);
		introCount++;
		if (introCount >= intro.length) {
			SceneManager.RENDER_THREAD.removeFrameListener(this);
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	@Override
	public void onClearRenderer() {
	}

	@Override
	public boolean onTouchDown(Vector2 point) {
		return false;
	}

	@Override
	public boolean onTouchUp(Vector2 point) {

		if (introCount < intro.length) {

			while (introCount < intro.length) {
				this.displayNextTutMessage();
			}

		} else {
			InputManager.removeTouchListener(this);
			for (Text text : introTexts) {
				text.fadeOut(new Vector2(-10, 0), Tutorial.TIME_BLEND);
			}
			this.setChanged();
			this.notifyObservers();
		}
		return true;
	}

	@Override
	public boolean onTouchMove(Vector2 point) {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public void createInfo(String message, String name) {
		Window info = WindowManager.createConfirmMessageBox(name + "/infoBox",
															TargetMetrics.width * 0.1f, TargetMetrics.height * 0.25f,
															TargetMetrics.width * 0.8f, TargetMetrics.height * 0.35f, message);
		WindowManager.makePopup(info, name);
	}
	
	public void createInfo(String message)
	{
		this.createInfo(message, new Integer(message.hashCode()).toString());
	}

	@Override
	public long getPriority() {
		return 0;
	}

}
