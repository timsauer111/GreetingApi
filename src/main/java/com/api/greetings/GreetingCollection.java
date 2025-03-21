package com.api.greetings;

import com.api.greetings.entity.Greeting;

import java.util.ArrayList;
import java.util.List;

public class GreetingCollection {
    private static List<Greeting> greetings = new ArrayList<Greeting>();

    public static Greeting addGreeting(Greeting greeting) {
        greetings.add(greeting);
        return greeting;
    }

    public static Greeting setGreeting(long id, Greeting greeting) {
        for (int i = 0; i < greetings.size(); i++) {
            if (greetings.get(i).getId() == id){
                greetings.set(i, greeting);
                return greetings.get(i);
            }
        }
        return null;
    }

    public static void clearSession() {
        greetings.clear();
    }

    public static Greeting deleteGreeting(long id) {
        for (Greeting greeting : greetings) {
            if(greeting.getId() == id) {
                greetings.remove(greeting);
                return greeting;
            }
        }
        return null;
    }
}
