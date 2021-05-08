package group11.Hockey.BusinessLogic.models;

import org.junit.Assert;
import org.junit.Test;

import group11.Hockey.BusinessLogic.DefaultHockeyFactory;
import group11.Hockey.db.Player.IPlayerDb;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;

public class PlayerTest {

	private static IPlayer playerWithParams;
	private static float age = 30;

	@BeforeClass
	public static void init() {
		playerWithParams = new Player(Constants.playerStats, Constants.playerStats, Constants.playerStats,
				Constants.playerStats, Constants.playerName, Constants.forward, false, false, age);
	}

	@Test
	public void setPlayerNameTest() {
		playerWithParams.setPlayerName(Constants.playerName);
		Assert.assertEquals(Constants.playerName, playerWithParams.getPlayerName());
	}

	@Test
	public void getPlayerNameTest() {
		Assert.assertEquals(Constants.playerName, playerWithParams.getPlayerName());
	}

	@Test
	public void setPositionTest() {
		playerWithParams.setPosition(Constants.forward);
		Assert.assertEquals(Constants.forward, playerWithParams.getPosition());
	}

	@Test
	public void getPositionTest() {
		Assert.assertEquals(Constants.forward, playerWithParams.getPosition());
	}

	@Test
	public void setCaptainTest() {
		Assert.assertFalse(playerWithParams.getCaptain());
	}

	@Test
	public void getCaptainTest() {
		Assert.assertFalse(playerWithParams.getCaptain());
	}

	@Test
	public void insertLeagueFreeAgentsTest() {
		IPlayerDb playerDb = mock(IPlayerDb.class);
		when(playerDb.insertLeagueFreeAgents("league", playerWithParams)).thenReturn(true);
		List<Player> listOfFreeAgents = new ArrayList<Player>();
		listOfFreeAgents.add((Player) playerWithParams);
		Player player2 = new Player("league", playerDb);
		boolean flag = player2.insertLeagueFreeAgents(listOfFreeAgents);
		Assert.assertTrue(flag);
	}

	@Test
	public void insertLeagueRetiredPlayersTest() {
		IPlayerDb playerDb = mock(IPlayerDb.class);
		when(playerDb.insertLeagueRetiredPlayers("league", playerWithParams)).thenReturn(true);
		List<Player> listOfFreeAgents = new ArrayList<Player>();
		listOfFreeAgents.add((Player) playerWithParams);
		Player player2 = new Player("league", playerDb);
		boolean flag = player2.insertLeagueRetiredPlayers(listOfFreeAgents);
		Assert.assertTrue(flag);
	}

	@Test
	public void deleteLeaguePlayersTest() {
		IPlayerDb playerDb = mock(IPlayerDb.class);
		when(playerDb.deleteLeaguePlayers("league")).thenReturn(true);
		List<Player> listOfFreeAgents = new ArrayList<Player>();
		listOfFreeAgents.add((Player) playerWithParams);
		Player player2 = new Player("league", playerDb);
		boolean flag = player2.deleteLeaguePlayers();
		Assert.assertTrue(flag);
	}

	@Test
	public void getPlayerStrengthTest() {
		float strength = playerWithParams.getPlayerStrength();
		Assert.assertEquals(strength, 25.0, 25.0);
	}

	@Test
	public void increaseAgeTest() {
		LeagueModelMock leagueModel = new LeagueModelMock();
		ILeague league = leagueModel.getLeagueInfo();
		float statDecayChance = league.getGamePlayConfig().getAging().getStatDecayChance();
		playerWithParams.increaseAge(league, 365, statDecayChance);
		Assert.assertEquals(age + 1, 31, 31);
	}
	
	@Test
	public void checkAndDecrementPlayerShootingStatTest() {
		IPlayer player = DefaultHockeyFactory.makePlayer();
		float strength = playerWithParams.getPlayerStrength();
		player.checkAndDecrementPlayerShootingStat(new Float(1.0));
		Assert.assertEquals(strength, 25.0, 25.0);
	}
	
	@Test
	public void checkAndDecrementPlayerCheckingStatTest() {
		IPlayer player = DefaultHockeyFactory.makePlayer();
		float strength = playerWithParams.getPlayerStrength();
		player.checkAndDecrementPlayerCheckingStat(new Float(1.0));
		Assert.assertEquals(strength, 25.0, 25.0);
	}
	
	@Test
	public void checkAndDecrementPlayerSkatingStatTest() {
		IPlayer player = DefaultHockeyFactory.makePlayer();
		float strength = playerWithParams.getPlayerStrength();
		player.checkAndDecrementPlayerSkatingStat(new Float(1.0));
		Assert.assertEquals(strength, 25.0, 25.0);
	}
	
	@Test
	public void checkAndDecrementPlayerSavingStatTest() {
		IPlayer player = DefaultHockeyFactory.makePlayer();
		float strength = playerWithParams.getPlayerStrength();
		player.checkAndDecrementPlayerSavingStat(new Float(1.0));
		Assert.assertEquals(strength, 25.0, 25.0);
	}

	@Test
	public void replacePlayerWithFreeAgentTest() {
		IPlayer player1 = new Player(Constants.playerStats, Constants.playerStats, Constants.playerStats,
				Constants.playerStats, Constants.playerName, Constants.forward, true, false, 50);
		IPlayer player2 = new Player(Constants.playerStats, Constants.playerStats, Constants.playerStats,
				Constants.playerStats, "Agent one", Constants.forward, true, true, 20);
		List<IPlayer> playerList = new ArrayList<>();
		playerList.add(player1);

		LeagueModelMock leagueModel = new LeagueModelMock();
		ILeague league = leagueModel.getLeagueInfo();
		List<Player> playerList2 = new ArrayList<>();
		playerList2.add((Player) player2);
		league.setFreeAgents(playerList2);

		playerWithParams.replacePlayerWithFreeAgent(league, playerList);
		Assert.assertEquals(playerList.size(), 2);
	}

	@Test
	public void removeFreeAgentsFromLeagueTest() {
		LeagueModelMock leagueModelMock = new LeagueModelMock();
		ILeague league = leagueModelMock.getLeagueInfo();
		Player player = new Player();
		Player freeAgent1 = new Player(Constants.playerStats, Constants.playerStats, Constants.playerStats,
				Constants.playerStats, Constants.playerName, Constants.forward, true, false, 50);
		Player freeAgent2 = new Player(Constants.playerStats, Constants.playerStats, Constants.playerStats,
				Constants.playerStats, "Player 2", Constants.forward, true, false, 50);
		List<IPlayer> freeAgents = new ArrayList<>();
		freeAgents.add(freeAgent1);
		freeAgents.add(freeAgent2);
		player.removeFreeAgentsFromLeague(league, freeAgents);
		Assert.assertTrue(league.getFreeAgents().size() == 28);
	}

}
