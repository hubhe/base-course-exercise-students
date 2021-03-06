package iaf.ofek.hadracha.base_course.web_server.EjectedPilotRescue;

import iaf.ofek.hadracha.base_course.web_server.AirSituation.Airplane;
import iaf.ofek.hadracha.base_course.web_server.Data.Coordinates;
import iaf.ofek.hadracha.base_course.web_server.Data.Entity;

import java.util.ArrayList;
import java.util.List;

public class EjectedPilotInfo implements Entity<EjectedPilotInfo> {
    private int id;
    private List<AllocatedAirplane> allocatedAirplanes = new ArrayList<>();

    public Coordinates coordinates;

    public String pilotName;

    /**
     * The rescue manager's client id, or null if non.
     */
    public String rescuedBy;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public EjectedPilotInfo clone() {
        try {
            return (EjectedPilotInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Not possible - Entity implements Cloneable");
        }
    }

    public void allocateAirplane(Airplane airplane, String controllerClientId){
        AllocatedAirplane allocatedAirplane = new AllocatedAirplane(airplane);
        allocatedAirplanes.add(allocatedAirplane);
        airplane.flyTo(coordinates, controllerClientId);
        airplane.onArrivedAtDestination(this::airplaneArrived);
    }

    private void airplaneArrived(Airplane airplane){

        allocatedAirplanes.stream()
                .filter(allocatedAirplane -> allocatedAirplane.airplane.id == airplane.id)
                .forEach(allocatedAirplane -> allocatedAirplane.arrivedAtDestination=true);

        if (allocatedAirplanes.stream().allMatch(allocatedAirplane -> allocatedAirplane.arrivedAtDestination)){
            allocatedAirplanes.forEach(allocatedAirplane -> allocatedAirplane.airplane.unAllocate());
        }
    }

    private class AllocatedAirplane{
        Airplane airplane;
        boolean arrivedAtDestination;

        AllocatedAirplane(Airplane airplane) {
            this.airplane = airplane;
        }
    }
}
