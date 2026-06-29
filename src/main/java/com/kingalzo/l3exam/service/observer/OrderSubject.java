package com.kingalzo.l3exam.service.observer;

import java.util.ArrayList;
import java.util.List;

public class OrderSubject {
    private final List<NotificationObserver> observers = new ArrayList<>();

    public void registerObserver(NotificationObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers(String message) {
        observers.forEach(observer -> observer.update(message));
    }
}
