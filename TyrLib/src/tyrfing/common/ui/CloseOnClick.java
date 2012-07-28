package tyrfing.common.ui;

public class CloseOnClick implements ClickListener {

	@Override
	public void onClick(Event event) {
		event.getEvoker().destroy();
	}

}
