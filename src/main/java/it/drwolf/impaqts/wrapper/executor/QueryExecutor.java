package it.drwolf.impaqts.wrapper.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sketchengine.manatee.CollocItems;
import com.sketchengine.manatee.Concordance;
import com.sketchengine.manatee.Corpus;
import com.sketchengine.manatee.IntVector;
import com.sketchengine.manatee.KWICLines;
import com.sketchengine.manatee.NumVector;
import com.sketchengine.manatee.PosAttr;
import com.sketchengine.manatee.StrVector;
import it.drwolf.impaqts.wrapper.dto.CollocationItem;
import it.drwolf.impaqts.wrapper.dto.CollocationQueryRequest;
import it.drwolf.impaqts.wrapper.dto.FrequencyItem;
import it.drwolf.impaqts.wrapper.dto.FrequencyOption;
import it.drwolf.impaqts.wrapper.dto.FrequencyOutput;
import it.drwolf.impaqts.wrapper.dto.FrequencyResultLine;
import it.drwolf.impaqts.wrapper.dto.KWICLine;
import it.drwolf.impaqts.wrapper.dto.KWICLineDTO;
import it.drwolf.impaqts.wrapper.dto.QueryRequest;
import it.drwolf.impaqts.wrapper.dto.QueryResponse;
import it.drwolf.impaqts.wrapper.dto.SortOption;
import it.drwolf.impaqts.wrapper.dto.TokenClassDTO;
import it.drwolf.impaqts.wrapper.exceptions.TagPresentException;
import it.drwolf.impaqts.wrapper.exceptions.TokenPresentException;
import it.drwolf.impaqts.wrapper.query.QueryPattern;
import it.drwolf.impaqts.wrapper.query.QueryTag;
import it.drwolf.impaqts.wrapper.query.QueryToken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class QueryExecutor {

	public static final String STRUCTATTRLIST = "STRUCTATTRLIST";
	public static final String ATTRLIST = "ATTRLIST";
	public static final String STRUCTLIST = "STRUCTLIST";
	public static final String DOCSTRUCTURE = "DOCSTRUCTURE";
	public static final int SAMPLE_SIZE = 10000000;
	public static final long FULL_SIZE = -1L;
	//sort
	public static final String LEFT_CONTEXT = "LEFT_CONTEXT";
	public static final String RIGHT_CONTEXT = "RIGHT_CONTEXT";
	public static final String NODE_CONTEXT = "NODE_CONTEXT";
	public static final String CACHE_FILE = "/tmp/cache/coliweb/wordfinlandial.conc";
	public static final String CACHE_DIR = "/tmp/cache/";
	public static final String EXT_CONC = ".conc";
	private static final Integer MINIMUM_EXECUTION_TIME = 100;
	private final ObjectMapper objectMapper;

	private final Map<Character, String> STAT_DESC = new HashMap<Character, String>() {{
		this.put('t', "T-score");
		this.put('m', "MI");
		this.put('3', "MI3");
		this.put('l', "log likelihood");
		this.put('s', "min. sensitivity");
		this.put('p', "MI.log_f");
		this.put('r', "relative freq.");
		this.put('f', "absolute freq.");
		this.put('d', "logDice");
	}};

	private final Map<String, String> COLLOCATIONS_ATTIBUTE = new HashMap<String, String>() {{
		this.put("WORD", "word");
		this.put("TAG", "tag");
		this.put("LEMMA", "lemma");
	}};

	private final Map<String, String> FROM_CODE = new HashMap<String, String>() {{
		this.put("lc", "<0>");
		this.put("rc", ">0");
		this.put("kl", "<0");
		this.put("kr", ">0");
	}};

	public QueryExecutor() {
		this.objectMapper = new ObjectMapper();
	}

	private float[] computeCorrection(NumVector freqs, NumVector norms, int normWidth, Corpus corpus) {
		if (norms.stream().reduce(0L, Long::sum) <= 0L) {
			return new float[] { normWidth / freqs.stream().reduce(0L, Long::max), 0L };
		} else {
			corpus.get_sizes().split("\n");
			return new float[] { 0F, 0F };
		}
	}

	private List<KWICLineDTO> elaborateKWICLines(KWICLines kl) {
		int refsLen = 1;
		StrVector refList = kl.get_ref_list();
		List<KWICLineDTO> kwicLineDTOList = new ArrayList<>();
		while (kl.nextline()) {
			KWICLineDTO kwicLineDTO = new KWICLineDTO();
			kwicLineDTO.setTokNum(kl.get_pos());
			kwicLineDTO.setHitlen(kl.get_kwiclen());
			if (refList.size() > 0) {
				kwicLineDTO.setRefs(refList.subList(0, refsLen));
				kwicLineDTO.setTblRefs(refList.subList(0, refsLen));
			}
			kwicLineDTO.setLeftLabel(this.tokens2StrClass(kl.get_left()));
			kwicLineDTO.setKwic(this.tokens2StrClass(kl.get_kwic()));
			kwicLineDTO.setRightLabel(this.tokens2StrClass(kl.get_right()));
			kwicLineDTO.setLinks(new ArrayList<>());
			kwicLineDTO.setLineGroup("_");
			kwicLineDTO.setLineGroupId(0);
		}

		return kwicLineDTOList;
	}

	// corpusName, CQL, start, end
	private void executeQuery(String corpusName, QueryRequest queryRequest) throws InterruptedException, IOException {
		final Corpus corpus = new Corpus(corpusName);
		final String cql = this.getCqlFromQueryRequest(queryRequest);
		final int start = queryRequest.getStart();
		final int end = queryRequest.getEnd();
		final Concordance concordance = new Concordance();
		concordance.load_from_query(corpus, cql, 0, 0); // il cql finale al posto di qr-getWord()
		int count = 0;
		int requestedSize = end - start;
		long now = System.currentTimeMillis();
		List<KWICLine> sentKwicLines = new ArrayList<>();
		// è possibile che durante le istruzioni del ciclo while non siano pronti i risultati,
		// ma che la concordance sia marcata come finished sull'ultima istruzione. Per questo imponiamo
		// un tempo minimo di esecuzione
		//while (!concordance.finished() || (System.currentTimeMillis() - now) < QueryExecutor.MINIMUM_EXECUTION_TIME) {
		while (!concordance.finished()) {
			System.out.println(String.format("### 1. Finished: %s\t Time: %d", "" + concordance.finished(),
					(System.currentTimeMillis() - now)));
			List<KWICLine> kwicLines = new ArrayList<>();
			count = concordance.size();
			QueryResponse queryResponse = new QueryResponse();
			queryResponse.setCurrentSize(count);
			Integer maxLine = requestedSize;
			if (maxLine > count) {
				maxLine = count;
			}
			KWICLines kl = new KWICLines(corpus, concordance.RS(false, start, end), "50#", "50#", "word", "word", "s",
					"#", 100);
			for (int linenum = 0; linenum < maxLine; linenum++) {
				if (!kl.nextline()) {
					break;
				}
				KWICLine kwicLine = new KWICLine(kl);
				kwicLines.add(kwicLine);
			}
			if (!sentKwicLines.equals(kwicLines)) {
				queryResponse.getKwicLines().addAll(kwicLines);
				sentKwicLines.clear();
				sentKwicLines.addAll(kwicLines);
			}
			queryResponse.setInProgress(!concordance.finished());
			System.out.println(this.objectMapper.writeValueAsString(queryResponse)); //scrive il risultato in JSON
			Thread.sleep(5);
			System.out.println(String.format("### 2. Finished: %s\t Time: %d", "" + concordance.finished(),
					(System.currentTimeMillis() - now)));
		}
		concordance.delete();
		corpus.delete();
	}

	private void executeQueryCollocation(String corpusName, QueryRequest queryRequest)
			throws InterruptedException, IOException {
		final Corpus corpus = new Corpus(corpusName);
		final int start = queryRequest.getStart();
		final int end = queryRequest.getEnd();

		CollocationQueryRequest collocationQueryRequest = queryRequest.getCollocationQueryRequest();

		QueryTag queryTag = this.getFirstTag(queryRequest);
		if (queryTag.getValue() != null) {
			Concordance concordance = null;
			Path cachePath = Paths.get(QueryExecutor.CACHE_DIR);
			if (!Files.exists(cachePath)) {
				Files.createDirectory(cachePath);
			}
			cachePath = Paths.get(QueryExecutor.CACHE_DIR + corpusName + "/");
			if (!Files.exists(cachePath)) {
				Files.createDirectory(cachePath);
			}
			String fileWordConcordance = queryTag.getName() + "_" + queryTag.getValue()
					.replace(" ", "_") + QueryExecutor.EXT_CONC;
			Optional<Path> pathWordOptional = Files.list(cachePath)
					.filter(file -> file.getFileName().toString().contains(fileWordConcordance))
					.findFirst();
			if (pathWordOptional.isPresent()) {
				concordance = new Concordance(corpus, pathWordOptional.get().toString());
			} else {
				concordance = new Concordance();
				//al posto di phrase su corpora.dipertimentodieccellenza, stessi risultati
				concordance.load_from_query(corpus, this.getCqlFromQueryRequest(queryRequest), 10000000, 0);

				long now = System.currentTimeMillis();
				while (!concordance.finished() || (System.currentTimeMillis() - now) < QueryExecutor.MINIMUM_EXECUTION_TIME) {
					Thread.sleep(5);
				}
				concordance.save(QueryExecutor.CACHE_DIR + corpusName + "/" + fileWordConcordance);

			}
			concordance.sync();
			CollocItems collocItems = new CollocItems(concordance,
					this.COLLOCATIONS_ATTIBUTE.get(collocationQueryRequest.getAttribute()),
					collocationQueryRequest.getSortBy().charAt(0), collocationQueryRequest.getMinFreqCorpus(),
					collocationQueryRequest.getMinFreqRange(), collocationQueryRequest.getRangeFrom(),
					collocationQueryRequest.getRangeTo(), end);
			List<CollocationItem> resultCollocations = new ArrayList<>();
			while (!collocItems.eos()) {
				while (!collocItems.eos()) {
					CollocationItem collocationItem = new CollocationItem();
					collocationItem.setWord(collocItems.get_item());
					collocationItem.setConcurrenceCount(collocItems.get_cnt());
					collocationItem.setCandidateCount(collocItems.get_freq());
					for (String func : collocationQueryRequest.getShowFunc()) {
						switch (func) {
						case "t":
							collocationItem.setTScore(collocItems.get_bgr('t'));
							break;
						case "m":
							collocationItem.setMi(collocItems.get_bgr('m'));
							break;
						case "3":
							collocationItem.setMi3(collocItems.get_bgr('3'));
							break;
						case "l":
							collocationItem.setLogLikelihood(collocItems.get_bgr('l'));
							break;
						case "s":
							collocationItem.setMinSensitivity(collocItems.get_bgr('s'));
							break;
						case "d":
							collocationItem.setLogDice(collocItems.get_bgr('d'));
							break;
						case "p":
							collocationItem.setMiLogF(collocItems.get_bgr('p'));
							break;
						default:
							break;
						}
					}
					resultCollocations.add(collocationItem);
					collocItems.next();
				}
			}
			QueryResponse queryResponse = new QueryResponse();
			queryResponse.setCurrentSize(resultCollocations.size());
			queryResponse.setCollocations(resultCollocations);
			queryResponse.setInProgress(!concordance.finished());
			System.out.println(this.objectMapper.writeValueAsString(queryResponse)); //scrive il risultato in JSON
			concordance.delete();
			corpus.delete();
		}

	}

	private void executeQueryFrequency(String corpusName, QueryRequest queryRequest)
			throws InterruptedException, IOException {
		final Corpus corpus = new Corpus(corpusName);
		final String cql = this.getCqlFromQueryRequest(queryRequest);
		final int start = queryRequest.getStart();
		final int end = queryRequest.getEnd();
		final int freqLimit = queryRequest.getFrequencyQueryRequest().getFrequencyLimit();
		final Concordance concordance = new Concordance();
		concordance.load_from_query(corpus, cql, QueryExecutor.SAMPLE_SIZE, QueryExecutor.FULL_SIZE);
		String crit = "doc.sito/ 0>0 doc.categoria/ 0>0";//da sostituire con della logica
		concordance.sort(crit, false);
		concordance.sync();

		int count = concordance.size();
		QueryResponse queryResponse = new QueryResponse();
		queryResponse.setCurrentSize(count);
		KWICLines kl = new KWICLines(corpus, concordance.RS(false, start, end), "", "", "word,tag,lemma", "word",
				"p,g,err,corr", "=doc.sito,=doc.categoria");

		List<KWICLineDTO> kwicLineDTOList = this.elaborateKWICLines(kl);
		FrequencyOutput frequencyOutput = new FrequencyOutput();
		frequencyOutput.setLines(kwicLineDTOList);
		frequencyOutput.setFromP(1);
		frequencyOutput.setConcSize(concordance.size());

		boolean ml = true;
		String sCrit = this.freqCritBuild(queryRequest);

		queryResponse.setFrequencies(Arrays.asList(
				new FrequencyItem[] { this.xfreqDist(concordance, corpus, start, end, sCrit, freqLimit) }));

		System.out.println(this.objectMapper.writeValueAsString(queryResponse));
		concordance.delete();
		corpus.delete();
	}

	private void executeQuerySort(String corpusName, QueryRequest queryRequest)
			throws InterruptedException, IOException {
		final Corpus corpus = new Corpus(corpusName);
		final String query = String.format(this.getCqlFromQueryRequest(queryRequest), queryRequest.getWord());
		final int start = queryRequest.getStart();
		final int end = queryRequest.getEnd();
		final Concordance concordance = new Concordance(corpus, query, 10000000, -1);

		StrVector vals = new StrVector();
		IntVector idx = new IntVector();
		String critParam = "";
		String crit = "";

		//		simple sort
		if (queryRequest.getSortQueryRequest().getMultilevelSort().isEmpty()) {
			if (queryRequest.getSortQueryRequest().getSortKey().equals(QueryExecutor.LEFT_CONTEXT)) {
				critParam = String.format("-1<0~-%d<0", queryRequest.getSortQueryRequest().getNumberTokens());
			} else if (queryRequest.getSortQueryRequest().getSortKey().equals(QueryExecutor.RIGHT_CONTEXT)) {
				critParam = String.format("1>0~%d>0", queryRequest.getSortQueryRequest().getNumberTokens());
			} else if (queryRequest.getSortQueryRequest().getSortKey().equals(QueryExecutor.NODE_CONTEXT)) {
				critParam = "0<0~0>0";
			}
			if (critParam.length() > 0) {
				crit = String.format("%s/%s%s %s", queryRequest.getSortQueryRequest().getAttribute(),
						queryRequest.getSortQueryRequest().getIgnoreCase() ? "i" : "",
						queryRequest.getSortQueryRequest().getBackward() ? "r" : "", critParam);
			}
		} else { //		multilevel sort
			List<String> critList = new ArrayList<>();
			for (SortOption sortOption : queryRequest.getSortQueryRequest().getMultilevelSort()) {
				if (sortOption.getPosition().contains("L")) {//Left
					critParam = String.format("-%s<0", sortOption.getPosition().charAt(0));
				} else if (sortOption.getPosition().contains("R")) {//Right
					critParam = String.format("~%s>0", sortOption.getPosition().charAt(0));
				} else { //Node
					critParam = "0~0>0";
				}
				critList.add(
						String.format("%s/%s%s %s", sortOption.getAttribute(), sortOption.getIgnoreCase() ? "i" : "",
								sortOption.getBackward() ? "r" : "", critParam));
			}
			crit = critList.stream().collect(Collectors.joining(" "));
		}

		boolean justLetters = !crit.split("/")[0].contains(".");

		if (critParam.length() > 0) {
			if (!queryRequest.getSortQueryRequest().getMultilevelSort().isEmpty()) {
				concordance.sort(crit, false);
			} else {
				concordance.sort(crit);
				concordance.sort_idx(crit, vals, idx, justLetters);
			}
		} else {
			concordance.shuffle();
		}

		int count = 0;
		long now = System.currentTimeMillis();
		List<KWICLine> sentKwicLines = new ArrayList<>();

		System.out.println(String.format("### 1. Finished: %s\t Time: %d", "" + concordance.finished(),
				(System.currentTimeMillis() - now)));
		List<KWICLine> kwicLines = new ArrayList<>();
		count = concordance.size();
		QueryResponse queryResponse = new QueryResponse();
		queryResponse.setCurrentSize(count);
		Integer maxLine = 4000;
		if (maxLine > count) {
			maxLine = count;
		}
		KWICLines kl = new KWICLines(corpus, concordance.RS(true, start, end), "40#", "40#", "word,tag,lemma", "word",
				"p,g,err,corr", "=doc.sito,=doc.categoria");
		for (int linenum = 0; linenum < maxLine; linenum++) {
			if (!kl.nextline()) {
				break;
			}
			KWICLine kwicLine = new KWICLine(kl);
			kwicLines.add(kwicLine);
		}

		if (!queryRequest.getSortQueryRequest().getMultilevelSort().isEmpty()) {
			concordance.sort_idx(crit, vals, idx, justLetters);
		}

		if (!sentKwicLines.equals(kwicLines)) {
			queryResponse.getKwicLines().addAll(kwicLines);
			sentKwicLines.clear();
			sentKwicLines.addAll(kwicLines);
		}
		queryResponse.setInProgress(!concordance.finished());
		System.out.println(this.objectMapper.writeValueAsString(queryResponse)); //scrive il risultato in JSON
		Thread.sleep(5);
		System.out.println(String.format("### 2. Finished: %s\t Time: %d", "" + concordance.finished(),
				(System.currentTimeMillis() - now)));
		concordance.delete();
		corpus.delete();
	}

	private String freqCritBuild(QueryRequest queryRequest) {
		List<String> resList = new ArrayList<>();
		if (!queryRequest.getFrequencyQueryRequest().getMultilevelFrequency().isEmpty()) {
			List<FrequencyOption> frequencyOptionList = queryRequest.getFrequencyQueryRequest()
					.getMultilevelFrequency();
			for (FrequencyOption frequencyOption : frequencyOptionList) {
				resList.add(this.oneLevelCrit("", frequencyOption.getAttribute(), "",
						this.fromAttributeToSymbolic(frequencyOption.getPosition()), "rc",
						frequencyOption.getIgnoreCase() ? "i" : "", "e", ""));
			}
		} else {
			//single level
		}

		return String.join(" ", resList);
	}

	private String fromAttributeToSymbolic(String attribute) {
		if (attribute.equals(QueryExecutor.NODE_CONTEXT)) {
			return " 0~0";
		} else if (attribute.contains("L")) {
			//left
			return String.format(" -%s", attribute.substring(0, 1));
		} else {
			//right
			return String.format(" %s", attribute.substring(0, 1));
		}
	}

	private String getCqlFromQueryRequest(QueryRequest queryRequest) {
		if (queryRequest.getQueryInCql()) {
			return queryRequest.getCql();
		}
		QueryPattern qp = queryRequest.getQueryPattern();
		if (qp != null) {
			String cql = qp.getCql();
			if (!cql.isEmpty()) {
				return qp.getCql();
			}
		}
		return "[]";
	}

	private QueryTag getFirstTag(QueryRequest queryRequest) {
		Map<String, String> result = new HashMap<>();
		if (queryRequest.getQueryPattern().getTokPattern().isEmpty()) {
			throw new TokenPresentException("no token present");
		} else {
			QueryToken queryToken = queryRequest.getQueryPattern().getTokPattern().get(0);
			if (queryToken.getTags().isEmpty() || queryToken.getTags().get(0).isEmpty()) {
				throw new TagPresentException("no tag present");
			}
			return queryToken.getTags().get(0).get(0);
		}
	}

	// chiamata a metodo getCQLfromqueryrequest che trasforma i dati di input in stringa cql
	public void manageQueryRequest(String corpus, QueryRequest queryRequest) {
		try {
			if (queryRequest.getCorpusMetadatum() != null && !queryRequest.getCorpusMetadatum().isEmpty()) {
				this.retrieveMetadata(corpus, queryRequest.getCorpusMetadatum());
			} else {
				if (queryRequest.getSortQueryRequest() != null) {
					//sorting
					this.executeQuerySort(corpus, queryRequest);
				} else if (queryRequest.getCollocationQueryRequest() != null) {
					//collocation
					this.executeQueryCollocation(corpus, queryRequest);
				} else if (queryRequest.getFrequencyQueryRequest() != null) {
					//frequency
					this.executeQueryFrequency(corpus, queryRequest);
				} else {
					System.out.println("*** CQL *** " + this.getCqlFromQueryRequest(queryRequest)); //debug
					this.executeQuery(corpus, queryRequest);
				}
			}
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}

	private String oneLevelCrit(String prefix, String attr, String ctx, String pos, String fcode, String icase,
			String bward, String empty) {
		StringBuilder res = new StringBuilder("");
		res.append(String.format("%s%s/%s%s%s", prefix, attr, icase, bward, empty));
		if (ctx.isEmpty()) {
			ctx = String.format("%s%s", pos, this.FROM_CODE.get(fcode));
		} else if (ctx.contains("~") && attr.contains(".")) {
			ctx = ctx.split("~")[0];
		}
		res.append(ctx);
		return res.toString();
	}

	// recupera valori dei metadati che sono sui singoli documenti
	private void retrieveMetadata(String corpusName, String attribute) throws JsonProcessingException {
		final Corpus corpus = new Corpus(corpusName);
		PosAttr posAttr = corpus.get_attr(attribute);
		final int posAttrRange = posAttr.id_range();
		QueryResponse queryResponse = new QueryResponse();
		for (int i = 0; i < posAttrRange; i++) {
			if (posAttr.freq(i) > 0) {
				queryResponse.getMetadataValues().add(posAttr.id2str(i));
			}
		}
		queryResponse.setCurrentSize(queryResponse.getMetadataValues().size());
		System.out.println(this.objectMapper.writeValueAsString(queryResponse));
	}

	private List<TokenClassDTO> tokens2StrClass(StrVector strVector) {
		List<TokenClassDTO> tokenClassDTOList = new ArrayList<>();
		for (int i = 0; i < strVector.size(); i += 2) {
			TokenClassDTO tokenClassDTO = new TokenClassDTO();
			tokenClassDTO.setToken(strVector.get(i));
			tokenClassDTO.setClazz(strVector.get(i + 1));
			tokenClassDTOList.add(tokenClassDTO);
		}
		return tokenClassDTOList;
	}

	private FrequencyItem xfreqDist(Concordance concordance, Corpus corpus, int start, int end, String crit,
			int limit) {
		FrequencyItem result = new FrequencyItem();
		StrVector words = new StrVector();
		NumVector freqs = new NumVector();
		NumVector norms = new NumVector();

		corpus.freq_dist(concordance.RS(), crit, limit, words, freqs, norms);
		float[] toBars = this.computeCorrection(freqs, norms, 300, corpus);
		List<FrequencyResultLine> frlList = new ArrayList<>();
		for (int i = 0; i < words.size(); i++) {
			FrequencyResultLine frl = new FrequencyResultLine();
			frl.setWord(Arrays.asList(words.get(i).split("\t")));
			frl.setFreq(freqs.get(i));
			frl.setFbar((long) (freqs.get(i) * toBars[0]) + 1);
			frl.setNorel(norms.get(i));
			frlList.add(frl);
		}
		result.setItems(frlList.stream()
				.sorted(Comparator.comparing(FrequencyResultLine::getFreq).reversed())
				.collect(Collectors.toList()));
		result.setTotalFreq(freqs.stream().reduce(0L, Long::sum));
		result.setTotal(frlList.size());
		result.setHead(crit);
		return result;
	}

}
