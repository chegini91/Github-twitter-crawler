package com.hafsang;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Graph is a data structure for storing github and twitter users
 * 
 * @author mohammad Chegini
 *
 */
public class Graph {
	// arraylist of twitterPersons
	ArrayList<TwitterPerson> twitterPersons = new ArrayList<>();
	// arraylist of githubPersons
	ArrayList<GitHubPerson> githubPersons = new ArrayList<>();
	// Map of twitter and github users
	Map<TwitterPerson, GitHubPerson> nodeMap = new HashMap<>();

	/**
	 * Get a github id, if it is already defined in the graph returns that, if
	 * not returns a new githubperson
	 * 
	 * @param id:
	 *            a github person id, it is a unique id for each github user
	 * @return
	 */
	public GitHubPerson returnGithubPersonInList(String id) {
		for (GitHubPerson gitHubPerson : githubPersons) {
			if (gitHubPerson.id.equals(id)) {
				return gitHubPerson;
			}
		}
		GitHubPerson temp = new GitHubPerson(id);
		this.githubPersons.add(temp);
		return temp;
	}

	/**
	 * Get a twitterPerson id, if it is already defined in the graph returns
	 * that, if not returns a new twitterPerson
	 * 
	 * @param id
	 * @return
	 */
	public TwitterPerson returnTwitterPersonID(String id) {
		for (TwitterPerson twitterPerson : twitterPersons) {
			if (twitterPerson.id.equals(id)) {
				return twitterPerson;
			}
		}
		TwitterPerson temp = new TwitterPerson(id);
		this.twitterPersons.add(temp);
		return temp;
	}

}
