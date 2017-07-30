package com.tyrfing.games.id17.gui.mails;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.gui.DateGUI;
import com.tyrfing.games.id17.gui.TabGUI;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.gui.IEventListener;
import com.tyrlib2.gui.ImageBox;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowEvent;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.input.IMotionEvent;
import com.tyrlib2.math.Vector2;

public class MailboxGUI {
	
	public static final ScaledVector2 MAILBOX_SIZE = DateGUI.FAST_FORWARD_BUTTON_SIZE.multiply(1.25f);
	public static final Vector2 MAILBOX_POS = new Vector2(1, TabGUI.SIGIL_POS.y-0.08f);
	public static final ScaledVector2 MAILBOX_OFFSET = new ScaledVector2(-MAILBOX_SIZE.x, 0);
	
	public static final ScaledVector2 INFO_LABEL_POS = new ScaledVector2(0.04f, 0.04f, 3);
	public static final int MAX_TOOLTIP_LENGTH = 120;
	
	private int current = 0;
	private Mail currentMail;
	
	private List<Mail> mails = new ArrayList<Mail>();
	
	public MailboxGUI() {
		
	}
	
	public int size() {
		return mails.size();
	}
	
	public Mail getCurrentMail() {
		return currentMail;
	}
	
	public Mail getMail(int index) {
		return mails.get(index);
	}
	
	public void showMail(int index) {
		if (currentMail != null) {
			currentMail.hide();
		}
		
		current = index;
		currentMail = mails.get(current);
		mails.remove(current);
		currentMail.show();
	}
	
	public boolean showIdentity(String identity) {
		if (	currentMail != null && currentMail.getIdentity() != null
			&&	currentMail.getIdentity().equals(identity)) return true;
		
		for (int i = 0; i < World.getInstance().getMainGUI().mailboxGUI.size(); ++i) {
			Mail m = mails.get(i);
			if (	m.getIdentity() != null
				&&	m.getIdentity().equals(identity)) {
				showMail(i);
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isIdentityPrefixShown(String prefix) {
		if (	currentMail != null && currentMail.getIdentity() != null
			&&	currentMail.getIdentity().startsWith(prefix)) return true;
		
		return false;
	}
	
	public void addMail(Mail mail, boolean front) {
		mail.setMailbox(this);
		mail.setAlpha(0);
		mail.setVisible(false);
		
		if (front) {
			if (currentMail != null) {
				currentMail.hide();
			}
			
			current = 0;
			mail.show();
			currentMail = mail;
		} else {
			addMailIcon(mail);
			mails.add(mail);
		}
	}
	
	private void addMailIcon(final Mail mail) {
		mail.mailHolder = (ImageBox) WindowManager.getInstance().createImageBox(mail.getName() + "/MAILBOX", MAILBOX_POS.add(new Vector2(0,MAILBOX_SIZE.get().y*mails.size())), "MAIL_ICONS", mail.iconName, MAILBOX_SIZE.get());
		mail.mailHolder.setReceiveTouchEvents(true);
		WindowManager.getInstance().addTextTooltip(mail.mailHolder, mail.getTooltipText());
		
		mail.mailHolder.addEventListener(WindowEventType.TOUCH_UP, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				IMotionEvent e = (IMotionEvent) event.getParam("MOTIONEVENT");
				if (e.getButton() == 0) {
					if (currentMail != null) {
						currentMail.hide();
					}
					currentMail = mail;
					mail.show();
				}else {
					removeMail(mail);
				}
			}
		});
		
		Window infoIcon = (ImageBox) WindowManager.getInstance().createImageBox(mail.getName() + "MAILBOX/INFO_ICON", new Vector2(), "MAIN_GUI", "BIG_CIRCLE_BORDER", MAILBOX_SIZE.get());
		mail.mailHolder.addChild(infoIcon);
		
		mail.mailHolder.moveBy(MAILBOX_OFFSET.get(), 0.5f);
	}
	
	public void removeMail() {
		if (mails.size() > 0) {
			removeMail(mails.get(0));
		}
	}
	
	public void removeMail(Mail mail) {
		int index = mails.indexOf(mail);
		
		if (index != -1) {
			
			for (int i = index + 1; i < mails.size(); ++i) {
				Mail other = mails.get(i);
				if (other.mailHolder != null) {
					other.mailHolder.moveTo(new Vector2(MAILBOX_POS.x+MAILBOX_OFFSET.get().x,MAILBOX_POS.y+MAILBOX_SIZE.get().y*(i-1)), 0.5f);
				}
			}
			
			mails.remove(index);
		}
		
		if (mail == currentMail) {
			currentMail = null;
		}
		
		if (current > 0 && index >= current) {
			current--;
		} 
		
		if (mail.mailHolder != null) {
			WindowManager.getInstance().destroyWindow(mail.mailHolder);
		}
		
	}
	
	public void hideMail(Mail mail) {
		if (currentMail == mail) {
			if (current >= mails.size()) {
				current = 0;
			} 
			
			currentMail = null;
		} 
	}

	public void removeMailByPrefix(String prefix) {
		
		List<Mail> fits = new ArrayList<Mail>();
		
		if (	currentMail != null && currentMail.getIdentity() != null
			&&	currentMail.getIdentity().startsWith(prefix)) fits.add(currentMail);
		
		for (int i = 0; i < size(); ++i) {
			Mail m = mails.get(i);
			if (	m.getIdentity() != null
				&&	m.getIdentity().startsWith(prefix)) {
				fits.add(m);
			}
		}
		
		for (int i = 0; i < fits.size(); ++i) {
			fits.get(i).hide();
			removeMail(fits.get(i));
		}
		
	}
}
