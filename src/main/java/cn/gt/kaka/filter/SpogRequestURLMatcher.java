package cn.gt.kaka.filter;

import org.springframework.security.web.util.matcher.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class object will use in PostAuthenticationFilter to filer a request to
 * check if authorization is required. Created by vishal.domale
 * 
 * @version 0.0.1
 */

public class SpogRequestURLMatcher implements RequestMatcher {

	private OrRequestMatcher blackmatcher;
	private OrRequestMatcher whitematcher;
	
	private final static RequestMatcher anymatcher=AnyRequestMatcher.INSTANCE;
	private final static RequestMatcher nonematcher=new NegatedRequestMatcher(AnyRequestMatcher.INSTANCE);

	public SpogRequestURLMatcher() {
		this(new String[] {}, new String[] {});
	}

	public SpogRequestURLMatcher(List<String> pathsToSkip, String processingPath) {
		this(pathsToSkip.toArray(new String[] {}), new String[] { processingPath });
	}

	public SpogRequestURLMatcher(String[] whitePathList, String[] blackPathList) {
		List<RequestMatcher> whiteList = new ArrayList<>();
		if(whitePathList.length==0)whiteList.add(nonematcher);
		for (String path : whitePathList) {
			whiteList.add(new AntPathRequestMatcher(path));
		}
		List<RequestMatcher> blackList = new ArrayList<>();
		if(blackPathList.length==0)blackList.add(anymatcher);
		for (String path : blackPathList) {
			blackList.add(new AntPathRequestMatcher(path));
		}

		whitematcher = new OrRequestMatcher(whiteList);
		blackmatcher = new OrRequestMatcher(blackList);
	}

	private SpogRequestURLMatcher(List<RequestMatcher> whiteList, List<RequestMatcher> blackList) {
		if(whiteList.isEmpty()) {
			whiteList.add(nonematcher);
		}
		if(blackList.isEmpty()) {
			blackList.add(anymatcher);
		}
		whitematcher = new OrRequestMatcher(whiteList);
		blackmatcher = new OrRequestMatcher(blackList);
	}

	/**
	 * Return false request url path contains in pathsToSkip list. Return true when
	 * url pattern /api/** to match an request url.
	 * 
	 * @param request
	 * @return boolean
	 */
	@Override
	public boolean matches(HttpServletRequest request) {
		if (whitematcher.matches(request)) {
			return false;
		}
		return blackmatcher.matches(request) ? true : false;
	}

	public static class MatcherBuilder {

		private List<String[]> whiteList = new LinkedList<>();
		private List<String[]> blackList = new LinkedList<>();
		private String base="";

		MatcherBuilder(String base){
			if(base!=null)
				this.base=base;
		}
		
		public MatcherBuilder addWhiteMatchers(String... whitePaths) {
			if (whitePaths != null) {
				for (String whitePath : whitePaths) {
					addWhiteMatcher(whitePath, null);
				}
			}
			return this;
		}

		public MatcherBuilder addBlackMatchers(String... blackPaths) {
			if (blackPaths != null) {
				for (String blackPath : blackPaths) {
					addBlackMatcher(blackPath, null);
				}
			}
			return this;
		}

		public MatcherBuilder addWhiteMatcher(String whitePath) {
			addWhiteMatcher(whitePath, null);
			return this;
		}

		public MatcherBuilder addWhiteMatcher(String whitePath, String httpMethod) {
			whiteList.add(new String[] {whitePath,httpMethod});
			return this;
		}

		public MatcherBuilder addBlackMatcher(String blackPath) {
			addBlackMatcher(blackPath, null);
			return this;
		}

		public MatcherBuilder addBlackMatcher(String blackPath, String httpMethod) {
			blackList.add(new String[] {blackPath,httpMethod});
			return this;
		}

		public SpogRequestURLMatcher build() {
			return new SpogRequestURLMatcher(
					whiteList.stream().map(white->new AntPathRequestMatcher(base+white[0], white[1])).collect(Collectors.toList()),
					blackList.stream().map(black->new AntPathRequestMatcher(base+black[0], black[1])).collect(Collectors.toList())
			);
		}

	}

	public static MatcherBuilder getBuilderWithBase(String base) {
		return new MatcherBuilder(base);
	}
	
	public static MatcherBuilder getBuilder() {
		return getBuilderWithBase("");
	}
}
