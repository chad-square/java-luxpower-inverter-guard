package org.example;

import org.example.service.GuardService;

import java.util.Timer;
import java.util.TimerTask;

public class InverterGuard {

    private final GuardService guardService;

    public InverterGuard() {
        this.guardService = new GuardService();
    }

    public void run() {

            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("the other thread to refreshing session...");
                    guardService.login();
                    // refresh session every 6 hours with 10 a second delay
                }
                //  refresh session every 6 hours with 10 a second delay
            } , 10000,  60000 * 60 * 6);

            new Timer().scheduleAtFixedRate( new TimerTask() {
                @Override
                public void run() {
                    guardService.runGuard();
                }
                //  run the guard service every 30 seconds
            } , 0, 30000);
    }
}
