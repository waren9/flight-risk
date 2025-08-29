package com.example.flightrisk;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/predict")
public class PredictionController {

    @PostMapping
    public Map<String, Object> predictRisk(@RequestBody FlightRoute route) {
        Random random = new Random();

        // Fake probabilities for now
        double turbulenceProbability = random.nextDouble();  // 0.0 - 1.0
        double birdStrikeProbability = random.nextDouble();

        // Suggest alternate route (just reverse as demo)
        String alternateRoute = route.getTo() + " → " + route.getFrom();

        Map<String, Object> result = new HashMap<>();
        result.put("from", route.getFrom());
        result.put("to", route.getTo());
        result.put("turbulenceProbability", turbulenceProbability);
        result.put("birdStrikeProbability", birdStrikeProbability);
        result.put("suggestedAlternate", alternateRoute);

        return result;
    }
}
