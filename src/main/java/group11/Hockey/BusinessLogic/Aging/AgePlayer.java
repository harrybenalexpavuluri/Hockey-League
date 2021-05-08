/*
 * Author: RajKumar B00849566
 */
package group11.Hockey.BusinessLogic.Aging;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import group11.Hockey.BusinessLogic.DefaultHockeyFactory;
import group11.Hockey.BusinessLogic.StateMachineState;
import group11.Hockey.BusinessLogic.LeagueSimulation.Interfaces.IParse;
import group11.Hockey.BusinessLogic.Validations.IValidations;
import group11.Hockey.BusinessLogic.models.IConference;
import group11.Hockey.BusinessLogic.models.IDivision;
import group11.Hockey.BusinessLogic.models.ILeague;
import group11.Hockey.BusinessLogic.models.IPlayer;
import group11.Hockey.BusinessLogic.models.ITeam;
import group11.Hockey.BusinessLogic.models.ITimeLine;
import group11.Hockey.BusinessLogic.models.Player;
import group11.Hockey.InputOutput.ICommandLineInput;
import group11.Hockey.InputOutput.IDisplay;
import group11.Hockey.db.League.ILeagueDb;

public class AgePlayer extends RetirePlayer implements IAgePlayer {

	ILeague league;
	int days;
	ILeagueDb leagueDb;
	IDisplay display;
	private ICommandLineInput commandLineInput;
	private IValidations validation;
	private static Logger logger = LogManager.getLogger(AgePlayer.class);

	public AgePlayer() {

	}

	public AgePlayer(ILeague league, int days, IDisplay display) {
		this.league = league;
		this.days = days;
		this.display = display;
	}

	public AgePlayer(ILeague league, int days, ILeagueDb leagueDb, IDisplay display) {
		this.league = league;
		this.days = days;
		this.leagueDb = leagueDb;
		this.display = display;
	}

	public AgePlayer(ILeague league, ILeagueDb leagueDb, IDisplay display, ICommandLineInput commandLineInput,
			IValidations validation) {
		this.league = league;
		this.leagueDb = leagueDb;
		this.display = display;
		this.commandLineInput = commandLineInput;
		this.validation = validation;
	}

	@Override
	public StateMachineState startState() {
		agePlayers();
		IParse parse = DefaultHockeyFactory.makeParse();
		ITimeLine timeLine = league.getTimeLine();
		String currentDate = timeLine.getCurrentDate();
		Date stanleyEndDateTime = timeLine.getStanleyEndDateTime();
		List<ITeam> qualifiedTeams = league.getQualifiedTeams();
		Date dateTime = parse.stringToDate(currentDate);
		if ((dateTime.equals(stanleyEndDateTime)) || (qualifiedTeams.size() == 1)) {
			logger.debug("Move to DraftPlayer State");
			return DefaultHockeyFactory.makeDraftPlayer(league, leagueDb, display);
		} else {
			logger.debug("Date is not end of stanley playoffs");
			return DefaultHockeyFactory.makeAdvanceTime(league, leagueDb, display, commandLineInput, validation);
		}
	}

	@Override
	public void agePlayers() {
		IParse parse = DefaultHockeyFactory.makeParse();
		Date currentDate = parse.stringToDate(league.getTimeLine().getCurrentDate());
		int ageDays;
		List<Player> freeAgents = (List<Player>) league.getFreeAgents();
		List<IConference> conferences = league.getConferences();
		Calendar currentDayCalender = Calendar.getInstance();
		Calendar birthDayCalender = Calendar.getInstance();
		currentDayCalender.setTime(currentDate);
		boolean dayCheck;
		float statDecayChance = league.getGamePlayConfig().getAging().getStatDecayChance();
		if (freeAgents.size() > 0) {
			for (IPlayer freeAgent : freeAgents) {
				Date playerBirthDate = getPlayerBirthDate(freeAgent, parse);
				birthDayCalender.setTime(playerBirthDate);
				dayCheck = (currentDayCalender.get(Calendar.MONTH) == birthDayCalender.get(Calendar.MONTH)
						&& currentDayCalender.get(Calendar.DAY_OF_MONTH) == birthDayCalender
								.get(Calendar.DAY_OF_MONTH));
				if (dayCheck) {
					ageDays = (int) ((currentDate.getTime() - playerBirthDate.getTime()) / (24 * 60 * 60 * 1000));
					long ageYear = ageDays / (365);
					freeAgent.setAge(ageYear);
					freeAgent.increaseAge(league, days, statDecayChance);
				} else {
					freeAgent.decreaseInjuredDaysForPlayer(days);
				}
			}
		}

		if (conferences.size() > 0) {
			for (IConference conference : conferences) {
				List<IDivision> divisions = conference.getDivisions();
				if (divisions.size() > 0) {
					for (IDivision division : divisions) {
						List<ITeam> teams = division.getTeams();
						if (teams.size() > 0) {
							for (ITeam team : teams) {
								List<IPlayer> players = team.getPlayers();
								if (players.size() > 0) {
									for (IPlayer player : players) {
										Date playerBirthDate = getPlayerBirthDate(player, parse);
										birthDayCalender.setTime(playerBirthDate);
										dayCheck = (currentDayCalender.get(Calendar.MONTH) == birthDayCalender
												.get(Calendar.MONTH)
												&& currentDayCalender.get(Calendar.DAY_OF_MONTH) == birthDayCalender
														.get(Calendar.DAY_OF_MONTH));
										if (dayCheck) {
											ageDays = (int) ((currentDate.getTime() - playerBirthDate.getTime())
													/ (24 * 60 * 60 * 1000));
											long ageYear = ageDays / (365);
											player.setAge(ageYear);
											player.increaseAge(league, days, statDecayChance);
										} else {
											player.decreaseInjuredDaysForPlayer(days);
										}

									}
								}
							}
						}
					}
				}
			}
		}
		checkForRetirement(league);
	}

	public void checkForRetirement(ILeague league) {
		boolean isRetired;
		List<IPlayer> retiredPlayers = new ArrayList<>();
		List<Player> freeAgents = (List<Player>) league.getFreeAgents();
		List<IConference> conferences = league.getConferences();

		Iterator<Player> freeAgentsItr = freeAgents.iterator();

		while (freeAgentsItr.hasNext()) {
			Player freeAgent = freeAgentsItr.next();
			isRetired = freeAgent.isIsRetired();
			if (isRetired) {
				retiredPlayers.add(freeAgent);
				freeAgentsItr.remove();
			}
		}

		if (conferences.size() > 0) {
			for (IConference conference : conferences) {
				List<IDivision> divisions = conference.getDivisions();
				if (divisions.size() > 0) {
					for (IDivision division : divisions) {
						List<ITeam> teams = division.getTeams();
						if (teams.size() > 0) {
							for (ITeam team : teams) {
								List<IPlayer> players = team.getPlayers();
								if (players.size() > 0) {
									for (IPlayer player : players) {
										isRetired = player.isIsRetired();
										if (isRetired) {
											retiredPlayers.add(player);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		List<IPlayer> existingRetiredPlayers = league.getRetiredPlayers();
		existingRetiredPlayers.addAll(retiredPlayers);
		league.setRetiredPlayers(existingRetiredPlayers);
		retireAndReplacePlayer(league);
	}

	public Date getPlayerBirthDate(IPlayer player, IParse parse) {

		int playerBirthYear = player.getBirthYear();
		int playerBirthMonth = player.getBirthMonth();
		int playerBirthDay = player.getBirthDay();
		String birthDate = Integer.toString(playerBirthDay) + "/" + Integer.toString(playerBirthMonth) + "/"
				+ Integer.toString(playerBirthYear);
		Date playerBirthDate = parse.stringToDate(birthDate);
		return playerBirthDate;

	}

}
