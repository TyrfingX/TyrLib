package com.tyrfing.games.id17.gui.house;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.diplomacy.Diplomacy;
import com.tyrfing.games.id17.diplomacy.actions.DiploAction;
import com.tyrfing.games.id17.diplomacy.actions.InviteToIntrigue;
import com.tyrfing.games.id17.diplomacy.category.OtherCategory;
import com.tyrfing.games.id17.gui.MenuPoint;
import com.tyrfing.games.id17.gui.PaperButton;
import com.tyrfing.games.id17.gui.TabGUI;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.JustificationEntry;
import com.tyrfing.games.id17.gui.war.BattleGUI;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.intrigue.IntrigueProject;
import com.tyrfing.games.id17.intrigue.actions.IntrigueAction;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.graphics.renderables.FormattedText2.ALIGNMENT;
import com.tyrlib2.graphics.renderer.TextureAtlas;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.gui.DestroyOnEvent;
import com.tyrlib2.gui.IEventListener;
import com.tyrlib2.gui.ImageBox;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.ProgressBar;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowEvent;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.util.Color;

public class IntrigueProjectGUI extends MenuPoint implements IUpdateable {
	
	public static final ScaledVector2 ACTION_LABEL_POS = new ScaledVector2(0.025f,0.035f,2);
	public static final ScaledVector2 PROGRESS_BAR_POS = new ScaledVector2(ACTION_LABEL_POS.x + HeaderedMail.ACCEPT_SIZE.x + 0.01f, ACTION_LABEL_POS.y + 0.005f,2);
	public static final ScaledVector2 PROGRESS_BAR_SIZE = new ScaledVector2(BattleGUI.INFO_BOX_SIZE.x - PROGRESS_BAR_POS.x - 0.025f, 0.0175f,2);
	
	public static final ScaledVector2 PROGRESS_TEXT_POS = new ScaledVector2(PROGRESS_BAR_POS.x, PROGRESS_BAR_POS.y + PROGRESS_BAR_SIZE.y,2);
	public static final ScaledVector2 REMAINING_DAYS_TEXT_POS = new ScaledVector2(PROGRESS_BAR_POS.x + PROGRESS_BAR_SIZE.x - 0.02f, PROGRESS_TEXT_POS.y,2);
	public static final ScaledVector2 INCREASE_TEXT_POS = new ScaledVector2(PROGRESS_BAR_POS.x + PROGRESS_BAR_SIZE.x/2, PROGRESS_TEXT_POS.y,2);
	public static final ScaledVector2 SUPPORTERS_TEXT_POS = new ScaledVector2(ACTION_LABEL_POS.x, ACTION_LABEL_POS.y + HeaderedMail.ACCEPT_SIZE.y,2);
	
	public static final ScaledVector2 SUPPORTER_BASE_POS = new ScaledVector2(ACTION_LABEL_POS.x, SUPPORTERS_TEXT_POS.y + 0.05f,2);
	public static final ScaledVector2 SUPPORTER_SIGIL_SIZE = TabGUI.SIGIL_SIZE.multiply(0.5f);
	
	public static final ScaledVector2 INVITE_POS = new ScaledVector2(BattleGUI.INFO_BOX_POS.x + BattleGUI.INFO_BOX_SIZE.x - HeaderedMail.ACCEPT_SIZE.x - 0.05f, BattleGUI.INFO_BOX_POS.y + BattleGUI.INFO_BOX_SIZE.y - HeaderedMail.ACCEPT_SIZE.y - 0.05f,2);
	
	private Window bg;
	private ImageBox target;
	private PaperButton action;
	private ProgressBar progress;
	private Label progressText;
	private Label remainingDaysText;
	private Label increaseText;
	private List<ImageBox> supporters = new ArrayList<ImageBox>();
	
	private PaperButton invite;
	private ImageBox inviteTarget;
	
	private House house;
	private House displayed;
	private IntrigueProject project;
	
	public IntrigueProjectGUI(Window parent) {
		bg = WindowManager.getInstance().createImageBox("INTRIGUE_PROJECT/BG", BattleGUI.INFO_BOX_POS, "MAIN_GUI", "PAPER2", BattleGUI.INFO_BOX_SIZE);
		parent.addChild(bg);
		mainElements.add(bg);
		
		action = new PaperButton("INTRIGUE_PROJECT/LABEL_BUTTON", ACTION_LABEL_POS.get(), HeaderedMail.ACCEPT_SIZE.get(2), HeaderedMail.ACCEPT_BORDER_SIZE.get().x, "Action");
		bg.addChild(action);
		mainElements.add(action);
		
		TextureAtlas atlas = SceneManager.getInstance().getTextureAtlas("SIGILS1");
		String regionName = atlas.getRegionName(0);
		target = (ImageBox) WindowManager.getInstance().createImageBox("INTRIGUE_PROJECT/TARGET_SIGIL", JustificationEntry.SIGIL_POS.get(0), "SIGILS1", regionName, JustificationEntry.SIGIL_SIZE.get(1));
		action.addChild(target);
		target.setInheritsAlpha(true);
	
		action.addEventListener(WindowEventType.CONFIRMED, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				final HeaderedMail mail = house.intrigueProject.action.showInfo(house, house.intrigueProject.receiver, house.intrigueProject.options);
				if (World.getInstance().getPlayerController().getHouse() == house.intrigueProject.sender) {
					mail.addAcceptButton();
					mail.setAcceptText("Stop");
					mail.getAcceptButton().addEventListener(WindowEventType.CONFIRMED, new IEventListener() {
						@Override
						public void onEvent(WindowEvent event) {
							house.intrigueProject.abort(house.intrigueProject.sender);
							World.getInstance().getMainGUI().mailboxGUI.removeMail(mail);
							mail.hide();
							World.getInstance().getMainGUI().houseGUI.intrigueGUI.show(house, displayed);
							hide();
						}
					});
				}
				
				if (mail != null) {
					World.getInstance().getMainGUI().mailboxGUI.addMail(mail, true);
				}
			}
		});
		
		target.addEventListener(WindowEventType.TOUCH_DOWN, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				World.getInstance().getMainGUI().houseGUI.show(house.intrigueProject.receiver);
			}
		});
		
		float tmp = WindowManager.getInstance().getSkin().FRAME_BORDER_SIZE;
		WindowManager.getInstance().getSkin().FRAME_BORDER_SIZE = 0.001f;
		progress = (ProgressBar) WindowManager.getInstance().createProgressBar("INTRIGUE_PROJECT/PROGRESSBAR", PROGRESS_BAR_POS, PROGRESS_BAR_SIZE, 1f);
		progress.setProgress(0.5f);
		bg.addChild(progress);
		mainElements.add(progress);
		WindowManager.getInstance().getSkin().FRAME_BORDER_SIZE = tmp;
		
		progressText = (Label) WindowManager.getInstance().createLabel("INTRIGUE_PROJECT/PROGRESS_TEXT", PROGRESS_TEXT_POS, "10/100");
		progressText.setColor(Color.BLACK.copy());
		progressText.setAlignment(ALIGNMENT.LEFT);
		bg.addChild(progressText);
		mainElements.add(progressText);
		
		remainingDaysText = (Label) WindowManager.getInstance().createLabel("INTRIGUE_PROJECT/REMAINING_DAYS_TEXT", REMAINING_DAYS_TEXT_POS, "523d");
		remainingDaysText.setColor(Color.BLACK.copy());
		remainingDaysText.setAlignment(ALIGNMENT.RIGHT);
		bg.addChild(remainingDaysText);
		mainElements.add(remainingDaysText);
		
		increaseText = (Label) WindowManager.getInstance().createLabel("INTRIGUE_PROJECT/INCREASE_TEXT", INCREASE_TEXT_POS, "+2");
		increaseText.setColor(Color.BLACK.copy());
		increaseText.setAlignment(ALIGNMENT.CENTER);
		bg.addChild(increaseText);
		mainElements.add(increaseText);
		
		Label supportersText = (Label) WindowManager.getInstance().createLabel("INTRIGUE_PROJECT/SUPPORTERS_TEXT", SUPPORTERS_TEXT_POS, "Supporters");
		supportersText.setColor(Color.BLACK.copy());
		bg.addChild(supportersText);
		mainElements.add(supportersText);
		
		invite = new PaperButton("INTRIGUE_PROJECT/INVITE_BUTTON", INVITE_POS.get(), HeaderedMail.ACCEPT_SIZE.get(), HeaderedMail.ACCEPT_BORDER_SIZE.get().x, "Invite");
		bg.addChild(invite);
		mainElements.add(invite);
		WindowManager.getInstance().addTextTooltip(invite, "");

		inviteTarget = (ImageBox) WindowManager.getInstance().createImageBox("INTRIGUE_PROJECT/INVITE_SIGIL", JustificationEntry.SIGIL_POS.get(0), "SIGILS1", regionName, JustificationEntry.SIGIL_SIZE);
		invite.addChild(inviteTarget);
		inviteTarget.setInheritsAlpha(true);
		
		inviteTarget.addEventListener(WindowEventType.TOUCH_DOWN, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				World.getInstance().getMainGUI().houseGUI.show(displayed);
			}
		});
		
		invite.addEventListener(WindowEventType.CONFIRMED, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				DiploAction invite = Diplomacy.getInstance().getAction(Diplomacy.OTHER, OtherCategory.INVITE_TO_INTRIGUE_ID);
				invite.selectedByUser(house, displayed);
			}
		 });
		
		hide();
	}
	
	public void show(final House house, final House displayed) {
		this.house = house;
		this.displayed = displayed; 
		this.project = house.intrigueProject;
		
		boolean ownNeighbour = house.haveSameOverlordWith(displayed)  || house.isRealmNeighbour(displayed);
		boolean targetNeighbour = house.intrigueProject.receiver.haveSameOverlordWith(displayed)  || house.intrigueProject.receiver.isRealmNeighbour(displayed);
		boolean neighbour = ownNeighbour || targetNeighbour;
		boolean validTarget =  displayed != house.intrigueProject.receiver && !house.intrigueProject.supporters.contains(displayed);
		
		Label tooltip = (Label) WindowManager.getInstance().getWindow(invite.getName() + "/TooltipText");
		tooltip.setText(
			Util.getFlaggedText("(1)",validTarget) + " Not target House or already supporting\n" + 
			Util.getFlaggedText("(2)", neighbour) + " Either of the following\n" + 
			Util.getFlaggedText(" (-)", ownNeighbour) + " Your Vassal or your Realm Neighbour\n" + 
			Util.getFlaggedText(" (-)", targetNeighbour) + " Target Vassal or target Realm Neighbour"
		);
		
		
		updateProgress();
		target.setAtlasRegion(house.intrigueProject.receiver.getSigilName());
		inviteTarget.setAtlasRegion(displayed.getName());
		
		inviteTarget.setReceiveTouchEvents(true);
		target.setReceiveTouchEvents(true);
		action.enable();
		
		if (!InviteToIntrigue.sIsEnabled(house, displayed)) {
			invite.disable();
			invite.setReceiveTouchEvents(true);
		} else {
			invite.enable();
		}
		
		increaseText.setText("+" + (int)(house.intrigueProject.getPlotSpeed()*100)/100.f);
		
		ScaledVector2 pos = new ScaledVector2(SUPPORTER_BASE_POS);
		for (int i = 0; i < project.supporters.size(); ++i) {
			ImageBox supporter = (ImageBox) WindowManager.getInstance().createImageBox("INTRIGUE_PROJECT/SUPPORTER_" + i, pos.get(), "SIGILS1", project.supporters.get(i).getName(), SUPPORTER_SIGIL_SIZE.get(1));
			final House supportHouse = project.supporters.get(i);
			supporter.addEventListener(WindowEventType.TOUCH_DOWN, new IEventListener() {
				@Override
				public void onEvent(WindowEvent event) {
					World.getInstance().getMainGUI().houseGUI.show(supportHouse);
				}
			});
			
			supporter.setReceiveTouchEvents(true);
			
			pos.x += SUPPORTER_SIGIL_SIZE.x;
			supporter.setAlpha(0);
			bg.addChild(supporter);
			mainElements.add(supporter);
			
			supporter.addEventListener(WindowEventType.FADE_OUT_FINISHED, new DestroyOnEvent());
		}
		
		World.getInstance().getUpdater().addItem(this);
		
		super.show();
	}
	
	private void updateProgress() {
		if (project != null) {
			IntrigueAction intrigue = project.action; 
			float maxPoints = project.getMaxPoints();
			action.getLabel().setText(intrigue.getName());
			progressText.setText((int)project.getPoints() + "/" + (int)maxPoints);
			progress.setProgress((project.getPoints() / maxPoints));
			remainingDaysText.setText(project.getEstimatedRemainingDays() + "d");
		} else {
			hide();
			World.getInstance().getMainGUI().houseGUI.hide();
		}
	}
	
	@Override
	public void hide() {
		if (displayed != null) {
			for (int i = 0; i < project.supporters.size(); ++i) {
				int last = mainElements.size()-1;
				mainElements.remove(last);
			}
		}
		
		displayed = null;
		
		inviteTarget.setReceiveTouchEvents(false);
		target.setReceiveTouchEvents(false);
		action.disable();
		
		World.getInstance().getUpdater().removeItem(this);
		super.hide();
	}
	
	@Override
	public void onUpdate(float time) {
		updateProgress();
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
} 
