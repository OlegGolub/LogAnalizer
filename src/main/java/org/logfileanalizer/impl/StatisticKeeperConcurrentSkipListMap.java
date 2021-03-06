package org.logfileanalizer.impl;

import org.logfileanalizer.StatisticLogLevel;
import org.logfileanalizer.StatisticInterval;
import org.logfileanalizer.StatisticKeeper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;


public class StatisticKeeperConcurrentSkipListMap implements StatisticKeeper {

    private ConcurrentNavigableMap<LocalDateTime, Integer> map = new ConcurrentSkipListMap();
    private StatisticInterval  statisticInterval;
    private StatisticLogLevel statisticLogLevel;

    public StatisticKeeperConcurrentSkipListMap(StatisticInterval  statisticInterval, StatisticLogLevel statisticLogLevel){
        this.statisticInterval = statisticInterval;
        this.statisticLogLevel = statisticLogLevel;
    }
    
    @Override
    /**
     * Count this fact according to statisticInterval
     * */
    public void registerTheFact(LocalDateTime moment) {
        LocalDateTime localDateTime = moment.withNano(0);
        if(statisticInterval==StatisticInterval.HOUR) {
            localDateTime = localDateTime.withMinute(0);
        }
        map.merge(localDateTime, 1, Integer::sum);
    }

    @Override
    public List<Statistic> getStatistics() {
        return map.keySet().stream().
                            map(key->new Statistic(key, statisticInterval, map.get(key))).
                            collect(Collectors.toList());
    }

    @Override
    public StatisticLogLevel getStatisticLogLevel() {
        return statisticLogLevel;
    }


    @Override
    public int getErrorStatisticForDateAndInterval(LocalDateTime theDate, StatisticInterval statisticInterval) {
        return map.get(theDate);
    }
}
