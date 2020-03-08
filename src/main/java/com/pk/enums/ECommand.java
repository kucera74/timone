package com.pk.enums;

public enum ECommand {
	
	HELP("Help", "h", "Print help"),
	QUIT("Quit", "q", "Terminate program"),
	FEE("Fee", "f", "Read file with fee definition"),
	DATA("Data", "d", "Read file with packages data"),
	INSERT("Insert", "i", "Insert package from command line"),
	LIST("List", "l", "Show list of fees");
	
	private String commandName;
	private String abbreviation;
	private String description;
	
	private ECommand(String commandName, String abbreviation, String description) {
		this.commandName = commandName;
		this.abbreviation = abbreviation;
		this.description = description;
	}
	
	public String getCommand() {
		return commandName;
	}
	
	public String getAbbreviation() {
		return abbreviation;
	}
	
	public String getDescription() {
		return description;
	}

	public static ECommand getByAbbreviation(String abbreviation) {
		for (ECommand cmd : ECommand.values()) {
			if (cmd.getAbbreviation().equalsIgnoreCase(abbreviation)) {
				return cmd;
			}
		}
		return null;
	}
	
	public static ECommand getByName(String name) {
		for (ECommand cmd : ECommand.values()) {
			if (cmd.getCommand().equalsIgnoreCase(name)) {
				return cmd;
			}
		}
		return null;
	}

}
