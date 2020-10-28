package it.drwolf.impaqts.wrapper;

import it.drwolf.impaqts.wrapper.executor.QueryExecutor;
import picocli.CommandLine;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "ImpaqtsWrapper")
public class MainWrapper implements Callable<Integer> {

	private static final String MANATEE_REGISTRY = "MANATEE_REGISTRY";

	@CommandLine.Option(names = { "-c", "--corpus" },
			required = true,
			description = "Corpus name")
	String corpus;
	@CommandLine.Option(names = { "-l", "--jnilib" },
			required = true,
			description = "Manatee JNI lib full path")
	Path manateeLibPath;
	@CommandLine.Option(names = { "-s", "--start" },
			description = "Results start index (first is 0)")
	Integer start;
	@CommandLine.Option(names = { "-e", "--end" },
			description = "Results end index")
	Integer end;
	@CommandLine.Option(names = { "-q", "--cql" },
			description = "CQL Query")
	String cql;
	@CommandLine.Option(names = { "-j", "--json" },
			description = "Query request in json format")
	String json;

	@CommandLine.Spec
	CommandLine.Model.CommandSpec spec;

	public static void main(String... args) {
		System.exit(new CommandLine(new MainWrapper()).execute(args));
	}

	@Override
	public Integer call() { // business logic
		this.checkEnvironmentVariables();
		this.loadJNI();
		this.validate();
		QueryExecutor queryExecutor = new QueryExecutor();
		if (this.json != null) {
			queryExecutor.manageQueryRequest(this.corpus, this.json);
		} else {
			queryExecutor.manageQueryRequest(this.corpus, this.cql, this.start, this.end);
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
		if (this.json == null || this.json.isEmpty()) {
			if (this.start == null || this.end == null || this.cql == null) {
				throw new CommandLine.ParameterException(this.spec.commandLine(),
						"If you don't specify a '--json' option, then you must specify '--start', '--end' and '--cql' option.");
			}
		}
	}

}
