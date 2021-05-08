package group11.Hockey.BusinessLogic.Drafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import group11.Hockey.BusinessLogic.DefaultHockeyFactory;
import group11.Hockey.BusinessLogic.StateMachineState;
import group11.Hockey.BusinessLogic.Enums.RosterSize;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import group11.Hockey.BusinessLogic.models.ILeague;
import group11.Hockey.BusinessLogic.models.IPlayer;
import group11.Hockey.BusinessLogic.models.ITeam;
import group11.Hockey.BusinessLogic.models.Player;
import group11.Hockey.BusinessLogic.models.Team;
import group11.Hockey.BusinessLogic.models.Roster.Interfaces.IRosterSearch;
import group11.Hockey.InputOutput.IDisplay;
import group11.Hockey.db.League.ILeagueDb;

/**
 *
 * @author Jatin Partap Rana
 *
 */
public class DraftPlayer extends StateMachineState implements IDraftPlayer {
	ILeague league;
	ILeagueDb leagueDb;
	IDisplay display;
	private static Logger logger = LogManager.getLogger(DraftPlayer.class);

	public DraftPlayer(ILeague league, ILeagueDb leagueDb, IDisplay display) {
		this.league = league;
		this.leagueDb = leagueDb;
		this.display = display;
	}

	@Override
	public StateMachineState startState() {
		draftPlayer();
		return DefaultHockeyFactory.makeTrophy(league, leagueDb, display);
	}

	@Override
	public void draftPlayer() {
		logger.info("Entered draftPlayer");
		List<ITeam> draftingTeams = new ArrayList<>();
		List<Player> sortedPlayers = new ArrayList<>();
		int numbersOfPlayersToGenerate;
		int indexForGeneratedPlayers = 0;

		try {

			List<ITeam> playOffTeamsInReverseOrder = league.getQualifiedTeams();
			List<Team> teamsInReverseOrder = DefaultHockeyFactory.makeTeam().orderTeamsInLeagueStandings(league);
			DefaultHockeyFactory.makeTeam().sortTeam(playOffTeamsInReverseOrder);
			draftingTeams.addAll(selectTeamFromRegularSeasonStandinfo(teamsInReverseOrder));
			draftingTeams.addAll(playOffTeamsInReverseOrder);
			numbersOfPlayersToGenerate = draftingTeams.size() * 7;
			IGeneratingPlayers generatingPlayers = DefaultHockeyFactory.makeGeneratePlayer();
			logger.info("Generating players for Draft");
			List<IPlayer> generatedPlayers = generatingPlayers.generatePlayers(numbersOfPlayersToGenerate);

			for (IPlayer player : generatedPlayers) {
				sortedPlayers.add((Player) player);
			}
			Collections.sort(sortedPlayers);
			for (int round = 1; round <= 7; round++) {
				logger.info("Drafting round " + round);
				for (ITeam team : draftingTeams) {
					logger.info("Players drafted for team " + team.getTeamName());
					team.getPlayers().add(sortedPlayers.get(indexForGeneratedPlayers));
					indexForGeneratedPlayers++;
				}
			}
			logger.info("Settling teams after drafting");
			for (ITeam team : draftingTeams) {
				List<IPlayer> extraPlayers = teamSettlement(team);
				@SuppressWarnings("unchecked")
				List<IPlayer> freeAgents = (List<IPlayer>) DefaultHockeyFactory.makeLeague().getFreeAgents();
				freeAgents.addAll(extraPlayers);
			}

		} catch (Exception e) {
			logger.info("Exception occurred in drafting player :" + e.getMessage());
		}

	}

	public List<Team> selectTeamFromRegularSeasonStandinfo(List<Team> regularSeasonTeams) {
		logger.debug("Entered selectTeamFromRegularSeasonStandinfo()");
		List<Team> teamsForRegularSeason = new ArrayList<>();
		Iterator<Team> interator = regularSeasonTeams.iterator();
		for (int i = 0; i < regularSeasonTeams.size() - 16; i++) {
			teamsForRegularSeason.add(interator.next());
			interator.remove();
		}
		return teamsForRegularSeason;
	}

	public List<IPlayer> teamSettlement(ITeam team) {
		List<IPlayer> extraPlayers = new ArrayList<IPlayer>();
		List<IPlayer> players = team.getPlayers();
		if (players.size() > 30) {
			IRosterSearch playerSearch = DefaultHockeyFactory.makeRosterSearch();
			List<IPlayer> forwardPlayer = playerSearch.getForwardList(players);
			List<IPlayer> defensePlayers = playerSearch.getDefenseList(players);
			List<IPlayer> goaliePlayer = playerSearch.getGoalieList(players);
			if (forwardPlayer.size() > RosterSize.ACTIVE_FORWARD_SIZE.getNumVal()) {
				int extraForwardPlayers = forwardPlayer.size() - RosterSize.ACTIVE_FORWARD_SIZE.getNumVal();
				populateExtraPlayerList(extraPlayers, forwardPlayer, extraForwardPlayers);
			}
			if (defensePlayers.size() > RosterSize.ACTIVE_DEFENSE_SIE.getNumVal()) {
				int extraDefensePlayers = defensePlayers.size() - RosterSize.ACTIVE_DEFENSE_SIE.getNumVal();
				populateExtraPlayerList(extraPlayers, defensePlayers, extraDefensePlayers);
			}
			if (goaliePlayer.size() > RosterSize.ACTIVE_GOALIE_SIZE.getNumVal()) {
				int extraGoalies = goaliePlayer.size() - RosterSize.ACTIVE_GOALIE_SIZE.getNumVal();
				populateExtraPlayerList(extraPlayers, goaliePlayer, extraGoalies);
			}
			for (int j = 0; j < players.size(); j++) {
				for (int i = 0; i < extraPlayers.size(); i++) {
					if (players.get(j).toString().equalsIgnoreCase(extraPlayers.get(i).toString())) {
						players.remove(j);
					}
				}
			}
		}

		return extraPlayers;

	}

	public void populateExtraPlayerList(List<IPlayer> extraPlayers, List<IPlayer> playerList, int extraPlayersCount) {
		for (int i = 0; i < extraPlayersCount; i++) {
			extraPlayers.add(playerList.get(i));
		}
	}
}
