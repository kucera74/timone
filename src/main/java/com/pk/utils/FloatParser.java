package com.pk.utils;

import java.util.regex.Pattern;

public class FloatParser extends DataParser<Float> {

	private static final String DATA_PATTERN_EXPRESSION = "\\d*\\.?\\d+";
	private static final Pattern DATA_PATTERN = Pattern.compile(DATA_PATTERN_EXPRESSION);
	
	@Override
	public Float parseString(String data) {
		if (!validate(data)) {
			throw new NumberFormatException(data + " is no valid float value.");
		}
		
		return Float.parseFloat(data);
	}

	@Override
	protected Pattern getPattern() {
		return DATA_PATTERN;
	}

}
