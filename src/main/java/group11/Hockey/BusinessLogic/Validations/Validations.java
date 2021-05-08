package group11.Hockey.BusinessLogic.Validations;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import group11.Hockey.BusinessLogic.BusinessConstants;
import group11.Hockey.BusinessLogic.DefaultHockeyFactory;
import group11.Hockey.BusinessLogic.models.ICoach;
import group11.Hockey.BusinessLogic.models.IConference;
import group11.Hockey.BusinessLogic.models.IDivision;
import group11.Hockey.BusinessLogic.models.IGeneralManager;
import group11.Hockey.BusinessLogic.models.ILeague;
import group11.Hockey.BusinessLogic.models.IPlayer;
import group11.Hockey.BusinessLogic.models.ITeam;
import group11.Hockey.InputOutput.IDisplay;
/**
 *
 * @author Jatin Partap Rana and Jigar
 *
 */
public class Validations implements IValidations {

	IConference conferenceObj = DefaultHockeyFactory.makeConference();
	IDivision divisionObj = DefaultHockeyFactory.makeDivision();
	IDisplay display;
	private static Logger logger = LogManager.getLogger(Validations.class);

	private static Validations validationsInstance = null;

	public Validations(IDisplay display) {
		this.display = display;
	}

	private Validations() {

	}

	public static Validations getInstance() {
		if (validationsInstance == null) {
			validationsInstance = new Validations();
		}
		return validationsInstance;
	}

	public boolean isConferenceNameValid(String conferenceName, List<IConference> conferencesList) {
		logger.debug("Entered isConferenceNameValid()");
		boolean conferenceNameCheck = false;
		if (isStrBlank(conferenceName)) {
			conferenceNameCheck = true;
		}
		if (conferenceObj.isConferenceNameValid(conferenceName, conferencesList)) {
			conferenceNameCheck = false;
		} else {
			display.showMessageOnConsole("Conference name does not exist, Please enter valid conference name");
			conferenceNameCheck = true;
		}
		return conferenceNameCheck;
	}

	public boolean isDivisionValid(String divisionName, IConference conferenceItem) {
		logger.debug("Entered isDivisionValid()");
		boolean divisionNameCheck = false;
		if (isStrBlank(divisionName)) {
			divisionNameCheck = true;
		}
		if (divisionObj.isDivisionNameValid(divisionName, conferenceItem.getDivisions())) {
			divisionNameCheck = false;
		} else {
			display.showMessageOnConsole("Division name does not exist, Please enter valid division name");
			divisionNameCheck = true;
		}
		return divisionNameCheck;
	}

	public boolean isTeamNameValid(String teamName, ILeague league) {
		logger.debug("Entered isTeamNameValid()");
		ITeam team = DefaultHockeyFactory.makeTeam();
		if (isStrBlank(teamName)) {
			return true;
		} else if (team.isTeamNameValid(teamName, league)) {
			return false;
		}
		display.showMessageOnConsole("Team name already exists in this League");
		return true;
	}

	public boolean generalManagerNameCheck(String name, ILeague league) {
		logger.debug("Entered generalManagerNameCheck()");
		if (isStrBlank(name)) {
			return true;
		} else {
			List<IGeneralManager> generalMangerList = league.getGeneralManagers();
			for (IGeneralManager generalManger : generalMangerList) {
				if (generalManger.getName().equalsIgnoreCase(name)) {
					return false;
				}
			}
			display.showMessageOnConsole("Enter valid general manager name");
			return true;
		}
	}

	public boolean headCoachNameCheck(String coachName, ILeague league) {
		logger.debug("Entered headCoachNameCheck()");
		boolean coachNameCheck = true;
		if (isStrBlank(coachName)) {
			coachNameCheck = true;
		} else {
			List<ICoach> coachList = league.getCoaches();
			for (ICoach coach : coachList) {
				if (coach.getName().equalsIgnoreCase(coachName)) {
					coachNameCheck = false;
					return coachNameCheck;
				}
			}
		}
		return coachNameCheck;
	}

	public boolean playerCheck(String playerNumber, ILeague league, List<Integer> selectedValues, List<IPlayer> forwards,
			List<IPlayer> defense, List<IPlayer> goalies) {
		logger.debug("Entered playerCheck()");
		boolean isPlayerValueNotValid = true;
		int playerNumberInInt = 0;
		try {
			playerNumberInInt = Integer.parseInt(playerNumber);
		} catch (Exception e) {
			logger.error("Error occured : " + e);
			display.showMessageOnConsole("Select valid value");
			isPlayerValueNotValid = true;
			return isPlayerValueNotValid;
		}
		if (playerNumberInInt < 0 || playerNumberInInt > league.getFreeAgents().size()) {
			display.showMessageOnConsole("Select Valid value");
			isPlayerValueNotValid = true;
			return isPlayerValueNotValid;
		}
		String position = league.getFreeAgents().get(playerNumberInInt - 1).getPosition();
		if (selectedValues.contains(playerNumberInInt)) {
			display.showMessageOnConsole("This player is already selected");
			isPlayerValueNotValid = true;
			return isPlayerValueNotValid;
		}
		if (position.equalsIgnoreCase("forward")) {
			if (forwards.size() <= Integer.parseInt(BusinessConstants.Number_Of_Forward.getValue().toString())) {
				isPlayerValueNotValid = false;
				selectedValues.add(playerNumberInInt);
				return isPlayerValueNotValid;
			} else {
				display.showMessageOnConsole("Can not select more forward player");
			}
		}
		if (position.equalsIgnoreCase("defense")) {
			if (defense.size() <= Integer.parseInt(BusinessConstants.Number_Of_Defense.getValue().toString())) {
				isPlayerValueNotValid = false;
				selectedValues.add(playerNumberInInt);
				return isPlayerValueNotValid;
			} else {
				display.showMessageOnConsole("Can not select more defense players");
			}
		}
		else if (position.equalsIgnoreCase("goalie")) {
			if (goalies.size() <= Integer.parseInt(BusinessConstants.Number_Of_Goalies.getValue().toString())) {
				isPlayerValueNotValid = false;
				selectedValues.add(playerNumberInInt);
				return isPlayerValueNotValid;
			} else {
				display.showMessageOnConsole("Can not select more goalies");
			}
		}
		return isPlayerValueNotValid;

	}

	public boolean isStrBlank(String str) {
		logger.debug("Entered isStrBlank()");
		if (str == null || str.isEmpty() || str.split(" +").length == 0) {
			return true;
		}

		return false;
	}

	public boolean isNoOfSeasonsValueValid(String numberOfSeasons) {
		logger.debug("Entered isNoOfSeasonsValueValid()");
		boolean isNoOfSeasonsValueValid = false;
		try {
			int value = Integer.parseInt(numberOfSeasons);
			if (value < 0) {
				logger.error("Simulation input is invalid");
				display.showMessageOnConsole("Select valid value for simulation");
				isNoOfSeasonsValueValid = true;
				return isNoOfSeasonsValueValid;
			} else {
				return false;
			}
		} catch (Exception e) {
			logger.error("Error occured : " + e);
			display.showMessageOnConsole("Select valid value for simulation");
			isNoOfSeasonsValueValid = true;
			return isNoOfSeasonsValueValid;
		}
	}

	/**
	 * @author Jigar Makwana B00842568
	 */
	public boolean isUserTradeInputValid(int userInput) {
		logger.debug("Entered isUserTradeInputValid()");
		if ((userInput == 1) || (userInput == 0)) {
			logger.debug("Trade input is valid");
			return true;
		} else {
			logger.error("Trade input is invalid");
			return false;
		}
	}

	/**
	 * @author Jigar Makwana B00842568
	 */
	public boolean isUserResolveRosterInputValid(int userInput, int listSize) {
		logger.debug("Entered isUserResolveRosterInputValid()");
		if (((userInput >= 1) && (userInput <= listSize))) {
			logger.debug("Roster input is valid");
			return true;
		} else {
			logger.error("Roster input is invalid");
			return false;
		}
	}
}
