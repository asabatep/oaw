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
package es.inteco.rastreador2.management;

import static es.inteco.common.Constants.CRAWLER_PROPERTIES;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.sun.management.OperatingSystemMXBean;

import es.gob.oaw.MailService;
import es.inteco.common.logging.Logger;
import es.inteco.common.properties.PropertiesManager;
import es.inteco.rastreador2.utils.DAOUtils;

/**
 * The Class ManagementThread.
 */
public class ManagementThread extends Thread {
	/** The last system time. */
	private BigDecimal lastSystemTime = BigDecimal.ZERO;
	/** The last process cpu time. */
	private BigDecimal lastProcessCpuTime = BigDecimal.ZERO;
	/** The num warnings memory. */
	private int numWarningsMemory = 0;
	/** The num warnings cpu. */
	private int numWarningsCpu = 0;
	/** The stop. */
	private boolean stop = false;

	/**
	 * Instantiates a new management thread.
	 */
	public ManagementThread() {
		super("ManagementThread");
	}

	/**
	 * Para la ejecución de este Thread.
	 */
	public void stopThread() {
		this.stop = true;
		// Interrumpimos el thread para despertarlo y que se pare
		this.interrupt();
	}

	/**
	 * Run.
	 */
	@Override
	public void run() {
		PropertiesManager pmgr = new PropertiesManager();
		long interval = Long.parseLong(pmgr.getValue("management.properties", "management.millis.interval"));
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
		BigDecimal heapMemoryMax = new BigDecimal(memoryMXBean.getHeapMemoryUsage().getMax());
		Logger.putLog("La memoria máxima asignada es de " + heapMemoryMax.divide(new BigDecimal(1000000), 2, BigDecimal.ROUND_HALF_UP) + " MB", ManagementThread.class, Logger.LOG_LEVEL_INFO);
		while (!stop) {
			try {
				Thread.sleep(interval);
				// Uso de memoria
				checkMemoryUsage();
				// Uso de CPU
				checkCpuUsage();
			} catch (InterruptedException ie) {
				// Si se interrumpe este hilo y no es porque lo estamos parando logeamos
				if (!stop) {
					Logger.putLog("Interrumpido hilo ManagementThread", ManagementThread.class, Logger.LOG_LEVEL_INFO);
				}
			} catch (Exception e) {
				Logger.putLog("Error al monitorizar la aplicación", ManagementThread.class, Logger.LOG_LEVEL_ERROR, e);
			}
		}
		Logger.putLog("ManagementThread finalizado", ManagementThread.class, Logger.LOG_LEVEL_INFO);
	}

	/**
	 * Check cpu usage.
	 */
	private void checkCpuUsage() {
		BigDecimal cpuUsage = getCpuLoad();
		PropertiesManager pmgr = new PropertiesManager();
		BigDecimal memoryPercentageLimit = new BigDecimal(pmgr.getValue("management.properties", "cpu.percentage.limit"));
		if (cpuUsage.compareTo(memoryPercentageLimit) > 0) {
			numWarningsCpu++;
			if (numWarningsCpu >= Integer.parseInt(pmgr.getValue("management.properties", "management.cpu.num.intervals"))) {
				Logger.putLog("El porcentaje de CPU utilizado es del " + cpuUsage.toPlainString() + "% . Se va a proceder a avisar a los administradores.", ManagementThread.class,
						Logger.LOG_LEVEL_ERROR);
				try {
					sendCpuMail(cpuUsage);
				} catch (Exception e) {
					Logger.putLog("Error al intentar enviar el correo electrónico", ManagementThread.class, Logger.LOG_LEVEL_ERROR, e);
				}
				numWarningsCpu = 0;
			}
		} else {
			numWarningsCpu = 0;
		}
	}

	/**
	 * Gets the cpu load.
	 *
	 * @return the cpu load
	 */
	private BigDecimal getCpuLoad() {
		OperatingSystemMXBean osMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		BigDecimal systemTime = new BigDecimal(System.nanoTime());
		BigDecimal processCpuTime = new BigDecimal(osMXBean.getProcessCpuTime());
		BigDecimal cpuUsage = (processCpuTime.subtract(lastProcessCpuTime)).divide(systemTime.subtract(lastSystemTime), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2)
				.divide(new BigDecimal(osMXBean.getAvailableProcessors()), 4, RoundingMode.HALF_UP);
		lastSystemTime = systemTime;
		lastProcessCpuTime = processCpuTime;
		return cpuUsage;
	}

	/**
	 * Check memory usage.
	 */
	private void checkMemoryUsage() {
		PropertiesManager pmgr = new PropertiesManager();
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
		BigDecimal heapMemoryUsed = new BigDecimal(memoryMXBean.getHeapMemoryUsage().getUsed());
		BigDecimal heapMemoryMax = new BigDecimal(memoryMXBean.getHeapMemoryUsage().getMax());
		BigDecimal memoryPercentageLimit = new BigDecimal(pmgr.getValue("management.properties", "memory.percentage.limit"));
		BigDecimal memoryPercentageGC = new BigDecimal(pmgr.getValue("management.properties", "memory.percentage.garbage.collector"));
		BigDecimal usedPercentage = heapMemoryUsed.divide(heapMemoryMax, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2);
		if (usedPercentage.compareTo(memoryPercentageLimit) > 0) {
			numWarningsMemory++;
			if (numWarningsMemory >= Integer.parseInt(pmgr.getValue("management.properties", "management.memory.num.intervals"))) {
				Logger.putLog(
						"La memoria utilizada es del " + usedPercentage + "%(" + heapMemoryUsed.divide(new BigDecimal(1000000), 2, BigDecimal.ROUND_HALF_UP) + " MB de "
								+ heapMemoryMax.divide(new BigDecimal(1000000), 2, BigDecimal.ROUND_HALF_UP) + " MB). Se va a proceder a avisar a los administradores.",
						ManagementThread.class, Logger.LOG_LEVEL_ERROR);
				try {
					sendMemoryMail(usedPercentage, heapMemoryMax, heapMemoryUsed);
				} catch (Exception e) {
					Logger.putLog("Error al intentar enviar el correo electrónico", ManagementThread.class, Logger.LOG_LEVEL_ERROR, e);
				}
				numWarningsMemory = 0;
			}
		} else if (usedPercentage.compareTo(memoryPercentageGC) > 0) {
			Logger.putLog("El uso de memoria es alto, se va a proceder a llamar al recolector de basura de Java manualmente", ManagementThread.class, Logger.LOG_LEVEL_INFO);
			memoryMXBean.gc();
		} else {
			numWarningsMemory = 0;
		}
	}

	/**
	 * Send memory mail.
	 *
	 * @param usedPercentage the used percentage
	 * @param heapMemoryMax  the heap memory max
	 * @param heapMemoryUsed the heap memory used
	 * @throws Exception the exception
	 */
	private void sendMemoryMail(BigDecimal usedPercentage, BigDecimal heapMemoryMax, BigDecimal heapMemoryUsed) throws Exception {
		PropertiesManager pmgr = new PropertiesManager();
		List<String> adminMails = DAOUtils.getMailsByRol(Long.parseLong(pmgr.getValue(CRAWLER_PROPERTIES, "role.administrator.id")));
		String alertSubject = pmgr.getValue("management.properties", "memory.alert.subject");
		String alertText = pmgr.getValue("management.properties", "common.alert.text");
		alertText += pmgr.getValue("management.properties", "memory.alert.text").replace("{0}", usedPercentage.toPlainString())
				.replace("{1}", heapMemoryUsed.divide(new BigDecimal(1000000), 2, BigDecimal.ROUND_HALF_UP).toPlainString())
				.replace("{2}", heapMemoryMax.divide(new BigDecimal(1000000), 2, BigDecimal.ROUND_HALF_UP).toPlainString());
		alertText += pmgr.getValue("management.properties", "memory.alert.info");
		MailService mailService = new MailService();
		mailService.sendMail(adminMails, alertSubject, alertText);
	}

	/**
	 * Send cpu mail.
	 *
	 * @param cpuUsage the cpu usage
	 * @throws Exception the exception
	 */
	private void sendCpuMail(BigDecimal cpuUsage) throws Exception {
		PropertiesManager pmgr = new PropertiesManager();
		List<String> adminMails = DAOUtils.getMailsByRol(Long.parseLong(pmgr.getValue(CRAWLER_PROPERTIES, "role.administrator.id")));
		String alertSubject = pmgr.getValue("management.properties", "cpu.alert.subject");
		String alertText = pmgr.getValue("management.properties", "common.alert.text");
		alertText += pmgr.getValue("management.properties", "cpu.alert.text").replace("{0}", cpuUsage.toPlainString());
		MailService mailService = new MailService();
		mailService.sendMail(adminMails, alertSubject, alertText);
	}
}
