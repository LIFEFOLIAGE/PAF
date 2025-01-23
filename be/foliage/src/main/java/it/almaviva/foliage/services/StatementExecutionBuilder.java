package it.almaviva.foliage.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public interface StatementExecutionBuilder {
	CallableStatement getExecution(Connection conn) throws SQLException;
}
