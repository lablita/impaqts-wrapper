package it.drwolf.impaqts.wrapper.utils;

import com.sketchengine.manatee.StrVector;

public class ContextUtils {

	private final static String TAGS_IN_CONTEXT = "{}";

	public static String strip_tags(StrVector tokens) {
		String ret = "";
		for (int i = 0; i < tokens.size(); i += 2) {
			ret += tokens.get(i);
			ret += ' ';
		}
		return ret;
	}

	public static String removeContextTags(String s) {
		int position = s.lastIndexOf(TAGS_IN_CONTEXT);
		StringBuilder builder = new StringBuilder();
		builder.append(s.substring(0, position));
		builder.append(s.substring(position + TAGS_IN_CONTEXT.length(), s.length()));
		return builder.toString();
	}
}
