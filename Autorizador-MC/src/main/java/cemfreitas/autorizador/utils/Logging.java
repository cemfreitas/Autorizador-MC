package cemfreitas.autorizador.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;

/* Logging class.
 * Provides logging service to the application.
 * Uses LogBack API.
 */
public class Logging {
	private static final Logger logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
	private static final Logger trace = LoggerFactory.getLogger("TRACE-LOG");
	private static ch.qos.logback.classic.Logger traceLogger = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory
			.getLogger("TRACE-LOG");
	private static ch.qos.logback.classic.Logger infoLogger = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory
			.getLogger(Logger.ROOT_LOGGER_NAME);

	public static void init(boolean traceOn) {
		if (!traceOn) {
			turnTraceOff();
		}
		if (!AutorizadorParams.getValue("HabilitaLogAplicacao").equalsIgnoreCase("S")) {
			turnApplicationLogOff();
		}
	}

	public static Logger getLogger() {
		return logger;

	}

	public static Logger getTrace() {
		return trace;
	}

	private static void turnTraceOff() {
		traceLogger.setLevel(Level.OFF);
	}

	private static void turnApplicationLogOff() {
		infoLogger.setLevel(Level.OFF);
	}

	//Shutdown hook. Stop services to flush log before application shutdown. 
	public static void shutdown() {

		if (infoLogger.isInfoEnabled()) {			
			infoLogger.getAppender("FILE-AUDIT").stop();
		}

		if (traceLogger.isTraceEnabled()) {			
			traceLogger.getAppender("FILE-TRACE").stop();
		}
	}
}
