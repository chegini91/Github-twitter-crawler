package com.hafsang;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * The person is father of twitterPerson and GitHubPerson
 * 
 * @author mohammad Chegini
 *
 */
public class Person {
	Set<Person> followers = new HashSet<>();
	Set<Person> following = new HashSet<>();
	URL profileLink;
	String id = new String();
	// ckeck if this node is crawled before or not
	boolean checked = false;
}
