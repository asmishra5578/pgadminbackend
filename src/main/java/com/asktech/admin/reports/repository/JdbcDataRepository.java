package com.asktech.admin.reports.repository;

import java.awt.print.Book;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.asktech.admin.reports.customInterface.DataRepository;
import com.asktech.admin.reports.dto.ReportData;

@Repository
public class JdbcDataRepository implements DataRepository{

	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	@Override
    public List<ReportData> executeQuery(String query) {
        return jdbcTemplate.query(
        		query,               
                (rs, rowNum) ->
                       new ReportData(
                    		rs.getString("data")
                        )
        );
    }
}
