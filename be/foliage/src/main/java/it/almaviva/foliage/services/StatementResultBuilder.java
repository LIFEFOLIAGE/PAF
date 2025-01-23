package it.almaviva.foliage.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;


public interface StatementResultBuilder {
	ResultSet getExecution(Connection conn) throws SQLException;
}
