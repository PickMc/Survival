package me.zxoir.smp.utilities;

import java.util.HashMap;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

public class RandomCollection<E> {
    private final NavigableMap<Double, E> map = new TreeMap<>();
    private final HashMap<E, Double> logged = new HashMap<>();
    private final Random random = ThreadLocalRandom.current();
    private double total = 0;

    public RandomCollection<E> add(double weight, E result) {
        if (weight <= 0) return this;
        total += weight;
        map.put(total, result);
        logged.put(result, weight);
        return this;
    }

    public RandomCollection<E> remove(E result) {
        if (!logged.containsKey(result)) return this;
        logged.remove(result);
        RandomCollection<E> randomCollection = new RandomCollection<>();
        logged.keySet().forEach(key -> randomCollection.add(logged.get(key), key));
        return randomCollection;
    }

    public HashMap<E, Double> getLogged() {
        return logged;
    }

    public int size() {
        return logged.size();
    }

    public boolean isEmpty() {
        return logged.isEmpty();
    }

    public E next() {
        try {
            double value = random.nextDouble() * total;
            return map.higherEntry(value).getValue();
        } catch (Exception e) {
            e.printStackTrace();
            return next();
        }
    }
}
