package com.chronos_couriers_util;

import java.util.Comparator;
import com.chronos_couriers_model.Package;

public class CheckPackagePriority implements Comparator<Package> {

    @Override
    public int compare(Package package1, Package package2) {
        if (package1.getType() != package2.getType()){
            return package1.getType() == Package.Type.EXPRESS ? -1:1;
        }
        if (package1.getDueDate() != package2.getDueDate()){
            return Long.compare(package1.getDueDate(), package2.getDueDate());
        }
        return Long.compare(package1.getOrderedTime(), package2.getOrderedTime());

    }
}
