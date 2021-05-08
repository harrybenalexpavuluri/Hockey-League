/*
 * Author: RajKumar B00849566
 */
package group11.Hockey.InputOutput.JsonParsing;

import org.json.simple.JSONObject;

import group11.Hockey.BusinessLogic.models.ILeague;

public interface IParseRootElement {
	public void parseRootElement(ILeague leagueModelObj, JSONObject jsonObject) throws Exception; 
}
