package handlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BirdStatFormatter {
	File birdStatFile;
	File birdCSV;
	
	//EBird does 4 points of data per month at the time of making thins
	static String[] setupString = {"Jan1", "Jan2", "Jan3", "Jan4", "Feb1", "Feb2", "Feb3", "Feb4",
									"Mar1", "Mar2", "Mar3", "Mar4", "Apr1", "Apr2", "Apr3", "Apr4",
									"May1", "May2", "May3", "May4", "Jun1", "Jun2", "Jun3", "Jun4",
									"Jul1", "Jul2", "Jul3", "Jul4", "Aug1", "Aug2", "Aug3", "Aug4",
									"Sep1", "Sep2", "Sep3", "Sep4", "Oct1", "Oct2", "Oct3", "Oct4",
									"Nov1", "Nov2", "Nov3", "Nov4", "Dec1", "Dec2", "Dec3", "Dec4"};
	
	/**
	 * Constructor, accepts a Stat file from ebird and target file name for the CSV file to write to
	 * 
	 * @param birdStatFile
	 * @param csvFileName
	 * @throws IOException 
	 */
	public BirdStatFormatter(File birdStatFile, String csvFileName) throws IOException {
		this.birdStatFile = birdStatFile;
		File birdCSV = new File(csvFileName);
	
		if(csvVerify(birdCSV)) {
		
			try {
				format(this.birdStatFile, birdCSV);
			} catch (IOException e) {
				System.out.println("Cannot Find Stat File");
			}
		} else {
			System.exit(0);
		}
	}
	
	//These next two I did not realize I needed until last minute when I wanted a way to do this without creating a bunch of objects
	/**
	 * Empty Constructor
	 */
	public BirdStatFormatter() {
		
	}
	
	/**
	 * Accepts stat file and target file name, then creates the target file object and gives it to the other format function
	 * 
	 * @param statFile
	 * @param csvFileName
	 * @return
	 * @throws IOException
	 */
	public boolean format(File statFile, String csvFileName) throws IOException {
		File csvFile = new File(csvFileName);
		
		try {
			format(statFile, csvFile);
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	//Functions with main/intended functionality of the class
	/**
	 * Reads in the data from the Stat file, converts it to CSV, then stores it in a new file
	 * 
	 * @param statFile
	 * @param csvFile
	 * @throws IOException
	 */
	public void format(File statFile, File csvFile) throws IOException {
		if(!csvFile.exists() || !statFile.exists()) {
			throw new IOException();
		}
		
		boolean hitSampleSize = false;
		boolean hitData = false;
		boolean firstData = true;
		
		int sampleSizeCount = 0;
		int sampleSizeMax = 48;
		
		
		BufferedReader statReader = new BufferedReader(new FileReader(statFile));
		BufferedWriter csvWriter = new BufferedWriter(new FileWriter(csvFile, true));
		
		String line = statReader.readLine();
		String birdName = new String();
		
		//There is a lot of white space in these files so rather than jump through that each line just power through the white space till there is none
		while(line != null) {
			try{
				while(line.equals("\\t") || line.contentEquals("")) {
						line = statReader.readLine();		
				}
			} catch (Exception e) {
				break;
			}
			
			//Regex, split at space OR tab
			String[] lineHolder = line.split(" |\\t");
			
			//write Bird name at the end of each line before starting again
			if(!birdName.isEmpty()) {
				csvWriter.write("," + birdName);
				birdName = new String();
			}
				
				
			String prev = lineHolder[0];
			
			for(String token: lineHolder) {
				//Need to grab the sample sizes so that the frequencies make more sense
				//I'm not a statistician so I hope that's what's needed
				if(token.equals("Size:") && prev.equals("Sample")) {
					hitSampleSize = true;
				}
				
				if(!hitData && hitSampleSize && (!token.matches("\\d*\\.?\\d+") && !token.contentEquals("Size:"))) {
					hitData = true;
					csvWriter.write("SampleSize\n");
				}
				
				if(hitSampleSize && !hitData) {
					if(sampleSizeCount < sampleSizeMax) {
						if(token.matches("\\d*\\.?\\d+")) {
							csvWriter.write(token);
						} else {
							csvWriter.write(" ");
						}
						
						//delimiters
						if(sampleSizeCount < 47 && sampleSizeCount != 0) {
							csvWriter.write(",");
						}
						
						sampleSizeCount++;
					}
					
				} else if (hitSampleSize && hitData) {
	
					//When Token is a Float, write it
					//If Token hit's a new bird name write the old bird name, create a new line and replace the bird name
					//If token and prev are both strings we're still going through the bird name, add to it
					if (token.matches("\\d*\\.?\\d+") && !prev.matches("\\d*\\.?\\d+")) {
						csvWriter.write("\n" + token);
					} else if ((!token.matches("\\d*\\.?\\d+") && prev.matches("\\d*\\.?\\d+")) && !firstData) {
						birdName = token;
					} else if (token.matches("\\d*\\.?\\d+") && prev.matches("\\d*\\.?\\d+")) {
						csvWriter.write("," + token);
					} else if (!token.matches("\\d*\\.?\\d+") && !prev.matches("\\d*\\.?\\d+")) {
						birdName += " " + token;
					}
				}
					
				prev = token;
			}
			
			line = statReader.readLine();
		}
		
		csvWriter.write("\n");
		
		statReader.close();
		csvWriter.close();
	}
	
	//CSV initial verification and setup
	/**
	 * Verify the format of the provided CSV file and set it up if it exists but is empty
	 * 
	 * @param csvName
	 * @throws IOException 
	 */
	private boolean csvVerify(File csvFile) throws IOException {
		if(!csvFile.exists()) {
			csvFile.createNewFile();
		}
		
		BufferedReader csvReader = new BufferedReader(new FileReader(csvFile));
		
		String line = csvReader.readLine();
		
		if(isCSVNew(line)) {
			csvSetup(csvFile);
		} else if (!firstLineRight(line)) {
			csvReader.close();
			System.out.println("Check the format of the provided CSV file");
			return false;
		}

		csvReader.close();
		return true;
	}
	
	/**
	 * Verifies if the CSV is empty
	 * 
	 * @param csvFile
	 * @throws IOException 
	 */
	private boolean isCSVNew(String firstLine) {
		if(firstLine == null) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks that the first line, the columns of the CSV file, are assigned correctly
	 * Case sensitive
	 * 
	 * @param firstLine
	 * @return
	 */
	private boolean firstLineRight(String firstLine) {
		String[] tokens = firstLine.split(",");
		
		if(tokens.length != setupString.length) {
			return false;
		}
		
		for(int i = 0; i < tokens.length; i++) {
			if(!(tokens[i].equals(setupString[i]))) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Sets up the first line, which are the columns, of the targeted CSV file
	 * no tabs between columns, only commas for delimiters
	 * 
	 * @param csvFile
	 * @throws IOException 
	 */
	private void csvSetup(File csvFile) throws IOException {
		BufferedWriter csvWriter = new BufferedWriter(new FileWriter(csvFile));
		
		for(int i = 0; i < setupString.length; i++) {
			if(i != setupString.length-1) {
				csvWriter.write(setupString[i] + ",");
			} else {
				csvWriter.write(setupString[i] + "\n");
			}
		}
		
		csvWriter.close();
	}
}
