package org.nameGetter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class NameGetter {

		public static void main(String[] args){
			
			System.out.println("Input id:");
			String emailId="";
			InputStreamReader input=new InputStreamReader(System.in);
			BufferedReader reader=new BufferedReader(input);
			
			try
			{
				emailId = reader.readLine();
				
				String address="http://www.ecs.soton.ac.uk/people/"+emailId;
				URL url=new URL(address);
				BufferedReader readerUrl=new BufferedReader(new InputStreamReader(url.openStream()));
				String line;
				while((line=readerUrl.readLine()) != null){
					if(line.contains("uos-sia-title")) break;
				}
				String name=line.split(">")[1].split("<")[0];
				if(name.equals("ECS People")){
					System.out.println("Nobody found!");
				} else {
					System.out.println(name.replace("&quot;", "\""));
				}
			} catch (Exception e){
				e.printStackTrace();
				return;
			}
			
		}
}
