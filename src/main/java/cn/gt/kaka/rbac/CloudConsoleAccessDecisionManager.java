package cn.gt.kaka.rbac;

import cn.gt.kaka.security.config.WhiteList;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.AbstractAccessDecisionManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class CloudConsoleAccessDecisionManager implements AccessDecisionManager{


	@Override
	public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes)
			throws AccessDeniedException, InsufficientAuthenticationException {
		String url = ((FilterInvocation) object).getRequestUrl().split("\\?")[0];
		if(skipWhiteListEndpoints(url)){
			return;
		}
		System.out.println("进入了验证");
	}

	@Override
	public boolean supports(ConfigAttribute attribute) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return true;
	}
	
	private boolean skipWhiteListEndpoints(String url) {
		
		return url.matches(WhiteList.VERSION_ENDPOINT) ;

	}

}
