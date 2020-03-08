package com.pk.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class DataParser<T> {

	public abstract T parseString(String data);
	
	protected abstract Pattern getPattern();
	
	protected boolean validate(String data) {
		Matcher matcher = getPattern().matcher(data);
		return matcher.matches();
	}
	
}
