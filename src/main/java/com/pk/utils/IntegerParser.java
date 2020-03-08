package com.pk.utils;

import java.util.regex.Pattern;

public class IntegerParser extends DataParser<Integer> {

	private static final String DATA_PATTERN_EXPRESSION = "\\d+";
	private static final Pattern DATA_PATTERN = Pattern.compile(DATA_PATTERN_EXPRESSION);
	
	@Override
	public Integer parseString(String data) {
		if (!validate(data)) {
			throw new NumberFormatException(data + " is no valid integer value.");
		}
		
		return Integer.parseInt(data);
	}

	@Override
	protected Pattern getPattern() {
		return DATA_PATTERN;
	}

}
