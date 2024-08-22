package com.jmt.ai.controllers;

import com.jmt.ai.services.AiPromptService;
import com.jmt.global.rests.JSONData;
import com.jmt.global.services.ConfigInfoService;
import com.jmt.restaurant.entities.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiPromptService service;
    private final ConfigInfoService configInfoService;

    @GetMapping
    public JSONData index(@RequestParam("message") String message) {
        Boolean useHuggConfig =  configInfoService.getUseHuggConfig();

        System.out.println("useHuggConfig :" + useHuggConfig);

        if (useHuggConfig) {

            String response = service.prompt(message + " 한국말로 알려주세요");

            System.out.println(response);
            JSONData jsonData = new JSONData(response);
            jsonData.setSuccess(response != null);
            System.out.println(jsonData.getData());
            return jsonData;
        }

        Restaurant data= service.onePickRestaurant(message);

        return new JSONData(
                "<a href='/restaurant/info/" + data.getRstrId() + "'>" +
                data.getRstrNm() + "</a> " +
                data.getRstrRdnmAdr() + " " +
                (data.getRstrIntrcnCont() != null ? data.getRstrIntrcnCont() : "") + " " +
                (data.getRstrTelNo() != null ? data.getRstrTelNo() : "")
        );
    }
}