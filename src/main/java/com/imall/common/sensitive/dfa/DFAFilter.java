package com.imall.common.sensitive.dfa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imall.common.utils.ResourceUtils;

import java.net.URL;
import java.util.*;

public class DFAFilter implements DFAForWordInterface {

	private static final Logger logger = LoggerFactory.getLogger(DFAFilter.class);

	private FilterSet set = new FilterSet();
	private Map<Integer, DFANode> nodes = new HashMap<Integer, DFANode>(1024, 1);
	private Set<Integer> splitWordSet = new HashSet<>();
	private static final char SIGN = '*';

	private static final String[] splits = new String[] {"!",".",",","#","$","%","&","*","(",")","|","?","/","@","\"","'",";","[","]","{","}","+","~","-","_","=","^","<",">"," ","！","。","，","￥","（","）","？","、","“","‘","；","【","】","—","…","《","》","1","2","3","4","5","6","7","8","9","0"};

	private boolean evict = false;

	private List<String> keywordList;

	private static DFAFilter instance;
	
	static{
		getInstance();
	}
	
	public synchronized static DFAFilter getInstance(){
		if(instance == null){
			reloadSensitiveWords();
		}
		return instance;
	}
	
	public static void reloadSensitiveWords(){
		String fileName = null;
		Set<String> allKeyword = new HashSet<String>();
		for(int i = 1; i < 10; i ++){
			fileName = "/config/sensitive_word/sensitive_word_" + i + ".txt";
			URL url = DFAFilter.class.getResource(fileName);
			if(url == null){
				continue;
			}
			allKeyword.addAll(ResourceUtils.loadFile(fileName));
		}
		DFAFilter temp = new DFAFilter(new ArrayList<String>(allKeyword));
		instance = temp;
	}
	
	private DFAFilter() {
		super();
	}

	private DFAFilter(List<String> keywordList) {
		this.keywordList = keywordList;
		init();
	}

	private void init() {
		addSensitiveWord(keywordList);
		addSplitWord(Arrays.asList(splits));
		evict = true;
	}

	public void evict(List<String> keywordList) {
		this.keywordList = keywordList;
		set = new FilterSet();
		nodes = new HashMap<Integer, DFANode>(1024, 1);
		splitWordSet = new HashSet<>();
		init();
	}

	private void addSplitWord(final List<String> words) {
		if (words != null && words.size() > 0) {
			char[] chs;
			for (String curr : words) {
				chs = curr.toCharArray();
				for (char c : chs) {
					splitWordSet.add(charConvert(c));
				}
			}
		}
	}

	private void addSensitiveWord(final List<String> words) {
		if (words != null && words.size() > 0) {
			char[] chs;
			int fchar;
			int lastIndex;
			DFANode fnode;
			for (String curr : words) {
				chs = curr.toCharArray();
				fchar = charConvert(chs[0]);
				if (!set.contains(fchar)) {
					set.add(fchar);
					fnode = new DFANode(fchar, chs.length == 1);
					nodes.put(fchar, fnode);
				} else {
					fnode = nodes.get(fchar);
					if (!fnode.isLast() && chs.length == 1)
						fnode.setLast(true);
				}
				lastIndex = chs.length - 1;
				for (int i = 1; i < chs.length; i++) {
					fnode = fnode.addIfNoExist(charConvert(chs[i]), i == lastIndex);
				}
			}
		}
	}

	public String searchKeyword (final String src) {
		try {
			if (!evict) {
				logger.error("init should done before used.");
				return src;
			}
			if (! src.equals("************") && MinorityChecker.hasMinority(src)) {
				return "************";
			}
			char[] chs = src.toCharArray();
			int length = chs.length;
			int currc;
			int k;
			DFANode node;
			for (int i = 0; i < length; i++) {
				currc = charConvert(chs[i]);
				//32 space.
				if (!set.contains(currc)) {
					continue;
				}
				node = nodes.get(currc);
				if (node == null)
					continue;
				boolean couldMark = false;
				int markNum = -1;
				if (node.isLast()) {
					couldMark = true;
					markNum = 0;
				}
				k = i;
				for (; ++k < length; ) {
					int temp = charConvert(chs[k]);
					if (splitWordSet.contains(temp) || temp == 32)
						continue;
					node = node.querySub(temp);
					if (node == null)
						break;
					if (node.isLast()) {
						couldMark = true;
						markNum = k - i;
					}
				}
				if (couldMark) {
					for (k = 0; k <= markNum; k++) {
						chs[k + i] = SIGN;
					}
					i = i + markNum;
				}
			}
			return new String(chs);
		} catch (Exception ex) {
			logger.error("ex={}", ex);
		}
		return src;

	}
	
	public final boolean isContains(final String src) {
		try {
			if (! evict) {
				logger.error("init should done before used.");
				return false;
			}
			char[] chs = src.toCharArray();
			int length = chs.length;
			int currc;
			int k;
			DFANode node;
			for (int i = 0; i < length; i++) {
				currc = charConvert(chs[i]);
				if (!set.contains(currc)) {
					continue;
				}
				node = nodes.get(currc);
				if (node == null)
					continue;
				boolean couldMark = false;
				if (node.isLast()) {
					couldMark = true;
				}
				k = i;
				for (; ++k < length;) {
					int temp = charConvert(chs[k]);
					if (splitWordSet.contains(temp))
						continue;
					node = node.querySub(temp);
					if (node == null)
						break;
					if (node.isLast()) {
						couldMark = true;
					}
				}
				if (couldMark) {
					return true;
				}
			}
			return false;
		} catch (Exception ex) {
			logger.error("ex={}", ex);
		}
		return false;
	}

	private static int charConvert(char src) {
		int r = BCConvert.qj2bj(src);
		return (r >= 'A' && r <= 'Z') ? r + 32 : r;
	}
}