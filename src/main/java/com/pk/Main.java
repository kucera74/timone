package com.pk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pk.enums.ECommand;
import com.pk.logic.PackageRunner;
import com.pk.logic.PackageWorker;
import com.pk.utils.DataParser;
import com.pk.utils.FloatParser;
import com.pk.utils.IntegerParser;

public class Main {

	private PackageWorker worker;
	private BufferedReader reader;
	
	private static final String DATA_PATTERN_EXPRESSION = "\\d+(\\.\\d{1,3})?\\s\\d{5}";
	private static final Pattern DATA_PATTERN = Pattern.compile(DATA_PATTERN_EXPRESSION);
	private static final String FEE_PATTERN_EXPRESSION = "\\d+(\\.\\d{1,3})?\\s\\d+\\.\\d{2}";
	private static final Pattern FEE_PATTERN = Pattern.compile(FEE_PATTERN_EXPRESSION);

	public static void main(String[] args) {
		Main instance = new Main();
		instance.run();
	}

	private void run() {
		worker = new PackageWorker();
		reader = new BufferedReader(new InputStreamReader(System.in));

		TimerTask packageRunner = new PackageRunner(worker);
		Timer printTimer = new Timer("Package info printer");
		printTimer.scheduleAtFixedRate(packageRunner, 60000L, 60000L);
		
		printMenu();

		Entry<ECommand, String[]> command = null;
		try {
			do {
				command = readCommand();
				
				switch (command.getKey()) {
					case DATA:
						readData(command.getValue());
						break;
					case FEE:
						readFees(command.getValue());
						break;
					case INSERT:
						insertInput(command.getValue());
						break;
					case LIST:
						worker.listFees();
						break;
					case QUIT:
						System.out.println("Bye :-)");
						System.exit(0);
					case HELP:
					default:
						printMenu();
						break;
				}
			} while (true);
		} catch (IOException ex) {
			System.out.println("\u001B[31mReading from input has a problem.\u001B[0m");
		}
	}
	
	private void printMenu() {
		System.out.println("Available commands:");
		for (ECommand cmd : ECommand.values()) {
			System.out.println(cmd.getCommand() + ": " + cmd.getAbbreviation() + " (" + cmd.getDescription() + ")");
		}
	}
	
	private Entry<ECommand, String[]> readCommand() throws IOException {
		String line = reader.readLine();
		String lineParts[] = line.split("\\s+");

		ECommand command = null;
		
		if (lineParts.length > 0) {
			command = ECommand.getByAbbreviation(lineParts[0]);
			if (command == null) {
				command = ECommand.getByName(lineParts[0]);
			}
		}
		if (lineParts.length == 0 || command == null) {
			System.out.println("\u001B[33mType a command.\u001B[0m");
			return readCommand();
		}
		
		if (lineParts.length > 1) {
			return new SimpleEntry<ECommand, String[]>(command, Arrays.copyOfRange(lineParts, 1, lineParts.length));
		} else {
			return new SimpleEntry<ECommand, String[]>(command, null);
		}
	}
	
	private void readData(String[] data) throws IOException {
		if (data == null) {
			System.out.print("Type a path to file with package info: ");
			String path = reader.readLine();
			readData(path);
		} else {
			@SuppressWarnings("unchecked")
			List<Number> parsedData = parseData(data, new FloatParser(), new IntegerParser());
			
			for (int i = 0; i < parsedData.size();) {
				worker.addPackage(parsedData.get(i++).floatValue(), parsedData.get(i++).intValue());
			}
		}
	}
	
	private void readData(String path) {
		@SuppressWarnings("unchecked")
		List<Number> fileData = readDataFromFile(path, DATA_PATTERN, new FloatParser(), new IntegerParser());
		
		for (int i = 0; i < fileData.size();) {
			worker.addPackage(fileData.get(i++).floatValue(), fileData.get(i++).intValue());
		}
	}
	
	private void readFees(String[] data) throws IOException {
		if (data == null) {
			System.out.print("Type a path to file with fee info: ");
			String path = reader.readLine();
			readFees(path);
		} else {
			DataParser<Float> floatParser = new FloatParser();
			@SuppressWarnings("unchecked")
			List<Float> fileData = parseData(data, floatParser, floatParser);
			worker.setFees(fileData);
		}
	}
	
	private void readFees(String path) {
		DataParser<Float> floatParser = new FloatParser();
		@SuppressWarnings("unchecked")
		List<Float> fileData = readDataFromFile(path, FEE_PATTERN, floatParser, floatParser);
		worker.setFees(fileData);
	}
	
	private void insertInput(String[] data) throws IOException {
		if (data == null) {
			System.out.print("Type a weight and destination postal (zip) code of package (value separator is one space): ");
			String input = reader.readLine();
			String[] dataParts = input.split("\\s");
			DataParser<Float> floatParser = new FloatParser();
			DataParser<Integer> integerParser = new IntegerParser();
			float weight = floatParser.parseString(dataParts[0]);
			int zip;
			if (dataParts.length < 2) {
				System.out.println("Type the destination postal (zip) code of package: ");
				input = reader.readLine();
				zip = integerParser.parseString(input);
			} else {
				zip = integerParser.parseString(dataParts[1]);
			}
			worker.addPackage(weight, zip);
		} else {
			@SuppressWarnings("unchecked")
			List<Number> parsedData = parseData(data, new FloatParser(), new IntegerParser());
			
			for (int i = 0; i < parsedData.size();) {
				worker.addPackage(parsedData.get(i++).floatValue(), parsedData.get(i++).intValue());
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> List<T> readDataFromFile(String path, Pattern dataPattern, DataParser<? extends T>...dataParsers) {
		List<T> result = new ArrayList<T>();
		File file = new File(path);
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			Matcher matcher;
			while ((line = br.readLine()) != null) {
				matcher = dataPattern.matcher(line);
				if (matcher.matches()) {
					String[] dataLine = line.split("\\s");
					int i = 0;
					for (String part : dataLine) {
						DataParser<? extends T> parser = i < dataParsers.length ? dataParsers[i++] : null;
						if (parser == null) {
							result.add((T) part);
						} else {
							result.add(parser.parseString(part));
						}
					}
				} else {
					System.out.println("\u001B[31mInvalid input: " + line + ".\u001B[0m");
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("\u001B[31mGiven file doesn't exists. Try another file.\u001B[0m");
		} catch (IOException e) {
			System.out.println("\u001B[31mGiven file cannot be opened.\u001B[0m");
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private <T> List<T> parseData(String[] data, DataParser<? extends T>...dataParsers) {
		List<T> result = new ArrayList<T>();
		
		if (dataParsers == null) {
			for (String item : data) {
				result.add((T) item);
			}
		} else {
			int pl = dataParsers.length;
			assert data.length % pl == 0 : "Number of data parsers doesn't match to the data length.";
			
			int i = 0;
			for (String item : data) {
				DataParser<? extends T> parser = dataParsers[i];
				if (parser == null) {
					result.add((T) item);
				} else {
					result.add(parser.parseString(item));
				}
				i = (i + 1) % pl;
			}
		}
		
		return result;
	}
}
