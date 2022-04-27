package it.drwolf.impaqts.wrapper.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sketchengine.manatee.Concordance;
import com.sketchengine.manatee.Corpus;
import com.sketchengine.manatee.IntVector;
import com.sketchengine.manatee.KWICLines;
import com.sketchengine.manatee.PosAttr;
import com.sketchengine.manatee.StrVector;
import it.drwolf.impaqts.wrapper.dto.KWICLine;
import it.drwolf.impaqts.wrapper.dto.QueryRequest;
import it.drwolf.impaqts.wrapper.dto.QueryResponse;
import it.drwolf.impaqts.wrapper.dto.SortOption;
import it.drwolf.impaqts.wrapper.query.QueryPattern;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QueryExecutor {

	public static final String STRUCTATTRLIST = "STRUCTATTRLIST";
	public static final String ATTRLIST = "ATTRLIST";
	public static final String STRUCTLIST = "STRUCTLIST";
	public static final String DOCSTRUCTURE = "DOCSTRUCTURE";
	//sort
	public static final String LEFT_CONTEXT = "LEFT_CONTEXT";
	public static final String RIGHT_CONTEXT = "RIGHT_CONTEXT";
	public static final String NODE_CONTEXT = "NODE_CONTEXT";

	private static final Integer MINIMUM_EXECUTION_TIME = 100;
	private final ObjectMapper objectMapper;

	public QueryExecutor() {
		this.objectMapper = new ObjectMapper();
	}

	// corpusName, CQL, start, end
	private void executeQuery(String corpusName, String cql, int start, int end)
			throws InterruptedException, IOException {
		final Corpus corpus = new Corpus(corpusName);
		final Concordance concordance = new Concordance();
		concordance.load_from_query(corpus, cql, 0, 0); // il cql finale al posto di qr-getWord()
		int count = 0;
		int requestedSize = end - start;
		long now = System.currentTimeMillis();
		List<KWICLine> sentKwicLines = new ArrayList<>();
		// è possibile che durante le istruzioni del ciclo while non siano pronti i risultati,
		// ma che la concordance sia marcata come finished sull'ultima istruzione. Per questo imponiamo
		// un tempo minimo di esecuzione
		while (!concordance.finished() || (System.currentTimeMillis() - now) < QueryExecutor.MINIMUM_EXECUTION_TIME) {
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

	private void executeQuerySort(String corpusName, QueryRequest queryRequest)
			throws InterruptedException, IOException {
		final Corpus corpus = new Corpus(corpusName);
		//				final Concordance concordance = new Concordance(corpus,
		//				String.format("[word=\"%s\" | lemma=\"%s\"]", "finlandia", "finlandia"), 10000000, -1);
		final Concordance concordance = new Concordance(corpus,
				String.format(this.getCqlFromQueryRequest(queryRequest), queryRequest.getWord()), 10000000, -1);

		/*if (queryRequest.getSortQueryRequest().getSortKey().equals(QueryExecutor.LEFT_CONTEXT)) {
			concordance.sort("word/i -1<0~-3<0", false);
		} else if (queryRequest.getSortQueryRequest().getSortKey().equals(QueryExecutor.RIGHT_CONTEXT)) {
			concordance.sort("word/i 1<0~3<0", false);
		} else { //NODE_CONTEXT
			concordance.sort("word/i 0<0~0<0", false);
		}*/

		StrVector vals = new StrVector();
		IntVector idx = new IntVector();
		String critParam;
		String crit;

		//		simple sort
		if (queryRequest.getSortQueryRequest().getMultilevelSort().isEmpty()) {
			if (queryRequest.getSortQueryRequest().getSortKey().equals(QueryExecutor.LEFT_CONTEXT)) {
				critParam = String.format("-1<0~-%d<0", queryRequest.getSortQueryRequest().getNumberTokens());
			} else if (queryRequest.getSortQueryRequest().getSortKey().equals(QueryExecutor.RIGHT_CONTEXT)) {
				critParam = String.format("1<0~%d<0", queryRequest.getSortQueryRequest().getNumberTokens());
			} else { //NODE_CONTEXT
				critParam = "0<0~0<0";
			}
			crit = String.format("%s/%s%s %s", queryRequest.getSortQueryRequest().getAttribute(),
					queryRequest.getSortQueryRequest().getIgnoreCase() ? "i" : "",
					queryRequest.getSortQueryRequest().getBackward() ? "r" : "", critParam);
		} else { //		multilevel sort
			List<String> critList = new ArrayList<>();
			for (SortOption sortOption : queryRequest.getSortQueryRequest().getMultilevelSort()) {
				if (sortOption.getPosition().contains("L")) {//Left
					critParam = String.format("-1<0~-%s<0", sortOption.getPosition().charAt(0));
				} else if (sortOption.getPosition().contains("R")) {//Right
					critParam = String.format("1<0~%s<0", sortOption.getPosition().charAt(0));
				} else { //Node
					critParam = "0<0~0<0";
				}
				critList.add(
						String.format("%s/%s%s %s", sortOption.getAttribute(), sortOption.getIgnoreCase() ? "i" : "",
								sortOption.getBackward() ? "r" : "", critParam));
			}
			crit = critList.stream().collect(Collectors.joining(" "));
		}

		concordance.sort_idx(crit, vals, idx, false);

		int count = 0;
		long now = System.currentTimeMillis();
		List<KWICLine> sentKwicLines = new ArrayList<>();
		// è possibile che durante le istruzioni del ciclo while non siano pronti i risultati,
		// ma che la concordance sia marcata come finished sull'ultima istruzione. Per questo imponiamo
		// un tempo minimo di esecuzione
		//while (!concordance.finished() || (System.currentTimeMillis() - now) < QueryExecutor.MINIMUM_EXECUTION_TIME) {
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
		KWICLines kl = new KWICLines(corpus, concordance.RS(true, 0, 4000), "40#", "40#", "word,tag,lemma", "word",
				"p,g,err,corr", "=doc.sito,=doc.categoria");
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
		//}
		concordance.delete();
		corpus.delete();
	}

	private void executeQuerySortLeft(String corpusName, String cql, int start, int end)
			throws InterruptedException, IOException {
		final Corpus corpus = new Corpus(corpusName);
		final Concordance concordance = new Concordance(corpus, "[word=\"finlandia\" | lemma=\"finlandia\"]", 10000000,
				-1);
		//left
		concordance.sort("word/i -1<0~-3<0", false);
		//right
		concordance.sort("word/i 1<0~3<0", false);
		//node
		concordance.sort("word/i 0<0~0<0", false);

		//
		//		while (true) {
		//			if (concordance.finished()) {
		//				break;
		//			}
		//		}

		//
		StrVector vals = new StrVector();
		IntVector idx = new IntVector();
		/*vals = manatee.StrVector(); idx = manatee.IntVector()
		if '.' in crit.split('/')[0]: just_letters = False
    else: just_letters = True
		conc.sort_idx(crit, vals, idx, just_letters)*/

		concordance.sort_idx("word/i 0<0~0<0", vals, idx, false);

		int count = 0;
		int requestedSize = end - start;
		long now = System.currentTimeMillis();
		List<KWICLine> sentKwicLines = new ArrayList<>();
		// è possibile che durante le istruzioni del ciclo while non siano pronti i risultati,
		// ma che la concordance sia marcata come finished sull'ultima istruzione. Per questo imponiamo
		// un tempo minimo di esecuzione
		//while (!concordance.finished() || (System.currentTimeMillis() - now) < QueryExecutor.MINIMUM_EXECUTION_TIME) {
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
		KWICLines kl = new KWICLines(corpus, concordance.RS(true, 0, 4000), "40#", "40#", "word,tag,lemma", "word",
				"p,g,err,corr", "=doc.sito,=doc.categoria");
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
		//}
		concordance.delete();
		corpus.delete();
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

	// chiamata a metodo getCQLfromqueryrequest che trasforma i dati di input in stringa cql
	public void manageQueryRequest(String corpus, QueryRequest queryRequest) {
		try {
			if (queryRequest.getCorpusMetadatum() != null && !queryRequest.getCorpusMetadatum().isEmpty()) {
				this.retrieveMetadata(corpus, queryRequest.getCorpusMetadatum());
			} else {
				if (queryRequest.getSortQueryRequest() != null) {
					this.executeQuerySort("coliweb", queryRequest); //sort
				} else if (queryRequest.getCollocationQueryRequest() != null) {
					//collocation
				} else {
					System.out.println("*** CQL *** " + this.getCqlFromQueryRequest(queryRequest)); //debug
					this.executeQuery(corpus, this.getCqlFromQueryRequest(queryRequest), queryRequest.getStart(),
							queryRequest.getEnd());
				}
			}
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
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

}
