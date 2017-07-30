package com.tyrfing.games.id17.diplomacy.actions;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.gui.mails.DiploYesNoMail;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.Loan;
import com.tyrfing.games.id17.networking.LoanChange;
import com.tyrfing.games.id17.war.WarJustification;

public class PayLoan extends DiploAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6828058330510351619L;

	public static final int DISHONOR = 50;
	
	public static final int ID = 8;
	
	public PayLoan() {
		super("Pay Loan", ID, 0, true);
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return sender.getLoan(receiver) != null;
	}

	@Override
	public void execute(House sender, House receiver, int[] options) {
		Loan loan = sender.getLoan(receiver);
		if (loan != null) {
			if (options[0] > 0) {
				float loanSize = loan.loanSize;
				sender.changeGold(-loanSize);
				receiver.changeGold(loanSize);
			} else {
				sender.changeHonor(-DISHONOR);
				receiver.addJustification(new WarJustification("Confiscation", null, sender, receiver));
			}
			
			sender.removeLoan(loan);
			receiver.removeLoan(loan);
			
			if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
				EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new LoanChange(loan, LoanChange.REMOVE));
			}
		}
	}
	
	@Override
	public Mail getOptionMail(House sender, House receiver) {
		Loan loan = sender.getLoan(receiver);
		if (loan == null) return null;
		int loanSize = loan.loanSize;
		return new DiploYesNoMail("Obligation: Payback loan", "Our loan has expired. We are to pay\n<#ff0000>" +  loanSize + "<img MAIN_GUI GOLD_ICON>\\#\nWe can choose to annull the loan.\nThis would be dishonorable.", this, sender, receiver);
	}
	
	@Override
	public Mail getSendMail(Message message) {
		if (message.options[0] > 0) {
			return new HeaderedMail("Obligation: Payback loan", "House of "  + message.sender.getLinkedName() + " has honored\ntheir obligations and payed their loan."  , message.sender, message.receiver);
		} else {
			return new HeaderedMail("Obligation: Payback loan", "House of "  + message.sender.getLinkedName() + " has\ndishonored their obligations to pay their loan!\n\nWe gain: Confiscation War Justification"  , message.sender, message.receiver);
		}
	}

}
