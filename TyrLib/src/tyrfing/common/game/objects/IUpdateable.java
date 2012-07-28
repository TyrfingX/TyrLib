package tyrfing.common.game.objects;

public interface IUpdateable {
	public void onUpdate(float time);
	public boolean isFinished();
}
