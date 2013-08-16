package umass.socketsInterface.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
/*
 * Used by clients to look up their external IP address.
 * 
 * Credit for this class goes to Stack Overflow user "Will"
 * 	(From this thread http://stackoverflow.com/questions/2939218/getting-the-external-ip-address-in-java)
 * 
 * Slight modifications made by me.
 */
public class IPChecker {

    public static String getIp(){
        URL whatismyip = null;
        String ip = null;
		try {
			whatismyip = new URL("http://checkip.amazonaws.com");
		} catch (MalformedURLException e1) {
			System.out.println("Malformed URL for Amazon's checkIP service.");
			e1.printStackTrace();
		}
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            ip = in.readLine();
        } catch (IOException e) {
			System.out.println("Unable to use amazon web service to look up external IP; an IOException occurred.");
			e.printStackTrace();
		} finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ip;
    }
}
