package com.eplugger.extend;

import java.io.PrintStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.eplugger.utils.SpringContextUtil;

import cn.hutool.core.net.DefaultTrustManager;
import cn.hutool.http.ssl.TrustAnyHostnameVerifier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class Helper {
	@Value("${spring.profiles.active}")
	private String profile;
	@Value("${ep.ssl.disable}")
	private String sslDisable;
	private static PrintStream out = System.out;

	public static Helper getInstance() {
		return SpringContextUtil.getBean(Helper.class);
	}

	public static void debug(Class<?> cls, String msg) {
		/*
		 * if (log.isDebugEnabled()) { if (cls == null) cls = Helper.class;
		 * log.debug("****Helper Dev *****"); log.debug(String.format("[%s]%s\n",
		 * cls.getName(), msg)); log.debug("***********************"); }
		 */
		if (isDev()) {
			if (cls == null)
				cls = Helper.class;
			out.printf("****Helper Dev ***** \n");
			out.printf(String.format("[%s]%s \n", cls.getName(), msg));
			out.printf("*********************** \n");
		}

	}

	public static boolean isDev() {
		return StringUtils.equals("dev", getInstance().profile);
	}

	public static void handleException(Exception e) {
		if (e != null && isDev()) {
			e.printStackTrace();
		}
	}
	
	@FunctionalInterface
	public static interface VoidZeroFunc{
		public void call();
	}
	//@Async
	public static <T> void safeDo(VoidZeroFunc func) {
		try {
			func.call();
		}catch (Exception e) {
			handleException(e);
		}
	}
	
	public static void disabledSSL() {
		if(StringUtils.equals("disable", getInstance().sslDisable)) {
			try {
				HttpsURLConnection.setDefaultHostnameVerifier(new TrustAnyHostnameVerifier());
				//TrustManager tm=new Tm();
				SSLContext context=SSLContext.getInstance("TLS");
				//TrustManager managers[] = {tm};
				TrustManager managers[] = {new DefaultTrustManager()};
				context.init(null,managers, null);
				HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
			}catch (Exception e) {
				handleException(e);
			}
		}
	}
	
	private static class Tm implements TrustManager,X509TrustManager{

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		//忽略认证
		@Override
		public X509Certificate[] getAcceptedIssuers() {
			
			return null;
		}
		
		
		
	}
	
	public static void log(Class<?> cls,Object msg) {
		safeDo(()->{
		Class<?> clsInner=cls;
		if (cls == null)
			clsInner = Helper.class;
		out.printf("****Helper Info ***** \n");
		out.printf(String.format("[%s]%s \n", clsInner.getName(), msg.toString()));
		out.printf("*********************** \n");
		});
	}
}
