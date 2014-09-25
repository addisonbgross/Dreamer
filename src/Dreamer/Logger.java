package Dreamer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

//TODO make this into a functional logger
//better yet, implement the java class that does it for you
public class Logger {
	public static void main(String[] args) {
		try {
 
			String content = "This is the content to write into file";
 
			BufferedWriter bw = new BufferedWriter(new FileWriter("log.txt"));
			bw.write(content);
			bw.write("\n");
			bw.write(content);
			bw.close();
 
			System.out.println("Done");
 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
