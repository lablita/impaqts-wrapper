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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.drwolf.impaqts.wrapper.dto.QueryRequest;
import it.drwolf.impaqts.wrapper.dto.QueryResponse;
import it.drwolf.impaqts.wrapper.executor.QueryExecutor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QueryExecutorTest {

	private static final String MANATEE_LIB_PATH = "/usr/local/lib/manatee/libmanatee_jni.so";
	private static final String CORPUS_NAME = "susanne";
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
	private final PrintStream originalErr = System.err;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeAll
	public static void init() {
		QueryExecutorTest.loadJNI();
	}

	private static void loadJNI() {
		File libManateeFile = new File(QueryExecutorTest.MANATEE_LIB_PATH);
		if (libManateeFile.canRead()) {
			System.load(libManateeFile.getAbsolutePath());
		}
	}

	@AfterEach
	public void restoreStreams() {
		System.setOut(this.originalOut);
		System.setErr(this.originalErr);
	}

	@BeforeEach
	public void setUpStreams() {
		System.setOut(new PrintStream(this.outContent));
		System.setErr(new PrintStream(this.errContent));
	}

	@Test
	public void testNoResults() {
		QueryRequest qr = new QueryRequest();
		qr.setCql("[word=\"il\"]");
		qr.setStart(0);
		qr.setEnd(10);
		QueryExecutor qe = new QueryExecutor("/tmp/cache/");
		try {
			qe.manageQueryRequest(QueryExecutorTest.CORPUS_NAME, qr);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		String output = this.outContent.toString();
		Assertions.assertNotNull("Output should not be null", output);
		String[] lines = output.split("\n");
		Assertions.assertTrue(lines.length > 1, "More than one line of output");
		List<String> noCommentsLine = Arrays.asList(lines)
				.stream()
				.filter(line -> line.startsWith("###"))
				.collect(Collectors.toList());
		Assertions.assertTrue(noCommentsLine.size() > 0, "At least one no comment output");
		String firstLine = noCommentsLine.get(0);
		try {
			QueryResponse response = this.objectMapper.readValue(firstLine, new TypeReference<>() {
			});
			Assertions.assertNotNull(response, "Response must not be null");
			Assertions.assertEquals(0, response.getCurrentSize(), "No results");
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSomeResults() {
		QueryRequest qr = new QueryRequest();
		qr.setCql("[word=\"the\"]");
		qr.setStart(0);
		qr.setEnd(10);
		QueryExecutor qe = new QueryExecutor("/tmp/cache/");
		try {
			qe.manageQueryRequest(QueryExecutorTest.CORPUS_NAME, qr);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		String output = this.outContent.toString();
		Assertions.assertNotNull("Output should not be null", output);
		String[] lines = output.split("\n");
		Assertions.assertTrue(lines.length > 1, "More than one line of output");
		List<String> noCommentsLine = Arrays.asList(lines)
				.stream()
				.filter(line -> line.startsWith("###"))
				.collect(Collectors.toList());
		Assertions.assertTrue(noCommentsLine.size() > 0, "At least one no comment output");
		String firstLine = noCommentsLine.get(0);
		try {
			QueryResponse response = this.objectMapper.readValue(firstLine, new TypeReference<>() {
			});
			Assertions.assertNotNull(response, "Response must not be null");
			Assertions.assertTrue(response.getCurrentSize() > 0, "Some results must be here");
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}
