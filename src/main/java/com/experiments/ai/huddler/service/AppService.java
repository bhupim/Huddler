package com.experiments.ai.huddler.service;

import com.experiments.ai.huddler.model.EItem;
import com.experiments.ai.huddler.model.Request;

import java.util.List;

public interface AppService {

    List<EItem> findMatchingItems(Request request);
}
