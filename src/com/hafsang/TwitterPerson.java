package com.hafsang;
import java.util.HashSet;
import java.util.Set;
/**
 * A Twitter Account
 * @author mohammad Chegini
 *
 */
public class TwitterPerson extends Person {
	Set<Tweet> favTweets = new HashSet<>();
	Set<TwitterPerson> favPersonByTweat = new HashSet<>();

	public TwitterPerson(String id) {
		this.id = id;
	}
}
