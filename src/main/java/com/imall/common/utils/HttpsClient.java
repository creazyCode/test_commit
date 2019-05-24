package com.imall.common.utils;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to https protocol.
 * 
 * @author jianxunji
 */
public class HttpsClient {
	/** logger */
	public static final Logger LOGGER = LoggerFactory.getLogger(HttpsClient.class);

	/** client */
	private static HttpClient client;

	/** trust manager */
	//TODO - SWC CI FindBugs
	public final static X509TrustManager TM = new X509TrustManager() {
		/**
		 * Check client trusted.
		 */
		@Override
		public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
			//TODO - SWC CI PMD
		}

		/**
		 * Check client trusted.
		 */
		@Override
		public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
			//TODO - SWC CI PMD
		}

		/**
		 * Get accepted issuers.
		 */
		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	};

	/**
	 * Get instance.
	 *
	 * @param maxTotal
	 * @param defaultMaxPerRoute
	 * @return http client
	 */
	@SuppressWarnings("deprecation")
	public static HttpClient getInstance(int maxTotal, int defaultMaxPerRoute){
		if(client != null){
			return client;
		}
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, new TrustManager[] { TM }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx);
			ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			SchemeRegistry sr = new SchemeRegistry();
			sr.register(new Scheme("https", ssf, 443));
			sr.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
			PoolingClientConnectionManager cm = new PoolingClientConnectionManager(sr);
			cm.setMaxTotal(maxTotal);
			cm.setDefaultMaxPerRoute(defaultMaxPerRoute);
			client = new DefaultHttpClient(cm);
		}catch(KeyManagementException e){
			LOGGER.error("get https client error, ingore it and return DefaultHttpClient", e);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("get https client error, ingore it and return DefaultHttpClient", e);
		}
		return client;
	}
}
