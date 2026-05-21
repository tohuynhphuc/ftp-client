package com.phuc.ftpclient.state;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class StateMachine {

    private static State currentState = State.INIT;
    private static final StateMachine instance = new StateMachine();
    private final List<Consumer<State>> listeners = new ArrayList<>();

    public static StateMachine getInstance() {
        return instance;
    }

    public void switchState(State newState) {
        currentState = newState;

        for (Consumer<State> listener : listeners) {
            listener.accept(newState);
        }
    }

    public State getCurrentState() {
        return currentState;
    }

    public void addListener(Consumer<State> listener) {
        listeners.add(listener);
    }

}
