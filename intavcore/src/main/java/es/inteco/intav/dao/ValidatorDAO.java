package es.inteco.intav.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import es.inteco.common.logging.Logger;
import es.inteco.intav.form.ValidatorForm;

/**
 * The Class ValidatorDAO.
 */
public class ValidatorDAO {
	
	
	/**
	 * Gets the Validator.
	 *
	 * @param c the c
	 * @return the Validator
	 * @throws Exception the exception
	 */
	public static ValidatorForm getValidator(Connection c) throws Exception{
		
		ValidatorForm validator = new ValidatorForm();
		
		String query = "SELECT p.status, p.url FROM observatorio_validator p WHERE 1=1";

		try (PreparedStatement ps = c.prepareStatement(query)) {
			
			try (ResultSet rs = ps.executeQuery()) {

				if (rs.next()) {
					validator.setStatus(rs.getInt("p.status"));
					validator.setUrl(rs.getString("p.url"));
				}
			}
			
		} catch (SQLException e) {
			Logger.putLog("SQL Exception: ", ValidatorDAO.class, Logger.LOG_LEVEL_ERROR, e);
			throw e;
		}
		
		return validator;
		
	
		
	}
	
	/**
	 * Update.
	 *
	 * @param c     the c
	 * @param Validator the Validator
	 * @throws SQLException the SQL exception
	 */
	public static void update(Connection c, ValidatorForm validator) throws SQLException {
		final String query = "UPDATE observatorio_validator SET status = ?, url = ? WHERE 1=1";

		try (PreparedStatement ps = c.prepareStatement(query)) {
			ps.setInt(1, validator.getStatus());
			ps.setString(2, validator.getUrl());
			ps.executeUpdate();
		} catch (SQLException e) {
			Logger.putLog("SQL Exception: ", ValidatorDAO.class, Logger.LOG_LEVEL_ERROR, e);
			throw e;
		}

	}

}
