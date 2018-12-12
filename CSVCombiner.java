package CSVCombiner;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

//import org.apache.commons.cli;

public class CSVCombiner
{
	public static HashMap<String,Entry> htable;
	public static ArrayList<String> attributes;

	// The arguments must be:
	// 0: The name of the file to write out
	// 1-*: The names of the files to combine
	public static void main(String[] args) throws IOException
	{
		//Options options = new Options();
		//options.addOption("m",true,"mode to determine the output file");
		
		//CommandLineParser parser = new DefaultParser();
		//CommandLine cmd = parser.parse( options, args);
		
		
		// Initialize data structures
		htable = new HashMap<String,Entry>();
		attributes = new ArrayList<String>();

		// Return the arguments to the user (as a sanity check)
		System.out.print("Arguments:");
		for(int i = 0 ; i < args.length ; i++)
		{
			System.out.print(" "+args[i]);
		}
		System.out.println();
		
		// For each of the argument files...
		for(int i = 1 ; i < args.length ; i++)
		{
			// Get the next file ready
			System.out.println("Reading File "+i+": "+args[i]);
			File csv = new File(args[i]);
			CSVReader reader = new CSVReader(new FileReader(csv));

			// Track any new attributes
			String[] nextLine;
			String[] fileAttributes = reader.readNext();
			System.out.print("Attributes:");
			for(int k = 0 ; k < fileAttributes.length ; k++)
			{
				System.out.print(" "+fileAttributes[k]);
			}
			System.out.println();
			updateAttributes(fileAttributes);

			// Update/add entries as required
			while ((nextLine = reader.readNext()) != null)
			{
				String key = nextLine[0];

				Entry e;

				if(htable.containsKey(key))
					e = htable.get(key);
				else
					e = new Entry();

				for (int j = 1 ; j < fileAttributes.length ; j++)
				{
					e.set(fileAttributes[j], nextLine[j]);
				}

				htable.put(key, e);
			}
			// Close resources
			reader.close();
		}

		// Write all the stored entries back out as a new CSV
		writeFinalCSV(args[0]);
	}


	public static void updateAttributes(String[] fileAttributes)
	{
		// Iterate from 1 because the zeroth entry will always be the ID
		for(int i = 1 ; i < fileAttributes.length ; i++)
		{
			if(!attributes.contains(fileAttributes[i]))
			{
				attributes.add(fileAttributes[i]);
			}
		}
		
		System.out.println("Now tracking "+attributes.size()+" attributes.");
	}

	public static void writeFinalCSV(String finalCSV) throws IOException
	{
		CSVWriter writer = null;
		int numAttributes = attributes.size();
		System.out.println("There are "+htable.size()+" entries, each with up to "+numAttributes+" attributes.");

		// Prepare an output file			
		if (!finalCSV.endsWith(".csv"))
		{
			finalCSV += ".csv";
		}

		File finalFile = new File(finalCSV);

		writer = new CSVWriter(new FileWriter(finalFile),',', '\"', '\\', "\n");

		System.out.println("Beginning to write to "+finalCSV);

		// Write the header for the final file
		String[] nextLine = new String[numAttributes+1];
		nextLine[0] = "ID";
		for(int i = 0 ; i < numAttributes ; i++)
		{
			nextLine[i+1] = attributes.get(i);
		}
		writer.writeNext(nextLine);								
		System.out.println("Header written...");

		// Write each entry for the final file
		for(Map.Entry<String,Entry> pair : htable.entrySet())
		{
			// The key is this entry's ID
			nextLine[0] = pair.getKey();

			// Get entry from the HashMap
			Entry e = pair.getValue();

			// Each attribute of the entry
			for(int i = 0 ; i < numAttributes ; i++)
			{
				nextLine[i+1] = e.get(attributes.get(i));
			}

			writer.writeNext(nextLine);								
		}
		// Close resources
		System.out.println("File written! Closing resources now.");
		writer.close();
	}
}	

