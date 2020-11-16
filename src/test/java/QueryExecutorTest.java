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
		qr.setWord("[word=\"il\"]");
		qr.setStart(0);
		qr.setEnd(10);
		QueryExecutor qe = new QueryExecutor();
		qe.manageQueryRequest(QueryExecutorTest.CORPUS_NAME, qr);
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
			QueryResponse response = this.objectMapper.readValue(firstLine, new TypeReference<QueryResponse>() {
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
		qr.setWord("[word=\"the\"]");
		qr.setStart(0);
		qr.setEnd(10);
		QueryExecutor qe = new QueryExecutor();
		qe.manageQueryRequest(QueryExecutorTest.CORPUS_NAME, qr);
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
			QueryResponse response = this.objectMapper.readValue(firstLine, new TypeReference<QueryResponse>() {
			});
			Assertions.assertNotNull(response, "Response must not be null");
			Assertions.assertTrue(response.getCurrentSize() > 0, "Some results must be here");
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}
