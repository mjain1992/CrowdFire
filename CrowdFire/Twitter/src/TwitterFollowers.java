import twitter4j.*;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

class TwitterFollowers{
	public static String[] fetchBestTimeToPost(Long userId, String userName){
		/***
		 * Input Params:
		 * userId - twitter user id for which you want the best day and time to post.
		 * userName - twitter user name for which you want the best day and time to post.
		 * 
		 * Output Params:
		 * return best day and best time to post in an string array.
		 */
		try{
	        String consumerKey = "kQAbbMEaZKl4LmvvhQNkHeydC";	 
	        String consumerSecret = "2ldZ5fLtPwZgafl8uO6tn4cL0z8dyl1C7tspgjqJU3WfaOY2vl";
	        
	        ConfigurationBuilder builder = new ConfigurationBuilder();
	        builder.setApplicationOnlyAuthEnabled(true);
	        builder.setOAuthConsumerKey(consumerKey);
	        builder.setOAuthConsumerSecret(consumerSecret);
	
	        OAuth2Token token = new TwitterFactory(builder.build()).getInstance().getOAuth2Token();
	
	        builder = new ConfigurationBuilder();
	        builder.setApplicationOnlyAuthEnabled(true);
	        builder.setOAuthConsumerKey(consumerKey);
	        builder.setOAuthConsumerSecret(consumerSecret);
	        builder.setOAuth2TokenType(token.getTokenType());
	        builder.setOAuth2AccessToken(token.getAccessToken());
	
	        Twitter twitter = new TwitterFactory(builder.build()).getInstance();

			long cursor =-1L;
		    List<Long> followers = new ArrayList<Long>();
		    boolean userIdFlag = false; 
		    if(userId != null){
		    	userIdFlag = true;
		    }
		    PagableResponseList<User> followersList;
		    do {
		    	if(userIdFlag){
		    		followersList = twitter.getFollowersList(userId, cursor);
		    	}else{
		    		followersList = twitter.getFollowersList(userName, cursor);
		    	}
		    	for (int i = 0; i < followersList.size(); i++)
		        {
		            User user = followersList.get(i);
		            followers.add(user.getId());
		        }
		    } while((cursor = followersList.getNextCursor())!=0 );
		    
		    Paging paging = new Paging();
		    paging.setCount(1);
		    List<Status> statuses = new ArrayList<Status>();
		    List<Date> dates = new ArrayList<Date>();
		    try{
		    	if(userIdFlag){
		    		statuses = twitter.getUserTimeline(userId, paging);
		    	}else{
		    		statuses = twitter.getUserTimeline(userName, paging);
		    	}
		    	if(statuses.size() != 0){
			    	dates.add(statuses.get(0).getCreatedAt());
			    }
	    	}catch(TwitterException e){
	    		System.out.println("error occured" + e);
	    	}
 
		    for(long follower: followers){
		    	try{
			    	List<Status> followerStatus = twitter.getUserTimeline(follower, paging);
			    	if(followerStatus.size() != 0){
				    	dates.add(followerStatus.get(0).getCreatedAt());
				    }
		    	}catch(TwitterException e){
		    		System.out.println("error occured" + e);
		    	}
		    }
		    return getBestTime(dates);
		} catch(TwitterException e){
			System.out.println("error occured" + e);
		}
		return new String[]{"Any day", "Any time"};
	}
	
	private static String[] getBestTime(List<Date> createdTimes){
		/***
		 * Input Params:
		 * createdTimes - list of the date for all the posts.
		 * 
		 * Output Params:
		 * return best day and best time to post in an array.
		 */
		if(createdTimes.size() == 0){
			return new String[]{"Any day", "Any time"};
		}
		String[] bestTimes = new String[2];
		int day = 0;
		int time = 0;
		int totalPosts = createdTimes.size();
		DecimalFormat formatter = new DecimalFormat("00");
		Calendar cal = Calendar.getInstance();
		
		for(Date createdTime:createdTimes){
			cal.setTime(createdTime);
			day = day + cal.get(Calendar.DAY_OF_WEEK);
			int minute = cal.get(Calendar.MINUTE);
			String minuteToTwoDigits = formatter.format(minute);
			String formatedTime = String.valueOf(cal.get(Calendar.HOUR_OF_DAY)) + minuteToTwoDigits;
			time = time + Integer.valueOf(formatedTime);
		}
		
		int bestDay = (int) Math.round((double)day/(double)totalPosts);
		bestTimes[0] = getBestDayStr(bestDay);
		int bestTime = (int) Math.round((double)time/(double)totalPosts);
		bestTimes[1] = getBestTimeStr(bestTime);
		return bestTimes;
	}
	
	private static String getBestDayStr(int bestDay){
		/***
		 * Input Params:
		 * bestDay - day of week in integer.
		 * 
		 * Output Params:
		 * return day of week as Integer.
		 */
		switch(bestDay){
		case 1:
			return "Sunday";
		case 2:
			return "Monday";
		case 3:
			return "Tuesday";
		case 4:
			return "Wednesday";
		case 5:
			return "Thursday";
		case 6:
			return "Friday";
		case 7:
			return "Saturday";
		default:
			return "Any day";
		}
	}
	
	private static String getBestTimeStr(int bestTime){
		/***
		 * Input Params:
		 * bestTime - time of the day in 24 hour format, like 1830.
		 * 
		 * Output Params:
		 * return time of day in 12 hour format, like 6:30 PM.
		 */
		if(bestTime == 0){
			return "Any time";
		}else{
			try{
				final SimpleDateFormat sdf = new SimpleDateFormat("hhmm");
				final Date dateObj = sdf.parse(String.valueOf(bestTime));
				return new SimpleDateFormat("hh:mm a").format(dateObj);
			}catch(Exception e){
				return "Any time";
			} 
		}
	}
	
	public static void main(String[] args){
		/**
		 * test method
		 */
		String[] bestTimes = TwitterFollowers.fetchBestTimeToPost(421766561l, null);
		for(int i = 0; i<bestTimes.length; i++){
			System.out.println(bestTimes[i]);
		}
	}
}
