package com.tyrfing.games.id17.gui.house;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.gui.DefaultItemListEntry.BG_TYPE;
import com.tyrfing.games.id17.gui.MenuPoint;
import com.tyrfing.games.id17.gui.TabGUI;
import com.tyrfing.games.id17.gui.holding.HoldingGUI;
import com.tyrfing.games.id17.gui.holding.OverviewGUI;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.gui.war.ArmyBuilderGUI;
import com.tyrfing.games.id17.gui.war.ArmyGUI;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.reputation.Reputation;
import com.tyrfing.games.id17.houses.reputation.ReputationSet;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.game.ILink;
import com.tyrlib2.game.LinkManager;
import com.tyrlib2.gui.DestroyOnEvent;
import com.tyrlib2.gui.IEventListener;
import com.tyrlib2.gui.ItemList;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.ScaledVector1;
import com.tyrlib2.gui.ScaledVector1.ScaleDirection;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowEvent;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.input.InputManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;

public class HouseOverviewGUI extends MenuPoint {
	
	public static final ScaledVector2 BG_DATA_POS = new ScaledVector2(OverviewGUI.HOLDING_INFO_POS.x * 1.9f, OverviewGUI.HOLDING_INFO_POS.y, 2);
	public static final ScaledVector2 BG_DATA_SIZE = new ScaledVector2(ArmyGUI.INFO_SIZE.x * 1.25f, ArmyGUI.INFO_SIZE.y, 2);

	public static final ScaledVector2 BG_BRANCH_POS = new ScaledVector2(BG_DATA_SIZE.x * 0.985f , BG_DATA_POS.y, 2);
	public static final ScaledVector2 BG_BRANCH_SIZE = new ScaledVector2(HoldingGUI.WINDOW_SIZE.x - BG_DATA_SIZE.x, BG_DATA_SIZE.y, 2);
	
	public static final ScaledVector2 GOLD_ICON_SIZE = new ScaledVector2(TabGUI.SIGIL_SIZE.multiply(0.4f));
	public static final ScaledVector2 GOLD_ICON_POS = new ScaledVector2(BG_DATA_SIZE.x * 0.80f - GOLD_ICON_SIZE.x, GOLD_ICON_SIZE.y / 2 + 0.02f, 2);
	
	public static final ScaledVector2 HONOR_ICON_SIZE = new ScaledVector2(TabGUI.SIGIL_SIZE.multiply(0.4f));
	public static final ScaledVector2 HONOR_ICON_POS = new ScaledVector2(BG_DATA_SIZE.x * 0.81f - HONOR_ICON_SIZE.x, GOLD_ICON_SIZE.y, 2);
	public static final ScaledVector1 ICON_OFFSET = new ScaledVector1( GOLD_ICON_SIZE.y + 0.02f, ScaleDirection.Y, 0 );
	
	public static final ScaledVector2 MALE_ICON_SIZE = new ScaledVector2(TabGUI.SIGIL_SIZE.multiply(0.4f));
	public static final ScaledVector2 MALE_ICON_POS = new ScaledVector2(BG_DATA_SIZE.x * 0.81f - MALE_ICON_SIZE.x, HONOR_ICON_POS.y, 2);
	
	public static final ScaledVector2 FEMALE_ICON_SIZE = MALE_ICON_SIZE;
	public static final ScaledVector2 FEMALE_ICON_POS = new ScaledVector2(BG_DATA_SIZE.x * 0.2f, 0, 0);
	
	public static final ScaledVector2 HOUSE_NAME_POS = new ScaledVector2(BG_DATA_POS.x + BG_DATA_SIZE.x * 0.1f, BG_DATA_POS.y + BG_DATA_SIZE.y * 0.1f, 2);
	
	public static final ScaledVector2 PLAYER_DATA_POS = new ScaledVector2(BG_DATA_POS.x + BG_DATA_SIZE.x * 0.05f, HOUSE_NAME_POS.y + BG_DATA_SIZE.y * 0.15f, 2);
	public static final ScaledVector2 OVERLORD_DATA_POS = new ScaledVector2(PLAYER_DATA_POS.x, PLAYER_DATA_POS.y + BG_DATA_SIZE.y * 0.05f + HouseEntry.SIZE_Y.x, 2);
	
	public static final ScaledVector2 REPUTATION_POS = new ScaledVector2(GOLD_ICON_POS.x+BG_DATA_SIZE.x*0.1f, GOLD_ICON_POS.y, 2);
	public static final ScaledVector2 REPUTATION_SIZE = new ScaledVector2(GOLD_ICON_SIZE.x*1.4f, GOLD_ICON_SIZE.y*1.4f, 1);
	public static final ScaledVector1 REPUTATION_OFFSET = new ScaledVector1(REPUTATION_SIZE.y*1.05f, ScaleDirection.Y, 1);
	
	public static final int REPUTATIONS_DETAILED_PER_ROW = 5;
	
	public House displayed;

	private Window parent;
	
	private Window bgData;
	private Window bgBranches;
	
	private Label houseName;
	
	private ItemList branches;
	
	private HouseEntry entryLord;
	private HouseEntry entryPlayer;
	
	private List<Window> reps = new ArrayList<Window>();
	private Label favorLabel;
	private Label alliesLabel;
	private Label reputationsLabel;
	
	private Label hegemonLabel;
	
	private List<Window> allies = new ArrayList<Window>();
	
	public HouseOverviewGUI(Window parent) {
		this.parent = parent;
		
		String name = parent.getName();
		
		bgData = WindowManager.getInstance().createImageBox(name + "/OVERVIEW/BG_DATA", BG_DATA_POS, "MAIN_GUI", "PAPER2", BG_DATA_SIZE);
		parent.addChild(bgData);
		mainElements.add(bgData);
		
		houseName = (Label) WindowManager.getInstance().createLabel(name + "/OVERVIEW/HOUSE_NAME", HOUSE_NAME_POS, "House");
		houseName.setColor(Color.BLACK);
		parent.addChild(houseName);
		mainElements.add(houseName);
		
		Vector2 basePos = new ScaledVector2(BG_DATA_SIZE.x*0.53f, BG_DATA_SIZE.y*0.135f + BG_DATA_POS.y, 1).get();

		/** SHOW FAVOR **/
		
		favorLabel = (Label) WindowManager.getInstance().createLabel(name + "/FAVORLABEL", basePos.copy(), "Favor 0");
		parent.addChild(favorLabel);
		favorLabel.setColor(Color.BLACK);
		mainElements.add(favorLabel);
		
		/** SHOW INFLUENCE **/
		
		hegemonLabel =  (Label) WindowManager.getInstance().createLabel(name + "/HEGEMONLABEL", OVERLORD_DATA_POS.get(), "Influence 0");
		parent.addChild(hegemonLabel);
		hegemonLabel.setColor(Color.BLACK);
		mainElements.add(hegemonLabel);
		
		/** SHOW ALLIES AND PROTECTORS **/
		
		basePos.y = PLAYER_DATA_POS.get().y;
		
		alliesLabel = (Label) WindowManager.getInstance().createLabel(name + "/ALLIESLABEL", basePos, "Allies:");
		parent.addChild(alliesLabel);
		alliesLabel.setColor(Color.BLACK);
		mainElements.add(alliesLabel);
		
		reputationsLabel = (Label) WindowManager.getInstance().createLabel(name + "/REPUTATIONSLABEL", REPUTATION_POS, "<link=REPUTATION>Reputations:");
		parent.addChild(reputationsLabel);
		mainElements.add(reputationsLabel);
		
		
		LinkManager.getInstance().registerLink(new ILink() {
			@Override
			public void onCall() {
				HeaderedMail mail = new HeaderedMail(
					"Reputations", "", 
					World.getInstance().getPlayerController().getHouse(), 
					displayed
				) {
					@Override
					protected void createHeaderedContent(String leftSigil, String rightSigil, boolean oneColumn, String title) {
						super.createHeaderedContent(leftSigil, rightSigil, oneColumn, title);
						
						Vector2 pos = HOUSE_NAME_POS.get();
						Reputation[] reputations = ReputationSet.reputations;
						for (int i = 0; i < reputations.length; ++i) {
							Window rep = WindowManager.getInstance().createImageBox(mainPaper.getName() + "/REPUTATION_DETAILED" + i, 
																					new Vector2(pos), "TRAITS", 
																					reputations[i].name, 
																					REPUTATION_SIZE);
							
							WindowManager.getInstance().addTextTooltip(rep, reputations[i].getTooltip(displayed));
							rep.setReceiveTouchEvents(true);
	
							mainPaper.addChild(rep);
							if (i % REPUTATIONS_DETAILED_PER_ROW == REPUTATIONS_DETAILED_PER_ROW-1) {
								pos.y += REPUTATION_OFFSET.get();
								pos.x = HOUSE_NAME_POS.get().x;
							} else {
								pos.x += REPUTATION_SIZE.get().x*1.05f;
							}
						}
					}
				};
				
				World.getInstance().getMainGUI().mailboxGUI.addMail(mail, true);
			}
		}, "REPUTATION");
		
		LinkManager.getInstance().registerLink(new ILink() {
			@Override
			public void onCall() {
				HeaderedMail mail = new HeaderedMail(
					"Reputations", "", 
					World.getInstance().getPlayerController().getHouse(), 
					World.getInstance().getPlayerController().getHouse()
				) {
					@Override
					protected void createHeaderedContent(String leftSigil, String rightSigil, boolean oneColumn, String title) {
						super.createHeaderedContent(leftSigil, rightSigil, oneColumn, title);
						
						House h = World.getInstance().getPlayerController().getHouse();
						
						Vector2 pos = HOUSE_NAME_POS.get();
						Reputation[] reputations = ReputationSet.reputations;
						for (int i = 0; i < reputations.length; ++i) {
							Window rep = WindowManager.getInstance().createImageBox(mainPaper.getName() + "/REPUTATION_DETAILED" + i, 
																					new Vector2(pos), "TRAITS", 
																					reputations[i].name, 
																					REPUTATION_SIZE);
							
							WindowManager.getInstance().addTextTooltip(rep, reputations[i].getTooltip(h));
							rep.setReceiveTouchEvents(true);
	
							mainPaper.addChild(rep);
							if (i % REPUTATIONS_DETAILED_PER_ROW == REPUTATIONS_DETAILED_PER_ROW-1) {
								pos.y += REPUTATION_OFFSET.get();
								pos.x = HOUSE_NAME_POS.get().x;
							} else {
								pos.x += REPUTATION_SIZE.get().x*1.05f;
							}
						}
						
						Mail back = World.getInstance().getMainGUI().mailboxGUI.getCurrentMail();
						if (back != null) {
							setBackMail(back);
						}
					}
				};
				
				World.getInstance().getMainGUI().mailboxGUI.addMail(mail, true);
			}
		}, "Reputation");
		
		bgBranches = WindowManager.getInstance().createImageBox(name + "/OVERVIEW/BG_BRANCHES", BG_BRANCH_POS, "MAIN_GUI", "PAPER", BG_BRANCH_SIZE);
		parent.addChild(bgBranches);
		mainElements.add(bgBranches);
		
		int items = (int) (3 / WindowManager.getInstance().getScale(0).y);
		branches = (ItemList) WindowManager.getInstance()
										   .createItemList(	name + "/OVERVIEW/BG_BRANCHES/FAMILY_LIST", 
												   			new ScaledVector2(BG_BRANCH_POS.x + BG_BRANCH_SIZE.x * 0.05f,
												   							  ArmyBuilderGUI.BUILD_UNITS_POS.y, 2), 
												   			BG_BRANCH_SIZE, 0, items);
		parent.addChild(branches);
		branches.setPassTouchEventsThrough(true);
		
		for (int i = 0; i < mainElements.size(); ++i) {
			mainElements.get(i).setVisible(false);
			mainElements.get(i).setAlpha(0);
		}
	}
	
	public void show(House house) {
		
		if (displayed == house) return;
		
		this.displayed = house;
		super.show();
	
		branches.setReceiveTouchEvents(true);
		
		if (branches.getCountEntries() == 0) {
			
			
			for (int i = 0; i < house.getSubHouses().size(); ++i) {
				House other = house.getSubHouses().get(i);
				HouseEntry entry = createEntry(other);
				entry.addTrait("Branch");
			}
			
			
			for (int i = 0; i < World.getInstance().getHouses().size(); ++i) {
				House other = World.getInstance().getHouses().get(i);
				
				if (World.getInstance().getHouses().get(i).getOverlord() != house) {
					if (other.getHouseStat(house, House.HAS_MARRIAGE) == 1) {
						createEntry(other);
					}
				}
			}
			
			
			branches.correctOffset();
			
			if (house != World.getInstance().getPlayerController().getHouse()) {
				entryPlayer = new HouseEntry(house, World.getInstance().getPlayerController().getHouse(), this, true, BG_TYPE.RECT);
				entryPlayer.setAlpha(0);
				entryPlayer.setVisible(true);
				entryPlayer.setReceiveTouchEvents(true);
				entryPlayer.setEnabled(true);
				entryPlayer.setRelativePos(PLAYER_DATA_POS.get());
				entryPlayer.fadeIn(1, 0.5f);
				entryPlayer.setInheritsFade(false);
				entryPlayer.setInheritsAlpha(false);
				entryPlayer.addEventListener(WindowEventType.FADE_OUT_FINISHED, new DestroyOnEvent());
				bgData.addChild(entryPlayer);
				WindowManager.getInstance().addWindow(entryPlayer);
			}
			
			if (house.getOverlord() != null) {
				hegemonLabel.setVisible(false);
				entryLord = new HouseEntry(house, house.getOverlord(), this, true, BG_TYPE.RECT);
				entryLord.addTrait("Lord");
				entryLord.setAlpha(0);
				entryLord.setVisible(true);
				entryLord.setReceiveTouchEvents(true);
				entryLord.setEnabled(true);
				entryLord.setRelativePos(OVERLORD_DATA_POS.get());
				entryLord.fadeIn(1, 0.5f);
				entryLord.setInheritsFade(false);
				entryLord.setInheritsAlpha(false);
				entryLord.addEventListener(WindowEventType.FADE_OUT_FINISHED, new DestroyOnEvent());
				bgData.addChild(entryLord);
				WindowManager.getInstance().addWindow(entryLord);
			} else {
				hegemonLabel.setAlpha(1);
				hegemonLabel.setVisible(true);
			}
			
			
		}
		
		if (reps.size() == 0) {
			Vector2 pos = REPUTATION_POS.get();
			pos.y += reputationsLabel.getSize().y*1.2f;
			for (int i = 0; i < House.MAX_REPUTATIONS; ++i) {
				if (house.activeReputations[i] != null) {
					Window rep = WindowManager.getInstance().createImageBox(parent.getName() + "/REPUTATION" + i, 
																			new Vector2(pos), "TRAITS", 
																			house.activeReputations[i].name, 
																			REPUTATION_SIZE);
					
					WindowManager.getInstance().addTextTooltip(rep, house.activeReputations[i].getTooltip(displayed));
					rep.setReceiveTouchEvents(true);
					
					parent.addChild(rep);
					rep.setAlpha(0);
					rep.fadeIn(1.0f, 0.5f);
					rep.addEventListener(WindowEventType.FADE_OUT_FINISHED, new DestroyOnEvent());
					reps.add(rep);
					pos.y += REPUTATION_OFFSET.get();
				}
			}
		}
		
		Vector2 basePos = new Vector2(alliesLabel.getRelativePos());
		basePos.y += alliesLabel.getSize().y;
		
		int countSigils = 0;
		
		for (int i = 0; i < World.getInstance().getHouses().size(); ++i) {
			final House h =  World.getInstance().getHouses().get(i);
			if (displayed.hasDefensivePact(h) || h.isProtector(displayed) || displayed.hasTradeAgreement(h)) {
				Window sigil = WindowManager.getInstance().createImageBox(parent.getName() + "/ALLY" + i, basePos, "SIGILS1", h.getSigilName(), REPUTATION_SIZE.get());
				parent.addChild(sigil);
				
				sigil.setReceiveTouchEvents(true);
				sigil.setAlpha(0);
				sigil.fadeIn(1.0f, 0.5f);
				
				sigil.addEventListener(WindowEventType.TOUCH_DOWN, new IEventListener() {
					@Override
					public void onEvent(WindowEvent event) {
						World.getInstance().getMainGUI().houseGUI.show(h);
					}
				});
				
				allies.add(sigil);
				
				String text = "";
				boolean makeNewLine = false;
				
				if (displayed.hasDefensivePact(h)) {
					text = "Ally";
					makeNewLine = true;
				}
				
				if (h.isProtector(displayed)) {
					if (makeNewLine) {
						text += "\n";
					}
					text += "Protector";
					makeNewLine = true;
				}
				
				if (h.hasTradeAgreement(displayed)) {
					if (makeNewLine) {
						text += "\n";
					}
					text += "Trader";
				}
				
				WindowManager.getInstance().addTextTooltip(sigil, text);
				
				countSigils++;
				if (countSigils % 3 == 0) {
					basePos.x = alliesLabel.getRelativePos().x;
					basePos.y += REPUTATION_SIZE.get().y;
				} else {
					basePos.x += REPUTATION_SIZE.get().x;
				}
			}
		}
		
		if (countSigils == 0) {
			alliesLabel.setText("Allies: None");
		} else {
			alliesLabel.setText("Allies: " + countSigils);
		}
		
		update();
		
		InputManager.getInstance().sort();
	}
	
	@Override
	public void hide() {
		super.hide();
	
		clear();
		
		branches.setReceiveTouchEvents(false);
		
		displayed = null;
	}
	
	private HouseEntry createEntry(House other) {
		HouseEntry entry = new HouseEntry(other, displayed, this, false, BG_TYPE.IMAGE);
		entry.addEventListener(WindowEventType.FADE_OUT_FINISHED, new DestroyOnEvent());
		branches.addItemListEntry(entry);
		
		entry.setAlpha(0);
		entry.setVisible(true);
		entry.setReceiveTouchEvents(true);
		entry.setEnabled(true);
		
		
		if (branches.getCountEntries() < 4) {
			if (branches.getCountEntries() != 2) {
				entry.fadeIn(0.8f, 0.5f);
			} else {
				entry.fadeIn(1, 0.5f);
			}
		}
		
		return entry;
	}
	
	public void clear() {
		for (int i = 0; i  < branches.getCountEntries(); ++i) {
			branches.getEntry(i).setReceiveTouchEvents(false);
			branches.getEntry(i).fadeOut(0, OverviewGUI.FADE_TIME);
		}
		
		if (entryLord != null) {
			entryLord.setReceiveTouchEvents(false);
			entryLord.destroy();
		}
		
		if (entryPlayer != null) {
			entryPlayer.setReceiveTouchEvents(false);
			entryPlayer.destroy();
		}
		branches.clear();
		
		for (int i = 0; i < reps.size(); ++i) {
			reps.get(i).destroy();
		}
		
		for (int i = 0; i < allies.size(); ++i) {
			allies.get(i).destroy();
		}
		
		allies.clear();
		
		reps.clear();
	}

	@Override
	public void update() {
		houseName.setText("House " + displayed.getName());
		if (World.getInstance().getPlayerController().getHouse() != displayed) {
			int favor = (int) World.getInstance().getPlayerController().getHouse().getHouseStat(displayed, House.FAVOR_STAT);
			favorLabel.setText("Favor: " + Util.getFlaggedText(String.valueOf(favor), favor > 0));
		} else {
			favorLabel.setText("");
		}
		
		if (displayed.isIndependend()) {
			hegemonLabel.setText(
				"<img SIGILS1 " + displayed.getHegemon().getName() + "> " +
				"Influence " + (int)displayed.getInfluenced(World.getInstance().getPlayerController().getHouse()) +
				" (" + displayed.getInfluenced(displayed.getHegemon()) + ")"
			);
		}
		
		for (int i = 0; i < House.MAX_REPUTATIONS; ++i) {
			if (displayed.activeReputations[i] != null) {
				
				Label tooltip = (Label) WindowManager.getInstance().getWindow(parent.getName() + "/REPUTATION" + i + "/TooltipText");
				if (tooltip != null) {
					tooltip.setText(displayed.activeReputations[i].getTooltip(displayed));
				}

			}
		}
	}
	

}
