package jp.co.starsoft.module.sqltester;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class LocalDBAccessor {
	static{
		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	private static String JDBC_URL = "jdbc:oracle:thin:@127.0.0.1:1521:ICARD";
	private static String JDBC_USER = "ICARD";
	private static String JDBC_PASS = "ICARD";
	
	private Connection conn;
	
	LocalDBAccessor() {

		try{
			conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
			conn.setAutoCommit(false);
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}
	
	void dummyRun(String sql) throws SQLException {
		
		char[] sqlContent = sql.toCharArray();
		
		int numberOfTheQuestionMark = 0;
		for(int i = 0; i < sqlContent.length; i++){
			if(sqlContent[i] == '?'){
				numberOfTheQuestionMark++;
			}
		}
		
		try (PreparedStatement stmt = conn.prepareStatement(sql);) {
			for(int i = 0; i < numberOfTheQuestionMark; i++) {
				stmt.setString(i + 1, "0");
			}
			stmt.execute();
		}finally{
			try{
				conn.rollback();
			}catch(SQLException ex){
				//ignore
				System.err.println("can not rollback");
				System.err.println(ex.getMessage());
			}
			
		}
	}

	void terminate() {

		try{
			conn.close();
		}catch(SQLException ex){
			System.err.println("the database connection can not be closed.");
			System.err.println(ex.getMessage());
		}
	}
	
}
