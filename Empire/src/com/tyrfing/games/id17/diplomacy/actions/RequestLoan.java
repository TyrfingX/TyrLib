package com.tyrfing.games.id17.diplomacy.actions;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.gui.mails.DiploYesNoMail;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.Loan;
import com.tyrfing.games.id17.networking.LoanChange;
import com.tyrfing.games.id17.world.World;

public class RequestLoan extends DiploAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 285151916138559537L;
	public static final int MIN_RELATIONS = 60;
	public static final int FAVOR = 20;
	
	public static final int ID = 9;
	
	public static final int WEALTHY_LOAN = 5;
	
	public RequestLoan() {
		super("Request Loan", ID, 1, true);
		disabledText = "FAILED TO UPDATE";
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return 		receiver.getGold() >= Loan.getLoanSize(sender, receiver) 
				&& 	receiver.getRelation(sender) >= MIN_RELATIONS 
				&&  sender.getLoan(receiver) == null
				&& 	sender.isUninfluenced() || sender.shareSphereOfInfluence(receiver);
	}
	
	@Override
	public void setDisabledText(House sender, House receiver) {
		Loan loan = sender.getLoan(receiver);
		if (loan == null) {
			int loanSize = Loan.getLoanSize(sender, receiver);
			disabledText = Util.getFlaggedText(" (1)", receiver.getGold() >= loanSize ) + " They have > " +  loanSize + "<img MAIN_GUI GOLD_ICON>\n";
			disabledText += Util.getFlaggedText(" (2)", receiver.getRelation(sender) >= MIN_RELATIONS ) + " Relations > " + MIN_RELATIONS + "\n";
			disabledText += Util.getFlaggedText(" (3)", sender.isUninfluenced() || sender.shareSphereOfInfluence(receiver) ) + " Either of the following\n";
			disabledText += Util.getFlaggedText("  (-)", sender.isUninfluenced() ) + " We have no Hegemon\n";
			disabledText += Util.getFlaggedText("  (-)", sender.shareSphereOfInfluence(receiver) ) + " Share sphere of influence";
		}	else {
			int loanSize = loan.loanSize;
			if (loanSize > 0)  {
				disabledText = " (1) We owe them " + loanSize + "<img MAIN_GUI GOLD_ICON>\n";
			} else {
				disabledText = " (1) They owe us " + -loanSize + "<img MAIN_GUI GOLD_ICON>\n";
			}
			
			disabledText += " (2) Payback " + World.toDate(loan.endDate);
		}
	}

	@Override
	public void execute(House sender, House receiver, int[] options) {
		int loanSize = options[0];
		receiver.changeGold(-loanSize);
		sender.changeGold(loanSize);
		Loan loan = new Loan(receiver, sender, loanSize);
		sender.addLoan(loan);
		receiver.addLoan(loan);
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new LoanChange(loan, LoanChange.ADD));
		}
	}
	
	@Override
	public void respond(Message message) {
		super.respond(message);
		if (message.response > 0) {
			execute(message.sender, message.receiver, message.options);
		} 
	}
	
	@Override
	public Mail getSendMail(Message message) {
		return new DiploYesNoMail("Request: Loan", "House of " + message.sender.getLinkedName() + " requests\na loan in height of\n" +  message.options[0] + "<img MAIN_GUI GOLD_ICON>", message);
	}
	
	@Override
	public Mail getResponseMail(Message message) {
		if (message.response > 0) {
			return new HeaderedMail("Loan request granted", "Our loan request from " + message.receiver.getLinkedName() + "\nhas been granted.\n\nWe have now loaned " +  message.options[0] + "<img MAIN_GUI GOLD_ICON>", message.sender, message.receiver);
		} else {
			return new HeaderedMail("Loan request denied", "Our loan request from " + message.receiver.getLinkedName() + "\nhas been denied!", message.sender, message.receiver);
		}
	}
	
	@Override
	public Mail getOptionMail(final House sender, final House receiver) {
		final int loanSize = Loan.getLoanSize(sender, receiver);
		final int payback = Loan.getPayback(loanSize);
		final RequestLoan request = this;
		HeaderedMail mail = new HeaderedMail("Request: Loan", "We request a loan in height of " + loanSize + ".\n\nPayback: <#ff0000>" + payback + "<img MAIN_GUI GOLD_ICON>\\# in " + Loan.YEARS + " Years\nSeasonal interest: <#ff0000>" + Loan.getInterest(payback) + "<img MAIN_GUI GOLD_ICON>", sender, receiver) {
			@Override 
			public void onAccept() {
				int[] options = { loanSize }; 
				Message message = new Message(request, sender, receiver, options);
				message.action.send(message.sender, message.receiver, options);
				remove();
			}
		};
		mail.addAcceptButton();
		return mail;
	}

}
