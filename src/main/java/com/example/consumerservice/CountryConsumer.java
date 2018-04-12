package com.example.consumerservice;

import com.google.common.collect.ImmutableList;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
public class CountryConsumer {
    
    private final ConsumerServiceApplication.CountryClient countryClient;
    
    public CountryConsumer(ConsumerServiceApplication.CountryClient countryClient){
     this.countryClient = countryClient;   
    }
    
    @HystrixCommand(fallbackMethod = "fallback")
    @GetMapping("/countries")
    public Collection<Country> allCountries(){
        return countryClient.getAllCountries()
                .getContent()
                .stream()
                .collect(Collectors.toList());
    }

    @GetMapping("/ofacCountries")
    public Collection<Country> getOfac(){
        return countryClient.getAllCountries()
                .getContent()
                .stream()
                .filter(this::isOfac)
                .collect(Collectors.toList());
    }
    
    private boolean isOfac(Country country) {
        List<String> ofacNames = ImmutableList.of("Afghanistan", "Iran", "North Korea");
        return ofacNames.contains(country.getName());
    }
    
    public Collection<Country> fallback(){
        return new ArrayList<>();
    }
}
