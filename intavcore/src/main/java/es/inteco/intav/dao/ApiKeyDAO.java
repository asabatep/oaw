package es.inteco.intav.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import es.inteco.common.logging.Logger;
import es.inteco.intav.form.ApiKeyForm;

/**
 * The Class apiKeyDAO.
 */
public class ApiKeyDAO {
	
	
	/**
	 * Gets the apiKey.
	 *
	 * @param c the c
	 * @return the apiKey
	 * @throws Exception the exception
	 */
	public static ApiKeyForm getApiKeyByName(Connection c, String name) throws Exception{
		
		ApiKeyForm apiKey = new ApiKeyForm();
		
		try (PreparedStatement ps = c.prepareStatement(
				"SELECT activa, apikey, descripcion, nombre FROM apikey WHERE nombre = ?")) {
			ps.setString(1, name);

			try (ResultSet rs = ps.executeQuery()) {

				if (rs.next()) {
					apiKey.setActive(rs.getBoolean("activa"));
					apiKey.setApiKey(rs.getString("apikey"));
					apiKey.setName(rs.getString("nombre"));
				}
			}
			
		} catch (SQLException e) {
			Logger.putLog("SQL Exception: ", ApiKeyDAO.class, Logger.LOG_LEVEL_ERROR, e);
			throw e;
		}
		
		return apiKey;
		
	
		
	}
}
