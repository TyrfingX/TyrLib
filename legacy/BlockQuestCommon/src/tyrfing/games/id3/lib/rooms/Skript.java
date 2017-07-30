package tyrfing.games.id3.lib.rooms;

import tyrfing.common.game.objects.IUpdateable;
import tyrfing.games.id3.lib.rooms.content.Monster;

/**
 * Class for special crawls
 * @author Sascha
 *
 */

public interface Skript extends IUpdateable {
	public void onSpawnRoom(Room room);
	public void onClearRoom(Room room);
	public void onClearRow();
	public void onMobDies(Monster monster);
	public void onFinishFloor();
}
