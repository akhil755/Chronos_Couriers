# Chronos_Couriers

Run the application main class(ChronosCouriers) and it will start with CLI enabled.

Using CLI you can provide commands to get the desired results. Below are the examples

For placing the order, please follow the command : placeOrder <packageId> <EXPRESS|STANDARD> <dueDate>
e.g : placeorder package1 EXPRESS 1234567

for registeringrider, follow : registerrider <riderId> <rating> <fragileHandling:true|false>
e.g : registerrider Rider1 4.5 true

for updating rider status, follow : updateRiderStatus <riderId> <AVAILABLE|BUSY|OFFLINE>
e.g : updateriderstatus Rider1 BUSY

for completing the delivery, follow : completedelivery <packageId>
e.g : completedelivery package1

for status of the package, follow : status <packageId>
e.g : status Package1

for status of the rider, follow : riderstatus <riderId>
e.g : riderstatus Rider1
