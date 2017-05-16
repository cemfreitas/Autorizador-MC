package cemfreitas.autorizador.tests;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TesteDB {

	private Connection dbConn;
	private String IpSql = "localhost";
	private String UsuarioSql = "sa";
	private String SenhaSql = "xxxxxx";
	private String DataBaseName = "Autorizador";

	private void init() {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
			String URL = "jdbc:sqlserver://" + IpSql + ":1433;DatabaseName=" + DataBaseName;

			dbConn = DriverManager.getConnection(URL, UsuarioSql, SenhaSql);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			dbConn = null;
			return;
		} catch (SQLException e) {
			e.printStackTrace();
			dbConn = null;
			return;
		} catch (InstantiationException e) {
			e.printStackTrace();
			dbConn = null;
			return;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			dbConn = null;
			return;
		} catch (Exception e) {
			e.printStackTrace();
			dbConn = null;
			return;
		}
	}

	private Connection getConnection() {
		if (dbConn != null) {
			return dbConn;
		}
		return null;
	}

	private void testeDB() {
		init();
		Connection conn = getConnection();
		ResultSet rs = null;
		CallableStatement cstmt = null;

		String sql = "select * from teste";

		try {
			cstmt = conn.prepareCall(sql);
			rs = cstmt.executeQuery();

			while (rs.next()) {
				System.out.println(rs.getString(1) + " - " + rs.getString(2));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (cstmt != null)
					cstmt.close();
				if (dbConn != null)
					dbConn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				rs = null;
				cstmt = null;
				dbConn = null;
			}
		}
	}

	public static void main(String[] args) {
		TesteDB teste = new TesteDB();
		teste.init();
		teste.testeDB();
	}

}
