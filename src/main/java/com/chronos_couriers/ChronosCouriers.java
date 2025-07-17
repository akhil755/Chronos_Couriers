package com.chronos_couriers;

import com.chronos_couriers_model.LogEntry;
import com.chronos_couriers_service.DispatchCentre;
import com.chronos_couriers_model.Package;
import com.chronos_couriers_model.Rider;
import com.chronos_couriers_util.AuditLogger;


import java.util.List;
import java.util.Scanner;

public class ChronosCouriers {

    public static void main(String... args){
        Scanner scanner = new Scanner(System.in);
        DispatchCentre dispatchCentre = new DispatchCentre();
        AuditLogger audit = AuditLogger.getInstance();

        System.out.println("Chronos Couriers application is started with CLI enabled");

        while (true){
            String input = scanner.nextLine().trim();
            if(input.equalsIgnoreCase("exit")) break;

            String[] parts = input.split(" ");
            if (parts.length==0) continue;

            try{
                switch(parts[0].toLowerCase()){
                    case "placeorder":{
                        if (parts.length!=5){
                            System.out.println("follow: placeOrder <packageId> <EXPRESS|STANDARD> <dueDate> <fragile: true|false>");
                            break;
                        }
                        String pkgId = parts[1];
                        Package.Type type = Package.Type.valueOf(parts[2].toUpperCase());
                        long dueDate = Long.parseLong(parts[3]);
                        boolean fragile = Boolean.parseBoolean(parts[4]);
                        long orderTime = System.currentTimeMillis();


                        Package pkg = new Package(pkgId, type, dueDate, orderTime, fragile);
                        dispatchCentre.placeOrder(pkg);
                        System.out.println("order placed: "+pkgId);
                        break;
                    }
                    case"registerrider":{
                        if (parts.length!=4){
                            System.out.println("follow: registerRider <riderId><rating><fragileHandling:true|false>");
                            break;
                        }
                        String riderId = parts[1];
                        double rating = Double.parseDouble(parts[2]);
                        boolean canHandleFragile = Boolean.parseBoolean(parts[3]);

                        Rider rider = new Rider(riderId, rating, canHandleFragile);
                        dispatchCentre.registerRider(rider);
                        System.out.println("Rider registered "+riderId);
                        break;
                    }
                    case "updateriderstatus":{
                        if (parts.length!=3){
                            System.out.println("follow : updateRiderStatus <riderId><AVAILABLE|BUSY|OFFLINE>");
                            break;
                        }
                        String riderId = parts[1];
                        Rider.Status status = Rider.Status.valueOf(parts[2].toUpperCase());
                        dispatchCentre.updateRiderStatus(riderId, status);

                        break;
                    }
                    case "completedelivery":{
                        if (parts.length!=2){
                            System.out.println("follow : completedelivery <packageId>");
                            break;
                        }
                        String packageId = parts[1];
                        dispatchCentre.completeDelivery(packageId);
                        System.out.println("delivery completed :"+packageId);
                        break;
                    }
                    case "missedexpress":{
                        List<String> missed = dispatchCentre.getMissedExpressDeliveries();
                        if (missed.isEmpty()){
                            System.out.println("No missed express packages");
                        }else {
                            System.out.println("Missed Express Deliveries " +missed);
                        }
                        break;
                    }
                    case "status":{
                        if (parts.length!=2){
                            System.out.println("follow : status <packageId>");
                            break;
                        }
                        try {
                            String packageId = parts[1];
                            System.out.println(dispatchCentre.getStatus(packageId));
                        }catch (IllegalArgumentException exception){
                            System.out.println("Error : "+exception.getMessage());
                        }
                        break;
                    }
                    case "riderstatus":{
                        if (parts.length!=2){
                            System.out.println("follow : riderStatus <riderId>");
                            break;
                        }
                        try {
                            String riderId = parts[1];
                            System.out.println(dispatchCentre.getRiderStatus(riderId));
                        }catch (IllegalArgumentException exception){
                            System.out.println("Error: "+exception.getMessage());
                        }
                        break;
                    }
                    case "packagehistory":{
                        if (parts.length!=2){
                            System.out.println("follow : packagehistory <packageId>");
                            break;
                        }
                        try{
                            List<LogEntry> history = audit.getPackageHistory(parts[1]);
                            if (history.isEmpty()) {
                                System.out.println("No history found for package: " + parts[1]);
                            }else {
                            history.forEach(System.out::println);
                            }
                        }catch (Exception exception){
                            System.out.println("Error: "+exception.getMessage());
                        }
                        break;
                    }
                    case "riderhistory":{
                        if (parts.length!=2){
                            System.out.println("follow : riderhistory <riderId>");
                            break;
                        }
                        audit.getRiderHistory(parts[1])
                                .forEach(System.out::println);
                        break;
                    }
                    case "riderstatushistory": {
                        if(parts.length!=2){
                            System.out.println("follow : riderstatushistory <riderId>");
                            break;
                        }
                        String riderId = parts[1];
                        audit.getRiderStatusHistory(riderId)
                                .forEach(System.out::println);
                        break;
                    }
                    case "riderdeliveries":{
                        if (parts.length!= 2){
                            System.out.println("follow : riderdeliveries <riderId>");
                            break;
                        }
                        String riderId = parts[1];
                        List<String> recent = dispatchCentre.getDeliveriesByRiderInLast24Hours(riderId);
                        if (recent.isEmpty()){
                            System.out.println("No deliveries found for "+riderId+ " in last 24 hours");
                        }else {
                            System.out.println("Deliveries by "+riderId+" in last 24 hours "+recent);
                        }
                        break;
                    }
                    default:
                        System.out.println("unknown command");

                }
            }catch (Exception exception){
                System.out.println("error : "+exception.getMessage());
            }

        }
        scanner.close();
        System.out.println("CLI is terminated");
    }
}
