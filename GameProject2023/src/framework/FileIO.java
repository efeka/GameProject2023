package framework;

import static framework.GameConstants.ScaleConstants.TILE_COLUMNS;
import static framework.GameConstants.ScaleConstants.TILE_ROWS;

import java.lang.StringBuilder;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.imageio.ImageIO;

public class FileIO {

	private int rows, cols;

	public FileIO() {
		rows = TILE_ROWS;
		cols = TILE_COLUMNS;
	}

	/**
	 * Loads a level from the given file.
	 * @param filename the name of the txt file
	 * @param index the index of the level to read
	 * @return 2D array containing the object UIDs in the level
	 */
	public int[][] loadLevel(String filename, int index) {		
		int[][] objectUIDs = new int[rows][cols];
		
		FileInputStream inputStream = null;
		BufferedReader reader = null;
        try {
        	inputStream = new FileInputStream(filename);
            reader = new BufferedReader(new InputStreamReader(inputStream));

            // Go to the level at the given index
            for (int i = 0; i < index; i++)
            	for (int j = 0; j < rows; j++)
            		reader.readLine();
            
            // Read the desired level and create the game objects
            for (int i = 0; i < rows; i++) {
            	String[] line = reader.readLine().trim().split(" ");
            	for (int j = 0; j < line.length; j++) 
            		objectUIDs[i][j] = Integer.parseInt(line[j]);
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
        	try {
        		if (inputStream != null)
        			inputStream.close();
			} 
        	catch (IOException e) {
				e.printStackTrace();
			}
        	try {
        		if (reader != null)
        			reader.close();
			} 
        	catch (IOException e) {
				e.printStackTrace();
			}
        }

		return objectUIDs;
	}

	public void saveLevel(String filename, int[][] objectUIDs) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++)
				sb.append(objectUIDs[i][j] + " ");
			sb.append("\n");
		}

		File outputFile = new File(filename);
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(outputFile, true);

			outputStream.write(sb.toString().getBytes());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (outputStream != null)
					outputStream.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Loads the image file with the given name from the res folder.
	 * @param filename the name of the image file
	 * @return the image
	 * @throws IOException
	 */
	public BufferedImage loadSheet(String filename) throws IOException {
		return ImageIO.read(getClass().getResource("/" + filename));
	}

}
