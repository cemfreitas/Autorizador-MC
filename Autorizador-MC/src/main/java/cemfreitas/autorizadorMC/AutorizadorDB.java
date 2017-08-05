package cemfreitas.autorizadorMC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import cemfreitas.autorizadorMC.utils.AppFunctions;
import cemfreitas.autorizadorMC.utils.AutorizadorParams;

/* AutorizadorDB class.
 * Provides database services to application.
 */
public class AutorizadorDB {

	private final static String databaseIp = AutorizadorParams.getValue("IPServidorBanco");
	private final static String databaseUser = AutorizadorParams.getValue("UsuarioBanco");
	private final static String databasePsw = AutorizadorParams.getValue("SenhaBanco");
	private final static String databaseName = AutorizadorParams.getValue("NomeBanco");
	private final static String URL = "jdbc:sqlserver://" + databaseIp + ":1433;DatabaseName=" + databaseName;

	public static void init() {
		Connection testConnection=null;
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
			
			testConnection = getConnection();

		} catch (ClassNotFoundException e) {			
			AppFunctions.showErroMessage("Erro ao carregar Driver SQLServer", e);
			System.exit(1);
		} catch (InstantiationException e) {			
			AppFunctions.showErroMessage("Erro ao conectar no SQL - Nome do banco : " + databaseName, e);
			System.exit(1);
		} catch (IllegalAccessException e) {
			AppFunctions.showErroMessage("Erro ao conectar no SQL - Nome do banco : " + databaseName, e);			
			System.exit(1);
		} catch (Exception e) {
			AppFunctions.showErroMessage("Erro ao conectar no SQL - Nome do banco : " + databaseName, e);			
			System.exit(1);
		} finally {
			try {
				testConnection.close();
			} catch (SQLException e) {
				AppFunctions.showErroMessage("Erro ao fechar conexao com SQL - Nome do banco : " + databaseName, e);			
				System.exit(1);
			}
		}
	}
	
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, databaseUser, databasePsw);
	}
}
