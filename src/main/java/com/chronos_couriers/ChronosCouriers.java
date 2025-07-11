package com.chronos_couriers;

import com.chronos_couriers_service.DispatchCentre;
import com.chronos_couriers_model.Package;
import com.chronos_couriers_model.Rider;


import java.util.Scanner;

public class ChronosCouriers {

    public static void main(String... args){
        Scanner scanner = new Scanner(System.in);
        DispatchCentre dispatchCentre = new DispatchCentre();

        System.out.println("Chronos Couriers application is started with CLI enabled");

        while (true){
            String input = scanner.nextLine().trim();
            if(input.equalsIgnoreCase("exit")) break;

            String[] parts = input.split(" ");
            if (parts.length==0) continue;

            try{
                switch(parts[0].toLowerCase()){
                    case "placeorder":{
                        if (parts.length<4){
                            System.out.println("follow: placeOrder <packageId> <EXPRESS|STANDARD> <dueDate>");
                            break;
                        }
                        String pkgId = parts[1];
                        Package.Type type = Package.Type.valueOf(parts[2].toUpperCase());
                        long dueDate = Long.parseLong(parts[3]);
                        long orderTime = System.currentTimeMillis();

                        Package pkg = new Package(pkgId, type, dueDate, orderTime);
                        dispatchCentre.placeOrder(pkg);
                        System.out.println("order placed: "+pkgId);
                        break;
                    }
                    case"registerrider":{
                        if (parts.length<4){
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
                        if (parts.length<3){
                            System.out.println("follow : updateRiderStatus <riderId><AVAILABLE|BUSY|OFFLINE>");
                            break;
                        }
                        String riderId = parts[1];
                        Rider.Status status = Rider.Status.valueOf(parts[2].toUpperCase());
                        dispatchCentre.updateRiderStatus(riderId, status);

                        System.out.println("Rider status updated : "+status);
                        break;
                    }
                    case "deliverycompletion":{
                        if (parts.length<2){
                            System.out.println("follow : deliverycompletion <packageId>");
                            break;
                        }
                        String packageId = parts[1];
                        dispatchCentre.deliveryCompletion(packageId);
                        System.out.println("delivery completed :"+packageId);
                        break;
                    }
                    case "status":{
                        if (parts.length<2){
                            System.out.println("follow : status <packageId>");
                            break;
                        }
                        String packageId = parts[1];
                        System.out.println(dispatchCentre.getStatus(packageId));
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
