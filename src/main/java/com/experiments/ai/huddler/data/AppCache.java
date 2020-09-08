package com.experiments.ai.huddler.data;

import com.experiments.ai.huddler.model.EItem;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AppCache {

    private Map<Long, EItem> eItemMap = new HashMap<>();

    private Map<Long, List<EItem>> categorizedItems = new HashMap<>();

    public Map<Long, EItem> geteItemMap() {
        return eItemMap;
    }

    public Map<Long, List<EItem>> getCategorizedItems() {
        return categorizedItems;
    }

    public synchronized void addItemToCache(EItem eItem) {
        List<EItem> eItemsByCategory = categorizedItems.getOrDefault(eItem.getLeaf_categ_id(), new ArrayList<>());
        eItemsByCategory.add(eItem);
        categorizedItems.put(eItem.getLeaf_categ_id(), eItemsByCategory);

        eItemMap.put(eItem.getItemId(), eItem);
    }
}
