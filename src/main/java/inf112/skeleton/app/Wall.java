package inf112.skeleton.app;

public class Wall extends TilePiece {
    
    private boolean wallN;
    private boolean wallE;
    private boolean wallS;
    private boolean wallW;

    public Wall(boolean wallN, boolean wallE, boolean wallS, boolean wallW) {
        this.wallN = wallN;
        this.wallE = wallE;
        this.wallS = wallS;
        this.wallW = wallW;
    }
    
    public Wall() {
        
    }
    
    boolean wallN() {
        return wallN;
    }
    boolean wallE() {
        return wallE;
    }
    boolean wallS() {
        return wallS;
    }
    boolean wallW() {
        return wallW;
    }

    @Override
    String getName() {
        return "Wall";
    }
}