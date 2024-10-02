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

import it.drwolf.impaqts.wrapper.dto.QueryRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DescriptionUtils {

	private static final Map<String, String> DESC_TEXT = Stream.of(
			new Object[][] { { "q", "Query" }, { "a", "Query" }, { "L", "Label filter" }, { "R", "Query" },
					{ "r", "Random sample" }, { "s", "Sort" }, { "f", "Shuffle" }, { "D", "Subparts filter" },
					{ "F", "Filter all but first hit in the structure" }, { "n", "Negative filter" },
					{ "N", "Negative filter (excluding KWIC)" }, { "p", "Positive filter" },
					{ "P", "Positive filter (excluding KWIC)" }, { "w", "Word sketch item" }, { "t", "Term" },
					{ "e", "GDEX" }, { "x", "Switch KWIC" }, { "X", "Filter by aligned corpus" },
					{ "g", "Sort labels" } }).collect(Collectors.toMap(data -> (String)data[0], data -> (String)data[1]));

	private static final Map<String, List<String>> FORMS = Stream.of(
			new Object[][] {
					{"q", new ArrayList<String>(Arrays.asList("first_form", "cql"))},
					{"a", new ArrayList<String>(Arrays.asList("first_form", "cql"))},
					{"r", new ArrayList<String>(Arrays.asList("reduce_form", "rlines"))}})
			.collect(Collectors.toMap(data -> (String)data[0], data -> (List<String>)data[1]));

	public List<String> getConcordancesDescription(QueryRequest queryRequest, String corpusName) {
		List<String> description = new ArrayList<>();

		return description;
	}
}
