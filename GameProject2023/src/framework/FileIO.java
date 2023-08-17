package framework;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class FileIO {

	public void readFile(String filename) {
		InputStream inputStream = getClass().getResourceAsStream("/" + filename);
		Scanner scanner = null;
		
		try {
			scanner = new Scanner(inputStream);
			
			while (scanner.hasNext())
				System.out.println(scanner.next());
		} 
		catch(Exception e) {
			System.err.println("Failed to load the levels file from /res.");
			e.printStackTrace();
		}
		finally {
			scanner.close();
		}
	}
	
	/**
	 * Loads the image file with the given name from the res folder.
	 * @param filename the name of the image file
	 * @return the BufferedImage extracted from the file
	 * @throws IOException
	 */
	public BufferedImage loadSheet(String filename) throws IOException {
		return ImageIO.read(getClass().getResource("/" + filename));
	}
	
	public static void main(String[] args) {
		new FileIO().readFile("levels.txt");
	}
	
}
