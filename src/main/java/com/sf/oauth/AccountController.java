package com.sf.oauth;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;


@Controller
@RequestMapping("/")
public class AccountController {
  
    private WebClient webClient;
 
    public AccountController(WebClient webClient) {
        this.webClient = webClient;
    }
    
    @GetMapping("/")
    public String index(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient,
                        @AuthenticationPrincipal OAuth2User oauth2User,
                        Model model) {
        model.addAttribute("username", oauth2User.getAttributes().get("name"));
        return "index";                            
    }
 
    @GetMapping("/accounts")
    public String accounts(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient,
                        @AuthenticationPrincipal OAuth2User oauth2User,
                        Model model) {
        String response = fetchAccounts(authorizedClient, (String) oauth2User.getAttributes().get("profile"));  

        model.addAttribute("response", prettyJson(response));
        model.addAttribute("username", oauth2User.getAttributes().get("name"));
 
        return "index";
    }

    private String prettyJson(String uglyString){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(uglyString);
        return gson.toJson(je);              
    }
    
    private String fetchAccounts(OAuth2AuthorizedClient authorizedClient, String userProfileURL) {
        String sfHost = userProfileURL.substring(0, userProfileURL.lastIndexOf("/"));
        return this.webClient
                .get()
                .uri(sfHost, uriBuilder ->
                    uriBuilder
                    .path("/services/data/v51.0/query/")
                    .queryParam("q","SELECT Id, Name FROM Account LIMIT 5")
                    .build()
                )
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
 

}