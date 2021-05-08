/*
 * Author: RajKumar B00849566
 */
package group11.Hockey.InputOutput.JsonParsing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import group11.Hockey.BusinessLogic.DefaultHockeyFactory;
import group11.Hockey.BusinessLogic.models.ICoach;
import group11.Hockey.BusinessLogic.models.League;
import group11.Hockey.InputOutput.DefaultInputOutputFactory;

import org.junit.Assert;
import org.junit.BeforeClass;

public class ParseRootcoachesTest {

	private static JSONObject jsonObject;

	@BeforeClass
	public static void init() throws FileNotFoundException, IOException, ParseException {
		IParseJsonForTestMock parseJsonObj = DefaultParseTestFactory.makeParseJsonForTestMock();
		jsonObject = parseJsonObj.parseJson();
	}

	@Test
	public void parseRootElementTest() throws Exception {
		League league = DefaultHockeyFactory.makeLeague();
		IParseRootElement parseRootcoaches = DefaultInputOutputFactory.makeParseRootcoaches();
		parseRootcoaches.parseRootElement(league, jsonObject);
		List<ICoach> lc = league.getCoaches();
		Assert.assertEquals(lc.size(), 50);

	}

}
