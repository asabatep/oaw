/*******************************************************************************
* Copyright (C) 2012 INTECO, Instituto Nacional de Tecnologías de la Comunicación, 
* This program is licensed and may be used, modified and redistributed under the terms
* of the European Public License (EUPL), either version 1.2 or (at your option) any later 
* version as soon as they are approved by the European Commission.
* Unless required by applicable law or agreed to in writing, software distributed under the 
* License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF 
* ANY KIND, either express or implied. See the License for the specific language governing 
* permissions and more details.
* You should have received a copy of the EUPL1.2 license along with this program; if not, 
* you may find it at http://eur-lex.europa.eu/legal-content/EN/TXT/?uri=CELEX:32017D0863
* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
* Modificaciones: MINHAFP (Ministerio de Hacienda y Función Pública) 
* Email: observ.accesibilidad@correo.gob.es
******************************************************************************/
package es.inteco.rastreador2.servlets;

import es.inteco.common.Constants;
import es.inteco.common.logging.Logger;
import es.inteco.common.properties.PropertiesManager;

import javax.net.ssl.*;
import javax.servlet.*;
import java.io.IOException;


/**
 * The Class SetSystemPropertiesServlet.
 */
public class SetSystemPropertiesServlet extends GenericServlet {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
	 * Inits the.
	 *
	 * @param config the config
	 * @throws ServletException the servlet exception
	 */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        PropertiesManager pmgr = new PropertiesManager();
        setTrustStore(pmgr);
        setProxy(pmgr);

        setTrustingAllCerts();

        setDefaultHostnameVerifier();
    }

    /**
	 * Sets the trust store.
	 *
	 * @param pmgr the new trust store
	 */
    private void setTrustStore(PropertiesManager pmgr) {
        final String trustStorePath = pmgr.getValue("certificados.properties", "truststore.path");
        final String trustStorePass = pmgr.getValue("certificados.properties", "truststore.pass");
        if ( trustStorePath!=null && !trustStorePath.isEmpty()) {
            System.setProperty("javax.net.ssl.trustStore", trustStorePath);
            Logger.putLog("Configurando el truststore en " + trustStorePath, SetSystemPropertiesServlet.class, Logger.LOG_LEVEL_INFO);
        }
        if ( trustStorePass!=null && !trustStorePass.isEmpty()) {
            System.setProperty("javax.net.ssl.trustStorePassword", trustStorePass);
        }
    }

    /**
	 * Sets the proxy.
	 *
	 * @param pmgr the new proxy
	 */
    private void setProxy(PropertiesManager pmgr) {
        if (pmgr.getValue(Constants.INTAV_PROPERTIES, "http.proxy.active").equalsIgnoreCase(Boolean.TRUE.toString())) {
            String host = pmgr.getValue(Constants.INTAV_PROPERTIES, "http.proxy.host");
            String port = pmgr.getValue(Constants.INTAV_PROPERTIES, "http.proxy.port");
            Logger.putLog("Configurando la información del proxy en " + host + ":" + port, SetSystemPropertiesServlet.class, Logger.LOG_LEVEL_INFO);
            System.setProperty("http.proxyHost", host);
            System.setProperty("http.proxyPort", port);
            System.setProperty("https.proxyHost", host);
            System.setProperty("https.proxyPort", port);
        }
    }

    /**
	 * Sets the trusting all certs.
	 */
    private void setTrustingAllCerts() {
        Logger.putLog("Configurando la aplicación para que no valide los certificados en SSL.", SetSystemPropertiesServlet.class, Logger.LOG_LEVEL_INFO);
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLContext.setDefault(sc);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            Logger.putLog("Excepción: ", SetSystemPropertiesServlet.class, Logger.LOG_LEVEL_ERROR, e);
        }

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLContext.setDefault(sc);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            Logger.putLog("Excepción: ", SetSystemPropertiesServlet.class, Logger.LOG_LEVEL_ERROR, e);
        }
    }

    /**
	 * Sets the default hostname verifier.
	 */
    private void setDefaultHostnameVerifier() {
        HostnameVerifier hv = new HostnameVerifier() {
            // No validamos el nombre de HOST
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }

    /**
	 * Service.
	 *
	 * @param request  the request
	 * @param response the response
	 * @throws ServletException the servlet exception
	 * @throws IOException      Signals that an I/O exception has occurred.
	 */
    @Override
    public void service(ServletRequest request, ServletResponse response)
            throws ServletException, IOException {
    }

}
