package server.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GameDAO {
	private static GameDAO gameDAO = null;

	private Connection conn = null;
	
	// singleton
	private GameDAO() {
		String jdbc_driver = "com.mysql.jdbc.Driver";
		String jdbc_url = "jdbc:mysql://127.0.0.1/koth";

		try {
			// JDBC 드라이버 로드
			Class.forName(jdbc_driver);
			conn = DriverManager.getConnection(jdbc_url, "root", "1q2w3e");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static GameDAO getInstance() {
		if (gameDAO == null) {
			gameDAO = new GameDAO();
		}
		
		return gameDAO;
	}
	
	public ResultSet returnResultAfterQuery(String query){
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void updateQuery(String query){
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(query);
			pstmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
