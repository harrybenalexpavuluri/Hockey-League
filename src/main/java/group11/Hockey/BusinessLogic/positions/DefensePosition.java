/*
 * Author: RajKumar B00849566
 */
package group11.Hockey.BusinessLogic.positions;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import group11.Hockey.BusinessLogic.BusinessConstants;
import group11.Hockey.BusinessLogic.PlayerStrength.IPlayerStrengthStrategy;
import group11.Hockey.BusinessLogic.models.IPlayer;

public class DefensePosition implements IPlayerStrengthStrategy {
	private IPlayer player;
	private static Logger logger = LogManager.getLogger(DefensePosition.class);

	public DefensePosition(IPlayer player) {
		super();
		this.player = player;
	}

	@Override
	public float claculateStrength() {
		logger.debug("Entered claculateStrength()");
		int reduceStrengthBy = BusinessConstants.Reduce_strength_by.getIntValue();
		float playerStrength = player.getSkating() + player.getChecking() + (player.getShooting() / reduceStrengthBy);
		if (player.isInjured()) {
			logger.debug("Player strength is reduced by " + reduceStrengthBy);
			playerStrength = playerStrength / reduceStrengthBy;
		}
		return playerStrength;
	}

}
