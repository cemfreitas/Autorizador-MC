package cemfreitas.autorizadorMC.manager;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cemfreitas.autorizadorMC.AutorizadorDB;

/* Abstract class TransactionBaseDB.
 * Provides basic implementations for database services. * 
 */
public abstract class TransactionBaseDB implements Transaction {
	private Connection dbConnection;
	private String storedProcedureName;
	private List<String> parametersList;
	private ResultSet resultSet;
	private CallableStatement cstmt;
	private List<String> databaseReturn = new ArrayList<String>();

	// Contructor receives SP name to be executed.
	public TransactionBaseDB(String storedProcedureName) {
		this.storedProcedureName = storedProcedureName;
	}

	public abstract long doTransaction() throws AutorizadorException;// To be
																		// implemented

	// Execute SP
	@Override
	public void send() throws AutorizadorException {
		StringBuffer sqlQuery = new StringBuffer();

		try {
			dbConnection = AutorizadorDB.getConnection();

			sqlQuery.append("exec ");
			sqlQuery.append(storedProcedureName);

			if (parametersList.size() == 6) {
				sqlQuery.append(" ?, ?, ?, ?, ?, ?");
			} else {
				sqlQuery.append(// ???!!! - Yea, I know, It's weird. I did this
								// for performance purposes, avoiding looping
								// each transaction to build this string.
						" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
			}

			cstmt = dbConnection.prepareCall(sqlQuery.toString());

			for (int i = 0; i < parametersList.size(); i++) {
				cstmt.setString(i + 1, parametersList.get(i));
			}

			resultSet = cstmt.executeQuery();

		} catch (SQLException e) {
			throw new AutorizadorException(
					"Erro na execucao da procedure " + storedProcedureName + ": " + e.getMessage());
		}
	}

	// Receive response of SP execution.
	@Override
	public void receive() throws AutorizadorException {
		try {
			if (!resultSet.next()) {
				throw new AutorizadorException("Procedure " + storedProcedureName + " nao retornou nenhum resultado");
			}

			ResultSetMetaData rsmd = resultSet.getMetaData();
			int columnsNumber = rsmd.getColumnCount();

			for (int i = 0; i < columnsNumber; i++) {

				databaseReturn.add(resultSet.getString(i + 1));
			}
		} catch (SQLException e) {
			throw new AutorizadorException(
					"Erro na execucao da procedure " + storedProcedureName + ": " + e.getMessage());
		} finally {
			close();
		}
	}

	private void close() throws AutorizadorException {
		try {
			if (resultSet != null)
				resultSet.close();
			if (cstmt != null)
				cstmt.close();
			if (dbConnection != null)
				dbConnection.close();
		} catch (SQLException e) {
			throw new AutorizadorException("Erro ao fechar database");
		}
	}

	public List<String> getParametersList() {
		return parametersList;
	}

	public void setParametersList(List<String> parametersList) {
		this.parametersList = parametersList;
	}

	public List<String> getDatabaseReturn() {
		return databaseReturn;
	}

	public void setDatabaseReturn(List<String> databaseReturn) {
		this.databaseReturn = databaseReturn;
	}

	//Not used in database transactions.
	@Override
	public void pack() throws AutorizadorException {
		return;

	}

	//Not used in database transactions.
	@Override
	public void unpack() throws AutorizadorException {
		return;
	}

	//For trace purposes 
	@Override
	public String getTransactionToTrace(Object transaction) {
		StringBuffer result = new StringBuffer();
		@SuppressWarnings("unchecked")
		List<String> spParams = (List<String>) transaction;

		result.append("exec " + storedProcedureName + " ");
		for (String param : spParams) {
			result.append("'");
			result.append(param);
			result.append("',");
		}

		return result.substring(0, result.length() - 1);
	}
}
