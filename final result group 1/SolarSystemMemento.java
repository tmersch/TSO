public class SolarSystemMemento {
    private final CelestialBody[] planets;
    private final SpaceProbe spaceProbe;
    private final boolean spaceProbeIncluded;

    /** Default full parametric constructor with a solar system and a spaceProbe
      *
      * @param planets the planets (and moons) constituting the solar system
      * @param spaceProbe the spaceProbe of the simulation
      */
    public SolarSystemMemento (CelestialBody[] planets, SpaceProbe spaceProbe) {
        //Clone the planets into this.planets
        //Initialize this.planets to a CelestialBody array of the same length as the planets parameter
        this.planets = new CelestialBody[planets.length];
        //Then loop over planets, and clone the Celestial bodies into this.planets
        for (int i = 0; i < planets.length; i ++) {
            this.planets[i] = planets[i].clone();
        }

        //Set spaceProbeIncluded to the correct value
        if (spaceProbe == null) {
            spaceProbeIncluded = false;
            this.spaceProbe = null;
        }
        else {
            spaceProbeIncluded = true;
            //If the spaceProbe is part of the simulation, also clone it into this.spaceProbe
            this.spaceProbe = spaceProbe.clone();
        }
    }

    /** Additional constructor with only planets (and moons), so without space probe
      *
      * @param planets the planets representing the solar system
      */
    public SolarSystemMemento (CelestialBody[] planets) {
        this(planets, null);
    }

    /** Returns a copy of the saved solar system, the "planets" variable
      */
    public CelestialBody[] getPlanetsState () {
        //Initialize a new CelestialBody array
        CelestialBody[] result = new CelestialBody[planets.length];

        //Copy all the items in the result array
        for (int i = 0; i < planets.length; i ++) {
            result[i] = planets[i].clone();
        }

        return result;
    }

    /** Returns a copy of the saved space probe, the "spaceProbe" variable
        Warning: could return null if called and there is no spaceProbe saved in this memento
      */
    public SpaceProbe getSpaceProbeState () {
        //Clone spaceProbe
        SpaceProbe result = spaceProbe.clone();

        return result;
    }

    /** Make a deep clone of this object and return it
      */
    public SolarSystemMemento clone () {
        return new SolarSystemMemento(planets, spaceProbe);
    }
}
