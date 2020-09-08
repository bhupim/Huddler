package com.experiments.ai.huddler.service;

import com.experiments.ai.huddler.data.AppCache;
import com.experiments.ai.huddler.model.EItem;
import com.experiments.ai.huddler.model.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppServiceImpl implements AppService {

    private final static Logger logger = LoggerFactory.getLogger(AppServiceImpl.class);

    @Autowired
    AppCache appCache;

    @Override
    public List<EItem> findMatchingItems(Request request) {
        logger.info("begin findMatchingItems {}", request);

        EItem item = appCache.geteItemMap().get(request.getItemId());

        if (item == null) {
            logger.debug("Requested item Id not found.");
            return new ArrayList<>();
        }

        List<EItem> results = buildItemPriorityList(item, request);
        logger.info("end findMatchingItems");
        return results;
    }

    private List<EItem> buildItemPriorityList(EItem item, Request request) {
        if (item.getHammingDistanceMap().isEmpty()) {
            ItemMatcher matcher = new ItemMatcher();

            List<EItem> categorizedItems = appCache.getCategorizedItems().get(item.getLeaf_categ_id());

            matcher.matchItems(item, categorizedItems);
        }
        Map<Long, Integer> hammingDistanceMap = item.getHammingDistanceMap();

        PriorityQueue<Long> itemPriorityQueue = new PriorityQueue<>(
                Comparator.comparing(hammingDistanceMap::get));

        for (long itemId : hammingDistanceMap.keySet()) {

            itemPriorityQueue.add(itemId);

            if (itemPriorityQueue.size() > request.getNumOfResults())
                itemPriorityQueue.poll();
        }

        List<EItem> result = new ArrayList<>();
        for(int i = 0; i < request.getNumOfResults(); i++) {
            result.add(i, appCache.geteItemMap().get(itemPriorityQueue.poll()));
        }
        return result;
    }

}
