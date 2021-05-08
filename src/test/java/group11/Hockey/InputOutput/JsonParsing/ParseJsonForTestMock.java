/*
 * Author: RajKumar B00849566
 */
package group11.Hockey.InputOutput.JsonParsing;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import group11.Hockey.BusinessLogic.DefaultHockeyFactory;

public class ParseJsonForTestMock implements IParseJsonForTestMock {
	public JSONObject parseJson() throws FileNotFoundException, IOException, ParseException {
		String fileName = ParseRootcoachesTest.class.getClassLoader().getResource("LeagueNew.json").getPath();
		JSONParser parser = DefaultHockeyFactory.makeJSONParser();
		Object fileObj = parser.parse(new FileReader(fileName));
		JSONObject jsonObject = (JSONObject) fileObj;
		return jsonObject;
	}
}
