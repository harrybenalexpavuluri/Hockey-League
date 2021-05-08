package group11.Hockey.BusinessLogic.models;

import java.util.List;

public interface IDivision {

	public boolean isDivisionNameValid(String divisionName, List<IDivision> divisionList);

	public Division getDivisionFromDivisionName(String divisionName, List<IDivision> list);

	public void addNewTeamInDivision(ITeam newTeam);

	public List<ITeam> getTeams();

	public void setTeams(List<ITeam> teams);

	public String getDivisionName();

	public void setDivisionName(String name);
}
