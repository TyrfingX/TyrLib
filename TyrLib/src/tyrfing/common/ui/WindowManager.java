package tyrfing.common.ui;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import tyrfing.common.game.objects.Updater;
import tyrfing.common.render.SceneManager;
import tyrfing.common.ui.widgets.Button;
import tyrfing.common.ui.widgets.ConfirmMessageBox;
import tyrfing.common.ui.widgets.ImageBox;
import tyrfing.common.ui.widgets.Label;
import tyrfing.common.ui.widgets.MessageBox;
import tyrfing.common.ui.widgets.Overlay;
import tyrfing.common.ui.widgets.ProgressBar;
import tyrfing.common.ui.widgets.YesNoMessageBox;

import android.graphics.Bitmap;
import android.graphics.Color;

public class WindowManager {
	public static final long MAIN_LAYER = 10000;
	public static final long OVERLAY_LAYER = MAIN_LAYER * 2;
	
	private static Map<String, Window> windows;
	protected static Updater windowUpdater;
	private static Skin skin;

	public static void init(Skin skin)
	{
		skin.load();
		WindowManager.skin = skin;
		windows = new ConcurrentHashMap<String, Window>();
		windowUpdater = new Updater();
		SceneManager.RENDER_THREAD.addFrameListener(windowUpdater);
	}
	
	public static Skin replaceSkin(Skin skin)
	{
		skin.load();
		Skin tmpSkin = WindowManager.skin;
		WindowManager.skin = skin;
		return tmpSkin;
	}
	
	public static ImageBox createImageBox(String name, float x, float y, float w, float h, Bitmap bitmap)
	{
		ImageBox imgBox = new ImageBox(name,x,y,w,h, bitmap);
		imgBox.setPriority(MAIN_LAYER);
		addWindow(name, imgBox);
		return imgBox;
	}
	
	public static Label createLabel(String name, float x, float y, float w, float h, String caption, int bgColor)
	{
		Label label = new Label(name,x,y,w,h);
		label.setCaption(caption);
		label.setBgColor(bgColor);
		label.setPriority(MAIN_LAYER);
		addWindow(name, label);
		return label;
	}
	
	public static Button createButton(String name, float x, float y, float w, float h, String caption)
	{
		Button button = new Button(name,x,y,w,h, caption);
		button.setPriority(MAIN_LAYER);
		addWindow(name, button);
		return button;
	}

	public static ProgressBar createProgressBar(String name, float x, float y, float w, float h, float progress)
	{
		ProgressBar progressBar = new ProgressBar(name,x,y,w,h, progress);
		progressBar.setPriority(MAIN_LAYER);
		addWindow(name, progressBar);
		return progressBar;
	}
	
	public static MessageBox createMessageBox(String name, float x, float y, float w, float h, String caption)
	{
		MessageBox box = new MessageBox(name, x, y, w, h, caption);
		box.setPriority(MAIN_LAYER);
		addWindow(name, box);
		return box;
	}
	
	public static MessageBox createConfirmMessageBox(String name, float x, float y, float w, float h, String caption)
	{
		ConfirmMessageBox box = new ConfirmMessageBox(name, x, y, w, h, caption);
		box.setPriority(MAIN_LAYER);
		addWindow(name, box);
		return box;		
	}

	public static MessageBox createYesNoMessageBox(String name, float x, float y, float w, float h, String caption)
	{
		YesNoMessageBox box = new YesNoMessageBox(name, x, y, w, h, caption);
		box.setPriority(MAIN_LAYER);
		addWindow(name, box);
		return box;		
	}
	
	public static void makePopup(Window window, String name)
	{
		Window overlay = WindowManager.createOverlay(name, Color.BLACK, 150);
		
		window.addClickListener(new ClickListener() {
			public void onClick(Event event) {
				Window parent = event.getEvoker();
				while (parent.getParent() != null) parent = parent.getParent();
				parent.destroy();
			}
		});

		overlay.addChild(window);
		overlay.blendIn(1);
	}
	
	public static Overlay createOverlay(String name, int color, int alpha)
	{
		Overlay overlay = new Overlay(name, color, alpha);
		overlay.setPriority(OVERLAY_LAYER);
		addWindow(name, overlay);
		return overlay;		
	}
	
	public static void addWindow(String name, Window window)
	{
		
			windows.put(name, window);
		
	}
	
	public static void destroyAllWindows()
	{
		if (windows != null)
		{
			for (Window window : windows.values())
			{
				WindowManager.destroyWindow(window);
			}
		}
	}
	
	public static void destroyWindow(String name)
	{
		Window window = windows.get(name);
		windowUpdater.removeItem(window);
		if (window != null)
		{
			window.destroy();
			windows.remove(name);
		}
	}
	
	public static void removeWindow(Window window)
	{
		windows.remove(window.getName());
		windowUpdater.removeItem(window);
	}
	
	public static void destroyWindow(Window window)
	{
		WindowManager.destroyWindow(window.getName());
	}
	
	public static Window getWindow(String name)
	{
		if (!windows.containsKey(name)) return null;
		return windows.get(name);
	}

}
