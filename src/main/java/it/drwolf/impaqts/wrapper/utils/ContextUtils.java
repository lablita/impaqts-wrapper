package it.drwolf.impaqts.wrapper.utils;

import com.sketchengine.manatee.StrVector;

public class ContextUtils {

	private final static String TAGS_IN_CONTEXT = "{}";
	private final static String IMPL = "impl";
	private final static String PPP = "ppp";
	private final static String VAG = "vag";
	private final static String TOP = "top";

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
		String builder = s.substring(0, position) + s.substring(position + TAGS_IN_CONTEXT.length());
		return builder;
	}

	public static String removeHtmlTags(String s, Boolean impaqts) {
		if (s != null && !s.isEmpty()) {
			return s.replaceAll("\\<.*?\\>", "");
		}
		return s;
	}
}

