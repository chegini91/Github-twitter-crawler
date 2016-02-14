package com.hafsang;
import java.util.HashSet;
import java.util.Set;

public class GitHubPerson extends Person {

	Set<GitHubPerson> favPersonByProject = new HashSet<>();

	public GitHubPerson(String id) {
		this.id = id;
	}
}
