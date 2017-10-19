package com.samples;

import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Example2 {
    public static void main(String[] args) {
        List<String> names = new ArrayList<>();
        Flux<String> flux = Flux.fromIterable(names);
        //ex1(flux);

        names.add("one");
        names.add("two");
        //sleep(1000);
        names.add("three");
        names.add("four");
        names.add("five");

        ex2(Mono.just("one"));
        ex2(Mono.just("two"));
        ex2(Mono.just("three"));
        ex2(Mono.just("four"));
        ex2(Mono.just("five"));

        System.out.println("Main waiting...");
        sleep(1000);
        System.out.println(Thread.currentThread().getName()+" - Done");
    }

    public static void ex1(Flux<String> flux){
        flux.log()
                .subscribeOn(Schedulers.elastic())
                .flatMap(v -> Flux.just(v))
                .map(Example1::operation1)
                .subscribe();
    }


    public static void ex2(Mono<String> mono) {
        mono
                .subscribeOn(Schedulers.elastic())
                .map(v -> handle(Example2::operation1,v))
                .map(v -> handle(Example2::operation2,v))
                .subscribe();

    }

    public static <T,R> String handle(Function<String,String> func, String str) {
        try{
            return func.apply(str);

        }catch(Exception ex) {
            System.out.println("Sending "+str+" to exceptions");
            throw new RuntimeException("Exception while processing "+str);
        }

    }

    // operation 1
    public static String operation1(String str)  {
        sleep(100);
        if(str.equalsIgnoreCase("three")){
            throw Exceptions.propagate(new Exception("Error while processing "+str));
        }
        String s = str.substring(0,1).toUpperCase()+str.substring(1);
        System.out.println(Thread.currentThread().getName()+"-"+s);
        return s;
    }

    // operation 2
    public static String operation2(String str) {
        sleep(100);
        String s = str.toUpperCase();
        System.out.println(Thread.currentThread().getName()+"-"+s);
        return s;
    }


    public static void sleep(int msecs) {
        try {
            int n =0;
            while(n++ < 1){
                Thread.sleep(msecs);
                //System.out.println(Thread.currentThread().getName());
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
