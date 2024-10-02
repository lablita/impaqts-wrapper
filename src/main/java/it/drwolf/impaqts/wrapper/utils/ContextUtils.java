/*
 * Copyright (C) 2024
 * EMMACorpus
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

