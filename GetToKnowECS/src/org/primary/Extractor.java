package org.primary;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;


public class Extractor {

	private String urlOfSite;
	
	public Extractor(String urlOfSite){
		this.urlOfSite=urlOfSite;
	}
	
	public ArrayList<Person> getPeople(){
		ArrayList<Person> people=new ArrayList<Person>();
		try {
			BufferedReader readerUrl=new BufferedReader(new InputStreamReader(new URL(urlOfSite).openStream()));
			System.out.println("Got the Html!");
			String line=readerUrl.readLine();
			String previousImgUrl="";
			int count=0;
			while(true){
				if(line==null)
					break;
				if(line.contains("<img")) {
					line=readerUrl.readLine();
					String imgUrl=line.split("\"")[1].split("&")[0];
					
					while(!line.contains("class=\"ecs_userpic\""))
						line=readerUrl.readLine();
					line=readerUrl.readLine();
					line=line.substring(line.indexOf("http://www.ecs.soton.ac.uk/people/"),line.length());
					String emailId=line.split("http://www.ecs.soton.ac.uk/people/")[1].split("\"")[0];
					line=line.substring(line.indexOf(">"),line.length());
					String name=line.split(">")[1].split("<")[0];
					line=line.substring(line.indexOf("<td>"),line.length());
					
					if(!previousImgUrl.equals(imgUrl)){
						count++;
						people.add(new Person(emailId,name,imgUrl));

						if(count%10==0)
							System.out.println(count+"people...");
					}
					previousImgUrl=new String(imgUrl);
					
				} else {
					line=readerUrl.readLine();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return people;
	}
}
