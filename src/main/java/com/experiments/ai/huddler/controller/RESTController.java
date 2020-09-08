package com.experiments.ai.huddler.controller;

import com.experiments.ai.huddler.model.EItem;
import com.experiments.ai.huddler.model.Request;
import com.experiments.ai.huddler.service.AppService;
import com.experiments.ai.huddler.utils.ItemSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

@RestController
public class RESTController {

    private final static Logger logger = LoggerFactory.getLogger(RESTController.class);

    @Autowired
    AppService appService;

    @GetMapping(value = "/get-matching-items",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<ResponseEntity<?>> getKSimilarItems(@RequestBody Request request) {
        logger.info("begin getKSimilarItems {} ", request);

        DeferredResult<ResponseEntity<?>> serviceResponse = new DeferredResult<>();

        serviceResponse.onTimeout(new Runnable() {
            @Override
            public void run() {
                serviceResponse.setErrorResult(
                        ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Request timed out."));
            }
        });

        ForkJoinPool.commonPool().submit(() -> {
                    //call service
                    List<EItem> matchingItems = appService.findMatchingItems(request);

                    ObjectMapper mapper = new ObjectMapper();
                    SimpleModule module = new SimpleModule();
                    module.addSerializer(EItem.class, new ItemSerializer());
                    mapper.registerModule(module);
                    String json = "";
                    try {
                        json = mapper.writeValueAsString(matchingItems);
                    } catch (Exception ex) {}

            ResponseEntity responseEntity = new ResponseEntity(json, HttpStatus.OK);
            serviceResponse.setResult(responseEntity);
        });

        logger.info("end getKSimilarItems");
        return serviceResponse;
    }


}
