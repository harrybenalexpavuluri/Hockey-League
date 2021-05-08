package group11.Hockey;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import group11.Hockey.BusinessLogic.DefaultHockeyFactory;
import group11.Hockey.BusinessLogic.IGlobalExceptionHandler;
import group11.Hockey.BusinessLogic.StateMachineState;
import group11.Hockey.InputOutput.ICommandLineInput;
import group11.Hockey.InputOutput.IDisplay;
import group11.Hockey.db.League.ILeagueDb;

public class App {


	public static void main(String[] args) {
		IGlobalExceptionHandler globalExceptionHandler = DefaultHockeyFactory.makeGlobalExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((UncaughtExceptionHandler) globalExceptionHandler);
        PropertyConfigurator.configure(App.class.getClassLoader().getResource("log4j.xml"));
        Logger logger = LogManager.getLogger(App.class);
		ILeagueDb leagueDb = DefaultHockeyFactory.makeLeagueSerialisation();
		ICommandLineInput commandLineInput = DefaultHockeyFactory.makeCommandLineInput();
		IDisplay display = DefaultHockeyFactory.makeDisplay();
		display.showMessageOnConsole("Welcome to Hockey Simulation!");
		StateMachineState currentState = null;
		try {
			if (args.length > 0) {
				String jsonFile = args[0];
				logger.info("Json provided");
				currentState = DefaultHockeyFactory.getJsonImport(jsonFile, commandLineInput, leagueDb, display);

			} else {
				logger.info("No Json provided");
				currentState = DefaultHockeyFactory.makeLoadTeam(commandLineInput, leagueDb);
			}
		} catch (Exception e) {
			logger.warn("Exception caught : "+e);
			System.exit(0);
		}
		do {
			currentState = currentState.startState();
		}while(currentState.ShouldContinue());

	}
}