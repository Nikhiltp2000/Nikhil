package com.atdxt;

import com.atdxt.ArithmeticServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class ArithmeticController {

    private static final Logger logger = LoggerFactory.getLogger(ArithmeticController.class);

    private final ArithmeticServiceImplementation arithmeticService;
    @Autowired
    public ArithmeticController(ArithmeticServiceImplementation arithmeticService) {
        this.arithmeticService = arithmeticService;
    }

    @GetMapping("/add")
    public int add(@RequestParam int a, @RequestParam int b) {
        logger.info("Performing addition operation");
    return arithmeticService.add(a, b);
    }

    @GetMapping("/subtract")
    public int subtract(@RequestParam int a, @RequestParam int b) {
        logger.info("Performing subtraction operation");
    return arithmeticService.subtract(a, b);
    }

    @GetMapping("/multiply")
    public int multiply(@RequestParam int a, @RequestParam int b) {
        logger.info("Performing multiplication operation");
    return arithmeticService.multiply(a, b);
    }

    @GetMapping("/divide")
    public double divide(@RequestParam int a, @RequestParam int b) {
        logger.info("Performing division operation");
    return arithmeticService.divide(a, b);
    }
}