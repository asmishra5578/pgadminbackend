package com.asktech.admin.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;

import com.asktech.admin.dto.login.JwtAuthenticationToken;
import com.asktech.admin.dto.login.JwtUserDetails;
import com.asktech.admin.dto.login.LoginRequestDto;
import com.asktech.admin.enums.AskTechGateway;
import com.asktech.admin.exception.JwtMissingException;
import com.asktech.admin.model.MerchantDetails;
import com.asktech.admin.model.UserAdminDetails;
import com.asktech.admin.repository.MerchantDetailsRepository;
import com.asktech.admin.repository.UserAdminDetailsRepository;


@Component
public class JwtAuthentication extends AbstractUserDetailsAuthenticationProvider {

	@Autowired
	private JwtValidator validator;

	@Autowired
	RestAuthenticationFailureHandler handler;

	@Autowired
	MerchantDetailsRepository userDao;
	@Autowired
	UserAdminDetailsRepository userAdminDao;

	String vrifyUserName = null;

	@Override
	protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
		String token = jwtAuthenticationToken.getToken();
		LoginRequestDto jwtUser = validator.validate(token);
		if (jwtUser == null) {
			//Log4jLogger.saveLog("JWT Token is Incorrect==> ");
			throw new JwtMissingException("JWT Token is Incorrect " + AskTechGateway.JWT_MISSING, null);
		}
		String uname = jwtUser.getUserNameOrEmail();
		System.out.println("Retrive UserName :: "+uname);
		MerchantDetails user;
		user = userDao.findByMerchantEmail(uname);
		
		if(user == null ) {
			UserAdminDetails userAdminDetails = userAdminDao.findByEmailId(uname);
			System.out.println("Retrive UserName :: "+userAdminDetails.getEmailId());
			
			if (userAdminDetails != null && userAdminDetails.getUserSession().getSessionStatus() == 0) {
				
				throw new SessionAuthenticationException(
						"Session Dead Please Login Again !!" + AskTechGateway.SESSION_DEAD);
			}
			if (userAdminDetails != null) {
				vrifyUserName = userAdminDetails.getEmailId();
			}
		}
		
		if (user != null && user.getUserSession().getSessionStatus() == 0) {
			//Log4jLogger.saveLog("Session Dead Please Login Again !!==> ");
			throw new SessionAuthenticationException(
					"Session Dead Please Login Again !!" + AskTechGateway.SESSION_DEAD);
		}
		if (user != null) {
			vrifyUserName = user.getMerchantEmail();
		}
		
		
		List<GrantedAuthority> grantedAuthority = AuthorityUtils
				.commaSeparatedStringToAuthorityList(jwtUser.getUserAgent());
		return new JwtUserDetails(jwtUser.getUserNameOrEmail(), token, jwtUser.getPassword(), jwtUser.getIpAddress(),
				grantedAuthority);
	}

	@Override
	public boolean supports(Class<?> authentication) {

		return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
	}

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		// TODO Auto-generated method stub

	}

	public boolean verifyjwt(String userName) {
		logger.info("inside verifyjwt () :: "+userName+" verifyusername "+vrifyUserName);
		if (!(vrifyUserName).equals(userName)) {
			return false;
		}
		return true;

	}
	
	

}
