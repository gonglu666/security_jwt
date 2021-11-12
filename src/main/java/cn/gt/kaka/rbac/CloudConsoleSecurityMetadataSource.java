package cn.gt.kaka.rbac;


import org.springframework.security.access.ConfigAttribute;

import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component
public class CloudConsoleSecurityMetadataSource implements FilterInvocationSecurityMetadataSource{


	@Override
	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {

		Set<ConfigAttribute> allAttributes = new HashSet<>();
		allAttributes.add(new SecurityConfig("user"));
		return allAttributes;
	}

	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		System.out.println("FilterInvocationSecurityMetadataSourceImpl:getAllConfigAttributes()");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return true;
	}

}
