package com.asktech.admin.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.asktech.admin.dto.merchant.MerchantValidationFilterDto;
import com.asktech.admin.exception.ValidationExceptions;
import com.asktech.admin.service.payout.VerifyUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet Filter implementation class PayoutMerchantApiFilter
 */
@Component
public class PayoutMerchantApiFilter implements Filter {
	@Autowired
	VerifyUser verifyUser;

	/**
	 * Default constructor.
	 */
	public PayoutMerchantApiFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		// place your code here
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		//String ipaddress = GeneralUtils.getClientIp(req);
		String ipaddress="117.215.148.117";
		String merchantid = req.getHeader("merchantid");
		String secret = req.getHeader("sec");
		System.out.println(ipaddress + " " + merchantid);
		try {
			if (verifyUser.checkUser(ipaddress, merchantid, secret)) {
				chain.doFilter(request, response);
			} else {
				res.resetBuffer();
				MerchantValidationFilterDto merchantValidationFilterDto = new MerchantValidationFilterDto();
				merchantValidationFilterDto.setStatus("404");
				merchantValidationFilterDto.setMsg("Invalid Authorization");
				res.setStatus(200);
				res.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
				res.getOutputStream().print(new ObjectMapper().writeValueAsString(merchantValidationFilterDto));
				res.flushBuffer();
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			res.resetBuffer();
			MerchantValidationFilterDto merchantValidationFilterDto = new MerchantValidationFilterDto();

			merchantValidationFilterDto.setStatus("404");
			merchantValidationFilterDto.setMsg("Invalid Data");
			res.setStatus(200);
			res.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			res.getOutputStream().print(new ObjectMapper().writeValueAsString(merchantValidationFilterDto));
			res.flushBuffer();
			e.printStackTrace();
		} catch (ValidationExceptions e) {
			// TODO Auto-generated catch block
			res.resetBuffer();
			MerchantValidationFilterDto merchantValidationFilterDto = new MerchantValidationFilterDto();

			merchantValidationFilterDto.setStatus("404");
			merchantValidationFilterDto.setMsg(e.getMessage());
			res.setStatus(200);
			res.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			res.getOutputStream().print(new ObjectMapper().writeValueAsString(merchantValidationFilterDto));
			res.flushBuffer();
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			res.resetBuffer();
			MerchantValidationFilterDto merchantValidationFilterDto = new MerchantValidationFilterDto();

			merchantValidationFilterDto.setStatus("404");
			merchantValidationFilterDto.setMsg("Invalid Data");
			res.setStatus(200);
			res.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			res.getOutputStream().print(new ObjectMapper().writeValueAsString(merchantValidationFilterDto));
			res.flushBuffer();
			e.printStackTrace();
			e.printStackTrace();
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			res.resetBuffer();
			MerchantValidationFilterDto merchantValidationFilterDto = new MerchantValidationFilterDto();

			merchantValidationFilterDto.setStatus("404");
			merchantValidationFilterDto.setMsg("Invalid Data");
			res.setStatus(200);
			res.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			res.getOutputStream().print(new ObjectMapper().writeValueAsString(merchantValidationFilterDto));
			res.flushBuffer();
			e.printStackTrace();
			e.printStackTrace();
		}

	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	@Override
	public void init(FilterConfig fConfig) throws ServletException {

		// TODO Auto-generated method stub
	}

}
