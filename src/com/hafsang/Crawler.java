package com.hafsang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * This Class include the main function. You should run this one to get results.
 * 
 * @author mohammad Chegini
 *
 */
public class Crawler {
	Graph graph = new Graph();
	Queue<GitHubPerson> gq = new LinkedBlockingQueue<>();
	Queue<TwitterPerson> tq = new LinkedBlockingQueue<>();
	int flag = 0;
	PrintWriter writer;
	PrintWriter gephiWriter;
	// if you want to crawle more users just change the numOfTwitter and
	// numOfGitHub
	final int numOfTwitter = 0;
	final int numOfGitHub = 2;
	int exceptionCount = 0;
	// githubaccounts sid
	final String[] gitHubAccounts = { "/josh", "/digitalcraftstudios", "/JoshuaGross" };

	/**
	 * Just calls the action function. Everything happens there
	 * 
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		new Crawler().action();
	}

	/**
	 * This function is responsible for main loops, including twitter crawling
	 * loop and github crawling loop. The graph variable is the most important
	 * thing. You will find all twitterPersons, GitHubPersons and map between
	 * them in this class
	 * 
	 * @throws InterruptedException
	 */
	public void action() throws IOException, InterruptedException {
		writer = new PrintWriter("log.txt", "UTF-8");
		gephiWriter = new PrintWriter("gephi.csv", "UTF-8");
		String initURL = new String();
		int gitStep = 0;
		int twitterStep = 0;
		// gq is for github crawling
		for (int i = 0; i < gitHubAccounts.length; i++) {
			GitHubPerson goriginal = new GitHubPerson(gitHubAccounts[i]);
			graph.githubPersons.add(goriginal);
			gq.add(goriginal);
		}

		// tq is for twitter crawling
		TwitterPerson toriginal = new TwitterPerson("joshpeek");
		graph.twitterPersons.add(toriginal);
		tq.add(toriginal);

		// main loop for crawling github users
		while (!gq.isEmpty() && gitStep < numOfGitHub) {
			try {
				gitStep++;
				GitHubPerson temp = gq.remove();
				boolean isFinished = false;
				int i = 1;
				if (!temp.checked) {
					System.out.println(gitStep + "/" + numOfGitHub + ": " + "githubaccount: " + temp.id);
					while (!isFinished && i < 5) {
						isFinished = extractFollowersGitHub(
								getPage(new URL("https://github.com" + temp.id + "/followers" + "?page=" + i)), temp);
						// TimeUnit.SECONDS.sleep(7);
						i++;
					}

					System.out.println("followers page" + i);
					isFinished = false;
					i = 1;
					while (!isFinished && i < 5) {
						isFinished = extractFollowingGitHub(
								getPage(new URL("https://github.com" + temp.id + "/following" + "?page=" + i)), temp);
						// TimeUnit.SECONDS.sleep(7);
						i++;
					}
					System.out.println("following page" + i);
					isFinished = false;
					i = 1;
//					while (!isFinished && i < 5) {
//						isFinished = extractFavPersonByProject(getPage(new URL(
//								"https://github.com/stars" + temp.id + "?direction=desc&page=" + i + "&sort=created")),
//								temp);
//						i++;
//					}
					System.out.println("fav" + i);
					isFinished = false;
					i = 1;
					extractTwitter(getPage(new URL("https://github.com" + temp.id)), temp);
					temp.checked = true;
				}
			} catch (Exception e) {
				exceptionCount++;
			}
		}

		// main loop for crawling twitter users
		while (!tq.isEmpty() && twitterStep < numOfTwitter) {
			try {
				twitterStep++;
				TwitterPerson temp = tq.remove();
				if (!temp.checked) {
					System.out.println(twitterStep + "/" + numOfTwitter + ": " + "twitteraccount: " + temp.id);
					extractFlollowersTwitter(getPage(new URL("http://twopcharts.com/" + temp.id + "/followers")), temp);
					extractFlollowingTwitter(getPage(new URL("http://twopcharts.com/" + temp.id + "/following")), temp);
					//extractFollowingFavTwitter(getPage(new URL("http://resque.favstar.fm/users/" + temp.id + "/faved")),
					//		temp);
					temp.checked = true;
				}
			} catch (Exception e) {
				exceptionCount++;
			}

		}

		finilize();
	}

	/**
	 * this function get a specific twitter user and add the persons where the
	 * user liked at least on of his tweats
	 * 
	 * @param page
	 * @param original
	 */
	public void extractFollowingFavTwitter(String page, TwitterPerson original) {
		Document doc = Jsoup.parse(page);
		Elements elem = doc.getElementsByClass("fs-screen-name");
		for (Element element : elem) {
			StringBuilder st = new StringBuilder(element.ownText());
			TwitterPerson person = graph.returnTwitterPersonID(st.substring(1));
			original.favPersonByTweat.add(person);
			// tq.add(person);
		}
	}

	/**
	 * This function get a specific github user and add his followings to the
	 * graph
	 * 
	 * @param page
	 * @param original
	 */
	public boolean extractFollowingGitHub(String page, GitHubPerson original) {
		Document doc = Jsoup.parse(page);
		Elements elem = doc.getElementsByClass("follow-list-name");
		int count = 0;
		for (Element element : elem) {
			Elements temp = element.getElementsByAttribute("href");
			GitHubPerson person = graph.returnGithubPersonInList(temp.attr("href"));
			original.following.add(person);
			gq.add(person);
			count++;
		}
		if (count < 10)
			return true;
		else
			return false;
	}

	/**
	 * This function extract projects owners that have been liked by a specific
	 * gitHub User
	 * 
	 * @param page
	 * @param original
	 */
	public boolean extractFavPersonByProject(String page, GitHubPerson original) {
		Document doc = Jsoup.parse(page);
		int count = 0;
		Elements elem = doc.getElementsByClass("prefix");
		for (Element element : elem) {
			String temp = "/" + element.html();
			// Elements temp = element.getElementsByAttribute("href");
			GitHubPerson person = graph.returnGithubPersonInList(temp);
			original.favPersonByProject.add(person);
			// q.add(person);
			count++;
		}
		if (count < 10)
			return true;
		else
			return false;
	}

	/**
	 * extract the twitter account from the github account. In 10 percent of
	 * instances, a user put a link to his twitter account in his gitHub page
	 * 
	 * @param page
	 */
	public void extractTwitter(String page, GitHubPerson original) {
		Document doc = Jsoup.parse(page);
		Elements eeee = doc.getAllElements();
		String s = eeee.html();

		Elements elem = doc.getElementsByClass("vcard-details");
		for (Element element : elem) {
			for (Element element2 : element.getElementsByAttribute("href")) {
				String ur = element2.attr("href");
				if (ur.contains("twitter")) {
					String st = new StringBuilder(ur.toString()).reverse().toString();
					String[] parts = st.split("/");
					st = parts[0];
					st = new StringBuilder(st).reverse().toString();
					System.out.println("$$$$$$$$$$$$$$$$$Twitter account= " + st);
					TwitterPerson tp = graph.returnTwitterPersonID(st);
					tq.add(tp);
					graph.nodeMap.put(tp, original);
					flag++;
				}
			}
		}
	}

	public void finilize() {
		// print github and twitter map
		writer.println("num of exceptions: " + exceptionCount);
		writer.println("-------------Founded Maps-------------");
		for (Entry<TwitterPerson, GitHubPerson> entry : graph.nodeMap.entrySet()) {
			writer.println(entry.getKey().id + "  " + entry.getValue().id);
			gephiWriter.println(entry.getKey().id + ";" + entry.getValue().id);
			gephiWriter.println(entry.getValue().id + ";" + entry.getKey().id);
		}
		writer.println("-------------End of Maps-------------");

		// print github crawling results
		for (int j = 0; j < numOfGitHub; j++) {
			GitHubPerson gp = graph.githubPersons.get(j);
			writer.println(gp.id + "--GITHUB--");

			writer.println(gp.id + "*******************" + "followers");
			for (Person gitHubPerson : gp.followers) {
				writer.println(gitHubPerson.id);
				if (gitHubPerson.checked)
					gephiWriter.println(gitHubPerson.id + ";" + gp.id);

			}
			writer.println(gp.id + "*******************" + "following");
			for (Person gitHubPerson : gp.following) {
				writer.println(gitHubPerson.id);
				// if (gitHubPerson.checked)
				// gephiWriter.println(gp.id + ";" + gitHubPerson.id);
			}
			writer.println(gp.id + "*******************" + "fav");
			for (Person gitHubPerson : gp.favPersonByProject) {
				writer.println(gitHubPerson.id);
			}
		}

		// print twitter crawling results
		for (int i = 0; i < numOfTwitter - 1; i++) {
			TwitterPerson tp = graph.twitterPersons.get(i);
			writer.println(tp.id + "--TWITTER--");
			writer.println(tp.id + "*******************" + "followers");
			for (Person follower : tp.followers) {
				writer.println(follower.id);
				if (follower.checked)
					gephiWriter.println(follower.id + ";" + tp.id);
			}
			writer.println(tp.id + "*******************" + "following");
			for (Person following : tp.following) {
				writer.println(following.id);
				// if (following.checked)
				// gephiWriter.println(tp.id + ";" + following.id);
			}
			writer.println(tp.id + "*******************" + "fav");
			for (TwitterPerson fav : tp.favPersonByTweat) {
				writer.println(fav.id);
			}
		}

		// writer.println(graph.twitterPersons.get(i).favPersonByTweat.get(j).id);

		writer.println();
		writer.println("*************" + flag);
		writer.close();
		gephiWriter.close();
	}

	/**
	 * This function extract a github users followers and add them to his
	 * followers section
	 * 
	 * @param page
	 * @param original
	 */
	public boolean extractFollowersGitHub(String page, GitHubPerson original) {
		Document doc = Jsoup.parse(page);
		int count = 0;
		Elements elem = doc.getElementsByClass("follow-list-name");

		for (Element element : elem) {
			Elements temp = element.getElementsByAttribute("href");
			GitHubPerson person = graph.returnGithubPersonInList(temp.attr("href"));
			original.followers.add(person);
			gq.add(person);
			count++;
		}
		if (count < 10)
			return true;
		else
			return false;

	}

	/**
	 * With this function you can easily get folloewrs of a specific twitter
	 * user
	 * 
	 * @param page
	 * @param original
	 */
	public void extractFlollowersTwitter(String page, TwitterPerson original) {
		Document doc = Jsoup.parse(page);
		Elements elem = doc.getElementsByAttribute("href");
		for (Element element : elem) {
			String st = element.ownText();
			if (!st.isEmpty() && st.charAt(0) == '@') {
				st = st.substring(1);
				if (!st.equals(original.id) && !st.equals("twopchartsisok")) {
					TwitterPerson tp = graph.returnTwitterPersonID(st);
					original.followers.add(tp);
					tq.add(tp);
				}
			}
		}
	}

	public void extractFlollowingTwitter(String page, TwitterPerson original) {
		Document doc = Jsoup.parse(page);
		Elements elem = doc.getElementsByAttribute("href");
		for (Element element : elem) {
			String st = element.ownText();
			if (!st.isEmpty() && st.charAt(0) == '@') {
				st = st.substring(1);
				if (!st.equals(original.id) && !st.equals("twopchartsisok")) {
					// System.out.println(st);
					TwitterPerson tp = graph.returnTwitterPersonID(st);
					original.following.add(tp);
					tq.add(tp);
				}
			}
		}
	}

	/**
	 * this function get a URL and return the html
	 * 
	 * @param url:
	 *            get the URL
	 * @return
	 * @throws IOException
	 */
	protected String getPage(URL url) throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(url.openStream()));
		} catch (IOException e) {
			// finilize();
			System.err.println("check your internet connection");
		}

		String strTemp = "";
		String page = "";
		while (null != (strTemp = br.readLine())) {
			page += strTemp;
		}
		return page;
	}
}
