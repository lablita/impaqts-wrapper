package it.drwolf.impaqts.wrapper.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sketchengine.manatee.CollocItems;
import com.sketchengine.manatee.Concordance;
import com.sketchengine.manatee.CorpRegion;
import com.sketchengine.manatee.Corpus;
import com.sketchengine.manatee.IntVector;
import com.sketchengine.manatee.KWICLines;
import com.sketchengine.manatee.NumVector;
import com.sketchengine.manatee.PosAttr;
import com.sketchengine.manatee.StrVector;
import it.drwolf.impaqts.wrapper.dto.CollocationItem;
import it.drwolf.impaqts.wrapper.dto.CollocationQueryRequest;
import it.drwolf.impaqts.wrapper.dto.ContextConcordanceItem;
import it.drwolf.impaqts.wrapper.dto.ContextConcordanceQueryRequest;
import it.drwolf.impaqts.wrapper.dto.DescResponse;
import it.drwolf.impaqts.wrapper.dto.FrequencyItem;
import it.drwolf.impaqts.wrapper.dto.FrequencyOption;
import it.drwolf.impaqts.wrapper.dto.FrequencyQueryRequest;
import it.drwolf.impaqts.wrapper.dto.FrequencyResultLine;
import it.drwolf.impaqts.wrapper.dto.KWICLine;
import it.drwolf.impaqts.wrapper.dto.KWICLineDTO;
import it.drwolf.impaqts.wrapper.dto.QueryRequest;
import it.drwolf.impaqts.wrapper.dto.QueryResponse;
import it.drwolf.impaqts.wrapper.dto.SortOption;
import it.drwolf.impaqts.wrapper.dto.TokenClassDTO;
import it.drwolf.impaqts.wrapper.dto.WideContextRequest;
import it.drwolf.impaqts.wrapper.exceptions.TagPresentException;
import it.drwolf.impaqts.wrapper.exceptions.TokenPresentException;
import it.drwolf.impaqts.wrapper.executor.corpusinfo.CorpusInfoRetriever;
import it.drwolf.impaqts.wrapper.query.QueryPattern;
import it.drwolf.impaqts.wrapper.query.QueryTag;
import it.drwolf.impaqts.wrapper.query.QueryToken;
import it.drwolf.impaqts.wrapper.utils.ContextUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import java.util.stream.Stream;

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
	public static final String EXT_CONC = ".conc";
	private static final Integer MINIMUM_EXECUTION_TIME = 100;
	private final ObjectMapper objectMapper;
	private final Map<Character, String> STAT_DESC = new HashMap<>();
	private final Map<String, String> collocationsAttributes = new HashMap<>();
	private final Map<String, String> fromCodes = new HashMap<>();
	private final Map<String, String> contextConcordances = new HashMap<>();
	public String cacheDir = "/tmp/cache/";

	public QueryExecutor(final String cacheDir) {
		this.cacheDir = cacheDir;
		this.objectMapper = new ObjectMapper();
		this.initMaps();
	}

	private static String getQuery4Context(ContextConcordanceItem cci) {
		List<String> terms = Arrays.asList(cci.getTerm().split(" "));
		StringBuilder queryBuilder = new StringBuilder("[");
		queryBuilder.append(terms.stream()
				.map(term -> String.format("%s=\"%s\"", cci.getAttribute(), term))
				.collect(Collectors.joining("|")));
		queryBuilder.append("];");
		return queryBuilder.toString();
	}

	private float calculateMaxRel(NumVector freqs, NumVector norms, float[] toBars) {
		float maxRel = 0F;
		for (int index = 0; index < freqs.size(); index++) {
			long nf = norms.get(index);
			if (nf == 0L) {
				nf = 100000L;
				norms.set(index, nf);
			}
			float newRel = (freqs.get(index) * toBars[0]) / (nf * toBars[1]);
			if (maxRel < newRel) {
				maxRel = newRel;
			}
		}
		return maxRel;
	}

	private float[] computeCorrection(NumVector freqs, NumVector norms, int normWidth, Corpus corpus) {
		long maxFreq = freqs.stream().max(Long::compareTo).orElse(1L);
		long maxNorm = norms.stream().max(Long::compareTo).orElse(1L);
		if (norms.stream().reduce(0L, Long::sum) <= 0L) {
			return new float[] { normWidth / maxFreq, 0L };
		} else {
			List<String> sizes = Arrays.asList(corpus.get_sizes().split("\n"));
			String normSumStr = sizes.stream().filter(item -> item.contains("normsum")).findFirst().orElse("");
			String wordCountStr = sizes.stream().filter(item -> item.contains("wordcount")).findFirst().orElse("");
			long normSum = Long.parseLong(normSumStr.substring(normSumStr.indexOf(" ") + 1));
			long wordCount = Long.parseLong(wordCountStr.substring(wordCountStr.indexOf(" ") + 1));
			long sumN = normSum > 0 ? normSum : (wordCount > 0 ? wordCount : corpus.size());
			long sumF = freqs.stream().reduce(0L, Long::sum);
			float corr = sumF / maxFreq <= sumN / maxNorm ? sumF / maxFreq : sumN / maxNorm;
			return new float[] { ((float) normWidth / sumF) * corr, ((float) normWidth / sumN) * corr };
		}
	}

	private List<DescResponse> contextConcordance(Concordance concordance, QueryRequest queryRequest) {
		List<DescResponse> descResponses = new ArrayList<>();
		ContextConcordanceQueryRequest contextConcordanceQueryRequest = queryRequest.getContextConcordanceQueryRequest();
		if (QueryRequest.RequestType.CONTEXT_QUERY_REQUEST.toString().equals(queryRequest.getQueryType())) {
			ContextConcordanceItem cci = contextConcordanceQueryRequest.getItems().get(0);
			if (ContextConcordanceItem.LemmaFilterType.ALL.toString().equals(cci.getLemmaFilterType())) {
				Arrays.asList(cci.getTerm().split(" ")).forEach(term -> {
					String query = String.format("[%s=\"%s\"];", cci.getAttribute(), term);
					this.singleContextConcordance(concordance, descResponses, cci,
							DescResponse.OperationType.POSITIVE_FILTER, query, term);
				});
			} else if (ContextConcordanceItem.LemmaFilterType.ANY.toString().equals(cci.getLemmaFilterType())) {
				String query = QueryExecutor.getQuery4Context(cci);
				this.singleContextConcordance(concordance, descResponses, cci,
						DescResponse.OperationType.POSITIVE_FILTER, query, cci.getTerm());
			} else if (ContextConcordanceItem.LemmaFilterType.NONE.toString().equals(cci.getLemmaFilterType())) {
				String query = QueryExecutor.getQuery4Context(cci);
				this.singleContextConcordance(concordance, descResponses, cci,
						DescResponse.OperationType.NEGATIVE_FILTER, query, cci.getTerm());
			}
		} else {
			contextConcordanceQueryRequest.getItems().forEach(cci -> {
				String query = String.format("[%s=\"%s\"];", cci.getAttribute(), cci.getTerm());
				this.singleContextConcordance(concordance, descResponses, cci,
						DescResponse.OperationType.POSITIVE_FILTER, query, cci.getTerm());
			});
		}
		return descResponses;
	}

	private DescResponse elaboratingContext4ConcordanciesFromFrequenciesPN(Concordance concordance,
			FrequencyOption frequencyOption, boolean positive, boolean multiFreq, String category) {
		StringBuilder lcTx = new StringBuilder();
		StringBuilder rcTx = new StringBuilder();
		Integer rank = 0;
		String query;
		if (multiFreq) {//MULTI_FREQ
			String position = frequencyOption.getPosition();
			if (position != null) {
				if (position.equals(QueryExecutor.NODE_CONTEXT)) {
					lcTx.append("0");
					rcTx.append("0>0");
				} else if (position.endsWith("L") && position.length() > 1) {
					lcTx.append("-");
					String distance = position.substring(0, 1);
					lcTx.append(distance).append("<0");
					rcTx.append(lcTx);
				} else if (position.endsWith("R") && position.length() > 1) {
					String distance = position.substring(0, 1);
					lcTx.append(distance).append(">0");
					rcTx.append(lcTx);
				}
			}
			query = String.format("[%s=\"%s\"];", frequencyOption.getAttribute(), frequencyOption.getTerm());
		} else {//_TEXT_TYPE_FREQ
			rank = 1;
			lcTx.append("0");
			rcTx.append("0<0");
			query = String.format("[] within <%s=\"%s\" />", category.replace(".", " "), frequencyOption.getTerm());
		}
		int collNum = concordance.numofcolls() + 1;
		concordance.set_collocation(collNum, query, lcTx.toString(), rcTx.toString(), rank, false);
		concordance.delete_pnfilter(collNum, positive);
		DescResponse descResponse = new DescResponse();
		descResponse.setNiceArg(frequencyOption.getTerm());
		descResponse.setPosition(frequencyOption.getPosition());
		descResponse.setAttribute(frequencyOption.getAttribute());
		descResponse.setTerm(frequencyOption.getTerm());
		descResponse.setSize(concordance.size());
		descResponse.setOperation(positive ?
				DescResponse.OperationType.POSITIVE_FILTER.toString() :
				DescResponse.OperationType.NEGATIVE_FILTER.toString());
		return descResponse;
	}

	private List<KWICLineDTO> elaboratingKWICLines(KWICLines kl) {
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
		int count = 0;
		int requestedSize = end - start;
		long now = System.currentTimeMillis();
		List<KWICLine> sentKwicLines = new ArrayList<>();
		// è possibile che durante le istruzioni del ciclo while non siano pronti i risultati,
		// ma che la concordance sia marcata come finished sull'ultima istruzione. Per questo imponiamo
		// un tempo minimo di esecuzione
		boolean withContextConcordance = queryRequest.getContextConcordanceQueryRequest() != null;
		boolean withConcordanceFilter = queryRequest.getFilterConcordanceQueryRequest() != null;

		concordance.load_from_query(corpus, cql, 0, 0); // il cql finale al posto di qr-getWord()
		Thread.sleep(50 );
		while (!concordance.finished() || (System.currentTimeMillis() - now) < QueryExecutor.MINIMUM_EXECUTION_TIME+200) {
			executeQueryStep(queryRequest, corpus, start, end, concordance, requestedSize, now, sentKwicLines,
					withContextConcordance, withConcordanceFilter);
		}
		executeQueryStep(queryRequest, corpus, start, end, concordance, requestedSize, now, sentKwicLines,
				withContextConcordance, withConcordanceFilter);
		concordance.delete();
		corpus.delete();
	}

	private void executeQueryStep(QueryRequest queryRequest, Corpus corpus, int start, int end, Concordance concordance,
			int requestedSize, long now, List<KWICLine> sentKwicLines, boolean withContextConcordance,
			boolean withConcordanceFilter) throws JsonProcessingException, InterruptedException {
		int count;
		QueryResponse queryResponse = new QueryResponse(queryRequest.getId());
		queryResponse.setId(queryRequest.getId());

		if (withContextConcordance) {
			List<DescResponse> descResponses = this.contextConcordance(concordance, queryRequest);
			queryResponse.getDescResponses().addAll(descResponses);
		}
		if (withConcordanceFilter) {
			//TODO
			DescResponse descResponse = this.filterConcordance(concordance, queryRequest);
			queryResponse.getDescResponses().add(descResponse);
		}
		List<KWICLine> kwicLines = new ArrayList<>();
		KWICLines kl = new KWICLines(corpus, concordance.RS(false, start, end), "50#", "50#", "word", "word",
				"up,g,err,corr", "doc", 100);
		count = concordance.size();
		Integer maxLine = requestedSize;
		if (maxLine > count) {
			maxLine = count;
		}
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
		Thread.sleep(500);
		queryResponse.setCurrentSize(concordance.size());
		queryResponse.setInProgress(!concordance.finished());
		System.out.println(this.objectMapper.writeValueAsString(queryResponse)); //scrive il risultato in JSON
		System.out.println(String.format("### 2. Finished: %s\t Time: %d\t Size: %d", "" + concordance.finished(),
				(System.currentTimeMillis() - now), concordance.fullsize()));
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
			Path cachePath = Paths.get(this.cacheDir);
			if (!Files.exists(cachePath)) {
				Files.createDirectory(cachePath);
			}
			cachePath = Paths.get(this.cacheDir + corpusName + "/");
			if (!Files.exists(cachePath)) {
				Files.createDirectory(cachePath);
			}
			String fileWordConcordance =
					queryTag.getName() + "_" + queryTag.getValue().replace(" ", "_") + QueryExecutor.EXT_CONC;
			try (Stream<Path> cachePaths = Files.list(cachePath)) {
				Optional<Path> pathWordOptional = cachePaths.filter(
						file -> file.getFileName().toString().contains(fileWordConcordance)).findFirst();
				if (pathWordOptional.isPresent()) {
					concordance = new Concordance(corpus, pathWordOptional.get().toString());
				} else {
					concordance = new Concordance();
					// al posto di phrase su corpora.dipertimentodieccellenza, stessi risultati
					concordance.load_from_query(corpus, this.getCqlFromQueryRequest(queryRequest), 10000000, 0);
					long now = System.currentTimeMillis();
					while (!concordance.finished()
							|| (System.currentTimeMillis() - now) < QueryExecutor.MINIMUM_EXECUTION_TIME) {
						Thread.sleep(5);
					}
					concordance.save(this.cacheDir + corpusName + "/" + fileWordConcordance);
				}
			}
			concordance.sync();
			CollocItems collocItems = new CollocItems(concordance,
					this.collocationsAttributes.get(collocationQueryRequest.getAttribute()),
					collocationQueryRequest.getSortBy() != null ? collocationQueryRequest.getSortBy().charAt(0) : 'm',
					collocationQueryRequest.getMinFreqCorpus(), collocationQueryRequest.getMinFreqRange(),
					collocationQueryRequest.getRangeFrom(), collocationQueryRequest.getRangeTo(), end);
			List<CollocationItem> resultCollocations = new ArrayList<>();
			while (!collocItems.eos()) {
				while (!collocItems.eos()) {
					CollocationItem collocationItem = new CollocationItem();
					collocationItem.setWord(collocItems.get_item());
					collocationItem.setConcurrenceCount(collocItems.get_cnt());
					collocationItem.setCandidateCount(collocItems.get_freq());
					List<String> functionsToBeShowed = collocationQueryRequest.getShowFunc();
					if (functionsToBeShowed == null || functionsToBeShowed.isEmpty()) {
						functionsToBeShowed = new ArrayList<>();
						functionsToBeShowed.add("m");
					}
					// System.out.println(functionsToBeShowed);
					for (String func : functionsToBeShowed) {
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
			QueryResponse queryResponse = new QueryResponse(queryRequest.getId());
			queryResponse.setCurrentSize(resultCollocations.size());
			queryResponse.setCollocations(resultCollocations);
			queryResponse.setInProgress(!concordance.finished());
			System.out.println(this.objectMapper.writeValueAsString(queryResponse)); //scrive il risultato in JSON
			concordance.delete();
			corpus.delete();
		}

	}

	private void executeQueryFrequency(String corpusName, QueryRequest queryRequest) throws IOException {
		final Corpus corpus = new Corpus(corpusName);
		final String cql = this.getCqlFromQueryRequest(queryRequest);
		final int start = queryRequest.getStart();
		final int end = queryRequest.getEnd();
		final Concordance concordance = new Concordance();
		concordance.load_from_query(corpus, cql, QueryExecutor.SAMPLE_SIZE, QueryExecutor.FULL_SIZE);

		int count = concordance.size();
		QueryResponse queryResponse = new QueryResponse(queryRequest.getId());
		queryResponse.setCurrentSize(count);
		boolean multiFreq = queryRequest.getQueryType()
				.equals(QueryRequest.RequestType.MULTI_FREQUENCY_QUERY_REQUEST.toString());

		queryResponse.setFrequency(this.xfreqDist(concordance, corpus, queryRequest, start, end, multiFreq, 300, 0L));

		queryResponse.getFrequency()
				.setOperation(multiFreq ?
						QueryRequest.RequestType.PN_MULTI_FREQ_CONCORDANCE_QUERY_REQUEST.toString() :
						QueryRequest.RequestType.PN_METADATA_FREQ_CONCORDANCE_QUERY_REQUEST.toString());

		System.out.println(this.objectMapper.writeValueAsString(queryResponse));
		concordance.delete();
		corpus.delete();
	}

	private void executeQueryPNFrequencyConcordance(String corpusName, QueryRequest queryRequest)
			throws InterruptedException, IOException {
		final Corpus corpus = new Corpus(corpusName);
		final String cql = this.getCqlFromQueryRequest(queryRequest);
		final int start = queryRequest.getStart();
		final int end = queryRequest.getEnd();
		final Concordance concordance = new Concordance();
		int count = 0;
		int requestedSize = end - start;
		long now = System.currentTimeMillis();
		List<KWICLine> sentKwicLines = new ArrayList<>();
		// è possibile che durante le istruzioni del ciclo while non siano pronti i risultati,
		// ma che la concordance sia marcata come finished sull'ultima istruzione. Per questo imponiamo
		// un tempo minimo di esecuzione
		concordance.load_from_query(corpus, cql, 0, 0); // il cql finale al posto di qr-getWord()
		int whileCount = 1;
		while (!concordance.finished()) {
			Thread.sleep(50);
			System.out.println(String.format("### WHILE %d", whileCount++));
		}
		count = concordance.size();
		List<DescResponse> descResponses = new ArrayList<>();
		QueryResponse queryResponse = new QueryResponse(queryRequest.getId());
		FrequencyQueryRequest frequencyQueryRequest = queryRequest.getFrequencyQueryRequest();
		queryResponse.setCurrentSize(count);
		List<FrequencyOption> frequencyOptionList = frequencyQueryRequest.getFreqOptList();
		boolean positive = frequencyQueryRequest.isPositive();
		boolean multiFreq = queryRequest.getQueryType()
				.equals(QueryRequest.RequestType.PN_MULTI_FREQ_CONCORDANCE_QUERY_REQUEST.toString());
		String category = frequencyQueryRequest.getCategory();
		for (FrequencyOption freqOpt : frequencyOptionList) {
			descResponses.add(
					this.elaboratingContext4ConcordanciesFromFrequenciesPN(concordance, freqOpt, positive, multiFreq,
							category));
		}
		queryResponse.getDescResponses().addAll(descResponses);

		List<KWICLine> kwicLines = new ArrayList<>();
		Integer maxLine = requestedSize;
		if (maxLine > count) {
			maxLine = count;
		}
		KWICLines kl = new KWICLines(corpus, concordance.RS(false, start, end), "50#", "50#", "word", "word",
				"up,g,err,corr", "doc", 100);
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
		if (!queryRequest.getSortQueryRequest().getMultilevel()) {
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
		QueryResponse queryResponse = new QueryResponse(queryRequest.getId());
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

	private void executeWideContextQuery(QueryRequest queryRequest) throws JsonProcessingException {
		final WideContextRequest wideContextRequest = queryRequest.getWideContextRequest();
		System.out.println(String.format("### 1. Wide context: %s %d %d", wideContextRequest.getCorpusName(),
				wideContextRequest.getPos(), wideContextRequest.getHitlen()));
		final Corpus corpus = new Corpus(wideContextRequest.getCorpusName());
		CorpRegion corpRegion = new CorpRegion(corpus, "word", "p,g,err,corr");
		final Long pos = wideContextRequest.getPos();
		StrVector leftRegion = corpRegion.region(pos - 40, pos);
		final Integer hitlen = wideContextRequest.getHitlen();
		StrVector kwicRegion = corpRegion.region(pos, pos + hitlen);
		StrVector rightRegion = corpRegion.region(pos + hitlen, pos + hitlen + 40);
		String leftContext = leftRegion.stream().collect(Collectors.joining(" "));
		String kwicContext = kwicRegion.stream().collect(Collectors.joining(" "));
		String rightContext = rightRegion.stream().collect(Collectors.joining(" "));
		QueryResponse queryResponse = new QueryResponse(queryRequest.getId());
		queryResponse.getWideContextResponse().setLeftContext(ContextUtils.removeContextTags(leftContext));
		queryResponse.getWideContextResponse().setKwic(ContextUtils.removeContextTags(kwicContext));
		queryResponse.getWideContextResponse().setRightContext(ContextUtils.removeContextTags(rightContext));
		queryResponse.setInProgress(false);
		System.out.println(this.objectMapper.writeValueAsString(queryResponse));
		System.out.println(String.format("### 2. Finished Wide context: %s %d %d", wideContextRequest.getCorpusName(),
				wideContextRequest.getPos(), wideContextRequest.getHitlen()));
	}

	private DescResponse filterConcordance(Concordance concordance, QueryRequest queryRequest) {
		DescResponse descResponses = new DescResponse();
		ContextConcordanceQueryRequest contextConcordanceQueryRequest = queryRequest.getContextConcordanceQueryRequest();

		//		if (QueryRequest.RequestType.CONTEXT_QUERY_REQUEST.toString().equals(queryRequest.getQueryType())) {
		//			ContextConcordanceItem cci = contextConcordanceQueryRequest.getItems().get(0);
		//			if (ContextConcordanceItem.LemmaFilterType.ALL.toString().equals(cci.getLemmaFilterType())) {
		//				Arrays.asList(cci.getTerm().split(" ")).forEach(term -> {
		//					String query = String.format("[%s=\"%s\"];", cci.getAttribute(), term);
		//					this.singleContextConcordance(concordance, descResponses, cci,
		//							DescResponse.OperationType.POSITIVE_FILTER, query, term);
		//				});
		//			} else if (ContextConcordanceItem.LemmaFilterType.ANY.toString().equals(cci.getLemmaFilterType())) {
		//				String query = QueryExecutor.getQuery4Context(cci);
		//				this.singleContextConcordance(concordance, descResponses, cci,
		//						DescResponse.OperationType.POSITIVE_FILTER, query, cci.getTerm());
		//			} else if (ContextConcordanceItem.LemmaFilterType.NONE.toString().equals(cci.getLemmaFilterType())) {
		//				String query = QueryExecutor.getQuery4Context(cci);
		//				this.singleContextConcordance(concordance, descResponses, cci,
		//						DescResponse.OperationType.NEGATIVE_FILTER, query, cci.getTerm());
		//			}
		//		} else {
		//			contextConcordanceQueryRequest.getItems().forEach(cci -> {
		//				String query = String.format("[%s=\"%s\"];", cci.getAttribute(), cci.getTerm());
		//				this.singleContextConcordance(concordance, descResponses, cci,
		//						DescResponse.OperationType.POSITIVE_FILTER, query, cci.getTerm());
		//			});
		//		}
		return descResponses;
	}

	private String freqCritBuild(QueryRequest queryRequest, boolean multi) {
		List<String> resList = new ArrayList<>();
		if (multi) {
			//multi
			List<FrequencyOption> frequencyOptionList = queryRequest.getFrequencyQueryRequest().getFreqOptList();
			for (FrequencyOption frequencyOption : frequencyOptionList) {
				resList.add(this.oneLevelCrit("", frequencyOption.getAttribute(), "",
						this.fromAttributeToSymbolic(frequencyOption.getPosition()), "rc",
						frequencyOption.getIgnoreCase() ? "i" : "", "e", ""));
			}
			return String.join(" ", resList);
		} else {
			//metadata
			return queryRequest.getFrequencyQueryRequest().getCategory();
		}
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

	private void getCorpusInfo(QueryRequest queryRequest) throws JsonProcessingException {
		final String corpusName = queryRequest.getCorpus();
		System.out.println(String.format("### 1. Corpus info: %s", corpusName));
		CorpusInfoRetriever corpusInfoRetriever = new CorpusInfoRetriever();
		QueryResponse queryResponse = new QueryResponse(queryRequest.getId());
		queryResponse.setCorpusInfo(corpusInfoRetriever.retrieveCorpusInfo(corpusName));
		queryResponse.setInProgress(false);
		System.out.println(this.objectMapper.writeValueAsString(queryResponse));
		System.out.println(String.format("### 2. Finished  Corpus info: %s", corpusName));
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

	private void initMaps() {
		this.STAT_DESC.put('t', "T-score");
		this.STAT_DESC.put('m', "MI");
		this.STAT_DESC.put('3', "MI3");
		this.STAT_DESC.put('l', "log likelihood");
		this.STAT_DESC.put('s', "min. sensitivity");
		this.STAT_DESC.put('p', "MI.log_f");
		this.STAT_DESC.put('r', "relative freq.");
		this.STAT_DESC.put('f', "absolute freq.");
		this.STAT_DESC.put('d', "logDice");

		this.collocationsAttributes.put("WORD", "word");
		this.collocationsAttributes.put("TAG", "tag");
		this.collocationsAttributes.put("LEMMA", "lemma");

		this.fromCodes.put("lc", "<0>");
		this.fromCodes.put("rc", ">0");
		this.fromCodes.put("kl", "<0");
		this.fromCodes.put("kr", ">0");

		this.contextConcordances.put("ALL", "P");
		this.contextConcordances.put("ANY", "P");
		this.contextConcordances.put("NONE", "N");
	}

	// chiamata a metodo getCQLfromqueryrequest che trasforma i dati di input in stringa cql
	public void manageQueryRequest(String corpus, QueryRequest queryRequest) throws Exception {
		try {
			if (queryRequest.getCorpusMetadatum() != null && !queryRequest.getCorpusMetadatum().isEmpty()) {
				this.retrieveMetadata(corpus, queryRequest);
			} else if (!QueryRequest.RequestType.contain(queryRequest.getQueryType())) {
				throw new Exception("Invalid RequestType");
			} else {
				QueryRequest.RequestType requestType = QueryRequest.RequestType.valueOf(queryRequest.getQueryType());

				switch (requestType) {
				case SORT_REQUEST:
					this.executeQuerySort(corpus, queryRequest);
					break;
				case COLLOCATION_REQUEST:
					this.executeQueryCollocation(corpus, queryRequest);
					break;
				case METADATA_FREQUENCY_QUERY_REQUEST:
				case MULTI_FREQUENCY_QUERY_REQUEST:
					this.executeQueryFrequency(corpus, queryRequest);
					break;
				case PN_METADATA_FREQ_CONCORDANCE_QUERY_REQUEST:
				case PN_MULTI_FREQ_CONCORDANCE_QUERY_REQUEST:
					this.executeQueryPNFrequencyConcordance(corpus, queryRequest);
					break;
				case WIDE_CONTEXT_QUERY_REQUEST:
					if (queryRequest.getWideContextRequest() != null
							&& queryRequest.getWideContextRequest().getPos() != null) {
						this.executeWideContextQuery(queryRequest);
					}
					break;
				case CORPUS_INFO:
					if (corpus != null && !corpus.isEmpty()) {
						this.getCorpusInfo(queryRequest);
					}
					break;
				case CONTEXT_QUERY_REQUEST:
				case VISUAL_QUERY_REQUEST:
				case TEXTUAL_QUERY_REQUEST:
				case FILTER_CONCORDANCE_QUERY_REQUEST: // forse non serve
				default:
					System.out.println("*** CQL *** " + this.getCqlFromQueryRequest(queryRequest)); //debug
					this.executeQuery(corpus, queryRequest);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private String oneLevelCrit(String prefix, String attr, String ctx, String pos, String fcode, String icase,
			String bward, String empty) {
		StringBuilder res = new StringBuilder("");
		res.append(String.format("%s%s/%s%s%s", prefix, attr, icase, bward, empty));
		if (ctx.isEmpty()) {
			ctx = String.format("%s%s", pos, this.fromCodes.get(fcode));
		} else if (ctx.contains("~") && attr.contains(".")) {
			ctx = ctx.split("~")[0];
		}
		res.append(ctx);
		return res.toString();
	}

	// recupera valori dei metadati che sono sui singoli documenti
	private void retrieveMetadata(String corpusName, QueryRequest queryRequest) throws JsonProcessingException {
		final Corpus corpus = new Corpus(corpusName);
		PosAttr posAttr = corpus.get_attr(queryRequest.getCorpusMetadatum());
		final int posAttrRange = posAttr.id_range();
		QueryResponse queryResponse = new QueryResponse(queryRequest.getId());
		for (int i = 0; i < posAttrRange; i++) {
			if (posAttr.freq(i) > 0) {
				queryResponse.getMetadataValues().add(posAttr.id2str(i));
			}
		}
		queryResponse.setCurrentSize(queryResponse.getMetadataValues().size());
		System.out.println(this.objectMapper.writeValueAsString(queryResponse));
	}

	private void singleContextConcordance(Concordance concordance, List<DescResponse> descResponses,
			ContextConcordanceItem cci, DescResponse.OperationType operationType, String query, String term) {
		final String window = cci.getWindow();
		final Integer tokens = cci.getTokens();
		String lcTx = "";
		String rcTx = "";
		int rank;
		if ("BOTH".equals(window)) {
			lcTx += (-tokens);
			rcTx += tokens;
			rank = 1;
		} else if ("LEFT".equals(window)) {
			lcTx += (-tokens);
			rcTx += "-1";
			rank = -1;
		} else {
			lcTx = "1";
			rcTx += tokens;
			rank = 1;
		}
		int collNum = concordance.numofcolls() + 1;
		concordance.set_collocation(collNum, query, lcTx, rcTx, rank, true);
		concordance.delete_pnfilter(collNum, operationType.equals(DescResponse.OperationType.POSITIVE_FILTER));
		DescResponse descResponse = new DescResponse();
		descResponse.setNiceArg(term.replace(" ", ", "));
		descResponse.setTerm(term);
		descResponse.setAttribute(cci.getAttribute());
		descResponse.setWindow(window);
		descResponse.setTokens(tokens);
		descResponse.setSize(concordance.size());
		//TODO to check
		descResponse.setOperation(operationType.toString());
		descResponses.add(descResponse);
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

	private FrequencyItem xfreqDist(Concordance concordance, Corpus corpus, QueryRequest queryRequest, int start,
			int end, boolean multi, int normWidth, long wlMaxFreq) {
		FrequencyQueryRequest frequencyQueryRequest = queryRequest.getFrequencyQueryRequest();
		final int freqLimit = frequencyQueryRequest.getFrequencyLimit();
		final String frequencyType = frequencyQueryRequest.getFrequencyType();
		final Integer frequencyColSort = frequencyQueryRequest.getFrequencyColSort();
		final String frequencyTypeSort = frequencyQueryRequest.getFrequencyTypeSort();
		FrequencyItem result = new FrequencyItem();
		StrVector words = new StrVector();
		NumVector freqs = new NumVector();
		NumVector norms = new NumVector();
		List<FrequencyResultLine> frlList = new ArrayList<>();
		String crit = this.freqCritBuild(queryRequest, multi);
		corpus.freq_dist(concordance.RS(), crit + " 0", freqLimit, words, freqs, norms);
		float[] toBars = this.computeCorrection(freqs, norms, normWidth, corpus);
		int noRel = multi ? 1 : 0;
		int normHeight = 15;
		long maxF = freqs.stream().max(Long::compareTo).orElse(0L);
		wlMaxFreq = wlMaxFreq > 0 ? wlMaxFreq : maxF;

		if (multi) {
			//multilevel
			for (int i = 0; i < words.size(); i++) {
				if (freqs.get(i) <= wlMaxFreq) {
					FrequencyResultLine frl = new FrequencyResultLine();
					frl.setWord(Arrays.asList(words.get(i).split("\t")));
					frl.setFreq(freqs.get(i));
					frl.setfBar((long) (freqs.get(i) * toBars[0]) + 1);
					frl.setNoRel(noRel);
					frlList.add(frl);
				}
			}
		} else if (toBars[1] > 0F) {
			//metadata
			float maxR = this.calculateMaxRel(freqs, norms, toBars);
			for (int i = 0; i < words.size(); i++) {
				if (freqs.get(i) <= wlMaxFreq) {
					FrequencyResultLine frl = new FrequencyResultLine();
					frl.setWord(Arrays.asList(words.get(i).split("\t")));
					frl.setFreq(freqs.get(i));
					frl.setfBar((long) (freqs.get(i) * toBars[0]) + 1);
					frl.setNorm(norms.get(i));
					frl.setnBar((int) (norms.get(i) * toBars[1]));
					frl.setRelBar(
							1L + (int) ((freqs.get(i) * toBars[0] * normWidth) / (norms.get(i) * toBars[1] * maxR)));
					frl.setNoRel(noRel);
					frl.setFreqBar((normHeight * (freqs.get(i) + 1)) / (maxF + 1) + 1);
					BigDecimal bd = BigDecimal.valueOf(((freqs.get(i) * toBars[0]) / (norms.get(i) * toBars[1]) * 100));
					bd = bd.setScale(1, RoundingMode.HALF_UP);
					frl.setRel(bd.floatValue());
					frlList.add(frl);
				}
			}
			if (frequencyQueryRequest.getIncludeCategoriesWithNoHits() && freqLimit == 0
					&& frequencyQueryRequest.getCategory().contains(".")) {
				List<String> allVals = new ArrayList<>();
				PosAttr attr = corpus.get_attr(frequencyQueryRequest.getCategory());
				for (int i = 0; i < attr.id_range(); i++) {
					allVals.add(attr.id2str(i));
				}
				for (String val : allVals) {
					if (words.stream().filter(w -> w.contains(val)).findAny().isPresent()) {
						continue;
					}
					FrequencyResultLine frl = new FrequencyResultLine();
					frl.setWord(Arrays.asList(new String[] { val }));
					frl.setFreq(0);
					frl.setfBar(0);
					frl.setNorm(0);
					frl.setnBar(0);
					frl.setRelBar(0);
					frl.setNoRel(noRel);
					frl.setFreqBar(0);
					frl.setRel(0);
					frlList.add(frl);
				}
			}
		}
		//Include Categories With No Hits
		if (frequencyQueryRequest.getIncludeCategoriesWithNoHits() != null
				&& frequencyQueryRequest.getIncludeCategoriesWithNoHits() && frequencyQueryRequest.getFrequencyLimit()
				.equals(0)) {
			PosAttr posAttr = corpus.get_attr(frequencyQueryRequest.getCategory());

			for (int i = 0; i < posAttr.id_range(); i++) {
				String attr = posAttr.id2str(i);
				if (frlList.stream().anyMatch(item -> item.getWord().get(0).equals(attr))) {
					continue;
				}
				FrequencyResultLine frequencyResultLine = new FrequencyResultLine();
				frequencyResultLine.getWord().add(attr);
				frequencyResultLine.setFreq(0L);
				frequencyResultLine.setRel(0F);
				frequencyResultLine.setNorm(0L);
				frequencyResultLine.setnBar(0);
				frequencyResultLine.setRelBar(0L);
				frequencyResultLine.setNoRel(0L);
				frequencyResultLine.setFreqBar(0L);
				frequencyResultLine.setfBar(0L);
				frlList.add(frequencyResultLine);
			}
		}
		//sorting
		List<FrequencyResultLine> frequencyItemList;
		if ("freq".equals(frequencyType) || "rel".equals(frequencyType)) {
			if ("asc".equals(frequencyTypeSort)) {
				frequencyItemList = "freq".equals(frequencyType) ?
						frlList.stream()
								.sorted(Comparator.comparing(FrequencyResultLine::getFreq))
								.collect(Collectors.toList()) :
						frlList.stream()
								.sorted(Comparator.comparing(FrequencyResultLine::getRel))
								.collect(Collectors.toList());
			} else {
				frequencyItemList = "freq".equals(frequencyType) ?
						frlList.stream()
								.sorted(Comparator.comparing(FrequencyResultLine::getFreq).reversed())
								.collect(Collectors.toList()) :
						frlList.stream()
								.sorted(Comparator.comparing(FrequencyResultLine::getRel).reversed())
								.collect(Collectors.toList());
			}
		} else {
			if ("asc".equals(frequencyTypeSort)) {
				FrequencyLevelComparator frequencyLevelComparator = new FrequencyLevelComparator();
				frequencyLevelComparator.setLevel(frequencyColSort);
				frequencyItemList = frlList.stream().sorted(frequencyLevelComparator).collect(Collectors.toList());
			} else {
				FrequencyLevelComparator frequencyLevelComparator = new FrequencyLevelComparator();
				frequencyLevelComparator.setLevel(frequencyColSort);
				frequencyItemList = frlList.stream()
						.sorted(frequencyLevelComparator.reversed())
						.collect(Collectors.toList());
			}
		}
		end = end >= frequencyItemList.size() ? frequencyItemList.size() : end;
		result.setItems(frequencyItemList.subList(start, end));
		result.setMaxFreq(maxF);
		result.setMaxRel(frlList.stream().map(FrequencyResultLine::getRel).max(Float::compareTo).orElse(0.0f));
		result.setTotalFreq(freqs.stream().reduce(0L, Long::sum));
		result.setTotal(frlList.size());
		result.setHead(crit);
		return result;
	}

}