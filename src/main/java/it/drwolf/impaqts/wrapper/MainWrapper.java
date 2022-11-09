package it.drwolf.impaqts.wrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.drwolf.impaqts.wrapper.dto.QueryRequest;
import it.drwolf.impaqts.wrapper.executor.QueryExecutor;
import picocli.CommandLine;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "ImpaqtsWrapper")
public class MainWrapper implements Callable<Integer> {

	private static final String MANATEE_REGISTRY = "MANATEE_REGISTRY";
	private final ObjectMapper objectMapper;

	@CommandLine.Option(names = { "-c", "--corpus" }, required = true, description = "Corpus name")
	String corpus;
	@CommandLine.Option(names = { "-l", "--jnilib" }, required = true, description = "Manatee JNI lib full path")
	Path manateeLibPath;
	@CommandLine.Option(names = { "-s", "--start" }, description = "Results start index (first is 0)")
	Integer start;
	@CommandLine.Option(names = { "-e", "--end" }, description = "Results end index")
	Integer end;
	@CommandLine.Option(names = { "-q", "--cql" }, description = "CQL Query")
	String cql;
	@CommandLine.Option(names = { "-j", "--json" }, description = "Query request in json format")
	String json;
	@CommandLine.Option(names = { "-d", "--cache-dir" }, description = "Cache dir path")
	String cacheDir;
	@CommandLine.Option(names = { "-m",
			"--corpusmetadata" }, description = "Retrieve corpus metadata. Specify first or second level attribute (eg. doc.file)")
	String corpusMetadata;

	@CommandLine.Spec
	CommandLine.Model.CommandSpec spec;

	public MainWrapper() {
		this.objectMapper = new ObjectMapper();
	}

	public static void main(String... args) {
		System.exit(new CommandLine(new MainWrapper()).execute(args));
	}

	@Override
	public Integer call() { // business logic
		this.checkEnvironmentVariables();
		this.loadJNI();
		this.validate();
		QueryExecutor queryExecutor = new QueryExecutor(this.cacheDir);
		QueryRequest qr = new QueryRequest();
		if (this.json != null) {
			try {
				String jsonFormatted = this.json.replace("\\\\", "\\");
				qr = this.objectMapper.readValue(jsonFormatted, new TypeReference<>() {
				});
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				System.err.println("Bad format for json request");
				System.exit(1);
			}
		} else if (this.corpusMetadata != null && !this.corpusMetadata.isEmpty()) {
			qr.setCorpusMetadatum(this.corpusMetadata);
		} else {
			qr.setStart(this.start);
			qr.setEnd(this.end);
			qr.setCql(this.cql);
		}
		try {
			queryExecutor.manageQueryRequest(this.corpus, qr);
		} catch (Exception e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
		}
		return 0;
	}

	private void checkEnvironmentVariables() {
		String manateeRegistryPath = System.getenv(MainWrapper.MANATEE_REGISTRY);
		if (manateeRegistryPath == null || manateeRegistryPath.isEmpty()) {
			System.err.println("Manatee registry environment variable not found. Exiting.");
			System.exit(1);
		}
		File manateeRegistryDir = new File(manateeRegistryPath);
		if (!manateeRegistryDir.canRead() || !manateeRegistryDir.isDirectory() || !manateeRegistryDir.canWrite()) {
			System.err.println("Cannot access Manatee registry. Exiting.");
			System.exit(1);
		}
	}

	private void loadJNI() {
		if (this.manateeLibPath == null) {
			throw new CommandLine.ParameterException(this.spec.commandLine(), "Manatee lib path is required. Exiting.");
		}
		File libManateeFile = this.manateeLibPath.toAbsolutePath().toFile();
		if (libManateeFile.canRead()) {
			System.load(libManateeFile.getAbsolutePath());
		} else {
			throw new CommandLine.ParameterException(this.spec.commandLine(), "Cannot read Manatee lib path. Exiting.");
		}
	}

	private void validate() {
		if ((this.json == null || this.json.isEmpty()) && (this.corpusMetadata == null || this.corpusMetadata.isEmpty())) {
			if (this.start == null || this.end == null || this.cql == null) {
				throw new CommandLine.ParameterException(this.spec.commandLine(),
						"If you don't specify a '--json' option or a '--corpusmetadata' option, then you must specify '--start', '--end' and '--cql' option.");
			}
		}
	}

}
