package io.analytics.repository;
import io.analytics.domain.CoreReportingData;
import io.analytics.domain.CoreReportingTypedData;

import java.io.IOException;

public interface ICoreReportingRepository {
	//TODO: Eventually we will want this to be 10x more flexible, so this will all probably be rewritten
	public CoreReportingData getMetricByDay(String metric, String startDate, String endDate, int maxResult); 
	
	public CoreReportingData getMetricByDayOfWeek(String metric, String startDate, String endDate, int maxResults);

	public CoreReportingTypedData getTopTrafficSources(String metric, String startDate, String endDate, int maxResults) throws IOException;

	public CoreReportingTypedData getPagePerformance(String startDate, String endDate, int maxResults) throws IOException;

	}

