package group11.Hockey.BusinessLogic.Validations;

import java.util.ArrayList;
import java.util.List;

import group11.Hockey.BusinessLogic.BusinessConstants;
import group11.Hockey.BusinessLogic.DefaultHockeyFactory;
import group11.Hockey.BusinessLogic.Enums.Positions;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import group11.Hockey.BusinessLogic.models.IConference;
import group11.Hockey.BusinessLogic.models.IGeneralManager;
import group11.Hockey.BusinessLogic.models.ILeague;
import group11.Hockey.BusinessLogic.models.IPlayer;
import group11.Hockey.BusinessLogic.models.ITeam;
import group11.Hockey.BusinessLogic.models.Player;
import group11.Hockey.InputOutput.ICommandLineInput;
import group11.Hockey.InputOutput.IDisplay;
/*
 * Jatin Rana And Jigar
 */
public class UserInputCheck implements IUserInputCheck {
	private ICommandLineInput commandLineInput;
	IValidations validation;
	IDisplay display;
	private static Logger logger = LogManager.getLogger(UserInputCheck.class);

	public UserInputCheck(ICommandLineInput commandLineInput, IValidations validation, IDisplay display) {
		logger.debug("Entered UserInputCheck()");
		this.commandLineInput = commandLineInput;
		this.validation = validation;
		this.display = display;
	}

	@Override
	public String conferenceNameFromUserCheck(List<IConference> conferencesList) {
		logger.debug("Entered conferenceNameFromUserCheck()");
		boolean checkConferenceName = true;
		String conferenceName = null;
		while (checkConferenceName) {
			display.showMessageOnConsole(BusinessConstants.Enter_Conference_Name.getValue().toString());
			conferenceName = commandLineInput.getValueFromUser();
			checkConferenceName = validation.isConferenceNameValid(conferenceName, conferencesList);
		}
		return conferenceName;
	}

	@Override
	public String divisonNameFromUserCheck(IConference conference) {
		logger.debug("Entered divisonNameFromUserCheck()");
		boolean checkDiviosneName = true;
		String divisionName = null;
		while (checkDiviosneName) {
			display.showMessageOnConsole(BusinessConstants.Enter_Division_Name.getValue().toString());
			divisionName = commandLineInput.getValueFromUser();
			checkDiviosneName = validation.isDivisionValid(divisionName, conference);
		}
		return divisionName;

	}

	@Override
	public void teamNameFromUserCheck(ITeam newTeam, ILeague league) {
		logger.debug("Entered teamNameFromUserCheck()");
		boolean checkTeamName = true;
		String teamName = null;
		while (checkTeamName) {
			display.showMessageOnConsole(BusinessConstants.Enter_Team_Name.getValue().toString());
			teamName = commandLineInput.getValueFromUser();
			checkTeamName = validation.isTeamNameValid(teamName, league);
		}
			newTeam.setTeamName(teamName);
			newTeam.setUserTeam(true);
	}

	@Override
	public void generalManagerNameFromUserCheck(ITeam newTeam, ILeague league) {
		logger.debug("Entered generalManagerNameFromUserCheck()");
		boolean checkManagerName = true;
		String generalManager = null;

		while (checkManagerName) {
			display.showMessageOnConsole(BusinessConstants.Enter_General_Manger_Name.getValue().toString());
			generalManager = commandLineInput.getValueFromUser();
			checkManagerName = validation.generalManagerNameCheck(generalManager, league);
		}

		IGeneralManager gmObj = DefaultHockeyFactory.makeGeneralManager(generalManager,
				BusinessConstants.Personality.getValue().toString());
		newTeam.addGeneralMangerToTeam(newTeam, gmObj, league);

	}

	@Override
	public void headCoachNameFromUserCheck(ITeam newTeam, ILeague league) {
		logger.debug("Entered headCoachNameFromUserCheck()");
		boolean checkHeadCoachName = true;
		String headCoach = null;
		while (checkHeadCoachName) {
			display.showMessageOnConsole(BusinessConstants.Enter_headCoach_Name.getValue().toString());
			headCoach = commandLineInput.getValueFromUser();
			checkHeadCoachName = validation.headCoachNameCheck(headCoach, league);
		}
			newTeam.addCoachToTeam(newTeam, headCoach, league);

	}

	@Override
	public void playerChoiceFromUser(ITeam newTeam, ILeague league) {
		logger.debug("Entered playerChoiceFromUser()");
		boolean playerValueCheck = true;
		List<Integer> selectedValuesFromUser = new ArrayList<Integer>();
		List<IPlayer> forwardList = new ArrayList<>();
		List<IPlayer> defenseList = new ArrayList<>();
		List<IPlayer> goalies = new ArrayList<>();
		IPlayer player = DefaultHockeyFactory.makePlayer();
		String playerValue;
		display.showMessageOnConsole(BusinessConstants.Select_Player.getValue().toString());
		for (int i = 0; i < Integer.parseInt(BusinessConstants.Number_Of_Total_Players.getValue().toString()); i++) {
			while (playerValueCheck) {
				display.showMessageOnConsole("Select " + (i + 1) + " player");
				playerValue = commandLineInput.getValueFromUser();
				playerValueCheck = validation.playerCheck(playerValue, league, selectedValuesFromUser, forwardList,
						defenseList, goalies);
				if (playerValueCheck == false) {
					String postion = league.getFreeAgents().get(Integer.parseInt(playerValue) - 1).getPosition();
					if (postion.equalsIgnoreCase(Positions.FORWARD.toString())) {
						forwardList.add((Player) league.getFreeAgents().get(Integer.parseInt(playerValue) - 1));
					} else if (postion.equalsIgnoreCase(Positions.DEFENSE.toString())) {
						defenseList.add((Player) league.getFreeAgents().get(Integer.parseInt(playerValue) - 1));
					} else if (postion.equalsIgnoreCase(Positions.GOALIE.toString())) {
						goalies.add((Player) league.getFreeAgents().get(Integer.parseInt(playerValue) - 1));
					}
				}
			}
			playerValueCheck = true;
		}

		List<IPlayer> finalListOfPlayers = new ArrayList<>();
		finalListOfPlayers.addAll(forwardList);
		finalListOfPlayers.addAll(defenseList);
		finalListOfPlayers.addAll(goalies);
		newTeam.setPlayers(finalListOfPlayers);
		player.removeFreeAgentsFromLeague(league, finalListOfPlayers);
	}

	/**
	 * The below method takes and validates the trade related input provided by User
	 * User gives input to respond the trade offered by AI
	 *
	 * @author Jigar Makwana B00842568
	 */

	@Override
	public int validateUserTradeInput() {
		logger.debug("Entered validateUserTradeInput()");
		boolean isValidInput = false;
		int userInput = -1;
		do {
			while (userInput == -1) {
				try {
					userInput = commandLineInput.getInt();
				} catch (Exception e) {
					logger.error("Error occured : " + e);
					display.showMessageOnConsole("Please enter 0 or 1");
				}
			}
			if (validation.isUserTradeInputValid(userInput)) {
				logger.debug("Trade value is valid");
				break;
			} else {
				logger.error("Invalid value given");
				display.showMessageOnConsole("Please enter 0 or 1");
				userInput = -1;
			}
		} while (isValidInput == false);
		return userInput;
	}

	/**
	 * The below method takes and validates the trade related User Inputs when
	 * User gives input to select players to add or drop to/from a team
	 *
	 * @author Jigar Makwana B00842568
	 */

	@Override
	public int userResolveRosterInput(int listSize) {
		logger.debug("Entered userResolveRosterInput()");
		boolean isValidInput = false;
		int userInput = 0;
		do {
			while (userInput <= 0) {
				try {
					userInput = commandLineInput.getInt();
				} catch (Exception e) {
					logger.error("Error occured : " + e);
					display.showMessageOnConsole("Please select valid value");
				}
			}
			if (validation.isUserResolveRosterInputValid(userInput, listSize)) {
				logger.debug("Roster value is valid");
				break;
			} else {
				logger.error("Invalid value selected");
				display.showMessageOnConsole("Please select valid value");
				isValidInput = false;
				userInput = 0;
			}
		} while (isValidInput == false);
		return userInput;
	}
}
