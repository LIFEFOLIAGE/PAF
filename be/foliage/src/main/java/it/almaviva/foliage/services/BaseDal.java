package it.almaviva.foliage.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class BaseDal extends AbstractDal {

	public BaseDal(
		JdbcTemplate jdbcTemplate,
		TransactionTemplate transactionTemplate,
		PlatformTransactionManager platformTransactionManager,
		String name
	) throws Exception {
		super(jdbcTemplate, transactionTemplate, platformTransactionManager, name);
	}

	@Autowired
	public BaseDal(
		JdbcTemplate jdbcTemplate,
		TransactionTemplate transactionTemplate,
		PlatformTransactionManager platformTransactionManager
	) throws Exception {
		this(jdbcTemplate, transactionTemplate, platformTransactionManager, "BaseDal");
	}

}
