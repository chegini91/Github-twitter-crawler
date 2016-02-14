package com.hafsang;

public class Tweet {
	TwitterPerson owner;
	String content = new String();

	public Tweet(TwitterPerson owner, String content) {
		this.owner = owner;
		this.content = content;
	}
}
