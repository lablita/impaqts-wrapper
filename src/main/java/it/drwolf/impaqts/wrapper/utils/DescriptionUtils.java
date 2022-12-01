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
