package com.n26.services;

import com.n26.cache.StatisticsCache;
import com.n26.models.Statistics;
import com.n26.services.impl.StatisticsServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class StatisticsServiceTest {

    @Mock
    private StatisticsCache statisticsCache;

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    @Test
    public void testStatisticsService() {
        final Statistics statistics = new Statistics();
        when(statisticsCache.getStatistics()).thenReturn(statistics);
        assertEquals(statisticsService.get(), statistics);
    }

}
